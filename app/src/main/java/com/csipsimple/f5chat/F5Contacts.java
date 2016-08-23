package com.csipsimple.f5chat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.csipsimple.R;
import com.csipsimple.f5chat.bean.Contact;
import com.csipsimple.f5chat.utility.SharedPrefrence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by HP on 22-06-2016.
 */
public class F5Contacts extends Activity{
    SharedPrefrence share;
    Context context;
    ListView F5ContactList;
    F5ContactAdapter f5ContactAdapter;
    ArrayList<Contact> f5Contacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f5contact);
        share=SharedPrefrence.getInstance(context);
        f5Contacts=share.getContactList(SharedPrefrence.F5_CONTACT);
        sortf5contact();

        F5ContactList = (ListView)findViewById(R.id.f5ContactList);
        f5ContactAdapter=new F5ContactAdapter(f5Contacts);
       F5ContactList.setAdapter(f5ContactAdapter);
    }
    class F5ContactAdapter extends BaseAdapter{
        ArrayList<Contact> f5list;
        public F5ContactAdapter(ArrayList<Contact> list)
        {
            this.f5list=list;
        }
        @Override
        public int getCount() {
            return f5list.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater f5inflater=getLayoutInflater();
            View f5contactview=f5inflater.inflate(R.layout.contactsrow,null);
            TextView contactName=(TextView)f5contactview.findViewById(R.id.contactName);
            Log.e("name--",contactName+"--");
            ImageView contactImage=(ImageView)f5contactview.findViewById(R.id.contactImage);
            Contact contactbeanobject=f5list.get(position);
            contactName.setText(contactbeanobject.getName());

            return f5contactview;
        }
    }
    public void sortf5contact()
    {
        Collections.sort(f5Contacts, new Comparator<Contact>() {

            @Override
            public int compare(Contact lhs, Contact rhs) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });
    }
}
