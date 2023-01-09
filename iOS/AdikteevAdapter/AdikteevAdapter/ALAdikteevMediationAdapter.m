//
//  ALAdikteevMediationAdapter.m
//  AdikteevAdapter
//

#import "ALAdikteevMediationAdapter.h"
#import "CrossDK/CrossDK-Swift.h"

#define ADAPTER_VERSION @"1.0.0.0"

@interface ALAdikteevInterstitialDelegate : NSObject<InterstitialAdDelegate>
@property (nonatomic, weak) ALAdikteevMediationAdapter *parentAdapter;
@property (nonatomic, strong) id<MAInterstitialAdapterDelegate> delegate;
- (instancetype)initWithParentAdapter:(ALAdikteevMediationAdapter *)parentAdapter andNotify:(id<MAInterstitialAdapterDelegate>)delegate;
@end

@interface ALAdikteevRewardedDelegate : NSObject<RewardedAdDelegate>
@property (nonatomic, weak) ALAdikteevMediationAdapter *parentAdapter;
@property (nonatomic, strong) id<MARewardedAdapterDelegate> delegate;
- (instancetype)initWithParentAdapter:(ALAdikteevMediationAdapter *)parentAdapter andNotify:(id<MARewardedAdapterDelegate>)delegate;
@end

@interface ALAdikteevMediationAdapter()
@property (nonatomic, strong) CrossDKOverlay *crossDKOverlay;

// Interstitial
@property (nonatomic, strong) ALAdikteevInterstitialDelegate *interstitialAdDelegate;

// Rewarded
@property (nonatomic, strong) ALAdikteevRewardedDelegate *rewardedAdDelegate;
@end

@implementation ALAdikteevMediationAdapter

#pragma mark - MAAdapter Methods

- (void)initializeWithParameters:(id<MAAdapterInitializationParameters>)parameters completionHandler:(void (^)(MAAdapterInitializationStatus, NSString * _Nullable))completionHandler {
    NSString *appId = parameters.customParameters[@"app_identifier"];
    NSString *apiKey = parameters.customParameters[@"api_key"];
    [
        CrossDKConfig.shared
        setupWithAppId:appId
        apiKey:apiKey
        userId: nil
    ];

    completionHandler(MAAdapterInitializationStatusInitializedSuccess, nil);
}

- (NSString *)SDKVersion {
    return [self.crossDKOverlay sdkVersion];
}

- (NSString *)adapterVersion {
    return ADAPTER_VERSION;
}

- (void)destroy {
    self.crossDKOverlay = nil;
    self.interstitialAdDelegate = nil;
    self.rewardedAdDelegate = nil;

    UIWindow *window = [[UIApplication sharedApplication] keyWindow];
    if (window != nil) {
        [self.crossDKOverlay dismissWithWindow:window];
    }
}

#pragma mark - MAInterstitialAdapter Methods

- (void)loadInterstitialAdForParameters:(id<MAAdapterResponseParameters>)parameters andNotify:(id<MAInterstitialAdapterDelegate>)delegate {
    self.crossDKOverlay = [[CrossDKOverlay alloc] init];
    self.interstitialAdDelegate = [[ALAdikteevInterstitialDelegate alloc] initWithParentAdapter: self andNotify: delegate];
    self.crossDKOverlay.interstitialAdDelegate = self.interstitialAdDelegate;
    [self.crossDKOverlay load];
}

- (void)showInterstitialAdForParameters:(id<MAAdapterResponseParameters>)parameters andNotify:(id<MAInterstitialAdapterDelegate>)delegate {
    UIWindow *window = [[UIApplication sharedApplication] keyWindow];
    if (window != nil) {
        [self.crossDKOverlay
         displayWithWindow:window
         format:OverlayFormatInterstitial
         position:OverlayPositionBottom
         withCloseButton:true
         isRewarded:false];
    }
}

#pragma mark - MARewardedAdapter Methods

- (void)loadRewardedAdForParameters:(id<MAAdapterResponseParameters>)parameters andNotify:(id<MARewardedAdapterDelegate>)delegate {
    self.crossDKOverlay = [[CrossDKOverlay alloc] init];
    self.rewardedAdDelegate = [[ALAdikteevRewardedDelegate alloc] initWithParentAdapter: self andNotify: delegate];
    self.crossDKOverlay.rewardedAdDelegate = self.rewardedAdDelegate;
    [self.crossDKOverlay load];
}

- (void)showRewardedAdForParameters:(id<MAAdapterResponseParameters>)parameters andNotify:(id<MARewardedAdapterDelegate>)delegate {
    UIWindow *window = [[UIApplication sharedApplication] keyWindow];
    if (window != nil) {
        [self.crossDKOverlay
         displayWithWindow:window
         format:OverlayFormatInterstitial
         position:OverlayPositionBottom
         withCloseButton:true
         isRewarded:true];
    }
}

@end

#pragma mark - ALAdikteevInterstitialDelegate Methods

@implementation ALAdikteevInterstitialDelegate

- (instancetype)initWithParentAdapter:(ALAdikteevMediationAdapter *)parentAdapter andNotify:(id<MAInterstitialAdapterDelegate>)delegate {
    self = [super init];
    if ( self ) {
        self.parentAdapter = parentAdapter;
        self.delegate = delegate;
    }
    return self;
}

- (void)didLoadInterstitialAd {
    [self.parentAdapter log: @"Interstitial loaded"];
    [self.delegate didLoadInterstitialAd];
}

- (void)didFailToLoadInterstitialAd {
    [self.parentAdapter log: @"Interstitial failed to load"];
    [self.delegate didFailToLoadInterstitialAdWithError: MAAdapterError.adNotReady];
}

- (void)didDisplayInterstitialAd {
    [self.parentAdapter log: @"Interstitial displayed"];
    [self.delegate didDisplayInterstitialAd];
}

- (void)didFailToDisplayInterstitialAd {
    [self.parentAdapter log: @"Interstitial failed to display"];
    [self.delegate didFailToDisplayInterstitialAdWithError:(MAAdapterError.internalError)];
}

- (void)didClickInterstitialAd {
    [self.parentAdapter log: @"Interstitial clicked"];
    [self.delegate didClickInterstitialAd];
}

- (void)didHideInterstitialAd {
    [self.parentAdapter log: @"Interstitial hidden"];
    [self.delegate didHideInterstitialAd];
}

@end

#pragma mark - RewardedAdDelegate Methods

@implementation ALAdikteevRewardedDelegate

- (instancetype)initWithParentAdapter:(ALAdikteevMediationAdapter *)parentAdapter andNotify:(id<MARewardedAdapterDelegate>)delegate {
    self = [super init];
    if ( self ) {
        self.parentAdapter = parentAdapter;
        self.delegate = delegate;
    }
    return self;
}

- (void)didLoadRewardedAd {
    [self.parentAdapter log: @"Rewarded loaded"];
    [self.delegate didLoadRewardedAd];
}

- (void)didFailToLoadRewardedAd {
    [self.parentAdapter log: @"Rewarded failed to load"];
    [self.delegate didFailToLoadRewardedAdWithError: MAAdapterError.adNotReady];
}

- (void)didDisplayRewardedAd {
    [self.parentAdapter log: @"Rewarded displayed"];
    [self.delegate didDisplayRewardedAd];
}

- (void)didFailToDisplayRewardedAd {
    [self.parentAdapter log: @"Rewarded failed to display"];
    [self.delegate didFailToDisplayRewardedAdWithError:(MAAdapterError.internalError)];
}

- (void)didStartRewardedAdVideo {
    [self.parentAdapter log: @"Rewarded started video"];
    [self.delegate didStartRewardedAdVideo];
}

- (void)didClickRewardedAd {
    [self.parentAdapter log: @"Rewarded clicked"];
    [self.delegate didClickRewardedAd];
}

- (void)didHideRewardedAd {
    [self.parentAdapter log: @"Rewarded hidden"];
    [self.delegate didHideRewardedAd];
}

- (void)didCompleteRewardedAdVideo {
    [self.parentAdapter log: @"Rewarded completed video"];
    [self.delegate didCompleteRewardedAdVideo];
}

- (void)didRewardUserWithReward {
    [self.parentAdapter log: @"Rewarded user with reward"];
    [self.delegate didRewardUserWithReward: [self.parentAdapter reward]];
}

@end
