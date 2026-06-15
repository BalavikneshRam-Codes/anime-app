import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private authService = inject(AuthService);

  isLoading = signal<boolean>(false);
  errorMessage = signal<string | null>(null);

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    rememberMe: [false]
  });

  onSubmit() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    // Simulate API call
    setTimeout(() => {
      this.isLoading.set(false);
      
      const { email, password } = this.loginForm.value;
      
      // Basic mock authentication check
      if (email === 'demo@anime.com' && password === 'password123') {
        // Success - navigate home
        this.authService.login({ email, name: 'Demo User' });
        this.router.navigate(['/']);
      } else {
        // Mock error
        this.errorMessage.set('Invalid email or password. Try demo@anime.com / password123');
      }
    }, 1500);
  }
}
