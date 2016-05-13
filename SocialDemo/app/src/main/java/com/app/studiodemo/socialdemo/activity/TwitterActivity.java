package com.app.studiodemo.socialdemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.studiodemo.socialdemo.R;
import com.app.studiodemo.socialdemo.activity.listenner.MyTagListenner;
import com.app.studiodemo.socialdemo.activity.twitter.OnTwitterCallBackListener;
import com.app.studiodemo.socialdemo.activity.twitter.TwitterConstant;
import com.app.studiodemo.socialdemo.activity.twitter.TwitterUtil;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/2/18.
 */
public class TwitterActivity extends Activity {
    private Button login, login_out, share_text, share_imag;
    private TextView name;
    private ImageView vatar_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter);
        findUI();
        initView();
    }

    private void findUI() {
        login = (Button) findViewById(R.id.login);
        login_out = (Button) findViewById(R.id.login_out);
        share_text = (Button) findViewById(R.id.share_text);
        share_imag = (Button) findViewById(R.id.share_imag);
        name = (TextView) findViewById(R.id.name);
        vatar_image = (ImageView) findViewById(R.id.vatar_image);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        login_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwitterUtil.getInstance(TwitterActivity.this).unBind();
                clearDatas();
            }
        });
        share_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share_message();
            }
        });
    }

    private void clearDatas() {
        name.setText("");
        vatar_image.setImageResource(R.mipmap.ic_launcher);
    }

    private void login() {
        boolean isAuthorization = TwitterUtil.getInstance(this).isAuthorization();
        if (isAuthorization) {
            TwitterUtil.getInstance(this).unBind();
            name.setText("");
            vatar_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            //去授权twitter
            TwitterUtil.getInstance(this).getRequestToken(new OnTwitterCallBackListener() {

                @Override
                public void onSuccess() {
                    TwitterUtil.getInstance(TwitterActivity.this).goToAuthorization(TwitterActivity.this, TwitterUtil.TWITTER_OAUTH_REQUESTCODE);
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

    private void initView() {
        viewTwitter();
    }

    private void viewTwitter() {
        boolean isAuthorization = TwitterUtil.getInstance(this).isAuthorization();
        if (isAuthorization) {
            String twitterName = TwitterUtil.getInstance(this).getNameFromPreferences();
            String twitterImag = TwitterUtil.getInstance(this).getImagFromPreferences();
            if (!TextUtils.isEmpty(twitterImag)) {
                Glide.with(this).load(Uri.parse(twitterImag)).centerCrop().into(vatar_image);
            }
            name.setText(twitterName);
        }
    }

    private void share_message() {
        TwitterUtil.getInstance(this).sendMessageByTwitter(this, "分享", new MyTagListenner() {
            @Override
            public void onTagComplete(String values, Object object) {
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == TwitterUtil.TWITTER_OAUTH_REQUESTCODE) {
            twitterResult(data);
        }
    }

    private void twitterResult(Intent data) {
        String oauth_verifier = data.getStringExtra(TwitterConstant.OAUTH_VERIFIER);
        // 获取授权accessToken 这里要转菊花
        TwitterUtil.getInstance(this).getAccessToken(oauth_verifier,
                new OnTwitterCallBackListener() {

                    @Override
                    public void onSuccess() {
                        // 获取displayName(昵称)
                        TwitterUtil.getInstance(TwitterActivity.this).getScreenName(
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
