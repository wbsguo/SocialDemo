package com.app.studiodemo.parsedemo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.parse.CountCallback;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.parse.twitter.Twitter;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2015/11/12.
 */
public class ParseUtils {
    private static final String TAG = "ParseUtils";
    public static final String noNet = "noNet";
    public static final String faild = "faild";
    public static final String success = "success";

    /**
     * 注册
     * @param context
     * @param emailValue
     * @param usernameValue
     * @param passwordValue
     * @param myTagListenner
     */
    public void register(Context context, String emailValue, String usernameValue, String passwordValue, final MyTagListenner myTagListenner) {
        if (!UtileTools.getInstance().checkNetWorkStatus(context)) {
            myTagListenner.onTagComplete(noNet, "");
            return;
        }
        ParseUser user = new ParseUser();
        user.setEmail(emailValue);
        user.setUsername(usernameValue);
        user.setPassword(passwordValue);
        user.signUpInBackground(new SignUpCallback() {

            @Override
            public void done(ParseException arg0) {
                if (arg0 == null) {
                    MyLog.v(TAG, "注册成功");
                    myTagListenner.onTagComplete(success, "");
                } else {
                    MyLog.v(TAG, "注册失败:" + arg0.getMessage());
                    myTagListenner.onTagComplete(faild, arg0.getMessage());
                }
            }
        });
    }

    /**
     * 登录
     * @param context
     * @param usernameValue
     * @param passwordValue
     * @param myTagListenner
     */
    public void login(Context context, String usernameValue, String passwordValue, final MyTagListenner myTagListenner) {
        if (!UtileTools.getInstance().checkNetWorkStatus(context)) {
            myTagListenner.onTagComplete(noNet, "");
            return;
        }
        ParseUser.logInInBackground(usernameValue, passwordValue, new LogInCallback() {
            @Override
            public void done(ParseUser arg0, ParseException arg1) {
                if (arg1 == null) {
                    MyLog.v(TAG, "登录成功");
                    myTagListenner.onTagComplete(success, "");
                } else {
                    MyLog.v(TAG, "登录失败:" + arg1.getMessage());
                    myTagListenner.onTagComplete(faild, arg1.getMessage());
                }
            }
        });
    }

    /**
     * 重置密码
     * @param context
     * @param emailValue
     * @param myTagListenner
     */
    public void resetPassword(Context context,String emailValue,final MyTagListenner myTagListenner){
        if (!UtileTools.getInstance().checkNetWorkStatus(context)) {
            myTagListenner.onTagComplete(noNet, "");
            return;
        }
        ParseUser.requestPasswordResetInBackground(emailValue, new RequestPasswordResetCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    MyLog.v(TAG, "重置密码成功");
                    myTagListenner.onTagComplete(success, "");
                } else {
                    MyLog.v(TAG, "重置密码失败:" + e.getMessage());
                    myTagListenner.onTagComplete(faild, e.getMessage());
                }
            }
        });
    }

    /**
     * 保存数据
     * @param myTagListenner
     * @param context
     */
    public void save(final MyTagListenner myTagListenner, final Context context) {
        if (!UtileTools.getInstance().checkNetWorkStatus(context)) {
            myTagListenner.onTagComplete(noNet, "");
            return;
        }
        ParseUser parseUser = ParseUser.getCurrentUser();
        if (parseUser != null) {
            ParseObject parseObject = new ParseObject("table_name");
            //增加访问权限
            parseObject.setACL(new ParseACL(parseUser));
            parseObject.put("table_fild", "table_value");
            parseObject.saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException arg0) {
                    if (arg0 == null) {
                        MyLog.v(TAG, "History保存成功");
                        myTagListenner.onTagComplete(success, "");
                    } else {
                        MyLog.v(TAG, "History保存失败:" + arg0.getMessage());
                        myTagListenner.onTagComplete(faild, arg0.getMessage());
                    }
                }
            });
        }
    }

    /**
     * 根据objectId更新
     * @param myTagListenner
     * @param context
     */
    public void updata(final MyTagListenner myTagListenner, final Context context,String objectId){
        if (!UtileTools.getInstance().checkNetWorkStatus(context)) {
            myTagListenner.onTagComplete(noNet, "");
            return;
        }
        ParseUser parseUser = ParseUser.getCurrentUser();
        if (parseUser != null) {
            ParseObject parseObject = new ParseObject("table_name");
            //增加访问权限
            parseObject.setObjectId(objectId);
            parseObject.setACL(new ParseACL(parseUser));
            //更新的字段名和值
            parseObject.put("table_fild", "table_value");
            parseObject.saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException arg0) {
                    if (arg0 == null) {
                        MyLog.v(TAG, "History保存成功");
                        myTagListenner.onTagComplete(success, "");
                    } else {
                        MyLog.v(TAG, "History保存失败:" + arg0.getMessage());
                        myTagListenner.onTagComplete(faild, arg0.getMessage());
                    }
                }
            });
        }
    }

    /**
     * 保存文件
     * @param context
     * @param myTagListenner
     * @param data
     */
    public void saveFile(Context context, final MyTagListenner myTagListenner,byte[] data){
        //头像文件名
        final ParseFile imageFile = new ParseFile("avatar.png", data);
        imageFile.saveInBackground(new SaveCallback() {
            public void done(ParseException arg0) {
                if (arg0 == null) {
                    MyLog.v(TAG, "文件保存成功");
                    ParseUser parseUser = ParseUser.getCurrentUser();
                    if (parseUser != null) {
                        ParseObject parseObject = new ParseObject("table_name");
                        //增加访问权限
                        parseObject.setACL(new ParseACL(parseUser));
                        //文件的字段名
                        parseObject.put("file_name", imageFile);
                        parseObject.put("table_fild", "fild_value");
                        parseObject.saveInBackground(new SaveCallback() {

                            @Override
                            public void done(ParseException arg0) {
                                if (arg0 == null) {
                                    MyLog.v(TAG, "User保存成功");
                                    myTagListenner.onTagComplete(success, "");
                                } else {
                                    MyLog.v(TAG, "User保存失败:" + arg0.getMessage());
                                    myTagListenner.onTagComplete(faild, arg0.getMessage());
                                }
                            }
                        });
                    }
                } else {
                    MyLog.v(TAG, "文件保存失败");
                }
            }
        }, new ProgressCallback() {
            public void done(Integer percentDone) {
                // Update your progress spinner here. percentDone will be between 0 and 100.
                MyLog.v(TAG, "percentDone:" + percentDone);
            }
        });
    }

    /**
     * 更新图片文件
     * @param context
     * @param myTagListenner
     * @param data
     */
    public void updataImageFile(Context context, final MyTagListenner myTagListenner, final byte[] data) {
        if (!UtileTools.getInstance().checkNetWorkStatus(context)) {
            myTagListenner.onTagComplete(noNet, "");
            return;
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery("table_name");
        query.whereEqualTo("table_fild", "fild_value");
        query.getFirstInBackground(new GetCallback<ParseObject>() {

            @Override
            public void done(final ParseObject parseObject, ParseException arg1) {
                if (arg1 == null) {
                    if (parseObject != null) {
                        final ParseFile imageFile = new ParseFile("avatar.png", data);
                        imageFile.saveInBackground(new SaveCallback() {
                            public void done(ParseException arg0) {
                                if (arg0 == null) {
                                    parseObject.put("avatarFile", imageFile);
                                    parseObject.saveInBackground(new SaveCallback() {

                                        @Override
                                        public void done(ParseException arg0) {
                                            if (arg0 == null) {
                                                MyLog.v(TAG, "更新头像成功");
                                                myTagListenner.onTagComplete(success, parseObject);
                                            } else {
                                                MyLog.v(TAG, "更新头像失败:" + arg0.getMessage());
                                                myTagListenner.onTagComplete(faild, arg0.getMessage());
                                            }
                                        }
                                    });
                                }
                            }
                        }, new ProgressCallback() {
                            public void done(Integer percentDone) {
                                // Update your progress spinner here. percentDone will be between 0 and 100.
                                MyLog.v(TAG, "percentDone:" + percentDone);
                            }
                        });
                    }
                } else {
                    myTagListenner.onTagComplete(faild, arg1.getMessage());
                }
            }
        });
    }

    /**
     * 先查询后更新字段
     * @param context
     * @param myTagListenner
     */
    public void updataFild(Context context, final MyTagListenner myTagListenner) {
        if (!UtileTools.getInstance().checkNetWorkStatus(context)) {
            myTagListenner.onTagComplete(noNet, "");
            return;
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery("table_name");
        query.whereEqualTo("table_fild", "fild_value");
        query.getFirstInBackground(new GetCallback<ParseObject>() {

            @Override
            public void done(final ParseObject parseObject, ParseException arg1) {
                if (arg1 == null) {
                    if (parseObject != null) {
                        parseObject.put("table_fild", "fild_value");
                        parseObject.saveInBackground(new SaveCallback() {

                            @Override
                            public void done(ParseException arg0) {
                                if (arg0 == null) {
                                    MyLog.v(TAG, "更新成功");
                                    myTagListenner.onTagComplete(success, "");
                                } else {
                                    MyLog.v(TAG, "更新失败:" + arg0.getMessage());
                                    myTagListenner.onTagComplete(faild, arg0.getMessage());
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 查询所有的
     * @param context
     * @param myTagListenner
     */
    private void finds(final Context context, final MyTagListenner myTagListenner) {
        if (!UtileTools.getInstance().checkNetWorkStatus(context)) {
            return;
        }
        ParseQuery<ParseObject> query_food = ParseQuery.getQuery("table_name");
        query_food.whereEqualTo("table_fild", "fild_value");
        query_food.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> parseObjects, ParseException arg1) {
                if (arg1 == null) {
                    MyLog.v(TAG, "初始食物数据完成");
                } else {
                    MyLog.v(TAG, "初始食物数据失败");
                }
            }
        });
    }

    /**
     * 查询某个字段后的数据
     * @param context
     * @param myTagListenner
     */
    private void initHistory(final Context context, final MyTagListenner myTagListenner) {
        if (!UtileTools.getInstance().checkNetWorkStatus(context)) {
            return;
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery("table_name");
        query.whereEqualTo("table_fild", ParseUser.getCurrentUser());
        query.whereGreaterThanOrEqualTo("table_fild", "fild_value");
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> arg0, ParseException arg1) {
            }
        });
    }

    /**
     * 更新本地
     * @param tableName
     * @param fieldName
     * @param fieldValue
     */
    public void updataLocalField(String tableName, final String fieldName, final int fieldValue) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereEqualTo("table_fild", "fild_value");
        query.fromLocalDatastore();//读取本地
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException arg1) {
                if (parseObject != null) {
                    if (parseObject.has(fieldName)) {
                        parseObject.put(fieldName, fieldValue);
                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException arg0) {
                                if (arg0 == null) {
                                    MyLog.v(TAG, "本地更新字段成功");
                                } else {
                                    MyLog.v(TAG, "本地更新字段失败:" + arg0.getMessage());
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 获取数量
     * @param tableName
     */
    public void getNetCount(String tableName) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereEqualTo("table_fild", "fild_value");
        query.countInBackground(new CountCallback() {

            @Override
            public void done(int arg0, ParseException arg1) {
                if (arg1 == null) {
                    MyLog.v(TAG, "数量:" + arg0);
                } else {
                }
            }
        });
    }

    /**
     * 删除
     * @param context
     * @param myTagListenner
     * @param tableName
     * @param objectId
     */
    public void deleteData(Context context,final MyTagListenner myTagListenner,String tableName,String objectId){
        if (!UtileTools.getInstance().checkNetWorkStatus(context)) {
            myTagListenner.onTagComplete(noNet, "");
            return;
        }
        final ParseObject parseObject = new ParseObject(tableName);
        //增加访问权限
        ParseUser parseUser = ParseUser.getCurrentUser();
        parseObject.setACL(new ParseACL(parseUser));
        parseObject.setObjectId(objectId);
        parseObject.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException arg0) {
                if (arg0 == null) {
                    myTagListenner.onTagComplete(success, "");
                    MyLog.v(TAG, "删除成功");
                } else {
                    myTagListenner.onTagComplete(faild, arg0.getMessage());
                    MyLog.v(TAG, "删除失败:" + arg0.getMessage());
                }
            }
        });
    }
    public boolean isTwitter(){
        boolean flag=false;
        Twitter twitter = ParseTwitterUtils.getTwitter();
        if(twitter!=null){
            String twitterId=twitter.getUserId();
            if(!TextUtils.isEmpty(twitterId)){
                flag=true;
            }
        }
        return flag;
    }
//    public void FBlogin(Context context,AccessToken accessToken,final MyTagListenner myTagListenner){
//        if(!UtileTools.getInstance().checkNetWorkStatus(context)){
//            myTagListenner.onTagComplete(ParseUtils.noNet, "");
//            return;
//        }
//        ParseFacebookUtils.logInInBackground(accessToken, new LogInCallback() {
//            @Override
//            public void done(ParseUser arg0, ParseException arg1) {
//                if (arg1 == null) {
//                    if (arg0 != null) {
//                        MyLog.e("", "登录成功");
//                        myTagListenner.onTagComplete(ParseUtils.success, arg0);
//                    } else {
//                        MyLog.e("", "登录失败");
//                        myTagListenner.onTagComplete(ParseUtils.faild, arg1.getMessage());
//                    }
//                } else {
//                    MyLog.e("", "登录失败");
//                    myTagListenner.onTagComplete(ParseUtils.faild, arg1.getMessage());
//                }
//            }
//        });
//    }

    public void FBloginPermiss(Activity activity,final MyTagListenner myTagListenner) {
        if(!UtileTools.getInstance().checkNetWorkStatus(activity)){
            myTagListenner.onTagComplete(ParseUtils.noNet, "");
            return;
        }
        //user_friends,public_profile
        List<String> permissions = Arrays.asList(
                "public_profile"
        );
        ParseFacebookUtils.logInWithReadPermissionsInBackground(activity, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    MyLog.e("MyApp", "失败Uh oh. The user cancelled the Facebook login." + err.getMessage());
                    myTagListenner.onTagComplete(ParseUtils.faild, "");
                } else {
                    MyLog.e("MyApp", "成功");
                    myTagListenner.onTagComplete(ParseUtils.success, user);
                }
            }
        });
    }
    public void TWlogin(Context context, final MyTagListenner myTagListenner){
        if(!UtileTools.getInstance().checkNetWorkStatus(context)){
            myTagListenner.onTagComplete(ParseUtils.noNet, "");
            return;
        }
        ParseTwitterUtils.logIn(context, new LogInCallback() {

            @Override
            public void done(ParseUser arg0, ParseException arg1) {
                if (arg1 == null) {
                    if (arg0 != null) {
                        MyLog.e("", "twitter登录成功");
                        myTagListenner.onTagComplete(ParseUtils.success, arg0);
                    } else {
                        MyLog.e("", "twitter登录失败");
                        myTagListenner.onTagComplete(ParseUtils.faild, arg1.getMessage());
                    }
                } else {
                    MyLog.e("", "twitter登录失败");
                    myTagListenner.onTagComplete(ParseUtils.faild, arg1.getMessage());
                }
            }
        });
    }
//    public void getTwitterImage(Context context,final MyTagListenner myTagListenner){
//        final Twitter twitter = ParseTwitterUtils.getTwitter();
//        TwitterUtil.getInstance(context).getImage(
//                twitter.getConsumerKey(), twitter.getConsumerSecret(),
//                twitter.getAuthToken(), twitter.getAuthTokenSecret(), new MyTagListenner() {
//                    @Override
//                    public void onTagComplete(String values, Object object) {
//                        if (values.equals(ParseUtils.success)) {
//                            MyLog.e("", "获取twitter头像成功");
//                            myTagListenner.onTagComplete(ParseUtils.success, object);
//                        } else {
//                            MyLog.e("", "获取twitter头像失败");
//                            myTagListenner.onTagComplete(ParseUtils.faild, "");
//                        }
//                    }
//                });
//    }
    public void linkTwitter(final ParseUser parseUser,Context context,final MyTagListenner myTagListenner){
        ParseTwitterUtils.link(parseUser, context, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (ParseTwitterUtils.isLinked(parseUser)) {
                    myTagListenner.onTagComplete(ParseUtils.success, "");
                } else {
                    myTagListenner.onTagComplete(ParseUtils.faild, "");
                }
            }
        });
    }
    public void linkFBPermiss(final ParseUser parseUser,Activity activity,final MyTagListenner myTagListenner){
        List<String> permissions = Arrays.asList(
                "public_profile"
        );
        ParseFacebookUtils.linkWithReadPermissionsInBackground(parseUser, activity, permissions, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(ParseFacebookUtils.isLinked(parseUser)){
                    myTagListenner.onTagComplete(ParseUtils.success, "");
                } else {
                    myTagListenner.onTagComplete(ParseUtils.faild, "");
                }
            }
        });
    }
//    public void linkFB(final ParseUser parseUser,AccessToken accessToken,final MyTagListenner myTagListenner){
//        ParseFacebookUtils.linkInBackground(parseUser, accessToken, new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (ParseFacebookUtils.isLinked(parseUser)) {
//                    myTagListenner.onTagComplete(ParseUtils.success, "");
//                } else {
//                    myTagListenner.onTagComplete(ParseUtils.faild, "");
//                }
//            }
//        });
//    }
    public void isLink(final MyTagListenner myTagListenner){
        if(ParseTwitterUtils.isLinked(ParseUser.getCurrentUser())){
            myTagListenner.onTagComplete(ParseUtils.success,"twitter");
        }else if(ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())){
            myTagListenner.onTagComplete(ParseUtils.success,"faceBook");
        }else{
            myTagListenner.onTagComplete(ParseUtils.success,"nolink");
        }
    }
    public void logOut(final Context context,final MyTagListenner myTagListenner){
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException arg0) {
                if (arg0 == null) {
                    myTagListenner.onTagComplete(ParseUtils.success, "");
                } else {
                    myTagListenner.onTagComplete(ParseUtils.faild, arg0.getMessage());
                }
            }
        });
    }
    public void getFBHashKey(Context context){
        try {
            PackageInfo info =context.getPackageManager().getPackageInfo("com.app.weightdiet", PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign= Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("MY KEY HASH", "sign:" + sign);//FVo8wmCWMry54hmX4CdM0xiODvw=
            }
        } catch (Exception e) {
        }
    }
}
