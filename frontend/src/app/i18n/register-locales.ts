import { registerLocaleData } from '@angular/common';
import localeDe from '@angular/common/locales/de';
import localeEn from '@angular/common/locales/en';
import localeEnCa from '@angular/common/locales/en-CA';
import localeEnIn from '@angular/common/locales/en-IN';
import localePt from '@angular/common/locales/pt';
import localePtPt from '@angular/common/locales/pt-PT';
import localeTr from '@angular/common/locales/tr';

let registered = false;

/** Register Angular locale data once for currency formatting. */
export function registerAppLocales(): void {
  if (registered) {
    return;
  }
  registerLocaleData(localePt);
  registerLocaleData(localePtPt);
  registerLocaleData(localeEn);
  registerLocaleData(localeEnCa);
  registerLocaleData(localeEnIn);
  registerLocaleData(localeDe);
  registerLocaleData(localeTr);
  registered = true;
}
