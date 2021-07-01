package com.codepath.apps.restclienttemplate;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.Objects;

import okhttp3.Headers;

public class ComposeFragment extends DialogFragment implements View.OnClickListener {


    TwitterClient client;
    public static final String TAG ="ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 140;

    Button btnTweet;
    EditText etCompose;
    ImageView ivCProfileImage;
    User currentUser;

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

        View view = inflater.inflate(R.layout.fragment_compose,
                container, false);

        btnTweet= view.findViewById(R.id.btnTweet);
        etCompose= view.findViewById(R.id.etCompose);
        ivCProfileImage = view.findViewById(R.id.ivCProfileImage);

        client = TwitterApp.getRestClient(getActivity());

        Bundle bundle = this.getArguments();
        assert bundle != null;
        currentUser = Parcels.unwrap(bundle.getParcelable("User"));

        Glide.with(getActivity()).load(currentUser.profileImageUrl)
                .transform(new RoundedCorners(100))
                .into(ivCProfileImage);


        btnTweet.setOnClickListener(v -> {
            String tweetContent = Objects.requireNonNull(etCompose.getText()).toString();
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
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {

        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }
    @Override
    public void onClick(View v) {

    }
}
