package greencity.mapping;

public interface GenericCommentReturnMapper<V, T> {
    T toEntity(V dto);
    V toDto(T entity);
}
