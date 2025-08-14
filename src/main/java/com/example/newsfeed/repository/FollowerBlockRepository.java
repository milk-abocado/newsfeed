package com.example.newsfeed.repository;

import com.example.newsfeed.dto.BlockResponseDto;
import com.example.newsfeed.entity.BlockedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowerBlockRepository extends JpaRepository<BlockedUser, Long> {
    /**
     * 특정 사용자가 다른 사용자를 차단했는지 여부 확인
     *
     * @param userId       차단한 사용자 ID
     * @param targetUserId 차단당한 사용자 ID
     * @return true = 차단 상태, false = 차단 아님
     */
    boolean existsByUserIdAndTargetUserId(Long userId, Long targetUserId);

    /**
     * 특정 사용자가 다른 사용자를 차단한 엔티티 조회
     *
     * @param userId       차단한 사용자 ID
     * @param targetUserId 차단당한 사용자 ID
     * @return 차단 엔티티 Optional
     */
    Optional<BlockedUser> findByUserIdAndTargetUserId(Long userId, Long targetUserId);

    /**
     * 로그인 사용자가 차단한 사용자 목록 조회
     * - BlockResponseDto(사용자 ID, 닉네임) 형태로 반환
     *
     * @param userId 차단한 사용자 ID
     * @return 차단당한 사용자 정보 리스트
     */
    @Query("""
           SELECT new com.example.newsfeed.dto.BlockResponseDto(
               u.id,
               u.nickname
           )
           FROM BlockedUser b
             JOIN Users u ON u.id = b.targetUserId
           WHERE b.userId = :userId
           """)
    List<BlockResponseDto> findBlockedUsersByUserId(@Param("userId") Long userId);

    /**
     * 로그인 사용자가 차단한 사용자들의 ID 목록 조회
     *
     * @param userId 차단한 사용자 ID
     * @return 차단당한 사용자 ID 리스트
     */
    @Query("select b.targetUserId from BlockedUser b where b.userId = :userId")
    List<Long> findTargetIdsBlockedBy(@Param("userId") Long userId);

    /**
     * 특정 사용자를 차단한 사용자들의 ID 목록 조회
     *
     * @param userId 차단당한 사용자 ID
     * @return 해당 사용자를 차단한 사용자 ID 리스트
     */
    @Query("select b.userId from BlockedUser b where b.targetUserId = :userId")
    List<Long> findUsersWhoBlocked(@Param("userId") Long userId);

    /**
     * 양방향 차단 여부 확인
     * - a가 b를 차단했거나, b가 a를 차단했으면 true
     */
    default boolean existsEitherDirection(Long a, Long b) {
        return existsByUserIdAndTargetUserId(a, b)
                || existsByUserIdAndTargetUserId(b, a);
    }

    /**
     * 단일 쿼리로 양방향 차단 여부 확인하고 싶을 때 사용
     * - 성능/로그 편의를 위해 제공
     */
    @Query("""
        select (count(b) > 0) from BlockedUser b
        where (b.userId = :a and b.targetUserId = :b)
           or (b.userId = :b and b.targetUserId = :a)
    """)
    boolean existsEitherDirectionQuery(@Param("a") Long a, @Param("b") Long b);

    /**
     * (편의) 특정 차단 레코드 삭제
     */
    void deleteByUserIdAndTargetUserId(Long userId, Long targetUserId);
}
