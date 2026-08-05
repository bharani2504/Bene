package com.example.bene.security.filter;

import com.example.bene.security.Jwt;
import com.example.bene.validator.BeneValidation;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private Jwt jwt;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        String uri = request.getRequestURI();
        if (uri.contains("/login") || (uri.contains("/corp/save"))) {
            filterChain.doFilter(request, response);
            return;
        }
        if (request.getHeader("Authorization") != null) {
            String headers = request.getHeader("Authorization");
            if (headers == null || !headers.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Missing or invalid Authorization header");
            }
            String token = headers.substring(7);

            try {
                String userCrn = jwt.extractUserCrn(token);
                boolean valid = jwt.validateToken(token, userCrn);
                if (valid) {
                    String role = jwt.extractRoles(token);

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    userCrn,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
                            );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    BeneValidation.applyError("The Given Token is Expired");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            filterChain.doFilter(request, response);

        }
    }
}
