export type AppLanguage = 'en' | 'pt' | 'de' | 'tr';

export type CountryCode = 'PT' | 'BR' | 'US' | 'CA' | 'DE' | 'IN' | 'TR';

export interface LocaleOption {
  countryCode: CountryCode;
  /** BCP 47 locale for Angular currency/date formatting */
  localeId: string;
  language: AppLanguage;
  currencyCode: string;
  /** Native / preferred display name for the country */
  labelKey: string;
}

/** Checkout-supported countries with language + currency rules. */
export const LOCALE_OPTIONS: readonly LocaleOption[] = [
  {
    countryCode: 'PT',
    localeId: 'pt-PT',
    language: 'pt',
    currencyCode: 'EUR',
    labelKey: 'locale.country.PT',
  },
  {
    countryCode: 'BR',
    localeId: 'pt',
    language: 'pt',
    currencyCode: 'BRL',
    labelKey: 'locale.country.BR',
  },
  {
    countryCode: 'US',
    localeId: 'en-US',
    language: 'en',
    currencyCode: 'USD',
    labelKey: 'locale.country.US',
  },
  {
    countryCode: 'CA',
    localeId: 'en-CA',
    language: 'en',
    currencyCode: 'CAD',
    labelKey: 'locale.country.CA',
  },
  {
    countryCode: 'DE',
    localeId: 'de-DE',
    language: 'de',
    currencyCode: 'EUR',
    labelKey: 'locale.country.DE',
  },
  {
    countryCode: 'IN',
    localeId: 'en-IN',
    language: 'en',
    currencyCode: 'INR',
    labelKey: 'locale.country.IN',
  },
  {
    countryCode: 'TR',
    localeId: 'tr-TR',
    language: 'tr',
    currencyCode: 'TRY',
    labelKey: 'locale.country.TR',
  },
] as const;

export const DEFAULT_COUNTRY_CODE: CountryCode = 'PT';

export const STORAGE_KEY = 'bookshop.locale.country';
