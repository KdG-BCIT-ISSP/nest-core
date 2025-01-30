package com.nest.core.member_management_service.repository;

import com.nest.core.member_management_service.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
}
