# Adikteev Android AppLovin MAX Adapter

Adikteev AppLovin MAX Adapter is a solution belonging to Adikteev. It provides android adapter to connect AppLovin SDK to Adikteev SDK.

First you need to integrate the AppLovin SDK then you can use our adapter to display a cross promoted ad.

## Requirements

Create an AppLovin account [here](https://dash.applovin.com/)

Create a custom network (type : SDK; Android / Fire OS Adapter Class Name: `com.YOUR_PACKAGE.AdikteevNetworkMediationAdapter`)

> Example with our package: com.adikteev.mediation.adapters.AdikteevNetworkMediationAdapter

Create your own Ad Unit for the wanted format (interstitial or rewarded) [here](https://dash.applovin.com/o/mediation/ad_units).

For testing purposes, you can add your device GAID to test mode devices [here](https://dash.applovin.com/o/mediation/test_modes/).

> Note : Modification on your AppLovin account is not real time, there may have up to 1 hour of delay.

## 1. Integrate AppLovin SDK

For a full guide refer to the Applovin integration documentation for android available [here](https://dash.applovin.com/documentation/mediation/android/getting-started/integration).

### Configuration

#### App-level build.gradle

Add the dependency to you app-level `build.gradle`.

```groovy
implementation 'com.applovin:applovin-sdk:+'
```

Add the repositories part only if it's not already in your `settings.gradle`.

Add the requirements (with last version indicating in the documentation [here](https://developers.google.com/android/guides/setup):

```groovy
implementation 'com.google.android.gms:play-services-ads-identifier:18.0.1'
```

Add the review service.

```groovy
apply plugin: 'applovin-quality-service'
applovin {
    apiKey "YOUR_AD_REVIEW_KEY_HERE"
}
```

> Note : If you are logged into your AppLovin account and copy this line from the documentation, you should directly see your key instead of "YOUR_AD_REVIEW_KEY_HERE".

#### Manifest

Add your SDK key to your `AndroidManifest.xml`, inside the application element:

```html
<meta-data android:name="applovin.sdk.key" android:value="YOUR_SDK_KEY_HERE" />
```

> Note : If you are logged into your AppLovin account and copy this line from the documentation, you should directly see your key instead of "YOUR_SDK_KEY_HERE".

#### App-level build.gradle

Add dependencies to your root level build.graddle (before the pluggins).

```groovy
buildscript {
    repositories {
        maven { url 'https://artifacts.applovin.com/android' }
    }
    dependencies {
        classpath "com.applovin.quality:AppLovinQualityServiceGradlePlugin:+"
    }
}
```

### Initialize the SDK

Follow AppLovin documentation for initializing the SDK:

```kotlin
class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Make sure to set the mediation provider value to "max" to ensure proper functionality
        AppLovinSdk.getInstance( context ).setMediationProvider( "max" )
        AppLovinSdk.getInstance( context ).initializeSdk({ configuration: AppLovinSdkConfiguration ->
            // AppLovin SDK is initialized, start loading ads
        })
    }
}
```

### Implement your ad

Follow the documentation depending on the format you want to use. Currently our adapter only supports interstitial and rewarded ads:

- [Interstitial](https://dash.applovin.com/documentation/mediation/android/ad-formats/interstitials)
- [Rewarded](https://dash.applovin.com/documentation/mediation/android/ad-formats/rewarded-ads)

## 2. Reference Adikteev SDK (CrossDK)

### CrossDK

Follow the `Installation` step in the readme of the CrossDK repository to install Adikteev Cross SDK [here](https://github.com/Adikteev/crossdk-android)

### Adapter class

Copy paste our adapter class from our repository inside your desired package.

> Example in com.adikteev.mediation.adapters.AdikteevNetworkMediationAdapter

### Proguard

Add these lines in your `proguard-rules.pro` to keep the class during your deployment:

```groovy
# Keep class for AppLovin adapter
-keepnames class com.YOUR_PACKAGE.AdikteevNetworkMediationAdapter
-keepclasseswithmembers class com.YOUR_PACKAGE.AdikteevNetworkMediationAdapter
```

> Example with our package : com.adikteev.mediation.adapters.AdikteevNetworkMediationAdapter

## 3. Activate the adapter

On your AppLovin account, edit your Ad Unit format [here](https://dash.applovin.com/o/mediation/ad_units) and enable the Custom Network created previously for Adikteev SDK.

Set the custom parameter:

```json
{
  "app_identifier": "com.YOUR_APP_ID",
  "api_key": "YOUR_API_KEY",
  "idfv": "(OPTIONAL)YOUR_DEVICE_ID_FOR_TESTING"
}
```

## Result

You should be able to see Adikteev cross promotion ad when showing applovin ads.
