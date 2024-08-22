package kr.co.ultari.board.entities;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.io.Serializable;

@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Embeddable
public class BoardUserId implements Serializable {
    @EqualsAndHashCode.Include
    @Id
    @Column(name = "BOARD_ID")
    private String boardId;

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "USER_ID")
    private String userId;

    public BoardUserId(String boardId, String userId) {
        this.boardId = boardId;
        this.userId = userId;
    }
}
