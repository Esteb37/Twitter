package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.databinding.ActivityDetailBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding app;
    Tweet tweet;

    TwitterClient client;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    String maxId = "";

    public static final String TAG = "DetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        app = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        client = TwitterApp.getRestClient(this);

        TweetsAdapter.OnScrollListener scrollListener = position -> {
            Log.i("Scroll", String.valueOf(position));
            if(position>=tweets.size()-1){
               // fetchTweets(tweet.user,maxId);
            }
        };

        TweetsAdapter.OnClickListener clickListener = position -> {
            Intent i = new Intent(this,DetailActivity.class);
            i.putExtra("tweet", Parcels.wrap(tweets.get(position)));
            startActivity(i);
        };


        tweets = new ArrayList<>();

        adapter = new TweetsAdapter(this, tweets, scrollListener,clickListener);

        app.rvReplies.setLayoutManager(new LinearLayoutManager(this));
        app.rvReplies.setAdapter(adapter);

        app.tvDName.setText(tweet.user.name);
        app.tvDScreenName.setText(tweet.user.screenName);
        app.tvDContent.setText(tweet.body);
        app.tvDLikes.setText(Tweet.formatCount(tweet.likes));
        app.tvDRetweets.setText(Tweet.formatCount(tweet.retweets));
        app.tvDSource.setText(Html.fromHtml(tweet.source));
        app.tvDDate.setText(tweet.date);
        app.tvDTime.setText(tweet.time);

        if(tweet.retweeter!=null){
            app.tvDRetweeted.setText(String.format("%s Retweeted", tweet.retweeter.name));
            app.llDRetweeted.setVisibility(View.VISIBLE);
        }
        else{
            app.llDRetweeted.setVisibility(View.GONE);
        }

        Glide.with(this).load(tweet.user.profileImageUrl)
                .transform(new FitCenter(), new RoundedCorners(100))
                .into(app.ivDProfilePicture);

        populateRepliesTimeline(tweet.user);
    }

    private void populateRepliesTimeline(User user) {

        client.getUserMentions(user.screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray statuses= null;
                try {
                    statuses = json.jsonObject.getJSONArray("statuses");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    adapter.clear();
                    tweets.addAll(client.parseReplies(statuses,tweet));
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG,"onFail"+response,throwable);
            }
        });
    }

}