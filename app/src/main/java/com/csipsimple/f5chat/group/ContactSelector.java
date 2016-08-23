package com.csipsimple.f5chat.group;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.csipsimple.R;
import com.csipsimple.f5chat.bean.Contact;
import com.csipsimple.f5chat.utility.SharedPrefrence;

import java.util.ArrayList;

public class ContactSelector extends Activity {

    ListView memberList;
    F5ContactAdapter f5ContactAdapter;
    SharedPrefrence share;
    ArrayList<Contact> f5Contacts = new ArrayList<Contact>();
    LinearLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ui_contact_selector);

        share = SharedPrefrence.getInstance(ContactSelector.this);
        f5Contacts = share.getContactList(SharedPrefrence.CONTACT_LIST);

        memberList = (ListView) findViewById(R.id.memberList);
        back = (LinearLayout) findViewById(R.id.back);

        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        memberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();
                intent.putExtra("contactPosition", position);
                setResult(RESULT_OK, intent);

                finish();
            }
        });

        f5ContactAdapter = new F5ContactAdapter(f5Contacts);
        memberList.setAdapter(f5ContactAdapter);
    }

    static class ViewHolder {
        private TextView groupName;
    }

    class F5ContactAdapter extends BaseAdapter {
        ArrayList<Contact> tempF5list = new ArrayList<>();

        public F5ContactAdapter(ArrayList<Contact> list) {
            this.tempF5list = list;
        }

        @Override
        public int getCount() {
            return tempF5list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                LayoutInflater f5inflater = ContactSelector.this.getLayoutInflater();
                convertView = f5inflater.inflate(R.layout.ui_group_item_list, null);
                holder = new ViewHolder();
                holder.groupName = (TextView) convertView.findViewById(R.id.groupName);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Contact contactbeanobject = tempF5list.get(position);
            holder.groupName.setText(contactbeanobject.getName());

            return convertView;
        }
    }

}

