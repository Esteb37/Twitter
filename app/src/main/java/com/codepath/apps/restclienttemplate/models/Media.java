package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Media {

    public String url;
    public String tinyUrl;

    Media(){}

    Media(JSONObject jsonObject) throws JSONException {
        url = jsonObject.getString("media_url_https");
        tinyUrl = jsonObject.getString("url");
    }

}
