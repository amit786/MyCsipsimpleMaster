package com.csipsimple.f5chat.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.csipsimple.R;

/**
 * Created by Kashish1 on 7/14/2016.
 */
public class CallMessageSetttingFragment extends RootFragment implements View.OnClickListener {
    LinearLayout blockList_layout,back;
    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.call_message_setting, container, false);

        blockList_layout = (LinearLayout)rootView.findViewById(R.id.blockList_layout);
        back = (LinearLayout)rootView.findViewById(R.id.back);

        blockList_layout.setOnClickListener(this);
        back.setOnClickListener(this);

        return rootView;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.blockList_layout:
                blockList();
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

    public void blockList()
    {
//        BlockList blockList = new BlockList();
//        //fragmentPageChange(nextFrag,R.id.more_framelayout);
//        FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
//        transaction.addToBackStack(null);
//        transaction.replace(R.id.callmessage_framelayout, blockList,"setting").commit();
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
