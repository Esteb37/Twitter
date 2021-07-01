package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
@Parcel
@Entity(foreignKeys = @ForeignKey(entity=User.class,parentColumns = "uid",childColumns = "userId"))
public class Tweet {

    private static final String TAG = "Tweet";


    @ColumnInfo
    @PrimaryKey
    @NonNull
    public String id;

    @Ignore
    public User user;

    @ColumnInfo
    public String userId;

    @Ignore
    public User retweeter;

    @Ignore
    public Media photo;

    @ColumnInfo
    public String body;

    @ColumnInfo
    public String createdAt;

    @ColumnInfo
    public String source;

    @ColumnInfo
    public String date;

    @ColumnInfo
    public String time;

    @ColumnInfo
    public int likes;

    @ColumnInfo
    public int retweets;

    @ColumnInfo
    public boolean liked;

    @ColumnInfo
    public boolean retweeted;

    public Tweet() {
    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        try{
            JSONObject retweetStatus = jsonObject.getJSONObject("retweeted_status");
            tweet = fromJson(retweetStatus);
            tweet.retweeter = User.fromJson(jsonObject.getJSONObject("user"));

        }catch(JSONException e){
            tweet.body = jsonObject.getString("text");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
            tweet.userId = tweet.user.uid;
            tweet.id = jsonObject.getString("id_str");
            tweet.liked = jsonObject.getBoolean("favorited");
            tweet.retweeted = jsonObject.getBoolean("retweeted");
            tweet.likes = jsonObject.getInt("favorite_count");
            tweet.retweets = jsonObject.getInt("retweet_count");
            tweet.source = jsonObject.getString("source");
            tweet.date = getDate(tweet.createdAt);
            tweet.time = getTime(tweet.createdAt);

            tweet.photo = null;

            try {
                JSONObject extendedEntities = jsonObject.getJSONObject("extended_entities");
                JSONArray media = extendedEntities.getJSONArray("media");
                tweet.photo = new Media(media.getJSONObject(0));
                tweet.body = tweet.body.replace(tweet.photo.tinyUrl,"");
            } catch (JSONException e2) {
                Log.e(TAG, String.valueOf(e2));
            }

        }




        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = Objects.requireNonNull(sf.parse(rawJsonDate)).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (ParseException e) {
            Log.i(TAG, "getRelativeTimeAgo failed");
            e.printStackTrace();
        }

        return "";
    }

    public static String getDate(String rawDate){
        String[] date = rawDate.split(" ");
        String day = date[2];
        String month = date[1];
        String year = date[5].substring(2);
        return String.format("%s %s. %s",day,month,year);
    }

    public static String getTime(String rawDate){
        String[] date = rawDate.split(" ");
        return date[3].substring(0,5);
    }

    public static String formatCount(int count){

        String result = String.valueOf(count);
        if(count>=1000&&count<1000000){
            int k = count/1000;
            int h = (count-k*1000)/100;
            result = String.format("%s.%sK",k,h);
        }
        else if(count>=1000000){
            int m = count/1000000;
            int k = (count-m*1000000)/100000;
            result = String.format("%s.%sM",m,k);
        }

        return result;
    }

}
