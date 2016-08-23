package com.csipsimple.f5chat.group;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.csipsimple.R;
import com.csipsimple.f5chat.ImageUtility;
import com.csipsimple.f5chat.bean.Contact;
import com.csipsimple.f5chat.bean.Group;
import com.csipsimple.f5chat.bean.GroupMember;
import com.csipsimple.f5chat.bean.GroupNotify;
import com.csipsimple.f5chat.compressimage.ImageLoadingUtils;
import com.csipsimple.f5chat.http.HTTPPost;
import com.csipsimple.f5chat.parser.JSONParser;
import com.csipsimple.f5chat.rooster.XmppConnect;
import com.csipsimple.f5chat.utility.DialogUtility;
import com.csipsimple.f5chat.utility.PreferenceConstants;
import com.csipsimple.f5chat.utility.SharedPrefrence;
import com.csipsimple.f5chat.utility.Utils;
import com.csipsimple.ui.TabsViewPagerFragmentActivity;
import com.github.lguipeng.library.animcheckbox.AnimCheckBox;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

//import com.csipsimple.f5chat.CustomViewIconTextTabsActivity;

public class AddParticipants extends Activity implements View.OnClickListener {

    private ImageLoadingUtils utils;
    private Bitmap scaledbitmap = null;
    String gName, gImage;
    //String userJID;
    private LinearLayout next_layout;
    //Map<String, Integer> mapIndex;
    SharedPrefrence share;
    //ArrayList<Contact> contactList = new ArrayList<Contact>();
    ListView F5ContactsList;
    F5ContactAdapter f5ContactAdapter;
    ArrayList<Contact> filterLst = new ArrayList<Contact>();
    ArrayList<Contact> f5Contacts = new ArrayList<Contact>();
    ArrayList<GroupMember> groupMembers = new ArrayList<GroupMember>();
    EditText f5EditTextSearch;
    ArrayList<Integer> removeIndex = new ArrayList<Integer>();
    // private ProgressDialog pDialog;

    LinearLayout addedPraticipantsView;

    String groupJID;
    String members = "";
    ArrayList<String> memberTemLst = new ArrayList<String>();
    int type = 0;

    LinearLayout back_arrow;
    TextView participantsCount;
    int participantLimit = 150;

    //Group groupAddMember = null;
    Group groupObj = null;
    GroupNotify groupNotify;

    String membersWithoutIP = "";

    static class ViewHolder {
        private TextView contactName;
        private AnimCheckBox checkbox;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ui_add_participants);

        share = SharedPrefrence.getInstance(AddParticipants.this);
        groupNotify = new GroupNotify();

        back_arrow = (LinearLayout) findViewById(R.id.back_arrow);
        participantsCount = (TextView) findViewById(R.id.participantsCount);
        addedPraticipantsView = (LinearLayout) findViewById(R.id.addedPraticipantsView);
        f5EditTextSearch = (EditText) findViewById(R.id.f5edittextsearch);
        F5ContactsList = (ListView) findViewById(R.id.participantsList);
        next_layout = (LinearLayout) findViewById(R.id.next_layout);

        type = getIntent().getExtras().getInt(PreferenceConstants.TYPE);
        if (type == PreferenceConstants.addMoreParticipants) {

            groupJID = getIntent().getExtras().getString(PreferenceConstants.GroupJidWithoutIp);
            groupObj = getMyGroup(groupJID);

        } else if (type == PreferenceConstants.addParticipants) {

            gName = getIntent().getExtras().getString(PreferenceConstants.group_name);
            gImage = getIntent().getExtras().getString(PreferenceConstants.group_image);
        }

        f5Contacts = share.getContactList(SharedPrefrence.F5_CONTACT);

        if (type == PreferenceConstants.addMoreParticipants) {
            filterLst();
        }

        sortf5contacts();
        f5ContactAdapter = new F5ContactAdapter(f5Contacts);
        F5ContactsList.setAdapter(f5ContactAdapter);

        f5EditTextSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                try {
                    if (f5EditTextSearch.getText().toString() != null) {
                        String txt = f5EditTextSearch.getText().toString().trim().toLowerCase();
                        f5ContactAdapter.getFilter(txt);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub


            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

                if (f5EditTextSearch.getText().length() == 0) {

                    try {
                        f5Contacts = share.getContactList(SharedPrefrence.F5_CONTACT);
                        if (type == PreferenceConstants.addMoreParticipants) {
                            filterLst();
                        }
                        sortf5contacts();
                        f5ContactAdapter = new F5ContactAdapter(f5Contacts);
                        F5ContactsList.setAdapter(f5ContactAdapter);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        next_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (memberTemLst.size() > 0) {
                    if (type == PreferenceConstants.addMoreParticipants) {
                        addMembers(gName);
                    } else if (type == PreferenceConstants.addParticipants) {
                        createGroup(gName);
                    }

                } else {
                    Toast.makeText(AddParticipants.this, "Please select attlist one member", Toast.LENGTH_LONG).show();
                }
            }
        });

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void filterLst() {
        removeIndex.clear();
        prepareMembersString();
        filterLst.clear();
        filterLst.addAll(f5Contacts);
        f5Contacts.clear();


        ArrayList<String> removeIndex = new ArrayList<String>();
        for (int i = 0; i < filterLst.size(); i++)
        {
            for (int j = 0; j < groupObj.getGroupMember().size(); j++)
            {
                if (groupObj.getGroupMember().get(j).getMember_jid().equals(filterLst.get(i).getJID())) {
                    String groupUserName = groupObj.getGroupMember().get(j).getName();
                    String f5UserName = filterLst.get(i).getName();
                    removeIndex.add(i+"");
                }
            }
        }

        for(int i = 0; i < removeIndex.size(); i++) {
            int pos = (Integer.parseInt(removeIndex.get(i))) - i;
            filterLst.remove(pos);
        }

        for (int i = 0; i < filterLst.size(); i++)
        {
            f5Contacts.add(filterLst.get(i));
        }

//        for (int i = 0; i < filterLst.size(); i++)
//        {
//            boolean bool = false;
//            for (int j = 0; j < groupObj.getGroupMember().size(); j++)
//            {
//                if (groupObj.getGroupMember().get(j).getMember_jid().equals(filterLst.get(i).getJID())) {
//                    bool =  true;
//                    break;
//                }
//            }
//            if(bool)
//                f5Contacts.add(filterLst.get(i));
//        }

    }

    public void prepareMembersString() {
        for (int i = 0; i < groupObj.getGroupMember().size(); i++) {
            members = members + "," + groupObj.getGroupMember().get(i).getMember_jid();
        }
    }

    public Group getMyGroup(String JID) {
        HashMap<String, Group> groupHashMap = share.getGroup(SharedPrefrence.GROUP_LST);
        Group group = groupHashMap.get(JID);
        return group;
    }

    public void updateGroupObj(String JID) {
        HashMap<String, Group> groupHashMap = share.getGroup(SharedPrefrence.GROUP_LST);
        groupHashMap.put(JID, groupObj);
        share.setGroupLst(SharedPrefrence.GROUP_LST, groupHashMap);
    }

    @Override
    public void onBackPressed() {

        if (f5EditTextSearch.getText().length() > 0) {
            f5EditTextSearch.setText(null);

            try {
                f5Contacts = share.getContactList(SharedPrefrence.F5_CONTACT);
                if (type == PreferenceConstants.addMoreParticipants) {
                    filterLst();
                }
                sortf5contacts();
                f5ContactAdapter = new F5ContactAdapter(f5Contacts);
                F5ContactsList.setAdapter(f5ContactAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            super.onBackPressed();
            finish();
        }
    }

    protected void addMembers(String groupName) {
        DialogUtility.showProgressDialog(this, false, "Processing...");
        new addMembersInGroup().execute();
    }

    public class addMembersInGroup extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            addMembersToGroupData();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            DialogUtility.pauseProgressDialog();
            super.onPostExecute(result);
        }
    }

    public void addMembersToGroupData() {

        if (Utils.checkInternetConn(AddParticipants.this)) {
            {
                try {
                    new HTTPPost(AddParticipants.this, addMemberGroupPrameters()).execute(PreferenceConstants.API_ADD_MEMBER, PreferenceConstants.ADD_MEMBER);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Utils.showAlertDialog(this, getString(R.string.alert), getString(R.string.checknet));
        }
    }


    public void addGroupMemberResponse(String msg, boolean result) {
        DialogUtility.pauseProgressDialog();
        try {
            if (result) {
                groupNotify.setGroupSubject(PreferenceConstants.GN_ADD_TO_GROUP);
                getMyGroupMembers();
                ArrayList<GroupMember> lst = groupObj.getGroupMember();
                for (int i = 0; i < groupMembers.size(); i++) {
                    lst.add(groupMembers.get(i));
                }
                groupObj.setGroupMember(lst);
                updateGroupObj(groupJID);
                notifyGroupUsers();

            } else {
                DialogUtility.showToast(this, msg);
            }
        }catch (Exception e) {}

        finish();
    }

    private List<NameValuePair> addMemberGroupPrameters() {

        membersWithoutIP = StringUtils.join(memberTemLst, ",");
        membersWithoutIP = membersWithoutIP.replace(" ", "").trim();

        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        nameValuePair.add(new BasicNameValuePair(PreferenceConstants.GroupJidWithoutIp, Utils.getJIDWithoutIP(groupJID)));
        nameValuePair.add(new BasicNameValuePair(PreferenceConstants.MemberJidWithoutIp, membersWithoutIP));
        nameValuePair.add(new BasicNameValuePair(PreferenceConstants.AdminJidWithoutIp, Utils.getJIDWithoutIP(share.getValue(PreferenceConstants.JID))));
        return nameValuePair;
    }

    //================

    protected void createGroup(String groupName) {
        DialogUtility.showProgressDialog(this, false, "Processing...");
        new createGrpData().execute();
    }

    public class createGrpData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            sendCreateGroupData(gName);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            DialogUtility.pauseProgressDialog();
            super.onPostExecute(result);
        }
    }

    public void sendCreateGroupData(String gName) {
        try {
            ImageUtility iu = new ImageUtility(PreferenceConstants.API_CREATE_GROUP);
            iu.connectForMultipart();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                if (scaledbitmap != null) {
                    scaledbitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String members = StringUtils.join(memberTemLst, ",");
            members = members.replace(" ", "").trim();
            System.out.println("Members:" + members);

            iu.addFormPart("user_jid", Utils.getMyJidWithoutIP(AddParticipants.this));
            iu.addFormPart("group_name", gName);
            iu.addFormPart("member_jid_without_ip", members);
            iu.addFilePart("group_image", gImage, baos.toByteArray());

            iu.finishMultipart();
            String data = iu.getResponse();

            GetCreateGroupsResponse(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void GetCreateGroupsResponse(String parseRes) {

        boolean isSuccess = false;

        try {
            DialogUtility.pauseProgressDialog();

            JSONParser parser = new JSONParser(this, parseRes);
            if (parser.STATUS.equals(PreferenceConstants.PASS_STATUS)) {
                isSuccess = true;

                getMyGroupMembers();
                groupObj = parser.createGroup(groupMembers);
                //________________________ NOTIFY TO OTHER MEMBERS THAT THEY ARE ADDED IN GROUP
                /*****************************************************************************/
                groupNotify.setGroupSubject(PreferenceConstants.GN_CREATE_GROUP);
                notifyGroupUsers();

                /*****************************************************************************/
                //________________________ NOTIFY TO OTHER MEMBERS THAT THEY ARE ADDED IN GROUP

                Intent in = new Intent(AddParticipants.this, TabsViewPagerFragmentActivity.class);
//                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);
                finish();
            } else {
                DialogUtility.showToast(this, parser.MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();

            if(isSuccess){
                Intent in = new Intent(AddParticipants.this, TabsViewPagerFragmentActivity.class);
                startActivity(in);
                finish();
            }
        }


    }

    public void getMyGroupMembers() {
        groupMembers.clear();

        for (int i = 0; i < f5Contacts.size(); i++) {
            String JID = f5Contacts.get(i).getJID();
            if(JID!=null) {
                JID = Utils.getNumberFromJID(JID);
                if (memberTemLst.contains(JID)) {
                    GroupMember member = new GroupMember();
                    member.setImage_url(f5Contacts.get(i).getImage());
                    member.setMember_jid(f5Contacts.get(i).getJID());
                    member.setName(f5Contacts.get(i).getName());
                    member.setIs_admin("F");
                    groupMembers.add(member);
                }
            }
        }
    }


    //    private void getIndexList(ArrayList<Contact> f5Contacts) {
//        mapIndex = new LinkedHashMap<String, Integer>();
//        for (int i = 0; i < f5Contacts.size(); i++) {
//            String f5 = f5Contacts.get(i).getName();
//            String index = f5.substring(0, 1).toUpperCase();
//
//            if (mapIndex.get(index) == null)
//                mapIndex.put(index, i);
//        }
//    }
//
//
//
    public void onClick(View view) {
//        TextView selectedIndex = (TextView) view;
//        F5ContactsList.setSelection(mapIndex.get(selectedIndex.getText().toString()));
//        Log.e("index size--", mapIndex.get(selectedIndex.getText()) + "");
    }
//
//
//    private void getallcontactIndexList(ArrayList<Contact> allcontact) {
//        mapIndex = new LinkedHashMap<String, Integer>();
//        for (int i = 0; i < allcontact.size(); i++) {
//            String all = allcontact.get(i).getName();
//            String index = all.substring(0, 1).toUpperCase();
//
//            if (mapIndex.get(index) == null)
//                mapIndex.put(index, i);
//        }
//    }

    @Override
    public void onResume() {

        super.onResume();
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
                LayoutInflater f5inflater = AddParticipants.this.getLayoutInflater();
                convertView = f5inflater.inflate(R.layout.ui_member_items, null);
                holder = new ViewHolder();
                holder.contactName = (TextView) convertView.findViewById(R.id.contactName);
                Log.e("name--", holder.contactName + "--");
                ImageView contactImage = (ImageView) convertView.findViewById(R.id.contactImage);
                holder.checkbox = (AnimCheckBox) convertView.findViewById(R.id.checkbox);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Contact contactbeanobject = tempF5list.get(position);
            holder.contactName.setText(contactbeanobject.getName());

            holder.checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkedOperation(position, holder.checkbox);
                }
            });

            if (memberTemLst.contains(Utils.getNumberFromJID(contactbeanobject.getJID()))) {
                holder.checkbox.setChecked(true, true);
            } else {
                holder.checkbox.setChecked(false, true);
            }

            return convertView;
        }

        public void checkedOperation(int position, AnimCheckBox checkbox) {

            String str = Utils.getNumberFromJID(tempF5list.get(position).getJID());

            if (checkbox.isChecked()) {
                checkbox.setChecked(false, true);
                if (memberTemLst.contains(str)) {
                    memberTemLst.remove(str);
                    addRemoveView();
                }
            } else {
                checkbox.setChecked(true, true);
                if (!memberTemLst.contains(str)) {
                    memberTemLst.add(str);
                    addRemoveView();
                }
            }
            if (participantLimit >= memberTemLst.size()) {
                participantsCount.setText(memberTemLst.size() + "/" + participantLimit);
            } else {
                Toast.makeText(AddParticipants.this, "Group members reached to group limit.", Toast.LENGTH_LONG).show();
            }


//                    participants_gallery.setSelection(2);
        }

        public void getFilter(String str) {
            tempF5list.clear();
            String txt = str.toLowerCase(Locale.getDefault());
            if (str.length() == 0) {
                tempF5list.addAll(tempF5list);
            } else {
                for (Contact contact : tempF5list) {
                    if (contact.getName().toLowerCase().contains(txt)) {
                        tempF5list.add(contact);
                    }
                }
            }
            notifyDataSetChanged();

        }

    }

    public void addRemoveView() {
        if (addedPraticipantsView.getChildCount() > 0) {
            addedPraticipantsView.removeAllViews();
        }
        for (String member : memberTemLst) {
            addedPraticipantsView.addView(appendView(member));
        }
    }

    public View appendView(String member) {
        CircleImageView iv = new CircleImageView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(90, 90);
        lp.setMargins(8, 0, 8, 0);
        iv.setLayoutParams(lp);
        iv.setImageResource(R.drawable.profileimage);

        return iv;
    }

    public void sortf5contacts() {
        try {
            Collections.sort(f5Contacts, new Comparator<Contact>() {

                @Override
                public int compare(Contact lhs, Contact rhs) {
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyGroupUsers() {
        if (groupObj != null) {
            String membersJID = StringUtils.join(memberTemLst, PreferenceConstants.SERVERATTHERSTE + ",");

            groupNotify.setGroupAdmin(groupObj.getAdmin_jid());
            groupNotify.setGroupImageUrl(groupObj.getGroup_image_url());
            groupNotify.setGroupJID(groupObj.getGroup_jid());
            groupNotify.setGroupName(groupObj.getGroup_name());
            groupNotify.setMembersJID(membersJID);
            XmppConnect.getInstance().sendGroupNotify(groupNotify);
        }
    }
}

