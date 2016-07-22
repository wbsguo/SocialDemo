package com.app.weighthealthdiet.twitter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import io.fabric.sdk.android.Fabric;

public class TwitterHelper {
    private static final String TAG = "TwitterHelper";
    private static final String TWITTER_KEY = "vSX5KTgik27a6WmJ6W6Rn4roy";
    private static final String TWITTER_SECRET = "tr9OhqqypCcSXZ2k5Bp3EV9hNKp1Kr63UABFmETVG720ehyQrg";
    private static TwitterHelper twitterHelper;

    volatile TwitterAuthClient authClient;

    private TwitterHelper() {

    }

    public static TwitterHelper getInstance() {
        if (twitterHelper == null) {
            twitterHelper = new TwitterHelper();
        }
        return twitterHelper;
    }

    /**
     * 必须先在application中初始化
     *
     * @param context
     */
    public void applicationOnCreate(Context context) {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(context, new Twitter(authConfig));
    }

    public int getRequestCode() {
        return this.getTwitterAuthClient().getRequestCode();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTwitterAuthClient().onActivityResult(requestCode, resultCode, data);
    }

    private TwitterAuthClient getTwitterAuthClient() {
        if (this.authClient == null) {
            Class var1 = TwitterHelper.class;
            synchronized (TwitterHelper.class) {
                if (this.authClient == null) {
                    this.authClient = new TwitterAuthClient();
                }
            }
        }
        return this.authClient;
    }

    public void login(Activity activity, Callback<TwitterSession> callback) {
        if (activity instanceof Activity) {
            if (callback == null) {
                throw new IllegalArgumentException("callback 不能为空哦！");
            }
            getTwitterAuthClient().authorize(activity, callback);
        } else {
            throw new IllegalArgumentException("context 必须是activity");
        }
    }

    public void getUserInfo(final UserCallBack userCallBack) {
        TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(
                false, false, new Callback<User>() {
                    @Override
                    public void success(Result<User> result) {
                        if (userCallBack != null) {
                            userCallBack.success(result);
                        }
                    }

                    @Override
                    public void failure(TwitterException e) {
                        if (userCallBack != null) {
                            userCallBack.failure(e);
                        }
                    }
                }
        );
    }

    public interface UserCallBack {
        void success(Result<User> result);

        void failure(TwitterException e);
    }

    /**
     * 网页分享
     *
     * @param context
     * @param message
     */
    public void sendMessageByTwitter(Activity context, String message) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse("https://twitter.com/intent/tweet?text=" + message);
        intent.setData(content_url);
        context.startActivity(intent);
    }

    /**
     * 客户端分享
     *
     * @param context
     * @param imageUrl 本地图片路径测试通过,网址未通过 Uri.fromFile(file)
     */
    public void shareImageUrl(Context context, String textString, String imageUrl) {
        TweetComposer.Builder builder = new TweetComposer.Builder(context);
        builder.text(textString);//文字
        builder.image(Uri.parse(imageUrl));
//            URL url=new URL(imageUrl);
//            builder.url(url); // 网址
        builder.show();
    }
}
