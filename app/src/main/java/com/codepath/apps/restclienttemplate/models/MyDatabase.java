package com.codepath.apps.restclienttemplate.models;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/*
    Database for handling timeline persistence and offline viewing
 */

//The database will be populated by Tweet and User objects
@Database(entities={Tweet.class, User.class}, version=4)
public abstract class MyDatabase extends RoomDatabase {

    //Get the Data Access Object
    public abstract TweetDao tweetDao();

    // Database name to be used
    public static final String NAME = "MyDataBase";
}
