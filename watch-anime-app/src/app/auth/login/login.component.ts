import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
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
  private http = inject(HttpClient);

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

    const { email, password } = this.loginForm.value;

    this.http.post<any>('/authenticate', { email, password }).subscribe({
      next: (response) => {
        this.isLoading.set(false);
        // The API returns AuthenticateResponseVO which contains userVO
        const user = response?.userVO || response?.data?.userVO || response;
        if (user && user.username) {
          this.authService.login({ id: user.id, username: user.username, email: user.email });
          this.router.navigate(['/']);
        } else {
          // Fallback if structure is flat
          this.authService.login({ id: response?.id, username: response?.username, email: response?.email });
          this.router.navigate(['/']);
        }
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.message || 'Invalid email or password. Please try again.');
      }
    });
  }
}
