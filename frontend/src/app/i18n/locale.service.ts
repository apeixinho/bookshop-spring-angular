import { Injectable, computed, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import {
  CountryCode,
  DEFAULT_COUNTRY_CODE,
  LOCALE_OPTIONS,
  LocaleOption,
  STORAGE_KEY,
} from './locale.models';
import { TRANSLATIONS } from './translations';
import { convertFromUsd } from './fx';

@Injectable({ providedIn: 'root' })
export class LocaleService {
  private readonly selectedCode = signal<CountryCode>(this.readStoredCountry());

  readonly options = LOCALE_OPTIONS;

  readonly current = computed(
    () =>
      LOCALE_OPTIONS.find((option) => option.countryCode === this.selectedCode()) ??
      LOCALE_OPTIONS[0],
  );

  readonly language = computed(() => this.current().language);
  readonly currencyCode = computed(() => this.current().currencyCode);
  readonly localeId = computed(() => this.current().localeId);
  readonly countryCode = computed(() => this.current().countryCode);

  selectCountry(code: CountryCode): void {
    if (!LOCALE_OPTIONS.some((option) => option.countryCode === code)) {
      return;
    }
    this.selectedCode.set(code);
    localStorage.setItem(STORAGE_KEY, code);
    document.documentElement.lang = this.language();
  }

  /** Translate a key; reads language signal so templates stay reactive. */
  t(key: string, params?: Record<string, string | number>): string {
    const lang = this.language();
    const dict = TRANSLATIONS[lang] ?? TRANSLATIONS.en;
    let value = dict[key] ?? TRANSLATIONS.en[key] ?? key;
    if (params) {
      for (const [name, replacement] of Object.entries(params)) {
        value = value.replaceAll(`{${name}}`, String(replacement));
      }
    }
    return value;
  }

  /** Convert a catalog USD amount into the active display currency. */
  toDisplayMoney(usdAmount: number): number {
    return convertFromUsd(usdAmount, this.currencyCode());
  }

  formatMoney(usdAmount: number): string {
    const option = this.current();
    const amount = this.toDisplayMoney(usdAmount);
    return (
      new CurrencyPipe(option.localeId).transform(
        amount,
        option.currencyCode,
        'symbol',
        '1.2-2',
        option.localeId,
      ) ?? `${amount}`
    );
  }

  optionLabel(option: LocaleOption): string {
    const country = this.t(option.labelKey);
    const language = this.t(`locale.lang.${option.language}`);
    return `${country} · ${language} · ${option.currencyCode}`;
  }

  private readStoredCountry(): CountryCode {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (stored && LOCALE_OPTIONS.some((option) => option.countryCode === stored)) {
      return stored as CountryCode;
    }
    return DEFAULT_COUNTRY_CODE;
  }
}
