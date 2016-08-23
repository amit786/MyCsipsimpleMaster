package com.csipsimple.f5chat;

/**
 * Created by SHRIG on 5/23/2016.
 */
//'''''''''''''''''''this is for swipable screen '''''''''''''''''''''''''''

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.csipsimple.R;
import com.csipsimple.f5chat.bean.CountryCodeBean;
import com.csipsimple.f5chat.utility.Chatutility;
import com.csipsimple.f5chat.utility.Utils;
import com.csipsimple.ui.PagerAdapter;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;


/**
 * The <code>ViewPagerFragmentActivity</code> class is the fragment activity hosting the ViewPager
 *
 * @author mwho
 */
public class ViewPagerFragmentActivity extends FragmentActivity {
    private static final int READ_PHONE_STATE = 0;
    private static final int ACCESS_NETWORK_STATE = 1;
    private static final int READ_EXTERNAL_STORAGE = 2;
    private static final int WRITE_EXTERNAL_STORAGE = 3;
    private static final int GET_ACCOUNTS = 4;
    private static final int USE_CREDENTIALS = 5;
    private static String[] PERMISSION_ARRAY = {Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.GET_ACCOUNTS};
    private boolean isPhonePerimssionGranted=false,isWriteStoragePerimssionGranted=false,isReadStoragePerimssionGranted=false,isAccountPerimssionGranted=false;
    private int savePosition = 0;

    /**
     * maintains the pager adapter
     */
    private PagerAdapter mPagerAdapter;
    private Context mContext;
    int count = 0,currentPage = 45, totalFrag = 2;
    ImageView imageView;
    Button button;
    ViewPager pager;
    Timer timer;
    private String[] countryname,code;

    String s;
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            Chatutility.changeStatusBarCustomColor(this);

        } else{

        }
        // Chatutility.changeStatusBarColor(this);

        countryname = getResources().getStringArray(R.array.country_names_arrays);
        code = getResources().getStringArray(R.array.country_codes_arrays);
        setContentView(R.layout.view_pager_layout);
        for (int i = 0 ; i< countryname .length ; i++)
        {
            CountryCodeBean cBean = new CountryCodeBean();
            cBean.setCountryCode(code[i]);
            cBean.setCountryName(countryname[i]);
            Utils.countrylist.add(cBean);
        }
        //String cname=Utils.countrylist.get().getCountryName();
        mContext = (Context) ViewPagerFragmentActivity.this;
        imageView = (ImageView) findViewById(R.id.pager_progess);
        this.initialisePaging();
        button = (Button) findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), PhoneNumber.class);
                startActivity(i);
                finish();
            }
        });
        //initialsie the pager
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTaskPager(),1000,1500);
    }

    @Override
    protected void onResume() {
//        isPhonePerimssionGranted = Utils.hasPermissionInManifest(ViewPagerFragmentActivity.this,READ_PHONE_STATE,Manifest.permission.READ_PHONE_STATE);
//        isWriteStoragePerimssionGranted = Utils.hasPermissionInManifest(ViewPagerFragmentActivity.this,WRITE_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        isAccountPerimssionGranted = Utils.hasPermissionInManifest(ViewPagerFragmentActivity.this,READ_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE);



        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case READ_PHONE_STATE:
                if(!isPhonePerimssionGranted)
                {
                    Utils.showAlertDialog(ViewPagerFragmentActivity.this,"Permission Required",getString(R.string.read_state_warning_msg),"Ok",null);

                }

                break;
            case READ_EXTERNAL_STORAGE:
                if(!isReadStoragePerimssionGranted)
                {
                    Utils.showAlertDialog(ViewPagerFragmentActivity.this,"Permission Required",getString(R.string.read_storage_warning_msg),"Ok",null);
                }
                break;
            case WRITE_EXTERNAL_STORAGE:
                if(!isWriteStoragePerimssionGranted)
                {
                    Utils.showAlertDialog(ViewPagerFragmentActivity.this,"Permission Required",getString(R.string.write_storage_warning_msg),"Ok",null);
                }
                break;

        }

    }


    /**
     * Initialise the fragments to be paged
     */
    private void initialisePaging() {

        List<Fragment> fragments = new Vector<Fragment>();

        for(int i=0; i<=30; i++){
            fragments.add(Fragment.instantiate(this, Welcome_one.class.getName()));
            fragments.add(Fragment.instantiate(this, Welcome_two.class.getName()));
            fragments.add(Fragment.instantiate(this, Welcome_three.class.getName()));
        }

        this.mPagerAdapter = new PagerAdapter(super.getSupportFragmentManager(), fragments);
        count = mPagerAdapter.getCount();
        Log.e("Count ", count + "--");
        //
        pager = (ViewPager) super.findViewById(R.id.viewpager);

//        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//            //            '''''''''''''''''this is for swipable DOTS arrangement /set position of dots'''''''''''''''
//            private static final float thresholdOffset = 0.5f;
//            private boolean scrollStarted, checkDirection;
//
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//                currentPage = position;
//
//                position = mod(position);
//
//                if (position == 0) {
//                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.dot_left));
//                } else if (position == 1) {
//                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.swipe_dot_center));
//                } else if (position == 2) {
//                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.swipe_dot_right));
//                }
//
//            }
//
//            public void reset()
//            {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                if (!scrollStarted && state == ViewPager.SCROLL_STATE_DRAGGING) {
//                    scrollStarted = true;
//                    checkDirection = true;
//                } else {
//                    scrollStarted = false;
//                }
//
////                if(savePosition>state)
////                {
////                    // Swiped Right
////                    if(currentPage == 2) {
////                        currentPage = 0;
////                    }
////                }else
////                {
////                    // Swiped Left
////                    if(currentPage == 0) {
////                        currentPage = 2;
////                    }
////                }
//                savePosition = state;
//            }
//        });


        pager.setAdapter(this.mPagerAdapter);
        pager.setCurrentItem(currentPage);

//        Toast.makeText(mContext, "Current Item " + count, Toast.LENGTH_SHORT).show();


//        '''''''''''''''''''''''''swipable position of DOTS'''''''''''''''''''

    }

    private int mod(int x)
    {
        int result = x % 3;
        if (result < 0)
            result += 3;
        return result;
    }

    class TimerTaskPager extends TimerTask
    {

        @Override
        public void run() {



            if(currentPage<count) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pager.setCurrentItem(currentPage);
                        currentPage++;
                    }
                });

            }
            else {
                currentPage=0;
            }
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}