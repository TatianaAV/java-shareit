package ru.practicum.shareit.item.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.commentdto.CommentCreate;
import ru.practicum.shareit.item.dto.commentdto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, User.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    Comment toComment(User author, Item item, CommentCreate comment);

    @Mapping(target = "authorName", source = "author.name")
    CommentDto toCommentDto(Comment comment);

    List<CommentDto> mapCommentDto(List<Comment> comments);
}
