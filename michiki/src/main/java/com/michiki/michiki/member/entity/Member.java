package com.michiki.michiki.member.entity;

import com.michiki.michiki.pivot.entity.MemberPlan;
import com.michiki.michiki.place.entity.Place;
import com.michiki.michiki.member.type.SocialType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@SequenceGenerator(
        name = "members_seq_gen",
        sequenceName = "MEMBERS_SEQ",
        allocationSize = 1,
        initialValue = 1
)
@Table(name = "MEMBERS")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder

// 소셜 로그인 기반 유저 정보를 나타내는 Entity
public class Member {

    // 유저 고유 ID
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "members_seq_gen")
    @Column(name = "MEMBER_ID", updatable = false, nullable = false)
    private Long memberId;

    // 소셜 로그인 제공자의 유저 식별자
    @Column(name = "SOCIAL_ID", length = 255, nullable = false)
    private String socialId;

    // 로그인 소셜 타입
    @Enumerated(EnumType.STRING)
    @Column(name = "SOCIAL_TYPE", length = 20, nullable = false)
    private SocialType socialType;

    // 유저 닉네임
    @Column(name = "NICKNAME", length = 64, nullable = false)
    private String nickname;

    // 유저 이메일
    @Column(name = "EMAIL", length = 255, nullable = false)
    private String email;

    // 프로필 이미지
    @Column(name = "PROFILE_IMAGE", length = 255)
    private String profileImage;

    // 가입 일시
    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // 유저가 추가한 장소 목록
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Place> places = new ArrayList<>();

    // 유저가 참여중인 계획 목록
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberPlan> plans;
}
