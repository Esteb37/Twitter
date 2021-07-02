package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.parceler.Parcels;


import okhttp3.Headers;

public class ReplyFragment extends DialogFragment implements View.OnClickListener {

    TwitterClient client;
    public static final String TAG ="ReplyActivity";
    public static final int MAX_TWEET_LENGTH = 140;

    Button btnReply;
    EditText etReply;

    public ReplyFragment() {

    }

    public static ReplyFragment newInstance(String title) {
        ReplyFragment frag = new ReplyFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_reply,
                container, false);

        etReply = view.findViewById(R.id.etReply);
        btnReply = view.findViewById(R.id.btnReply);

        client = TwitterApp.getRestClient(getActivity());

        btnReply.setOnClickListener(v -> {
            String tweetContent = String.valueOf(etReply.getText());
            if(tweetContent.isEmpty()){
                Toast.makeText(getActivity(),
                        "Tweet cannot be empty.",
                        Toast.LENGTH_SHORT).show();
            }
            if(tweetContent.length()>MAX_TWEET_LENGTH){
                Toast.makeText(getActivity(), "Tweet is too long.", Toast.LENGTH_LONG).show();
            }
            else{
                Bundle bundle = this.getArguments();
                assert bundle != null;
                Tweet tweet = Parcels.unwrap(bundle.getParcelable("Tweet"));
                client.replyTweet(tweet,tweetContent, new JsonHttpResponseHandler() {
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

        return view;
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {

    }
}
