/**
 * Catalog unit prices are USD. Rates mirror backend CurrencyRates (authoritative).
 * Rounding: convert each unit with HALF_UP to 2dp, then multiply by quantity.
 * TRY 41.00 is a documented demo default.
 */
export type FxRates = Readonly<Record<string, number>>;

export const FALLBACK_USD_TO_RATES: FxRates = {
  USD: 1,
  EUR: 0.87,
  CAD: 1.4,
  BRL: 5.06,
  INR: 95.52,
  TRY: 41.0,
};

export function convertUnitFromUsd(usdAmount: number, currencyCode: string, rates: FxRates): number {
  const rate = rates[currencyCode.toUpperCase()] ?? 1;
  return Math.round((Number(usdAmount) || 0) * rate * 100) / 100;
}

export function lineTotalFromUsd(
  usdUnitPrice: number,
  quantity: number,
  currencyCode: string,
  rates: FxRates,
): number {
  const unit = convertUnitFromUsd(usdUnitPrice, currencyCode, rates);
  return Math.round(unit * quantity * 100) / 100;
}
