package com.practice.olegtojgildin.layoutspractice_meet_5;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * Created by olegtojgildin on 23/12/2018.
 */


public class MyIntentService extends IntentService {

    public static final Intent newIntent(Context context) {
        return new Intent(context, MyIntentService.class);
    }

    public static final String ACTION = "sendInfo";

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent arg0) {
        for (int i = 0; i < 1000; i++) {
            Intent in = new Intent(ACTION);
            in.putExtra("Info", i);
            LocalBroadcastManager.getInstance(this).sendBroadcast(in);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

