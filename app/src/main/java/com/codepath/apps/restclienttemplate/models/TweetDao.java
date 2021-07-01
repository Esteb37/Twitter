package com.codepath.apps.restclienttemplate.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.codepath.apps.restclienttemplate.TweetWithUser;

import java.util.List;

@Dao
public interface TweetDao {

    // @Query annotation requires knowing SQL syntax
    // See http://www.sqltutorial.org/
    @Query("SELECT Tweet.*, User.*"+
            " FROM Tweet INNER JOIN User ON Tweet.userId = User.uid ORDER BY Tweet.createdAt DESC LIMIT 20")
    List<TweetWithUser> recentItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweets);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(User... users);


}
