package greencity.repository;

import greencity.entity.EventComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventCommentRepo extends JpaRepository<EventComment, Long> {
    @Query("SELECT c FROM EventComment c WHERE c.parentComment.id = :commentId")
    List<EventComment> findAllByEventCommentId(Long commentId);
    //Get all event comments by its ID sorted for a given creation
    List<EventComment> findByEventIdOrderByCreatedAtDesc(Long eventId);

    //get all answers under main comment
    List<EventComment> findByParentCommentIdOrderByCreatedAtDesc(Long parentCommentId);

    //count quantity of comments
    @Query("SELECT COUNT(c) FROM EventComment c WHERE c.event.id = :eventId")
    Long countByEventId(Long eventId);
}
