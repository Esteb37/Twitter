package com.codepath.apps.restclienttemplate.client;

import android.content.Context;

import com.codepath.apps.restclienttemplate.BuildConfig;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance(); // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;       // Change this inside apikey.properties
	public static final String REST_CONSUMER_SECRET = BuildConfig.CONSUMER_SECRET; // Change this inside apikey.properties

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				null,  // OAuth2 scope, null for OAuth1
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}
	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	public void getHomeTimeline(String maxId,JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 20);
		params.put("max_id",maxId);
		params.put("exclude_replies",true);
		client.get(apiUrl, params, handler);
	}

	public void getHomeTimeline(JsonHttpResponseHandler handler){
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 20);
		params.put("since_id",1);
		params.put("exclude_replies",true);
		client.get(apiUrl, params, handler);
	}

	public void getProfileTimeline(String screenName,JsonHttpResponseHandler handler){
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 20);
		params.put("since_id",1);
		params.put("screen_name",screenName);
		params.put("exclude_replies",true);
		client.get(apiUrl, params, handler);
	}

	public void getProfileTimeline(String screenName,String maxId,JsonHttpResponseHandler handler){
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 20);
		params.put("max_id",maxId);
		params.put("screen_name",screenName);
		params.put("exclude_replies",true);
		client.get(apiUrl, params, handler);
	}

	public void publishTweet(String tweetContent, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("status", tweetContent);
		client.post(apiUrl, params, "",handler);
	}

	public void likeTweet(String id,JsonHttpResponseHandler handler){
		String apiUrl = getApiUrl("favorites/create.json");
		RequestParams params = new RequestParams();
		params.put("id",id);
		client.post(apiUrl,params,"",handler);
	}

	public void unlikeTweet(String id,JsonHttpResponseHandler handler){
		String apiUrl = getApiUrl("favorites/destroy.json");
		RequestParams params = new RequestParams();
		params.put("id",id);
		client.post(apiUrl,params,"",handler);
	}

	public void replyTweet(Tweet tweet, String replyContent, JsonHttpResponseHandler handler){
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", "@"+tweet.user.screenName+" "+replyContent);
		params.put("in_reply_to_status_id",tweet.id);
		client.post(apiUrl, params, "",handler);
	}

	public void retweetTweet(String id, JsonHttpResponseHandler handler){
		String apiUrl = getApiUrl("statuses/retweet.json");
		RequestParams params = new RequestParams();
		params.put("id",id);
		client.post(apiUrl,params,"",handler);
	}
	public void unretweetTweet(String id, JsonHttpResponseHandler handler){
		String apiUrl = getApiUrl("statuses/unretweet.json");
		RequestParams params = new RequestParams();
		params.put("id",id);
		client.post(apiUrl,params,"",handler);
	}

	public void getCurrentUser(JsonHttpResponseHandler handler){
		String apiUrl = getApiUrl("account/verify_credentials.json");
		client.get(apiUrl,null,handler);
	}

	public void getUserMentions(String screenName,JsonHttpResponseHandler handler){
		String apiUrl = getApiUrl("search/tweets.json");
		RequestParams params = new RequestParams();
		params.put("q","@"+screenName);
		params.put("count","100");
		client.get(apiUrl,params,handler);
	}

	public List<Tweet> parseReplies(JSONArray mentions,Tweet tweet) throws JSONException {
		List<Tweet> replies = new ArrayList<>();
		JSONObject mention;
		for(int i = 0;i<mentions.length();i++){
			mention	= (JSONObject) mentions.get(i);
			if(mention.getString("in_reply_to_status_id_str").equals(tweet.id)){
				replies.add(Tweet.fromJson(mention));
			}
		}
		return replies;
	}
}
