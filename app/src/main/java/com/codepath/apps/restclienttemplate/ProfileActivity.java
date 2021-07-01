package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityProfileBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding app;
    TwitterClient client;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    String maxId = "";
    User user;
    public static final String TAG = "ActivityProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        app = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        client = TwitterApp.getRestClient(this);

        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        TweetsAdapter.OnScrollListener scrollListener = position -> {
            Log.i("Scroll", String.valueOf(position));
            if(position>=tweets.size()-1){
                fetchTweets(user,maxId);
            }
        };

        TweetsAdapter.OnClickListener clickListener = position -> {
            Intent i = new Intent(this,DetailActivity.class);
            i.putExtra("tweet", Parcels.wrap(tweets.get(position)));
            startActivity(i);
        };


        tweets = new ArrayList<>();

        adapter = new TweetsAdapter(this, tweets, scrollListener,clickListener);

        app.rvPTweets.setLayoutManager(new LinearLayoutManager(this));
        app.rvPTweets.setAdapter(adapter);

        app.tvPFollowers.setText(String.valueOf(user.followers));
        app.tvPFollowing.setText(String.valueOf(user.following));
        app.tvPName.setText(user.name);
        app.tvPScreenName.setText(user.screenName);
        app.tvPScreenName.setText(user.description);

        Glide.with(this).load(user.profileImageUrl)
                .into(app.ivPProfilePicture);
        Glide.with(this).load(user.bannerImageUrl)
                .into(app.ivPBanner);

        populateProfileTimeline(user);

    }


    private void populateProfileTimeline(User user) {

        client.getProfileTimeline(user.screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG,"onSuccess"+json.toString());
                Log.i(TAG,"onSuccess"+json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    adapter.clear();
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                    maxId = tweets.get(tweets.size()-1).id;

                } catch (JSONException e) {
                    Log.e(TAG,"JSON Exception",e);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG,"onFail",throwable);
            }
        });
    }

    private void fetchTweets(User user,String max){
        Log.i("maxId", max);
        client.getProfileTimeline(user.screenName,max,new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG,"onSuccess"+json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                    maxId = tweets.get(tweets.size()-1).id;
                } catch (JSONException e) {
                    Log.e(TAG,"JSON Exception",e);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG,"onFail",throwable);
            }
        });
    }
}