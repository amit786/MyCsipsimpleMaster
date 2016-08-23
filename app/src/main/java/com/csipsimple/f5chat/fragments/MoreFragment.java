package com.csipsimple.f5chat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csipsimple.R;
import com.csipsimple.f5chat.ConfirmContactDialog;
import com.csipsimple.f5chat.bean.Contact;
import com.csipsimple.f5chat.utility.DialogUtility;
import com.csipsimple.f5chat.utility.PreferenceConstants;
import com.csipsimple.f5chat.utility.SharedPrefrence;
import com.csipsimple.ui.TabsViewPagerFragmentActivity;

import java.util.ArrayList;

//import com.csipsimple.f5chat.CustomViewIconTextTabsActivity;


public class MoreFragment extends RootFragment {
    FragmentTransaction transaction;
    MoreFragment moreFragment;
    String[] mobileArray = {"Android", "IPhone", "WindowsMobile", "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X"};

    SharedPrefrence share;
    ArrayList<Contact> contactList = new ArrayList<Contact>();

    Integer[] imageId = {

            R.drawable.more_tab_share_icon,
            R.drawable.more_tab_sticker_icon,
            R.drawable.more_tab_games_icon,
            R.drawable.more_tab_add_contact,
            R.drawable.more_tab_setting_icon,
            R.drawable.more_tab_about_icon
    };
    String[] web = {
            "Share F5chat with Friends",
            "Sticker Market",
            "F5chat games",
            "Add contact",
            "Settings",
            "About"
    };

    public MoreFragment() {
        // Required empty public constructor

        share = SharedPrefrence.getInstance(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        TabsViewPagerFragmentActivity.isContactFragmentClicked = true;

        contactList = share.getContactList(SharedPrefrence.CONTACT_LIST);
        if (contactList.size() == 0) {
//            openContactSyncView();
        }
    }

    public void openContactSyncView() {
        String syncStatus = share.getValue(SharedPrefrence.SYNC_ENABLE);
        if (syncStatus.equals("true")) {
            DialogUtility.showToast(getActivity(), "Please wait...");
        } else {
            if (TabsViewPagerFragmentActivity.isContactFragmentClicked) {
                Intent incontact = new Intent(getActivity(), ConfirmContactDialog.class);
                startActivityForResult(incontact, PreferenceConstants.CONFIRM_READ_CONTACT);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootview = inflater.inflate(R.layout.more_fragment, container, false);

        moreFragment = new MoreFragment();

//        ListView listView = (ListView) rootview.findViewById(R.id.listview);
//        CustomList adapter = new
//                CustomList(getActivity(), web, imageId);
//        listView.setAdapter(adapter);
////        public void setFragment(Fragment fragment) {
////            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
////            transaction.addToBackStack(null);
////            transaction.replace(R.id.flCardList, fragment).commit();
////        }
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//
//                switch (position)
//                {
//                    case 0:
//                        break;
//                    case 1:
//                        StickerMainFragment stickerMainFragment = new StickerMainFragment();
//                        transaction=getActivity().getSupportFragmentManager().beginTransaction();
//                        transaction.addToBackStack(null);
//                        transaction.hide(moreFragment);
//                        // transaction.remove(moreFragment);
//                        transaction.replace(R.id.more_framelayout, stickerMainFragment,"stickerfragment").commit();
//                        break;
//                    case 2:
//                        GameFragment gameFragment = new GameFragment();
//                     transaction=getActivity().getSupportFragmentManager().beginTransaction();
//                        transaction.addToBackStack(null);
//                        transaction.hide(moreFragment);
//                        // transaction.remove(moreFragment);
//                        transaction.replace(R.id.more_framelayout, gameFragment,"gameactivity").commit();
//                        break;
//                    case 3:
//                        AddContact addContact= new AddContact();
//                        transaction=getActivity().getSupportFragmentManager().beginTransaction();
//                        transaction.addToBackStack(null);
//                        transaction.hide(moreFragment);
//                        // transaction.remove(moreFragment);
//                        transaction.replace(R.id.more_framelayout, addContact,"addcontact").commit();
//                        break;
//                    case 4:
//                        CustomSettingList nextFrag= new CustomSettingList();
//                        //fragmentPageChange(nextFrag,R.id.more_framelayout);
//                        transaction=getActivity().getSupportFragmentManager().beginTransaction();
//                        transaction.addToBackStack(null);
//                        transaction.hide(moreFragment);
//                       // transaction.remove(moreFragment);
//                        transaction.replace(R.id.more_framelayout, nextFrag,"setting").commit();
//                        break;
//                    case 5:
//                        AboutUsFragment aboutUsFragment= new AboutUsFragment();
//                        //fragmentPageChange(nextFrag,R.id.more_framelayout);
//                        transaction=getActivity().getSupportFragmentManager().beginTransaction();
//                        transaction.addToBackStack(null);
//                        transaction.hide(moreFragment);
//                        //transaction.remove(moreFragment);
//                        transaction.replace(R.id.more_framelayout, aboutUsFragment,"setting").commit();
//                        break;
//
//                }
////                if (web[+position].equals("Settings"))
////                {
////                    //RelativeLayout mainview=(RelativeLayout)rootview.findViewById(R.id.mainView);
////                    //mainview.removeAllViews();
////                    FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
////                    transaction.addToBackStack(null);
////                    transaction.replace(R.id.mainView, nextFrag,"setting").commit();
//////                    getFragmentManager().beginTransaction()
//////                            .replace(R.id.mainView, nextFrag,"setting")
//////                            .addToBackStack(null)
//////                            .commit();
////                }
//                Toast.makeText(getActivity(), "You Clicked at " + web[+position], Toast.LENGTH_SHORT).show();
//
//            }
//        });


        return rootview;
    }
public void fragmentPageChange(Fragment fragment, int pageId)
{
    FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
    transaction.addToBackStack(null);
    transaction.replace(pageId, fragment,"setting").commit();
}
}
