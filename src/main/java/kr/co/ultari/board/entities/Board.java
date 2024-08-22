package kr.co.ultari.board.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MSG_BOARD")
public class Board {
    @Id
    @Column(name = "BOARD_ID")
    private String boardId;

    @Column(name = "BOARD_NAME")
    private String boardName;

    @Column(name = "BOARD_USE", columnDefinition = "CHAR", length=1)
    private String boardUse;

    @Column(name = "BOARD_PUBLIC", columnDefinition = "CHAR", length=1)
    private String boardPublic;

    @Column(name = "BOARD_ORDER")
    private int boardOrder;

    @Column(name = "REGIST_DATE")
    private LocalDateTime registDate;

    @OneToMany(mappedBy="board")
    private List<BoardUser> boardUser;

    @OneToMany(mappedBy="board")
    private List<Content> content;
}
