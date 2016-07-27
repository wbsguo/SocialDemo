package com.app.studiodemo.socialdemo.activity.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
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

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/2/18.
 */
public class FacebookTool {
    private static final String TAG = "FacebookTool";
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
    private static final String PERMISSION = "publish_actions";
    private CallbackManager callbackManager;
    //facebook头像
    public final static String facebookVatar = "http://graph.facebook.com/%s/picture?type=large";

    /**
     * 先必须初始化
     *
     * @param applicationContext
     */
    public void init(Context applicationContext) {
        FacebookSdk.sdkInitialize(applicationContext);
        callbackManager = CallbackManager.Factory.create();
    }
    /**
     * 获取头像
     *
     * @param facebookId
     * @return
     */
    public String getFacebookImag(String facebookId) {
        String facebookImag = String.format(facebookVatar, facebookId);
        return facebookImag;
    }
    private void getUserInfo(final Context context, final AccessToken accessToken, final FBCallBack listenner) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            if(object!=null){
                                Log.e(TAG,"用户信息:"+object.toString());
                                String name = object.getString("name");
                                String email = object.getString("email");
                                String gender = object.getString("gender");
                                JSONObject pictureObject=object.getJSONObject("picture");
                                JSONObject dataObject=pictureObject.getJSONObject("data");
                                String url=dataObject.getString("url");
                                User user=new User();
                                user.userid=accessToken.getUserId();
                                user.name=name;
                                user.email=email;
                                user.image=url;
                                user.token=accessToken;
                                user.gender=gender;
                                setProfile(context,user);
                                listenner.onSuccess(user);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listenner.onError("失败");
                        }
                    }
                });
        Bundle parameters = new Bundle();
//        parameters.putString("fields", "name,id,email,gender,birthday,friends,albums,picture,cover");
        parameters.putString("fields", "name,id,email,gender,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    /**
     * 获取用户信息
     *
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
    private void loginWithReadPermissions(Activity activity, FBCallBack listenner) {
        LoginManager.getInstance().logInWithReadPermissions(activity,
                Arrays.asList("public_profile", "user_friends", "email"));
        listenner.onSuccess();
    }

    public void loginWithPushPermissions(Activity activity, MyTagListenner myTagListenner) {
        LoginManager.getInstance().logInWithPublishPermissions(activity, Arrays.asList("public_profile"));
    }

    public void setProfile(Context context, User user) {
        SharedPreferences preferences = context.getSharedPreferences("fbPre", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("fbUserId", user.userid);
        editor.putString("fbname", user.name);
        editor.putString("fbemail", user.email);
        editor.putString("fbgender", user.gender);
        editor.putString("fbUserImage", user.image);
        editor.commit();
    }
    private boolean isAccessToken() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return false;
        } else {
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

    public void login(final Activity activity, final FBCallBack listenner) {
        if (isAccessToken()) {
            if (!isExpired()) {
                getUserInfo(activity, AccessToken.getCurrentAccessToken(), listenner);
            } else { // 失效，重新授权
                loginWithReadPermissions(activity, new FBCallBack() {
                    @Override
                    public void onSuccess(User user) {

                    }

                    @Override
                    public void onSuccess() {
                        FbLogin(activity, listenner);
                    }

                    @Override
                    public void onError(String errorInfo) {

                    }
                });
            }
        } else {
            loginWithReadPermissions(activity, new FBCallBack() {
                @Override
                public void onSuccess(User user) {

                }

                @Override
                public void onSuccess() {
                    FbLogin(activity, listenner);
                }

                @Override
                public void onError(String errorInfo) {

                }
            });
        }
    }

    private void FbLogin(final Context context, final FBCallBack listenner) {
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = loginResult.getAccessToken();
                        getUserInfo(context, accessToken, listenner);
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
    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null
                && accessToken.getPermissions().contains(PERMISSION);
    }
    public interface FBCallBack {
        void onSuccess(User user);

        void onSuccess();

        void onError(String errorInfo);
    }

    public class User{
        AccessToken token;
        String userid;
        String name;
        String email;
        String gender;
        String image;
    }



    /*========================分享=====================================================*/
    /**
     * 分享文字
     */
    public void share_message(Activity activity) {
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .build();
        ShareDialog shareDialog = new ShareDialog(activity);
        if (shareDialog.canShow(linkContent)) {
            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    Log.e(TAG,"分享文字成功:");
                }

                @Override
                public void onCancel() {
                    Log.e(TAG,"分享文字取消");
                }

                @Override
                public void onError(FacebookException e) {
                    Log.e(TAG,"分享文字异常:"+e.getMessage());
                }
            });
            shareDialog.show(linkContent);
        } else if (hasPublishPermission()) {
            ShareApi.share(linkContent, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {

                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {

                }
            });
        } else {
            //获取权限
            LoginManager.getInstance().logInWithPublishPermissions(activity,
                    Arrays.asList(PERMISSION));
        }
    }

    public void share_phto(Activity activity,List<String> filePath) {
        ArrayList<SharePhoto> photos = new ArrayList<>();
        for (String path : filePath) {
            SharePhoto sharePhoto = new SharePhoto.Builder()
                    .setBitmap(BitmapFactory.decodeFile(path))
                    .build();
            photos.add(sharePhoto);
        }
        SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder()
                .setPhotos(photos)
                .build();
        ShareDialog shareDialog = new ShareDialog(activity);
        if (shareDialog.canShow(sharePhotoContent)) {
            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    Log.e(TAG,"图片分享成功:");
                }

                @Override
                public void onCancel() {
                    Log.e(TAG,"图片分享取消");
                }

                @Override
                public void onError(FacebookException e) {
                    Log.e(TAG,"图片分享异常:"+e.getMessage());
                }
            });
            shareDialog.show(sharePhotoContent);
        } else if (hasPublishPermission()) {
            ShareApi.share(sharePhotoContent, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    Log.e(TAG,"图片分享成功:");
                }

                @Override
                public void onCancel() {
                    Log.e(TAG,"图片分享取消");
                }

                @Override
                public void onError(FacebookException e) {
                    Log.e(TAG,"图片分享异常:"+e.getMessage());
                }
            });
        }else {
            //获取权限
            LoginManager.getInstance().logInWithPublishPermissions(activity,
                    Arrays.asList(PERMISSION));
        }
    }

    public void share_phto(Activity activity,
                           FacebookCallback<Sharer.Result> shareCallback, Bitmap bitmap) {
        ShareDialog shareDialog = new ShareDialog(activity);
        shareDialog.registerCallback(callbackManager, shareCallback);
        ArrayList<SharePhoto> photos = new ArrayList<>();
        SharePhoto sharePhoto = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();
        photos.add(sharePhoto);
        SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder()
                .setPhotos(photos)
                .build();
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            shareDialog.show(sharePhotoContent);
        } else if (hasPublishPermission()) {
            ShareApi.share(sharePhotoContent, shareCallback);
        }else {
            //获取权限
            LoginManager.getInstance().logInWithPublishPermissions(activity,
                    Arrays.asList(PERMISSION));
        }
    }

    public void share_phto(Activity activity,
                           FacebookCallback<Sharer.Result> shareCallback,String imageUrl) {
        ShareDialog shareDialog = new ShareDialog(activity);
        shareDialog.registerCallback(callbackManager, shareCallback);
        ArrayList<SharePhoto> photos = new ArrayList<>();
        SharePhoto sharePhoto = new SharePhoto.Builder()
                .setImageUrl(Uri.parse(imageUrl))
                .build();
        photos.add(sharePhoto);
        SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder()
                .setPhotos(photos)
                .build();
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            shareDialog.show(sharePhotoContent);
        } else if (hasPublishPermission()) {
            ShareApi.share(sharePhotoContent, shareCallback);
        }else {
            //获取权限
            LoginManager.getInstance().logInWithPublishPermissions(activity,
                    Arrays.asList(PERMISSION));
        }
    }
    /**
     * 获取KeyHash
     * @param context
     */
    public void getFBHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo("包名", PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("MY KEY HASH", "sign:" + sign);
            }
        } catch (Exception e) {
        }
    }
}
