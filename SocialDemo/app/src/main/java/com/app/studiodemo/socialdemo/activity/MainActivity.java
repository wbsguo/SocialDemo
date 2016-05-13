package com.app.studiodemo.socialdemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.app.studiodemo.socialdemo.R;

public class MainActivity extends Activity {
    private Button facebook_test,twitter_test,line_test,email_test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findUI();
    }
    private void findUI(){
        facebook_test=(Button)findViewById(R.id.facebook_test);
        facebook_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,FacebookActivity.class);
                startActivity(intent);
            }
        });
        twitter_test=(Button)findViewById(R.id.twitter_test);
        twitter_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,TwitterActivity.class);
                startActivity(intent);
            }
        });
        line_test=(Button)findViewById(R.id.line_test);
        line_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,LineActivity.class);
                startActivity(intent);
            }
        });
        email_test=(Button)findViewById(R.id.email_test);
        email_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,EmailActivity.class);
                startActivity(intent);
            }
        });
    }
}
