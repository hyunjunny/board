package kr.co.ultari.board.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AlarmMapper {
    void saveAlarm(@Param("boardId") String boardId, @Param("contentId") String contentId, @Param("userId") String userId);
    void updateCountAlarm(@Param("contentId") String contentId, @Param("userId") String userId);

    List<Map<String, String>> selectAlarm(@Param("maxCount") int maxCount, @Param("intervalMin") int intervalMin);

    void updateSendYN(@Param("flag") String flag, @Param("contentId") String contentId, @Param("userId") String userId);

    void removeAlarm(String contentId);

    void deleteCron(String date);

    Map<String, String> selectUUID(String userId);
}
