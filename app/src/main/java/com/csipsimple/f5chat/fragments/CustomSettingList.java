package com.csipsimple.f5chat.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.csipsimple.R;

/**
 * Created by Kashish1 on 7/13/2016.
 */
public class CustomSettingList extends RootFragment implements View.OnClickListener{
    CustomSettingList customSettingList;
    LinearLayout back;
    FragmentTransaction transaction;

    Integer[] imageId = {

            R.drawable.privacy_icon,
            R.drawable.notification_icon,
            R.drawable.call_icon,
            R.drawable.multimedia_icon,
            R.drawable.personal_info_icon,
            R.drawable.change_pic_icon
    };

    String[] web = {
            "Privacy",
            "Notifications",
            "Calls and Messages",
            "Multimedia",
            "Personal information",
            "Change background picture"
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.setting_fragment, container, false);

        ListView listView = (ListView) rootview.findViewById(R.id.listview);
        back = (LinearLayout)rootview.findViewById(R.id.back);

        back.setOnClickListener(this);

        CustomList adapter = new
                CustomList(getActivity(), web, imageId);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position)
                {
                    case 0:
                        PrivacySettingFragment privacyFragment = new PrivacySettingFragment();
                        transaction=getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.addToBackStack(null);
                        transaction.replace(R.id.setting_framelayout, privacyFragment,"setting").commit();
                        break;
                    case 1:
                        NotificationSettingFragment notificationSettingFragment = new NotificationSettingFragment();
                        transaction=getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.addToBackStack(null);
                        transaction.replace(R.id.setting_framelayout, notificationSettingFragment,"setting").commit();
                        break;
                    case 2:
                        CallMessageSetttingFragment callMessageSetttingFragment = new CallMessageSetttingFragment();
                        transaction=getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.addToBackStack(null);
                        transaction.replace(R.id.setting_framelayout, callMessageSetttingFragment,"setting").commit();
                        break;
                    case 3:
                        MultimediaSettingFragment multimediaSettingFragment = new MultimediaSettingFragment();
                        transaction=getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.addToBackStack(null);
                        transaction.replace(R.id.setting_framelayout, multimediaSettingFragment,"setting").commit();
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                }
               // Toast.makeText(getActivity(), "You Clicked at " + web[+position], Toast.LENGTH_SHORT).show();

            }
        });
        return rootview;
    }

    @Override
    public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.back:
                    back();
                    break;
            }
    }

public void fragmentPageChange(Fragment fragment, int pageId)
{
    FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
    transaction.addToBackStack(null);
    transaction.replace(pageId, fragment,"setting").commit();
}

    @Override
    public boolean onBackPressed() {
        back();
        return super.onBackPressed();
    }

    public void back()
    {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            getActivity().finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }

}
