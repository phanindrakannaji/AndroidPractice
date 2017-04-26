package com.example.android.friendfinder;

/**
 * Created by phani on 4/13/17.
 */

class Friend {

    private String email;
    private String fullName;
    private Double latitude;
    private Double longitude;
    private String timestamp;

    Friend(String email, String fullName, Double latitude, Double longitude, String timestamp) {
        this.email = email;
        this.fullName = fullName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
