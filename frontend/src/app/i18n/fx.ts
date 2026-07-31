/**
 * Catalog unit prices are stored in USD. Convert with fixed demo rates for display/checkout.
 * TRY 41.00 is a documented demo default — not supplied by product.
 */
export const USD_TO_RATES: Readonly<Record<string, number>> = {
  USD: 1,
  EUR: 0.87,
  CAD: 1.4,
  BRL: 5.06,
  INR: 95.52,
  TRY: 41.0,
};

export function convertFromUsd(usdAmount: number, currencyCode: string): number {
  const rate = USD_TO_RATES[currencyCode.toUpperCase()] ?? 1;
  return Math.round((Number(usdAmount) || 0) * rate * 100) / 100;
}
