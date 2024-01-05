package dev.idriz.videomaker.security.filter;

import dev.idriz.videomaker.service.AuthService;
import dev.idriz.videomaker.token.JWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWT jwt;
    private final AuthService authService;

    public JwtAuthenticationFilter(JWT jwt, AuthService authService) {
        this.jwt = jwt;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        var authz = request.getHeader("Authorization");
        if (authz == null) {
            filterChain.doFilter(request, response);
            return;
        }
        var token = authz.replace("Bearer ", "");
        var userId = jwt.extractUserId(token);
        if (userId == null) {
            filterChain.doFilter(request, response);
            return;
        }
        var uuid = UUID.fromString(userId);
        authService.authenticateCurrentThread(uuid);
        filterChain.doFilter(request, response);
    }
}
