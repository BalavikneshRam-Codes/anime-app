import { HttpInterceptorFn } from '@angular/common/http';
import { isDevMode } from '@angular/core';

export const apiInterceptor: HttpInterceptorFn = (req, next) => {
  // In development (ng serve), route to local Spring Boot.
  // In production, keep it relative (e.g. '/fetchAnime') because 
  // Angular and Spring Boot are hosted on the exact same domain!
  if (isDevMode() && req.url.startsWith('/')) {
    const apiReq = req.clone({
      url: `http://localhost:8080${req.url}`
    });
    return next(apiReq);
  }

  // If in production, just pass the relative URL through normally.
  // The browser will automatically prepend the correct active domain!
  return next(req);
};
