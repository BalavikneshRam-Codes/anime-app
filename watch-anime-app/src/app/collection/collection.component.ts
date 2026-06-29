import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { LoginPromptModalComponent } from '../auth/login-prompt-modal/login-prompt-modal.component';
import { SidebarMenuComponent } from '../shared/sidebar-menu/sidebar-menu.component';

@Component({
  selector: 'app-collection',
  standalone: true,
  imports: [CommonModule, RouterModule, LoginPromptModalComponent, SidebarMenuComponent],
  template: `
<div class="min-h-screen bg-gray-950 text-white p-8 font-sans selection:bg-purple-500 selection:text-white relative">
  <div class="max-w-7xl mx-auto mb-12 flex flex-col gap-8">
    
    <!-- Top Nav / Back Button -->
    <div class="flex items-center gap-4 relative z-[100]">
      <app-sidebar-menu></app-sidebar-menu>
      <a routerLink="/" class="inline-flex items-center gap-2 text-gray-400 hover:text-white transition-colors w-fit group">
        <svg class="w-5 h-5 transform group-hover:-translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"></path>
        </svg>
        <span class="font-medium">Back to Home</span>
      </a>
    </div>

    <!-- Header -->
    <header class="mb-4 text-left relative z-10">
      <h1 class="text-4xl font-black tracking-tight text-white drop-shadow-2xl mb-2 capitalize">
        My <span class="text-transparent bg-clip-text bg-gradient-to-br from-purple-400 to-purple-700">{{ collectionType() }}</span>
      </h1>
      <p class="text-gray-400 text-lg font-medium">
        @if (!loading()) {
          You have {{ pageableVO() ? pageableVO().totalElements : animeList().length }} anime in this collection.
        } @else {
          Loading your collection...
        }
      </p>
    </header>

  </div>

  <!-- Loading State -->
  @if (loading()) {
    <div class="flex justify-center items-center py-20 relative z-10">
      <div class="relative w-16 h-16">
        <div class="absolute inset-0 rounded-full border-4 border-gray-800"></div>
        <div class="absolute inset-0 rounded-full border-4 border-purple-500 border-t-transparent animate-spin"></div>
      </div>
    </div>
  }

  <!-- Results Grid -->
  @if (!loading()) {
    @if (animeList().length === 0) {
      <div class="text-center py-20 relative z-10">
        <svg class="w-24 h-24 mx-auto text-gray-800 mb-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path>
        </svg>
        <h3 class="text-2xl font-bold text-gray-400 mb-2">No anime found</h3>
        <p class="text-gray-500">Your collection is empty. Start adding some anime!</p>
      </div>
    } @else {
      <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6 max-w-7xl mx-auto relative z-10">
        @for (anime of animeList(); track anime.id) {
          <div (click)="goToAnime(anime.animeId)" class="group relative rounded-2xl overflow-hidden bg-gray-900 border border-gray-800 shadow-xl hover:shadow-2xl hover:shadow-purple-500/20 transition-all duration-300 ease-in-out hover:-translate-y-2 cursor-pointer flex flex-col h-full">
            
            <div class="relative w-full aspect-[3/4] overflow-hidden">
              <div class="absolute inset-0 bg-gray-800 animate-pulse" [class.hidden]="anime.imageLoaded"></div>
              <img [src]="anime.poster" [alt]="anime.title" (load)="anime.imageLoaded = true" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110 opacity-0" [class.opacity-100]="anime.imageLoaded" loading="lazy" />
              <div class="absolute inset-0 bg-gradient-to-t from-gray-950 via-gray-950/40 to-transparent opacity-90 group-hover:opacity-100 transition-opacity duration-300"></div>

              <!-- Top Badges -->
              <div class="absolute top-4 left-4 flex gap-2">
                @if (anime.score) {
                  <span class="px-2.5 py-1 text-xs font-bold bg-purple-600/90 text-white rounded-md backdrop-blur-md shadow-sm flex items-center gap-1">
                    ⭐ {{ anime.score }}
                  </span>
                }
                @if (anime.rating) {
                  <span class="px-2.5 py-1 text-xs font-bold bg-gray-800/90 text-gray-200 rounded-md backdrop-blur-md shadow-sm">
                    {{ anime.rating }}
                  </span>
                }
              </div>

              <!-- Action Badges (Favorite & Bookmark) -->
              <div class="absolute top-4 right-4 flex flex-col gap-2 z-20">
                <button (click)="toggleStatus($event, anime, 'favorite')" class="p-2 bg-gray-900/60 hover:bg-gray-800/90 rounded-full backdrop-blur-md transition-all shadow-sm border border-white/10 group/btn">
                  <svg [class]="anime.is_favorite ? 'text-pink-500 fill-current' : 'text-gray-300'" class="w-5 h-5 group-hover/btn:scale-110 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"></path>
                  </svg>
                </button>
                <button (click)="toggleStatus($event, anime, 'bookmark')" class="p-2 bg-gray-900/60 hover:bg-gray-800/90 rounded-full backdrop-blur-md transition-all shadow-sm border border-white/10 group/btn">
                  <svg [class]="anime.watch_status === 'WATCH_LATER' ? 'text-purple-500 fill-current' : 'text-gray-300'" class="w-5 h-5 group-hover/btn:scale-110 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z"></path>
                  </svg>
                </button>
              </div>
            </div>

            <!-- Content Area -->
            <div class="p-6 flex-1 flex flex-col relative z-10 -mt-24">
              <h2 class="text-xl font-bold text-white mb-2 line-clamp-2 group-hover:text-purple-400 transition-colors duration-200 drop-shadow-md">
                {{ anime.title }}
              </h2>
              
              <div class="flex items-center text-xs text-pink-400 font-medium mb-3">
                <span>{{ anime.season }} {{ anime.year }}</span>
                <span class="mx-2 text-gray-600">•</span>
                <span class="text-gray-400">{{ anime.status || anime.type }}</span>
              </div>

              <p class="text-gray-400 text-sm line-clamp-3 mb-4 flex-1 transition-colors duration-300 group-hover:text-gray-300">
                {{ anime.description }}
              </p>

              <!-- Genres -->
              <div class="flex flex-wrap gap-2 mt-auto">
                @for (genre of (anime.genres_list || anime.genres); track (genre.id || genre)) {
                  <span class="px-2 py-1 text-[10px] uppercase tracking-wider font-semibold bg-gray-800 border border-gray-700 text-gray-300 rounded hover:bg-purple-500/30 hover:border-purple-500/50 hover:text-purple-300 transition-colors">
                    {{ genre.name || genre }}
                  </span>
                }
              </div>
            </div>
          </div>
        }
      </div>

      <!-- Pagination -->
      @if (pageableVO() && pageableVO().totalPages > 1) {
        <div class="flex justify-center items-center gap-4 mt-12 max-w-7xl mx-auto relative z-10">
          <button (click)="changePage(pageNum() - 1)" [disabled]="pageNum() === 1" class="px-6 py-2.5 rounded-xl font-bold text-sm transition-all disabled:opacity-50 disabled:cursor-not-allowed bg-gray-900 border border-gray-800 text-white hover:bg-gray-800 hover:border-gray-700 disabled:hover:bg-gray-900 disabled:hover:border-gray-800">
            Previous
          </button>
          
          <div class="flex items-center gap-2">
            <span class="px-4 py-2 rounded-xl bg-purple-600/20 text-purple-400 font-bold border border-purple-500/30">
              {{ pageNum() }}
            </span>
            <span class="text-gray-500 font-medium">of</span>
            <span class="text-gray-400 font-bold">{{ pageableVO().totalPages }}</span>
          </div>

          <button (click)="changePage(pageNum() + 1)" [disabled]="pageNum() === pageableVO().totalPages" class="px-6 py-2.5 rounded-xl font-bold text-sm transition-all disabled:opacity-50 disabled:cursor-not-allowed bg-gray-900 border border-gray-800 text-white hover:bg-gray-800 hover:border-gray-700 disabled:hover:bg-gray-900 disabled:hover:border-gray-800">
            Next
          </button>
        </div>
      }
    }
  }

  <app-login-prompt-modal></app-login-prompt-modal>
</div>
  `
})
export class CollectionComponent implements OnInit {
  animeList = signal<any[]>([]);
  loading = signal<boolean>(true);
  collectionType = signal<string>('favorites');
  pageableVO = signal<any>(null);
  pageNum = signal<number>(1);

  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private authService = inject(AuthService);

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const type = params.get('type') || 'favorites';
      if (type !== 'favorites' && type !== 'bookmarks') {
        this.router.navigate(['/']);
        return;
      }
      this.collectionType.set(type);
      this.pageNum.set(1);
      
      if (!this.authService.isLoggedIn()) {
        this.router.navigate(['/login']);
        return;
      }

      this.fetchCollection();
    });
  }

  fetchCollection() {
    this.loading.set(true);
    
    const user = this.authService.currentUser();
    
    const payload = {
      userId: user?.id,
      collectionType: this.collectionType(),
      pageNum: this.pageNum().toString(),
      pageSize: "20"
    };

    this.http.post<any>('/fetchUserCollection', payload).subscribe({
      next: (response) => {
        if (response && response.animeList) {
          this.animeList.set(response.animeList);
          this.pageableVO.set(response.pageableVO);
        } else {
          this.animeList.set([]);
          this.pageableVO.set(null);
        }
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Fetch collection error:', error);
        this.animeList.set([]);
        this.loading.set(false);
      }
    });
  }

  changePage(newPage: number) {
    if (newPage >= 1 && newPage <= (this.pageableVO()?.totalPages || 1)) {
      this.pageNum.set(newPage);
      this.fetchCollection();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  goToAnime(id: number) {
    this.router.navigate(['/anime', btoa(id.toString())]);
  }

  toggleStatus(event: Event, anime: any, type: 'favorite' | 'bookmark') {
    event.stopPropagation();
    event.preventDefault();

    if (!this.authService.isLoggedIn()) {
      this.authService.openLoginPromptModal();
      return;
    }

    const user = this.authService.currentUser();
    
    let previousState = { ...anime };
    let newState = 'WATCH_LATER';

    if (type === 'favorite') {
      anime.is_favorite = !anime.is_favorite;
    } else if (type === 'bookmark') {
      if (anime.watch_status === 'WATCH_LATER') {
        anime.watch_status = null;
        newState = 'NONE'; // Match WatchStatus enum in backend
      } else {
        anime.watch_status = 'WATCH_LATER';
      }
    }

    const payload = {
      userId: user.id,
      animeId: anime.id,
      isFavorite: anime.is_favorite,
      watchStatus: type === 'bookmark' ? newState : (anime.watch_status || 'NONE')
    };

    this.http.post('/updateUserAnimeStatus', payload).subscribe({
      next: (res: any) => {
        if (res.status === 'error') {
          Object.assign(anime, previousState);
        } else {
          // If we remove from the current collection view, we could remove it from the list here
          // For now, let's keep it in the list but updated, or we can refetch.
          // Refetching ensures consistency if they un-favorite from the favorites page.
          if ((type === 'favorite' && this.collectionType() === 'favorites' && !anime.is_favorite) ||
              (type === 'bookmark' && this.collectionType() === 'bookmarks' && anime.watch_status !== 'WATCH_LATER')) {
            // Re-fetch to update the list and pagination
            this.fetchCollection();
          }
        }
      },
      error: (err) => {
        console.error('Status update failed', err);
        Object.assign(anime, previousState);
      }
    });
  }
}
