package greencity.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reply_to_comment")
public class ReplyToComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 8000, nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isEdited = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReplyToComment that = (ReplyToComment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
