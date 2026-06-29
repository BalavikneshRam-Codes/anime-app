import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-sidebar-menu',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <!-- Hamburger Trigger -->
    <button (click)="toggleSidebar()" class="p-2 sm:p-2.5 rounded-xl bg-gray-900/50 hover:bg-gray-800/80 backdrop-blur-md border border-white/5 transition-all text-gray-300 hover:text-white shadow-lg">
      <svg class="w-5 h-5 sm:w-6 sm:h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
      </svg>
    </button>

    <!-- Sidebar Overlay -->
    @if (isOpen()) {
      <div class="fixed inset-0 z-[200] flex">
        <!-- Backdrop -->
        <div 
          class="absolute inset-0 bg-gray-950/60 backdrop-blur-sm transition-opacity animate-[fadeIn_0.2s_ease-out]"
          (click)="closeSidebar()">
        </div>

        <!-- Drawer -->
        <div class="relative w-72 max-w-[80vw] h-full bg-gray-900 border-r border-gray-800 shadow-[20px_0_50px_rgba(0,0,0,0.5)] transform transition-transform animate-[slideInLeft_0.3s_ease-out] flex flex-col">
          
          <!-- Top Accent -->
          <div class="absolute top-0 right-0 w-1 h-full bg-gradient-to-b from-purple-400 to-purple-600"></div>

          <!-- Header -->
          <div class="p-6 border-b border-gray-800 flex justify-between items-center">
            <span class="text-xl font-black tracking-tighter text-transparent bg-clip-text bg-gradient-to-r from-white to-gray-300">Anime<span class="text-purple-500">Verse</span></span>
            <button (click)="closeSidebar()" class="text-gray-400 hover:text-white transition-colors p-1 rounded-lg hover:bg-white/5">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <!-- Navigation Links -->
          <div class="flex-1 overflow-y-auto py-6 px-4 space-y-2">
            
            <a routerLink="/" (click)="closeSidebar()" class="flex items-center gap-4 px-4 py-3 rounded-xl text-gray-300 hover:text-white hover:bg-white/5 transition-all group">
              <svg class="w-5 h-5 text-gray-500 group-hover:text-purple-400 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
              </svg>
              <span class="font-bold">Home</span>
            </a>

            <div class="pt-4 pb-2 px-4">
              <p class="text-xs font-bold text-gray-500 uppercase tracking-wider">My Collection</p>
            </div>

            <button (click)="navigateCollection('favorites')" class="w-full flex items-center gap-4 px-4 py-3 rounded-xl text-gray-300 hover:text-white hover:bg-white/5 transition-all group text-left">
              <svg class="w-5 h-5 text-gray-500 group-hover:text-pink-500 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
              </svg>
              <span class="font-bold">My Favorites</span>
            </button>

            <button (click)="navigateCollection('bookmarks')" class="w-full flex items-center gap-4 px-4 py-3 rounded-xl text-gray-300 hover:text-white hover:bg-white/5 transition-all group text-left">
              <svg class="w-5 h-5 text-gray-500 group-hover:text-purple-400 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z" />
              </svg>
              <span class="font-bold">Watchlist</span>
            </button>

          </div>

          <!-- Footer (User Info) -->
          <div class="p-6 border-t border-gray-800">
            @if (authService.isLoggedIn()) {
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-full bg-gradient-to-br from-purple-400 to-purple-600 flex items-center justify-center text-white font-bold">
                  {{ authService.currentUser()?.username?.substring(0, 2)?.toUpperCase() || 'DU' }}
                </div>
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-bold text-white truncate">{{ authService.currentUser()?.username || 'Demo User' }}</p>
                  <button (click)="closeSidebar(); authService.openSignOutModal()" class="text-xs text-red-400 hover:text-red-300 font-semibold transition-colors mt-0.5">Sign Out</button>
                </div>
              </div>
            } @else {
              <button (click)="closeSidebar(); router.navigate(['/login'])" class="w-full py-2.5 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-bold text-sm transition-colors shadow-lg shadow-purple-500/20">
                Sign In
              </button>
            }
          </div>

        </div>
      </div>
    }
  `,
  styles: [`
    @keyframes fadeIn {
      from { opacity: 0; }
      to { opacity: 1; }
    }
    @keyframes slideInLeft {
      from { transform: translateX(-100%); }
      to { transform: translateX(0); }
    }
  `]
})
export class SidebarMenuComponent {
  isOpen = signal<boolean>(false);
  authService = inject(AuthService);
  router = inject(Router);

  toggleSidebar() {
    this.isOpen.update(v => !v);
  }

  closeSidebar() {
    this.isOpen.set(false);
  }

  navigateCollection(type: 'favorites' | 'bookmarks') {
    this.closeSidebar();
    if (!this.authService.isLoggedIn()) {
      this.authService.openLoginPromptModal();
      return;
    }
    this.router.navigate(['/collection', type]);
  }
}
