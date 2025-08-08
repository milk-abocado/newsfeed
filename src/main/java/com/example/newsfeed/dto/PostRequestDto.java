package com.example.newsfeed.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostRequestDto {

    // 게시물 본문 내용 (텍스트)
    private String content;

    // 추가할 이미지 URL 리스트 (최대 3장까지 허용)
    @JsonProperty("addImageUrl")  // 클라이언트 요청에서 "addImageUrl" 키로 매핑
    @Size(max = 3, message = "이미지는 최대 3장까지만 업로드할 수 있습니다.")
    private List<String> imageUrlList;

    // 유효성 검사 헬퍼 메서드
    // content, imageUrlList, deleteImageIdList 모두 비어 있는 경우 true 반환
    // 하나라도 채워져 있으면 false → 유효한 요청으로 간주
    public boolean isAllEmpty() {
        return (content == null || content.trim().isEmpty())   // 공백만 입력도 비어 있다고 간주
                && (imageUrlList == null || imageUrlList.isEmpty());
    }
}
