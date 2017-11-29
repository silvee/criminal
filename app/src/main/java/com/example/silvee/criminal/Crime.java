package com.example.silvee.criminal;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Created by silvee on 22.11.2017.
 */

public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private boolean mRequirePolice;





    // Getters and setters
    public UUID getId() {
        return mId;
    }
    public String getTitle() { return mTitle; }
    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }
    public Date getDate() {
        return mDate;
    }
    public void setDate(Date mDate) { this.mDate = mDate; }
    public boolean isSolved() {
        return mSolved;
    }
    public void setSolved(boolean mSolved) {
        this.mSolved = mSolved;
    }
    public boolean requirePolice() { return mRequirePolice; }
    public void setRequirePolice(boolean mRequirePolice) { this.mRequirePolice = mRequirePolice; }



    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }
}
