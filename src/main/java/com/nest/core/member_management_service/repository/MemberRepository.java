package com.nest.core.member_management_service.repository;

import com.nest.core.member_management_service.model.Member;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    Member findByEmail(String email);

    @Query("SELECT m FROM Member m WHERE m.region = :region")
    List<Member> findAllByRegion(String region);
}
