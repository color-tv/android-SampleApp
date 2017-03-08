# ColorTV Android TV/Amazon Fire TV SDK Sample App

This repository contains a sample application for the ColorTV SDK. It shows different ad formats and a proper way of implementing ColorTV SDK to an app. Use it as a reference to implement ColorTV SDK into your app. Below you can find a complete guide for integrating the SDK and using all of it's features.

>**NOTE**
>
>This tutorial assumes you integrate the SDK by a Gradle Maven dependency. If you'd rather download the `.aar` package, please refer to [this page](https://bintray.com/colortv/maven/android-sdk/view).

##Getting Started
Before getting started make sure you have:

* Added your app in the My Applications section of the Color Dashboard. You need to do this so that you can get your App ID that you'll be adding to your app with our SDK.

* Make sure your Android Studio version is up to date and that your application is targeting `minSdkVersion:14`

##Adding Android TV/Amazon Fire TV SDK

###Connecting Your App

In your project's **build.gradle** make sure you are using ColorTV Bintray repository:

```groovy
repositories {
    maven {
        url  "http://colortv.bintray.com/maven"
    }
}
```

Then add the following dependencies in your app's **build.gradle** file in Android Studio:

```groovy
dependencies {
    compile 'com.colortv:android-sdk:3.4.1'
    compile 'com.google.android.gms:play-services-ads:10.2.0'
    compile 'com.google.android.gms:play-services-location:10.2.0' //optional
    compile 'com.android.support:recyclerview-v7:25.2.0'
}
```

>**NOTE**
>
>The play-services-location dependency is only required if you want to anonymously track user location for better ad targeting

Doing this prevents you from having to download our SDK and adding it manually to your project, as the aar file will handle that for you.

##Initializing the SDK

Setup the ColorTvSDK by invoking `ColorTvSdk` initialization method.

```java
ColorTvSdk.init(this, "your_app_id_from_dashboard");
```

Your app id is generated in the publisher dashboard after adding an application in the My Applications section. Copy the app id and paste the value for "your_app_id_from_dashboard".

>**NOTE**
>
>We recommend putting the initialization method inside **MainActivity.onCreate()**. The application must be initialized before invoking any functions of the SDK.

##Declaring Session

Creating a session is **necessary** for tracking user sessions and  virtual currency transactions. Add the following code to every Activity file in your application e.g. `MainActivity.java`

```java
// Start Session
@Override
protected void onCreate() {
  super.onCreate();
  ColorTvSdk.onCreate();
}

// End Session
@Override
protected void onDestroy() {
  super.onDestroy();
  ColorTvSdk.onDestroy();
}
```

##Placements

Placements are used to optimize user experience and analytics. Below are all predefined placement values used to indicate the specific location of Recommendation Center, UpNext and ads in your app:

- VideoStart
- VideoPause
- VideoStop
- VideoEnd
- AppLaunch
- AppResume
- AppClose
- MainMenu
- Pause
- StageOpen
- StageComplete
- StageFailed
- LevelUp
- BetweenLevels
- StoreOpen
- InAppPurchase
- AbandonInAppPurchase
- VirtualGoodPurchased
- UserHighScore
- OutofGoods
- OutofEnergy
- InsufficientCurrency
- FinishedTutorial

##Content Recommendations

>**NOTE**
>
>Only for Content Providers

In order to control Content Recommendations you need to retrieve **ColorTvRecommendationsController** object by calling `ColorTvSdk.getRecommendationsController()` method. This object is a singleton, which allows loading and showing both **Recommendation Center** and **UpNext**, listening to events or managing assets preloading.

###Registering recommendations listener

To get callbacks about the content recommendation status, you need to create a ColorTvContentRecommendationListener object by implementing it's methods:

```java
ColorTvContentRecommendationListener recommendationsListener = new ColorTvContentRecommendationListener() {

    @Override
    public void onLoaded(String placement) {
    }

    @Override
    public void onError(String placement, ColorTvError colorTvError) {
    }

    @Override
    public void onClosed(String placement, boolean watched) {
    }

    @Override
    public void onExpired(String placement) {
    }

    @Override
    public void onContentChosen(String videoId, String videoUrl, Map<String, String> videoParams) {
    }
};
```

>**NOTE**
>
>The `videoParams` parameter of `onContentChosen` method contains any key-value pairs that you can specify to be passed here for each video in the feed.

After you create the listener, get the `ColorTvRecommendationsController` object:

```java
ColorTvRecommendationsController recommendationsController = ColorTvSdk.getRecommendationsController();
```

and register that listener to the SDK:

```java
recommendationsController.registerListener(recommendationsListener);
```

>**NOTE**
>
>If you set up `videoUrl` as a deep link, then `onContentChosen` callback is invoked simultaneously to opening new activity with the deep link.

###Loading Content Recommendations

Before displaying recommendations in either **Recommendation Center** or **UpNext** you need to load recommendations related data from server. In order to do that you can invoke one of following methods:

```java
colorTvRecommendationsController.load(ColorTvPlacements.VIDEO_END);

colorTvRecommendationsController.load(ColorTvPlacements.VIDEO_END, previousVideoId);

colorTvRecommendationsController.loadOnlyUpNext(ColorTvPlacements.VIDEO_END);

colorTvRecommendationsController.loadOnlyUpNext(ColorTvPlacements.VIDEO_END, previousVideoId);
```

Use one of the predefined placements that you can find in `ColorTvPlacements` class, e.g. `ColorTvPlacements.VIDEO_END`.

Methods with `previousVideoId` parameter should be used when loading recommendations after playing a video in your app, as it allows to provide accurate recommendations. Video Id have to be the same as set in your feed shared with ColorTv. `load` methods can be used when using both **Recommendation Center** and **UpNext**. `loadOnlyUpNext` should be used when your intent is to use **UpNext** unit only, as it skips loading assets for **Recommendation Center**.

In case you would like to load recommendations just before showing them, you can decide to speed up the loading process by disabling assets prealoding. In this case you need to invoke the following method:

```java
colorTvRecommendationsController.setPreloadingAssets(false);
```

It is not a recommended action, however, as the video previews won't be played due to a decrease in performance.

###Showing Content Recommendations

You can show Content Recommendations in one of two visual forms: **Recommendation Center** and **UpNext**.

####Recommendation Center

Recommendation Center is a unit that lets you display recommendations in an Activity with scrollable grid layout form.

In order to show Recommendation Center, you have to call following method:

```java
colorTvRecommendationsController.showRecommendationCenter(ColorTvPlacements.VIDEO_END);
```

Invoking this method will show Recommendation Center for the placement you pass. You need to call the `load` method for a given placement before invoking `showRecommendationCenter` in order to load recommendations related data. Also make sure you got the `onLoaded` callback first, otherwise the Recommendation Center won't be displayed.

####UpNext

UpNext is a unit which diplays only the best recommendation in form of a small view designed to be placed over a video, which up next holds recommendation for. In order to keep the video playing we are delivering UpNext as a Fragment which you need to add to your layout. To make it work properly, you need to add a container to which you'll inject the UpNext fragment. This container have to be inside a RelativeLayout or a FrameLayout with the possibility to use the whole screen size in order to let it place itself in a proper place and size for various devices. The container should also have width set to `match_parent` and height set to `wrap_content`. For a sample of the correct layout, please refer to our [sample application's layout](https://github.com/color-tv/android-SampleApp/blob/master/SampleApp/app/src/main/res/layout/activity_exo.xml). In close future we are going to provide more possibilities to customize the UpNext layout and it's position.

In order to fetch UpNext fragment, you have to call following method:

```java
colorTvRecommendationsController.getUpNextFragment(Placements.VIDEO_END);
```

It should be previously loaded for a given placement with either `load` or `loadOnlyUpNext` methods. Unlike **Recommendation Center** the same **UpNext** can be displayed multiple times with the same server data as long as it isn't clicked. What is more when UpNext wasn't clicked it is possible to invoke `showContentRecommendation` method. It is impossible to display **Recommendation Center** when **UpNext** is added, but if you call the `showConetentRecomendation` method, **Recommendation Center** will be opened as soon as UpNext is closed without clicking it.

**UpNextFragment** provides a bunch of methods that allows you to manage **UpNext's** behaviour.

It is highly recommended to use the following method:

```java
upNextFragment.autoStart(getSupportFragmentManager(), R.id.flUpNextFragment, upNextStartBeforeVideoEndTime, shouldUseAutoPlay, new UpNextFragment.ColorTvVideoListener() {
                    @Override
                    public int getDurationInSeconds() {
                        return (int) TimeUnit.MILLISECONDS.toSeconds(videoView.getDuration());
                    }

                    @Override
                    public int getPositionInSeconds() {
                        return (int) TimeUnit.MILLISECONDS.toSeconds(videoView.getCurrentPosition());
                    }
                });
            }
```

It is designed to improve user experience provided by **UpNext** and lets you skip handling many cases by yourself. It automatically injects the fragment into the view container with given id, when video which it is related to is positioned at given amount of seconds before the end. It also removes **UpNext** when the video has been rewind to a position farther from video end than given in `secondsBeforeEnd`. It records all video pauses, for both buffering and controller invoked pause and it is pausing the auto play timer in such situations. When user forwards the video to a position closer than 3 seconds to the end, then auto play timer is set to `secondsBeforeEnd` value in order to give the user time to react to UpNext. Otherwise it is set to the amount of seconds remaining to the end of the video. When auto play countdown finishes, it automatically "clicks" an element invoking the `onContentChosen` callback. Position of the video is being read from `videoListener` which should return the current position of a video and its whole duration.

If auto start is no longer required it can also be canceled with the following method:

```java
upNextFragment.cancelAutoStart();
```

Auto playing can be disabled by `shouldUseAutoPlay` flag. Then it either waits for a click from the user, or can be handled with other methods such as:

```java
upNextFragment.setAutoPlayTimer(lengthInMillis, shouldStartAutomatically);

upNextFragment.startAutoPlayTimer();

upNextFragment.stopAutoPlayTimer();

upNextFragment.invokeClick();

upNextFragment.cancel();
```

These methods allow you to configure custom behaviour such as clicking **UpNext** at the video end, initiating auto play then; just clicking recommendation at video end without any timer; cancelling (removing) **UpNext** on some custom action, and many others.

`invokeClick` and `cancel` methods can be safely used when `autoStart` is used with `shouldUseAutoPlay`, although it is not recommended to use `setAutoPlayTimer`, `startAutoPlayTimer` and `stopAutoPlayTimer` in that case, as it may cause some unexpected behaviour.

#####UpNext's specific actions for TVs

Android TV requires focus handling and controlling with key events. It is why we've provided following methods:

```java
upNextFragment.requestFocus();

upNextFragment.dispatchKeyEvent();
```

By default UpNext requests focus when created, but in case of a necessity to change it, it is possible to gain focus back by calling `requestFocus` method.

There is no need to invoke `dispatchKeyEvent`, as the default behaviour based on focus with usage of `autoStart` works, although you may find it useful in your specific case. You need to add it to your activity's `dispatchKeyEvent` method. It dispatches `KEYCODE_DPAD_CENTER` and `KEYCODE_MEDIA_PLAY_PAUSE` invoking click on **UpNext**, and for other events removes the **UpNext** fragment. It doesn't dispatch `KEYCODE_BACK` and when **UpNext** is destroyed. It dispatches only `ACTION_DOWN` events. It is designed especially for media players that don't work properly on AndroidTv when there is some other view which is focused.

##Video Tracking

>**NOTE**
>
>Only for Content Providers

In order to provide additional data for ColorTv Analytics and to improve Content Recommendation, you can report events related to your videos.

###Tracking Events

Below are all the tracking event values predefined in `ColorTvTrackingEventType` class:

- VIDEO_STARTED
- VIDEO_PAUSED
- VIDEO_STOPPED
- VIDEO_RESUMED
- VIDEO_COMPLETED

First, get the `ColorTvVideoTrackingController`:
```java
ColorTvVideoTrackingController videoTrackingController = ColorTvSdk.getVideoTrackingController();
```

You can report them by calling the following method:

```java
videoTrackingController.reportVideoTrackingEvent(videoId, ColorTvTrackingEventType.VIDEO_STOPPED, positionSeconds);
```

`videoId` is an id that you have set in your feed.
`positionSeconds` is a postition at which the given event occur.

To report fast-forwarding or rewinding through the video, use `VIDEO_PAUSED` at the start and `VIDEO_RESUMED` at the end of the process.

If you are using ExoPlayer you can track your video events by calling the following method:

```java
videoTrackingController.setExoPlayerToTrackVideo(exoPlayer);
```

`exoPlayer` is your ExoPlayer instance.

If you are launching a video without using ColorTv Content Recommendation Center then you need to also call:

```java
videoTrackingController.setVideoIdForPlayerTracking(videoId);
```

with id of launched video that is set in your feed. In case you are using ColorTv Content Recommendation, the video id will be automatically taken from the chosen recommendation.

##Ads

>**NOTE**
>
>Ads are provided only for AndroidTv devices.

Displaying ads is simillar to displaying Recommendation Center. They may be shown wherever you place them inside your app, but you need to include a Placement parameter to indicate the specific location.

You can use the same Placements as are pointed in [Placements section](#placements).

>**NOTE**
>
>You can choose what ad units you want to show for a specific placement in the dashboard, [click to learn more about Ad Units](index.md#ad-units)

To get callbacks about the ad status, you need to create a `ColorTvAdListener` object by overriding it's methods:

```java
ColorTvAdListener adListener = new ColorTvAdListener() {

        @Override
        public void onClosed(String placement, boolean watched) {
        }

        @Override
        public void onLoaded(String placement) {
        }

        @Override
        public void onError(String placement, ColorTvError error) {
        }

        @Override
        public void onExpired(String placement) {
        }
};
```

After you create the listener, get the `ColorTvAdController` object:

```java
ColorTvAdController adController = ColorTvSdk.getAdController();
```

and register that listener to the SDK:

```java
adController.registerListener(adListener);
```

To load an ad for a certain placement, you need to call the following method:

```java
adController.load(ColorTvPlacements.LEVEL_UP);
```

Use one of the predefined placements that you can find in `ColorTvPlacements` class, e.g. `ColorTvPlacements.LEVEL_UP`.

In order to show an ad, call the following function:

```java
adController.show(ColorTvPlacements.LEVEL_UP);
```

Calling this method will show an ad for the placement you pass. Make sure you get the `onLoaded` callback first, otherwise the ad won't be played.

>**NOTE**
>
>It is recommended to set up multiple placements inside your app to maximize monetization and improve user experience.  

##Earning Virtual Currency

A listener must be added in order to receive events when a virtual currency transaction has occurred.

```java
private OnCurrencyEarnedListener listener = new OnCurrencyEarnedListener() {
    @Override
    public void onCurrencyEarned(String placement, int currencyAmount, String currencyType){

    }
};

...

adController.addOnCurrencyEarnedListener(listener);
```

Use the following function to unregister listeners:

```java
adController.removeOnCurrencyEarnedListener(listener);
```

Use the following function to cancel all listeners:

```java
adController.clearOnCurrencyEarnedListeners();
```

>**NOTE**
>
>Session must also be implemented for virtual currency transactions to function.

###Currency for user

In order to distribute currency to the same user but on other device, use below:
```java
ColorTvSdk.setUserId("user123");
```

##INSTALL_REFERRER Conflict

If any of your `BroadcastReceiver` class declared in `AndroidManifest.xml` contains Intent Action `INSTALL_REFERRER`:

```xml
<receiver ...>
  <intent-filter>
    <action android:name="com.android.vending.INSTALL_REFERRER"/>
  </intent-filter>
</receiver>
```

Add the following code to your `AndroidManifest.xml` file:

```xml
<receiver android:name="com.colortv.android.ColorTvBroadcastReceiver">
```

In your BroadcastReceiver that handles action **com.android.vending.INSTALL_REFERRER**, add Java code:

```java
if (intent.getAction().equals("com.android.vending.INSTALL_REFERRER")) {
  final String referrer = intent.getStringExtra("referrer");
  ColorTvSdk.registerReferrer(context, referrer);
}
```

##User profile

To improve ad targeting you can use the `ColorTvUserProfile` class. To do so, get the reference to this class:

```java
ColorTvUserProfile user = ColorTvSdk.getUserProfile();
```

You can set age, gender and some keywords as comma-separated values like so:

```java
user.setAge(24);
user.setGender(UserProfile.Gender.FEMALE);
user.setKeywords("sport,health");
```

These values will automatically be saved and attached to an ad request.

##Disabling voice input on phone fields

If you don't want to use the voice input functionality add the following line to your manifest:

```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" tools:node="remove" />
```

and call the following method after the `ColorTvSdk.init()`:

```java
ColorTvSdk.setRecordAudioEnabled(false);
```

##Summary

After completing all previous steps your Activity could look like this:

```java
import com.colortv.android.api.ColorTvPlacements;
import com.colortv.android.api.ColorTvSdk;
import com.colortv.android.api.listener.ColorTvAdListener;
import com.colortv.android.api.listener.ColorTvContentRecommendationListener;
import com.colortv.android.api.ColorTvError;
import com.colortv.android.api.listener.ColorTvOnCurrencyEarnedListener;
import com.colortv.android.api.controller.ColorTvAdController;
import com.colortv.android.api.controller.ColorTvRecommendationsController;

public class MainActivity extends Activity {

    private ColorTvAdController adController;
    private ColorTvRecommendationsController recommendationsController;

    private ColorTvAdListener adListener = new ColorTvAdListener() {

        @Override
        public void onClosed(String placement, boolean watched) {
        }

        @Override
        public void onLoaded(String placement) {
            adController.show(placement);
        }

        @Override
        public void onError(String placement, ColorTvError colorTvError) {
        }

        @Override
        public void onExpired(String placement) {
        }
    };

    private ColorTvContentRecommendationListener recommendationListener = new ColorTvContentRecommendationListener() {

        @Override
        public void onLoaded(String placement) {
            recommendationsController.showRecommendationCenter(placement);
        }

        @Override
        public void onError(String placement, ColorTvError colorTvError) {
        }

        @Override
        public void onClosed(String placement, boolean watched) {
        }

        @Override
        public void onExpired(String placement) {
        }

        @Override
        public void onContentChosen(String videoId, String videoUrl, Map<String, String> videoParams) {
            //play video with videoId, kept under videoUrl
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ColorTvSdk.init(this, "your_app_id");

        recommendationsController = ColorTvSdk.getRecommendationsController();
        recommendationsController.registerListener(recommendationListener);

        findViewById(R.id.btnShowContentRec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recommendationsController.load(ColorTvPlacements.VIDEO_START);
            }
        });

        adController = ColorTvSdk.getAdController();
        adController.registerListener(adListener);

        findViewById(R.id.btnShowAd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adController.load(ColorTvPlacements.APP_LAUNCH);
            }
        });

        adController.addOnCurrencyEarnedListener(new OnCurrencyEarnedListener() {
            @Override
            public void onCurrencyEarned(String placement, int currencyAmount, String currencyType) {
                Toast.makeText(MainActivity.this, "Received " + currencyAmount + " " + currencyType, Toast.LENGTH_LONG).show();
            }
        });

        ColorTvSdk.onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ColorTvSdk.onDestroy();
        ColorTvSdk.clearOnCurrencyEarnedListeners();
    }
}
```

##Customizable Recommendation Center

If you want to customize `Recommendation Center` we have added `ColorTvRecommendationConfig` (accessible from `ColorTvRecommendationController`) which provides methods that allow you to do it.

Layouts containing RecyclerView and items are fully customizable. You can change one, both or use the default.
Process of designing is no different from the usual layout creation. You can use all types of Views or Layouts, there are no limits.

>**NOTE**
>
>Once the layout is set, it is stored in the config. If you want to reset the layout to the default one, invoke the resetToDefault() method.

###ColorTvRecommendationConfig

Available methods

```java
setGridLayout(Device device, @LayoutRes int layoutResId)
setItemLayout(Device device, @LayoutRes int layoutResId)
setRowCount(Device device, int rowCount)
setFont(Device device, Typeface typeface)
setSnapEnabled(boolean enabled)
resetToDefault()
```

All methods (except for `setSnapEnabled` - only mobile) are applicable for TV, mobile and tablet devices.


####setGridLayout(Device device, @LayoutRes int layoutResId)

This method is used to set custom grid layout for specified device type. Grid layout can contain following views. Moreover you can add additional images, texts etc. We will handle the following views if they're available:

| ID                              | View type  | Animation    | Description                                     | Device |
|---------------------------------|------------|--------------|-------------------------------------------------|--------|
| ctv_featuredUnitLayoutContainer | Any layout |              | Used to inject featured content if available               | TV     |
| ctv_recyclerViewContainer       | Any layout |              | Used to inject RecylerView                               | ALL    |
| ctv_ivFavoriteContainer         | Any layout | Visibility.VISIBLE and scale | Appears on play/pause button click if available | ALL    |
| ctv_ivGridClose                 | ImageView  | Scale        |                                                 | TV     |
| ctv_layoutSubscriptionFragment  | Any layout |              | Discovery center only                          | TV     |

####setItemLayout(Device device, @LayoutRes int layoutResId)

This method is used to set custom item layout for specified device type. You can add additional images, texts etc. All views are animated if they contain selectors with default state and state_selected.

| ID                         | VIEW TYPE    | ANIMATION (on focus)                | DESCRIPTION                                               | DEVICE |
|----------------------------|--------------|-------------------------------------|-----------------------------------------------------------|--------|
| ctv_hide                   | Any layout   | Visibility.GONE                     | Everything inside is hidden on focus                           | ALL    |
| ctv_show                   | Any layout   | Visibility.VISIBLE                  | Everything inside is shown on focus                           | ALL    |
| ctv_autoPlayTimerContainer | Any layout   |                                     | Used to inject countdown timer to autoplay the first recommended video    | ALL    |
| ctv_videoPreviewContainer  | Any layout   |                                     | Play video preview. Automatically hides thumbnail on focus | ALL    |
| ctv_ivVideoThumbnail       | ImageView    | Visibility.GONE                     | Display video thumbnail                                   | ALL    |
| ctv_ivAppLogo              | ImageView    |                                     | Display logo                                              | ALL    |
| ctv_ivPlayButton           | ImageView    | Visibility.GONE (on mobile - scale) |                                                           | ALL    |
| ctv_ivBlackMask            | ImageView    | Visibility.GONE (on mobile - fade)  |                                                           | ALL    |
| ctv_tvTitle                | TextView     |                                     | Display title                                             | ALL    |
| ctv_tvDescription          | TextView     |                                     | Display description                                       | ALL    |
| ctv_tvDuration             | TextView     |                                     | Display duration                                          | ALL    |
| ctv_tvGenre                | TextView     |                                     | Define how each genre should look like. Visibility must be GONE                   | ALL    |
| ctv_llGenres               | LinearLayout |                                     | Each ctv_tvGenre will be added here                       | ALL    |

####setRowCount(Device device, int rowCount)

This method is used to set the number of rows for specified device type.

####setFont(Device device, Typeface typeface)

This method is used to set a custom font for specified device type. It only works if you don't use custom item layout.

####setSnapEnabled(boolean enabled)

This method is used to set snapping RecyclerView elements to the center of the screen. Available only on Mobile. The value is forced to false if item layout is not default.

- true - the height of the items is forced to `MATCH_PARENT` and the width is calculated by the following formula: `ITEM_HEIGHT*0.45`. If you change the height of the RecyclerView in the custom grid layout we recommend you to disable snapping. Video preview is enabled and will be shown if available
- false - the height and the width of the items is set to be as defined in layout. Video preview is disabled and will not be shown whether it is available or not

####resetToDefault()

This method is used to reset all settings to default.


For an example of the usage all of the above methods, check our sample app: [Usage of custom recommendation center](https://github.com/color-tv/android-SampleApp/blob/master/SampleApp/app/src/main/java/com/colortv/demo/CustomRecommendationCenterActivity.java)
