import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { LoginPromptModalComponent } from '../auth/login-prompt-modal/login-prompt-modal.component';

@Component({
  selector: 'app-search-results',
  standalone: true,
  imports: [CommonModule, RouterModule, LoginPromptModalComponent],
  templateUrl: './search-results.component.html'
})
export class SearchResultsComponent implements OnInit {
  animeList = signal<any[]>([]);
  loading = signal<boolean>(true);
  query = signal<string>('');
  pageableVO = signal<any>(null);
  pageNum = signal<number>(1);

  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private authService = inject(AuthService);

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const q = params['q'] || '';
      const p = parseInt(params['page'] || '1', 10);
      this.query.set(q);
      this.pageNum.set(p);

      if (q) {
        this.performSearch(q, p);
      } else {
        this.animeList.set([]);
        this.pageableVO.set(null);
        this.loading.set(false);
      }
    });
  }

  performSearch(searchQuery: string, page: number) {
    this.loading.set(true);
    const payload: any = {
      title: searchQuery,
      pageNum: page,
      pageSize: 20
    };

    if (this.authService.isLoggedIn()) {
      const user = this.authService.currentUser();
      if (user) {
        payload.userId = user.userVO ? user.userVO.id : user.id;
      }
    }

    this.http.post<any>('/loadAnime', payload).subscribe({
      next: (response) => {
        if (response) {
          this.animeList.set(response.animeList || []);
          this.pageableVO.set(response.pageableVO || null);
        } else {
          this.animeList.set([]);
          this.pageableVO.set(null);
        }
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Search error:', error);
        this.animeList.set([]);
        this.pageableVO.set(null);
        this.loading.set(false);
      }
    });
  }

  changePage(newPage: number) {
    if (newPage < 1) return;
    const currentVo = this.pageableVO();
    if (currentVo && newPage > currentVo.totalPages) return;

    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page: newPage },
      queryParamsHandling: 'merge'
    });
    // Scroll to top
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  get pageNumbers(): number[] {
    const total = this.pageableVO()?.totalPages || 1;
    const current = this.pageNum();
    const delta = 2; // Show 2 pages before and after
    const range: number[] = [];

    for (let i = Math.max(2, current - delta); i <= Math.min(total - 1, current + delta); i++) {
      range.push(i);
    }

    if (current - delta > 2) {
      range.unshift(-1); // -1 signifies ellipsis
    }
    if (current + delta < total - 1) {
      range.push(-1);
    }

    const pages: number[] = [1];
    if (total > 1) {
      pages.push(...range);
      pages.push(total);
    }
    return pages;
  }

  encodeId(id: number): string {
    return btoa(id.toString());
  }

  toggleStatus(event: Event, anime: any, type: 'favorite' | 'bookmark') {
    event.stopPropagation();
    event.preventDefault();

    if (!this.authService.isLoggedIn()) {
      this.authService.openLoginPromptModal();
      return;
    }

    const user = this.authService.currentUser();
    const userId = user.userVO ? user.userVO.id : user.id;

    const payload: any = {
      userId: userId,
      animeId: anime.id
    };

    if (type === 'favorite') {
      anime.is_favorite = !anime.is_favorite;
      payload.isFavorite = anime.is_favorite;
    } else if (type === 'bookmark') {
      anime.watch_status = anime.watch_status === 'WATCH_LATER' ? 'NONE' : 'WATCH_LATER';
      payload.watchStatus = anime.watch_status;
    }

    this.http.post<any>('/updateUserAnimeStatus', payload).subscribe({
      next: (res) => {
        if (res.status === 'error') {
          // Revert on error
          if (type === 'favorite') anime.is_favorite = !anime.is_favorite;
          if (type === 'bookmark') anime.watch_status = anime.watch_status === 'NONE' ? 'WATCH_LATER' : 'NONE';
        }
      },
      error: () => {
        // Revert on error
        if (type === 'favorite') anime.is_favorite = !anime.is_favorite;
        if (type === 'bookmark') anime.watch_status = anime.watch_status === 'NONE' ? 'WATCH_LATER' : 'NONE';
      }
    });
  }
}
