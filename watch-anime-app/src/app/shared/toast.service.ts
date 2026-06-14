import { Injectable, signal } from '@angular/core';

export interface ToastMessage {
  message: string;
  type: 'error' | 'success' | 'info';
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  toastSignal = signal<ToastMessage | null>(null);
  private timeoutId: any;

  showError(message: string, duration = 5000) {
    this.show({ message, type: 'error' }, duration);
  }

  showSuccess(message: string, duration = 5000) {
    this.show({ message, type: 'success' }, duration);
  }

  showInfo(message: string, duration = 5000) {
    this.show({ message, type: 'info' }, duration);
  }

  private show(toast: ToastMessage, duration: number) {
    this.toastSignal.set(toast);
    
    if (this.timeoutId) {
      clearTimeout(this.timeoutId);
    }
    
    if (duration > 0) {
      this.timeoutId = setTimeout(() => {
        this.clear();
      }, duration);
    }
  }

  clear() {
    this.toastSignal.set(null);
    if (this.timeoutId) {
      clearTimeout(this.timeoutId);
      this.timeoutId = null;
    }
  }
}
