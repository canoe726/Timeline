package com.example.mytimetable;

import android.app.Application;

public class GlobalVariables extends Application {

    private String user_id = "";
    private String university_url = "";

    public String getGlobalUserId() { return user_id; }
    public void setGlobalUserId(String globalUserId) { this.user_id = globalUserId; }

    public String getGlobalUniversityUrl() { return university_url; }
    public void setGlobalUniversityUrl(String globalUniversity_url) { this.university_url = globalUniversity_url; }
}
