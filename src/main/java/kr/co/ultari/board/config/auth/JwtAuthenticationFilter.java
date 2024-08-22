package kr.co.ultari.board.config.auth;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {

            String token = jwtProvider.resolveToken(request);
            if (StringUtils.hasText(token)) {

                if (jwtProvider.validateToken(token)) {
                    // check access token
                    token = token.split(" ")[1].trim();

                    Authentication auth = jwtProvider.getAuthentication(token);

                    if (log.isDebugEnabled())
                        log.debug(request.getRequestURI() + ":" + auth.getName() + ":" + auth.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    String newToken = jwtProvider.createToken(jwtProvider.getClaims(token));
                    response.setHeader(HttpHeaders.AUTHORIZATION, newToken);
                }

            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
        }

    }
}