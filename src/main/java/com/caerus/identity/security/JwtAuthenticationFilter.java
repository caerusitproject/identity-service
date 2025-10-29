package com.caerus.identity.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);

      try {
        Claims claims = jwtUtil.extractAllClaims(token);

        List<String> privileges = claims.get("privileges", List.class);
        List<SimpleGrantedAuthority> authorities =
            privileges != null
                ? privileges.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                : Collections.emptyList();

        String role = claims.get("role", String.class);
        if (role != null) {
          authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (ExpiredJwtException ex) {
        log.warn("JWT expired: {}", ex.getMessage());
        writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
        return;
      } catch (JwtException ex) {
        log.warn("Invalid JWT: {}", ex.getMessage());
        writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
        return;
      } catch (Exception ex) {
        log.error("JWT processing error: {}", ex.getMessage(), ex);
        writeError(
            response,
            HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            "Internal error validating token");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  private void writeError(HttpServletResponse response, int status, String message)
      throws IOException {
    response.setContentType("application/json");
    response.setStatus(status);
    response.getWriter().write("{\"error\":\"" + message + "\"}");
  }
}
