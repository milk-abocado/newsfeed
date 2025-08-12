package com.example.newsfeed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public class PasswordResetVerifyRequestDto {

        @NotBlank(message = "인증코드를 입력해주세요.")
        @Pattern(regexp = "\\d{6}", message = "인증코드는 6자리 숫자여야 합니다.")
        private String code;
    }

