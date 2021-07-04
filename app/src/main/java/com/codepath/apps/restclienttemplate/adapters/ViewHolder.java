package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.activities.ProfileActivity;
import com.codepath.apps.restclienttemplate.client.TwitterApp;
import com.codepath.apps.restclienttemplate.client.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.fragments.ReplyFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;
/*
    Class for managing the Tweet item view holders in the RecyclerView
 */
public class ViewHolder extends RecyclerView.ViewHolder{

    //Current context
    Context context;

    //Twitter client
    TwitterClient client;

    //Binder vor View Binding to reduce boilerplate code
    ItemTweetBinding app;

    public static final String TAG = "ViewHolder";

    /*
        Constructor from an item view, binder, context and clickListener

        @param view - The view that contains the tweet details
        @param app - The binder used for View Binding
        @param context - The activity in which the adapter is functioning
        @param clickListener - A listener for clicking on a tweet item

        @return none
     */
    public ViewHolder(View view, ItemTweetBinding app, Context context, TweetsAdapter.OnClickListener clickListener){

        //Set the tweet view
        super(view);

        //Set the binder
        this.app = app;

        //Set a listener on the tweet view
        itemView.setOnClickListener(v -> clickListener.onItemClicked(getAdapterPosition()));

        //Set the current context
        this.context = context;

        //Get the Twitter Client
        client = TwitterApp.getRestClient(context);
    }

    /*
        Populates the tweet item with the tweet details and sets up the item

        @param tweet - The tweet being displayed

        @return void
     */
    public void bind(Tweet tweet){

        //If the tweet is a retweet
        if(tweet.retweeter!=null){

            //Set the "retweeted by" banner
            app.llRetweeted.setVisibility(View.VISIBLE);
            app.tvRetweeted.setText(String.format("%s Retweeted", tweet.retweeter.name));
        }

        //If the tweet is original
        else{

            //Hide the "retweeted by" banner
            app.llRetweeted.setVisibility(View.GONE);
        }

        //Set the tweet details
        app.tvBody.setText(tweet.body);
        app.tvHandle.setText(String.format("@%s", tweet.user.screenName));
        app.tvName.setText(tweet.user.name);
        app.tvTime.setText(Tweet.getRelativeTimeAgo(tweet.createdAt));

        //If the tweet has likes
        if(tweet.likes>0){

            //Show the amount of likes
            app.tvLikes.setText(Tweet.formatCount(tweet.likes));
            app.tvLikes.setVisibility(View.VISIBLE);
        }

        //If the tweet has no likes
        else

            //Hide the likes text
            app.tvLikes.setVisibility(View.INVISIBLE);

        //If the tweet has been retweeted
        if(tweet.retweets>0){

            //Show the amount of retweets
            app.tvRetweets.setText(Tweet.formatCount(tweet.retweets));
            app.tvRetweets.setVisibility(View.VISIBLE);
        }

        //If the tweet has not ben retweeted
        else

            //Hide the retweet text
            app.tvRetweets.setVisibility(View.INVISIBLE);

        //Load publisher's profile picture
        Glide.with(context).load(tweet.user.profileImageUrl)
                .transform(new RoundedCorners(100))
                .into(app.ivProfileImage);

        //If the tweet has embedded images
        if(tweet.photo!=null){

            //Load and show the image
            app.ivMedia.setVisibility(View.VISIBLE);
            Glide.with(context).load(tweet.photo.url)
                    .transform(new CenterCrop(), new RoundedCorners(80))
                    .into(app.ivMedia);

        }

        //If the tweet has no images
        else{

            //Hide the image view
            app.ivMedia.setVisibility(View.GONE);
        }

        //Show if the user has liked the tweet or not
        if(tweet.liked)
            app.btnLike.setImageResource(R.drawable.ic_vector_heart);

        else
            app.btnLike.setImageResource(R.drawable.ic_vector_heart_stroke);

        //Show if the user has retweeted the tweet or not
        if(tweet.retweeted)
            app.btnRetweet.setImageResource(R.drawable.ic_vector_retweet);
        else
            app.btnRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);

        //Set a click listener on the tweet's profile picture
        app.ivProfileImage.setOnClickListener(v -> {

            //Set an intent to open the profile screen
            Intent i = new Intent(context, ProfileActivity.class);

            //Pass the user into the activity
            i.putExtra("user",Parcels.wrap(tweet.user));

            //Start the activity
            context.startActivity(i);
        });

        //Set an onclick listener for the like button
        app.btnLike.setOnClickListener(v -> {

            //If the user has liked the tweet before
            if(tweet.liked){

                //Send a request to remove the user's like from the tweet
                client.unlikeTweet(tweet.id, new JsonHttpResponseHandler() {

                    //If the tweet was successfully liked
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {

                        //Log success
                        Log.d(TAG,"Tweet Unliked");

                        //Remove the "liked by user" status
                        tweet.liked = false;

                        //Remove the user's like from the tweet's like count
                        tweet.likes-=1;

                        //Change the like button icon to "not liked"
                        app.btnLike.setImageResource(R.drawable.ic_vector_heart_stroke);

                        //If the tweet has likes
                        if(tweet.likes>0) {

                            //Show the like count
                            app.tvLikes.setText(Tweet.formatCount(tweet.likes));
                            app.tvLikes.setVisibility(View.VISIBLE);
                        }

                        //If the tweet has no likes
                        else

                            //Hide the like count
                            app.tvLikes.setVisibility(View.INVISIBLE);

                    }

                    //If the request was unsuccessful
                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                        //Log failure
                        Log.d(TAG,"Tweet not unliked:  "+response,throwable);
                    }
                });
            }

            //If the user has not liked the tweet
            else{

                //Send a request to like the tweet
                client.likeTweet(tweet.id, new JsonHttpResponseHandler() {

                    //If the request is successful
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {

                        //Log success
                        Log.d(TAG,"Tweet liked");

                        //Set the like icon button to "liked"
                        app.btnLike.setImageResource(R.drawable.ic_vector_heart);

                        //Add user's like to tweet's like count
                        tweet.likes+=1;

                        //Set the tweet to "liked by user"
                        tweet.liked = true;

                        //If the tweet has likes
                        if(tweet.likes>0) {

                            //Show the like count
                            app.tvLikes.setText(Tweet.formatCount(tweet.likes));
                            app.tvLikes.setVisibility(View.VISIBLE);
                        }

                        //If the tweet has no likes
                        else

                            //Hide the like count
                            app.tvLikes.setVisibility(View.INVISIBLE);
                    }

                    //IF the request is unsuccessful
                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                        //Log failure
                        Log.d(TAG,"Tweet not liked: "+response,throwable);
                    }
                });
            }

        });

        //Set an onclick listener for the "comment" button
        app.btnComment.setOnClickListener(v -> {

            //Get a fragment manager
            FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();

            //Prepare a bundle
            Bundle bundle = new Bundle();

            //Put the tweet into the bundle
            bundle.putParcelable("Tweet", Parcels.wrap(tweet));

            //Create an instance of the "reply" fragment
            ReplyFragment replyFragment = ReplyFragment.newInstance("Reply");

            //Pass the bundle into the fragment
            replyFragment.setArguments(bundle);

            //Launch the fragment
            replyFragment.show(fm, "activity_reply");
        });

        //Set an onclick listener for the "retweet" button
        app.btnRetweet.setOnClickListener(v -> {

            //If the tweet has been retweeted by the user
            if(tweet.retweeted){

                //Send a request to undo the retweet
                client.unretweetTweet(tweet.id, new JsonHttpResponseHandler() {

                    //If the request was successful
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {

                        //Log success
                        Log.d(TAG,"Tweet unretweeted");

                        //Set the tweet to "not retweeted by user"
                        tweet.retweeted = false;

                        //Remove the retweet from the retweet count
                        tweet.retweets-=1;

                        //Set the retweet icon to "not retweeted"
                        app.btnRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);

                        //If the tweet has retweets
                        if(tweet.retweets>0) {

                            //Show the retweet count
                            app.tvRetweets.setText(Tweet.formatCount(tweet.retweets));
                            app.tvRetweets.setVisibility(View.VISIBLE);
                        }

                        //If the tweet has no retweets
                        else

                            //hide the retweet count
                            app.tvRetweets.setVisibility(View.INVISIBLE);

                    }

                    //If the request is not successful
                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                        //Log failure
                        Log.d(TAG,"Tweet not unretweeted: "+response,throwable);
                    }
                });
            }

            //If the user has not retweeted the tweet
            else{

                //Send a request to retweet the tweet
                client.retweetTweet(tweet.id, new JsonHttpResponseHandler() {

                    //If the request was successful
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {

                        //Log success
                        Log.d(TAG,"Tweet retweeted");

                        //Set the retweet icon to "retweeted"
                        app.btnRetweet.setImageResource(R.drawable.ic_vector_retweet);

                        //Add the retweet to the tweet's retweet count
                        tweet.retweets+=1;

                        //Set the tweet's status to "retweeted by user"
                        tweet.retweeted = true;

                        //If the tweet has retweets
                        if(tweet.retweets>0) {

                            //Show the retweet count
                            app.tvRetweets.setText(Tweet.formatCount(tweet.retweets));
                            app.tvRetweets.setVisibility(View.VISIBLE);
                        }

                        //If the tweet has no retweets
                        else

                            //Hide the retweet count
                            app.tvRetweets.setVisibility(View.INVISIBLE);

                    }

                    //If the request is unsuccessful
                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                        //Log failure
                        Log.d(TAG,"Tweet not retweeted: "+response,throwable);
                    }
                });
            }
        });
    }
}