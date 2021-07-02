package com.codepath.apps.restclienttemplate.models;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/*
    Class that handles Twitter User objects
 */
@Parcel
@Entity
public class User {

    //Member variables set for database querying

    //Unique user id
    @ColumnInfo
    @PrimaryKey
    @NonNull
    public String uid;

    //Chosen name
    @ColumnInfo
    public String name;

    //Handle
    @ColumnInfo
    public String screenName;

    //Amount of followers
    @ColumnInfo
    public int followers;

    //Amount of users the user follows
    @ColumnInfo
    public int following;

    //Chosen bio
    @ColumnInfo
    public String description;

    //URL to the user's profile banner
    @ColumnInfo
    public String bannerImageUrl;

    //URL to the user's profile image
    @ColumnInfo
    public String profileImageUrl;

    /*
        Default constructor

        @param none

        @return none
     */
    User(){}

    /*
        Creates a user object from the information in a JSON

        @param jsonObject - The object with user information

        @return User - The generated user
     */
    public static User fromJson(JSONObject jsonObject) throws JSONException {

        //Create a new user
        User user = new User();

        //Set the user's information from the JSON
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.followers = jsonObject.getInt("followers_count");
        user.following = jsonObject.getInt("friends_count");
        user.uid = jsonObject.getString("id_str");
        user.description = jsonObject.getString("description");

        //Prepare in case the user has not selected a profile image
        try{
            user.profileImageUrl = jsonObject.getString("profile_image_url_https");
        }catch(JSONException e){
            user.profileImageUrl = "";
        }

        //Prepare in case the user has not selected a banner
        try {
            user.bannerImageUrl = jsonObject.getString("profile_banner_url");
        }catch(JSONException e){
            user.bannerImageUrl = "";
        }

        //Return the generated user
        return user;
    }

    /*
        Creates a list of all publishing users in an array of tweets

        @param tweetList - The list of tweets to parse

        @return List<User> - The list of users present in the tweet list
     */
    public static List<User> fromJsonTweetArray(List<Tweet> tweetList) {

        //Create an array of user objects
        List<User> users = new ArrayList<>();

        //For each tweet
        for(int i = 0;i< tweetList.size();i++){

            //Get the publishing user
            users.add(tweetList.get(i).user);
        }

        //Return the list of users
        return users;
    }
}
