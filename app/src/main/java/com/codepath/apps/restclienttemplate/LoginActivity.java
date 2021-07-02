package com.codepath.apps.restclienttemplate;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.ActionBar;

import com.codepath.oauth.OAuthLoginActionBarActivity;

import java.util.Objects;

/*
	Screen for logging into the application
 */
public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

	/*
        Sets up the screen's items

        @param savedInstanceState - Current state of the screen

        @return void
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		//Set screen's current state
		super.onCreate(savedInstanceState);

		//Set the activity's layout xml
		setContentView(R.layout.activity_login);

		//Set the action bar to a custom layout
		Objects.requireNonNull(this.getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setCustomView(R.layout.custom_action_bar);
	}


	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
		Log.d("LoginTest","success");
		Intent i = new Intent(this, TimelineActivity.class);
		startActivity(i);
	}

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to login
	public void loginToRest(View view) {
		getClient().connect();
	}

}
