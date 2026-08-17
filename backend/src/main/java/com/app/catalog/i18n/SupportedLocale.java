package com.app.catalog.i18n;

import java.util.Locale;
import java.util.Set;

/**
 * Normalizes API {@code lang} values to catalog translation locales ({@code en|pt|de|tr}).
 */
public final class SupportedLocale {

    public static final String DEFAULT = "en";

    private static final Set<String> SUPPORTED = Set.of("en", "pt", "de", "tr");

    private SupportedLocale() {
    }

    public static String normalize(String lang) {
        if (lang == null || lang.isBlank()) {
            return DEFAULT;
        }
        String primary = Locale.forLanguageTag(lang.trim().replace('_', '-')).getLanguage();
        if (primary == null || primary.isBlank()) {
            return DEFAULT;
        }
        String lower = primary.toLowerCase(Locale.ROOT);
        return SUPPORTED.contains(lower) ? lower : DEFAULT;
    }
}
