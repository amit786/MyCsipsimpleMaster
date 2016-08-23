package com.csipsimple.f5chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.csipsimple.R;
import com.csipsimple.f5chat.http.HTTPPost;
import com.csipsimple.f5chat.rooster.RoosterConnectionService;
import com.csipsimple.f5chat.utility.Chatutility;
import com.csipsimple.f5chat.utility.DialogUtility;
import com.csipsimple.f5chat.utility.PreferenceConstants;
import com.csipsimple.f5chat.utility.SharedPrefrence;
import com.csipsimple.f5chat.utility.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SHRIG on 5/24/2016.
 */
public class Verification extends Activity {

    private static final String TAG = "VerificationActivity";
    SharedPrefrence share;
    com.csipsimple.f5chat.font_family.Font_Welcome_scr_Tv_light requestpin;
    int counter = 0;
    String otp;
    com.csipsimple.f5chat.font_family.Font_Welcome_scr_Tv_light nxt;
    TextView bt0, bt1, bt2, bt3, bt4, bt5, bt6, bt7, bt8, bt9;
    LinearLayout imgclear;
    ImageView img1, img2, img3, img4, img5, img6;
    com.csipsimple.f5chat.font_family.Font_Welcome_scr_Tv_light num1;
    com.csipsimple.f5chat.font_family.Font_Welcome_scr_Tv_light num2;
    com.csipsimple.f5chat.font_family.Font_Welcome_scr_Tv_light num3;
    com.csipsimple.f5chat.font_family.Font_Welcome_scr_Tv_light num4;
    com.csipsimple.f5chat.font_family.Font_Welcome_scr_Tv_light num5;
    com.csipsimple.f5chat.font_family.Font_Welcome_scr_Tv_light num6;
    ImageView back;
    LinearLayout backlayout;
    String s = "";
    Button submit_btn;
    String resendTxt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Chatutility.changeStatusBarCustomColor(this, "#2c3342");

        }
        // Chatutility.changeStatusBarColor(this);
        super.setContentView(R.layout.varification_scr);
        share = SharedPrefrence.getInstance(getApplicationContext());
        back = (ImageView) findViewById(R.id.back);
        requestpin = (com.csipsimple.f5chat.font_family.Font_Welcome_scr_Tv_light) findViewById(R.id.requestpin);
        nxt = (com.csipsimple.f5chat.font_family.Font_Welcome_scr_Tv_light) findViewById(R.id.next);
        submit_btn = (Button) findViewById(R.id.submit_btn);
        backlayout = (LinearLayout) findViewById(R.id.backlayout);
        bt0 = (TextView) findViewById(R.id.bt0);
        bt1 = (TextView) findViewById(R.id.bt1);
        bt2 = (TextView) findViewById(R.id.bt2);
        bt3 = (TextView) findViewById(R.id.bt3);
        bt4 = (TextView) findViewById(R.id.bt4);
        bt5 = (TextView) findViewById(R.id.bt5);
        bt6 = (TextView) findViewById(R.id.bt6);
        bt7 = (TextView) findViewById(R.id.bt7);
        bt8 = (TextView) findViewById(R.id.bt8);
        bt9 = (TextView) findViewById(R.id.bt9);
        imgclear = (LinearLayout) findViewById(R.id.imgclr);
        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);
        img4 = (ImageView) findViewById(R.id.img4);
        img5 = (ImageView) findViewById(R.id.img5);
        img6 = (ImageView) findViewById(R.id.img6);
        num1 = (com.csipsimple.f5chat.font_family.Font_Welcome_scr_Tv_light) findViewById(R.id.num1);
        num2 = (com.csipsimple.f5chat.font_family.Font_Welcome_scr_Tv_light) findViewById(R.id.num2);
        num3 = (com.csipsimple.f5chat.font_family.Font_Welcome_scr_Tv_light) findViewById(R.id.num3);
        num4 = (com.csipsimple.f5chat.font_family.Font_Welcome_scr_Tv_light) findViewById(R.id.num4);
        num5 = (com.csipsimple.f5chat.font_family.Font_Welcome_scr_Tv_light) findViewById(R.id.num5);
        num6 = (com.csipsimple.f5chat.font_family.Font_Welcome_scr_Tv_light) findViewById(R.id.num6);

        submit_btn.setEnabled(false);
        submit_btn.setAlpha(0.5f);
        nxt.setEnabled(false);
        nxt.setAlpha(0.5f);

        otp = share.getValue(SharedPrefrence.OTP);
        Toast.makeText(Verification.this, "OTP is " + otp, Toast.LENGTH_SHORT).show();
        requestpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regenerateOTP();
            }
        });

        backlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoBack();
            }
        });

        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        bt0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s = "0";
                increment(counter);
            }
        });
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s = "1";
                increment(counter);
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s = "2";
                increment(counter);
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s = "3";
                increment(counter);
            }
        });
        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s = "4";
                increment(counter);
            }
        });
        bt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s = "5";
                increment(counter);
            }
        });
        bt6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s = "6";
                increment(counter);
            }
        });
        bt7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s = "7";
                increment(counter);
            }
        });
        bt8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s = "8";
                increment(counter);
            }
        });
        bt9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s = "9";
                increment(counter);
            }
        });
        imgclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Decrement(counter);
            }
        });
    }

    public void increment(int c) {
        try {
            if (c == 0 || c < 6) {
                if (counter < 6) {
                    nxt.setEnabled(false);
                    nxt.setAlpha(0.5f);
                    submit_btn.setEnabled(false);
                    submit_btn.setAlpha(0.5f);
                }
                if (counter == 0) {
                    c++;
                    num1.setText(s);
                    img1.setVisibility(View.INVISIBLE);
                }
                if (counter == 1) {
                    c++;
                    num2.setText(s);
                    img2.setVisibility(View.INVISIBLE);
                }
                if (counter == 2) {
                    c++;
                    num3.setText(s);
                    img3.setVisibility(View.INVISIBLE);
                }
                if (counter == 3) {
                    c++;
                    num4.setText(s);
                    img4.setVisibility(View.INVISIBLE);
                }
                if (counter == 4) {
                    c++;
                    num5.setText(s);
                    img5.setVisibility(View.INVISIBLE);
                }
                if (counter == 5) {
                    c++;

                    num6.setText(s);
                    img6.setVisibility(View.INVISIBLE);
                    nxt.setEnabled(true);
                    nxt.setAlpha(1f);
                    submit_btn.setEnabled(true);
                    submit_btn.setAlpha(1f);
                    // new ContactSyncAsyncTask(context).execute();
                    submit_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String number = num1.getText().toString() + num2.getText().toString() + num3.getText().toString() + num4.getText().toString() + num5.getText().toString() + num6.getText().toString();
                            if (otp.equals(number)) {
                                submit();

                            } else {
                                Toast.makeText(Verification.this, "Please enter correct OTP", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    nxt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String number = num1.getText().toString() + num2.getText().toString() + num3.getText().toString() + num4.getText().toString() + num5.getText().toString() + num6.getText().toString();
                            if (otp.equals(number)) {
                                submit();
                            } else {
                                Toast.makeText(Verification.this, "Please enter correct OTP", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    String number = num1.getText().toString() + num2.getText().toString() + num3.getText().toString() + num4.getText().toString() + num5.getText().toString() + num6.getText().toString();
                    if (otp.equals(number)) {
                        submit();
                    } else {
                        Toast.makeText(Verification.this, "Please enter correct OTP", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            counter++;


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Decrement(int c) {
        if (c > 0 || c == 6) {
            if (counter <= 6) {
                nxt.setEnabled(false);
                nxt.setAlpha(0.5f);
                submit_btn.setEnabled(false);
                submit_btn.setAlpha(0.5f);
            }
            if (c == 1) {
                num1.setText("");
                img1.setVisibility(View.VISIBLE);

            }
            if (c == 2) {
                num2.setText("");
                img2.setVisibility(View.VISIBLE);
            }
            if (c == 3) {
                num3.setText("");
                img3.setVisibility(View.VISIBLE);
            }
            if (c == 4) {
                num4.setText("");
                img4.setVisibility(View.VISIBLE);
            }
            if (c == 5) {
                num5.setText("");
                img5.setVisibility(View.VISIBLE);
            }
            if (c >= 6) {
                counter = 6;
                num6.setText("");
                img6.setVisibility(View.VISIBLE);
            }
            counter--;
        }
    }



    private void saveCredentialsAndLogin()
    {
        share.setValue(SharedPrefrence.JID, Utils.getUserId(this) + PreferenceConstants.SERVERATTHERSTE);
        share.setValue(SharedPrefrence.PASSWORD, getDeviceToken());
        share.setValue(SharedPrefrence.ISLOGIN, "true");

        //Start the service
        Intent i1 = new Intent(this, RoosterConnectionService.class);
        startService(i1);
        Intent inprofile = new Intent(Verification.this, ProfileActivity.class);
        startActivity(inprofile);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                String action = intent.getAction();
//                switch (action) {
//                    case RoosterConnectionService.UI_AUTHENTICATED:
//                        Log.d(TAG, "Got a broadcast to show the main app window");
//                        DialogUtility.pauseProgressDialog();
////                        Intent inprofile = new Intent(Verification.this, ProfileActivity.class);
////                        //inprofile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                        startActivity(inprofile);
////                        finish();
//                        break;
//
//                }
//
//            }
//        };
//        IntentFilter filter = new IntentFilter(RoosterConnectionService.UI_AUTHENTICATED);
//        this.registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //this.unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public void regenerateOTP()
    {
        resendOTP();
        for (int i = 6; i > 0; i--) {
            Decrement(i);
        }
    }

    public void submit() {
        DialogUtility.showProgressDialog(this, true, "Processing...");
        registerUser();
    }

    @Override
    public void onBackPressed() {
        gotoBack();
    }

    public void gotoBack() {
        Intent i = new Intent(Verification.this, PhoneNumber.class);
        startActivity(i);
        finish();
    }

    public void registerUser() {

        if (Utils.checkInternetConn(this)) {
            {
                try {
                    DialogUtility.showProgressDialog(this, false, getString(R.string.please_wait));
                    new HTTPPost(this, getMemberJID()).execute(PreferenceConstants.REGISTRATION, PreferenceConstants.REGISTRATION_RESPONSE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Utils.showAlertDialog(this, getString(R.string.alert), getString(R.string.checknet));
        }
    }

    private List<NameValuePair> getMemberJID() {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        nameValuePair.add(new BasicNameValuePair("user_id", Utils.getUserId(this)));
        nameValuePair.add(new BasicNameValuePair("mobile_no",Utils.getMobile(this)));
        nameValuePair.add(new BasicNameValuePair("password", getDeviceToken()));
        nameValuePair.add(new BasicNameValuePair("device_id", getDeviceToken()));
        nameValuePair.add(new BasicNameValuePair("device_token", getDeviceToken()));
        nameValuePair.add(new BasicNameValuePair("device_type", PreferenceConstants.DEVICE_TYPE));
        return nameValuePair;
    }

    public String getOTP() {
        return share.getValue(SharedPrefrence.OTP);
    }



    public String getDeviceToken() {
        return share.getValue(SharedPrefrence.DEVICETOKEN);
    }

    public void receiveResponse() {
        saveCredentialsAndLogin();
        DialogUtility.pauseProgressDialog();
    }

    public void receiveResponse(String msg) {
        DialogUtility.showToast(this, msg);
        DialogUtility.pauseProgressDialog();
        saveCredentialsAndLogin();
    }

    public void resendOTP() {

        if (Utils.checkInternetConn(this)) {
            {
                try {
                    resendTxt = String.valueOf(Utils.generateRandomNumber());
                    DialogUtility.showProgressDialog(this, true, getString(R.string.please_wait));
                    new HTTPPost(this, getOTParam()).execute(PreferenceConstants.RESEND_SEND_OTP, PreferenceConstants.RESEND_SEND_OTP_RESPONSE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Utils.showAlertDialog(this, getString(R.string.alert), getString(R.string.checknet));
        }
    }

    private List<NameValuePair> getOTParam() {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        nameValuePair.add(new BasicNameValuePair("mobile_no", Utils.getMobile(this)));
        nameValuePair.add(new BasicNameValuePair("confirmation_code", resendTxt));
        return nameValuePair;
    }

    public void receiveResponseResendOTP() {
        DialogUtility.pauseProgressDialog();
        share.setValue(SharedPrefrence.OTP, String.valueOf(resendTxt));
        otp = resendTxt;
        Toast.makeText(Verification.this, "OTP is " + resendTxt, Toast.LENGTH_LONG).show();
    }

    public void receiveResponseResendOTP(String msg) {
        DialogUtility.pauseProgressDialog();
        DialogUtility.showToast(this, msg);
    }


}

