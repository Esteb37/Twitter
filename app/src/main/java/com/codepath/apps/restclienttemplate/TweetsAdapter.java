package com.codepath.apps.restclienttemplate;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    Context context;
    List<Tweet> tweets;
    TwitterClient client;

    //Listener for recyclerview scrolling
    public interface OnScrollListener { void onScroll(int position);}
    OnScrollListener scrollListener;

    public TweetsAdapter(Context context, List<Tweet> tweets, OnScrollListener scrollListener) {
        this.context = context;
        this.tweets = tweets;
        this.scrollListener = scrollListener;
    }



    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
        client = TwitterApp.getRestClient(context);
        if (scrollListener != null)  scrollListener.onScroll(position);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvName;
        TextView tvTime;
        ImageView ivMedia;
        ImageButton btnLike;
        ImageButton btnComment;
        ImageButton btnRetweet;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvHandle);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnRetweet = itemView.findViewById(R.id.btnRetweet);
        }

        public void bind(Tweet tweet){
            tvBody.setText(tweet.body);
            tvScreenName.setText(String.format("@%s", tweet.user.screenName));
            tvName.setText(tweet.user.name);
            tvTime.setText(Tweet.getRelativeTimeAgo(tweet.createdAt));
            Glide.with(context).load(tweet.user.profileImageUrl)
                    .into(ivProfileImage);

            if(tweet.photo!=null){
                ivMedia.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.photo.url)
                        .transform(new CenterCrop(), new RoundedCorners(30))
                        .into(ivMedia);

            }
            else{
                ivMedia.setVisibility(View.GONE);
            }

            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(tweet.liked){
                        client.unlikeTweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.d("Unliked","true");
                                tweet.liked = false;
                                btnLike.setImageResource(R.drawable.ic_vector_heart_stroke);
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
                                btnLike.setImageResource(R.drawable.ic_vector_heart);
                                tweet.liked = true;
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.d("Liked","false "+response);
                            }
                        });
                    }

                }
            });

            btnComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("Tweet",Parcels.wrap(tweet));
                    ReplyFragment replyFragment = ReplyFragment.newInstance("Reply");
                    replyFragment.setArguments(bundle);
                    replyFragment.show(fm, "activity_reply");
                }

            });
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }



}
