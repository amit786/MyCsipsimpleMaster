package com.csipsimple.f5chat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csipsimple.R;

/**
 * Created by SHRIG on 5/23/2016.
 */
public class Welcome_one extends Fragment {
    com.csipsimple.f5chat.font_family.Font_Welcome_scr_Tv_light tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.welcome_scr_one, container, false);
        tv=(com.csipsimple.f5chat.font_family.Font_Welcome_scr_Tv_light)rootView.findViewById(R.id.view);
        tv.setAlpha(0.5f);
        return rootView;
    }

}

