package com.csipsimple.f5chat.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.csipsimple.R;

/**
 * Created by Kashish1 on 7/19/2016.
 */
public class ScanQrCode extends RootFragment implements View.OnClickListener {
    Button scanQR;
    FragmentTransaction transaction;
    ScanQrCode scanQrCode;
    LinearLayout back;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootview = inflater.inflate(R.layout.scan_qr_code, container, false);
        back = (LinearLayout) rootview.findViewById(R.id.back);

        scanQR = (Button) rootview.findViewById(R.id.scan_qr);

        scanQrCode = new ScanQrCode();
        scanQR.setOnClickListener(this);
        back.setOnClickListener(this);
        return rootview;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_qr:
                scanQr();
                break;
            case R.id.back:
                back();
                break;
        }

    }

    public void scanQr() {
        MyQRCode myQRCode = new MyQRCode();
        //fragmentPageChange(nextFrag,R.id.more_framelayout);
        transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        //transaction.hide(scanQrCode);
        // transaction.remove(moreFragment);
        transaction.replace(R.id.scan_qr_framelayout, myQRCode, "scanqr").commit();
    }

    @Override
    public boolean onBackPressed() {
        back();
        return super.onBackPressed();
    }

    public void back() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            getActivity().finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }
}