package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/*
    The adapter for managing timeline recyclerviews
 */
public class TweetsAdapter extends RecyclerView.Adapter<ViewHolder>{

    //Current context
    Context context;

    //List of tweets to display
    List<Tweet> tweets;

    //Listener for recyclerview item click
    public interface OnClickListener {

        //Return clicked position
        void onItemClicked(int position);
    }
    OnScrollListener scrollListener;

    public interface OnScrollListener {

        //Return the current position
        void onScroll(int position);
    }
    OnClickListener clickListener;


    //Binder for viewbinding tor educe boilerplate code
    ItemTweetBinding app;

    /*
        Constructor from a context, a list of tweets, a scroll listener and a click listener

        @param context - The activity from which the adapter is being called
        @param tweets - The list of tweets to display
        @param scrollListener - The listener for the user's scrolling position
        @param clickListener - The listener for the clicked item

        @return none

     */
    public TweetsAdapter(Context context, List<Tweet> tweets, OnScrollListener scrollListener, OnClickListener clickListener) {
        this.context = context;
        this.tweets = tweets;
        this.scrollListener = scrollListener;
        this.clickListener = clickListener;
    }


    /*
        Inflates the items to be displayed from the layout file

        @param parent - The parent ViewGroup object
        @int viewType - The type of view to be inflated

        @return ViewHolder - the inflated ViewHolder with the item view
     */
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        //Inflate the item view from the layout file
        app = ItemTweetBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        View view = app.getRoot();

        //Return a ViewHolder with the item view
        return new ViewHolder(view,app,context,clickListener);
    }


    /*

     */
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
        if (scrollListener != null)  scrollListener.onScroll(position);
    }

    /*
        Gets the amount of items in the tweet list

        @param none

        @return int - the amount of tweets
     */
    @Override
    public int getItemCount() {
        return tweets.size();
    }


    /*
        Cleans all elements of the recycler and updates the adapter

        @param none

        @return void
     */
    public void clear() {

        //Clean the list of tweets
        tweets.clear();

        //Update the adapter
        notifyDataSetChanged();
    }



}
