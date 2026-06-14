import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (toastService.toastSignal()) {
      <div 
        class="fixed bottom-6 right-6 z-[100] min-w-[320px] max-w-md animate-[slideUp_0.3s_ease-out_forwards]"
        role="alert">
        
        <div class="rounded-xl overflow-hidden shadow-[0_8px_30px_rgb(0,0,0,0.5)] border"
             [ngClass]="{
               'bg-gray-900 border-red-500/50': toastService.toastSignal()?.type === 'error',
               'bg-gray-900 border-green-500/50': toastService.toastSignal()?.type === 'success',
               'bg-gray-900 border-blue-500/50': toastService.toastSignal()?.type === 'info'
             }">
             
          <div class="flex items-start p-4">
            
            <!-- Icon -->
            <div class="flex-shrink-0 mr-3 mt-0.5">
              @if (toastService.toastSignal()?.type === 'error') {
                <div class="h-8 w-8 rounded-full bg-red-500/20 flex items-center justify-center">
                  <svg class="h-5 w-5 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                </div>
              }
              @if (toastService.toastSignal()?.type === 'success') {
                <div class="h-8 w-8 rounded-full bg-green-500/20 flex items-center justify-center">
                  <svg class="h-5 w-5 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                  </svg>
                </div>
              }
              @if (toastService.toastSignal()?.type === 'info') {
                <div class="h-8 w-8 rounded-full bg-blue-500/20 flex items-center justify-center">
                  <svg class="h-5 w-5 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                </div>
              }
            </div>
            
            <!-- Content -->
            <div class="flex-1 w-0">
              <p class="text-sm font-semibold text-white mb-1">
                @if (toastService.toastSignal()?.type === 'error') { Error }
                @if (toastService.toastSignal()?.type === 'success') { Success }
                @if (toastService.toastSignal()?.type === 'info') { Information }
              </p>
              <p class="mt-1 text-sm text-gray-300">
                {{ toastService.toastSignal()?.message }}
              </p>
            </div>
            
            <!-- Close Button -->
            <div class="ml-4 flex-shrink-0 flex">
              <button 
                (click)="toastService.clear()"
                class="inline-flex text-gray-400 hover:text-white hover:bg-gray-800 focus:outline-none focus:ring-2 focus:ring-purple-500 p-1.5 rounded-lg transition-colors">
                <span class="sr-only">Close</span>
                <svg class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
                </svg>
              </button>
            </div>
            
          </div>
          
          <!-- Progress Bar Effect -->
          <div class="h-1 w-full bg-gray-800">
            <div class="h-full animate-[shrink_5s_linear_forwards]"
                 [ngClass]="{
                   'bg-red-500': toastService.toastSignal()?.type === 'error',
                   'bg-green-500': toastService.toastSignal()?.type === 'success',
                   'bg-blue-500': toastService.toastSignal()?.type === 'info'
                 }">
            </div>
          </div>
          
        </div>
      </div>
    }
  `,
  styles: [`
    @keyframes slideUp {
      from { transform: translateY(100%) scale(0.95); opacity: 0; }
      to { transform: translateY(0) scale(1); opacity: 1; }
    }
    @keyframes shrink {
      from { width: 100%; }
      to { width: 0%; }
    }
  `]
})
export class ToastComponent {
  toastService = inject(ToastService);
}
