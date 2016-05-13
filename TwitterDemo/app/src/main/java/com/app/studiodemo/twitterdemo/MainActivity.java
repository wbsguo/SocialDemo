package com.app.studiodemo.twitterdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.twitter.OnTwitterCallBackListener;
import com.app.twitter.TwitterConstant;
import com.app.twitter.TwitterUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final int TWITTER_OAUTH_REQUESTCODE = 100;
    private RelativeLayout twitter_relay;
    private TextView twitter_username;
    private ImageView twitter_phto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        twitter_relay = (RelativeLayout) findViewById(R.id.twitter_relay);
        twitter_username = (TextView) findViewById(R.id.twitter_username);
        twitter_phto = (ImageView) findViewById(R.id.twitter_phto);
        viewTwitter();
        twitter_relay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                twitter_click();
            }
        });
    }
    private void viewTwitter(){
        boolean isAuthorization = TwitterUtil.getInstance(this).isAuthorization();
        if(isAuthorization){
            String twitterName = TwitterUtil.getInstance(this).getNameFromPreferences();
            String twitterImag = TwitterUtil.getInstance(this).getImagFromPreferences();
            if(!TextUtils.isEmpty(twitterImag)){
                ImageLoader.getInstance().displayImage(twitterImag, twitter_phto);
            }
            twitter_username.setText(twitterName);
        }
    }
    private void twitter_click(){
        boolean isAuthorization = TwitterUtil.getInstance(this).isAuthorization();
        if(isAuthorization){
            TwitterUtil.getInstance(this).unBind();
            twitter_username.setText("");
            twitter_phto.setImageResource(R.drawable.sns_twitter);
        }else{
            //去授权twitter
            TwitterUtil.getInstance(this).getRequestToken(new OnTwitterCallBackListener() {

                @Override
                public void onSuccess() {
                    TwitterUtil.getInstance(MainActivity.this).goToAuthorization(MainActivity.this, TWITTER_OAUTH_REQUESTCODE);
                }

                @Override
                public void onSelfId(Long id) {

                }

                @Override
                public void onFriends(ArrayList<String> list) {

                }

                @Override
                public void onError() {
                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == TWITTER_OAUTH_REQUESTCODE) {
            String oauth_verifier = data.getStringExtra(TwitterConstant.OAUTH_VERIFIER);
            // 获取授权accessToken 这里要转菊花
            TwitterUtil.getInstance(this).getAccessToken(oauth_verifier,
                    new OnTwitterCallBackListener() {

                        @Override
                        public void onSuccess() {
                            // 获取displayName(昵称)
                            TwitterUtil.getInstance(MainActivity.this).getScreenName(
                                    new OnTwitterCallBackListener() {

                                        @Override
                                        public void onSuccess() {
                                            viewTwitter();
                                        }

                                        @Override
                                        public void onSelfId(Long id) {
                                        }

                                        @Override
                                        public void onFriends(
                                                ArrayList<String> list) {
                                        }

                                        @Override
                                        public void onError() {
                                        }
                                    });
                        }

                        @Override
                        public void onSelfId(Long id) {
                        }

                        @Override
                        public void onFriends(ArrayList<String> list) {
                        }

                        @Override
                        public void onError() {
                        }

                    });

        }
    }
}
