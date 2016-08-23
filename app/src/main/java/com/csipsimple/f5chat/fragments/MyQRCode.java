package com.csipsimple.f5chat.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.csipsimple.R;

/**
 * Created by Kashish1 on 7/19/2016.
 */
public class MyQRCode extends RootFragment implements View.OnClickListener {
    LinearLayout back;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootview = inflater.inflate(R.layout.scan_qr_received, container, false);

         back = (LinearLayout) rootview.findViewById(R.id.back);

        back.setOnClickListener(this);
        return rootview;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
