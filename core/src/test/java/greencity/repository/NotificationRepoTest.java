package greencity.repository;

import greencity.entity.Notification;
import greencity.entity.User;
import org.aspectj.weaver.ast.Not;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static greencity.ModelUtils.getNotification;
import static greencity.ModelUtils.getUser;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@ExtendWith(SpringExtension.class)
public class NotificationRepoTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.2")
            .withDatabaseName("greencity")
            .withUsername("postgres")
            .withPassword("1505Duma");

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private UserRepo userRepo;

    private User user;
    private Notification notification1;
    private Notification notification2;
    private Notification notification3;

    @BeforeEach
    public void setUp() {
        User user = getUser();
        userRepo.save(user);
        Notification notification = getNotification();
        notificationRepo.save(notification);
        Notification notification2 = getNotification();
        notificationRepo.save(notification2);
        Notification notification3 = getNotification();
        notificationRepo.save(notification3);
    }

    @Test
    public void testFindAllByUserId() {
        List<Notification> notifications = notificationRepo.findAllByUserId(user.getId());
        assertThat(notifications).hasSize(3).contains(notification1, notification2, notification3);
    }
}
