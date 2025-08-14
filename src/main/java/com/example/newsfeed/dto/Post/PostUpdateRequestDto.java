package com.example.newsfeed.dto.Post;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostUpdateRequestDto {
    // 수정할 게시물 내용
    private String content;

    // 새로 추가할 이미지 URL 목록
    @Size(max = 3, message = "이미지는 최대 3장까지만 업로드할 수 있습니다.")
    private List<String> addImageUrl;

    // 삭제할 이미지 ID 목록
    private List<Long> deleteImageId;

    // 유효성 검사 헬퍼 메서드
    // content, imageUrlList, deleteImageIdList 모두 비어 있는 경우 true 반환
    // 하나라도 채워져 있으면 false → 유효한 요청으로 간주
    public boolean isAllEmpty() {
        return (content == null || content.isBlank())
                && (addImageUrl == null || addImageUrl.isEmpty())
                && (deleteImageId == null || deleteImageId.isEmpty());
    }
}
