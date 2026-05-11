package kz.app.jsonapidemo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class JsonApiContentTypeFilter extends OncePerRequestFilter {

    private static final Set<String> JSON_API_SUPPORTED_METHODS = Set.of("POST", "PUT", "PATCH");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (isJsonApiSupportedMethod(request.getMethod())) {
            String contentType = request.getContentType();
            if (contentType == null || !contentType.contains("application/vnd.api+json")) {
                response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
                return;
            }
        }

        response.setContentType("application/vnd.api+json");
        filterChain.doFilter(request, response);
    }

    private boolean isJsonApiSupportedMethod(String method) {
        return JSON_API_SUPPORTED_METHODS.contains(method);
    }
}
