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
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "members_seq_gen")
    @Column(name = "MEMBER_ID", updatable = false, nullable = false)
    private Long memberId;

    @Column(name = "SOCIAL_ID", length = 255, nullable = false)
    private String socialId;

    @Enumerated(EnumType.STRING)
    @Column(name = "SOCIAL_TYPE", length = 20, nullable = false)
    private SocialType socialType;

    @Column(name = "NICKNAME", length = 64, nullable = false)
    private String nickname;

    @Column(name = "EMAIL", length = 255, nullable = false)
    private String email;

    @Column(name = "PROFILE_IMAGE", length = 255)
    private String profileImage;

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Place> places = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberPlan> plans;
}
