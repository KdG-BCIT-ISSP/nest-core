package com.nest.core.notification_service.repository;

import com.nest.core.member_management_service.model.Member;
import com.nest.core.notification_service.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.member.id = :memberId order by n.createdAt desc")
    Page<Notification> findAllByMemberId(Long memberId, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.member.id = :memberId and n.isRead = false")
    List<Notification> existsByMemberIdAndReadFalse(Long memberId);
}
