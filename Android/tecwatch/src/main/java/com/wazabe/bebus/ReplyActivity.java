package com.wazabe.bebus;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;

/**
 * Created by 201601 on 5/8/2015.
 */
public class ReplyActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle remoteInput = RemoteInput.getResultsFromIntent(getIntent());
        String text ="1";
        if (remoteInput != null) {
            text +="1";
            text += remoteInput.getCharSequence(MainActivity.EXTRA_VOICE_REPLY).toString();
        }
    }
}
