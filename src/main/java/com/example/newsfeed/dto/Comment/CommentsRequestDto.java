package com.example.newsfeed.dto.Comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentsRequestDto {
    @NotBlank(message = "내용은 비어 있을 수 없습니다.")
    private String content;
}
