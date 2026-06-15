import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-sign-up',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './sign-up.component.html'
})
export class SignUpComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private authService = inject(AuthService);

  step = signal<number>(1);
  isLoading = signal<boolean>(false);
  errorMessage = signal<string | null>(null);

  // Step 1: Full Name & Email
  step1Form = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]]
  });

  // Step 2: OTP
  step2Form = this.fb.group({
    otp: ['', [Validators.required, Validators.pattern('^[0-9]{6}$')]]
  });

  onSendOtp() {
    if (this.step1Form.invalid) {
      this.step1Form.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    // Simulate API call to send OTP
    setTimeout(() => {
      this.isLoading.set(false);
      this.step.set(2);
    }, 1200);
  }

  onVerifyOtp() {
    if (this.step2Form.invalid) {
      this.step2Form.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    // Simulate API call to verify OTP and login
    setTimeout(() => {
      this.isLoading.set(false);
      
      const otp = this.step2Form.get('otp')?.value;
      
      // Mock validation: any 6 digit code works for demo purposes
      if (otp?.length === 6) {
        const { name, email } = this.step1Form.value;
        this.authService.login({ email, name: name || 'New User' });
        this.router.navigate(['/']);
      } else {
        this.errorMessage.set('Invalid OTP code. Please try again.');
      }
    }, 1500);
  }

  goBack() {
    this.step.set(1);
    this.errorMessage.set(null);
  }
}
