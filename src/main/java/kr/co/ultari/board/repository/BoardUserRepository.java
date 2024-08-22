package kr.co.ultari.board.repository;

import kr.co.ultari.board.entities.BoardUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardUserRepository extends JpaRepository<BoardUser, String> {
    List<BoardUser> findByUserId(String userId);

    void deleteByUserId(String userId);
}
