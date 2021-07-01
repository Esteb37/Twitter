package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity
public class User {

    @ColumnInfo
    @PrimaryKey
    @NonNull
    public String uid;

    @ColumnInfo
    public String name;

    @ColumnInfo
    public String screenName; //handle

    @ColumnInfo
    public int followers;

    @ColumnInfo
    public int following;

    @ColumnInfo
    public String description;

    @ColumnInfo
    public String bannerImageUrl;

    @ColumnInfo
    public String profileImageUrl;

    User(){}

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url_https");
        user.followers = jsonObject.getInt("followers_count");
        user.following = jsonObject.getInt("friends_count");
        user.uid = jsonObject.getString("id_str");
        try {
            user.bannerImageUrl = jsonObject.getString("profile_banner_url");
        }catch(JSONException e){
            Log.d("Exception", String.valueOf(e));
        }

        user.description = jsonObject.getString("description");
        return user;
    }

    public static List<User> fromJsonTweetArray(List<Tweet> tweetList) {
        List<User> users = new ArrayList<>();
        for(int i = 0;i< tweetList.size();i++){
            users.add(tweetList.get(i).user);
        }
        return users;

    }
}
