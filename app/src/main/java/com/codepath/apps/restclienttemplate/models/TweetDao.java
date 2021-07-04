package com.codepath.apps.restclienttemplate.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/*
    Data Access Object interface for managing timeline persistence
 */
@Dao
public interface TweetDao {

    //Query that gets all Tweet and User information,
    //connects tweet creators to the user database and
    //orders the result by date
    @Query("SELECT Tweet.*, User.*"+
            " FROM Tweet INNER JOIN User ON Tweet.userId = User.uid ORDER BY Tweet.createdAt DESC LIMIT 20")

    //Get recent tweets and their respective users
    List<TweetWithUser> recentItems();

    //Insert the tweets into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweets);

    //Insert the users into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(User... users);


}
