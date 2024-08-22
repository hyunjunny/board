package kr.co.ultari.board.repository;

import kr.co.ultari.board.entities.Content;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, String> {
    List<Content> findByBoardIdOrderByRegistDateDesc(String boardId, Pageable pageable);

    void deleteByContentId(String contentId);

    Content findByBoardIdAndContentIdAndUserId(String boardId, String contentId, String userId);

    Content findByContentId(String contentId);

    @Modifying
    @Query(value = "UPDATE msg_content SET content = :content WHERE content_id = :contentId",nativeQuery = true)
    void updateContentByContentId(@Param("content")String content, @Param("contentId")String contentId);
}
