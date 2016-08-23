package com.csipsimple.f5chat;//package com.csipsimple.f5chat;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.database.ContentObserver;
//import android.os.Bundle;
//import android.provider.ContactsContract;
//import android.support.design.widget.TabLayout;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.view.LayoutInflater;
//import android.widget.TextView;
//
//import com.csipsimple.f5chat.background.CallBack;
//import com.csipsimple.f5chat.background.ContactSyncAsyncTask;
//import com.csipsimple.f5chat.bean.Group;
//import com.csipsimple.f5chat.fragments.CallsFragment;
//import com.csipsimple.f5chat.fragments.ContactsFragment;
//import com.csipsimple.f5chat.fragments.MessagesFragment;
//import com.csipsimple.f5chat.fragments.MoreFragment;
//import com.csipsimple.f5chat.utility.Chatutility;
//import com.csipsimple.f5chat.utility.SharedPrefrence;
//import com.csipsimple.f5chat.utility.DialogUtility;
//import com.csipsimple.f5chat.utility.PreferenceConstants;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import rooster.XmppConnect;
//import rooster.RoosterConnectionService;
//
//
//public class CustomViewIconTextTabsActivity extends FragmentActivity implements CallBack {
//
//    //    private Toolbar toolbar;
//    private TabLayout tabLayout;
//    private NonSwipeableViewPager viewPager;
//    private BroadcastReceiver syncCompleteReceive;
//    private MessagesFragment messagesFragment = new MessagesFragment();
//    private CallsFragment callsFragment = new CallsFragment();
//    private ContactsFragment contactsFragment = new ContactsFragment();
//    private MoreFragment moreFragment = new MoreFragment();
//
//    MyContentObserver observer;
//    public static SharedPrefrence share;
//    private BroadcastReceiver mBroadcastReceiver;
//
//    private BroadcastReceiver updateInGroup;
//
//    public static boolean isContactFragmentClicked = false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
//        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            Chatutility.changeStatusBarCustomColor(this, "#2c3342");
//
//        } else {
//
//        }
//        setContentView(R.layout.act_custom_view_icon_text_tabs);
//
//        isContactFragmentClicked = false;
//
//        share = SharedPrefrence.getInstance(this);
//        observer = new MyContentObserver();
//        //  XmppConnect.getInstance(this).addCallBack(this);
//
////        toolbar = (Toolbar) findViewById(R.id.toolbar);
////        setSupportActionBar(toolbar);
////        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        viewPager = (NonSwipeableViewPager) findViewById(R.id.viewpager);
//        setupViewPager(viewPager);
//
//
//        tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(viewPager);
//        setupTabIcons();
//    }
//
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//
//        finish();
//    }
//
//    public void checkConnection() {
//        XmppConnect mConnection = XmppConnect.getInstance();
//        mConnection.connectDirect();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        System.out.println("ONRESUME");
//
//        // XmppConnect.getInstance(this).addCallBack(this);
//        syncCompleteReceive = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//                System.out.println("<-- sync complete update list");
//                contactsFragment.updateList();
//            }
//        };
//
//        IntentFilter groupUpdate = new IntentFilter(RoosterConnectionService.GROUP_UPDATE);
//        this.registerReceiver(updateInGroup, groupUpdate);
//
//        updateInGroup = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                DialogUtility.showLOG("<-- sync complete update list");
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        messagesFragment.updateGroupLst();
//                    }
//                });
//
//            }
//        };
//
//        IntentFilter filter = new IntentFilter(PreferenceConstants.SYNC_COMPELLED);
//        this.registerReceiver(syncCompleteReceive, filter);
//
//        getApplicationContext().getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, observer);
//
//        checkConnection();
//
//        mBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//                switch (action) {
//                    case RoosterConnectionService.NEW_MESSAGE:
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                messagesFragment.loadAllLstContent();
//                            }
//                        });
//                        return;
//                }
//
//            }
//        };
//
//        IntentFilter receiveMessage = new IntentFilter(RoosterConnectionService.NEW_MESSAGE);
//        registerReceiver(mBroadcastReceiver, receiveMessage);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(mBroadcastReceiver);
//        // unregisterReceiver(mBroadcastReceiver);
////        unregisterReceiver(syncCompleteReceive);
////        //getApplicationContext().getContentResolver().unregisterContentObserver(observer);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        try {
//            unregisterReceiver(syncCompleteReceive);
//            unregisterReceiver(updateInGroup);
//            getApplicationContext().getContentResolver().unregisterContentObserver(observer);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Adding custom view to tab
//     */
//    private void setupTabIcons() {
//
//        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
//        tabOne.setText("Messages");
//        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.msg_icon, 0, 0);
//        tabLayout.getTabAt(0).setCustomView(tabOne);
//
//        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
//        tabTwo.setText("Calls");
//        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.call_img, 0, 0);
//        tabLayout.getTabAt(1).setCustomView(tabTwo);
//
//        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
//        tabThree.setText("Contacts");
//        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.contact_img, 0, 0);
//        tabLayout.getTabAt(2).setCustomView(tabThree);
//
//        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
//        tabFour.setText("More");
//        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.more_dots, 0, 0);
//        tabLayout.getTabAt(3).setCustomView(tabFour);
//    }
//
//    /**
//     * Adding fragments to ViewPager
//     *
//     * @param viewPager
//     */
//    private void setupViewPager(ViewPager viewPager) {
//        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        adapter.addFrag(messagesFragment, "Messages");
//        adapter.addFrag(callsFragment, "Calls");
//        adapter.addFrag(contactsFragment, "Contacts");
//        adapter.addFrag(moreFragment, "More");
//        viewPager.setAdapter(adapter);
//    }
//
//    @Override
//    public void getMessage() {
//        System.out.println("<-- callback run ");
//        runOnUiThread(new Runnable() {
//            public void run() {
//                messagesFragment.loadAllLstContent();
//            }
//        });
//    }
//
//    class ViewPagerAdapter extends FragmentPagerAdapter {
//        private final List<Fragment> mFragmentList = new ArrayList<>();
//        private final List<String> mFragmentTitleList = new ArrayList<>();
//
//        public ViewPagerAdapter(FragmentManager manager) {
//            super(manager);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return mFragmentList.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return mFragmentList.size();
//        }
//
//        public void addFrag(Fragment fragment, String title) {
//            mFragmentList.add(fragment);
//            mFragmentTitleList.add(title);
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mFragmentTitleList.get(position);
//        }
//    }
//
//    private class MyContentObserver extends ContentObserver {
//
//        public MyContentObserver() {
//            super(null);
//        }
//
//        @Override
//        public void onChange(boolean selfChange) {
//            super.onChange(selfChange);
//
//            String statusOfSync = share.getValue(SharedPrefrence.SYNC_ENABLE);
//            System.out.println("<-- change in contact list with sync status " + statusOfSync);
//            if (statusOfSync.equals("false"))
//                new ContactSyncAsyncTask(getApplicationContext()).execute();
//        }
//
//    }
//
//
//    public static Group getMyGroup(String JID)
//    {
//        HashMap<String, Group> groupHashMap = share.getGroup(SharedPrefrence.GROUP_LST);
//        Group group= groupHashMap.get(JID);
//        return group;
//    }
//
//}
