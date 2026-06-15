import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  isLoggedIn = signal<boolean>(false);
  currentUser = signal<any>(null);
  isSignOutModalOpen = signal<boolean>(false);

  login(user: any) {
    this.isLoggedIn.set(true);
    this.currentUser.set(user);
  }

  logout() {
    this.isLoggedIn.set(false);
    this.currentUser.set(null);
    this.isSignOutModalOpen.set(false);
  }

  openSignOutModal() {
    this.isSignOutModalOpen.set(true);
  }

  closeSignOutModal() {
    this.isSignOutModalOpen.set(false);
  }
}
