package kr.co.ultari.board.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrgMapper {
    Map<String, String> getUserInfo(String userId);
    Map<String, String> checkLogin(String userId);
    List<Map<String,String>> getUserInfoListByBoardId(String boardId);

    List<Map<String,String>> subOrganizationDept(String deptId);
    List<Map<String,String>> subOrganizationUser(String deptId);

    List<Map<String, String>> subContentUserOrganization(@Param("deptId") String deptId, @Param("contentId")  String contentId);

    List<Map<String, String>> getAllUserInfo();

    List<Map<String, String>> getReadUserList(@Param("boardId") String boardId,@Param("contentId") String contentId,@Param("deptId") String deptId);

    List<Map<String, String>> getOrgDepth(String userHigh);

    List<Map<String, String>> getReadDeptList(String deptId);

    List<Map<String,String>> getRecvCount(@Param("boardId") String boardId, @Param("contentId") String contentId);
}
