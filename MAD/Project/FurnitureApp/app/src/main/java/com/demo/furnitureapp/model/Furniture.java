package com.demo.furnitureapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Furniture implements Parcelable {

    private String email;
    private String title;
    private String description;
    private String url;
    private String time;
    private String furnitureId;

    public Furniture() {
        // Default constructor required for Firebase
    }

    public Furniture(String email, String title, String description, String url, String time, String furnitureId) {
        this.email = email;
        this.title = title;
        this.description = description;
        this.url = url;
        this.time = time;
        this.furnitureId = furnitureId;
    }

    protected Furniture(Parcel in) {
        email = in.readString();
        title = in.readString();
        description = in.readString();
        url = in.readString();
        time = in.readString();
        furnitureId = in.readString();
    }

    public static final Creator<Furniture> CREATOR = new Creator<Furniture>() {
        @Override
        public Furniture createFromParcel(Parcel in) {
            return new Furniture(in);
        }

        @Override
        public Furniture[] newArray(int size) {
            return new Furniture[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFurnitureId() {
        return furnitureId;
    }

    public void setFurnitureId(String furnitureId) {
        this.furnitureId = furnitureId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeString(time);
        dest.writeString(furnitureId);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
