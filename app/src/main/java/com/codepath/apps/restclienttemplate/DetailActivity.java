package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.databinding.ActivityDetailBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding app;
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
        app.tvDSource.setText(Html.fromHtml(tweet.source));
        app.tvDDate.setText(tweet.date);
        app.tvDTime.setText(tweet.time);

        if(tweet.retweeter!=null){
            app.tvDRetweeted.setText(String.format("%s Retweeted", tweet.retweeter.name));
            app.llDRetweeted.setVisibility(View.VISIBLE);
        }
        else{
            app.llDRetweeted.setVisibility(View.GONE);
        }

        Glide.with(this).load(tweet.user.profileImageUrl)
                .transform(new FitCenter(), new RoundedCorners(100))
                .into(app.ivDProfilePicture);

    }
}