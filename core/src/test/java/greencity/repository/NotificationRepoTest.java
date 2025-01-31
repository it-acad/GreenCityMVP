package greencity.repository;

import greencity.GreenCityApplication;
import greencity.IntegrationTestBase;
import greencity.entity.Notification;
import greencity.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static greencity.ModelUtils.getNotification;
import static greencity.ModelUtils.getUser;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GreenCityApplication.class)
public class NotificationRepoTest extends IntegrationTestBase {

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private UserRepo userRepo;

    private User user;
    private Notification notification1;
    private Notification notification2;
    private Notification notification3;
    private Notification notification4;

    @BeforeEach
    public void setUp() {
        user = getUser();
        user.setRefreshTokenKey(UUID.randomUUID().toString());
        userRepo.save(user);

        notification1 = getNotification();
        notification1.setReceivedTime(LocalDateTime.now().minusDays(1));
        notificationRepo.save(notification1);

        notification2 = getNotification();
        notification2.setReceivedTime(LocalDateTime.now().minusDays(2));
        notification2.setRead(true);
        notificationRepo.save(notification2);

        notification3 = getNotification();
        notificationRepo.save(notification3);

        notification4 = getNotification();
        notification4.setReceivedTime(LocalDateTime.now().minusDays(3));
        notification4.setRead(true);
        notificationRepo.save(notification4);
    }

    @Test
    public void testFindAllByUserId() {
        List<Notification> notifications = notificationRepo.findAllByUserId(user.getId());
        assertThat(notifications).hasSize(4).contains(notification1, notification2, notification3, notification4);
    }

    @Test
    public void testFindAllByUserId_NoNotifications() {
        List<Notification> notifications = notificationRepo.findAllByUserId(user.getId() + 1);
        assertThat(notifications).isEmpty();
    }

    @Test
    public void testFindAllByUserIdAndIsReadFalse() {
        List<Notification> notifications = notificationRepo.findAllByUserIdAndIsReadFalse(user.getId());
        assertThat(notifications).hasSize(2).contains(notification1, notification3);
    }

    @Test
    public void testFindAllByUserIdAndIsReadFalse_NoUnreadNotifications() {
        notification1.setRead(true);
        notificationRepo.save(notification1);
        List<Notification> notifications = notificationRepo.findAllByUserIdAndIsReadFalse(user.getId());
        assertThat(notifications).containsOnly(notification3);
    }

    @Test
    public void testFindFirstThreeByUserIdOrderByReceivedTimeDesc() {
        List<Notification> notifications = notificationRepo.findFirstThreeByUserIdOrderByReceivedTimeDesc(user.getId());
        assertThat(notifications).hasSize(3).containsExactly(notification3, notification1, notification2);
    }

    @Test
    public void testFindFirstThreeByUserIdOrderByReceivedTimeDesc_LessThanThreeNotifications() {
        notificationRepo.deleteAll();
        Notification newNotification = getNotification();
        newNotification.setUser(user);
        newNotification.setReceivedTime(LocalDateTime.now());
        notificationRepo.save(newNotification);

        List<Notification> notifications = notificationRepo.findFirstThreeByUserIdOrderByReceivedTimeDesc(user.getId());
        assertThat(notifications).hasSize(1).containsExactly(newNotification);
    }

    @Test
    public void testFindAllByUserIdOrderByReceivedTimeDesc() {
        List<Notification> notifications = notificationRepo.findAllByUserIdOrderByReceivedTimeDesc(user.getId());
        assertThat(notifications).hasSize(4).containsExactly(notification3, notification1, notification2, notification4);
    }

    @Test
    public void testFindAllByUserIdOrderByReceivedTimeDesc_NoNotifications() {
        List<Notification> notifications = notificationRepo.findAllByUserIdOrderByReceivedTimeDesc(user.getId() + 1);
        assertThat(notifications).isEmpty();
    }
}


