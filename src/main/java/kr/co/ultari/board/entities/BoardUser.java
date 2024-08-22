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
@Table(name = "MSG_BOARD_USER")
@IdClass(BoardUserId.class)
public class BoardUser implements Serializable {

    private static final long serialVersionUID = 2379826561995940704L;

    @Id
    @Column(name = "BOARD_ID")
    private String boardId;

    @Id
    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "REGIST_DATE")
    private LocalDateTime registDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="BOARD_ID", insertable = false, updatable = false)
    private Board board;
}
