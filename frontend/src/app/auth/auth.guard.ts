import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = async (_route, state) => {
  const auth = inject(AuthService);
  const token = await auth.ensureValidAccessToken();
  if (token) {
    return true;
  }
  void auth.login(state.url);
  return false;
};
