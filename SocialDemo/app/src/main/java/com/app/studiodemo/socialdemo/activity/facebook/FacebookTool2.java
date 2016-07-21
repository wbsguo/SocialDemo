package com.app.studiodemo.socialdemo.activity.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
public class FacebookTool2 {
    private static FacebookTool2 tool;

    private FacebookTool2() {

    }

    public static FacebookTool2 getInstance() {
        if (tool == null) {
            tool = new FacebookTool2();
        }
        return tool;
    }

    public static final int facebookLogin = 64206;
    public static final int facebookShare = 64207;
    public static final String noNet = "noNet";
    public static final String faild = "faild";
    public static final String success = "success";
    private CallbackManager callbackManager;
    //facebook头像
    public final static String facebookVatar = "http://graph.facebook.com/%s/picture?type=large";
    //	public final static String  facebookVatar = "http://graph.facebook.com/%s/picture?width=200&height=200";
    public void init(Context applicationContext) {
        FacebookSdk.sdkInitialize(applicationContext);
        callbackManager = CallbackManager.Factory.create();
    }

    private void getUserInfo(final Context context, final AccessToken accessToken,final MyTagListenner myTagListenner) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            String name = object.getString("name");
                            setUserName(context, name);
                            setUserId(context, accessToken.getUserId());
                            myTagListenner.onTagComplete(success,"");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            myTagListenner.onTagComplete(faild,"");
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name");//可以有一下多个字段名称
//        {
//                "id": "12345678",
//                "birthday": "1/1/1950",
//                "first_name": "Chris",
//                "gender": "male",
//                "last_name": "Colm",
//                "link": "http://www.facebook.com/12345678",
//                "location": {
//                  "id": "110843418940484",
//                  "name": "Seattle, Washington"
//                  },
//                "locale": "en_US",
//                "name": "Chris Colm",
//                "timezone": -8,
//                "updated_time": "2010-01-01T16:40:43+0000",
//                "verified": true
//        }
        request.setParameters(parameters);
        request.executeAsync();
    }

    public Profile getUserProfile() {
        return Profile.getCurrentProfile();
    }

    /**
     * token是否过期
     *
     * @return
     */
    private boolean isExpired() {
        return AccessToken.getCurrentAccessToken().isExpired();
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
    public void loginWithReadPermissions(Activity activity,MyTagListenner myTagListenner) {
        LoginManager.getInstance().logInWithReadPermissions(activity,
                Arrays.asList("public_profile", "user_friends"));
        myTagListenner.onTagComplete(success, "");
    }

    public void loginWithPushPermissions(Activity activity,MyTagListenner myTagListenner) {
        LoginManager.getInstance().logInWithPublishPermissions(activity, Arrays.asList("public_profile"));
        myTagListenner.onTagComplete(success,"");
    }

    public String getUserName(Context context) {
        return context.getSharedPreferences("fbname", Context.MODE_PRIVATE).getString("fbname", "");
    }

    public void setUserName(Context context, String name) {
        context.getSharedPreferences("fbname", Context.MODE_PRIVATE).edit().putString("fbname", name).commit();
    }

    public String getUserId(Context context) {
        return context.getSharedPreferences("fbUserId", Context.MODE_PRIVATE).getString("fbUserId", "");
    }

    public void setUserId(Context context, String userId) {
        context.getSharedPreferences("fbUserId", Context.MODE_PRIVATE).edit().putString("fbUserId", userId).commit();
    }
    public boolean isAccessToken(){
        if(AccessToken.getCurrentAccessToken()==null){
            return false;
        }else{
            return true;
        }
    }
    public void login(final Context context,final MyTagListenner myTagListenner){
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.e("", "登录成功");
                        AccessToken accessToken = loginResult.getAccessToken();
                        getUserInfo(context, accessToken,myTagListenner);
                    }

                    @Override
                    public void onCancel() {
                        Log.e("", "取消登录");
                        myTagListenner.onTagComplete(faild,"");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.e("", "登录失败:" + exception.getMessage());
                        myTagListenner.onTagComplete(faild,exception.getMessage());
                    }
                });
    }
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
//			.setContentTitle("Hello Facebook")
                    // .setContentDescription(
                    // "The 'Hello Facebook' sample  showcases simple Facebook integration")
                    // .setContentUrl(Uri.parse("http://developers.facebook.com/docs/android"))
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
}
