package kr.co.ultari.board.repository;

import kr.co.ultari.board.entities.ContentUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentUserRepository extends JpaRepository<ContentUser,String> {
}
