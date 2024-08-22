package kr.co.ultari.board.repository;

import kr.co.ultari.board.entities.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, String> {
    void deleteByBoardId(String boardId);

    Board findByBoardId(String boardId);
}
