package greencity.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CriteriaConfig {
    private final EntityManager entityManager;
    @Bean
    public CriteriaBuilder criteriaBuilder() {
        return entityManager.getCriteriaBuilder();
    }
}
