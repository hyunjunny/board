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
@Table(name = "MSG_CONTENT_REPLY")
@IdClass(ContentReplyId.class)
public class ContentReply implements Serializable {
    @Id
    @Column(name = "REPLY_ID")
    private String replyId;

    @Id
    @Column(name = "BOARD_ID")
    private String boardId;

    @Id
    @Column(name = "CONTENT_ID")
    private String contentId;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "REPLY", columnDefinition = "LONGTEXT")
    private String reply;

    @Column(name = "REGIST_DATE")
    private LocalDateTime registDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(value = {
            @JoinColumn(name = "CONTENT_ID", updatable = false, insertable = false),
            @JoinColumn(name = "BOARD_ID", updatable = false, insertable = false)
    })
    private Content content;
}
