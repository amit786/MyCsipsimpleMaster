package com.csipsimple.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;

import com.actionbarsherlock.view.Menu;
import com.csipsimple.R;
import com.csipsimple.api.SipConfigManager;
import com.csipsimple.api.SipManager;
import com.csipsimple.api.SipProfile;
import com.csipsimple.f5chat.background.ContactSyncAsyncTask;
import com.csipsimple.f5chat.bean.Group;
import com.csipsimple.f5chat.fragments.CallsFragment;
import com.csipsimple.f5chat.fragments.ContactsFragment;
import com.csipsimple.f5chat.fragments.MessagesFragment;
import com.csipsimple.f5chat.fragments.MoreFragment;
import com.csipsimple.f5chat.rooster.RoosterConnectionService;
import com.csipsimple.f5chat.rooster.XmppConnect;
import com.csipsimple.f5chat.utility.DialogUtility;
import com.csipsimple.f5chat.utility.PreferenceConstants;
import com.csipsimple.f5chat.utility.SharedPrefrence;
import com.csipsimple.f5chat.utility.Utils;
import com.csipsimple.utils.CustomDistribution;
import com.csipsimple.utils.Log;
import com.csipsimple.utils.PreferencesProviderWrapper;
import com.csipsimple.utils.PreferencesWrapper;
import com.csipsimple.utils.backup.BackupWrapper;
import com.csipsimple.wizards.BasePrefsWizard;
import com.csipsimple.wizards.WizardUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;


/**
 * The <code>TabsViewPagerFragmentActivity</code> class implements the Fragment activity that maintains a TabHost using a ViewPager.
 *
 * @author mwho
 */
public class TabsViewPagerFragmentActivity extends FragmentActivity implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

    private TabHost mTabHost;
    private ViewPager mViewPager;
    private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, TabsViewPagerFragmentActivity.TabInfo>();
    private PagerAdapter mPagerAdapter;

    View message;
    View call;
    View contact;
    View more;
    LinearLayout msg_bg;
    LinearLayout call_bg;
    LinearLayout contact_bg;
    LinearLayout more_bg;
    List<Fragment> fragments = new Vector<Fragment>();

    private BroadcastReceiver syncCompleteReceive;
    MyContentObserver observer;
    public static SharedPrefrence share;
    private BroadcastReceiver mBroadcastReceiver;
    private BroadcastReceiver updateInGroup;
    public static boolean isContactFragmentClicked = false;



    public static final int ACCOUNTS_MENU = Menu.FIRST + 1;
    public static final int PARAMS_MENU = Menu.FIRST + 2;
    public static final int CLOSE_MENU = Menu.FIRST + 3;
    public static final int HELP_MENU = Menu.FIRST + 4;
    public static final int DISTRIB_ACCOUNT_MENU = Menu.FIRST + 5;


    private static final String THIS_FILE = "SIP_HOME";

    private final static int TAB_ID_DIALER = 0;
    private final static int TAB_ID_CALL_LOG = 1;
    private final static int TAB_ID_FAVORITES = 2;
    private final static int TAB_ID_MESSAGES = 3;
    private final static int TAB_ID_WARNING = 4;

    // protected static final int PICKUP_PHONE = 0;
    private static final int REQUEST_EDIT_DISTRIBUTION_ACCOUNT = 0;
    //private PreferencesWrapper prefWrapper;
    private PreferencesProviderWrapper prefProviderWrapper;
    private boolean hasTriedOnceActivateAcc = false;


    /**
     * @author mwho
     *         Maintains extrinsic info of a tab's construct
     */
    private class TabInfo {
        private String tag;
        private Class<?> clss;
        private Bundle args;
        private Fragment fragment;

        TabInfo(String tag, Class<?> clazz, Bundle args) {
            this.tag = tag;
            this.clss = clazz;
            this.args = args;
        }

    }

    /**
     * A simple factory that returns dummy views to the Tabhost
     *
     * @author mwho
     */
    class TabFactory implements TabContentFactory {

        private final Context mContext;

        /**
         * @param context
         */
        public TabFactory(Context context) {
            mContext = context;
        }

        /**
         * (non-Javadoc)
         *
         * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
         */
        public View createTabContent(String tag) {
            //View view1 = LayoutInflater.from(mContext).inflate(R.layout.bottom_layout,null,false);
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }

    }

    /**
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Inflate the layout
        //prefWrapper = new PreferencesWrapper(this);
        prefProviderWrapper = new PreferencesProviderWrapper(this);
        setContentView(R.layout.bottom_tabs);

        isContactFragmentClicked = false;
        share = SharedPrefrence.getInstance(this);
        observer = new MyContentObserver();
        // Initialise the TabHost
        this.initialiseTabHost(savedInstanceState);
        if (savedInstanceState != null) {
            //        mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); //set the tab as per the saved state
        }
        // Intialise ViewPager
        this.intialiseViewPager();

        hasTriedOnceActivateAcc = false;

        if (!prefProviderWrapper.getPreferenceBooleanValue(SipConfigManager.PREVENT_SCREEN_ROTATION)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }

    }

    /**
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("tab", mTabHost.getCurrentTabTag()); //save the tab selected
        super.onSaveInstanceState(outState);
    }

    /**
     * Initialise ViewPager
     */
    private void intialiseViewPager() {


        fragments.add(Fragment.instantiate(this, MessagesFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, CallsFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, ContactsFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, MoreFragment.class.getName()));

        this.mPagerAdapter = new PagerAdapter(super.getSupportFragmentManager(), fragments);
        //
        this.mViewPager = (ViewPager) super.findViewById(R.id.viewpager);
        this.mViewPager.setAdapter(this.mPagerAdapter);
        this.mViewPager.setOnPageChangeListener(this);
    }

    /**
     * Initialise the Tab Host
     */
    private void initialiseTabHost(Bundle args) {
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();

        message = LayoutInflater.from(this).inflate(R.layout.tab_message, null, false);
        call = LayoutInflater.from(this).inflate(R.layout.tab_call, null, false);
        contact = LayoutInflater.from(this).inflate(R.layout.tab_contact, null, false);
        more = LayoutInflater.from(this).inflate(R.layout.tab_more, null, false);

        msg_bg = (LinearLayout) message.findViewById(R.id.msg_bg);
        call_bg = (LinearLayout) call.findViewById(R.id.call_bg);
        contact_bg = (LinearLayout) contact.findViewById(R.id.contact_bg);
        more_bg = (LinearLayout) more.findViewById(R.id.more_bg);
        // more_bg.setSelected(true);


        TabInfo tabInfo = null;
        TabsViewPagerFragmentActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab1").setIndicator(message), (tabInfo = new TabInfo("Tab1", Fragment1.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        TabsViewPagerFragmentActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab2").setIndicator(call), (tabInfo = new TabInfo("Tab2", Fragment1.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        TabsViewPagerFragmentActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab3").setIndicator(contact), (tabInfo = new TabInfo("Tab3", Fragment3.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        TabsViewPagerFragmentActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab3").setIndicator(more), (tabInfo = new TabInfo("Tab3", Fragment3.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);


        // Default to first tab
        //this.onTabChanged("Tab1");
        //
        mTabHost.setOnTabChangedListener(this);
    }


    private static void AddTab(TabsViewPagerFragmentActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
        // Attach a Tab view factory to the spec
        tabSpec.setContent(activity.new TabFactory(activity));
        tabHost.addTab(tabSpec);
    }

    /**
     * (non-Javadoc)
     *
     * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
     */
    public void onTabChanged(String tag) {
        //TabInfo newTab = this.mapTabInfo.get(tag);
        int pos = this.mTabHost.getCurrentTab();
        this.mViewPager.setCurrentItem(pos);
        setBGColor(pos);
    }

    /* (non-Javadoc)
     * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrolled(int, float, int)
     */
    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected(int)
     */
    @Override
    public void onPageSelected(int position) {
        // TODO Auto-generated method stub
        this.mTabHost.setCurrentTab(position);
        setBGColor(position);
    }

    /* (non-Javadoc)
     * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrollStateChanged(int)
     */
    @Override
    public void onPageScrollStateChanged(int state) {
        // TODO Auto-generated method stub

    }

    public void setBGColor(int pos) {
        if (pos == 0) {
            msg_bg.setSelected(true);
            call_bg.setSelected(false);
            contact_bg.setSelected(false);
            more_bg.setSelected(false);
        } else if (pos == 1) {
            msg_bg.setSelected(false);
            call_bg.setSelected(true);
            contact_bg.setSelected(false);
            more_bg.setSelected(false);
        } else if (pos == 2) {
            msg_bg.setSelected(false);
            call_bg.setSelected(false);
            contact_bg.setSelected(true);
            more_bg.setSelected(false);
        } else if (pos == 3) {
            msg_bg.setSelected(false);
            call_bg.setSelected(false);
            contact_bg.setSelected(false);
            more_bg.setSelected(true);
//            Fragment1 fg = (Fragment1) mPagerAdapter.getItem(3);
//            if (fg.text != null)
//                fg.text.setText("Amit have change");
//            fragments.set(3, fg);
//            mPagerAdapter.notifyDataSetChanged();
        }
    }

    public void checkConnection() {
        XmppConnect mConnection = XmppConnect.getInstance();
        mConnection.connectDirect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // XmppConnect.getInstance(this).addCallBack(this);
        syncCompleteReceive = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                System.out.println("<-- sync complete update list");
                //contactsFragment.updateList();
            }
        };
        IntentFilter filter = new IntentFilter(PreferenceConstants.SYNC_COMPELLED);
        this.registerReceiver(syncCompleteReceive, filter);



        updateInGroup = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DialogUtility.showLOG("<-- sync complete update list");
                runOnUiThread(new Runnable() {
                    public void run() {
                        // messagesFragment.updateGroupLst();
                    }
                });

            }
        };
        IntentFilter groupUpdate = new IntentFilter(RoosterConnectionService.GROUP_UPDATE);
        this.registerReceiver(updateInGroup, groupUpdate);



        getApplicationContext().getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, observer);

        checkConnection();

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action) {
                    case RoosterConnectionService.NEW_MESSAGE:
                        runOnUiThread(new Runnable() {
                            public void run() {
                                //messagesFragment.loadAllLstContent();
                            }
                        });
                        return;
                }

            }
        };

        IntentFilter receiveMessage = new IntentFilter(RoosterConnectionService.NEW_MESSAGE);
        registerReceiver(mBroadcastReceiver, receiveMessage);

        onForeground = true;
        prefProviderWrapper.setPreferenceBooleanValue(PreferencesWrapper.HAS_BEEN_QUIT, false);
        Log.d(THIS_FILE, "WE CAN NOW start SIP service");
        startSipService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(mBroadcastReceiver);
            // unregisterReceiver(mBroadcastReceiver);
//        unregisterReceiver(syncCompleteReceive);
//        //getApplicationContext().getContentResolver().unregisterContentObserver(observer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        disconnect(false);
        super.onDestroy();
        try {
            unregisterReceiver(syncCompleteReceive);
            unregisterReceiver(updateInGroup);
            getApplicationContext().getContentResolver().unregisterContentObserver(observer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyContentObserver extends ContentObserver {

        public MyContentObserver() {
            super(null);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            String statusOfSync = share.getValue(SharedPrefrence.SYNC_ENABLE);
            System.out.println("<-- change in contact list with sync status " + statusOfSync);
            if (statusOfSync.equals("false"))
                new ContactSyncAsyncTask(getApplicationContext()).execute();
        }

    }


    public static Group getMyGroup(String JID) {
        HashMap<String, Group> groupHashMap = share.getGroup(SharedPrefrence.GROUP_LST);
        Group group = groupHashMap.get(JID);
        return group;
    }

    // Service monitoring stuff
    private void startSipService() {
        Thread t = new Thread("StartSip") {
            public void run() {
                Intent serviceIntent = new Intent(SipManager.INTENT_SIP_SERVICE);
                // Optional, but here we bundle so just ensure we are using csipsimple package
                serviceIntent.setPackage(TabsViewPagerFragmentActivity.this.getPackageName());
                serviceIntent.putExtra(SipManager.EXTRA_OUTGOING_ACTIVITY, new ComponentName(TabsViewPagerFragmentActivity.this, TabsViewPagerFragmentActivity.class));
                startService(serviceIntent);
                postStartSipService();
            };
        };
        t.start();

    }

    private void postStartSipService() {

        // If we have never set fast settings

        if (CustomDistribution.showFirstSettingScreen())
        {
            if (!prefProviderWrapper.getPreferenceBooleanValue(PreferencesWrapper.HAS_ALREADY_SETUP, false)) {
                Intent prefsIntent = new Intent(SipManager.ACTION_UI_PREFS_FAST);
                prefsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(prefsIntent);
                return;
            }
        } else {
            boolean doFirstParams = !prefProviderWrapper.getPreferenceBooleanValue(PreferencesWrapper.HAS_ALREADY_SETUP, false);
            prefProviderWrapper.setPreferenceBooleanValue(PreferencesWrapper.HAS_ALREADY_SETUP, true);
            if (doFirstParams) {
                prefProviderWrapper.resetAllDefaultValues();
            }
        }

        String username = Utils.getJIDWithoutHost(this);
        String password = "f5_"+share.getValue(SharedPrefrence.PASSWORD);
        System.out.println("<-- sip detail username "+username);
        System.out.println("<-- sip detail password "+password);

        getLogin("912929292929","f512345");

//        // If we have no account yet, open account panel,
//        if (!hasTriedOnceActivateAcc) {
//
//            Cursor c = getContentResolver().query(SipProfile.ACCOUNT_URI, new String[] {
//                    SipProfile.FIELD_ID
//            }, null, null, null);
//            int accountCount = 0;
//            if (c != null) {
//                try {
//                    accountCount = c.getCount();
//                } catch (Exception e) {
//                    Log.e(THIS_FILE, "Something went wrong while retrieving the account", e);
//                } finally {
//                    c.close();
//                }
//            }
//
//
//            if (accountCount == 0) {
//                Intent accountIntent = null;
//                WizardInfo distribWizard = CustomDistribution.getCustomDistributionWizard();
//                if (distribWizard != null) {
//                    accountIntent = new Intent(this, BasePrefsWizard.class);
//                    accountIntent.putExtra(SipProfile.FIELD_WIZARD, distribWizard.id);
//                } else {
//                    accountIntent = new Intent(this, AccountsEditList.class);
//                }
//
//                if (accountIntent != null) {
//                    accountIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(accountIntent);
//                    hasTriedOnceActivateAcc = true;
//                    return;
//                }
//            }
//            hasTriedOnceActivateAcc = true;
//        }
    }
    private boolean onForeground = false;

    private final static int CHANGE_PREFS = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHANGE_PREFS) {
            sendBroadcast(new Intent(SipManager.ACTION_SIP_REQUEST_RESTART));
            BackupWrapper.getInstance(this).dataChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void disconnect(boolean quit) {
        Log.d(THIS_FILE, "True disconnection...");
        Intent intent = new Intent(SipManager.ACTION_OUTGOING_UNREGISTER);
        intent.putExtra(SipManager.EXTRA_OUTGOING_ACTIVITY, new ComponentName(this, SipHome.class));
        sendBroadcast(intent);
        if(quit) {
            finish();
        }
    }
    public void getLogin(String user, String password) {
        String resFirst = "No such user exist with given IMEI";
        String resSecond = "IMEI can't be blank";
        String resThird = "Not Connected from server";
        String resFourth = "Imei or public key cant be blank";
        String resFifth = "Imei or public key cant be blank";


        if (user != resFirst) {
            Cursor c = getContentResolver().query(SipProfile.ACCOUNT_URI, new String[]{
                    SipProfile.FIELD_ID
            }, null, null, null);
            int accountCount = 0;
            if (c != null) {
                try {
                    accountCount = c.getCount();
                } catch (Exception e) {
                    Log.e(THIS_FILE, "Something went wrong while retrieving the account", e);
                } finally {
                    c.close();
                }
            }

           // accountCount = 0;

            if (accountCount == 0) {
                Intent accountIntent = null;
                WizardUtils.WizardInfo distribWizard = CustomDistribution.getCustomDistributionWizard();
                if (distribWizard != null) {
                    accountIntent = new Intent(this, BasePrefsWizard.class);
                    accountIntent.putExtra(SipProfile.FIELD_WIZARD, distribWizard.id);
                } else {
                    accountIntent = new Intent(this, BasePrefsWizard.class);
                }

                if (accountIntent != null) {

                    //	System.out.println("<-- set account "+response);
                    accountIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    accountIntent.putExtra("UserName", user);
                    accountIntent.putExtra("IMEI", "0987889098788987");
                    accountIntent.putExtra("pass", password);
                    startActivity(accountIntent);
                    hasTriedOnceActivateAcc = true;
                    return;
                }
            } else {

            }

            hasTriedOnceActivateAcc = true;
        }
    }
}