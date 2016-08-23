package com.csipsimple.f5chat.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csipsimple.R;
import com.csipsimple.f5chat.bean.FriendPresence;
import com.csipsimple.f5chat.database.DatabaseHandler;
import com.csipsimple.f5chat.utility.PreferenceConstants;
import com.csipsimple.f5chat.utility.SharedPrefrence;


/**
 * Created by Kashish1 on 7/4/2016.
 */
public class F5FriendDetail extends FragmentActivity {
    TextView mobileno,tv_user_name;
    ImageView chatIcon;
    String jid,mobile,user_name, countryCode;
    LinearLayout back;
    TextView tv_user_status;

    SharedPrefrence share;
    DatabaseHandler db;

    private BroadcastReceiver mBroadcastReceiver;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_contact_profile_girl);
        share = SharedPrefrence.getInstance(this);
        db = new DatabaseHandler(this);
        mobileno=(TextView)findViewById(R.id.mobileno);
        tv_user_name=(TextView)findViewById(R.id.user_name);
        tv_user_status = (TextView) findViewById(R.id.tv_user_status);
        chatIcon=(ImageView)findViewById(R.id.chat_icon);
        back=(LinearLayout)findViewById(R.id.back);

        jid = getIntent().getExtras().getString(SharedPrefrence.JID);
        mobile=getIntent().getExtras().getString("mobile");
        user_name=getIntent().getExtras().getString("user_name");
        countryCode = getIntent().getExtras().getString("countryCode");

        tv_user_name.setText(user_name);
        mobileno.setText("(+" + countryCode + ")" + mobile);

        if(!jid.contains(PreferenceConstants.SERVERATTHERSTE))
        {
            jid = jid+ PreferenceConstants.SERVERATTHERSTE;
        }

        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Inside here we start the chat activity
                Intent intent = new Intent(F5FriendDetail.this
                        , ChatWindow.class);
                intent.putExtra(SharedPrefrence.JID, jid);
                intent.putExtra("mobile", mobile);
                intent.putExtra("user_name",user_name);
                startActivity(intent);
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePresence();
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updatePresence();
            }
        };

        IntentFilter filter = new IntentFilter(com.csipsimple.f5chat.rooster.RoosterConnectionService.PRESENCE_CHANGE);
        registerReceiver(mBroadcastReceiver, filter);
    }

    public void updatePresence() {
        FriendPresence friendPresence  = db.getFriendPresence(jid);
        String fPresence = friendPresence.getMode();
        if(fPresence!=null) {
            tv_user_status.setText(friendPresence.getMode());
        } else {
            tv_user_status.setVisibility(View.GONE);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }


}
