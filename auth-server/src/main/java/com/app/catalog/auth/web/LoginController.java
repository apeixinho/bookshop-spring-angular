package com.app.catalog.auth.web;

import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;

@Controller
public class LoginController {

    private final String frontendOrigin;
    private final LocaleResolver localeResolver;

    public LoginController(
        @Value("${catalog.auth.frontend-origin:http://localhost:4200}") String frontendOrigin,
        LocaleResolver localeResolver) {
        this.frontendOrigin = frontendOrigin.split(",")[0].trim();
        this.localeResolver = localeResolver;
    }

    @GetMapping("/login")
    public String login(
        @RequestParam(required = false) String lang,
        @RequestParam(name = "ui_locales", required = false) String uiLocales,
        HttpServletRequest request,
        HttpServletResponse response,
        Model model) {

        String fromQuery = firstNonBlank(lang, uiLocales);
        String normalized;
        if (fromQuery != null) {
            normalized = LoginLocaleFilter.normalize(fromQuery);
        } else {
            normalized = LoginLocaleFilter.normalize(localeResolver.resolveLocale(request).getLanguage());
        }
        if (normalized == null) {
            normalized = "en";
        }
        localeResolver.setLocale(request, response, Locale.forLanguageTag(normalized));

        model.addAttribute("shopUrl", frontendOrigin + "/products");
        return "login";
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
