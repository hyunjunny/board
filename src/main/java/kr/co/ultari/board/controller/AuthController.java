package kr.co.ultari.board.controller;

import kr.co.ultari.board.config.auth.JwtProvider;
import kr.co.ultari.board.service.AuthService;
import kr.co.ultari.board.util.AmCodec;
import kr.co.ultari.board.util.StringUtilCustomize;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    final protected JwtProvider jwtProvider;

    final protected AuthService authService;

    @PostMapping({ "/api/token" })
    public ResponseEntity<String> getToken(@RequestParam("reqToken") String reqToken) {
        log.debug(reqToken);
        ResponseEntity<String> responseEntity;

        AmCodec ac = new AmCodec();
        String decrypt = ac.DecryptSEED(reqToken);
        log.debug(decrypt);

        String userId = decrypt.split("\\|")[0];

        String dateTime = decrypt.split("\\|")[1];

        log.debug(userId);
        log.debug(dateTime);
        long dt = Long.parseLong(dateTime);

        String nowtime = StringUtilCustomize.getNowDate("yyyyMMddHHmmss");
        long now = Long.parseLong(nowtime);

        if(now - dt > 100) {
            responseEntity = new ResponseEntity<>("systemCode error", HttpStatus.UNAUTHORIZED);
            log.debug("error");
        } else {
            //Map<String, String> param = authService.getUserById(userId);
            Map<String,String> param = authService.checkLogin(userId);

            log.debug(param.get("userid"));

            if (!ObjectUtils.isEmpty(param)) {
                String token = jwtProvider.createToken(param.get("userid"), Collections.singletonList("ROLE_USER"), param);

                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.AUTHORIZATION, token);
                responseEntity = new ResponseEntity<>(token, headers, HttpStatus.OK);
                log.debug(token);

            } else {
                responseEntity = new ResponseEntity<>("systemCode error", HttpStatus.UNAUTHORIZED);
                log.debug("error");
            }
        }
        return responseEntity;
    }
}