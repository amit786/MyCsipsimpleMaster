package com.csipsimple.f5chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.csipsimple.R;
import com.csipsimple.f5chat.bean.CallLogs;
import com.csipsimple.f5chat.fragments.CallsFragment;

import java.util.ArrayList;

/**
 * Created by Kashish1 on 7/13/2016.
 */
public class CallDetailAdapter extends BaseAdapter{
    private ArrayList<CallLogs> arrayLst;// = new ArrayList<>();
    private static LayoutInflater inflater = null;
    CallsFragment callFragment;
    String Type;

    public CallDetailAdapter(CallsFragment callFragment, ArrayList<CallLogs> callloglist, String Type) {
        arrayLst = callloglist;
        this.callFragment = callFragment;
        inflater = (LayoutInflater) callFragment.getActivity().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.Type = Type;
    }

    @Override
    public int getCount() {
        return arrayLst.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class Holder {
                //private section items
        TextView username;
        TextView lastStatus;
        TextView callingTime;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rootview=null;
        if(Type.equals("ALL"))
        {
            rootview=inflater.inflate(R.layout.phone_screen,null);
            holder.username=(TextView)rootview.findViewById(R.id.call_user_name);
            holder.lastStatus=(TextView)rootview.findViewById(R.id.call_status);
            holder.callingTime=(TextView)rootview.findViewById(R.id.calling_time);
            CallLogs logs=arrayLst.get(position);
            holder.username.setText(logs.getNumber());
//            holder.lastStatus.setText(logs.getType());
//            holder.callingTime.setText(logs.getTime());
        }
        else if(Type.equals("MISSED"))
        {
            rootview=inflater.inflate(R.layout.missed_screen,null);
            holder.username=(TextView)rootview.findViewById(R.id.mcall_user_name);
            holder.callingTime=(TextView)rootview.findViewById(R.id.mcalling_time);
            CallLogs logs=arrayLst.get(position);
            holder.username.setText(logs.getNumber());
           // holder.callingTime.setText(logs.getTime());
        }
        return rootview;
    }
}
