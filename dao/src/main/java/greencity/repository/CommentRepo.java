package greencity.repository;

import greencity.entity.Comment;
import greencity.entity.ReplyToComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepo extends JpaRepository<Comment, Long> {
    @Query("SELECT r FROM ReplyToComment r WHERE r.comment.id = :commentId")
    List<Comment> findAllByCommentId(Long commentId);
}
