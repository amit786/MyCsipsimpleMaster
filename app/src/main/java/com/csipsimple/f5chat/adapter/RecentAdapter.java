package com.csipsimple.f5chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.csipsimple.R;
import com.csipsimple.f5chat.bean.Group;
import com.csipsimple.f5chat.bean.RecentChat;
import com.csipsimple.f5chat.fragments.MessagesFragment;
import com.csipsimple.f5chat.utility.SharedPrefrence;
import com.csipsimple.f5chat.utility.PreferenceConstants;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 7/7/2016.
 */
public class RecentAdapter extends BaseAdapter {

    String[] result;
    private ArrayList<RecentChat> arrayLst;// = new ArrayList<>();
    private static LayoutInflater inflater = null;
    MessagesFragment messagesFragment;
    String Type;
    SharedPrefrence share;

    public RecentAdapter(MessagesFragment messagesFragment, ArrayList<RecentChat> recentLst, String Type) {
        arrayLst = recentLst;

        share = SharedPrefrence.getInstance(messagesFragment.getActivity());

        this.messagesFragment = messagesFragment;
        inflater = (LayoutInflater) messagesFragment.getActivity().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.Type = Type;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        //  return result.length;

        return arrayLst.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return arrayLst.get(position);

    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        //private section items
        TextView contactNameP;
        TextView chatCount;
       // IconButton chatCount;
        //View chatCount;

        //private section items
        de.hdodenhof.circleimageview.CircleImageView iv_profile;
        com.csipsimple.f5chat.view.OpenSemiBoldTextView contactNameAll;
        com.csipsimple.f5chat.font_family.Font_welcome_scr_Tv_regular lastStatus;
        com.csipsimple.f5chat.font_family.Font_welcome_scr_Tv_regular time;

        de.hdodenhof.circleimageview.CircleImageView groupImg;
        com.csipsimple.f5chat.view.OpenSemiBoldTextView groupName;

        //private group items
    }

    public void updateItem(ArrayList<RecentChat> updatedLst) {
        arrayLst.clear();
        arrayLst.addAll(updatedLst);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView = null;
        if (Type.equals("ALL")) {
            rowView = inflater.inflate(R.layout.message_srceen, null);
            holder.iv_profile = (de.hdodenhof.circleimageview.CircleImageView) rowView.findViewById(R.id.iv_profile);

            holder.contactNameAll = (com.csipsimple.f5chat.view.OpenSemiBoldTextView) rowView.findViewById(R.id.tv_contactname);
            String ss=(arrayLst.get(position).getName());

//            if(!Utils.areAllNumericWayThree(ss)) {
//                Group group = getMyGroup(Utils.formatGroupJID(ss));
//                ss = group.getGroup_name();
//            }

//            Log.e("nsc test ","name "+arrayLst.get(position).getName());
            holder.contactNameAll.setText(ss);


            holder.lastStatus = (com.csipsimple.f5chat.font_family.Font_welcome_scr_Tv_regular) rowView.findViewById(R.id.lastStatus);
            String message =  arrayLst.get(position).getLastMessage();
            try {
                message = URLDecoder.decode(message, "UTF-8");
            }catch (Exception e){
            }
            holder.lastStatus.setText(message);

            holder.time = (com.csipsimple.f5chat.font_family.Font_welcome_scr_Tv_regular) rowView.findViewById(R.id.time);
            holder.time.setText(arrayLst.get(position).getTime());
        } else if (Type.equals("GROUP")) {
            rowView = inflater.inflate(R.layout.ui_group_item_list, null);
            holder.groupImg = (de.hdodenhof.circleimageview.CircleImageView) rowView.findViewById(R.id.groupImg);
            holder.groupName = (com.csipsimple.f5chat.view.OpenSemiBoldTextView) rowView.findViewById(R.id.groupName);
            holder.groupName.setText(arrayLst.get(position).getGroup_name());
        } else if (Type.equals(PreferenceConstants.MESSAGE_TYPE_CHAT)) {
            rowView = inflater.inflate(R.layout.recent_list_item, null);
            holder.chatCount = (TextView) rowView.findViewById(R.id.chatCount);

            if (getCount(arrayLst.get(position).getChatCount()).equals("VISIBLE"))
            {
                 holder.chatCount.setText(arrayLst.get(position).getChatCount());
            }
            else
                holder.chatCount.setVisibility(View.INVISIBLE);

            holder.contactNameP = (com.csipsimple.f5chat.view.OpenSemiBoldTextView) rowView.findViewById(R.id.tv_contactName);
            String sss=(arrayLst.get(position).getName());
            holder.contactNameP.setText(arrayLst.get(position).getName());
        }


        return rowView;
    }

    public Group getMyGroup(String JID)
    {
        try {
            HashMap<String, Group> groupHashMap = share.getGroup(SharedPrefrence.GROUP_LST);
            Group group= groupHashMap.get(JID);
            return group;
        }catch (Exception e) {
            return new Group();
        }
    }

    public String getCount(String count) {
        try {
            int validCount = Integer.parseInt(count);
            if (validCount > 0)
                return "VISIBLE";
            else
                return "INVISIBLE";
        } catch (Exception exception) {
            return "INVISIBLE";
        }
    }
}
