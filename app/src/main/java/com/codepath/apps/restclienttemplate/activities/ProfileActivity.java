package com.codepath.apps.restclienttemplate.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.client.TwitterApp;
import com.codepath.apps.restclienttemplate.client.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.ActivityProfileBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Headers;

/*
    Class for viewing a user's profile, details and tweets
 */
public class ProfileActivity extends AppCompatActivity {

    //Prepare for view binding to reduce boilerplate code
    ActivityProfileBinding app;

    //Twitter client
    TwitterClient client;

    //List of user's tweets
    List<Tweet> tweets;

    //Adapter for the tweets recyclerview
    TweetsAdapter adapter;

    //ID of the last retrieved tweet
    String maxId = "";

    //User to view
    User user;

    //TAG for logging
    public static final String TAG = "ActivityProfile";

    /*
       Sets up the screen's items

       @param savedInstanceState - Current state of the screen

       @return void
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Set screen's current state
        super.onCreate(savedInstanceState);

        //Set the activity's layout xml
        setContentView(R.layout.activity_profile);

        //Inflate the binder with the layout
        app = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        //Change color of notifications bar
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#15202a"));

        //Set custom header
        Objects.requireNonNull(this.getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar2);

        //Change title of window
        TextView title = findViewById(R.id.abTitle);
        title.setText(R.string.profile);

        //Get twitter client
        client = TwitterApp.getRestClient(this);

        //Get the user to view their profile
        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        //Set a scrolling listener for endless scrolling
        TweetsAdapter.OnScrollListener scrollListener = position -> {

            //If the user has gotten to the end of the list of tweets
            if(position>=tweets.size()-1){

                //Get more tweets
                fetchTweets(user,maxId);
            }
        };

        //Set a click listener to open a tweet's details
        TweetsAdapter.OnClickListener clickListener = position -> {

            //Prepare intent
            Intent i = new Intent(this,DetailActivity.class);

            //Put the tweet into the intent
            i.putExtra("tweet", Parcels.wrap(tweets.get(position)));

            //Start activity with intent
            startActivity(i);
        };

        //Prepare list of user's tweets
        tweets = new ArrayList<>();

        //Create an adapter for the recyclerview with scroll and click listeners
        adapter = new TweetsAdapter(this, tweets, scrollListener,clickListener);

        //Set the layout for the recyclerview
        app.rvPTweets.setLayoutManager(new LinearLayoutManager(this));

        //Set the adapter for the recyclerview
        app.rvPTweets.setAdapter(adapter);

        //Populate the screen's details with the user's information
        app.tvPFollowers.setText(String.valueOf(user.followers));
        app.tvPFollowing.setText(String.valueOf(user.following));
        app.tvPName.setText(user.name);
        app.tvPScreenName.setText(user.screenName);
        app.tvPBio.setText(user.description);
        Glide.with(this).load(user.profileImageUrl)
                .transform(new RoundedCorners(100))
                .into(app.ivPProfilePicture);

        Glide.with(this).load(user.bannerImageUrl)
                .into(app.ivPBanner);

        //Populate the list of user's tweets
        populateProfileTimeline();
    }

    /*
        Populates the profile's recyclerview with all the tweets published by the user

        @param none

        @return void
     */
    private void populateProfileTimeline() {

        //Get the user's timeline of published tweets
        client.getProfileTimeline(user.screenName, new JsonHttpResponseHandler() {

            //If the request was successful
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {

                //Log success
                Log.d(TAG,"Tweets retrieved");

                //Get the array of jsonObject tweets
                JSONArray jsonArray = json.jsonArray;

                try {

                    //Clean the recyclerview
                    adapter.clear();

                    //Add all json Objects as Tweet objects
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));

                    //Notify the adapter that the dataset has changed
                    adapter.notifyDataSetChanged();
                    maxId = tweets.get(tweets.size()-1).id;

                } catch (JSONException e) {
                    Log.e(TAG,"JSON Exception",e);
                    e.printStackTrace();
                }

            }

            //If the request was unsuccessful
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG,"Tweets not retrieved: "+response,throwable);
            }
        });
    }

    /*
        Gets all tweets from a user published before a specific tweet

        @param user - The user whose tweets are required
        @param max - The tweet id before which we want to retrieve tweets

        @return void
        
     */
    private void fetchTweets(User user,String max){

        //Get the user's timeline parting from the selected tweet
        client.getProfileTimeline(user.screenName,max,new JsonHttpResponseHandler() {

            //If the request was successful
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {

                //Log success
                Log.d(TAG,"Tweets retrieved");

                //Get the array of tweets
                JSONArray jsonArray = json.jsonArray;
                try {

                    //Add all json Objects as Tweet objects
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));

                    //Notify the adapter that the dataset changed
                    adapter.notifyDataSetChanged();

                    //Set the oldest tweet
                    maxId = tweets.get(tweets.size()-1).id;

                } catch (JSONException e) {
                    Log.e(TAG,"JSON Exception",e);
                    e.printStackTrace();
                }

            }

            //If the request was unsuccessful
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                //Log failure
                Log.d(TAG,"Tweets not retrieved",throwable);
            }
        });
    }
}