package com.csipsimple.f5chat;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csipsimple.R;
import com.csipsimple.api.ISipService;
import com.csipsimple.api.SipManager;
import com.csipsimple.api.SipProfile;
import com.csipsimple.utils.CallHandlerPlugin;
import com.csipsimple.widgets.AccountChooserButton;

/**
 * Created by Kashish1 on 7/15/2016.
 */
public class DialPad extends Activity implements View.OnClickListener {
    LinearLayout bt0, bt1, bt2, bt3, bt4, bt5, bt6, bt7, bt8, bt9, btstar, bthash;
    ImageView clear;
    TextView tv_exit, et_number;
    String s, totalNumber = "";
    int counter = 0;
    LinearLayout btncall;

    private AccountChooserButton accountChooserButton;

    private ISipService service;
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            service = ISipService.Stub.asInterface(arg1);
            /*
             * timings.addSplit("Service connected"); if(configurationService !=
             * null) { timings.dumpToLog(); }
             */
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            service = null;
        }
    };
    private Boolean isDigit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialer_first_scr);


        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(),
                "" +
                        "fonts/opensansregular.ttf");

        bt0 = (LinearLayout) findViewById(R.id.ll_bt0);
        bt1 = (LinearLayout) findViewById(R.id.ll_bt1);
        bt2 = (LinearLayout) findViewById(R.id.ll_bt2);
        bt3 = (LinearLayout) findViewById(R.id.ll_bt3);
        bt4 = (LinearLayout) findViewById(R.id.ll_bt4);
        bt5 = (LinearLayout) findViewById(R.id.ll_bt5);
        bt6 = (LinearLayout) findViewById(R.id.ll_bt6);
        bt7 = (LinearLayout) findViewById(R.id.ll_bt7);
        bt8 = (LinearLayout) findViewById(R.id.ll_bt8);
        bt9 = (LinearLayout) findViewById(R.id.ll_bt9);
        btstar = (LinearLayout) findViewById(R.id.ll_bt_star);
        bthash = (LinearLayout) findViewById(R.id.ll_bt_hash);
        clear = (ImageView) findViewById(R.id.clear);
        tv_exit = (TextView) findViewById(R.id.tv_exit);
        et_number = (TextView) findViewById(R.id.et_number);

        et_number.setTypeface(tf);
        //AutofitHelper.create(et_number);

        bt0.setOnClickListener(this);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);
        bt4.setOnClickListener(this);
        bt5.setOnClickListener(this);
        bt6.setOnClickListener(this);
        bt7.setOnClickListener(this);
        bt8.setOnClickListener(this);
        bt9.setOnClickListener(this);
        btstar.setOnClickListener(this);
        bthash.setOnClickListener(this);
        tv_exit.setOnClickListener(this);
        clear.setOnClickListener(this);
        clear.setOnLongClickListener(null);

        accountChooserButton = (AccountChooserButton) findViewById(R.id.accountChooserButton);
        btncall = (LinearLayout) findViewById(R.id.btncall);
        btncall.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.ll_bt0):
                s = "0";
                increment(counter);
                break;
            case (R.id.ll_bt1):
                s = "1";
                increment(counter);
                break;
            case (R.id.ll_bt2):
                s = "2";
                increment(counter);
                break;
            case (R.id.ll_bt3):
                s = "3";
                increment(counter);
                break;
            case (R.id.ll_bt4):
                s = "4";
                increment(counter);
                break;
            case (R.id.ll_bt5):
                s = "5";
                increment(counter);
                break;
            case (R.id.ll_bt6):
                s = "6";
                increment(counter);
                break;
            case (R.id.ll_bt7):
                s = "7";
                increment(counter);
                break;
            case (R.id.ll_bt8):
                s = "8";
                increment(counter);
                break;
            case (R.id.ll_bt9):
                s = "9";
                increment(counter);
                break;
            case (R.id.ll_bt_star):
                s = "*";
                increment(counter);
                break;
            case (R.id.ll_bt_hash):
                s = "#";
                increment(counter);
                break;
            case (R.id.clear):
                decrement();
                break;
            case (R.id.tv_exit):
                finish();
                break;
            case (R.id.btncall):
                placeCall();
                break;

        }
    }

    public void increment(int c) {
        counter++;

        totalNumber += s;
        et_number.setText(totalNumber);
    }

    public void decrement() {
        int a = counter;
        if (counter > 0) {
            if (totalNumber != null && totalNumber.length() > 0) {
                totalNumber = totalNumber.substring(0, totalNumber.length() - 1);
                et_number.setText(totalNumber);
                counter--;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(connection);
    }

    private void placeCallWithOption(Bundle b) {
        if (service == null) {
            return;
        }
        String toCall = "";
        Long accountToUse = SipProfile.INVALID_ID;
        // Find account to use
        SipProfile acc = accountChooserButton.getSelectedAccount();
        if (acc == null) {
            return;
        }

        accountToUse = acc.id;
        // Find number to dial
        toCall = et_number.getText().toString();
        if (isDigit) {
            toCall = PhoneNumberUtils.stripSeparators(toCall);
        }

//        if(accountChooserFilterItem != null && accountChooserFilterItem.isChecked()) {
//            toCall = rewriteNumber(toCall);
//        }

        if (TextUtils.isEmpty(toCall)) {
            return;
        }

        // Well we have now the fields, clear theses fields
        //digits.getText().clear();

        // -- MAKE THE CALL --//
        if (accountToUse >= 0) {
            // It is a SIP account, try to call service for that
            try {
                service.makeCallWithOptions(toCall, accountToUse.intValue(), b);
            } catch (RemoteException e) {
                // Log.e(THIS_FILE, "Service can't be called to make the call");
            }
        } else if (accountToUse != SipProfile.INVALID_ID) {
            // It's an external account, find correct external account
            CallHandlerPlugin ch = new CallHandlerPlugin(this);
            ch.loadFrom(accountToUse, toCall, new CallHandlerPlugin.OnLoadListener() {
                @Override
                public void onLoad(CallHandlerPlugin ch) {
                    placePluginCall(ch);
                }
            });
        }
    }

    private void placePluginCall(CallHandlerPlugin ch) {
        try {
            String nextExclude = ch.getNextExcludeTelNumber();
            if (service != null && nextExclude != null) {
                try {
                    service.ignoreNextOutgoingCallFor(nextExclude);
                } catch (RemoteException e) {
                    // Log.e(THIS_FILE, "Impossible to ignore next outgoing call", e);
                }
            }
            ch.getIntent().send();
        } catch (PendingIntent.CanceledException e) {
            // Log.e(THIS_FILE, "Pending intent cancelled", e);
        }
    }
    public void placeCall() {
        placeCallWithOption(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent serviceIntent = new Intent(SipManager.INTENT_SIP_SERVICE);
        // Optional, but here we bundle so just ensure we are using csipsimple package
        serviceIntent.setPackage(getPackageName());
        bindService(serviceIntent, connection,
                Context.BIND_AUTO_CREATE);
    }
}
