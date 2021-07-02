package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/*
    Class for handling embedded images
 */
@Parcel
public class Media {

    //HTTP Url
    public String url;

    //t.co URL
    public String tinyUrl;

    Media(){}

    /*
        Constructor from a JSON Object with image information

        @param jsonObject - The object with the information

        @return none
     */
    Media(JSONObject jsonObject) throws JSONException {

        //Get HTTP URL
        url = jsonObject.getString("media_url_https");

        //Get t.co URL
        tinyUrl = jsonObject.getString("url");
    }

}
