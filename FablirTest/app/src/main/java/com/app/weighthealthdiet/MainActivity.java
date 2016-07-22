package com.app.weighthealthdiet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.app.weighthealthdiet.twitter.TwitterHelper;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";
    private Button login,share_message,share_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login=(Button)findViewById(R.id.login);
        share_message=(Button)findViewById(R.id.share_message);
        share_image=(Button)findViewById(R.id.share_image);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        share_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareMessage();
            }
        });
        share_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==TwitterHelper.getInstance().getRequestCode()){
            TwitterHelper.getInstance().onActivityResult(requestCode, resultCode, data);
        }
    }
    private void login(){
        TwitterHelper.getInstance().login(this, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                loginByTwitter(result);
            }

            @Override
            public void failure(TwitterException e) {
            }
        });
    }
    private void loginByTwitter(final Result<TwitterSession> result) {
        TwitterHelper.getInstance().getUserInfo(new TwitterHelper.UserCallBack() {

            @Override
            public void success(Result<User> result) {
                Log.e(TAG,"name:"+result.data.name+"email:"+result.data.email+"image:"+result.data.profileImageUrl);
            }

            @Override
            public void failure(TwitterException e) {

            }
        });
    }
    private void shareMessage(){
        TwitterHelper.getInstance().sendMessageByTwitter(this,"women");
    }
    private void shareImage(){
        String path="/storage/emulated/0/GoStore/icon/-1637101839.jpg";
        TwitterHelper.getInstance().shareImageUrl(this,"文字",path);
    }
}
