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

/*
    Class for handling Tweet objects
 */
@Parcel
@Entity(foreignKeys = @ForeignKey(entity=User.class,parentColumns = "uid",childColumns = "userId"))
public class Tweet {

    //TAG for Logging
    private static final String TAG = "Tweet";

    //Member variables setup for database parsing
    @ColumnInfo
    @PrimaryKey
    @NonNull
    //Unique Tweet ID
    public String id;

    //The user that published the tweet
    @Ignore
    public User user;

    //The ID of the user that published the tweet
    @ColumnInfo
    public String userId;

    //The user that retweeted the tweet
    @Ignore
    public User retweeter;

    //The first embedded image
    @Ignore
    public Media photo;

    //The content of the tweet
    @ColumnInfo
    public String body;

    //The unformatted date and time the tweet was published
    @ColumnInfo
    public String createdAt;

    //The device from which the tweet was sent
    @ColumnInfo
    public String source;

    //The formatted date the tweet was published
    @ColumnInfo
    public String date;

    //The formatted time the tweet was published
    @ColumnInfo
    public String time;

    //The amount of likes received
    @ColumnInfo
    public int likes;

    //The times the tweet has been retweeted
    @ColumnInfo
    public int retweets;

    //If the current user has liked the tweet
    @ColumnInfo
    public boolean liked;

    //If the current user has retweeted the tweet
    @ColumnInfo
    public boolean retweeted;

    /*
        Default constructor

        @param none

        @return none
     */
    public Tweet() {}

    /*
        Creates a tweet from the information in a JSON Object

        @param jsonObject - The Object with the information

        @return Tweet - The parsed Tweet
     */
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {

        //Create a new tweet
        Tweet tweet = new Tweet();

        //Check if the tweet is actually a retweet
        try{

            //Get the original tweet
            JSONObject retweetStatus = jsonObject.getJSONObject("retweeted_status");

            //Recursively create a new tweet from the original tweet information
            tweet = fromJson(retweetStatus);

            //Set the user that retweeted the tweet
            tweet.retweeter = User.fromJson(jsonObject.getJSONObject("user"));

        //If the tweet is not a retweet
        }catch(JSONException notRetweet){

            //Set the tweet information from the jsonObject
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

            //Check if there is embedded media
            try {

                //Get entities from tweet
                JSONObject extendedEntities = jsonObject.getJSONObject("extended_entities");

                //Get embedded images
                JSONArray media = extendedEntities.getJSONArray("media");

                //Get the first image and create a Media object with it
                tweet.photo = new Media(media.getJSONObject(0));

                //Remove the image URL from the tweet body
                tweet.body = tweet.body.replace(tweet.photo.tinyUrl,"");

            } catch (JSONException noMedia) {
                Log.d(TAG,"No Media");
            }

        }

        //Return the generated tweet
        return tweet;
    }

    /*
        Creates an array of tweets from an array of jsonObjects

        @param jsonArray - The array of objects with tweet information

        @return List<Tweet> - The generated array of tweet objects
     */
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {

        //Prepare list of tweets
        List<Tweet> tweets = new ArrayList<>();

        //For each jsonObject with tweet information
        for (int i = 0; i < jsonArray.length(); i++) {

            //Create a new tweet from the json information and add to the array
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }

        //Return the array of generated tweets
        return tweets;
    }

    /*
        Receives an unformatted date string and returns how long ago the tweet was
        published

        @param rawJsonDate - The string with the time the tweet was published

        @return String - How long ago relative to now the tweet was created
     */
    public static String getRelativeTimeAgo(String rawJsonDate) {

        //Local time variables
        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        //The format that twitter uses for raw timestamps
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";

        //Create a formatted date from the twitter format
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);

        //Set the formatted date to allow inputs that don't exactly match
        sf.setLenient(true);

        try {

            //Get the numerical time from the timestamp
            long time = Objects.requireNonNull(sf.parse(rawJsonDate)).getTime();

            //Get the current numerical time
            long now = System.currentTimeMillis();

            //Get the relative time difference
            final long diff = now - time;

            //Create a specific string for each time period
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
    /*
        Creates a formatted date from the unformatted timestamp

        @param rawDate - The raw unformatted timestamp

        @return String - The formatted date
     */
    public static String getDate(String rawDate){

        //Split the date into space separated sections
        String[] date = rawDate.split(" ");

        //Get the date information
        String day = date[2];
        String month = date[1];
        String year = date[5].substring(2);

        //Return the formatted date
        return String.format("%s %s. %s",day,month,year);
    }

    /*
        Creates a formatted time from the unformatted timestamp

        @param rawDate - The raw unformatted timestamp

        @return String - The formatted time
     */
    public static String getTime(String rawDate){

        //Split the date into space separated sections
        String[] date = rawDate.split(" ");

        //Return the hour and minute
        return date[3].substring(0,5);
    }

    /*
        Creates a formatted amount for likes and retweets
        (i.e. 34.5K instead of 34503)

        @param count - The number to be formatted

        @return String - The formatted count
     */
    public static String formatCount(int count){

        //Set default formatted count to current count
        String result = String.valueOf(count);

        //If the count is in the thousands
        if(count>=1000&&count<1000000){

            //Get the amount of thousands
            int k = count/1000;

            //Get the amount of hundreds
            int h = (count-k*1000)/100;

            //Set the formatted string
            result = String.format("%s.%sK",k,h);
        }

        //If the count is in the millions
        else if(count>=1000000){

            //Get the amount of millions
            int m = count/1000000;

            //Get the amount of hundred thousands
            int k = (count-m*1000000)/100000;

            //Set the formatted string
            result = String.format("%s.%sM",m,k);
        }

        //Return the formatted count string
        return result;
    }

}
