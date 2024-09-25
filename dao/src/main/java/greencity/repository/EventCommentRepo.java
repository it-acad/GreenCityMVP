package greencity.repository;

import greencity.entity.EventComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventCommentRepo extends JpaRepository<EventComment, Long> {
    List<EventComment> findByEventIdOrderByCreatedAtDesc(Long eventId);

    List<EventComment> findByParentCommentIdOrderByCreatedAtDesc(Long parentCommentId);
    @Query("SELECT c FROM EventComment c WHERE c.parentComment.id = :commentId")
    List<EventComment> findAllByEventCommentId(Long commentId);

    //Get all event comments by its ID sorted for a given creation
    List<EventComment> findByEventIdOrderByCreatedDateDesc(Long eventId);

    //get all answers under main comment
    List<EventComment> findByParentCommentIdOrderByCreatedDateDesc(Long parentCommentId);

    @Query("SELECT COUNT(c) FROM EventComment c WHERE c.event.id = :eventId")
    Long countByEventId(Long eventId);
}