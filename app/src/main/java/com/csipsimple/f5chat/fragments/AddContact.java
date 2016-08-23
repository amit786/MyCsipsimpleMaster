package com.csipsimple.f5chat.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.csipsimple.R;

/**
 * Created by Kashish1 on 7/19/2016.
 */
public class AddContact extends RootFragment implements View.OnClickListener{
    FragmentTransaction transaction;
    AddContact addContact;
    private LinearLayout back;
    RelativeLayout scanQR;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootview = inflater.inflate(R.layout.addcontact, container, false);

        addContact = new AddContact();

        scanQR = (RelativeLayout)rootview.findViewById(R.id.scanQR);
        back = (LinearLayout) rootview.findViewById(R.id.back);

        scanQR.setOnClickListener(this);
        back.setOnClickListener(this);
        return rootview;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.scanQR:
               ScanQrCode scanQrCode = new ScanQrCode();
                transaction=getActivity().getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.hide(addContact);
                // transaction.remove(moreFragment);
                transaction.replace(R.id.addcontact_framelayout, scanQrCode,"addcontact").commit();
                break;
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
