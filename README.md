# ColorTV SDK Demo App

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

##Adding Android TV/Fire TV SDK

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
    compile 'com.colortv:android-sdk:1.2.6'
    compile 'com.google.android.gms:play-services-ads:8.4.0'
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
Below are all the possible placement values: 

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
 
>**NOTE**
>
>    You can choose what ad units you want to show for a specific placement in the dashboard, [click to learn more about Ad Units](https://www.colortv.com/dashboard/docs/#ad-units)

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
        public void onAdClosed(String placement) {
    }
};
```

and register that listener to the SDK:

```java
ColorTvSdk.registerAdListener(listener);
```

To load an ad for a certain placement, you need to call the following method:

```java
ColorTvSdk.loadAd("chosen_placement");
```

It is recommended that you use one of the predefined placements that you can find in `AdPlacement` class, e.g. `AdPlacement.LEVEL_UP`,  You can also use a custom placement.

In order to show an ad, call the following function: 

```java
ColorTvSdk.showAd("chosen_placement");
```

Calling this method will show an ad for the placement you pass. Make sure you get the `adLoaded` callback first, otherwise the ad won't be played.

>**NOTE**
>
>   It is recommended to set up multiple placements inside your app to maximize monetization and improve user experience.


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
    public void onCurrencyEarned(int currencyAmount, String currencyType){

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

>**Reminder!**
>
>    Session must also be implemented for virtual currency transactions to function.

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
<receiver android:name="com.replay.android.ColorTvBroadcastReceiver">
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

##Summary

After completing all previous steps your Activity could look like this:

```java
import com.colortv.android.AdPlacement;
import com.colortv.android.ColorTvAdListener;
import com.colortv.android.ColorTvError;
import com.colortv.android.ColorTvSdk;
import com.colortv.android.OnCurrencyEarnedListener;

public class MainActivity extends Activity {

    private ColorTvAdListener listener = new ColorTvAdListener() {

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
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ColorTvSdk.setDebugMode(true);
        ColorTvSdk.init(this, "your_app_id");
        ColorTvSdk.registerOnAdStatusChangedListener(listener);

        findViewById(R.id.btnShowAd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorTvSdk.loadAd(AdPlacement.APP_LAUNCH);
            }
        });

        ColorTvSdk.addOnCurrencyEarnedListener(new OnCurrencyEarnedListener() {
            @Override
            public void onCurrencyEarned(int currencyAmount, String currencyType) {
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
