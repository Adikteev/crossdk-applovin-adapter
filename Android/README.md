# Adikteev Android AppLovin MAX Adapter

Adikteev AppLovin MAX Adapter is a solution belonging to Adikteev. It provides android adapter to
connect AppLovin SDK to Adikteev SDK.

First you need to integrate the AppLovin SDK then you can use our adapter to display a cross
promoted ad.

## Requirements

Create an AppLovin account [here](https://dash.applovin.com/)

Create a custom network with the following configuration:

- type: SDK
- Android / Fire OS Adapter Class
  Name: `com.adikteev.mediation.adapters.AdikteevNetworkMediationAdapter`

Create your own Ad Unit for the wanted format (interstitial or
rewarded) [here](https://dash.applovin.com/o/mediation/ad_units).

For testing purposes, you can add your device GAID to test mode
devices [here](https://dash.applovin.com/o/mediation/test_modes/).

> Note : Modification on your AppLovin account is not real time, there may have up to 1 hour of
> delay.

## 1. Integrate AppLovin SDK

For a full guide refer to the Applovin integration documentation for android
available [here](https://dash.applovin.com/documentation/mediation/android/getting-started/integration)
.

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

### 2.1. CrossDK

Follow the `Installation` step in the readme of the CrossDK repository to install Adikteev Cross
SDK [here](https://github.com/Adikteev/crossdk-android)

### 2.2. Adapter installation

#### 2.2.1. Installation with Github packages

1. Step 1 : Generate a Personal Access Token for GitHub

- Inside you GitHub account go to: Settings -> Developer Settings -> Personal Access Tokens ->
  Generate new token
- Make sure you select the following scopes (“ write:packages”, “ read:packages”) and Generate a
  token
- After Generating make sure to copy your new personal access token. You cannot see it again! The
  only option is to generate a new key

2. Step 2 : Update build.gradle inside the application module

- Add the following code to build.gradle inside the app module that will be using the SDK published
  on GitHub Package Registry

```groovy
repositories {
  maven {
    name = "GitHubPackages"
    url = uri("https://maven.pkg.github.com/Adikteev/crossdk-applovin-adapter")
    credentials {
      /*
      *GITHUB_USERID: your github user Id
      *PERSONAL_ACCESS_TOKEN: The generated access token
       */
      username = "GITHUB_USERID"
      password = "PERSONAL_ACCESS_TOKEN"
    }
  }
}
```

- inside dependencies of the settings.gradle of app module, use the following code:

```groovy
dependencies {
  implementation 'com.adikteev:crossdk-adapter-android:x.x.x'
}
```

> x.x.x: refers to the version of the adapter

#### 2.2.2. Manual installation

- You can also download manually the adapter release package directly from
  our [Github package registry page](https://github.com/Adikteev/crossdk-applovin-adapter/packages/1821114)
- Place the release package inside a lib folder in your android studio project
- Add these lines into your app build.gradle file:

```groovy
implementation files('./libs/crossdk-adapter-android-x.x.x.aar')
```

> x.x.x: refers to the version of the adapter

## 3. Activate the adapter

On your AppLovin account, edit your Ad Unit
format [here](https://dash.applovin.com/o/mediation/ad_units) and enable the Custom Network created
previously for Adikteev SDK.

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
