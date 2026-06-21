import { Component, signal, inject, computed, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../auth.service';
import { ToastService } from '../../shared/toast.service';

@Component({
  selector: 'app-sign-up',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './sign-up.component.html'
})
export class SignUpComponent implements OnDestroy {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private authService = inject(AuthService);
  private http = inject(HttpClient);
  private toastService = inject(ToastService);

  step = signal<number>(1);
  isLoading = signal<boolean>(false);
  errorMessage = signal<string | null>(null);
  backendMessage = signal<string | null>(null);

  // Countdown State
  countdownSeconds = signal<number>(0);
  totalSeconds = 60;
  private countdownInterval: any;

  // Password Visibility Toggle States
  showPassword = signal<boolean>(false);
  showConfirmPassword = signal<boolean>(false);

  formattedTime = computed(() => {
    const seconds = this.countdownSeconds();
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
  });

  // Step 1: Username & Email
  step1Form = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3), Validators.pattern('^[a-zA-Z0-9_]+$')]],
    email: ['', [Validators.required, Validators.email]]
  });

  // Step 2: OTP
  step2Form = this.fb.group({
    otp: ['', [Validators.required, Validators.pattern('^[0-9]{6}$')]]
  });

  // Step 3: Password Setting
  step3Form = this.fb.group({
    password: ['', [Validators.required, Validators.minLength(6)]],
    confirmPassword: ['', [Validators.required]]
  }, {
    validators: (control) => {
      const password = control.get('password');
      const confirmPassword = control.get('confirmPassword');
      return password && confirmPassword && password.value === confirmPassword.value ? null : { mismatch: true };
    }
  });

  ngOnDestroy() {
    this.clearTimer();
  }

  private clearTimer() {
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
      this.countdownInterval = null;
    }
  }

  startCountdown(expireMins: number) {
    this.clearTimer();
    this.totalSeconds = expireMins * 60;
    this.countdownSeconds.set(this.totalSeconds);

    this.countdownInterval = setInterval(() => {
      const current = this.countdownSeconds();
      if (current <= 1) {
        this.countdownSeconds.set(0);
        this.clearTimer();
      } else {
        this.countdownSeconds.set(current - 1);
      }
    }, 1000);
  }

  calculateDashOffset(): number {
    const circumference = 2 * Math.PI * 34; // approx 213.6
    if (this.totalSeconds <= 0) return 0;
    const ratio = this.countdownSeconds() / this.totalSeconds;
    return circumference * (1 - ratio);
  }

  onSendOtp() {
    if (this.step1Form.invalid) {
      this.step1Form.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const body = {
      email: this.step1Form.value.email,
      username: this.step1Form.value.username
    };

    this.http.post<any>('/signUp', body).subscribe({
      next: (response) => {
        this.isLoading.set(false);
        this.step.set(2);
        const expireMins = parseInt(response?.otpExpireMins || '1', 10);
        this.startCountdown(expireMins);
        this.toastService.showSuccess('Verification code sent to your email!');
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.message || 'Failed to send OTP. Please try again.');
      }
    });
  }

  onResendOtp() {
    if (this.isLoading()) return;

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const body = {
      email: this.step1Form.value.email,
      username: this.step1Form.value.username
    };

    this.http.post<any>('/resetOtp', body).subscribe({
      next: (response) => {
        this.isLoading.set(false);
        const expireMins = parseInt(response?.otpExpireMins || '1', 10);
        this.startCountdown(expireMins);
        this.toastService.showSuccess('A new verification code has been sent!');
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.message || 'Failed to resend OTP. Please try again.');
      }
    });
  }

  onVerifyOtp() {
    if (this.step2Form.invalid) {
      this.step2Form.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const body = {
      otp: this.step2Form.get('otp')?.value,
      email: this.step1Form.get('email')?.value
    };

    this.http.post<any>('/validateOtp', body).subscribe({
      next: (response) => {
        this.isLoading.set(false);
        this.backendMessage.set(response?.message || 'OTP verified successfully!');
        this.clearTimer();
        this.step.set(3);
        this.toastService.showSuccess(this.backendMessage() || 'OTP verified!');
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.message || 'Invalid OTP code. Please try again.');
      }
    });
  }

  isSuccess = signal<boolean>(false);

  onSavePassword() {
    if (this.step3Form.invalid) {
      this.step3Form.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const body = {
      email: this.step1Form.get('email')?.value,
      newPassword: this.step3Form.get('password')?.value,
      confirmPassword: this.step3Form.get('confirmPassword')?.value
    };

    this.http.post<any>('/setPassword', body).subscribe({
      next: (response) => {
        this.isLoading.set(false);
        this.isSuccess.set(true);
        this.toastService.showSuccess(response?.message || 'Password set successfully!', 6000);
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 6000);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.message || 'Failed to set password. Please try again.');
      }
    });
  }

  getPasswordStrength(): { score: number; text: string; colorClass: string; barWidth: string } {
    const val = this.step3Form.get('password')?.value || '';
    if (!val) return { score: 0, text: 'No Password', colorClass: 'bg-gray-800', barWidth: 'w-0' };
    
    let score = 0;
    if (val.length >= 6) score++;
    if (/[A-Z]/.test(val)) score++;
    if (/[a-z]/.test(val)) score++;
    if (/[0-9]/.test(val)) score++;
    if (/[^A-Za-z0-9]/.test(val)) score++;

    switch (score) {
      case 1:
      case 2:
        return { score, text: 'Weak', colorClass: 'bg-red-500', barWidth: 'w-1/3 shadow-[0_0_8px_rgba(239,68,68,0.5)]' };
      case 3:
      case 4:
        return { score, text: 'Medium', colorClass: 'bg-yellow-500', barWidth: 'w-2/3 shadow-[0_0_8px_rgba(234,179,8,0.5)]' };
      case 5:
        return { score, text: 'Strong', colorClass: 'bg-green-500', barWidth: 'w-full shadow-[0_0_8px_rgba(34,197,94,0.5)]' };
      default:
        return { score: 0, text: 'Too Short', colorClass: 'bg-red-500', barWidth: 'w-12 shadow-[0_0_8px_rgba(239,68,68,0.5)]' };
    }
  }

  goBack() {
    this.step.set(1);
    this.errorMessage.set(null);
    this.clearTimer();
  }
}
