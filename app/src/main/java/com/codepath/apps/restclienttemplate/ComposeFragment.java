package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.codepath.apps.restclienttemplate.databinding.FragmentComposeBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeFragment extends DialogFragment implements View.OnClickListener {


    @NonNull FragmentComposeBinding app;
    TwitterClient client;
    public static final String TAG ="ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 140;

    public ComposeFragment() {

    }

    public static ComposeFragment newInstance(String title) {
        ComposeFragment frag = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        app = FragmentComposeBinding.inflate(getLayoutInflater());
        View view = inflater.inflate(R.layout.fragment_compose,
                container, false);
        ;

        client = TwitterApp.getRestClient(getActivity());

        Button btnTweet = view.findViewById(R.id.btnTweet);
        btnTweet.setOnClickListener(v -> {
            String tweetContent = app.etCompose.getText().toString();
            if(tweetContent.isEmpty()){
                Toast.makeText(getActivity(),
                        "Tweet cannot be empty.",
                        Toast.LENGTH_SHORT).show();
            }
            if(tweetContent.length()>MAX_TWEET_LENGTH){
                Toast.makeText(getActivity(), "Tweet is too long.", Toast.LENGTH_LONG).show();
            }
            else{
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.d(TAG,"onSuccess");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.d(TAG,"Published tweet says "+tweet.body);
                            dismiss();
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

        return inflater.inflate(R.layout.fragment_compose, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {

    }
}
