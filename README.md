# ColorTV Android TV/Amazon Fire TV SDK Demo App

This repository contains a demo application for the ColorTV SDK. It shows different ad formats and a proper way of implementing ColorTV SDK to an app. Use it as a reference to implement ColorTV SDK into your app. Below you can find a complete guide for integrating the SDK and using all of it's features.

>**NOTE**
>
>This tutorial assumes you integrate the SDK by a Gradle Maven dependency. If you'd rather download the `.aar` package, please refer to [this page](https://bintray.com/colortv/maven/android-sdk/view).

##Getting Started
Before getting started make sure you have: 

* Added your app in the My Applications section of the Color Dashboard. You need to do this so that you can get your App ID that you'll be adding to your app with our SDK.

* Make sure your Android Studio version is up to date and that your application is targeting `minSdkVersion:14`

>**NOTE**
>
>    Our SDK supports Android versions 21+, but for convenience in maintaining one app for multiple platforms we've lowered the `minSdkVersion` to 14. ColorTv SDK will not be initialized however on versions below 21.

##Adding Android TV/Amazon Fire TV SDK

For a demo of the correct integration, please refer to our [demo application](https://github.com/color-tv/android-DemoApp)

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
    compile 'com.colortv:android-sdk:2.1.0'
    compile 'com.google.android.gms:play-services-ads:9.6.1'
    compile 'com.android.support:recyclerview-v7:24.2.1'
}
```

Doing this prevents you from having to download our SDK and adding it manually to your project, as the aar file will handle that for you.

##Initializing the SDK

Import the SDK to your `MainActivity.java` file.

```java
import com.colortv.android.ColorTvSdk;
```

Setup the ColorTvSDK for your app by invoking `ColorTvSdk` initialization method below in your code. 

```java
ColorTvSdk.init(this, "your_app_id_from_dashboard");
```

Your app id is generated in the publisher dashboard after adding or editing an application in the My Applications section. Copy the app id and paste the value for "your_app_id_from_dashboard".

>**NOTE**
>
>    We recommend putting the initialization method inside either **Application.onCreate()** or **MainActivity.onCreate() **. The application must be initialized before invoking any functions of the SDK.

##Displaying Ads

Ads may be shown wherever you place them inside your app, but they **must** include a Placement parameter to indicate the specific location. Placements are used to optimize user experience and analytics. 

###Placements
Below are all predefined placement values: 

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

- VideoStart

- VideoPause

- VideoStop

- VideoEnd
 
>**NOTE**
>
>    You can choose what ad units you want to show for a specific placement in the dashboard, [click to learn more about Ad Units](index.md#ad-units)
   
>**NOTE**
>
>    The `AdPlacement` class has been deprecated and replaced by `Placements`. The old version will be removed in future versions of the SDK.
    
To get callbacks about the ad status, you need to create a `ColorTvAdListener` object by overriding it's methods:

```java
ColorTvAdListener listener = new ColorTvAdListener() {

    @Override
    public void onAdLoaded(String placement) {
        ColorTvSdk.showAd(placement);
    }

    @Override
    public void onAdError(String placement, ColorTvError colorTvError) {
    }

    @Override
    public void onAdClosed(String placement, boolean watched) {
    }

    @Override
    public void onAdExpired(String placement) {
    }
};
```

and register that listener to the SDK:

```java
ColorTvSdk.registerAdListener(listener);
```

>**NOTE**
>
>    The **onAdClosed** callback without the **watched** flag has been deprecated and will be removed in future versions of the SDK.

To load an ad for a certain placement, you need to call the following method:

```java
ColorTvSdk.loadAd(Placements.LEVEL_UP);
```

Use one of the predefined placements that you can find in `Placements` class, e.g. `Placements.LEVEL_UP`.

In order to show an ad, call the following function: 

```java
ColorTvSdk.showAd(Placements.LEVEL_UP);
```

Calling this method will show an ad for the placement you pass. Make sure you get the `onAdLoaded` callback first, otherwise the ad won't be played.

>**NOTE**
>
>    It is recommended to set up multiple placements inside your app to maximize monetization and improve user experience.

##Displaying Recommendation Center

>**NOTE**
>
>    Only for Content Providers

Displaying Recommendation Center is simillar to displaying ads. It may be shown wherever you place them inside your app, but you need to include a Placement parameter to indicate the specific location. Placements are used to optimize user experience and analytics. 

You can use the same Placements as are pointed in [Ads section](#placements).

To get callbacks about the content recommendation status, you need to create a ColorTvContentRecommendationListener object by overriding it's methods:

```java
ColorTvContentRecommendationListener listener = new ColorTvContentRecommendationListener() {

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
    public void onContentChosen(String videoId, String videoUrl) {
    }
};
```

and register that listener to the SDK:

```java
ColorTvSdk.registerContentRecommendationListener(listener);
```

>**NOTE**
>
>if you set up videoUrl as a deep link, then onContentChosen callback is invoked simultaneously to opening new activity with the deep link.

To load a Content Recommendation for a certain placement, you need to call one of the following methods:

```java
ColorTvSdk.loadContentRecommendation(Placements.LEVEL_UP, previousVideoId);

ColorTvSdk.loadContentRecommendation(Placements.LEVEL_UP);
```

Use one of the predefined placements that you can find in `Placements` class, e.g. `Placements.LEVEL_UP`.
If you display Recommendation Center after playing some video you should additionally provide id of this video to get a better recommendation.

In order to show Content Recommendation, call the following function: 

```java
ColorTvSdk.showContentRecommendation(Placements.LEVEL_UP);
```

Calling this method will show Content Recommendation for the placement you pass. Make sure you get the `onLoaded` callback first, otherwise the Content Recommendation won't be displayed.

>**NOTE**
>
>    It is recommended to set up multiple placements inside your app to maximize monetization and improve user experience.  

##Declaring Session

Creating a session is **necessary** for tracking virtual currency transactions and user sessions. Add the following code to every Activity file in your application e.g. `MainActivity.java`

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

##Earning Virtual Currency

A listener must be added in order to receive events when a virtual currency transaction has occurred. 

```java
private OnCurrencyEarnedListener listener = new OnCurrencyEarnedListener() {
    @Override
    public void onCurrencyEarned(String placement, int currencyAmount, String currencyType){

    }
};

...
  
ColorTvSdk.addOnCurrencyEarnedListener(listener);
```

Use the following function to unregister listeners:

```java
ColorTvSdk.removeOnCurrencyEarnedListener(listener);
```

Use the following function to cancel all listeners: 

```java
ColorTvSdk.clearOnCurrencyEarnedListeners();
```

>**NOTE**
>
>    Session must also be implemented for virtual currency transactions to function.

###Currency for user

In order to distribute currency to the same user but on other device, use below:
```java
ColorTvSdk.setUserId("user123");
```

##Video Tracking

>**NOTE**
>
>    Only for Content Providers

In order to provide additional data for ColorTv Analytics and to improve Content Recommendation, you can report events related to your videos.

###Tracking Events
Below are all predefined in `TrackingEventType` class tracking event values: 

- VIDEO_STARTED

- VIDEO_STOPPED

- VIDEO_COMPLETED

You can report them calling one of following methods:

```java
ColorTvSdk.reportVideoTrackingEvent(videoId, TrackingEventType.VIDEO_STOPPED, watchedSeconds);

ColorTvSdk.reportVideoTrackingEvent(videoId, TrackingEventType.VIDEO_STARTED);
```

`videoId` is an id which you have set in video feed provided in ColorTv dashboard.
`watchedSeconds` is a watched duration of video with given id.

You should use the first method only to report `VIDEO_STOPPED` event, or some custom events which you need to know the time they happened. Second method should be used to report `VIDEO_STARTED`, `VIDEO_COMPLETED` events, or some custom events which aren't related to moment in the video.

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
 
To improve ad targeting you can use the UserProfile class. To do so, create a new instance of this class:

```java
UserProfile user = new UserProfile(context);
```

You can set age, gender and some keywords as comma-separated values, eg. `sport,health` like so:

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
import com.colortv.android.Placements;
import com.colortv.android.ColorTvAdListener;
import com.colortv.android.ColorTvError;
import com.colortv.android.ColorTvSdk;
import com.colortv.android.OnCurrencyEarnedListener;

public class MainActivity extends Activity {

    private ColorTvAdListener adListener = new ColorTvAdListener() {

        @Override
        public void onAdLoaded(String placement) {
            ColorTvSdk.showAd(placement);
        }

        @Override
        public void onAdError(String placement, ColorTvError colorTvError) {

        }

        @Override
        public void onAdClosed(String placement) {
        }

        @Override
        public void onAdExpired(String placement) {
        }
    };
    
    private ColorTvContentRecommendationListener recommendationListener = new ColorTvContentRecommendationListener() {

        @Override
        public void onLoaded(String placement) {
            ColorTvSdk.showContentRecommendation(placement);
        }

        @Override
        public void onError(String placement, ColorTvError colorTvError) {
        }

        @Override
        public void onClosed(String placement) {
        }

        @Override
        public void onExpired(String placement) {
        }
        
        @Override
        public void onContentChosen(String videoId, String videoUrl) {
            //play video with videoId, kept under videoUrl
        }
    }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ColorTvSdk.setDebugMode(true);
        ColorTvSdk.init(this, "your_app_id");
        ColorTvSdk.registerAdListener(adListener);
        ColorTvSdk.registerContentRecommendationListener(recommendationListener);

        findViewById(R.id.btnShowAd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorTvSdk.loadAd(Placements.APP_LAUNCH);
            }
        });
        
        findViewById(R.id.btnShowContentRec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorTvSdk.loadContentRecommendation(Placements.VIDEO_START);
            }
        });

        ColorTvSdk.addOnCurrencyEarnedListener(new OnCurrencyEarnedListener() {
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
