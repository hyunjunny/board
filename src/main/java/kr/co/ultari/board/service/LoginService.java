package kr.co.ultari.board.service;

import kr.co.ultari.board.repository.mapper.OrgMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class LoginService {
    @Autowired
    OrgMapper orgMapper;

    public boolean checkLogin(String userId){
        Map<String, String> map = orgMapper.getUserInfo(userId);

        if(map!=null) return true;
        else return false;
    }
}
