package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {

    public String name;
    public String screenName; //handle
    public String profileImageUrl;
    public String followers;
    public String following;
    public String bannerImageUrl;
    public String description;

    User(){}

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url_https");
        user.followers = jsonObject.getString("followers_count");
        user.following = jsonObject.getString("friends_count");
        try {
            user.bannerImageUrl = jsonObject.getString("profile_banner_url");
        }catch(JSONException e){

        }

        user.description = jsonObject.getString("description");
        return user;
    }
}
