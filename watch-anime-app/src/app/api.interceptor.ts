import { HttpInterceptorFn } from '@angular/common/http';

export const apiInterceptor: HttpInterceptorFn = (req, next) => {
  // If we are running locally on port 4200, route to local Spring Boot.
  // In production, keep it relative (e.g. '/fetchAnime') because 
  // Angular and Spring Boot are hosted on the exact same domain!
  if (window.location.hostname === 'localhost' && req.url.startsWith('/')) {
    const apiReq = req.clone({
      url: `http://localhost:8080${req.url}`
    });
    return next(apiReq);
  }

  // If in production, just pass the relative URL through normally.
  // The browser will automatically prepend the correct active domain!
  return next(req);
};
