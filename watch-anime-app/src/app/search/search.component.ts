import { Component, signal, inject, ElementRef, HostListener, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './search.component.html'
})
export class SearchComponent {
  @Input() variant: 'normal' | 'large' = 'normal';
  query = signal<string>('');
  results = signal<any[]>([]);
  loading = signal<boolean>(false);
  showDropdown = signal<boolean>(false);

  private http = inject(HttpClient);
  private router = inject(Router);
  private elementRef = inject(ElementRef);
  private searchTimeout: any;

  @HostListener('document:click', ['$event'])
  onClickOutside(event: Event) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.showDropdown.set(false);
    }
  }

  onSearchChange(newValue: string) {
    this.query.set(newValue);

    if (this.searchTimeout) {
      clearTimeout(this.searchTimeout);
    }

    if (!newValue.trim()) {
      this.results.set([]);
      this.showDropdown.set(false);
      return;
    }

    this.loading.set(true);
    this.showDropdown.set(true);

    this.searchTimeout = setTimeout(() => {
      this.performSearch(newValue);
    }, 400); // 400ms debounce
  }

  performSearch(searchQuery: string) {
    const payload = {
      title: searchQuery,
      pageNum: 1,
      pageSize: 5
    };

    this.http.post<any>('/loadAnime', payload).subscribe({
      next: (response) => {
        if (response && response.animeList) {
          this.results.set(response.animeList);
        } else {
          this.results.set([]);
        }
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Search error:', error);
        this.results.set([]);
        this.loading.set(false);
      }
    });
  }

  goToAnime(id: number) {
    this.showDropdown.set(false);
    this.query.set('');
    this.router.navigate(['/anime', btoa(id.toString())]);
  }

  viewAllResults() {
    this.showDropdown.set(false);
    if (this.query().trim()) {
      this.router.navigate(['/search'], { queryParams: { q: this.query() } });
    }
  }
}
