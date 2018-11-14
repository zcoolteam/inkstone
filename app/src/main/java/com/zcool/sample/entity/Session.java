package com.zcool.sample.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Session implements Parcelable {

    public long lastModify;
    public String token;
    public User user;

    public Session() {
    }

    protected Session(Parcel in) {
        lastModify = in.readLong();
        token = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(lastModify);
        dest.writeString(token);
        dest.writeParcelable(user, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Session> CREATOR = new Creator<Session>() {
        @Override
        public Session createFromParcel(Parcel in) {
            return new Session(in);
        }

        @Override
        public Session[] newArray(int size) {
            return new Session[size];
        }
    };

}
