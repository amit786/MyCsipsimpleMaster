package com.csipsimple.f5chat.rooster;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.csipsimple.f5chat.utility.DialogUtility;

/**
 * Created by gakwaya on 4/28/2016.
 */
public class RoosterConnectionService extends Service {


    public static final String UI_AUTHENTICATED = "com.blikoon.rooster.uiauthenticated";
    public static final String SEND_MESSAGE = "com.blikoon.rooster.sendmessage";
    public static final String BUNDLE_MESSAGE_BODY = "b_body";
    public static final String BUNDLE_TO = "b_to";

    public static final String NEW_MESSAGE = "com.blikoon.rooster.newmessage";
    public static final String BUNDLE_FROM_JID = "b_from";
    public static final String NEW_GROUP_MESSAGE = "com.blikoon.rooster.newgroupmessage";
    public static final String BUNDLE_FROM_GROUP_JID = "group_jid";
    public static final String NOTIFY_MESSAGE = "com.blikoon.rooster.notifymessage";

    public static final String GROUP_UPDATE = "com.csipsimple.f5chat.group.update";

    public static final String PRESENCE_CHANGE = "com.csipsimple.f5chat.presence.change";

    public static com.csipsimple.f5chat.rooster.XmppConnect.ConnectionState sConnectionState;
    public static com.csipsimple.f5chat.rooster.XmppConnect.LoggedInState sLoggedInState;
    private boolean mActive;//Stores whether or not the thread is active
    private Thread mThread;
    private Handler mTHandler;//We use this handler to post messages to
    //the background thread.
    private com.csipsimple.f5chat.rooster.XmppConnect mConnection;

    public RoosterConnectionService() {

    }

    public static com.csipsimple.f5chat.rooster.XmppConnect.ConnectionState getState() {
        if (sConnectionState == null) {
            return com.csipsimple.f5chat.rooster.XmppConnect.ConnectionState.DISCONNECTED;
        }
        return sConnectionState;
    }

    public static com.csipsimple.f5chat.rooster.XmppConnect.LoggedInState getLoggedInState() {
        if (sLoggedInState == null) {
            return com.csipsimple.f5chat.rooster.XmppConnect.LoggedInState.LOGGED_OUT;
        }
        return sLoggedInState;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DialogUtility.showLOG("onCreate()");
    }

    private void initConnection() {
        DialogUtility.showLOG("<-- initConnection()");
        try {
            mConnection = com.csipsimple.f5chat.rooster.XmppConnect.getInstance();
            mConnection.initObject(getApplicationContext());
            mConnection.connect();
        } catch (Exception e) {
            DialogUtility.showLOG("<-- Something went wrong while connecting ,make sure the credentials are right and try again");
            e.printStackTrace();
            stopSelf();
        }

    }


    public void start() {
        DialogUtility.showLOG(" Service Start() function called.");
        if (!mActive) {
            mActive = true;
            if (mThread == null || !mThread.isAlive()) {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Looper.prepare();
                        mTHandler = new Handler();
                        initConnection();
                        //THE CODE HERE RUNS IN A BACKGROUND THREAD.
                        Looper.loop();

                    }
                });
                mThread.start();
            }


        }

    }

    public void stop() {
        DialogUtility.showLOG("<--stop()");
        mActive = false;
        mTHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mConnection != null) {
                    mConnection.disconnect();
                }
            }
        });

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DialogUtility.showLOG("<-- onStartCommand()");
        start();
        return Service.START_STICKY;
        //RETURNING START_STICKY CAUSES OUR CODE TO STICK AROUND WHEN THE APP ACTIVITY HAS DIED.
    }

    @Override
    public void onDestroy() {
        DialogUtility.showLOG("<-- onDestroy()");
        super.onDestroy();
        stop();
    }
}
