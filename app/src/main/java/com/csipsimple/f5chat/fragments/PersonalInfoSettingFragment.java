package com.csipsimple.f5chat.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csipsimple.R;

/**
 * Created by Kashish1 on 7/14/2016.
 */
public class PersonalInfoSettingFragment extends RootFragment {
    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.privacy_setting, container, false);

        return rootView;
    }
    @Override
    public boolean onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            getActivity().finish();
        } else {
            getFragmentManager().popBackStack();
        }
        return super.onBackPressed();
    }
}
