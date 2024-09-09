package greencity.repository;

import greencity.entity.EventComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventCommentRepo extends JpaRepository<EventComment, Long> {
    @Query("SELECT r FROM EventComment r WHERE r.comment.id = :commentId")
    List<EventComment> findAllByEventCommentId(Long commentId);
}