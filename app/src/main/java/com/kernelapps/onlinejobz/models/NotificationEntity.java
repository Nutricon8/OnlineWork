package com.kernelapps.onlinejobz.models;

import java.io.Serializable;

public class NotificationEntity implements Serializable {
    public int id; // You can still use this manually if needed
    public String title;
    public String message;
    public long timestamp;
    public boolean read;

    // Optional: constructor
    public NotificationEntity() {}

    public NotificationEntity(String title, String message, long timestamp, boolean read) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.read = read;
    }
}
