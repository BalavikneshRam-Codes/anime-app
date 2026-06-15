import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { RouterModule } from '@angular/router';

import { SearchComponent } from '../search/search.component';
import { AuthService } from '../auth/auth.service';
import { SignOutModalComponent } from '../auth/sign-out-modal/sign-out-modal.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, SearchComponent, SignOutModalComponent],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  authService = inject(AuthService);
  isProfileDropdownOpen = signal<boolean>(false);
  
  animeList = signal<any[]>([]);
  loading = signal<boolean>(true);
  private http = inject(HttpClient);

  ngOnInit() {
    this.http.post<any>('/fetchRecentAnimeList', {}).subscribe({
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

  toggleProfileDropdown() {
    this.isProfileDropdownOpen.update(v => !v);
  }
}
