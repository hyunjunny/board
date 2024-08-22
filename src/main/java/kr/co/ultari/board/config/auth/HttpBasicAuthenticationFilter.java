package kr.co.ultari.board.config.auth;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class HttpBasicAuthenticationFilter extends OncePerRequestFilter {

    @Value("${spring.security.user.name:msgadm}")
    private String userName;

    @Value("${spring.security.user.password:msgadm}")
    private String userPassword;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = HttpBasicProvider.resolveToken(request);
            if (StringUtils.hasText(token)) {
                String[] auth = HttpBasicProvider.parseToken(token);
                if (!ObjectUtils.isEmpty(auth)) {
                    UserDetails userDetails = AuthUserDetails.builder().username(userName).password(userPassword)
                            .build();
                    SecurityContextHolder.getContext()
                            .setAuthentication(HttpBasicProvider.getAuthentication(userDetails));
                }
            }

        } catch (Exception e) {
            throw new ServletException(e);
        }

        filterChain.doFilter(request, response);
    }
}