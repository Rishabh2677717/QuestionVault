package com.cracksemester.backend.service;

import java.util.List;
import java.util.Map;

public interface AdminDashboardService {
    Map<String, Object> getDashboardStats();
    Map<String, Object> getContentHealth();
    List<Map<String, Object>> getContentGaps();
    List<Map<String, Object>> getBrokenFiles();
    List<Map<String, Object>> getDuplicates();
    Map<String, Object> getSearchAnalytics();
    List<Map<String, Object>> getActivityLogs();
    List<Map<String, Object>> getReports();
    void resolveReport(String reportId);
}
