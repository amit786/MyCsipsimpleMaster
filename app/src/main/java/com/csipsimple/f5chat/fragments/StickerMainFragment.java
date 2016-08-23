package com.csipsimple.f5chat.fragments;//package com.csipsimple.f5chat.fragments;
//
//import android.os.Bundle;
//import android.support.design.widget.TabLayout;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentStatePagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.util.SparseArray;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.csipsimple.R;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class StickerMainFragment extends RootFragment implements View.OnClickListener {
//    private LinearLayout back;
//    private ViewPager viewPager;
//    private ViewPagerAdapter adapter;
//    private TabLayout sticker_TabLayout;
//    // Tab titles
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        final View rootview = inflater.inflate(R.layout.sticker_main, container, false);
//
//        viewPager = (ViewPager) rootview.findViewById(R.id.pager_sticker);
//        sticker_TabLayout = (TabLayout) rootview.findViewById(R.id.tab_sticker);
//        back = (LinearLayout)rootview.findViewById(R.id.back);
//
//        back.setOnClickListener(this);
//
//
//        // Note that we are passing childFragmentManager, not FragmentManager
//        adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
//        adapter.addFragment(new StickerFree(), "Free");
//        adapter.addFragment(new StickerHot(), "Hot");
//        adapter.addFragment(new StickerNew(), "New");
//        viewPager.setAdapter(adapter);
//
//        sticker_TabLayout.setupWithViewPager(viewPager);
//        sticker_TabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//                for (int i = 0; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); i++) {
//                    getActivity().getSupportFragmentManager().popBackStack();
//                }
//            }
//        });
//        setupTabIcons();
//
//        return rootview;
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId())
//        {
//            case R.id.back:
//                back();
//                break;
//        }
//    }
//
//    private void setupTabIcons() {
//
//        TextView tabOne = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab_games, null);
//        tabOne.setText("Free");
//        tabOne.setGravity(Gravity.CENTER_HORIZONTAL);
//        sticker_TabLayout.getTabAt(0).setCustomView(tabOne);
//
//
//        TextView tabTwo = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab_games, null);
//        tabTwo.setText("Hot");
//        tabTwo.setGravity(Gravity.CENTER_HORIZONTAL);
//        sticker_TabLayout.getTabAt(1).setCustomView(tabTwo);
//
//        TextView tabThree = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab_games, null);
//        tabThree.setText("New");
//        tabThree.setGravity(Gravity.CENTER_HORIZONTAL);
//        sticker_TabLayout
//                .getTabAt(2).setCustomView(tabThree);
//
//    }
//
//
//
//    class ViewPagerAdapter extends FragmentStatePagerAdapter {
//
//
//        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
//
//        private List<Fragment> mFragmentList = new ArrayList<>();
//        private List<String> mFragmentTitleList = new ArrayList<>();
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
//        public void addFragment(Fragment fragment, String title) {
//            mFragmentList.add(fragment);
//            mFragmentTitleList.add(title);
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mFragmentTitleList.get(position);
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            Fragment fragment = (Fragment) super.instantiateItem(container, position);
//            registeredFragments.put(position, fragment);
//            return fragment;
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            registeredFragments.remove(position);
//            super.destroyItem(container, position, object);
//        }
//
//        public Fragment getRegisteredFragment(int position) {
//            return registeredFragments.get(position);
//        }
//    }
//
//    @Override
//    public boolean onBackPressed() {
//        back();
//        return super.onBackPressed();
//
//    }
//    public void back()
//    {
//        if (getFragmentManager().getBackStackEntryCount() == 0) {
//            getActivity().finish();
//        } else {
//            getFragmentManager().popBackStack();
//        }
//    }
//
//}