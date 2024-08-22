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
public class ContentAttachId implements Serializable {
    @EqualsAndHashCode.Include
    @Id
    @Column(name = "BOARD_ID")
    private String contentId;

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "ATTACH_ID")
    private String attachId;

    public ContentAttachId(String contentId, String attachId) {
        this.contentId = contentId;
        this.attachId = attachId;
    }
}
