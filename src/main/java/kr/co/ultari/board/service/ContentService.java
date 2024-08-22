package kr.co.ultari.board.service;

import kr.co.ultari.board.entities.Board;
import kr.co.ultari.board.entities.Content;
import kr.co.ultari.board.repository.BoardRepository;
import kr.co.ultari.board.repository.ContentRepository;
import kr.co.ultari.board.repository.mapper.OrgMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ContentService {
    @Autowired
    BoardRepository boardRepository;

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    OrgMapper orgMapper;

    public Content getContent(String boardId, String contentId, String userId) {
        return contentRepository.findByBoardIdAndContentIdAndUserId(boardId,contentId,userId);
    }

    public JSONArray getContentUserOrganization(String deptId, String contentId) {
        JSONArray arr = new JSONArray();
        List<Map<String,String>> deptList = orgMapper.subOrganizationDept(deptId);
        for(Map<String,String> deptMap:deptList){
            JSONObject json = new JSONObject();
            json.put("deptId",deptMap.get("deptId"));
            json.put("deptHigh",deptMap.get("deptHigh"));
            json.put("deptName",deptMap.get("deptName"));
            json.put("deptOrder",deptMap.get("deptOrder"));
            json.put("type","dept");
            json.put("children",getContentUserOrganization(deptMap.get("deptId"),contentId));
            if(json.getJSONArray("children").isEmpty()) {
                json.remove("children");
            }else arr.put(json);
        }
        List<Map<String,String>> userList = orgMapper.subContentUserOrganization(deptId,contentId);
        for(Map<String,String> userMap:userList){
            JSONObject json = new JSONObject();
            json.put("userId",userMap.get("userId"));
            json.put("deptId",userMap.get("deptId"));
            json.put("userName",userMap.get("userName"));
            json.put("posName",userMap.get("posName"));
            json.put("userOrder",userMap.get("userOrder"));
            json.put("type","user");
            arr.put(json);
        }
        return arr;
    }

    public JSONArray getReadUserList(String boardId, String contentId, String deptId) {
        Board board = boardRepository.findByBoardId(boardId);
        if(board.getBoardPublic().equals("1")) boardId = null;
        List<Map<String,String>> userList = orgMapper.getReadUserList(boardId, contentId, deptId);
        JSONArray arr = new JSONArray();
        for(Map<String, String> map : userList){
            JSONObject json = new JSONObject();
            for(Map.Entry<String, String> entry : map.entrySet()){
                String key = entry.getKey();
                String value = entry.getValue();
                json.put(key, value);
            }
            arr.put(json);
        }
        return arr;
    }

    public JSONArray getOrgDepthList(String userHigh) {
        List<Map<String,String>> userList = orgMapper.getOrgDepth(userHigh);
        JSONArray arr = new JSONArray();
        for(Map<String, String> map : userList){
            JSONObject json = new JSONObject();
            for(Map.Entry<String, String> entry : map.entrySet()){
                String key = entry.getKey();
                String value = entry.getValue();
                json.put(key, value);
            }
            arr.put(json);
        }
        return arr;
    }

    public JSONArray getReadDeptList(String userHigh) {
        List<Map<String,String>> userList = orgMapper.getReadDeptList(userHigh);
        JSONArray arr = new JSONArray();
        for(Map<String, String> map : userList){
            JSONObject json = new JSONObject();
            for(Map.Entry<String, String> entry : map.entrySet()){
                String key = entry.getKey();
                String value = entry.getValue();
                json.put(key, value);
            }
            arr.put(json);
        }
        return arr;
    }

    public String getReadUserCount(String boardId, String contentId) {
        String total = "";
        String count = "";
        List<Map<String,String>> list = orgMapper.getRecvCount(boardId, contentId);

        log.debug(list.toString());
        for(Map<String,String> map : list) {
            if(map.get("countName").equals("total")) total = String.valueOf(map.get("count"));
            else if(map.get("countName").equals("read")) count = String.valueOf(map.get("count"));
        }

        return count + "/" + total;
    }
}
