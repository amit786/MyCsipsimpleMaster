package com.csipsimple.f5chat.group;//package com.csipsimple.f5chat.group;
//
//import android.content.res.Configuration;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.view.Window;
//
//import com.csipsimple.R;
//import com.csipsimple.f5chat.utility.PreferenceConstants;
//
//
//public class MainActivity extends AppCompatActivity {
//    private static final String TAG = MainActivity.class.getSimpleName();
//
//    // Lifecycle Method ////////////////////////////////////////////////////////////////////////////
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.act_main);
//        String uri = getIntent().getStringExtra("uri");
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction().add(R.id.container, MainFragment.getInstance(uri)).commit();
//        }
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//    }
//
//    public void startResultActivity(Uri uri) {
//        if (isFinishing()) return;
//        // Start ResultActivity
//        //startActivity(ResultActivity.createIntent(this, uri));
//        PreferenceConstants.uri = uri;
//        finish();
//
//    }
//}
