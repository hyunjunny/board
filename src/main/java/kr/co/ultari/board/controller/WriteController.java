package kr.co.ultari.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import kr.co.ultari.board.entities.Content;
import kr.co.ultari.board.service.AuthService;
import kr.co.ultari.board.service.ContentService;
import kr.co.ultari.board.service.WriteService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class WriteController {
    @Autowired
    WriteService writeService;

    @Autowired
    ContentService contentService;

    @Autowired
    AuthService authService;

    @Autowired
    ObjectMapper mapper;

    @RequestMapping("contentwrite/{boardId}")
    public String contentWrite(final HttpServletRequest request, Model model, @PathVariable("boardId") String boardId) throws Exception {

        if(log.isTraceEnabled())
            log.trace(mapper.writeValueAsString(model));

        Claims claims = authService.getClaims(request);
        String tokenUserId = claims.getSubject();
        log.debug(boardId+", "+tokenUserId);

        model.addAttribute("userId", tokenUserId);
        model.addAttribute("boardId",boardId);
        return "tui_write.html";
    }

    @PostMapping("contentwrite/upload")
    @ResponseBody
    public ResponseEntity<String> contentWriteUpload(@RequestBody Content dto, final HttpServletRequest request) throws Exception{

        if(log.isTraceEnabled())
            log.trace(mapper.writeValueAsString(dto));
        log.debug(dto.getUserId());

        Claims claims = authService.getClaims(request);
        String tokenUserId = claims.getSubject();
        log.debug(claims.getSubject());
        dto.setUserId(tokenUserId);
        String rtn ="1";
        if(writeService.uploadContent(dto)) rtn = "0";
        log.debug(rtn);
        return ResponseEntity.ok(rtn);
    }

    @RequestMapping("/contentwrite/modify/{boardId}/{contentId}")
    public String contentModify(Model model, final HttpServletRequest request, @PathVariable("boardId") String boardId, @PathVariable("contentId") String contentId) throws Exception {

        Claims claims = authService.getClaims(request);
        String tokenUserId = claims.getSubject();
        log.debug(claims.getSubject());
        log.debug(boardId+", "+tokenUserId+", "+contentId);
        if(!tokenUserId.equalsIgnoreCase(tokenUserId)) return "error";

        Content content = contentService.getContent(boardId,contentId,tokenUserId);
        model.addAttribute("userId", content.getUserId());
        model.addAttribute("boardId",content.getBoardId());
        model.addAttribute("contentId",content.getContentId());
        model.addAttribute("registDate", content.getRegistDate());
        model.addAttribute("modifyContent","1");
        return "tui_write.html";
    }

    @RequestMapping("/contentwrite/getcontent/{contentId}")
    @ResponseBody
    public String getContent(final HttpServletRequest request, @PathVariable("contentId") String contentId) {
        log.debug(contentId);
        Claims claims = authService.getClaims(request);
        String tokenUserId = claims.getSubject();
        log.debug(claims.getSubject());

        JSONObject json = writeService.getContent(contentId);
        log.debug(json.toString());
        return json.toString();
    }
}
