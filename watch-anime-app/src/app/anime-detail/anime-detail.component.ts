import { Component, OnInit, OnDestroy, signal, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

import { SearchComponent } from '../search/search.component';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-anime-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, SearchComponent],
  templateUrl: './anime-detail.component.html'
})
export class AnimeDetailComponent implements OnInit, OnDestroy {
  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);
  private sanitizer = inject(DomSanitizer);
  private authService = inject(AuthService);

  anime = signal<any>(null);
  loading = signal<boolean>(true);
  currentEpisode = signal<any>(null);
  selectedServer = signal<'sub' | 'dub'>('sub');

  searchQuery = signal<string>('');
  selectedChunkIndex = signal<number>(0);
  isDropdownOpen = signal<boolean>(false);
  autoPlayNext = signal<boolean>(false);

  private watchTimer: any;
  private readonly WATCH_THRESHOLD_MS = 5 * 60 * 1000; // 5 minutes

  episodeChunks = computed(() => {
    const list = this.anime()?.episodes_list || [];
    const chunks = [];
    const chunkSize = 100;
    for (let i = 0; i < list.length; i += chunkSize) {
      const chunk = list.slice(i, i + chunkSize);
      const start = chunk[0].order;
      const end = chunk[chunk.length - 1].order;
      chunks.push({ start, end, episodes: chunk, index: Math.floor(i / chunkSize) });
    }
    return chunks;
  });

  displayedEpisodes = computed(() => {
    const list = this.anime()?.episodes_list || [];
    const query = this.searchQuery().trim().toLowerCase();

    if (query) {
      return list.filter((ep: any) => ep.order.toString().includes(query) || (ep.title && ep.title.toLowerCase().includes(query)));
    }

    const chunks = this.episodeChunks();
    if (chunks.length === 0) return [];

    const index = this.selectedChunkIndex();
    const chunk = chunks.find(c => c.index === index) || chunks[0];
    return chunk.episodes;
  });

  activeUrl = computed(() => {
    const ep = this.currentEpisode();
    if (!ep) return null;
    let url = this.selectedServer() === 'dub' && ep.dub_url ? ep.dub_url : ep.sub_url;
    if (url && this.autoPlayNext()) {
      url += (url.includes('?') ? '&' : '?') + 'autoPlay=1';
    }
    return url;
  });

  safeIframeUrl = computed(() => {
    const url = this.activeUrl();
    if (!url) return null;
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  });

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        try {
          const decodedId = Number(atob(id));
          this.fetchAnime(decodedId);
        } catch (e) {
          // Fallback if the id was not encoded
          const fallbackId = Number(id);
          if (!isNaN(fallbackId)) {
            this.fetchAnime(fallbackId);
          }
        }
      }
    });
  }

  fetchAnime(animeId: number) {
    this.loading.set(true);
    
    const payload: any = { animeId };
    if (this.authService.isLoggedIn()) {
      const user = this.authService.currentUser();
      if (user) {
        payload.userId = user.userVO ? user.userVO.id : user.id;
      }
    }

    this.http.post<any>('/fetchAnime', payload).subscribe({
      next: (res) => {
        this.anime.set(res);
        if (res.episodes_list && res.episodes_list.length > 0) {
          let targetEp = res.episodes_list[0];
          let chunkIndex = 0;
          let shouldAutoPlay = false;

          if (res.latestUserEpisodeId) {
            const idx = res.episodes_list.findIndex((ep: any) => String(ep.episode_embed_id) === String(res.latestUserEpisodeId));
            if (idx !== -1) {
              if (idx < res.episodes_list.length - 1) {
                targetEp = res.episodes_list[idx + 1];
                shouldAutoPlay = true;
              } else {
                targetEp = res.episodes_list[idx];
              }
              const targetIdx = res.episodes_list.findIndex((ep: any) => ep.id === targetEp.id);
              chunkIndex = Math.floor(targetIdx / 100);
            }
          }

          this.autoPlayNext.set(shouldAutoPlay);
          this.currentEpisode.set(targetEp);
          this.selectedChunkIndex.set(chunkIndex);
          
          if (this.selectedServer() === 'dub' && !targetEp.dub_url) {
            this.selectedServer.set('sub');
          }
          this.startWatchTimer();
        }
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error fetching anime details:', err);
        this.loading.set(false);
      }
    });
  }

  selectEpisode(ep: any) {
    this.autoPlayNext.set(true);
    this.currentEpisode.set(ep);
    if (this.selectedServer() === 'dub' && !ep.dub_url) {
      this.selectedServer.set('sub');
    }
    this.startWatchTimer();
  }

  startWatchTimer() {
    this.clearWatchTimer();
    
    // Only track if user is logged in
    if (!this.authService.isLoggedIn()) return;
    
    this.watchTimer = setTimeout(() => {
      const user = this.authService.currentUser();
      const ep = this.currentEpisode();
      if (user && ep) {
        this.http.post('/api/progress/complete', {
          userId: user.userVO ? user.userVO.id : user.id, // Assuming structure based on AuthenticateResponseVO
          episodeId: ep.id
        }).subscribe({
          next: () => console.log('Episode marked as completed'),
          error: (err) => console.error('Failed to mark episode as completed', err)
        });
      }
    }, this.WATCH_THRESHOLD_MS);
  }

  clearWatchTimer() {
    if (this.watchTimer) {
      clearTimeout(this.watchTimer);
      this.watchTimer = null;
    }
  }

  ngOnDestroy() {
    this.clearWatchTimer();
  }

  hasNextEpisode = computed(() => {
    const list = this.anime()?.episodes_list || [];
    const current = this.currentEpisode();
    if (!current || list.length === 0) return false;
    const idx = list.findIndex((ep: any) => ep.id === current.id);
    return idx !== -1 && idx < list.length - 1;
  });

  isWatched(ep: any): boolean {
    const watchedIds = this.anime()?.watched_episode_ids || [];
    return watchedIds.includes(ep.episode_embed_id);
  }

  nextEpisode() {
    const list = this.anime()?.episodes_list || [];
    const current = this.currentEpisode();
    if (!current || list.length === 0) return;

    const idx = list.findIndex((ep: any) => ep.id === current.id);
    if (idx !== -1 && idx < list.length - 1) {
      const nextEp = list[idx + 1];
      this.selectEpisode(nextEp);

      const chunkIndex = this.episodeChunks().findIndex(chunk =>
        chunk.episodes.some((e: any) => e.id === nextEp.id)
      );
      if (chunkIndex !== -1 && this.selectedChunkIndex() !== chunkIndex) {
        this.selectedChunkIndex.set(chunkIndex);
      }
    }
  }

  selectChunk(index: number) {
    this.selectedChunkIndex.set(index);
    this.isDropdownOpen.set(false);
  }

  toggleDropdown() {
    this.isDropdownOpen.update(v => !v);
  }

  updateSearch(event: Event) {
    const input = event.target as HTMLInputElement;
    this.searchQuery.set(input.value);
  }

  setServer(type: 'sub' | 'dub') {
    this.selectedServer.set(type);
  }

  getSafeIframeUrl(url: string): SafeResourceUrl {
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }
}
