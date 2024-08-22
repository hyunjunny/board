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
public class ContentId implements Serializable {
    @EqualsAndHashCode.Include
    @Id
    @Column(name = "BOARD_ID")
    private String boardId;

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "CONTENT_ID")
    private String contentId;

    public ContentId(String boardId, String contentId) {
        this.boardId = boardId;
        this.contentId = contentId;
    }
}
