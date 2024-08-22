package kr.co.ultari.board.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MSG_CONTENT_USER")
@IdClass(ContentUserId.class)
public class ContentUser implements Serializable {

    private static final long serialVersionUID = 2379826561995940704L;

    @Id
    @Column(name = "BOARD_ID")
    private String boardId;

    @Id
    @Column(name = "CONTENT_ID")
    private String contentId;

    @Id
    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "REGIST_DATE")
    private LocalDateTime registDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(value = {
            @JoinColumn(name = "CONTENT_ID", updatable = false, insertable = false),
            @JoinColumn(name = "BOARD_ID", updatable = false, insertable = false)
    })
    private Content content;

    public ContentUser(String boardId, String contentId, String userId, LocalDateTime now) {
        this.boardId = boardId;
        this.contentId = contentId;
        this.userId = userId;
        this.registDate = now;
    }
}
