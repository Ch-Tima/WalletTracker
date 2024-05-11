package com.chtima.wallettracker.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(defaultValue = "guest")
    public String firstname;
    @ColumnInfo(defaultValue = "")
    public String lastname;

    @ColumnInfo(defaultValue = "0.0")
    public double balance;

    @ColumnInfo(defaultValue = "USD")
    public String currency;


    public User(String firstname, String lastname, double balance, String currency) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.balance = balance;
        this.currency = currency;
    }

    protected User(Parcel in) {
        id = in.readLong();
        firstname = in.readString();
        lastname = in.readString();
        balance = in.readDouble();
        currency = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(firstname);
        dest.writeString(lastname);
        dest.writeDouble(balance);
        dest.writeString(currency);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
