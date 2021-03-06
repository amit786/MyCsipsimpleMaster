package com.csipsimple.f5chat.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.csipsimple.R;

/**
 * Created by Kashish1 on 7/14/2016.
 */
public class PrivacySettingFragment extends RootFragment implements View.OnClickListener {
    View rootView;
    LinearLayout back;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.privacy_setting, container, false);

        back = (LinearLayout) rootView.findViewById(R.id.back);

        back.setOnClickListener(this);

    return rootView;
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
