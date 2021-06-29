package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    ActivityComposeBinding app;
    TwitterClient client;
    public static final String TAG ="ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 140;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        app = ActivityComposeBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        client = TwitterApp.getRestClient(this);

        app.btnTweet.setOnClickListener(v -> {
            String tweetContent = app.etCompose.getText().toString();
            if(tweetContent.isEmpty()){
                Toast.makeText(ComposeActivity.this, "Tweet cannot be empty.", Toast.LENGTH_SHORT).show();
            }
            if(tweetContent.length()>MAX_TWEET_LENGTH){
                Toast.makeText(ComposeActivity.this, "Tweet is too long.", Toast.LENGTH_LONG).show();
            }
            else{
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.d(TAG,"onSuccess");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.d(TAG,"Published tweet says "+tweet.body);
                            Intent i = new Intent();
                            i.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK,i);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.d(TAG,"onFailure");
                    }
                });
            }


        });

    }
}