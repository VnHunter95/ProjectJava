package com.example.hunter95.mynewtrackingapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hunter95 on 12/31/2016.
 */
public class UserInfo {
    String username;
    long curAlditude;
    long curLongditude;
    String updateTime;
    ArrayList<String> friendList = new ArrayList<>();
    String id;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public ArrayList<String> getFriendList() {
        return friendList;
    }

    public void setFriendList(ArrayList<String> friendList) {
        this.friendList = friendList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getCurAlditude() {
        return curAlditude;
    }

    public void setCurAlditude(long curAlditude) {
        this.curAlditude = curAlditude;
    }

    public long getCurLongditude() {
        return curLongditude;
    }

    public void setCurLongditude(long curLongditude) {
        this.curLongditude = curLongditude;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
