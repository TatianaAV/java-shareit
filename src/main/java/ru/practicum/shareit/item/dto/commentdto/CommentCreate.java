package ru.practicum.shareit.item.dto.commentdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreate {

    @NotBlank(message = "Комментарий не может быть пустым")
    private String text;
}
