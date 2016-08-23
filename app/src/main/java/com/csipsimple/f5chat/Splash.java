package com.csipsimple.f5chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import com.csipsimple.R;
import com.csipsimple.f5chat.database.DatabaseHandler;
import com.csipsimple.f5chat.rooster.RoosterConnectionService;
import com.csipsimple.f5chat.utility.SharedPrefrence;
import com.csipsimple.f5chat.utility.Utils;
import com.csipsimple.ui.TabsViewPagerFragmentActivity;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;


/**
 * Created by Kashish1 on 7/5/2016.
 */
public class Splash extends FragmentActivity {

   // private BroadcastReceiver mBroadcastReceiver;
    private static final String TAG = "SPLASH";
    DatabaseHandler db;
    SharedPrefrence share;

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        initImageLoader(this);
        share = SharedPrefrence.getInstance(this);
        db = new DatabaseHandler(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkLogin();
            }
        }, SPLASH_TIME_OUT);
    }

    public void checkLogin() {

        if (getLogin()) {
            //Start the service
            if (Utils.isMyServiceRunning(RoosterConnectionService.class, getApplicationContext())) {
                Intent i1 = new Intent(this, RoosterConnectionService.class);
                stopService(i1);
            }
            Intent i1 = new Intent(this, RoosterConnectionService.class);
            startService(i1);


            String name = share.getValue(SharedPrefrence.NAME);
            System.out.println("<-- Names : " + name);

            if(name.equalsIgnoreCase("")) {

                Intent in = new Intent(Splash.this, ProfileActivity.class);
                startActivity(in);
                finish();
            } else {
                Intent in = new Intent(Splash.this, TabsViewPagerFragmentActivity.class);
                startActivity(in);
                finish();
            }
        } else {
            Intent in = new Intent(Splash.this, ViewPagerFragmentActivity.class);
            startActivity(in);
            finish();

        }
    }
    public boolean getLogin()
    {
        String isLogin = share.getValue(SharedPrefrence.ISLOGIN);
        if (isLogin.equals("true"))
            return true;
        else
            return false;
    }


    @Override
    protected void onPause() {
        super.onPause();
        //this.unregisterReceiver(mBroadcastReceiver);
    }
    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, "nudeCache");
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCache(new UnlimitedDiskCache(cacheDir));
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

}
