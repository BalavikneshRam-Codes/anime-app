import { Component, OnInit, signal, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

import { SearchComponent } from '../search/search.component';

@Component({
  selector: 'app-anime-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, SearchComponent],
  templateUrl: './anime-detail.component.html'
})
export class AnimeDetailComponent implements OnInit {
  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);
  private sanitizer = inject(DomSanitizer);

  anime = signal<any>(null);
  loading = signal<boolean>(true);
  currentEpisode = signal<any>(null);
  selectedServer = signal<'sub' | 'dub'>('sub');

  searchQuery = signal<string>('');
  selectedChunkIndex = signal<number>(0);
  isDropdownOpen = signal<boolean>(false);

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
    return this.selectedServer() === 'dub' && ep.dub_url ? ep.dub_url : ep.sub_url;
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
    this.http.post<any>('/fetchAnime', { animeId }).subscribe({
      next: (res) => {
        this.anime.set(res);
        if (res.episodes_list && res.episodes_list.length > 0) {
          const firstEp = res.episodes_list[0];
          this.currentEpisode.set(firstEp);
          this.selectedChunkIndex.set(0);
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
    this.currentEpisode.set(ep);
    if (this.selectedServer() === 'dub' && !ep.dub_url) {
      this.selectedServer.set('sub');
    }
  }

  hasNextEpisode = computed(() => {
    const list = this.anime()?.episodes_list || [];
    const current = this.currentEpisode();
    if (!current || list.length === 0) return false;
    const idx = list.findIndex((ep: any) => ep.id === current.id);
    return idx !== -1 && idx < list.length - 1;
  });

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
