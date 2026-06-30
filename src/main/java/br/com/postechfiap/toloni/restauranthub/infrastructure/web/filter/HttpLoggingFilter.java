package br.com.postechfiap.toloni.restauranthub.infrastructure.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/// HTTP filter that logs all incoming requests and their responses.
///
/// Logs the HTTP method, URI, status code, elapsed time, and request body.
/// Sensitive fields such as `password` are masked in the log output.
/// Health check endpoints are ignored.
@Component
public class HttpLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(HttpLoggingFilter.class);

    private static final List<String> IGNORED_PATHS = List.of(
            "/actuator/health",
            "/actuator"
    );

    private static final List<String> SENSITIVE_FIELDS = List.of("password");

    @Value("${app.logging.http.enabled:true}")
    private boolean enabled;

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        if (!enabled) return true;
        return IGNORED_PATHS.stream()
                .anyMatch(path -> request.getRequestURI().startsWith(path));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var wrappedRequest = new ContentCachingRequestWrapper(request, 10 * 1024);
        var wrappedResponse = new ContentCachingResponseWrapper(response);
        var start = System.currentTimeMillis();

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            var elapsed = System.currentTimeMillis() - start;
            var requestBody = getRequestBody(wrappedRequest);
            var responseBody = getResponseBody(wrappedResponse);

            log.info("{} {} -> {} ({}ms){}{}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    elapsed,
                    requestBody.isBlank() ? "" : " request=" + maskSensitiveFields(requestBody),
                    responseBody.isBlank() ? "" : " response=" + maskSensitiveFields(responseBody));

            wrappedResponse.copyBodyToResponse();
        }
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        var body = request.getContentAsByteArray();
        if (body.length == 0) return "";
        return new String(body, StandardCharsets.UTF_8);
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        var body = response.getContentAsByteArray();
        if (body.length == 0) return "";
        return new String(body, StandardCharsets.UTF_8);
    }

    private String maskSensitiveFields(String body) {
        var masked = body;
        for (var field : SENSITIVE_FIELDS) {
            masked = masked.replaceAll(
                    "(?i)(\"" + field + "\"\\s*:\\s*\")([^\"]*)(\")",
                    "\"" + field + "\":\"***\""
            );
        }
        return masked;
    }
}