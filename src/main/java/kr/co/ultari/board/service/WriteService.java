package kr.co.ultari.board.service;

import kr.co.ultari.board.entities.BoardUser;
import kr.co.ultari.board.entities.Content;
import kr.co.ultari.board.entities.ContentUser;
import kr.co.ultari.board.repository.BoardRepository;
import kr.co.ultari.board.repository.ContentRepository;
import kr.co.ultari.board.repository.ContentUserRepository;
import kr.co.ultari.board.repository.mapper.AlarmMapper;
import kr.co.ultari.board.repository.mapper.OrgMapper;
import kr.co.ultari.board.scheduler.AlarmScheduler;
import kr.co.ultari.board.util.StringUtilCustomize;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WriteService {
    @Autowired
    ContentRepository contentRepository;

    @Autowired
    ContentUserRepository contentUserRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    AlarmMapper alarmMapper;

    @Autowired
    OrgMapper orgMapper;

    @Autowired
    AlarmScheduler alarmScheduler;

    @Value("${ultari.board.alarm.use:false}")
    boolean alarmUse;

    @Transactional
    public boolean uploadContent(Content content) {

        if(content.getContentId().equals("")){
            content.setContentId(StringUtilCustomize.getUUID());
            content.setRegistDate(LocalDateTime.now());
            contentRepository.save(content);
            contentUserRepository.save(new ContentUser(content.getBoardId(),content.getContentId(),content.getUserId(),LocalDateTime.now()));

            log.debug("alarmUse : "+alarmUse);
            if(alarmUse && content.getUsePush().equals("1")) {
                saveAlarm(content);
                /*try {
                    alarmScheduler.AlarmLoad();
                } catch (InterruptedException e) {
                    log.error(e+"");
                }*/
            }
        } else {
            contentRepository.updateContentByContentId(content.getContent(), content.getContentId());
        }

        return true;
    }

    public JSONObject getContent(String contentId) {
        JSONObject json = new JSONObject();
        json.put("content",contentRepository.findByContentId(contentId).getContent());
        return json;
    }

    public void saveAlarm(Content content) {
        String boardId = content.getBoardId();
        String contentId = content.getContentId();
        String boardPublic = boardRepository.findByBoardId(boardId).getBoardPublic();

        log.debug("boardId : "+boardId+", contentId : "+contentId +", boardPublic : "+boardPublic);
        if(boardPublic.equals("1")){
            List<Map<String,String>> userList = orgMapper.getAllUserInfo();
            for(Map<String,String> user:userList){
                String userId = user.get("userId");
                log.debug(boardId+", "+contentId+", "+userId);
                alarmMapper.saveAlarm(boardId, contentId, userId);
            }
        }else{
            List<BoardUser> boardList = boardRepository.findByBoardId(boardId).getBoardUser();
            for(BoardUser boardUser:boardList) {
                String userId = boardUser.getUserId();
                log.debug(boardId+", "+contentId+", "+userId);
                alarmMapper.saveAlarm(boardId, contentId, userId);
            }
        }
    }
}
