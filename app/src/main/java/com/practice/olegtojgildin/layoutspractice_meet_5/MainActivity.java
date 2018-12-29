package com.practice.olegtojgildin.layoutspractice_meet_5;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<TextView> textViewsRelative = new ArrayList<>();
    List<TextView> textViewsLinearHoriz = new ArrayList<>();
    List<TextView> textViewsLinearVert = new ArrayList<>();

    private Messenger mService;
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    TextView textViewConstraint1;
    TextView textViewConstraint2;

    private LocalBroadcastManager mLocalBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initviews();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

    }

    public void initviews() {
        textViewsRelative.add((TextView) findViewById(R.id.textview_relative1));
        textViewsRelative.add((TextView) findViewById(R.id.textview_relative2));
        textViewsRelative.add((TextView) findViewById(R.id.textview_relative3));
        textViewsRelative.add((TextView) findViewById(R.id.textview_relative4));
        textViewsRelative.add((TextView) findViewById(R.id.textview_relative5));
        textViewsRelative.add((TextView) findViewById(R.id.textview_relative6));
        textViewsRelative.add((TextView) findViewById(R.id.textview_relative7));
        textViewsRelative.add((TextView) findViewById(R.id.textview_relative8));
        textViewsRelative.add((TextView) findViewById(R.id.textview_relative9));

        textViewConstraint1 = findViewById(R.id.firstText);
        textViewConstraint2 = findViewById(R.id.secondText);

        textViewsLinearHoriz.add((TextView) findViewById(R.id.textview1_linear));
        textViewsLinearHoriz.add((TextView) findViewById(R.id.textview2_linear));
        textViewsLinearHoriz.add((TextView) findViewById(R.id.textview3_linear));

        textViewsLinearVert.add((TextView) findViewById(R.id.textview1_linearVert));
        textViewsLinearVert.add((TextView) findViewById(R.id.textview2_linearVert));
        textViewsLinearVert.add((TextView) findViewById(R.id.textview3_linearVert));

    }


    @Override
    protected void onResume() {
        super.onResume();
        bindService();

        startService(MyIntentService.newIntent(MainActivity.this));
        IntentFilter iff = new IntentFilter(MyIntentService.ACTION);
        mLocalBroadcastManager.registerReceiver(mReceiver, iff);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setDataForLinear(Integer.toString(intent.getIntExtra("Info", 0)));
        }
    };

    public void setDataForLinear(String data) {
        for (TextView textView : textViewsLinearHoriz)
            textView.setText(data);
        for (TextView textView : textViewsLinearVert)
            textView.setText(data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unBindService();
        mLocalBroadcastManager.unregisterReceiver(mReceiver);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            Message msg = Message.obtain(null, MyService.MESSAGE_REGISTER_CLIENT);
            msg.replyTo = mMessenger;
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            Log.d("SecondActivity", "disconnected");
        }
    };


    public void bindService() {
        bindService(MyService.newIntent(MainActivity.this), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unBindService() {
        Message msg = Message.obtain(null, MyService.MESSAGE_UNREGISTER_CLIENT);
        msg.replyTo = mMessenger;
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindService(mServiceConnection);
    }


    public static final Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MyService.MESSAGE_INFO: {
                    for (TextView textView : textViewsRelative)
                        textView.setText(msg.obj.toString());
                    textViewConstraint1.setText(msg.obj.toString());
                    textViewConstraint2.setText(msg.obj.toString());
                    changeAngleForClock(msg);
                    break;
                }
            }
        }

        private void changeAngleForClock(Message msg) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) textViewConstraint2.getLayoutParams();
            layoutParams.circleAngle = Integer.parseInt(msg.obj.toString()) * 10 % 360;
            textViewConstraint2.setLayoutParams(layoutParams);
        }
    }
}
