import { Component, OnInit, signal, inject, HostListener, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { RouterModule } from '@angular/router';

import { SearchComponent } from '../search/search.component';
import { AuthService } from '../auth/auth.service';
import { SignOutModalComponent } from '../auth/sign-out-modal/sign-out-modal.component';
import { LoginPromptModalComponent } from '../auth/login-prompt-modal/login-prompt-modal.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, SearchComponent, SignOutModalComponent, LoginPromptModalComponent],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  authService = inject(AuthService);
  isProfileDropdownOpen = signal<boolean>(false);
  
  animeList = signal<any[]>([]);
  loading = signal<boolean>(true);
  currentPage = signal<number>(1);
  totalPages = signal<number>(1);
  
  filters = signal<any>({ ratings: [], genres: [], seasons: [], types: [], statuses: [] });
  selectedFilters = signal<any>({ rating: '', genre: '', season: '', type: '', status: '' });
  openDropdown = signal<string | null>(null);

  private http = inject(HttpClient);
  private elementRef = inject(ElementRef);

  @HostListener('document:click', ['$event'])
  onClickOutside(event: Event) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.openDropdown.set(null);
    }
  }

  ngOnInit() {
    this.fetchFilters();
    this.loadPage(1);
  }

  fetchFilters() {
    this.http.post<any>('/fetchAnimeFilter', {}).subscribe({
      next: (response) => {
        if (response) {
          this.filters.set(response);
        }
      },
      error: (err) => console.error('Error fetching filters:', err)
    });
  }

  loadPage(page: number) {
    if (page < 1 || (this.totalPages() > 1 && page > this.totalPages())) return;
    
    this.loading.set(true);

    const currentFilters = this.selectedFilters();
    const hasFilters = Object.values(currentFilters).some(val => val !== '');

    const endpoint = hasFilters ? '/loadAnime' : '/fetchRecentAnimeList';
    const payload: any = { pageNum: page.toString(), pageSize: '20' };

    if (hasFilters) {
      if (currentFilters.rating) payload.rating = currentFilters.rating;
      if (currentFilters.genre) payload.genres = currentFilters.genre;
      if (currentFilters.season) payload.season = currentFilters.season;
      if (currentFilters.type) payload.type = currentFilters.type;
      if (currentFilters.status) payload.status = currentFilters.status;
    }

    if (this.authService.isLoggedIn()) {
      const user = this.authService.currentUser();
      if (user) {
        payload.userId = user.userVO ? user.userVO.id : user.id;
      }
    }

    this.http.post<any>(endpoint, payload).subscribe({
      next: (response) => {
        if (response && response.animeList) {
          this.animeList.set(response.animeList);
        }
        if (response && response.pageableVO) {
          this.currentPage.set(response.pageableVO.pageNumber);
          this.totalPages.set(response.pageableVO.totalPages);
        }
        this.loading.set(false);
        // Scroll to top
        window.scrollTo({ top: 0, behavior: 'smooth' });
      },
      error: (error) => {
        console.error('Error fetching anime list:', error);
        this.loading.set(false);
      }
    });
  }

  toggleFilterDropdown(dropdownName: string) {
    if (this.openDropdown() === dropdownName) {
      this.openDropdown.set(null);
    } else {
      this.openDropdown.set(dropdownName);
    }
  }

  selectFilter(type: string, value: string) {
    this.selectedFilters.update(filters => ({ ...filters, [type]: value }));
    this.openDropdown.set(null);
    this.loadPage(1);
  }

  clearFilters() {
    this.selectedFilters.set({ rating: '', genre: '', season: '', type: '', status: '' });
    this.openDropdown.set(null);
    this.loadPage(1);
  }

  get pageNumbers(): number[] {
    const total = this.totalPages();
    const current = this.currentPage();
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

  toggleProfileDropdown() {
    this.isProfileDropdownOpen.update(v => !v);
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
          if (type === 'favorite') anime.is_favorite = !anime.is_favorite;
          if (type === 'bookmark') anime.watch_status = anime.watch_status === 'NONE' ? 'WATCH_LATER' : 'NONE';
        }
      },
      error: () => {
        if (type === 'favorite') anime.is_favorite = !anime.is_favorite;
        if (type === 'bookmark') anime.watch_status = anime.watch_status === 'NONE' ? 'WATCH_LATER' : 'NONE';
      }
    });
  }
}
