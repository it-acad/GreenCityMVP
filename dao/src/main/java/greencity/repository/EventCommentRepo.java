package greencity.repository;

import greencity.entity.EventComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventCommentRepo extends JpaRepository<EventComment, Long> {
    @Query("SELECT c FROM EventComment c WHERE c.parentComment.id = :commentId")
    List<EventComment> findAllByEventCommentId(Long commentId);
}