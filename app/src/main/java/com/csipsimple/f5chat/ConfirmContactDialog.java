package com.csipsimple.f5chat;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.csipsimple.R;
import com.csipsimple.f5chat.background.ContactSyncAsyncTask;
import com.csipsimple.f5chat.utility.PreferenceConstants;

/**
 * Created by HP on 02-06-2016.
 */
public class ConfirmContactDialog extends Activity {
    com.csipsimple.f5chat.view.OpenRegularButton okbtn;

    RelativeLayout cross;

    // ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //  pd=new ProgressDialog(ConfirmContactDialog.this);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.confirm_contact_dialog);
        this.setFinishOnTouchOutside(false);
        okbtn = (com.csipsimple.f5chat.view.OpenRegularButton) findViewById(R.id.okbtn);
        cross = (RelativeLayout) findViewById(R.id.cross);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent allContactIntent = new Intent(ConfirmContactDialog.this, CustomViewIconTextTabsActivity.class);
//                startActivity(allContactIntent);

                setResult(PreferenceConstants.CONFIRM_READ_CONTACT);
                finish();
                //finish();
            }
        });
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent in=new Intent(ConfirmContactDialog.this,CustomViewIconTextTabsActivity.class);
                new ContactSyncAsyncTask(getApplicationContext()).execute();
                setResult(PreferenceConstants.CONFIRM_READ_CONTACT);
                finish();
            }
        });
    }
}
