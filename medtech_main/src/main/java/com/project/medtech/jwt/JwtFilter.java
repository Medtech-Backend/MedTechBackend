package com.project.medtech.jwt;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private final JwtProvider jwtProvider;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        System.out.println(((HttpServletRequest) request).getServletPath());
        if (((HttpServletRequest) request).getServletPath().equals("/api/v1/auth/login") ||
                ((HttpServletRequest) request).getServletPath().equals("/api/v1/auth/refresh") ||
                ((HttpServletRequest) request).getServletPath().equals("/api/v1/user/register") ||
                ((HttpServletRequest) request).getServletPath().equals("/api/v1/user/check") ||
                ((HttpServletRequest) request).getServletPath().equals("/api/v1/user/sent_reset_code") ||
                ((HttpServletRequest) request).getServletPath().equals("/api/v1/user/check_reset_code")) {
            filterChain.doFilter(request, response);
        } else {
            String token = getTokenFromRequest((HttpServletRequest) request);
            if (token != null && jwtProvider.validateToken(token)) {
                final Claims claims = jwtProvider.getClaims(token);
                ArrayList<String> roles = (ArrayList<String>) claims.get("roles");
                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
                final UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(claims.getSubject(),
                                null, authorities
                        );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(request, response);
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String bearer = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
