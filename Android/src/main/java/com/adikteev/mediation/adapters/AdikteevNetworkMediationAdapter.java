package com.applovin.mediation.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import com.adikteev.crossdk.CrossDKConfig;
import com.adikteev.crossdk.network.OnInitCrossDKListener;
import com.adikteev.crossdk.views.CrossDKInterstitialView;
import com.adikteev.crossdk.views.listener.CrossDKContentCallback;
import com.adikteev.crossdk.views.listener.CrossDKLoadCallback;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.adapter.MaxAdapterError;
import com.applovin.mediation.adapter.MaxInterstitialAdapter;
import com.applovin.mediation.adapter.MaxRewardedAdapter;
import com.applovin.mediation.adapter.listeners.MaxInterstitialAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxRewardedAdapterListener;
import com.applovin.mediation.adapter.parameters.MaxAdapterInitializationParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters;
import com.applovin.mediation.adapters.MediationAdapterBase;
import com.applovin.sdk.AppLovinSdk;

import java.util.concurrent.atomic.AtomicBoolean;

@Keep
public class AdikteevNetworkMediationAdapter extends MediationAdapterBase implements MaxInterstitialAdapter, MaxRewardedAdapter {

    ///////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////

    private static final String ADAPTER_VERSION = "1.0.1.1";

    private static final AtomicBoolean initialized = new AtomicBoolean();
    private static InitializationStatus iStatus;
    private CrossDKInterstitialView interstitialView;
    private InterstitialListener interstitialListener;
    private CrossDKInterstitialView rewardedView;
    private RewardedInterstitialListener rewardedInterstitialListener;

    ///////////////////////////////////////////////////////////////////////////
    // EXPLICIT DEFAULT CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    public AdikteevNetworkMediationAdapter(final AppLovinSdk sdk) {
        super(sdk);
    }

    ///////////////////////////////////////////////////////////////////////////
    // SPECIALIZATION
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void initialize(MaxAdapterInitializationParameters maxAdapterInitializationParameters, Activity activity, OnCompletionListener onCompletionListener) {
        if (initialized.compareAndSet(false, true)) {
            log("AdikteevNetworkMediationAdapter: Initializing Adikteev SDK...");
            iStatus = InitializationStatus.INITIALIZING;
            Context context = getContext(activity);
            final String apiKey = maxAdapterInitializationParameters.getCustomParameters().getString("api_key");
            final String appId = maxAdapterInitializationParameters.getCustomParameters().getString("app_identifier");
            final String deviceId = maxAdapterInitializationParameters.getCustomParameters().getString("idfv");
            new CrossDKConfig.Builder()
                    .apiKey(apiKey)
                    .appId(appId)
                    .deviceId(deviceId)
                    .setup(context, new OnInitCrossDKListener() {
                        @Override
                        public void onInitSuccess() {
                            log("Adikteev SDK initialized successfully");
                            initialized.set(true);
                            iStatus = InitializationStatus.INITIALIZED_SUCCESS;
                            onCompletionListener.onCompletion(iStatus, null);
                        }

                        @Override
                        public void onInitFailure(@Nullable Exception e) {
                            log("Adikteev SDK initialization failure!");
                            initialized.set(false);
                            iStatus = InitializationStatus.INITIALIZED_FAILURE;
                            onCompletionListener.onCompletion(iStatus, e != null ? e.getMessage() : null);
                        }
                    });
        } else {
            log("AdikteevNetworkMediationAdapter: Adikteev SDK attempted initialization already - marking initialization as completed");
            onCompletionListener.onCompletion(iStatus, null);
        }
    }

    @Override
    public String getSdkVersion() {
        return getAdapterVersion().substring(0, getAdapterVersion().lastIndexOf('.'));
    }

    @Override
    public String getAdapterVersion() {
        return ADAPTER_VERSION;
    }

    @Override
    public void onDestroy() {
        log("Destroy called for adapter " + this);
        if (interstitialView != null) {
            interstitialView.destroy();
            interstitialView = null;
            interstitialListener = null;
        }
        if (rewardedView != null) {
            rewardedView.destroy();
            rewardedView = null;
            rewardedInterstitialListener = null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // SPECIALIZATION INTERSTITIAL
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void loadInterstitialAd(MaxAdapterResponseParameters maxAdapterResponseParameters, Activity activity, MaxInterstitialAdapterListener maxInterstitialAdapterListener) {
        interstitialView = new CrossDKInterstitialView(getContext(activity));
        log("AdikteevNetworkMediationAdapter: Loading interstitial ...");
        interstitialView.load(new CrossDKLoadCallback() {
            @Override
            public void onRecommendationLoaded() {
                log("AdikteevNetworkMediationAdapter: Interstitial recommendation loaded.");
                maxInterstitialAdapterListener.onInterstitialAdLoaded();
                interstitialListener = new InterstitialListener(maxInterstitialAdapterListener);
                interstitialView.setCrossDKContentCallback(interstitialListener);
            }

            @Override
            public void onRecommendationLoadFailure() {
                log("AdikteevNetworkMediationAdapter: Interstitial recommendation load failed.");
                maxInterstitialAdapterListener.onInterstitialAdLoadFailed(MaxAdapterError.INTERNAL_ERROR);
            }
        });
    }

    @Override
    public void showInterstitialAd(MaxAdapterResponseParameters maxAdapterResponseParameters, Activity activity, MaxInterstitialAdapterListener maxInterstitialAdapterListener) {
        if (interstitialView != null && activity != null) {
            activity.addContentView(interstitialView, getLayoutParams());
            interstitialView.show();
        } else {
            log("AdikteevNetworkMediationAdapter: Interstitial failed to show! ");
            maxInterstitialAdapterListener.onInterstitialAdDisplayFailed(new MaxAdapterError(-4205, "Ad Display Failed"));
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // LISTENER INTERSTITIAL
    ///////////////////////////////////////////////////////////////////////////

    private class InterstitialListener implements CrossDKContentCallback {
        private final MaxInterstitialAdapterListener listener;

        private InterstitialListener(final MaxInterstitialAdapterListener maxInterstitialAdapterListener) {
            listener = maxInterstitialAdapterListener;
        }

        @Override
        public void onRecommendationDisplayed() {
            listener.onInterstitialAdDisplayed();
        }

        @Override
        public void onRecommendationClicked() {
            listener.onInterstitialAdClicked();
        }

        @Override
        public void onRecommendationClosed() {
            listener.onInterstitialAdHidden();
        }

        @Override
        public void onUnsupportedApiVersion() {
            listener.onInterstitialAdDisplayFailed(MaxAdapterError.INTERNAL_ERROR);
        }

        @Override
        public void onConfigurationError() {
            listener.onInterstitialAdDisplayFailed(MaxAdapterError.INTERNAL_ERROR);
        }

        @Override
        public void onNoRecommendation() {
            listener.onInterstitialAdDisplayFailed(MaxAdapterError.INTERNAL_ERROR);
        }

        @Override
        public void onShowContentError() {
            listener.onInterstitialAdDisplayFailed(MaxAdapterError.INTERNAL_ERROR);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // SPECIALIZATION REWARDED
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void loadRewardedAd(MaxAdapterResponseParameters maxAdapterResponseParameters, Activity activity, MaxRewardedAdapterListener maxRewardedAdapterListener) {
        rewardedView = new CrossDKInterstitialView(getContext(activity));
        log("AdikteevNetworkMediationAdapter: Loading rewarded interstitial ...");
        rewardedView.load(new CrossDKLoadCallback() {
            @Override
            public void onRecommendationLoaded() {
                log("AdikteevNetworkMediationAdapter: Rewarded interstitial recommendation loaded.");
                maxRewardedAdapterListener.onRewardedAdLoaded();
                rewardedInterstitialListener = new RewardedInterstitialListener(maxRewardedAdapterListener);
                rewardedView.setCrossDKContentCallback(rewardedInterstitialListener);
            }

            @Override
            public void onRecommendationLoadFailure() {
                log("AdikteevNetworkMediationAdapter: Rewarded interstitial recommendation load failed.");
                maxRewardedAdapterListener.onRewardedAdLoadFailed(MaxAdapterError.INTERNAL_ERROR);
            }
        });
    }

    @Override
    public void showRewardedAd(MaxAdapterResponseParameters maxAdapterResponseParameters, Activity activity, MaxRewardedAdapterListener maxRewardedAdapterListener) {
        if (rewardedView != null && activity != null) {
            configureReward(maxAdapterResponseParameters);
            activity.addContentView(rewardedView, getLayoutParams());
            rewardedView.show();
            rewardedView.setRewarded(true, () -> {
                log("AdikteevNetworkMediationAdapter: Rewarded interstitial user earned reward ");
                MaxReward reward = getReward();
                maxRewardedAdapterListener.onUserRewarded(reward);
            });
        } else {
            log("AdikteevNetworkMediationAdapter: Rewarded interstitial failed to show! ");
            maxRewardedAdapterListener.onRewardedAdDisplayFailed(new MaxAdapterError(-4205, "Ad Display Failed"));
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // LISTENER REWARDED
    ///////////////////////////////////////////////////////////////////////////

    private class RewardedInterstitialListener implements CrossDKContentCallback {
        private final MaxRewardedAdapterListener listener;

        private RewardedInterstitialListener(final MaxRewardedAdapterListener maxRewardedAdapterListener) {
            listener = maxRewardedAdapterListener;
        }

        @Override
        public void onRecommendationDisplayed() {
            listener.onRewardedAdDisplayed();
        }

        @Override
        public void onRecommendationClicked() {
            listener.onRewardedAdClicked();
        }

        @Override
        public void onRecommendationClosed() {
            listener.onRewardedAdHidden();
        }

        @Override
        public void onUnsupportedApiVersion() {
            listener.onRewardedAdDisplayFailed(MaxAdapterError.INTERNAL_ERROR);
        }

        @Override
        public void onConfigurationError() {
            listener.onRewardedAdDisplayFailed(MaxAdapterError.INTERNAL_ERROR);
        }

        @Override
        public void onNoRecommendation() {
            listener.onRewardedAdDisplayFailed(MaxAdapterError.INTERNAL_ERROR);
        }

        @Override
        public void onShowContentError() {
            listener.onRewardedAdDisplayFailed(MaxAdapterError.INTERNAL_ERROR);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // HELPER
    ///////////////////////////////////////////////////////////////////////////

    private Context getContext(@Nullable Activity activity) {
        // NOTE: `activity` can only be null in 11.1.0+, and `getApplicationContext()` is introduced in 11.1.0
        return (activity != null) ? activity : getApplicationContext();
    }

    private FrameLayout.LayoutParams getLayoutParams() {
        FrameLayout.LayoutParams adParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        adParams.gravity = Gravity.BOTTOM;
        return adParams;
    }
}
