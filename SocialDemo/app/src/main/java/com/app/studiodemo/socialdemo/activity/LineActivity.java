package com.app.studiodemo.socialdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.app.studiodemo.socialdemo.R;
import com.app.studiodemo.socialdemo.activity.line.LineShareTool;

/**
 * Created by Administrator on 2016/2/19.
 */
public class LineActivity extends Activity{
    private Button share_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line);
        share_text=(Button)findViewById(R.id.share_text);
        share_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share_message();
            }
        });
    }
    private void share_message() {
        LineShareTool.getInstance().shareToLineByWeb(this, "分享");
    }
}
