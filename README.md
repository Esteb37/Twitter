# Project 2 - *Twitter*

This is a Twitter application clone for demonstrating the understanding of HTTP requests, recycler views, and POST and GET protocols.

Time spent: **20** hours spent in total

## User Stories

The following **required** functionality is completed:

* [x] User can sign in to Twitter using OAuth login
* [x] User can view the tweets from their home timeline eyes
* [x] User can Logout
* [x] User can compose a new tweet 
* [x] User can pull to refresh, view character count and embed images 

The following **stretch** features are implemented:

* [x] Improve the user interface and theme the app to feel "twitter branded" with colors and styles (1 to 5 points)
* [x] When any background or network task is happening, user sees an indeterminate progress indicator (1 point)
* [x] User can "reply" to any tweet from their home timeline (1 point)
	* [x] The user that wrote the original tweet is automatically "@" replied in compose
* [x] User can click on a tweet to be taken to a "detail view" of that tweet (2 points)
* [x] User can take favorite (and unfavorite) or reweet actions on a tweet
* [x] User can view more tweets as they scroll with Endless Scrolling. Number of tweets is unlimited. Refer to the endless scrolling conceptual guide for more details. (2 points)
* [x] Compose activity is replaced with a modal overlay (2 points)
* [x] Links in tweets are clickable and will launch the web browser (see autolink) (1 point)
* [x] Replace all icon drawables and other static image assets with vector drawables where appropriate. (1 point)
* [x] User can view following / followers list through any profile they view. (2 points)
* [x] Apply the View Binding library to reduce view boilerplate. (1 point)
* [ ] Experiment with fancy scrolling effects on the Twitter profile view. (2 points)
* [x] User can open the twitter app offline and see last loaded tweets persisted into SQLite (2 points

The following **additional** features are implemented:

* [x] A user's tweets and retweets are available at their profile
* [x] View information profile like banner and bio
* [x] View a tweet's replies in the Detail View
* [x] Added format to counters and dates (i.e. 3.2K instead of 3200)
* [x] The user can retweet and un retweet
* [x] The link to an embedded image is removed
* [x] Can persist up to 200 tweets and include all information like images and like counts
* [x] If a tweet is a retweet, show the original tweet with a banner above announcing who retweeted it

## Video Walkthrough

Here's a walkthrough of implemented user stories in portrait mode:

<img src='https://github.com/Esteb37/Twitter/blob/master/walkthrough.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />


## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Glide](https://github.com/bumptech/glide) - Image loading and caching library for Android

## License

    Copyright 2021 Esteban Padilla Cerdio

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
