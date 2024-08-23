package kr.co.ultari.board.service;

import kr.co.ultari.board.entities.Board;
import kr.co.ultari.board.entities.BoardUser;
import kr.co.ultari.board.entities.Content;
import kr.co.ultari.board.entities.ContentUser;
import kr.co.ultari.board.repository.BoardRepository;
import kr.co.ultari.board.repository.BoardUserRepository;
import kr.co.ultari.board.repository.ContentRepository;
import kr.co.ultari.board.repository.ContentUserRepository;
import kr.co.ultari.board.repository.mapper.AlarmMapper;
import kr.co.ultari.board.repository.mapper.OrgMapper;
import kr.co.ultari.board.util.StringUtilCustomize;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BoardService {
    @Autowired
    BoardRepository boardRepository;

    @Autowired
    BoardUserRepository boardUserRepository;

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    ContentUserRepository contentUserRepository;

    @Autowired
    OrgMapper orgMapper;

    @Autowired
    AlarmMapper alarmMapper;

    @Autowired
    ConetnetCachedService cachedService;

    @Value("${ultari.board.alarm.use:false}")
    boolean alarmUse;

    @Transactional
    public JSONArray getBoardList(String userid) {
        JSONArray arr = new JSONArray();

        List<Board> list = boardRepository.findAll(Sort.by(Sort.Direction.ASC,"boardOrder"));

        for(Board board:list){
            //log.info(board.getBoardId());
            //log.info(board.getBoardPublic());

            if(board.getBoardUse().equals("0")) continue;

            if(board.getBoardPublic().equals("1")) {
                JSONObject json = new JSONObject();
                json.put("boardId", board.getBoardId());
                json.put("boardName", board.getBoardName());
                json.put("boardOrder", board.getBoardOrder());
                json.put("boardUse", board.getBoardUse());
                json.put("registDate", board.getRegistDate());
                arr.put(json);
            } else {
                for (BoardUser boardUser : board.getBoardUser()) {
                    //log.info(boardUser.getUserId());
                    if (boardUser.getUserId().equals(userid)) {
                        JSONObject json = new JSONObject();
                        json.put("boardId", board.getBoardId());
                        json.put("boardName", board.getBoardName());
                        json.put("boardOrder", board.getBoardOrder());
                        json.put("boardUse", board.getBoardUse());
                        json.put("registDate", board.getRegistDate());
                        arr.put(json);
                    }
                }
            }
        }
        return arr;
    }

    @Transactional
    public JSONArray getAdminBoardList() {
        JSONArray arr = new JSONArray();

        List<Board> list = boardRepository.findAll(Sort.by(Sort.Direction.ASC,"boardOrder"));

        for(Board board:list){
            log.info(board.getBoardId());
            log.info(board.getBoardPublic());

            JSONObject json = new JSONObject();
            json.put("boardId", board.getBoardId());
            json.put("boardName", board.getBoardName());
            json.put("boardOrder", board.getBoardOrder());
            json.put("boardUse", board.getBoardUse());
            json.put("registDate", board.getRegistDate());
            arr.put(json);
        }
        return arr;
    }

    public JSONArray getContentList(String boardId, Pageable pageable) {
        JSONArray arr = new JSONArray();

        List<Content> list = contentRepository.findByBoardIdOrderByRegistDateDesc(boardId, pageable);
        //List<Content> list = cachedService.findByBoardIdOrderByRegistDateDesc(boardId, pageable);

        for(Content content:list){
            JSONObject json = new JSONObject();
            String userName = "UNKNOWN";

            log.debug(content.getUserId());
            Map<String,String> userInfo = orgMapper.getUserInfo(content.getUserId());

            String userId = content.getUserId();
            String loginId = content.getUserId();

            if(userInfo!=null) {
                userName = userInfo.get("username");
                userId = userInfo.get("userid");
            }
            log.debug(userId);

            json.put("boardId",content.getBoardId());
            json.put("contentId",content.getContentId());
            json.put("userId",userId);
            json.put("loginId",loginId);
            json.put("content",content.getContent());
            json.put("registDate", StringUtilCustomize.castNowDate(content.getRegistDate()));
            json.put("replyCount",content.getContentReply().size());
            json.put("userName", userName);
            JSONArray contentUserArr = new JSONArray();
            for(ContentUser contentUser:content.getContentUser()){
                contentUserArr.put(contentUser.getUserId());
            }
            json.put("contentUser",contentUserArr);
            arr.put(json);
        }
        return arr;
    }

    @Transactional
    public boolean deleteContent(String boardId, String contentId, String userId) {
        Content content = contentRepository.findByBoardIdAndContentIdAndUserId(boardId,contentId,userId);
        if(content == null) return false;

        if(alarmUse) alarmMapper.removeAlarm(contentId);

        contentRepository.deleteByContentId(contentId);
        return true;
    }

    public boolean setReadContent(String boardId, String contentId, String userId) {
        contentUserRepository.save(new ContentUser(boardId,contentId,userId,LocalDateTime.now()));
        return true;
    }

    public boolean checkUserId(String userId) {
        Map<String,String> map = orgMapper.getUserInfo(userId);
        if(map != null) return true;
        else return false;
    }

    public String getBoardPublic(String boardId) {
        return boardRepository.findByBoardId(boardId).getBoardPublic();
    }
}

