package com.app.studiodemo.socialdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.app.studiodemo.socialdemo.R;
import com.app.studiodemo.socialdemo.activity.email.EmailTool;

/**
 * Created by Administrator on 2016/2/19.
 */
public class EmailActivity extends Activity{
    private Button email_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        email_text=(Button)findViewById(R.id.email_text);
        email_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }
    private void sendEmail(){
        EmailTool.getInstance().sendMessageByEmail(this, "发送", getString(R.string.app_name));
    }
}
