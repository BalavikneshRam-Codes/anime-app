import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { RouterModule, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-search-results',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './search-results.component.html'
})
export class SearchResultsComponent implements OnInit {
  animeList = signal<any[]>([]);
  loading = signal<boolean>(true);
  query = signal<string>('');
  
  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const q = params['q'] || '';
      this.query.set(q);
      
      if (q) {
        this.performSearch(q);
      } else {
        this.animeList.set([]);
        this.loading.set(false);
      }
    });
  }

  performSearch(searchQuery: string) {
    this.loading.set(true);
    const payload = {
      title: searchQuery,
      pageNum: 1,
      pageSize: 50
    };

    this.http.post<any>('http://localhost:8080/loadAnime', payload).subscribe({
      next: (response) => {
        if (response && response.animeList) {
          this.animeList.set(response.animeList);
        } else {
          this.animeList.set([]);
        }
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Search error:', error);
        this.animeList.set([]);
        this.loading.set(false);
      }
    });
  }
}
