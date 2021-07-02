package com.codepath.apps.restclienttemplate;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.databinding.ActivityDetailBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Headers;

/*
    Screen for viewing a tweet's details and replies
 */
public class DetailActivity extends AppCompatActivity {

    //Prepare for view binding to reduce boilerplate code
    ActivityDetailBinding app;

    //The tweet to view
    Tweet tweet;

    //Twitter client
    TwitterClient client;

    //List of replies
    List<Tweet> tweets;

    //Adapter for the recyclerview
    TweetsAdapter adapter;

    //TAG for Logging
    public static final String TAG = "DetailActivity";

    //String maxId = "";

    /*
        Sets up the screen's items

        @param savedInstanceState - Current state of the screen

        @return void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Save state of screen
        super.onCreate(savedInstanceState);

        //Set the content of the screen to the activity's xml
        setContentView(R.layout.activity_detail);

        //Inflate the binder with the layout
        app = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        //Set the notification bar's color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#15202a"));

        //Set the header to a custom layout
        Objects.requireNonNull(this.getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar2);

        //Set the title of the window
        TextView title = findViewById(R.id.abTitle);
        title.setText(R.string.tweet);

        //Get the tweet that the user wants to view
        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        //Get the twitter client
        client = TwitterApp.getRestClient(this);

        //Setup a scroll listener for the replies recyclerview
        TweetsAdapter.OnScrollListener scrollListener = position -> {

            //If scrolled to the end
            /*if(position>=tweets.size()-1){

                //Get more replies
               // fetchTweets(tweet.user,maxId);
            }*/
        };

        //Setup a click listener for the replies recycler view
        TweetsAdapter.OnClickListener clickListener = position -> {

            //Create an intent to view the selected tweet's details
            Intent i = new Intent(this,DetailActivity.class);

            //Pass the selected tweet into the intent
            i.putExtra("tweet", Parcels.wrap(tweets.get(position)));

            //Start another detail activity
            startActivity(i);
        };

        //Instantiate reply list
        tweets = new ArrayList<>();

        //Instantiate adapter with scroll and click listeners
        adapter = new TweetsAdapter(this, tweets, scrollListener,clickListener);

        //Set layout manager for the replies recycler view
        app.rvReplies.setLayoutManager(new LinearLayoutManager(this));

        //Set adapter for the replies recycler view
        app.rvReplies.setAdapter(adapter);

        //Populate the screen's information from the tweet
        app.tvDName.setText(tweet.user.name);
        app.tvDScreenName.setText(tweet.user.screenName);
        app.tvDContent.setText(tweet.body);
        app.tvDLikes.setText(Tweet.formatCount(tweet.likes));
        app.tvDRetweets.setText(Tweet.formatCount(tweet.retweets));
        app.tvDSource.setText(Html.fromHtml(tweet.source));
        app.tvDDate.setText(tweet.date);
        app.tvDTime.setText(tweet.time);

        //If the tweet was retweeted
        if(tweet.retweeter!=null){

            //Set "retweeted by" banner
            app.tvDRetweeted.setText(String.format("%s Retweeted", tweet.retweeter.name));
            app.llDRetweeted.setVisibility(View.VISIBLE);
        }

        //If the tweet is original
        else{

            //Hide "retweeted by banner"
            app.llDRetweeted.setVisibility(View.GONE);
        }

        //Set profile image of user
        Glide.with(this).load(tweet.user.profileImageUrl)
                .transform(new FitCenter(), new RoundedCorners(100))
                .into(app.ivDProfilePicture);

        //If the tweet has an embedded image
        if(tweet.photo!=null){

            //Show the image
            app.ivPMedia.setVisibility(View.VISIBLE);

            //Set the image
            Glide.with(this).load(tweet.photo.url)
                    .transform(new CenterCrop(), new RoundedCorners(80))
                    .into(app.ivPMedia);
        }

        //Fill the recyclerview with replies
        populateRepliesTimeline();
    }

    /*
        Parses the user's mentions to find replies to this tweet
        and fills the recycler view with the replies

        @param none

        @return void
     */
    private void populateRepliesTimeline() {

        //Get all the tweets in which the user has been mentioned
        client.getUserMentions(tweet.user.screenName, new JsonHttpResponseHandler() {

            //If the request is successful
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {

                //Log success
                Log.d(TAG,"Mentions retrieved");

                //Prepare the array for all the user's mentions
                JSONArray statuses = null;

                try {

                    //Get the array of tweets
                    statuses = json.jsonObject.getJSONArray("statuses");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {

                    //Clean the current recycler view
                    adapter.clear();

                    //Find all replies and add them to the array
                    assert statuses != null;
                    tweets.addAll(client.parseReplies(statuses,tweet));

                    //Notify the adapter that the data has changed
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            //If the request is unsuccessful
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                //Log fail
                Log.d(TAG,"Mentions not retrieved: "+response,throwable);
            }
        });
    }

}