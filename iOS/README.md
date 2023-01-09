# Adikteev iOS AppLovin MAX Adapter

Adikteev AppLovin MAX Adapter is a solution belonging to Adikteev. It provides iOS adapter to connect AppLovin SDK to Adikteev SDK.

First you need to integrate the AppLovin SDK then you can use our adapter to display a cross promoted ad.

## Requirements

Create an AppLovin account [here](https://dash.applovin.com/).

Create a custom network (type : SDK; iOS Adapter Class Name: `ALAdikteevMediationAdapter`).

Create your own Ad Unit for the wanted format (interstitial or rewarded) [here](https://dash.applovin.com/o/mediation/ad_units).

For testing purposes, you can add your device IDFA to test mode devices [here](https://dash.applovin.com/o/mediation/test_modes/).

> Note : Modification on your AppLovin account is not real time, there may have up to 1 hour of delay.

## 1. Integrate AppLovin SDK

For a full guide refer to the Applovin integration documentation for iOS available [here](https://dash.applovin.com/documentation/mediation/ios/getting-started/integration).

### Initialize the SDK

Follow AppLovin documentation for initializing the SDK:

```swift
@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate
{
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool
    {
        // Please make sure to set the mediation provider value to "max" to ensure proper functionality
        ALSdk.shared()!.mediationProvider = "max"
        ALSdk.shared()!.userIdentifier = "USER_ID"
        ALSdk.shared()!.initializeSdk { (configuration: ALSdkConfiguration) in
            // Start loading ads
        }
     }
}
```

### Implement your ad

Follow the documentation depending on the format you want to use. Currently our adapter only supports interstitial and rewarded ads:

- [Interstitial](https://dash.applovin.com/documentation/mediation/ios/ad-formats/interstitials)
- [Rewarded](https://dash.applovin.com/documentation/mediation/ios/ad-formats/rewarded-ads)

## 2. Reference Adikteev SDK (CrossDK)

### CrossDK

Follow the `Installation` step in the readme of the CrossDK repository to install Adikteev Cross SDK [here](https://github.com/Adikteev/crossdk-ios)

### Adapter class

Copy paste our adapter classes (ALAdikteevMediationAdapter.m & ALAdikteevMediationAdapter.h) from our repository inside your desired package.

## 3. Activate the adapter

On your AppLovin account, edit your Ad Unit format [here](https://dash.applovin.com/o/mediation/ad_units) and enable the Custom Network created previously for Adikteev SDK.

Set the custom parameter:

```json
{
  "app_identifier": "YOUR_APP_ID",
  "api_key": "YOUR_API_KEY"
}
```

## Result

You should be able to see Adikteev cross promotion ad when showing applovin ads.
