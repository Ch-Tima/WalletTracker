package com.chtima.wallettracker.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

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

    public static User empty(){
        return new User("", "", 0.0, "");
    }

    public static boolean isEmpty(User user){
        return  user.firstname.isEmpty() &&  user.lastname.isEmpty() && user.currency.isEmpty();
    }

    public void addToBalance(double val){
        balance = balance + val;
    }

    public void deductFromBalance(double val){
        balance = balance - val;
    }
}
