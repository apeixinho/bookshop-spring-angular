import { Injectable, inject, signal } from '@angular/core';
import { LocaleService } from '../i18n/locale.service';

export type ToastTone = 'neutral' | 'success';

export interface Toast {
  id: number;
  message: string;
  tone: ToastTone;
}

const FLASH_KEY = 'catalog.flash';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly items = signal<Toast[]>([]);
  private readonly i18n = inject(LocaleService);
  private nextId = 0;

  readonly toasts = this.items.asReadonly();

  success(message: string, durationMs = 3200): void {
    this.push(message, 'success', durationMs);
  }

  info(message: string, durationMs = 3200): void {
    this.push(message, 'neutral', durationMs);
  }

  dismiss(id: number): void {
    this.items.update((list) => list.filter((toast) => toast.id !== id));
  }

  /** Show a flash message set before a full-page redirect (e.g. OIDC logout). */
  consumeFlash(): void {
    const flash = sessionStorage.getItem(FLASH_KEY);
    if (!flash) {
      return;
    }
    sessionStorage.removeItem(FLASH_KEY);
    this.info(this.i18n.t(flash));
  }

  private push(message: string, tone: ToastTone, durationMs: number): void {
    const id = ++this.nextId;
    this.items.update((list) => [...list, { id, message, tone }]);
    window.setTimeout(() => this.dismiss(id), durationMs);
  }
}
