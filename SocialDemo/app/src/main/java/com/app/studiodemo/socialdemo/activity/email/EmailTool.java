package com.app.studiodemo.socialdemo.activity.email;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2016/2/19.
 */
public class EmailTool {
    private static EmailTool tool;
    private EmailTool() {

    }
    public static EmailTool getInstance() {
        if (tool == null) {
            tool = new EmailTool();
        }
        return tool;
    }
    public void sendMessageByEmail(Context context, String message, String appName) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_SUBJECT, appName);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        context.startActivity(Intent.createChooser(intent, appName));
    }
}
