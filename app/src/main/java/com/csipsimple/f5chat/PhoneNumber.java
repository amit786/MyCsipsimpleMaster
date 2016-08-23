package com.csipsimple.f5chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csipsimple.R;
import com.csipsimple.f5chat.http.HTTPPost;
import com.csipsimple.f5chat.utility.Chatutility;
import com.csipsimple.f5chat.utility.DialogUtility;
import com.csipsimple.f5chat.utility.PreferenceConstants;
import com.csipsimple.f5chat.utility.SharedPrefrence;
import com.csipsimple.f5chat.utility.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by HP on 23-05-2016.
 */
public class PhoneNumber extends Activity implements View.OnClickListener {
    com.csipsimple.f5chat.view.OpenRegularTextView tvcname;
    com.csipsimple.f5chat.view.OpenRegularTextView tvnext;
    com.csipsimple.f5chat.view.OpenRegularTextView tvccode;
    com.csipsimple.f5chat.view.OpenRegularEditText etphonenumber;
    TextView bt0, bt1, bt2, bt3, bt4, bt5, bt6, bt7, bt8, bt9;
    LinearLayout imgclear;
    LinearLayout ll, next, layout_top;
    String phonenumber, CountryZipCode = "", number, s = "";
    String mobilenumber = "";
    LinearLayout back;
    SharedPrefrence share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Chatutility.changeStatusBarColor(this);

        // Chatutility.changeStatusBarColor(this);
        setContentView(R.layout.phone_number);
        share = SharedPrefrence.getInstance(this);
        layout_top = (LinearLayout) findViewById(R.id.layou_top);
        back = (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(this);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Chatutility.changeStatusBarCustomColor(this, "#2c3342");
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//            params.setMargins(0, 20, 0, 0);
//            layout_top.setLayoutParams(params);
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.MATCH_PARENT);
//            layoutParams.setMargins(0,20,0,0);
//            layout_top.setLayoutParams(layoutParams);

        }
        tvcname = (com.csipsimple.f5chat.view.OpenRegularTextView) findViewById(R.id.tvcountryname);
        tvccode = (com.csipsimple.f5chat.view.OpenRegularTextView) findViewById(R.id.tvccode);
        tvnext = (com.csipsimple.f5chat.view.OpenRegularTextView) findViewById(R.id.tvnext);
        imgclear = (LinearLayout) findViewById(R.id.imgclr);

        ll = (LinearLayout) findViewById(R.id.back);
        next = (LinearLayout) findViewById(R.id.Next);
        etphonenumber = (com.csipsimple.f5chat.view.OpenRegularEditText) findViewById(R.id.etphonenumber);


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

        tvnext.setEnabled(false);
        tvnext.setAlpha(0.5f);

        imgclear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                etphonenumber.setText("");
                mobilenumber="";
                tvnext.setEnabled(false);
                tvnext.setAlpha(0.5f);
                return false;
            }
        });

        updateCuntryCodeAndName();
//        //Log.e("country"+loc.getDisplayCountry(),"code");
//        for (int i = 0; i < countrycodewithname.length; i++) {
//            if (countrycodewithname[i].contains(country_name)) {
//            }
//        }

        tvcname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number = etphonenumber.getText().toString();
                share.setValue(SharedPrefrence.MOBILE, number);

                Intent selectCountry = new Intent(PhoneNumber.this, CountryCode.class);
                startActivityForResult(selectCountry, PreferenceConstants.countryCode);
            }
        });
        tvnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number = etphonenumber.getText().toString();

                String countryCode = share.getValue(SharedPrefrence.COUNTRY_CODE);
                if(countryCode != null) {

                    share.setValue(SharedPrefrence.MOBILE, number);
                    Intent sendNum = new Intent(PhoneNumber.this, PhoneDialog.class);
                    startActivityForResult(sendNum, PreferenceConstants.phoneDialogCode);

                } else {
                    DialogUtility.showToast(PhoneNumber.this, "Please select your country");

                    Intent selectCountry = new Intent(PhoneNumber.this, CountryCode.class);
                    startActivityForResult(selectCountry, PreferenceConstants.countryCode);
                }
            }

        });

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoBack();
            }
        });
        PreferenceConstants.phoneNumber = this;
    }

    public void updateCuntryCodeAndName() {
        String jj = share.getValue(SharedPrefrence.COUNTRY_NAME);
        if (share.getValue(SharedPrefrence.COUNTRY_NAME).length() == 0) {
            String[] countrycodewithname = getResources().getStringArray(R.array.country_names_arrays);
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            String countryCode = tm.getSimCountryIso().toUpperCase();
            Locale loc = new Locale("", countryCode);
            String country_name = loc.getDisplayCountry();
            share.setValue(SharedPrefrence.COUNTRY_NAME, country_name);
            tvcname.setText(country_name);
            String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
            for (int i = 0; i < rl.length; i++) {
                String[] g = rl[i].split(",");
                if (g[1].trim().equals(countryCode.trim())) {
                    CountryZipCode = g[0];
                    break;
                }
            }
            tvccode.setText(CountryZipCode);
            share.setValue(SharedPrefrence.COUNTRY_CODE, CountryZipCode.replace("+", ""));
        }
        else
        {
            tvcname.setText(share.getValue(SharedPrefrence.COUNTRY_NAME));
            tvccode.setText("+"+share.getValue(SharedPrefrence.COUNTRY_CODE));
            etphonenumber.setText(share.getValue(SharedPrefrence.MOBILE));
            mobilenumber = share.getValue(SharedPrefrence.MOBILE);
            if (etphonenumber.getText().toString().length() > 4) {
                tvnext.setEnabled(true);
                tvnext.setAlpha(1f);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        etphonenumber.setText(share.getValue(SharedPrefrence.MOBILE));
        mobilenumber = share.getValue(SharedPrefrence.MOBILE);
        //enableNext();
//        tvcname.setText(share.getValue(SharedPrefrence.COUNTRY_NAME));
//        tvccode.setText(share.getValue(SharedPrefrence.COUNTRY_NAME));
//
//        mobilenumber = share.getValue(SharedPrefrence.MOBILE);
//        etphonenumber.setText(share.getValue(SharedPrefrence.MOBILE));
//        if (etphonenumber.getText().toString().length() > 3) {
//            tvnext.setEnabled(true);
//            tvnext.setAlpha(1f);
//        }
    }

    public void increment(String c) {
        mobilenumber = mobilenumber + c;
        etphonenumber.setText(mobilenumber);
        if (etphonenumber.length() > 4) {
            tvnext.setEnabled(true);
            tvnext.setAlpha(1f);
        } else if (etphonenumber.getText().toString().length() < 5) {
            tvnext.setEnabled(false);
            tvnext.setAlpha(0.5f);
        }
    }

    public void Decrement() {
        mobilenumber = mobilenumber.substring(0, mobilenumber.length() - 1);
        etphonenumber.setText(mobilenumber);
        if (etphonenumber.length() > 4) {
            tvnext.setEnabled(true);
            tvnext.setAlpha(1f);
        } else if (etphonenumber.getText().toString().length() < 5) {
            tvnext.setEnabled(false);
            tvnext.setAlpha(0.5f);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PreferenceConstants.phoneDialogCode) {
            generateOTP();
        }
        if (resultCode == PreferenceConstants.countryCode) {
            enableNext();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt0:
                s = "0";
                increment(s);
                break;
            case R.id.bt1:
                s = "1";
                increment(s);
                break;
            case R.id.bt2:
                s = "2";
                increment(s);
                break;
            case R.id.bt3:
                s = "3";
                increment(s);
                break;
            case R.id.bt4:
                s = "4";
                increment(s);
                break;
            case R.id.bt5:
                s = "5";
                increment(s);
                break;
            case R.id.bt6:
                s = "6";
                increment(s);
                break;
            case R.id.bt7:
                s = "7";
                increment(s);
                break;
            case R.id.bt8:
                s = "8";
                increment(s);
                break;
            case R.id.bt9:
                s = "9";
                increment(s);
                break;
            case R.id.imgclr:
                if (etphonenumber.getText().toString().trim().length() > 0)
                    Decrement();
                break;
            case R.id.back:
                gotoBack();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        gotoBack();
    }

    public void gotoBack() {
        Intent i = new Intent(PhoneNumber.this, ViewPagerFragmentActivity.class);
        startActivity(i);
        finish();
    }

    public void generateOTP() {
        number = etphonenumber.getText().toString();
        String mob = number.replace("+", "");
        String numWithCode = number.replace("+", "").trim();
        int confirmation = Utils.generateRandomNumber();

        String deviceToken = Chatutility.getDeviceToken(this);

        share.setValue(SharedPrefrence.OTP, String.valueOf(confirmation));
        share.setValue(SharedPrefrence.MOBILE, String.valueOf(numWithCode));
        share.setValue(SharedPrefrence.DEVICETOKEN, deviceToken);

        confirmNumber();
    }

    private List<NameValuePair> getMemberJID() {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        nameValuePair.add(new BasicNameValuePair("mobile_no", share.getValue(SharedPrefrence.MOBILE)));
        nameValuePair.add(new BasicNameValuePair("confirmation_code", share.getValue(SharedPrefrence.OTP)));
        return nameValuePair;
    }

    public void confirmNumber() {

        if (Utils.checkInternetConn(this)) {
            {
                try {
                    DialogUtility.showProgressDialog(this, false, getString(R.string.please_wait));
                    new HTTPPost(this, getMemberJID()).execute(PreferenceConstants.SEND_OTP, PreferenceConstants.SEND_OTP_RESPONSE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Utils.showAlertDialog(this, getString(R.string.alert), getString(R.string.checknet));
        }
    }

    public void receiveResponse() {
        Intent inverification = new Intent(PhoneNumber.this, Verification.class);
        startActivity(inverification);
        finish();

    }

    public void receiveResponse(String msg) {
        DialogUtility.showToast(this, msg);
    }

    public void enableNext() {
        String kk = share.getValue(SharedPrefrence.COUNTRY_NAME);
        String kkf = share.getValue(SharedPrefrence.COUNTRY_CODE);
        tvcname.setText(share.getValue(SharedPrefrence.COUNTRY_NAME));
        tvccode.setText("+"+share.getValue(SharedPrefrence.COUNTRY_CODE));

        mobilenumber = share.getValue(SharedPrefrence.MOBILE);
        etphonenumber.setText(share.getValue(SharedPrefrence.MOBILE));
        if (etphonenumber.getText().toString().length() > 4) {
            tvnext.setEnabled(true);
            tvnext.setAlpha(1f);
        }
    }

}
