package kr.co.ultari.board.config.auth;

import de.codecentric.boot.admin.client.registration.BlockingRegistrationClient;
import java.util.Arrays;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class CustomRegistrationClient extends BlockingRegistrationClient {

    @Autowired
    JwtProvider provider;

    String token;

    @Scheduled(fixedDelay = 1000*1800)
    public void getToken() {
        token = "Bearer " + provider.createToken("msgadm", Arrays.asList("ROLE_ADMIN"));
    }

    public CustomRegistrationClient(RestTemplate restTemplate) {
        super(restTemplate);

    }

    @Override
    protected HttpHeaders createRequestHeaders() {
        if(!StringUtils.hasText(token))
            getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set(HttpHeaders.AUTHORIZATION, token);
        return HttpHeaders.readOnlyHttpHeaders(headers);
    }
}