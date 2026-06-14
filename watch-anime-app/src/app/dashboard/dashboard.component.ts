import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { RouterModule } from '@angular/router';

import { SearchComponent } from '../search/search.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, SearchComponent],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  animeList = signal<any[]>([]);
  loading = signal<boolean>(true);
  private http = inject(HttpClient);

  ngOnInit() {
    this.http.post<any>('http://localhost:8080/fetchRecentAnimeList', {}).subscribe({
      next: (response) => {
        if (response && response.animeList) {
          this.animeList.set(response.animeList);
        }
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error fetching anime list:', error);
        this.loading.set(false);
      }
    });
  }
}
