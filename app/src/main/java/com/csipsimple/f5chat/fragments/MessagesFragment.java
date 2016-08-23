package com.csipsimple.f5chat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.csipsimple.R;
import com.csipsimple.f5chat.adapter.GroupAdapter;
import com.csipsimple.f5chat.adapter.RecentAdapter;
import com.csipsimple.f5chat.bean.Group;
import com.csipsimple.f5chat.bean.RecentChat;
import com.csipsimple.f5chat.database.DatabaseHandler;
import com.csipsimple.f5chat.group.CreateGroup;
import com.csipsimple.f5chat.group.GroupChatWindow;
import com.csipsimple.f5chat.http.HTTPPost;
import com.csipsimple.f5chat.utility.PreferenceConstants;
import com.csipsimple.f5chat.utility.SharedPrefrence;
import com.csipsimple.f5chat.utility.Utils;
import com.csipsimple.ui.TabsViewPagerFragmentActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

//import com.csipsimple.f5chat.CustomViewIconTextTabsActivity;


public class MessagesFragment extends Fragment implements View.OnClickListener {
    //private RelativeLayout relativeLayoutAll, relativeLayoutGroup, relativeLayoutPrivate;
    private com.csipsimple.f5chat.font_family.Font_welcome_scr_Tv_regular tvAll, tvGroups, tvPrivate;
    ImageView createGroup;
    ListView recentList;
    RecentAdapter adapter;
    GroupAdapter groupAdapter;
    ArrayList<RecentChat> lstAll = new ArrayList<RecentChat>();
    ArrayList<Group> lstGroup = new ArrayList<Group>();
    ArrayList<RecentChat> lstPrivate = new ArrayList<RecentChat>();
    DatabaseHandler db;
    String enableLst = "";
    SharedPrefrence share;

    public MessagesFragment() {
        // Required empty public constructor
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                .detectAll()
//                .penaltyLog()
//                .build();
//        StrictMode.setThreadPolicy(policy);

        View rootView = inflater.inflate(R.layout.message_fragment, container, false);



        share = SharedPrefrence.getInstance(getActivity());
        db = new DatabaseHandler(getActivity());
//        relativeLayoutAll = (RelativeLayout) rootView.findViewById(R.id.relative_all);
//        relativeLayoutGroup = (RelativeLayout) rootView.findViewById(R.id.relative_groups);
//        relativeLayoutPrivate = (RelativeLayout) rootView.findViewById(R.id.relative_private);

        createGroup = (ImageView) rootView.findViewById(R.id.createGroup);
        tvAll = (com.csipsimple.f5chat.font_family.Font_welcome_scr_Tv_regular) rootView.findViewById(R.id.tvAll);
        tvGroups = (com.csipsimple.f5chat.font_family.Font_welcome_scr_Tv_regular) rootView.findViewById(R.id.tvGroups);
        tvPrivate = (com.csipsimple.f5chat.font_family.Font_welcome_scr_Tv_regular) rootView.findViewById(R.id.tvPrivate);

        tvAll.setSelected(true);
        tvGroups.setSelected(false);
        tvPrivate.setSelected(false);

        recentList = (ListView) rootView.findViewById(R.id.recentList);
        recentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotoFriendDetail(position);
            }

        });

//        tvGroups.setAlpha(0.5f);
//        tvPrivate.setAlpha(0.5f);
//        tvAll.setAlpha(1f);
//        tvAll.setBackgroundColor(getResources().getColor(R.color.black));
        tvAll.setOnClickListener(this);
        tvGroups.setOnClickListener(this);
        tvPrivate.setOnClickListener(this);
        createGroup.setOnClickListener(this);

        enableLst = "ALL";
        loadAllLstContent();

//    GetGroups(share.getValue(SharedPrefrence.GROUPS_RESPONSE));

        // Inflate the layout for this fragment
        return rootView;
    }

    public void gotoFriendDetail(int position) {
        Intent intent = null;
        String jid = "";
        String mobile_no = "";
        String user_name = "";
        if (enableLst.equals("PRIVATE")) {
            jid = lstPrivate.get(position).getFriendJID();
            mobile_no = Utils.getNumberFromJID(lstPrivate.get(position).getFriendJID());
            user_name = lstPrivate.get(position).getName();

            intent = new Intent(getActivity()
                    , ChatWindow.class);

        } else if (enableLst.equals("GROUP"))
        {
            jid = lstGroup.get(position).getGroup_jid();
            mobile_no = Utils.getNumberFromJID(lstGroup.get(position).getAdmin_jid());
            user_name = lstGroup.get(position).getGroup_name();
            intent = new Intent(getActivity()
                    , GroupChatWindow.class);
        } else if (enableLst.equals("ALL"))
        {
            jid = lstAll.get(position).getFriendJID();
            mobile_no = Utils.getNumberFromJID(lstAll.get(position).getFriendJID());
            user_name = lstAll.get(position).getName();

            if (jid.contains(PreferenceConstants.SERVERATTHERSTE)) {
                intent = new Intent(getActivity()
                        , ChatWindow.class);
            } else {
                intent = new Intent(getActivity()
                        , GroupChatWindow.class);

//                jid = lstGroup.get(position).getGroup_jid();
                if (jid.contains(PreferenceConstants.SERVERATTHERSTE)) {
                    jid = jid.replace(PreferenceConstants.SERVERATTHERSTE, "");
                }
                if (!jid.contains("@broadcast." + PreferenceConstants.SERVER)) {
                    jid = jid + "@broadcast." + PreferenceConstants.SERVER;
                }
            }
        }

        intent.putExtra(SharedPrefrence.JID, jid); //84e1b20464011ac98289bc61e54a6690@broadcast.85.214.17.140
        intent.putExtra("mobile", mobile_no);
        intent.putExtra("user_name", user_name);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAllLstContent();
        loadGroupLst();

        TabsViewPagerFragmentActivity.isContactFragmentClicked = false;
    }


    public void loadGroupLst() {

        if (Utils.checkInternetConn(getActivity())) {
            {
                try {
                    //DialogUtility.showProgressDialog(getActivity(), true, getString(R.string.please_wait));
                    new HTTPPost(this, getMemberJID()).execute(PreferenceConstants.API_GET_GROUP_LIST, PreferenceConstants.GROUP_LIST_RESPONSE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Utils.showAlertDialog(getActivity(), getActivity().getString(R.string.alert), getActivity().getString(R.string.checknet));
        }
    }


    //    public void GetGroups(String parseRes) {
//        System.out.println("Response : " + parseRes);
//        try {
//            if (parseRes.length() > 0) {
//                JSONObject jobject = new JSONObject(parseRes);
//                int status = jobject.getInt("status");
//
//                if (status == 1) {
//                    share.setValue(SharedPrefrence.GROUPS_RESPONSE, parseRes);
//
//                    JSONArray groups = jobject.getJSONArray("groups");
//
//                    lstGroup.clear();
//
//                    for (int i = 0; i < groups.length(); i++) {
//                        JSONObject arr = groups.getJSONObject(i);
//
//                        String group_jid = arr.getString("group_jid");
//                        String group_name = arr.getString("group_name");
//                        String group_admin_jid = arr.getString("group_admin_jid");
//                        String url = arr.getString("url");
//
//                        RecentChat gBean = new RecentChat(group_jid, group_name, group_admin_jid, url);
//                        lstGroup.add(gBean);
//                    }
//
//                    // loadAllLstContent();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    public void receiveResponse() {
        lstGroup = new ArrayList<Group>(share.getGroup(SharedPrefrence.GROUP_LST).values());
//        groupAdapter = new GroupAdapter(this, lstGroup, "GROUP");
//        recentList.setAdapter(groupAdapter);
//        groupAdapter.notifyDataSetChanged();
    }

    public void receiveResponse(String msg) {
        // DialogUtility.showToast(getActivity(), msg);
    }

    public void loadAllLstContent() {
        try {
            lstAll = db.getChatByGroup("ALL");

            lstGroup = new ArrayList<Group>(share.getGroup(SharedPrefrence.GROUP_LST).values());

           // lstGroup = share.getGroupLst(SharedPrefrence.GROUP_LST);
            lstPrivate = db.getChatByGroup("PRIVATE");

            if (adapter != null) {
                recentList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            if (enableLst.equals("PRIVATE")) {
                adapter = new RecentAdapter(this, lstPrivate, PreferenceConstants.MESSAGE_TYPE_CHAT);
                recentList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } else if (enableLst.equals("GROUP")) {

                groupAdapter = new GroupAdapter(this, lstGroup, "GROUP");
                recentList.setAdapter(groupAdapter);
                groupAdapter.notifyDataSetChanged();
            } else if (enableLst.equals("ALL")) {

                adapter = new RecentAdapter(this, lstAll, "ALL");
                recentList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAllList() {
        try {
            adapter = new RecentAdapter(this, lstAll, "ALL");
            recentList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            enableLst = "ALL";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateGroupLst()
    {
        try {
            if(enableLst.equals("GROUP"))
            {
                lstGroup = new ArrayList<Group>(share.getGroup(SharedPrefrence.GROUP_LST).values());
                groupAdapter = new GroupAdapter(this, lstGroup, "GROUP");
                recentList.setAdapter(groupAdapter);
                groupAdapter.notifyDataSetChanged();
                enableLst = "GROUP";
            }
            else
            {
                lstGroup = new ArrayList<Group>(share.getGroup(SharedPrefrence.GROUP_LST).values());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void loadGroupList() {
        try {
            lstGroup = new ArrayList<Group>(share.getGroup(SharedPrefrence.GROUP_LST).values());
            groupAdapter = new GroupAdapter(this, lstGroup, "GROUP");
            recentList.setAdapter(groupAdapter);
            groupAdapter.notifyDataSetChanged();
            enableLst = "GROUP";

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPrivateList() {
        try {
            adapter = new RecentAdapter(this, lstPrivate, PreferenceConstants.MESSAGE_TYPE_CHAT);
            recentList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            enableLst = "PRIVATE";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvAll: {
                clickAll();
                break;
            }
            case R.id.tvGroups: {
                clickGroups();
                break;
            }
            case R.id.tvPrivate: {
                clickPrivates();
                break;
            }
            case R.id.createGroup: {
                Intent intent = new Intent(getActivity(), CreateGroup.class);
                intent.putExtra(PreferenceConstants.TYPE, PreferenceConstants.createGroup);
                startActivity(intent);
                break;
            }
        }
    }

    private void clickAll() {
        tvAll.setSelected(true);
        tvGroups.setSelected(false);
        tvPrivate.setSelected(false);
        loadAllList();
    }

    private void clickGroups() {
        tvAll.setSelected(false);
        tvGroups.setSelected(true);
        tvPrivate.setSelected(false);

        loadGroupList();
    }

    private void clickPrivates() {
        tvAll.setSelected(false);
        tvGroups.setSelected(false);
        tvPrivate.setSelected(true);
        loadPrivateList();
    }

    private List<NameValuePair> getMemberJID() {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        nameValuePair.add(new BasicNameValuePair("member_jid_without_ip", Utils.getMyJidWithoutIP(getActivity())));
        return nameValuePair;
    }
}
