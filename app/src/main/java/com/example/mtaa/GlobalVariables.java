package com.example.mtaa;

import android.app.Application;

public class GlobalVariables extends Application {

    private Number userId;

    public Number getUserId() {
        return userId;
    }

    public void setUserId(Number userId) {
        this.userId = userId;
    }
}
