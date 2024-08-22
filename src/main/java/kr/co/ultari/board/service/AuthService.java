package kr.co.ultari.board.service;

import io.jsonwebtoken.Claims;
import kr.co.ultari.board.config.auth.JwtProvider;
import kr.co.ultari.board.repository.BoardUserRepository;
import kr.co.ultari.board.repository.ContentUserRepository;
import kr.co.ultari.board.repository.mapper.OrgMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service
@Slf4j
public class AuthService {

    @Autowired
    BoardUserRepository boardUserRepository;

    @Autowired
    ContentUserRepository contentUserRepository;

    @Autowired
    OrgMapper orgMapper;

    public Map<String,String> getUserById(String userId)
    {
        return orgMapper.getUserInfo(userId);
    }

    public Map<String, String> checkLogin(String userId)
    {
        return orgMapper.checkLogin(userId);
    }

    @Autowired
    JwtProvider jwtProvider;

    public Claims getClaims(HttpServletRequest request)
    {
        Claims claims = null;
        String token = jwtProvider.resolveToken(request);
        if(StringUtils.hasText(token))
        {
            claims = jwtProvider.getClaims(jwtProvider.getToken(token));
        }
        return claims;
    }
}