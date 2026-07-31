package com.app.bookshop.auth.web;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.LocaleResolver;

/**
 * Captures {@code ui_locales} / {@code lang} from the authorize request and stores
 * it in {@link org.springframework.web.servlet.i18n.SessionLocaleResolver} so the
 * login form renders in the SPA language after redirect to {@code /login}.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class LoginLocaleFilter extends OncePerRequestFilter {

    private static final Set<String> SUPPORTED = Set.of("en", "pt", "de", "tr");

    private final LocaleResolver localeResolver;

    public LoginLocaleFilter(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path != null && path.contains("/oauth2/authorize")) {
            String raw = firstNonBlank(request.getParameter("ui_locales"), request.getParameter("lang"));
            String normalized = normalize(raw);
            if (normalized != null) {
                localeResolver.setLocale(request, response, Locale.forLanguageTag(normalized));
            }
        }
        filterChain.doFilter(request, response);
    }

    static String normalize(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String primary = Locale.forLanguageTag(raw.trim().replace('_', '-').split("[,\\s]")[0])
            .getLanguage()
            .toLowerCase(Locale.ROOT);
        return SUPPORTED.contains(primary) ? primary : "en";
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) {
            return a;
        }
        if (b != null && !b.isBlank()) {
            return b;
        }
        return null;
    }
}
