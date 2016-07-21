package com.app.studiodemo.socialdemo.activity.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.app.studiodemo.socialdemo.activity.listenner.MyTagListenner;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Administrator on 2016/2/18.
 */
public class FacebookTool {
    private static final String TAG="FacebookTool";
    private static FacebookTool tool;

    private FacebookTool() {

    }

    public static FacebookTool getInstance() {
        if (tool == null) {
            tool = new FacebookTool();
        }
        return tool;
    }

    public static final int facebookLogin = 64206;
    public static final int facebookShare = 64207;
    private CallbackManager callbackManager;
    //facebook头像
    public final static String facebookVatar = "http://graph.facebook.com/%s/picture?type=large";

    /**
     * 先必须初始化
     * @param applicationContext
     */
    public void init(Context applicationContext) {
        FacebookSdk.sdkInitialize(applicationContext);
        callbackManager = CallbackManager.Factory.create();
    }
    public void onResume(Context context) {
        //长期运行的活动，应用变得活跃
        AppEventsLogger.activateApp(context);
    }
    public void onPause(Context context) {
        //结束活动
        AppEventsLogger.deactivateApp(context);
    }

    /**
     * onSaveInstanceState 可做保存用
     * @param outState
     */
    public void onSaveInstanceState(Bundle outState) {
        Profile profile = Profile.getCurrentProfile();
        if(profile!=null){
            outState.putString("FACEBOOKNAME", profile.getName());
        }
    }
    private void getUserInfo(final Context context, final AccessToken accessToken,final FBCallBack listenner) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            String name = object.getString("name");
                            String facebookImag = getFacebookImag(accessToken.getUserId());
                            setProfile(context,accessToken.getUserId(),name,facebookImag);
                            listenner.onSuccess(accessToken,accessToken.getUserId(),name,facebookImag);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listenner.onError("失败");
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name");//可以有一下多个字段名称
        request.setParameters(parameters);
        request.executeAsync();
    }

    /**
     * 获取用户信息
     * @return
     */
    public Profile getUserProfile() {
        return Profile.getCurrentProfile();
    }
    /**
     * activity中onActivityResult调用
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    public void logOut() {
        LoginManager.getInstance().logOut();
    }

    /**
     * @param activity 默认含有获取用户信息和用户好友信息
     */
    private void loginWithReadPermissions(Activity activity,FBCallBack listenner) {
        LoginManager.getInstance().logInWithReadPermissions(activity,
                Arrays.asList("public_profile", "user_friends"));
        listenner.onSuccess();
    }

    public void loginWithPushPermissions(Activity activity,MyTagListenner myTagListenner) {
        LoginManager.getInstance().logInWithPublishPermissions(activity, Arrays.asList("public_profile"));
    }

    public String getUserName(Context context) {
        return context.getSharedPreferences("fbPre", Context.MODE_PRIVATE).getString("fbname", "");
    }
    public String getUserId(Context context) {
        return context.getSharedPreferences("fbPre", Context.MODE_PRIVATE).getString("fbUserId", "");
    }
    public void setProfile(Context context,String userId,String userName,String userImage){
        Log.e(TAG,"userId:"+userId+"userName:"+userName+"userImage:"+userImage);
        SharedPreferences preferences=context.getSharedPreferences("fbPre", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("fbUserId", userId);
        editor.putString("fbname", userName);
        editor.putString("fbUserImage", userImage);
        editor.commit();
    }
    public String getUserImage(Context context) {
        return context.getSharedPreferences("fbPre", Context.MODE_PRIVATE).getString("fbUserImage", "");
    }
    private boolean isAccessToken(){
        if(AccessToken.getCurrentAccessToken()==null){
            return false;
        }else{
            return true;
        }
    }
    /**
     * token是否过期
     *
     * @return
     */
    private boolean isExpired() {
        return AccessToken.getCurrentAccessToken().isExpired();
    }
    public void login(final Activity activity,final FBCallBack listenner){
        if(isAccessToken()){
            if (!isExpired()) {
                FbLogin(activity,listenner);
            } else { // 失效，重新授权
                loginWithReadPermissions(activity, new FBCallBack() {
                    @Override
                    public void onSuccess(AccessToken token, String userid, String name, String image) {

                    }

                    @Override
                    public void onSuccess() {
                        FbLogin(activity,listenner);
                    }

                    @Override
                    public void onError(String errorInfo) {

                    }
                });
            }
        }else{
            loginWithReadPermissions(activity, new FBCallBack() {
                @Override
                public void onSuccess(AccessToken token, String userid, String name, String image) {

                }

                @Override
                public void onSuccess() {
                    FbLogin(activity,listenner);
                }

                @Override
                public void onError(String errorInfo) {

                }
            });
        }
    }
    private void FbLogin(final Context context,final FBCallBack listenner){
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = loginResult.getAccessToken();
                        Profile profile=FacebookTool.getInstance().getUserProfile();
                        if(profile!=null){
                            String facebookImag = getFacebookImag(accessToken.getUserId());
                            setProfile(context,accessToken.getUserId(),profile.getName(),facebookImag);
                            listenner.onSuccess(accessToken,accessToken.getUserId(),profile.getName(),facebookImag);
                        }else{
                            getUserInfo(context, accessToken,listenner);
                        }
                    }

                    @Override
                    public void onCancel() {
                        listenner.onError("失败");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        listenner.onError(exception.getMessage());
                    }
                });
    }

    /**
     * 获取头像
     * @param facebookId
     * @return
     */
    public String getFacebookImag(String facebookId) {
        String facebookImag = String.format(facebookVatar, facebookId);
        return facebookImag;
    }
    /**
     * 分享文字
     */
    public void share_message(Activity activity,
                              FacebookCallback<Sharer.Result> shareCallback) {
        ShareDialog shareDialog = new ShareDialog(activity);
        shareDialog.registerCallback(callbackManager, shareCallback);
        boolean canPresentShareDialog = ShareDialog.canShow(ShareLinkContent.class);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null || canPresentShareDialog) {
            Profile profile = Profile.getCurrentProfile();
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .build();
            if (canPresentShareDialog) {
                shareDialog.show(linkContent);
            } else if (profile != null && hasPublishPermission()) {
                ShareApi.share(linkContent, shareCallback);
            }
        }
    }

    private static final String PERMISSION = "publish_actions";

    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null
                && accessToken.getPermissions().contains(PERMISSION);
    }

    /**
     * @param activity
     * @param shareCallback
     * @param image         默认图片资源
     */
    public void share_phto(Activity activity,
                           FacebookCallback<Sharer.Result> shareCallback, Bitmap image) {
        ShareDialog shareDialog = new ShareDialog(activity);
        shareDialog.registerCallback(callbackManager, shareCallback);
        boolean canPresentShareDialog = ShareDialog.canShow(ShareLinkContent.class);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null || canPresentShareDialog) {
            SharePhoto sharePhoto = new SharePhoto.Builder().setBitmap(image)
                    .build();
            ArrayList<SharePhoto> photos = new ArrayList<>();
            photos.add(sharePhoto);
            SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder()
                    .setPhotos(photos).build();
            if (canPresentShareDialog) {
                shareDialog.show(sharePhotoContent);
            } else if (hasPublishPermission()) {
                ShareApi.share(sharePhotoContent, shareCallback);
            } else {
                //获取权限
                LoginManager.getInstance().logInWithPublishPermissions(activity,
                        Arrays.asList(PERMISSION));
            }
        }
    }
    public interface FBCallBack {
        void onSuccess(AccessToken token, String userid, String name,String image);
        void onSuccess();
        void onError(String errorInfo);
    }
}
