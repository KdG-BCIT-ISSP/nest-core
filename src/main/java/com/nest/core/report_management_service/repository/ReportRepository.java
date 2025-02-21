package com.nest.core.report_management_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.nest.core.report_management_service.model.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT r FROM Report r WHERE r.post.id = :postId AND r.post.type = 'ARTICLE'")
    List<Report> findAllArticleReports(@Param("postId") Long articleId);

    @Query("SELECT r FROM Report r WHERE r.post.id = :postId AND r.post.type = 'USERPOST'")
    List<Report> findAllPostReports(@Param("postId") Long postId);
}
