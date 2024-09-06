package greencity.mapping;

public interface GenericCommentResponseMapper<V, T> {
    T toEntity(V dto);
    V toDto(T entity);
}
