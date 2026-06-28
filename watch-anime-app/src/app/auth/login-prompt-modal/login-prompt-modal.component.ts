import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login-prompt-modal',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (authService.isLoginPromptModalOpen()) {
      <div class="fixed inset-0 z-[200] flex items-center justify-center p-4">
        
        <!-- Backdrop -->
        <div 
          class="absolute inset-0 bg-gray-950/80 backdrop-blur-sm transition-opacity animate-[fadeIn_0.2s_ease-out]"
          (click)="authService.closeLoginPromptModal()">
        </div>

        <!-- Modal Card -->
        <div class="relative w-full max-w-sm bg-gray-900 border border-gray-800 rounded-3xl shadow-[0_0_50px_rgba(147,51,234,0.15)] overflow-hidden transform transition-all animate-[scaleUp_0.2s_ease-out]">
          
          <!-- Top Accent -->
          <div class="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-purple-400 to-purple-600"></div>

          <!-- Content -->
          <div class="p-8 text-center">
            
            <div class="mx-auto flex items-center justify-center h-16 w-16 rounded-full bg-purple-500/10 mb-6">
              <svg class="h-8 w-8 text-purple-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
              </svg>
            </div>

            <h3 class="text-2xl font-black text-white mb-2 tracking-tight">Unlock Features</h3>
            <p class="text-sm text-gray-400 mb-8 font-medium">
              You need to be logged in to save anime to your favorites or bookmarks. Sign in to unlock this feature!
            </p>

            <!-- Actions -->
            <div class="flex flex-col sm:flex-row gap-3">
              <button 
                (click)="authService.closeLoginPromptModal()"
                class="w-full sm:w-1/2 px-4 py-3 text-sm font-bold text-gray-300 bg-gray-800 hover:bg-gray-700 hover:text-white rounded-xl transition-colors">
                Cancel
              </button>
              <button 
                (click)="onLogin()"
                class="w-full sm:w-1/2 px-4 py-3 text-sm font-bold text-white bg-gradient-to-r from-purple-500 to-purple-600 hover:from-purple-400 hover:to-purple-500 rounded-xl shadow-lg shadow-purple-500/20 hover:shadow-purple-500/40 transition-all">
                Log In
              </button>
            </div>

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
    @keyframes scaleUp {
      from { transform: scale(0.95); opacity: 0; }
      to { transform: scale(1); opacity: 1; }
    }
  `]
})
export class LoginPromptModalComponent {
  authService = inject(AuthService);
  private router = inject(Router);

  onLogin() {
    this.authService.closeLoginPromptModal();
    this.router.navigate(['/login']);
  }
}
