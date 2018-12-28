package com.practice.olegtojgildin.layoutspractice_meet_5;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by olegtojgildin on 28/12/2018.
 */


public class MyService extends Service {

    public static final int MESSAGE_REGISTER_CLIENT = 0;
    public static final int MESSAGE_UNREGISTER_CLIENT = 1;
    public static final int MESSAGE_INFO = 3;
    public static final int MESSAGE_SET_VALUE = 4;
private boolean isBind;
    private List<Messenger> mClient=new ArrayList<Messenger>();
    private Messenger mMessenger = new Messenger(new IncomingHandler());


    public static final Intent newIntent(Context context){
        return new Intent(context,MyService.class);
    }

    @Override
    public void onCreate() {
        InfoGenerator.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    private Thread InfoGenerator = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                sendToClients(Message.obtain(null, MESSAGE_INFO, i));
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    });

    private void sendToClients(Message message) {
        for (Messenger messenger : mClient) {
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private class IncomingHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MESSAGE_REGISTER_CLIENT:
                    mClient.add(msg.replyTo);
                    break;
                case MESSAGE_UNREGISTER_CLIENT:
                    mClient.remove(msg.replyTo);
                    break;
                case MESSAGE_SET_VALUE:
                    break;
            }
        }
    }
}
