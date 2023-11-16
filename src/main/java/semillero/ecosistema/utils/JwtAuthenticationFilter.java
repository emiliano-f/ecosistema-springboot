package semillero.ecosistema.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String JWT_SECRET_KEY;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (request.getServletPath().contains("/api/auth")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = extractToken(request, response, filterChain);

            if (token == null) return;

            Claims claims = processToken(token);

            setAuthentication(claims);

            filterChain.doFilter(request, response);
        } catch (MalformedJwtException e) {
            handleTokenError(response, "Malformed JWT: " + e.getMessage());
        } catch (JwtException e) {
            handleTokenError(response, "JWT Exception: " + e.getMessage());
        }
    }

    private String extractToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return null;
        }

        return header.substring(7);
    }

    private Claims processToken(String token) {
        return Jwts.parser()
                .setSigningKey(JWT_SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    private void setAuthentication(Claims claims) {
        if (claims.getSubject() != null) {
            Object claimsAuthorities = claims.get("authorities");

            if (claimsAuthorities instanceof List<?> list) {
                List<SimpleGrantedAuthority> authorities = list.stream()
                        .filter(authority -> authority instanceof String)
                        .map(authority -> new SimpleGrantedAuthority((String) authority))
                        .toList();

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
    }

    private void handleTokenError(HttpServletResponse response, String error) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + error + "\"}");
    }
}
