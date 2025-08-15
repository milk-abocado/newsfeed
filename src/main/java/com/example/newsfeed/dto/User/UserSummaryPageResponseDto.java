package com.example.newsfeed.dto.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
// 한글 키로 직렬화하여 이해하기 쉬운 응답 출력
public class UserSummaryPageResponseDto {

    @JsonProperty("목록")
    private List<UserSummaryDto> content;

    @JsonProperty("현재 페이지")
    private int pageNumber;

    @JsonProperty("페이지 크기")
    private int pageSize;

    @JsonProperty("총 페이지 수")
    private int totalPages;

    @JsonProperty("총 요소 수")
    private long totalElements;

    @JsonProperty("첫 페이지 여부")
    private boolean first;

    @JsonProperty("마지막 페이지 여부")
    private boolean last;
}
