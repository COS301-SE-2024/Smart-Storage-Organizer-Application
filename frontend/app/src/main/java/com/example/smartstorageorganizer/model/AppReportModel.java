package com.example.smartstorageorganizer.model;

public class AppReportModel {
    String pageTitle;
    String views;
    String activeUsers;
    String viewsPerActiveUser;
    String AverageEngagementTimePerActiveUser;
    String eventCount;

    public AppReportModel(String pageTitle, String views, String activeUsers, String viewsPerActiveUser, String averageEngagementTimePerActiveUser, String eventCount) {
        this.pageTitle = pageTitle;
        this.views = views;
        this.activeUsers = activeUsers;
        this.viewsPerActiveUser = viewsPerActiveUser;
        AverageEngagementTimePerActiveUser = averageEngagementTimePerActiveUser;
        this.eventCount = eventCount;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(String activeUsers) {
        this.activeUsers = activeUsers;
    }

    public String getViewsPerActiveUser() {
        return viewsPerActiveUser;
    }

    public void setViewsPerActiveUser(String viewsPerActiveUser) {
        this.viewsPerActiveUser = viewsPerActiveUser;
    }

    public String getAverageEngagementTimePerActiveUser() {
        return AverageEngagementTimePerActiveUser;
    }

    public void setAverageEngagementTimePerActiveUser(String averageEngagementTimePerActiveUser) {
        AverageEngagementTimePerActiveUser = averageEngagementTimePerActiveUser;
    }

    public String getEventCount() {
        return eventCount;
    }

    public void setEventCount(String eventCount) {
        this.eventCount = eventCount;
    }
}
