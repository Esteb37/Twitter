package com.codepath.apps.restclienttemplate.activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.client.TwitterApp;
import com.codepath.apps.restclienttemplate.client.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.fragments.ComposeFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.TweetWithUser;
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
    Home screen with timeline of user's tweets
 */
public class TimelineActivity extends AppCompatActivity {

    //Prepare for view binding to reduce boilerplate code
    ActivityTimelineBinding app;

    //Twitter client
    TwitterClient client;

    //User's timeline of tweets
    List<Tweet> tweets;

    //Adapter for the recyclerview
    TweetsAdapter adapter;

    //ID of the tweet at the current button
    String maxId = "0";

    //Data access object for persisting timeline
    TweetDao tweetDao;

    //The logged-in user's information object
    User currentUser;

    //TAG for Logging
    public static final String TAG = "TimelineActivity";

    /*
       Sets up the screen's items

       @param savedInstanceState - Current state of the screen

       @return void
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Set current instance state
        super.onCreate(savedInstanceState);

        //Set the screen's xml file
        setContentView(R.layout.activity_timeline);

        //Change color of notification bar
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#15202a"));

        //Set custom action bar
        Objects.requireNonNull(this.getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);

        //Inflate the binder with the layout
        app = ActivityTimelineBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        //Get the Twitter client
        client = TwitterApp.getRestClient(this);

        //Instantiate the database object
        tweetDao = ((TwitterApp) getApplicationContext()).getMyDatabase().tweetDao();

        //Create a scroll listener for lazy loading
        TweetsAdapter.OnScrollListener scrollListener = position -> {

            //If the user has scrolled to the bottom
            if(position>=tweets.size()-1){

                //Load more tweets
                fetchTweets(maxId);
            }
        };

        //Create a click listener for opening a detailed tweet
        TweetsAdapter.OnClickListener clickListener = position -> {

            //Create the intent to open Detail Activity
            Intent i = new Intent(this,DetailActivity.class);

            //Pass the selected tweet into the intent
            i.putExtra("tweet",Parcels.wrap(tweets.get(position)));

            //Launch the detail activity
            startActivity(i);
        };

        //Prepare list of tweets
        tweets = new ArrayList<>();

        //Create the adapter for the recyclerview
        adapter = new TweetsAdapter(this, tweets, scrollListener,clickListener);

        //Set the layout manager for the recycler view
        app.rvTweets.setLayoutManager(new LinearLayoutManager(this));

        //Set the adapter for the recycler view
        app.rvTweets.setAdapter(adapter);

        //Set an onclick listener for the Compose button
        app.floatingActionButton.setOnClickListener(v->{

            //Get the fragment manager
            FragmentManager fm = getSupportFragmentManager();

            //Create a new instance of the compose fragment
            ComposeFragment composeFragment = ComposeFragment.newInstance("New Tweet");

            //Prepare a bundle
            Bundle bundle = new Bundle();

            //Pass the current user into the bundle
            bundle.putParcelable("User",Parcels.wrap(currentUser));

            //Put the bundle into the fragment
            composeFragment.setArguments(bundle);

            //Load fragment
            composeFragment.show(fm, "activity_compose");
        });

        //Load the persisted tweets from the database
        AsyncTask.execute(() -> {

            //Get the tweetwithuser objects from the database
            List<TweetWithUser> tweetWithUsers = tweetDao.recentItems();

            //Get the list of tweets from the tweetwithuser objects
            List<Tweet> tweetsFromDB = TweetWithUser.getTweetList(tweetWithUsers);

            //Clean the timeline
            adapter.clear();

            //Add persisted tweets to the timeline
            tweets.addAll(tweetsFromDB);

            //Refresh the adapter
            adapter.notifyDataSetChanged();
        });

        //Set swipe down motion for refreshing the page
        app.swipeContainer.setOnRefreshListener(this::populateHomeTimeline);

        //Load tweets
        populateHomeTimeline();

        //Get the logged-in user
        getCurrentUser();
    }

    /*
        Loads the options menu's layout file

        @param menu - The options menu

        @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    /*public void showProgressBar() {
        progressBar.setVisible(true);
    }

    public void hideProgressBar() {
        progressBar.setVisible(false);
    }*/

    /*
        Detects button clicks from the options menu and determines the
        action selected

        @param item - The menu object

        @return true
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //If the selected button was "Log Out"
        if (item.getItemId() == R.id.menuLogout) {

            //Clear the current user's access token
            TwitterApp.getRestClient(this).clearAccessToken();

            //Navigate backwards to Login screen
            Intent i = new Intent(this, LoginActivity.class);

            //Makes sure the Back button won't work
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            //Same as above
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //Navigate to the login screen
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    /*
        Gets the user's tweet timeline and loads it into the recyclerview

        @param none

        @return void
     */
    private void populateHomeTimeline() {

        //Send a request for the user's timeline
        client.getHomeTimeline(new JsonHttpResponseHandler() {

            //If the request is successful
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {

                //Log success
                Log.i(TAG,"Timeline loaded");

                //Get the array of tweets from the response
                JSONArray jsonArray = json.jsonArray;
                try {

                    //Clean the timeline
                    adapter.clear();

                    //Create an array of tweet objects from the response
                    List<Tweet> tweetList = Tweet.fromJsonArray(jsonArray);

                    //Add tweets to the timeline
                    tweets.addAll(tweetList);

                    //Update the adapter
                    adapter.notifyDataSetChanged();

                    //Hide the "loading" refresh icon
                    app.swipeContainer.setRefreshing(false);

                    //Get the bottom tweet's id
                    maxId = tweets.get(tweets.size()-1).id;

                    //Persist the new timeline
                    AsyncTask.execute(() -> {

                        //Get the list of users whose tweets are present int he timeline
                        List<User> userList = User.fromJsonTweetArray(tweetList);

                        //Persist the users into the database
                        tweetDao.insertModel(userList.toArray(new User[0]));

                        //Persist the tweets into the database
                        tweetDao.insertModel(tweetList.toArray(new Tweet[0]));
                    });
                } catch (JSONException e) {
                    Log.e(TAG,"JSON Exception",e);
                    e.printStackTrace();
                }
            }

            //If the request was unsuccessful
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                //Log failure
                Log.i(TAG,"onFail"+response,throwable);
            }
        });
    }

    /*
        Gets a new list of tweets that were published before the selected tweet

        @param max - The id of the last tweet

        @return void
     */
    private void fetchTweets(String max){

        //Request the user's timeline published before the selected tweet
        client.getHomeTimeline(max,new JsonHttpResponseHandler() {

            //If the request was successful
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {

                //Log success
                Log.i(TAG,"Tweets fetched");

                //Get the array of tweet json objects
                JSONArray jsonArray = json.jsonArray;
                try {

                    //Get an array of tweet objects and add them to the timeline
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));

                    //Update the adapter
                    adapter.notifyDataSetChanged();

                    //Get the new bottom tweet's id
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
                Log.i(TAG,"onFail"+response,throwable);
            }
        });
    }

    /*
        Sets the current user's information into a user object

        @param none

        @return void
     */
    private void getCurrentUser(){

        //Request the current user's information
        client.getCurrentUser(new JsonHttpResponseHandler() {

            //If the request was successful
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {

                //Log success
                Log.d(TAG,"User retrieved");
                try {
                    //Create a User object from the JSON
                    currentUser = User.fromJson(json.jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //If the request was unsuccessful
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                //Log failure
                Log.d(TAG,"User not retrieved: "+response,throwable);
            }
        });
    }
}