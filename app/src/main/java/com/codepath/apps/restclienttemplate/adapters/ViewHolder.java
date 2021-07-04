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

public class ViewHolder extends RecyclerView.ViewHolder{



    Context context;
    TwitterClient client;
    ItemTweetBinding app;
    public ViewHolder(View view, ItemTweetBinding app, Context context, TweetsAdapter.OnClickListener clickListener){
        super(view);

        this.app = app;

        itemView.setOnClickListener(v -> clickListener.onItemClicked(getAdapterPosition()));
        this.context = context;
        client = TwitterApp.getRestClient(context);
    }

    public void bind(Tweet tweet){

        if(tweet.retweeter!=null){
            app.llRetweeted.setVisibility(View.VISIBLE);
            app.tvRetweeted.setText(String.format("%s Retweeted", tweet.retweeter.name));
        }
        else{
            app.llRetweeted.setVisibility(View.GONE);
        }

        app.tvBody.setText(tweet.body);
        app.tvHandle.setText(String.format("@%s", tweet.user.screenName));
        app.tvName.setText(tweet.user.name);
        app.tvTime.setText(Tweet.getRelativeTimeAgo(tweet.createdAt));
        if(tweet.likes>0){
            app.tvLikes.setText(Tweet.formatCount(tweet.likes));
            app.tvLikes.setVisibility(View.VISIBLE);
        }

        else
            app.tvLikes.setVisibility(View.INVISIBLE);

        if(tweet.retweets>0){
            app.tvRetweets.setText(Tweet.formatCount(tweet.retweets));
            app.tvRetweets.setVisibility(View.VISIBLE);
        }
        else
            app.tvRetweets.setVisibility(View.INVISIBLE);

        Glide.with(context).load(tweet.user.profileImageUrl)
                .transform(new RoundedCorners(100))
                .into(app.ivProfileImage);

        if(tweet.photo!=null){
            app.ivMedia.setVisibility(View.VISIBLE);
            Glide.with(context).load(tweet.photo.url)
                    .transform(new CenterCrop(), new RoundedCorners(80))
                    .into(app.ivMedia);

        }
        else{
            app.ivMedia.setVisibility(View.GONE);
        }

        if(tweet.liked)
            app.btnLike.setImageResource(R.drawable.ic_vector_heart);

        else
            app.btnLike.setImageResource(R.drawable.ic_vector_heart_stroke);

        if(tweet.retweeted)
            app.btnRetweet.setImageResource(R.drawable.ic_vector_retweet);
        else
            app.btnRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);

        app.btnLike.setOnClickListener(v -> {
            if(tweet.liked){
                client.unlikeTweet(tweet.id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.d("Unliked","true");
                        tweet.liked = false;
                        tweet.likes-=1;
                        app.btnLike.setImageResource(R.drawable.ic_vector_heart_stroke);
                        if(tweet.likes>0) {
                            app.tvLikes.setText(Tweet.formatCount(tweet.likes));
                            app.tvLikes.setVisibility(View.VISIBLE);
                        }
                        else
                            app.tvLikes.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.d("Unliked","false "+response);
                    }
                });
            }
            else{
                client.likeTweet(tweet.id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.d("Liked","true");
                        app.btnLike.setImageResource(R.drawable.ic_vector_heart);
                        tweet.likes+=1;
                        tweet.liked = true;
                        if(tweet.likes>0) {
                            app.tvLikes.setText(Tweet.formatCount(tweet.likes));
                            app.tvLikes.setVisibility(View.VISIBLE);
                        }
                        else
                            app.tvLikes.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.d("Liked","false "+response);
                    }
                });
            }

        });

        app.btnComment.setOnClickListener(v -> {
            FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putParcelable("Tweet", Parcels.wrap(tweet));
            ReplyFragment replyFragment = ReplyFragment.newInstance("Reply");
            replyFragment.setArguments(bundle);
            replyFragment.show(fm, "activity_reply");
        });

        app.btnRetweet.setOnClickListener(v -> {
            if(tweet.retweeted){
                client.unretweetTweet(tweet.id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.d("Unretweeted","true");
                        tweet.retweeted = false;
                        tweet.retweets-=1;
                        app.btnRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                        if(tweet.retweets>0) {
                            app.tvRetweets.setText(Tweet.formatCount(tweet.retweets));
                            app.tvRetweets.setVisibility(View.VISIBLE);
                        }
                        else
                            app.tvRetweets.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.d("Unretweeted","false "+response);
                    }
                });
            }
            else{
                client.retweetTweet(tweet.id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.d("Retweeted","true");
                        app.btnRetweet.setImageResource(R.drawable.ic_vector_retweet);
                        tweet.retweets+=1;
                        tweet.retweeted = true;
                        if(tweet.retweets>0) {
                            app.tvRetweets.setText(Tweet.formatCount(tweet.retweets));
                            app.tvRetweets.setVisibility(View.VISIBLE);
                        }
                        else
                            app.tvRetweets.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.d("Retweeted","false "+response);
                    }
                });
            }
        });

        app.ivProfileImage.setOnClickListener(v -> {
            Intent i = new Intent(context, ProfileActivity.class);
            i.putExtra("user",Parcels.wrap(tweet.user));
            context.startActivity(i);
        });
    }
}