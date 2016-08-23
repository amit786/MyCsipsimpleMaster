package com.csipsimple.f5chat;//package com.csipsimple.f5chat;
//
//import android.os.Bundle;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v4.view.ViewPager;
//
//import com.actionbarsherlock.app.ActionBar;
//import com.actionbarsherlock.app.SherlockFragmentActivity;
//import com.csipsimple.R;
//import com.csipsimple.f5chat.fragments.CallsFragment;
//import com.csipsimple.f5chat.fragments.ContactsFragment;
//import com.csipsimple.f5chat.fragments.MessagesFragment;
//import com.csipsimple.f5chat.fragments.MoreFragment;
//
///**
// * Created by Administrator on 8/8/2016.
// */
//public class MainTabBar extends SherlockFragmentActivity {
//    /** Called when the activity is first created. */
//    private ActionBar actionBar;
//    private ViewPager viewPager;
//
//    public MessagesFragment messagesFragment;
//    public CallsFragment callsFragment;
//    public ContactsFragment contactsFragment;
//    public MoreFragment moreFragment;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//
//        messagesFragment = new MessagesFragment();
//        callsFragment = new CallsFragment();
//        contactsFragment = new ContactsFragment();
//        moreFragment = new MoreFragment();
//
//        viewPager = (ViewPager) findViewById(R.id.pager);
//        viewPager.setOnPageChangeListener(onPageChangeListener);
//        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),this));
//        addActionBarTabs();
//
//
//    }
//
//    private ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
//        @Override
//        public void onPageSelected(int position) {
//            super.onPageSelected(position);
//            actionBar.setSelectedNavigationItem(position);
//        }
//    };
//
//    private void addActionBarTabs() {
//        actionBar = getSupportActionBar();
//        String[] tabs = { "Tab 1", "Tab 2", "Tab 3" };
//        for (String tabTitle : tabs) {
//            ActionBar.Tab tab = actionBar.newTab().setText(tabTitle)
//                    .setTabListener(tabListener);
//            actionBar.addTab(tab);
//            actionBar.set
//        }
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//    }
//
//    private ActionBar.TabListener tabListener = new ActionBar.TabListener() {
//        @Override
//        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
//            viewPager.setCurrentItem(tab.getPosition());
//        }
//
//        @Override
//        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
//        }
//
//        @Override
//        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
//        }
//    };
//}