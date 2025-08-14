package com.example.newsfeed.dto.Password;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public class PasswordResetVerifyRequestDto {

        @NotBlank(message = "인증 코드를 입력해 주세요.")
        @Pattern(regexp = "\\d{6}", message = "인증 코드는 6자리 숫자여야 합니다.")
        private String code;
    }

