package kr.co.ultari.board.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MSG_CONTENT")
@IdClass(ContentId.class)
public class Content implements Serializable {

    @Id
    @Column(name = "CONTENT_ID")
    private String contentId;

    @Id
    @Column(name = "BOARD_ID")
    private String boardId;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "CONTENT", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "USE_PUSH", columnDefinition = "CHAR", length=1)
    private String usePush;

    @Column(name = "REGIST_DATE")
    private LocalDateTime registDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID", insertable = false, updatable = false)
    private Board board;

    @OneToMany(mappedBy = "content", orphanRemoval = true)
    private List<ContentUser> contentUser;

    @OneToMany(mappedBy = "content", orphanRemoval = true)
    private List<ContentReply> contentReply;
}
