package com.csipsimple.f5chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.csipsimple.R;
import com.csipsimple.f5chat.bean.Group;
import com.csipsimple.f5chat.fragments.MessagesFragment;

import java.util.ArrayList;

/**
 * Created by User on 7/26/2016.
 */
public class GroupAdapter extends BaseAdapter {

    String[] result;
    private ArrayList<Group> arrayLst;// = new ArrayList<>();
    private static LayoutInflater inflater = null;
    MessagesFragment messagesFragment;
    String Type;

    public GroupAdapter(MessagesFragment messagesFragment, ArrayList<Group> recentLst, String Type) {
        arrayLst = recentLst;
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
        de.hdodenhof.circleimageview.CircleImageView groupImg;
        com.csipsimple.f5chat.view.OpenSemiBoldTextView groupName;
    }

    public void updateItem(ArrayList<Group> updatedLst) {
        arrayLst.clear();
        arrayLst.addAll(updatedLst);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView = null;
        if (Type.equals("GROUP"))
        {
            rowView = inflater.inflate(R.layout.ui_group_item_list, null);
            holder.groupImg = (de.hdodenhof.circleimageview.CircleImageView) rowView.findViewById(R.id.groupImg);
            holder.groupName = (com.csipsimple.f5chat.view.OpenSemiBoldTextView) rowView.findViewById(R.id.groupName);
            holder.groupName.setText(arrayLst.get(position).getGroup_name());
        }
        return rowView;
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
