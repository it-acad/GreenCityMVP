package greencity.mapping;

public interface GenericNotificationResponseDtoMapper<E,D> {
    D toDto(E entity);
    E toEntity(D dto);
}
