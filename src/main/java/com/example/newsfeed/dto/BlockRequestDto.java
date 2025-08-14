package com.example.newsfeed.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockRequestDto {

    @NotNull(message = "targetUserId는 필수입니다.")
    @Positive(message = "targetUserId는 양수여야 합니다.")
    private Long targetUserId;
}
