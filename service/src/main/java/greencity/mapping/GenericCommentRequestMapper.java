package greencity.mapping;

public interface GenericCommentRequestMapper <V, T>{
    T toEntity(V dto);
    V toDto(T entity);
}
