package com.codepath.apps.restclienttemplate;

import androidx.room.Embedded;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;

import java.util.ArrayList;
import java.util.List;

/*
    Class for joining a Tweet with its publisher User
 */
public class TweetWithUser {

    //The tweet's publisher
    @Embedded
    public User user;

    //The tweet
    @Embedded
    public Tweet tweet;

    /*
        Returns a list of tweets from a list of tweetWithUser objects

        @param tweetWithUsers - The list of tweetWithUser objects

        @return List<Tweet> - A list of tweets
        
     */
    public static List<Tweet> getTweetList(List<TweetWithUser> tweetWithUsers) {

        //Create an array of tweets
        List<Tweet> tweets = new ArrayList<>();

        //For each tweetWithUser object
        for(int i = 0;i<tweetWithUsers.size();i++){

            //Get the tweet
            Tweet tweet = tweetWithUsers.get(i).tweet;

            //Set the tweet's user to be the object's user
            tweet.user = tweetWithUsers.get(i).user;

            //Add tweet to list
            tweets.add(tweet);
        }

        //Return list of tweets
        return tweets;
    }
}
