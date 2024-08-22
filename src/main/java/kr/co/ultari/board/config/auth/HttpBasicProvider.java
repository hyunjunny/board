package kr.co.ultari.board.config.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Base64Utils;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class HttpBasicProvider {

    public static Authentication getAuthentication(UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public static String resolveToken(HttpServletRequest request) {
        log.info(request.getHeader(HttpHeaders.AUTHORIZATION));
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }

    public static String[] parseToken(String token) {
        try {
            // Bearer 검증
            if (!token.substring(0, "Basic ".length()).equalsIgnoreCase("Basic ")) {
                return null;
            } else {
                token = token.split(" ")[1].trim();
            }
            String[] decrypt = new String(Base64Utils.decodeFromString(token)).split(":");

            return decrypt;
        } catch (Exception e) {
            log.error("",e);
            return null;
        }
    }
}