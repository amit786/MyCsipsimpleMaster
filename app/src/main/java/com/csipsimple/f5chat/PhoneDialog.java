package com.csipsimple.f5chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.csipsimple.R;
import com.csipsimple.f5chat.utility.PreferenceConstants;
import com.csipsimple.f5chat.utility.SharedPrefrence;
import com.csipsimple.f5chat.utility.Utils;
import com.csipsimple.f5chat.view.OpenLightTextView;
import com.csipsimple.f5chat.view.OpenRegularButton;

/**
 * Created by HP on 24-05-2016.
 */
public class PhoneDialog extends Activity {
    private static final int READ_CONTACTS = 0;
    private boolean isReadContactPermission = false;
    // String url = "https://85.214.17.140/F5chat/send_otp.php";
    //   String parameters,num;
    //ProgressDialog pd;
    OpenLightTextView tvnum;
    OpenRegularButton bagree;
    OpenRegularButton bedit;
    SharedPrefrence share;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        // ...but notify us that it happened.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        setContentView(R.layout.confirm_phone_dialog);
      //  isReadContactPermission = Utils.hasPermissionInManifest(PhoneDialog.this, READ_CONTACTS, Manifest.permission.READ_CONTACTS);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions
            //Toast.makeText(PhoneDialog.this, "greater"+currentapiVersion, Toast.LENGTH_SHORT).show();
        } else {
            // do something for phones running an SDK before lollipop
            //Toast.makeText(PhoneDialog.this, "less"+currentapiVersion, Toast.LENGTH_SHORT).show();
        }
        share = SharedPrefrence.getInstance(getApplicationContext());
        this.setFinishOnTouchOutside(false);
        tvnum = (OpenLightTextView) findViewById(R.id.tvnumber);
        bagree = (OpenRegularButton) findViewById(R.id.btagree);
        bedit = (OpenRegularButton) findViewById(R.id.btedit);
//        Bundle in = getIntent().getExtras();
//        if (in != null) {
//            num = in.getString("number");
//            tvnum.setText(num);
//        }
        tvnum.setText("+"+share.getValue(SharedPrefrence.COUNTRY_CODE)+share.getValue(SharedPrefrence.MOBILE));

        bagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // new ContactSyncAsyncTask(PhoneDialog.this).execute();
//
//
//                String mob = num.replace("+", "");
//                String numwithcode = num.replace("+", "").trim();
//                Random r = new Random();
//                int Low = 100000;
//                int High = 999999;
//                int confirmation = r.nextInt(High - Low) + Low;
//                String devicetoken = Chatutility.getDeviceToken(PhoneDialog.this);
//
//                share.setValue(SharedPrefrence.OTP, String.valueOf(confirmation));
//                share.setValue(SharedPrefrence.MOBILE, String.valueOf(numwithcode));
//                share.setValue(SharedPrefrence.DEVICETOKEN, String.valueOf(devicetoken));
//                String mob = num.replace("+", "");
//                String numwithcode = num.replace("+", "").trim();
//                Random r = new Random();
//                int Low = 100000;
//                int High = 999999;
//                int confirmation = r.nextInt(High - Low) + Low;
//                String devicetoken = Chatutility.getDeviceToken(PhoneDialog.this);
//
//                share.setValue(SharedPrefrence.OTP, String.valueOf(confirmation));
//                share.setValue(SharedPrefrence.MOBILE, String.valueOf(numwithcode));
//                share.setValue(SharedPrefrence.DEVICETOKEN, String.valueOf(devicetoken));
//
//                parameters = "mobile_no=" + mob + "&confirmation_code=" + confirmation;
//                new SendOtp().execute(PreferenceConstants.SEND_OTP, parameters);
                setResult(PreferenceConstants.phoneDialogCode);
                finish();
            }
        });
        bedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(PreferenceConstants.phoneEdit);
                finish();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case READ_CONTACTS:
                if (!isReadContactPermission) {
                    Utils.showAlertDialog(PhoneDialog.this, "Permission Required", getString(R.string.read_contact_warning_msg), "Ok", null);

                }

                break;
        }
    }

//    class SendOtp extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... params) {
//            // TODO Auto-generated method stub
//            String result = Chatutility.excutePost(PreferenceConstants.SEND_OTP, parameters);
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            pd.dismiss();
//            try {
//                JSONObject obj = new JSONObject(result);
//                String status = obj.getString("status");
//                String message = obj.getString("message");
//                if (status.equals("0")) {
//                    Toast.makeText(PhoneDialog.this, message, Toast.LENGTH_LONG).show();
//                } else {
//                    // new ContactSyncAsyncTask(PhoneDialog.this).execute();
//                    // Toast.makeText(PhoneDialog.this,message,Toast.LENGTH_LONG).show();
//                    Intent inverification = new Intent(PhoneDialog.this, Verification.class);
//                    startActivity(inverification);
//                    PreferenceConstants.phoneNumber.finish();
//                    finish();
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                Toast.makeText(PhoneDialog.this, "Please try later...", Toast.LENGTH_LONG).show();
//            }
//        }
//
//    }

    @Override
    public void onBackPressed() {
        finish();
    }
}


