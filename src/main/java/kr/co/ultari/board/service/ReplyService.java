package kr.co.ultari.board.service;

import kr.co.ultari.board.entities.ContentReply;
import kr.co.ultari.board.repository.ContentReplyRepository;
import kr.co.ultari.board.repository.mapper.OrgMapper;
import kr.co.ultari.board.util.StringUtilCustomize;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ReplyService {
    @Autowired
    ContentReplyRepository replyRepository;

    @Autowired
    OrgMapper orgMapper;

    public JSONArray getReplyList(String boardId, String contentId, String userId) {
        JSONArray arr = new JSONArray();

        List<ContentReply> list = replyRepository.findByContentIdOrderByRegistDateAsc(contentId);
        for(ContentReply reply:list){
            String userName = "UNKNOWN";
            Map<String,String> userInfo = orgMapper.getUserInfo(reply.getUserId());
            userId = reply.getUserId();
            String loginId = reply.getUserId();
            if(userInfo!=null) {
                userName = userInfo.get("username");
                userId = userInfo.get("userid");
            }

            JSONObject json = new JSONObject();
            json.put("replyId",reply.getReplyId());
            json.put("contentId",reply.getContentId());
            json.put("boardId",reply.getBoardId());
            json.put("userId",userId);
            json.put("loginId",loginId);
            json.put("reply",reply.getReply());
            json.put("registDate", StringUtilCustomize.castNowDate(reply.getRegistDate()));
            json.put("userName", userName);
            arr.put(json);
        }
        return arr;
    }

    public boolean uploadReply(ContentReply reply) {
        reply.setReplyId(StringUtilCustomize.getUUID());
        reply.setRegistDate(LocalDateTime.now());
        replyRepository.save(reply);
        return true;
    }
}
