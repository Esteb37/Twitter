package com.codepath.apps.restclienttemplate;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    public interface OnClickListener { void onItemClicked(int position);}
    OnClickListener clickListener;

    public TweetsAdapter(Context context, List<Tweet> tweets, OnScrollListener scrollListener, OnClickListener clickListener) {
        this.context = context;
        this.tweets = tweets;
        this.scrollListener = scrollListener;
        this.clickListener = clickListener;
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
        TextView tvLikes;
        TextView tvRetweets;
        TextView tvComments;
        ImageView ivMedia;
        ImageButton btnLike;
        ImageButton btnComment;
        ImageButton btnRetweet;
        TextView tvRetweeted;
        LinearLayout llRetweeted;

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
            llRetweeted = itemView.findViewById(R.id.llRetweeted);
            tvRetweeted = itemView.findViewById(R.id.tvRetweeted);
            tvComments = itemView.findViewById(R.id.tvComments);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvRetweets = itemView.findViewById(R.id.tvRetweets);
            itemView.setOnClickListener(v -> clickListener.onItemClicked(getAdapterPosition()));
        }

        public void bind(Tweet tweet){

            if(tweet.retweeter!=null){
                llRetweeted.setVisibility(View.VISIBLE);
                tvRetweeted.setText(String.format("%s Retweeted", tweet.retweeter.name));
            }
            else{
                llRetweeted.setVisibility(View.GONE);
            }

            tvBody.setText(tweet.body);
            tvScreenName.setText(String.format("@%s", tweet.user.screenName));
            tvName.setText(tweet.user.name);
            tvTime.setText(Tweet.getRelativeTimeAgo(tweet.createdAt));
            if(tweet.likes>0){
                tvLikes.setText(Tweet.formatCount(tweet.likes));
                tvLikes.setVisibility(View.VISIBLE);
            }

            else
                tvLikes.setVisibility(View.INVISIBLE);

            if(tweet.retweets>0){
                tvRetweets.setText(Tweet.formatCount(tweet.retweets));
                tvRetweets.setVisibility(View.VISIBLE);
            }
            else
                tvRetweets.setVisibility(View.INVISIBLE);

            Glide.with(context).load(tweet.user.profileImageUrl)
                    .transform(new RoundedCorners(100))
                    .into(ivProfileImage);

            if(tweet.photo!=null){
                ivMedia.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.photo.url)
                        .transform(new CenterCrop(), new RoundedCorners(80))
                        .into(ivMedia);

            }
            else{
                ivMedia.setVisibility(View.GONE);
            }

            if(tweet.liked)
                btnLike.setImageResource(R.drawable.ic_vector_heart);

            else
                btnLike.setImageResource(R.drawable.ic_vector_heart_stroke);

            if(tweet.retweeted)
                btnRetweet.setImageResource(R.drawable.ic_vector_retweet);
            else
                btnRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);

            btnLike.setOnClickListener(v -> {
                if(tweet.liked){
                    client.unlikeTweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d("Unliked","true");
                            tweet.liked = false;
                            tweet.likes-=1;
                            btnLike.setImageResource(R.drawable.ic_vector_heart_stroke);
                            if(tweet.likes>0) {
                                tvLikes.setText(Tweet.formatCount(tweet.likes));
                                tvLikes.setVisibility(View.VISIBLE);
                            }
                            else
                                tvLikes.setVisibility(View.INVISIBLE);

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
                            tweet.likes+=1;
                            tweet.liked = true;
                            if(tweet.likes>0) {
                                tvLikes.setText(Tweet.formatCount(tweet.likes));
                                tvLikes.setVisibility(View.VISIBLE);
                            }
                            else
                                tvLikes.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d("Liked","false "+response);
                        }
                    });
                }

            });

            btnComment.setOnClickListener(v -> {
                FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putParcelable("Tweet",Parcels.wrap(tweet));
                ReplyFragment replyFragment = ReplyFragment.newInstance("Reply");
                replyFragment.setArguments(bundle);
                replyFragment.show(fm, "activity_reply");
            });

            btnRetweet.setOnClickListener(v -> {
                if(tweet.retweeted){
                    client.unretweetTweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d("Unretweeted","true");
                            tweet.retweeted = false;
                            tweet.retweets-=1;
                            btnRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                            if(tweet.retweets>0) {
                                tvRetweets.setText(Tweet.formatCount(tweet.retweets));
                                tvRetweets.setVisibility(View.VISIBLE);
                            }
                            else
                                tvRetweets.setVisibility(View.INVISIBLE);

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
                            btnRetweet.setImageResource(R.drawable.ic_vector_retweet);
                            tweet.retweets+=1;
                            tweet.retweeted = true;
                            if(tweet.retweets>0) {
                                tvRetweets.setText(Tweet.formatCount(tweet.retweets));
                                tvRetweets.setVisibility(View.VISIBLE);
                            }
                            else
                                tvRetweets.setVisibility(View.INVISIBLE);

                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d("Retweeted","false "+response);
                        }
                    });
                }
            });

            ivProfileImage.setOnClickListener(v -> {
                Intent i = new Intent(context,ProfileActivity.class);
                i.putExtra("user",Parcels.wrap(tweet.user));
                context.startActivity(i);
            });
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }



}
