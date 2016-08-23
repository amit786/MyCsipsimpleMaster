package com.csipsimple.f5chat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.csipsimple.api.SipManager;
import com.csipsimple.api.SipProfile;
import com.csipsimple.api.SipUri;
import com.csipsimple.db.DBAdapter;
import com.csipsimple.f5chat.adapter.CallDetailAdapter;
import com.csipsimple.f5chat.DialPad;
import com.csipsimple.R;
import com.csipsimple.f5chat.bean.CallLogs;
import com.csipsimple.ui.TabsViewPagerFragmentActivity;

import java.util.ArrayList;


public class CallsFragment extends Fragment implements View.OnClickListener {
    private RelativeLayout relativeLayoutAll, relativeLayoutMissed;
    private com.csipsimple.f5chat.font_family.Font_welcome_scr_Tv_regular tv_all, tv_missed;
    ListView call_listview;
    ArrayList<CallLogs> allCallslist=new ArrayList<CallLogs>();
    CallDetailAdapter callLogs;
    String enableLst = "";
    LinearLayout ll_edit;
    RelativeLayout keypadView;
    private static int SPLASH_TIME_OUT = 100;
    DBAdapter db;

    public CallsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        TabsViewPagerFragmentActivity.isContactFragmentClicked = false;
        Toast.makeText(getActivity(),"<-- CallFragment resume call",Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.call_fragment, container, false);

        db = new DBAdapter(getActivity());

        keypadView = (RelativeLayout) rootView.findViewById(R.id.keypad);
        ll_edit=(LinearLayout)rootView.findViewById(R.id.ll_edit);
        call_listview=(ListView)rootView.findViewById(R.id.call_listView);
        tv_all = (com.csipsimple.f5chat.font_family.Font_welcome_scr_Tv_regular) rootView.findViewById(R.id.tv_all);
        tv_missed = (com.csipsimple.f5chat.font_family.Font_welcome_scr_Tv_regular) rootView.findViewById(R.id.tv_missed);

        tv_all.setOnClickListener(this);
        tv_missed.setOnClickListener(this);
        keypadView.setOnClickListener(this);
        ll_edit.setOnClickListener(this);



        tv_all.setSelected(true);
        tv_missed.setSelected(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showUI();
            }
        }, SPLASH_TIME_OUT);


        call_listview.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {  }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem == 0) {
                    keypadView.setVisibility(View.VISIBLE);

                } else {
                    keypadView.setVisibility(View.GONE);
                }
            }
        });

        return rootView;
    }
    public void showUI()
    {

//        allCallslist = db.getAllLogs();
////        for (int i=0;i<10;i++)
////        {
////            allCallslist.add(new CallLogs("Rahul "+i,"05:10 am","Received"));
////        }
//        callLogs=new CallDetailAdapter(this,allCallslist,"ALL");
//        call_listview.setAdapter(callLogs);
//        callLogs.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tv_all:
                tvall();
                break;

            case R.id.tv_missed:
                tvmissed();
                break;
            case R.id.keypad:
                keypad();
                break;
            case R.id.ll_edit:
//                edit();
                break;
        }

    }
    public void tvall()
    {
        tv_all.setSelected(true);
        tv_missed.setSelected(false);
        callLogs=new CallDetailAdapter(this,allCallslist,"ALL");
        call_listview.setAdapter(callLogs);
        callLogs.notifyDataSetChanged();
        enableLst = "ALL";
    }
    public void tvmissed()
    {
        tv_all.setSelected(false);
        tv_missed.setSelected(true);
        callLogs=new CallDetailAdapter(this,allCallslist,"MISSED");
        call_listview.setAdapter(callLogs);
        callLogs.notifyDataSetChanged();
        enableLst = "MISSED";
    }
    public void keypad()
    {
        Intent in = new Intent(getActivity(), DialPad.class);
        startActivity(in);
    }

    public void edit()
    {
       // BlockList blockList = new BlockList();
        //fragmentPageChange(nextFrag,R.id.more_framelayout);
        FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        // transaction.replace(R.id.frame_call_fragment, blockList,"setting").commit();
    }
    public void placeCall(String number, Long accId) {
        System.out.println("<-- number "+number);
        System.out.println("<-- accId "+accId);
        if(!TextUtils.isEmpty(number)) {
            System.out.println("<-- numberformated "+ SipUri.getCanonicalSipContact(number, false));

            Intent it = new Intent(Intent.ACTION_CALL);
            it.setData(SipUri.forgeSipUri(SipManager.PROTOCOL_CSIP, SipUri.getCanonicalSipContact(number, false)));
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(accId != null) {
                it.putExtra(SipProfile.FIELD_ACC_ID, accId);
            }
            getActivity().startActivity(it);
        }
    }
}

