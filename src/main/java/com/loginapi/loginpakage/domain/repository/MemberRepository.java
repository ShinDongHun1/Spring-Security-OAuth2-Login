package com.loginapi.loginpakage.domain.repository;

import com.loginapi.loginpakage.domain.Member;
import com.loginapi.loginpakage.global.oauth2.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findBySocialTypeAndSocialId(SocialType socialType , String socialId);
}
