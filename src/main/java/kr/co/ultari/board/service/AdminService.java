package kr.co.ultari.board.service;

import kr.co.ultari.board.entities.Board;
import kr.co.ultari.board.entities.BoardUser;
import kr.co.ultari.board.repository.BoardRepository;
import kr.co.ultari.board.repository.BoardUserRepository;
import kr.co.ultari.board.repository.mapper.OrgMapper;
import kr.co.ultari.board.util.StringUtilCustomize;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    BoardUserRepository boardUserRepository;

    @Autowired
    OrgMapper orgMapper;

    public JSONArray getBoardList() {
        JSONArray arr = new JSONArray();
        List<Board> list = boardRepository.findAll(Sort.by(Sort.Direction.ASC,"boardOrder"));

        for(Board b:list) {
            JSONObject json = new JSONObject();
            json.put("boardId",b.getBoardId());
            json.put("boardName",b.getBoardName());
            json.put("boardPublic",b.getBoardPublic());
            json.put("boardUse",b.getBoardUse());
            json.put("boardOrder",b.getBoardOrder());
            json.put("registDate", StringUtilCustomize.castNowDate(b.getRegistDate()));

            arr.put(json);
        }

        return arr;
    }

    public Boolean AddBoard(Board dto) {
        dto.setBoardId(StringUtilCustomize.getUUID());
        dto.setRegistDate(LocalDateTime.now());
        boardRepository.save(dto);
        return true;
    }

    public Boolean ModBoard(Board dto) {
        dto.setRegistDate(LocalDateTime.now());
        boardRepository.save(dto);
        return true;
    }

    @Transactional
    public boolean DelBoard(String boardId) {
        boardRepository.deleteByBoardId(boardId);
        return true;
    }

    public boolean setBoardUser(String userId) {
        return true;
    }

    public JSONArray getBoardUserList(String boardId) {
        JSONArray arr = new JSONArray();

        Map<Map<String,String>, ArrayList<Map<String,String>>> map = new HashMap<>();
        ArrayList<Map<String,String>> deptList = new ArrayList<>();

        List<Map<String,String>> list = orgMapper.getUserInfoListByBoardId(boardId);
        for(Map<String,String> user:list) {
            Map<String,String> deptMap = new HashMap<>();
            deptMap.put("deptId",user.get("userHigh"));
            deptMap.put("deptName",user.get("deptName"));

            if(map.containsKey(deptMap)){
                map.get(deptMap).add(user);
            }else {
                ArrayList<Map<String,String>> arrList = new ArrayList<>();
                arrList.add(user);
                map.put(deptMap, arrList);
            }

            if(!deptList.contains(deptMap))
                deptList.add(deptMap);
        }

        for(Map<String,String> dept:deptList) {
            JSONObject deptJson = new JSONObject();
            JSONArray childrenUserList = new JSONArray();
            for(Map<String,String> user:map.get(dept)){
                JSONObject json = new JSONObject();
                json.put("title",user.get("userName")+" "+user.get("posName"));
                json.put("key",user.get("userId"));
                json.put("type","user");
                json.put("icon",false);
                childrenUserList.put(json);
            }
            deptJson.put("title",dept.get("deptName"));
            deptJson.put("key",dept.get("deptId"));
            deptJson.put("type","dept");
            deptJson.put("isFolder",true);
            //deptJson.put("hideCheckbox",true);
            deptJson.put("isLazy",true);
            deptJson.put("children",childrenUserList);

            arr.put(deptJson);
        }

        return arr;
    }

    public JSONArray getSubOrganization(String deptId) {
        JSONArray arr = new JSONArray();
        List<Map<String,String>> deptList = orgMapper.subOrganizationDept(deptId);
        for(Map<String,String> dept:deptList){
            JSONObject json = new JSONObject();
            json.put("title",dept.get("deptName"));
            json.put("key",dept.get("deptId"));
            json.put("type","dept");
            json.put("isFolder",true);
            //json.put("hideCheckbox",true);
            json.put("isLazy",true);
            arr.put(json);
        }

        List<Map<String,String>> userList = orgMapper.subOrganizationUser(deptId);
        for(Map<String,String> user:userList){
            JSONObject json = new JSONObject();
            json.put("title",user.get("userName")+" "+user.get("posName"));
            json.put("key",user.get("userId"));
            json.put("type","user");
            json.put("icon",false);
            arr.put(json);
        }

        return arr;
    }

    public boolean addAuthUser(String userList,String boardId) {
        String[] list = userList.split(",");

        for(String userId:list) {
            BoardUser boardUser = new BoardUser();
            boardUser.setUserId(userId);
            boardUser.setBoardId(boardId);
            boardUser.setRegistDate(LocalDateTime.now());

            boardUserRepository.save(boardUser);
        }

        return true;
    }

    @Transactional
    public boolean delAuthUser(String userList, String boardId) {
        String[] list = userList.split(",");
        for(String userId:list) {
            boardUserRepository.deleteByUserId(userId);
        }

        return true;
    }

    public JSONArray getFullOrganization(String deptId) {
        JSONArray arr = new JSONArray();

        List<Map<String,String>> deptList = orgMapper.subOrganizationDept(deptId);
        for(Map<String,String> deptMap:deptList){
            JSONObject json = new JSONObject();
            json.put("title",deptMap.get("deptName"));
            json.put("key",deptMap.get("deptId"));
            json.put("type","dept");
            json.put("isFolder",true);
            //json.put("hideCheckbox",true);
            json.put("isLazy",true);
            json.put("children",getFullOrganization(deptMap.get("deptId")));
            if(json.getJSONArray("children").isEmpty()) {
                json.remove("children");
                json.put("isLazy",false);
            }
            arr.put(json);
        }
        List<Map<String,String>> userList = orgMapper.subOrganizationUser(deptId);
        for(Map<String,String> userMap:userList){
            JSONObject json = new JSONObject();
            json.put("title",userMap.get("userName")+" "+userMap.get("posName"));
            json.put("key",userMap.get("userId"));
            json.put("type","user");
            json.put("icon",false);
            arr.put(json);
        }
        System.out.println("arr : "+arr.toString());
        return arr;
    }
}
