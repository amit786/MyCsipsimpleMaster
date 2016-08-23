package com.csipsimple.f5chat.group;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.csipsimple.R;
import com.csipsimple.f5chat.http.HTTPPost;
import com.csipsimple.f5chat.utility.SharedPrefrence;
import com.csipsimple.f5chat.utility.PreferenceConstants;
import com.csipsimple.f5chat.utility.Utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class DeleteMember extends Activity {

    String parameters, group_name, group_members;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayList<String> deletedMembers = new ArrayList<String>();

    Button deleteMemberBtn;
    ListView memberList;
    dataListAdapter adapter;

    ProgressDialog progress;
    private ProgressDialog pDialog;
    String GroupJidWithoutIp, AdminJidWithoutIp;

    SharedPrefrence share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ui_delete_member);

        share = SharedPrefrence.getInstance(DeleteMember.this);

        deleteMemberBtn = (Button) findViewById(R.id.deleteMemberBtn);
        memberList = (ListView) findViewById(R.id.memberList);

        group_name = getIntent().getExtras().getString(PreferenceConstants.GroupName);
        GroupJidWithoutIp = getIntent().getExtras().getString(PreferenceConstants.GroupJidWithoutIp);
        AdminJidWithoutIp = getIntent().getExtras().getString(PreferenceConstants.AdminJidWithoutIp);
        group_members = getIntent().getExtras().getString(PreferenceConstants.GroupMembers);

        group_members = group_members.replace(PreferenceConstants.SERVERATTHERSTE, "");
        group_members = group_members.replace(Utils.getMyJidWithoutIP(DeleteMember.this), "");
        group_members = group_members.replace("admin", "");

        String[] memebers = group_members.split(" ");
        for (int i = 0; i < memebers.length; i++) {
            String jid = memebers[i].trim();

            if (!jid.isEmpty()) {
                listItems.add(jid);
            }
        }

        System.out.println("List Items : " + listItems);

        deleteMemberBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                progress = ProgressDialog.show(DeleteMember.this, "",
                        "please wait...", true);

                deleteRosterFromGroup(StringUtils.join(deletedMembers, ','));
            }
        });

        adapter = new dataListAdapter(DeleteMember.this, listItems);
        memberList.setAdapter(adapter);
    }

    private List<NameValuePair> deletedMemberGroupPrameters() {

        String members = StringUtils.join(deletedMembers, ",");
        members = members.replace(" ", "").trim();
        System.out.println("Members:" + members);

        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        nameValuePair.add(new BasicNameValuePair(PreferenceConstants.GroupJidWithoutIp, GroupJidWithoutIp));
        nameValuePair.add(new BasicNameValuePair(PreferenceConstants.MemberJID, members));
        nameValuePair.add(new BasicNameValuePair(PreferenceConstants.AdminJidWithoutIp, AdminJidWithoutIp));
        return nameValuePair;
    }

    protected void deleteRosterFromGroup(String members) {

        if (Utils.checkInternetConn(DeleteMember.this)) {
            {
                try {
                    pDialog = ProgressDialog.show(DeleteMember.this, "", "Loading...", true);

                    new HTTPPost(DeleteMember.this, deletedMemberGroupPrameters()).execute(PreferenceConstants.API_DELETE_MEMBER, PreferenceConstants.DELETE_MEMBER);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Utils.showAlertDialog(this, getString(R.string.alert), getString(R.string.checknet));
        }
    }


    public void deleteGroupMemberResponse(String parseRes) {
        pDialog.dismiss();

        System.out.println("Response : " + parseRes);

        if(parseRes.contains("success")) {
            Toast.makeText(DeleteMember.this, "Participants Added successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(DeleteMember.this, "Participants cannot be added\nplease try again", Toast.LENGTH_LONG).show();
        }

        finish();
    }

    public void DeleteMemberResponse(String parseRes) {
        progress.dismiss();

        if (parseRes.contains("success")) {

            Toast.makeText(DeleteMember.this,
                    "Members deleted successfully", Toast.LENGTH_SHORT)
                    .show();
        } else {

            Toast.makeText(DeleteMember.this,
                    "Members not deleted", Toast.LENGTH_LONG)
                    .show();
        }

        finish();
    }

    class dataListAdapter extends BaseAdapter {

        DeleteMember ctx;
        ArrayList<String> listItems;

        public dataListAdapter(DeleteMember ctx, ArrayList<String> listItem) {
            this.ctx = ctx;
            this.listItems = listItem;
        }

        public int getCount() {
            return listItems.size();
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            convertView = inflater.inflate(R.layout.ui_member_items, null);

            TextView member;
            CheckBox checkBox;

            TextView user_number = (TextView) convertView
                    .findViewById(R.id.contactName);

            member = (TextView) convertView.findViewById(R.id.contactName);
            checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);


            String number = listItems.get(position);
            String name = Utils.getContactNameByNumber(DeleteMember.this, number);

            if (number.contains(PreferenceConstants.SERVERATTHERSTE)) {
                number = number
                        .replace(PreferenceConstants.SERVERATTHERSTE, "");
            }

            if (!number.equals(name)) {
                user_number.setVisibility(View.VISIBLE);

                user_number.setText(number);
            }

            member.setText(name);

            checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {

                    String str = listItems.get(position);

                    if (deletedMembers.contains(str)) {
                        deletedMembers.remove(str);
                    } else {
                        deletedMembers.add(str);
                    }
                }
            });

            return convertView;
        }
    }
}

