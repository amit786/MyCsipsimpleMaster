package com.csipsimple.f5chat.fragments;

import android.support.v4.app.Fragment;

import com.csipsimple.f5chat.BackPressImpl;
import com.csipsimple.f5chat.listner.OnBackPressListener;


/**
 * Created by shahabuddin on 6/6/14.
 */
public class RootFragment extends Fragment implements OnBackPressListener {

    @Override
    public boolean onBackPressed() {
        return new BackPressImpl(this).onBackPressed();
    }
}
