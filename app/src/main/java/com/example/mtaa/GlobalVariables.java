package com.example.mtaa;

import android.app.Application;

public class GlobalVariables extends Application {

    private String userId;

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

}
