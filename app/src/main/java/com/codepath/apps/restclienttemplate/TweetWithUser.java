package com.codepath.apps.restclienttemplate;

import androidx.room.Embedded;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;

import java.util.ArrayList;
import java.util.List;

public class TweetWithUser {

    @Embedded
    public User user;

    @Embedded
    public Tweet tweet;


    public static List<Tweet> getTweetList(List<TweetWithUser> tweetWithUsers) {
        List<Tweet> tweets = new ArrayList<>();
        for(int i = 0;i<tweetWithUsers.size();i++){
            Tweet tweet = tweetWithUsers.get(i).tweet;
            tweet.user = tweetWithUsers.get(i).user;
            tweets.add(tweet);
        }
        return tweets;
    }
}
