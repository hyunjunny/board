package kr.co.ultari.board.repository;

import kr.co.ultari.board.entities.ContentReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentReplyRepository extends JpaRepository<ContentReply, String> {
    List<ContentReply> findByContentIdOrderByRegistDateDesc(String contentId);

    List<ContentReply> findByContentIdOrderByRegistDateAsc(String contentId);
}
