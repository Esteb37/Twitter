package com.codepath.apps.restclienttemplate.fragments;
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

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.client.TwitterApp;
import com.codepath.apps.restclienttemplate.client.TwitterClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import okhttp3.Headers;

/*
    PopUp for composing and publishing a tweet
 */
public class ComposeFragment extends DialogFragment implements View.OnClickListener {

    //Twitter API Client
    TwitterClient client;

    //TAG for Logging
    public static final String TAG ="ComposeActivity";

    //Maximum amount of characters permitted in a tweet
    public static final int MAX_TWEET_LENGTH = 140;

    //Button for publishing the tweet
    Button btnTweet;

    //Text input for the tweet content
    EditText etCompose;

    /*
        Default constructor

        @param none

        @return none
     */
    public ComposeFragment() {

    }

    /*
        Creates a new instance of the popup fragment

        @param title - The popup's title

        @return ComposeFragment - The popup instance
     */
    public static ComposeFragment newInstance(String title) {

        //Create a new fragment object
        ComposeFragment frag = new ComposeFragment();

        //Prepare the argument bundle
        Bundle args = new Bundle();

        //Put the title in the bundle
        args.putString("title", title);

        //Set the arguments into the fragment
        frag.setArguments(args);

        //Return the fragment instance
        return frag;
    }

    /*
        Sets up the Fragment's views and layouts

        @param inflater - The layout inflater for visuals
        @param container - The fragment container
        @param savedInstanceState - The current state of the fragment

        @return View - The inflated view with all the fragment elements
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflate the view inside the container with the fragment's xml file
        View view = inflater.inflate(R.layout.fragment_compose,
                container, false);

        //Get the fragment's items
        etCompose = view.findViewById(R.id.etCompose);
        btnTweet = view.findViewById(R.id.btnTweet);

        //Get the Twitter Client
        client = TwitterApp.getRestClient(getActivity());

        //Set up a listener for the Tweet button
        btnTweet.setOnClickListener(v -> {

            //Get the contents of the Text Input
            String tweetContent = Objects.requireNonNull(etCompose.getText()).toString();

            //If the tweet is empty
            if(tweetContent.isEmpty()){

                //Announce to the user that it cannot be empty
                Toast.makeText(getActivity(),
                        "Tweet cannot be empty.",
                        Toast.LENGTH_SHORT).show();
            }

            //IF the tweet is too long
            if(tweetContent.length()>MAX_TWEET_LENGTH){

                //Announce to the user that it is too long
                Toast.makeText(getActivity(), "Tweet is too long.", Toast.LENGTH_LONG).show();
            }

            //If it fits all criteria
            else{

                //Send a request to publish the tweet
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {

                    //If the tweet is correctly published
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {

                        //Log success
                        Log.d(TAG,"Tweet published");

                        dismiss();
                    }

                    //If the tweet is not sent
                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.d(TAG,"Tweet Not Published: "+response,throwable);
                    }
                });
            }
        });

        //Return the inflated view
        return view;
    }

    /*
        Sets the size of the fragment to occupy most of the available screen

        @param none

        @return void
     */
    @Override
    public void onResume() {
        // Get existing layout params for the window
        WindowManager.LayoutParams params = Objects.requireNonNull(getDialog()).getWindow().getAttributes();

        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;

        //Set the custom properties to fragment
        getDialog().getWindow().setAttributes(params);

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
