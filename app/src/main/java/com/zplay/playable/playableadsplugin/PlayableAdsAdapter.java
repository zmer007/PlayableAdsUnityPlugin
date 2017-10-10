package com.zplay.playable.playableadsplugin;

import android.app.Activity;

import com.playableads.PlayLoadingListener;
import com.playableads.PlayPreloadingListener;
import com.playableads.PlayableAds;
import com.unity3d.player.UnityPlayer;
/**
 * 2017/9/22.
 */
public class PlayableAdsAdapter {
    private static final String TAG = "PlayableAdsAdapter";

    public static void InitPA(final Activity activity, final String appId, final String adUnitId) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlayableAds.init(activity, appId, adUnitId);
            }
        });
    }

    public static void RequestAd(Activity activity, final String objectName) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlayableAds.getInstance().requestPlayableAds(new PlayPreloadingListener() {
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
        });
    }

    public static void PresentAd(final Activity activity, final String objectName) {
        if(!PlayableAds.getInstance().canPresentAd()){
            UnityPlayer.UnitySendMessage(objectName, "OnPresentError", "cache not finished");
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlayableAds.getInstance().presentPlayableAD(activity, new PlayLoadingListener() {
                    @Override
                    public void playableAdsIncentive() {
                        UnityPlayer.UnitySendMessage(objectName, "PlayableAdsIncentive", "incentive");
                    }

                    @Override
                    public void onAdsError(int i, String s) {
                        UnityPlayer.UnitySendMessage(objectName, "OnPresentError", "error code: " + i + "\nmsg: " + s);
                    }
                });
            }
        });
    }

    public static boolean canPresentAd(){
        return PlayableAds.getInstance().canPresentAd();
    }

}
