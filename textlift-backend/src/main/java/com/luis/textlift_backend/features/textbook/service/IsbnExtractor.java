package com.luis.textlift_backend.features.textbook.service;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IsbnExtractor {
    private static final Pattern ISBN_CANDIDATE = Pattern.compile(
            "(?i)\\b(?:isbn(?:-1[03])?\\s*[:#]?)?\\s*(" +
                    "(?:97[89][\\s-]?)?\\d[\\d\\s-]{8,16}[\\dXx]" +
            ")\\b"
    );

    public static Optional<String> extractBestIsbn13(String text) {
        if (text == null || text.isBlank()) return Optional.empty();

        Set<String> isbn13s = new LinkedHashSet<>();
        Set<String> isbn10s = new LinkedHashSet<>();

        Matcher m = ISBN_CANDIDATE.matcher(text);
        while (m.find()) {
            String raw = m.group(1);
            String normalized = normalize(raw);

            if (normalized.length() == 13 && isValidIsbn13(normalized)) {
                isbn13s.add(normalized);
            } else if (normalized.length() == 10 && isValidIsbn10(normalized)) {
                isbn10s.add(normalized.toUpperCase());
            }
        }

        if (!isbn13s.isEmpty()) return Optional.of(isbn13s.iterator().next());

        for (String ten : isbn10s) {
            String thirteen = isbn10to13(ten);
            if (isValidIsbn13(thirteen)) return Optional.of(thirteen);
        }

        return Optional.empty();
    }

    private static String normalize(String raw) {
        // Keep digits and X only
        return raw.replaceAll("[^0-9Xx]", "");
    }

    private static boolean isValidIsbn13(String s) {
        if (s == null || !s.matches("\\d{13}")) return false;
        int sum = 0;
        for (int i = 0; i < 13; i++) {
            int d = s.charAt(i) - '0';
            sum += (i % 2 == 0) ? d : 3 * d;
        }
        return sum % 10 == 0;
    }

    private static boolean isValidIsbn10(String s) {
        if (s == null || !s.matches("\\d{9}[\\dXx]")) return false;
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            char c = s.charAt(i);
            int d = (c == 'X' || c == 'x') ? 10 : (c - '0');
            sum += d * (10 - i);
        }
        return sum % 11 == 0;
    }

    private static String isbn10to13(String isbn10) {
        String core9 = isbn10.substring(0, 9);
        String prefix12 = "978" + core9;

        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int d = prefix12.charAt(i) - '0';
            sum += (i % 2 == 0) ? d : 3 * d;
        }
        int check = (10 - (sum % 10)) % 10;
        return prefix12 + check;
    }

}
