package kr.co.ultari.board.config.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import kr.co.ultari.board.exceptions.IsWrongPasswordException;
import kr.co.ultari.board.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtProvider {

    @Value("${jwt.secret.key:x!A%D*G-KaPdSgVkYp3s5v8y/B?E(H+M}")
    private String key;

    private Key secretKey;

    @Value("${ultari.jwt.expiration:3600}")
    private long exp;

    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String account, List<String> roles, Map<String, String> params) {
        Claims claims = Jwts.claims().setSubject(account);
        claims.put("roles", roles);
        claims.putAll(params);
        Date now = new Date();
        return Jwts.builder().setHeaderParam("typ", "JWT").setClaims(claims).setIssuedAt(now).setExpiration(new Date(now.getTime() + exp * 1000))
                .signWith(secretKey, SignatureAlgorithm.HS256).compact();
    }

    public String createToken(String account, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(account);
        claims.put("roles", roles);
        Date now = new Date();
        return Jwts.builder().setHeaderParam("typ", "JWT").setClaims(claims).setIssuedAt(now).setExpiration(new Date(now.getTime() + exp * 1000))
                .signWith(secretKey, SignatureAlgorithm.HS256).compact();
    }

    public String createToken(Claims claims) {
        Date now = new Date();
        return Jwts.builder().setHeaderParam("typ", "JWT").setClaims(claims).setIssuedAt(now).setExpiration(new Date(now.getTime() + exp * 1000))
                .signWith(secretKey, SignatureAlgorithm.HS256).compact();
    }

    public Authentication getAuthentication(String token) throws UserNotFoundException, IsWrongPasswordException {

        AuthUserDetails userDetails = AuthUserDetails.builder().username(getAccount(token)).password(getAccount(token)).build();
        for (String role : getRoles(token)) {
            userDetails.addAuthority(role);
        }

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getAccount(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    public Claims getClaims(String token) {
        log.debug(token);
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {

        Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
        return (List<String>) claims.getBody().get("roles");
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }

    public boolean validateToken(String token) {
        if (!token.substring(0, "BEARER ".length()).equalsIgnoreCase("BEARER ")) {
            return false;
        } else {
            token = token.split(" ")[1].trim();
        }
        Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);

        return !claims.getBody().getExpiration().before(new Date());
    }

    public String getToken(String token) {
        if (!token.substring(0, "BEARER ".length()).equalsIgnoreCase("BEARER ")) {
            return "";
        } else {
            token = token.split(" ")[1].trim();
        }

        return token;
    }
}