import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { from, switchMap } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { environment } from '../../environments/environment';

function isBookshopApiUrl(url: string): boolean {
  try {
    const api = new URL(environment.apiBaseUrl, window.location.origin);
    const target = new URL(url, window.location.origin);
    return target.origin === api.origin && target.pathname.startsWith(api.pathname.replace(/\/$/, '') || '/');
  } catch {
    return false;
  }
}

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  if (!isBookshopApiUrl(req.url)) {
    return next(req);
  }

  return from(auth.ensureValidAccessToken()).pipe(
    switchMap((token) => {
      if (!token) {
        return next(req);
      }
      return next(req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
    }),
  );
};
