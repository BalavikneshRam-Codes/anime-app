import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { ToastService } from './shared/toast.service';

export const apiInterceptor: HttpInterceptorFn = (req, next) => {
  const toastService = inject(ToastService);
  let apiReq = req;

  // If we are running locally on port 4200, route to local Spring Boot.
  // In production, keep it relative (e.g. '/fetchAnime') because 
  // Angular and Spring Boot are hosted on the exact same domain!
  if (window.location.hostname === 'localhost' && req.url.startsWith('/')) {
    apiReq = req.clone({
      url: `http://localhost:8080${req.url}`
    });
  }

  return next(apiReq).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'An unexpected error occurred. Please try again.';
      
      if (error.error && typeof error.error.message === 'string') {
        // Backend sent a specific error message
        errorMessage = error.error.message;
      } else if (error.status === 0) {
        errorMessage = 'Unable to connect to the server. Please check your internet connection.';
      } else if (error.status === 404) {
        errorMessage = 'The requested resource was not found.';
      } else if (error.status >= 500) {
        errorMessage = 'Server error. Our team has been notified.';
      }

      toastService.showError(errorMessage);
      
      return throwError(() => error);
    })
  );
};
