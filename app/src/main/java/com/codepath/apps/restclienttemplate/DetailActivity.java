package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.databinding.ActivityDetailBinding;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding app;
    Context context;
    Tweet tweet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        app = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        app.tvDName.setText(tweet.user.name);
        app.tvDScreenName.setText(tweet.user.screenName);
        app.tvDContent.setText(tweet.body);
        app.tvDLikes.setText(String.valueOf(tweet.likes));
        app.tvDRetweets.setText(String.valueOf(tweet.retweets));
        app.tvDSource.setText(tweet.source);

        Glide.with(this).load(tweet.user.profileImageUrl)
                .transform(new FitCenter(), new RoundedCorners(100))
                .into(app.ivDProfilePicture);

    }
}