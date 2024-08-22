package kr.co.ultari.board.controller;

import io.jsonwebtoken.Claims;
import kr.co.ultari.board.entities.ContentReply;
import kr.co.ultari.board.service.AuthService;
import kr.co.ultari.board.service.ReplyService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class ReplyController {
    @Autowired
    ReplyService replyService;

    @Autowired
    AuthService authService;

    @PostMapping("contentreply/list")
    @ResponseBody
    public String contentReplyList(final HttpServletRequest request, @RequestParam("boardId") String boardId,@RequestParam("contentId") String contentId,@RequestParam("userId") String userId) {
        log.debug(boardId);
        log.debug(contentId);
        log.debug(userId);

        Claims claims = authService.getClaims(request);
        String tokenUserId = claims.getSubject();
        log.debug(claims.getSubject());

        JSONObject json = new JSONObject();
        JSONArray arr = replyService.getReplyList(boardId,contentId,userId);
        json.put("list",arr);
        log.debug(json.toString());
        return json.toString();
    }

    @PostMapping("contentreply/upload")
    @ResponseBody
    public ResponseEntity<String> contentReplyUpload(@RequestBody ContentReply dto, final HttpServletRequest request) {
        Claims claims = authService.getClaims(request);
        String tokenUserId = claims.getSubject();
        log.debug(claims.getSubject());

        String rtn ="1";

        //if(!tokenUserId.equals(dto.getUserId())) return ResponseEntity.ok(rtn);
        dto.setUserId(tokenUserId);
        log.debug(dto.getUserId());
        log.debug(dto.getContentId());
        log.debug(dto.getBoardId());
        log.debug(dto.getReply());
        if(replyService.uploadReply(dto)) rtn = "0";
        log.debug(rtn);
        return ResponseEntity.ok(rtn);
    }
}