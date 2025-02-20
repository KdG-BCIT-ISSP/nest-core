package com.nest.core.report_management_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nest.core.report_management_service.model.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
