package kr.co.ultari.board.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MSG_CONTENT_ATTACH")
public class ContentAttach implements Serializable {
    @Id
    @Column(name = "CONTENT_ID")
    private String contentId;

    @Id
    @Column(name = "ATTACH_ID")
    private String attachId;

    @Column(name = "ATTACH_NAME")
    private String attachName;

    @Column(name = "ATTACH_PATH")
    private String attachPath;

    @Column(name = "ATTACH_LENGTH")
    private String attachLength;

    @Column(name = "REGIST_DATE")
    private LocalDateTime registDate;
}
