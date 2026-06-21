import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  isLoggedIn = signal<boolean>(false);
  currentUser = signal<any>(null);
  isSignOutModalOpen = signal<boolean>(false);

  constructor() {
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      this.currentUser.set(JSON.parse(storedUser));
      this.isLoggedIn.set(true);
    }
  }

  login(user: any) {
    this.isLoggedIn.set(true);
    this.currentUser.set(user);
    localStorage.setItem('currentUser', JSON.stringify(user));
  }

  logout() {
    this.isLoggedIn.set(false);
    this.currentUser.set(null);
    this.isSignOutModalOpen.set(false);
    localStorage.removeItem('currentUser');
  }

  openSignOutModal() {
    this.isSignOutModalOpen.set(true);
  }

  closeSignOutModal() {
    this.isSignOutModalOpen.set(false);
  }
}
