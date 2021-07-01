package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    TwitterClient client;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    ActivityTimelineBinding app;
    MenuItem progressBar;
    String maxId = "0";
    TweetDao tweetDao;
    User currentUser;

    public static final int REQUEST_CODE = 37;

    public static final String TAG = "TimelineActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);



        app = ActivityTimelineBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        client = TwitterApp.getRestClient(this);
        tweetDao = ((TwitterApp) getApplicationContext()).getMyDatabase().tweetDao();

        TweetsAdapter.OnScrollListener scrollListener = position -> {
            Log.i("Scroll", String.valueOf(position));
            if(position>=tweets.size()-1){
                fetchTweets(maxId);
            }
        };

        TweetsAdapter.OnClickListener clickListener = position -> {
            Intent i = new Intent(this,DetailActivity.class);
            i.putExtra("tweet",Parcels.wrap(tweets.get(position)));
            startActivity(i);
        };


        tweets = new ArrayList<>();

        adapter = new TweetsAdapter(this, tweets, scrollListener,clickListener);

        app.rvTweets.setLayoutManager(new LinearLayoutManager(this));
        app.rvTweets.setAdapter(adapter);

        AsyncTask.execute(() -> {
            List<TweetWithUser> tweetWithUsers = tweetDao.recentItems();
            List<Tweet> tweetsFromDB = TweetWithUser.getTweetList(tweetWithUsers);
            adapter.clear();
            tweets.addAll(tweetsFromDB);
            adapter.notifyDataSetChanged();
        });
        populateHomeTimeline();
        getCurrentUser();

        Objects.requireNonNull(app.btnLogout).setOnClickListener(v -> {
            client.clearAccessToken(); // forget who's logged in
            finish(); // navigate backwards to Login screen
        });

        app.swipeContainer.setOnRefreshListener(this::populateHomeTimeline);

        app.floatingActionButton.setOnClickListener(v->{
            FragmentManager fm = getSupportFragmentManager();
            ComposeFragment composeFragment = ComposeFragment.newInstance("New Tweet");
            Bundle bundle = new Bundle();
            bundle.putParcelable("User",Parcels.wrap(currentUser));
            composeFragment.setArguments(bundle);
            composeFragment.show(fm, "activity_compose");
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        progressBar = menu.findItem(R.id.miActionProgress);
        return super.onPrepareOptionsMenu(menu);
    }

    /*public void showProgressBar() {
        progressBar.setVisible(true);
    }

    public void hideProgressBar() {
        progressBar.setVisible(false);
    }*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.compose) {


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            assert data != null;
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweets.add(0,tweet);
            adapter.notifyItemInserted(0);
            app.rvTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void populateHomeTimeline() {

        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG,"onSuccess"+json.toString());
                Log.i(TAG,"onSuccess"+json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    adapter.clear();
                    List<Tweet> tweetList = Tweet.fromJsonArray(jsonArray);
                    tweets.addAll(tweetList);
                    adapter.notifyDataSetChanged();
                    app.swipeContainer.setRefreshing(false);
                    maxId = tweets.get(tweets.size()-1).id;

                    AsyncTask.execute(() -> {

                        List<User> userList = User.fromJsonTweetArray(tweetList);
                        tweetDao.insertModel(userList.toArray(new User[0]));

                        tweetDao.insertModel(tweetList.toArray(new Tweet[0]));
                    });

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

    private void fetchTweets(String max){
        Log.i("maxId", max);
        client.getHomeTimeline(max,new JsonHttpResponseHandler() {
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

    private void getCurrentUser(){
        client.getCurrentUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    currentUser = User.fromJson(json.jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("User","success");
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("User","success");
            }
        });
    }
}