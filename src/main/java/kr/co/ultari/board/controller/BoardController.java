package kr.co.ultari.board.controller;

import io.jsonwebtoken.Claims;
import kr.co.ultari.board.repository.mapper.OrgMapper;
import kr.co.ultari.board.service.AuthService;
import kr.co.ultari.board.service.BoardService;
import kr.co.ultari.board.service.ContentService;
import kr.co.ultari.board.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@Controller
public class BoardController {
    @Autowired
    BoardService boardService;

    @Autowired
    AuthService authService;

    @Autowired
    ContentService contentService;

    @Autowired
    LoginService loginService;

    @Autowired
    OrgMapper orgMapper;

    @Value("${ultari.board.view-content-size:5}")
    private int viewContentSize;

    @Value("${ultari.other-picture.url:}")
    String photoUrl;

    @RequestMapping("/")
    public String index(Model model, final HttpServletRequest request, @RequestParam(required = false) String boardId){

        try
        {
            Claims claims = authService.getClaims(request);
            String tokenUserId = claims.getSubject();
            log.debug(claims.getSubject());

            model.addAttribute("userId", tokenUserId);
            model.addAttribute("boardId",boardId);
            model.addAttribute("viewSize",viewContentSize);
            model.addAttribute("photoUrl",photoUrl);

        }
        catch(Exception e)
        {
            log.error("",e);
            throw new RuntimeException(e);
        }

        return "index";
    }

    @PostMapping("getboardlist")
    @ResponseBody
    public String getBoardList(final HttpServletRequest request){

        Claims claims = authService.getClaims(request);
        String tokenUserId = claims.getSubject();
        log.debug(claims.getSubject());

        log.debug(tokenUserId);

        JSONObject json = new JSONObject();
        JSONArray arr = new JSONArray();
        if(tokenUserId.equals("msgadm")) arr = boardService.getAdminBoardList();
        else arr = boardService.getBoardList(tokenUserId);

        json.put("list",arr);
        //log.debug(json.toString());
        return json.toString();
    }

    @RequestMapping("getcontentlist")
    @ResponseBody
    @Cacheable(value = "CONTENT")
    public String getContentList(final HttpServletResponse response, final HttpServletRequest request, @RequestParam String boardId, Pageable pageable){
        log.debug(boardId);
        JSONObject json = new JSONObject();
        JSONArray arr = boardService.getContentList(boardId,pageable);
        json.put("list",arr);
        //log.debug(json.toString());
        return json.toString();
    }

    @PostMapping("deletecontent")
    @ResponseBody
    public ResponseEntity<String> deleteContent(final HttpServletRequest request, @RequestParam String boardId, @RequestParam String contentId){
        log.debug(boardId);
        log.debug(contentId);

        Claims claims = authService.getClaims(request);
        String userId = claims.getSubject();
        log.debug(claims.getSubject());

        String rtn ="1";
        if(boardService.deleteContent(boardId,contentId,userId)) rtn = "0";
        log.debug(rtn);
        return ResponseEntity.ok(rtn);
    }

    @RequestMapping("setreadcontent/{boardId}/{contentId}")
    @ResponseBody
    public ResponseEntity<String> setReadContent(final HttpServletRequest request, @PathVariable("boardId") String boardId, @PathVariable("contentId") String contentId) {
        Claims claims = authService.getClaims(request);
        String tokenUserId = claims.getSubject();
        log.debug(claims.getSubject());

        log.debug(boardId);
        log.debug(contentId);
        log.debug(tokenUserId);

        String rtn ="0";
        if(!boardService.setReadContent(boardId,contentId,tokenUserId)) rtn = "1";
        log.debug(rtn);
        return ResponseEntity.ok(rtn);
    }

    @RequestMapping("/getcontentreaduser/{boardId}/{contentId}")
    public String getContentReadUser(Model model, final HttpServletRequest request, @PathVariable("boardId") String boardId, @PathVariable("contentId") String contentId){
        Claims claims = authService.getClaims(request);
        String tokenUserId = claims.getSubject();
        log.debug(claims.getSubject());

        Map<String, String> map = orgMapper.getUserInfo(tokenUserId);

        String boardPublic = boardService.getBoardPublic(boardId);

        JSONArray arr = contentService.getReadUserList(boardId, contentId, map.get("userhigh"));
        JSONArray deptArr = contentService.getReadDeptList(map.get("userhigh"));
        JSONArray orgDepthArr = contentService.getOrgDepthList(map.get("userhigh"));
        log.debug(arr.toString());
        log.debug(deptArr.toString());
        log.debug(orgDepthArr.toString());

        String recvCount = contentService.getReadUserCount(boardId,contentId);
        log.debug(recvCount);

        model.addAttribute("boardId",boardId);
        model.addAttribute("contentId",contentId);
        model.addAttribute("deptData", deptArr.toString());
        model.addAttribute("userData",arr.toString());
        model.addAttribute("userName",map.get("username"));
        model.addAttribute("posName",map.get("posname"));
        model.addAttribute("orgDepth", orgDepthArr.toString());
        model.addAttribute("recvCount",recvCount);
        model.addAttribute("photoUrl",photoUrl);
        model.addAttribute("photoId",map.get("userid"));
        return "contentUserView";
    }

    @RequestMapping("/getcontentreaduser/{boardId}/{contentId}/{deptId}")
    @ResponseBody
    public String getContentReadSubOrg(Model model, final HttpServletRequest request, @PathVariable("boardId") String boardId, @PathVariable("contentId") String contentId, @PathVariable("deptId") String deptId){
        Claims claims = authService.getClaims(request);
        String tokenUserId = claims.getSubject();
        log.debug(claims.getSubject());

        Map<String, String> map = orgMapper.getUserInfo(tokenUserId);

        JSONArray arr = contentService.getReadUserList(boardId, contentId, deptId);
        JSONArray deptArr = contentService.getReadDeptList(deptId);
        JSONArray orgDepthArr = contentService.getOrgDepthList(deptId);
        log.debug(arr.toString());
        log.debug(deptArr.toString());
        log.debug(orgDepthArr.toString());

        String recvCount = contentService.getReadUserCount(boardId,contentId);
        log.debug(recvCount);

        JSONObject json = new JSONObject();

        json.put("boardId",boardId);
        json.put("contentId",contentId);
        json.put("deptData", deptArr);
        json.put("userData",arr);
        json.put("userName",map.get("username"));
        json.put("orgDepth", orgDepthArr);
        json.put("recvCount", recvCount);

        log.debug(json.toString());
        return json.toString();
    }
}
