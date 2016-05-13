package com.app.studiodemo.socialdemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.studiodemo.socialdemo.R;
import com.app.studiodemo.socialdemo.activity.facebook.FacebookTool;
import com.app.studiodemo.socialdemo.activity.listenner.MyTagListenner;
import com.bumptech.glide.Glide;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.share.Sharer;

/**
 * Created by Administrator on 2016/2/18.
 */
public class FacebookActivity extends Activity {
    private Button login,login_out,share_text,share_imag;
    private TextView fbName;
    private ImageView vatar_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);
        findUI();
        facebookInit();
        initView();
    }
    private void findUI(){
        login=(Button)findViewById(R.id.login);
        login_out=(Button)findViewById(R.id.login_out);
        share_text=(Button)findViewById(R.id.share_text);
        share_imag=(Button)findViewById(R.id.share_imag);
        fbName=(TextView)findViewById(R.id.fbName);
        vatar_image=(ImageView)findViewById(R.id.vatar_image);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        login_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacebookTool.getInstance().logOut();
                clearDatas();
            }
        });
        share_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share_message();
            }
        });
        share_imag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share_phto();
            }
        });
    }
    private void clearDatas(){
        FacebookTool.getInstance().setUserName(this,"");
        FacebookTool.getInstance().setUserId(this,"");
        fbName.setText("");
        vatar_image.setImageResource(R.mipmap.ic_launcher);
    }
    private void facebookInit(){
        FacebookTool.getInstance().init(this);
    }
    private void login(){
        if(FacebookTool.getInstance().isAccessToken()){
            FacebookTool.getInstance().login(this, new MyTagListenner() {
                @Override
                public void onTagComplete(String values, Object object) {
                    if(values.equals(FacebookTool.success)){
                        initView();
                    }
                }
            });
        }else{
            FacebookTool.getInstance().loginWithReadPermissions(this, new MyTagListenner() {
                @Override
                public void onTagComplete(String values, Object object) {
                    if (values.equals(FacebookTool.success)) {
                        FacebookTool.getInstance().login(FacebookActivity.this, new MyTagListenner() {
                            @Override
                            public void onTagComplete(String values, Object object) {

                            }
                        });
                    }
                }
            });
        }
    }
    private void initView(){
        Profile profile = FacebookTool.getInstance().getUserProfile();
        if(profile!=null){
            updataUi(profile.getId(), profile.getName());
        }
    }
    private void updataUi(String userId,String userName){
        if(!TextUtils.isEmpty(userId)){
            String facebookImag = FacebookTool.getInstance().getFacebookImag(userId);
            Glide.with(this).load(Uri.parse(facebookImag)).centerCrop().into(vatar_image);
        }
        if(!TextUtils.isEmpty(userName)){
            fbName.setText(userName);
        }
    }
    private void share_message() {
        FacebookTool.getInstance().share_message(this, shareCallback);
    }
    private void share_phto() {
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        FacebookTool.getInstance().share_phto(this, shareCallback, image);
    }
    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel() {
            Log.e("", "取消分享");
        }

        @Override
        public void onError(FacebookException error) {
            Log.e("", "分享失败:"+error.getMessage());
        }

        @Override
        public void onSuccess(Sharer.Result result) {
            Log.e("", "分享成功");
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FacebookTool.facebookLogin) {
            FacebookTool.getInstance().onActivityResult(requestCode, resultCode, data);
        }else if (requestCode == FacebookTool.facebookShare) {
            FacebookTool.getInstance().onActivityResult(requestCode, resultCode, data);
        }
    }
}
