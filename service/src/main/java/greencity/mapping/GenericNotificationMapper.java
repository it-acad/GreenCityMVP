package greencity.mapping;

public interface GenericNotificationMapper<V, T> {
    V toEntity(T dto);
    T toDto(V entity);
}
