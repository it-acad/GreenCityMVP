package greencity.mapping;

public interface GenericCommentMapper<V, T>{
    T toEntity(V dto);
    V toDto(T entity);
}
