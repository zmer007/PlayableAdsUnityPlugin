package com.zplay.playable.playableadsplugin;

import android.app.Activity;
import android.util.Log;

import com.playableads.PlayLoadingListener;
import com.playableads.PlayPreloadingListener;
import com.playableads.PlayableAds;
import com.unity3d.player.UnityPlayer;

import java.lang.ref.WeakReference;

/**
 * 2017/9/22.
 */
public class PlayableAdsAdapter {

    private static final String TAG = "PlayableAdsAdapter";
    private static WeakReference<PlayableAds> instanceRef = null;
    private static boolean autoload = true;
    private static int cacheCount = 1;

    public static void InitPA(final Activity activity, final String appId) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                instanceRef = new WeakReference<>(PlayableAds.init(activity, appId));
                instanceRef.get().setAutoLoadAd(autoload);
                instanceRef.get().setCacheCountPerUnitId(cacheCount);
            }
        });
    }

    public static void AutoloadAd(boolean autoload) {
        PlayableAdsAdapter.autoload = autoload;
        if (instanceRef == null || instanceRef.get() == null) {
            Log.e(TAG, "AutoloadAd: PlayableAds instance is null");
            return;
        }
        instanceRef.get().setAutoLoadAd(autoload);
    }

    public static void CacheCountPerUnitId(int count) {
        cacheCount = count;
        if (instanceRef == null || instanceRef.get() == null) {
            Log.e(TAG, "CacheCountPerUnitId: PlayableAds instance is null");
            return;
        }
        instanceRef.get().setCacheCountPerUnitId(count);
    }

    public static void RequestAd(String adUnitId, final String objectName) {
        if (instanceRef == null || instanceRef.get() == null) {
            Log.e(TAG, "RequestAd: PlayableAds instance is null");
            return;
        }
        instanceRef.get().requestPlayableAds(adUnitId, new PlayPreloadingListener() {
            @Override
            public void onLoadFinished() {
                UnityPlayer.UnitySendMessage(objectName, "OnLoadFinished", "load finished");
            }

            @Override
            public void onLoadFailed(int i, String s) {
                UnityPlayer.UnitySendMessage(objectName, "OnLoadFailed", "error code: " + i + "\nmsg: " + s);
            }
        });

    }

    public static void PresentAd(String adUnitId, final String objectName) {
        Log.d(TAG, "PresentAd: 0");
        if (instanceRef == null || instanceRef.get() == null) {
            Log.e(TAG, "RequestAd: PlayableAds instance is null");
            return;
        }
        Log.d(TAG, "PresentAd: 1");
        PlayableAds ads = instanceRef.get();

        if (!ads.canPresentAd(adUnitId)) {
            UnityPlayer.UnitySendMessage(objectName, "OnPresentError", "cache not finished");
            return;
        }
        Log.d(TAG, "PresentAd: 2" + adUnitId + ":" + ads);
        ads.presentPlayableAD(adUnitId, new PlayLoadingListener() {
            @Override
            public void onVideoStart() {
                Log.d(TAG, "onVideoStart: ");
                UnityPlayer.UnitySendMessage(objectName, "PlayableAdsMessage", "video start");
            }

            @Override
            public void onVideoFinished() {
                Log.d(TAG, "onVideoFinished: ");
                UnityPlayer.UnitySendMessage(objectName, "PlayableAdsMessage", "video finished");
            }

            @Override
            public void playableAdsIncentive() {
                Log.d(TAG, "playableAdsIncentive: ");
                UnityPlayer.UnitySendMessage(objectName, "PlayableAdsIncentive", "incentive");
            }

            @Override
            public void onLandingPageInstallBtnClicked() {
                Log.d(TAG, "onLandingPageInstallBtnClicked: ");
                UnityPlayer.UnitySendMessage(objectName, "PlayableAdsInstallButtonClicked", "install button clicked");
            }

            @Override
            public void onAdClosed() {
                Log.d(TAG, "onAdClosed: ");
                UnityPlayer.UnitySendMessage(objectName, "PlayableAdClosed", "ad closed");
            }

            @Override
            public void onAdsError(int i, String s) {
                Log.d(TAG, "onAdsError: ");
                UnityPlayer.UnitySendMessage(objectName, "OnPresentError", "error code: " + i + "\nmsg: " + s);
            }
        });
    }

    public static boolean canPresentAd(String adUnitId) {
        if (instanceRef == null || instanceRef.get() == null) {
            Log.e(TAG, "RequestAd: PlayableAds instance is null");
            return false;
        }
        return instanceRef.get().canPresentAd(adUnitId);
    }

}
