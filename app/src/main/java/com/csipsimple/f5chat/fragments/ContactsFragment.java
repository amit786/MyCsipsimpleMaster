package com.csipsimple.f5chat.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.csipsimple.f5chat.ConfirmContactDialog;
//import com.csipsimple.f5chat.CustomViewIconTextTabsActivity;
import com.csipsimple.R;
import com.csipsimple.f5chat.bean.Contact;
import com.csipsimple.f5chat.utility.SharedPrefrence;
import com.csipsimple.f5chat.utility.DialogUtility;
import com.csipsimple.f5chat.utility.PreferenceConstants;
import com.csipsimple.ui.TabsViewPagerFragmentActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class ContactsFragment extends Fragment implements View.OnClickListener {
    Map<String, Integer> mapIndex;
    private RelativeLayout f5ContactListLayout, allContactListLayout;
    private com.csipsimple.f5chat.font_family.Font_welcome_scr_Tv_regular textViewF5Chat, textViewAll;
    // Context context;
    ListView allcontactslist;
    ListView contactlist;
    SharedPrefrence share;
    ArrayList<Contact> contactList = new ArrayList<Contact>();
    AllContactsAdapter contactsAdapter;
    ListView F5ContactsList;
    F5ContactAdapter f5ContactAdapter;
    ArrayList<Contact> f5Contacts = new ArrayList<Contact>();
    EditText allEditTextSearch, f5EditTextSearch;
    View rootView;
    TextView textView;
    LinearLayout indexLayout;

    ImageView plus;

    public ContactsFragment() {
        // Required empty public constructor
        share = SharedPrefrence.getInstance(getActivity());
    }

    static class ViewHolder {
        private TextView contactName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Map<String, Integer> mapIndex;
        //contactlist = (ListView) findViewById(R.id.list_fruits);

        rootView = inflater.inflate(R.layout.contact_fragment, container, false);



        f5ContactListLayout = (RelativeLayout) rootView.findViewById(R.id.f5contactlistlayout);
        allContactListLayout = (RelativeLayout) rootView.findViewById(R.id.allcontactlistlayout);
        allcontactslist = (ListView) rootView.findViewById(R.id.allcontactlist);
        textViewF5Chat = (com.csipsimple.f5chat.font_family.Font_welcome_scr_Tv_regular) rootView.findViewById(R.id.textviewf5chat);
        textViewAll = (com.csipsimple.f5chat.font_family.Font_welcome_scr_Tv_regular) rootView.findViewById(R.id.textviewall);

        textViewAll.setSelected(false);
        textViewF5Chat.setSelected(true);

        plus = (ImageView) rootView.findViewById(R.id.plus);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // new ContactSyncAsyncTask(getActivity()).execute();

                Intent intent = new Intent(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT, Uri.parse("tel:" + " "));
                intent.putExtra(ContactsContract.Intents.EXTRA_FORCE_CREATE, true);
                startActivity(intent);
            }
        });
        allEditTextSearch = (EditText) rootView.findViewById(R.id.alledittextsearch);
        f5EditTextSearch = (EditText) rootView.findViewById(R.id.f5edittextsearch);
        F5ContactsList = (ListView) rootView.findViewById(R.id.f5contactslist);
        contactList = share.getContactList(SharedPrefrence.CONTACT_LIST);
        f5Contacts = share.getContactList(SharedPrefrence.F5_CONTACT);
        if (contactList.size() == 0) {
//            openContactSyncView();
        }
        Log.e("f5Contacts " + f5Contacts.size(), "contactList " + contactList.size());

        sortallcontacts();
        sortf5contacts();
        Log.e("name--", contactList + "--");
        contactsAdapter = new AllContactsAdapter(contactList);
        f5ContactAdapter = new F5ContactAdapter(f5Contacts);
        F5ContactsList.setAdapter(f5ContactAdapter);
        allcontactslist.setAdapter(contactsAdapter);
        f5ContactListLayout.setVisibility(View.VISIBLE);
        allContactListLayout.setVisibility(View.GONE);

        getIndexList(f5Contacts);
        // Log.e("list",f5contactarray+"");
        displayIndex();

        F5ContactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotoFriendDetail(position);
            }

        });

        textViewF5Chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewAll.setSelected(false);
                textViewF5Chat.setSelected(true);
                sortf5contacts();
                f5ContactListLayout.setVisibility(View.VISIBLE);
                allContactListLayout.setVisibility(View.GONE);
                indexLayout.removeAllViews();
                getIndexList(f5Contacts);
                displayIndex();

            }
        });

        textViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactList.size() == 0) {
//                    openContactSyncView();
                }
                textViewAll.setSelected(true);
                textViewF5Chat.setSelected(false);
                sortallcontacts();
                f5ContactListLayout.setVisibility(View.GONE);
                allContactListLayout.setVisibility(View.VISIBLE);
                indexLayout.removeAllViews();
                getallcontactIndexList(contactList);
                displayallcontactIndex();
            }
        });
        // Inflate the layout for this fragment

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
            }
        });
        allEditTextSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                try {
                    if (allEditTextSearch.getText().toString() != null) {
                        String txt = allEditTextSearch.getText().toString().trim().toLowerCase();
                        contactsAdapter.getFilter(txt);
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
            }
        });
        return rootView;
    }

    private void getIndexList(ArrayList<Contact> f5Contacts) {
        mapIndex = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < f5Contacts.size(); i++) {
            String f5 = f5Contacts.get(i).getName();
            String index = f5.substring(0, 1).toUpperCase();

            if (mapIndex.get(index) == null)
                mapIndex.put(index, i);
        }
    }

    private void displayIndex() {
        indexLayout = (LinearLayout) rootView.findViewById(R.id.side_index);


        List<String> indexList = new ArrayList<String>(mapIndex.keySet());
        for (String index : indexList) {
            textView = (TextView) getActivity().getLayoutInflater().inflate(
                    R.layout.side_index_item, null);
            // if(index.contains())
            textView.setText(index.toUpperCase());
            textView.setOnClickListener(this);
            indexLayout.addView(textView);
        }
    }

    public void onClick(View view) {
        TextView selectedIndex = (TextView) view;
        F5ContactsList.setSelection(mapIndex.get(selectedIndex.getText().toString()));
        Log.e("index size--", mapIndex.get(selectedIndex.getText()) + "");
    }


    private void getallcontactIndexList(ArrayList<Contact> allcontact) {
        mapIndex = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < allcontact.size(); i++) {
            String all = allcontact.get(i).getName();
            String index = all.substring(0, 1).toUpperCase();

            if (mapIndex.get(index) == null)
                mapIndex.put(index, i);
        }
    }

    private void displayallcontactIndex() {
        LinearLayout indexLayout = (LinearLayout) rootView.findViewById(R.id.side_index1);

        TextView textView;
        List<String> indexList = new ArrayList<String>(mapIndex.keySet());
        for (String index : indexList) {
            textView = (TextView) getActivity().getLayoutInflater().inflate(
                    R.layout.side_index_item, null);
            // if(index.contains())
            textView.setText(index.toUpperCase());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView selectedIndex = (TextView) v;
                    allcontactslist.setSelection(mapIndex.get(selectedIndex.getText().toString()));
                    Log.e("index size--", mapIndex.get(selectedIndex.getText()) + "");
                }
            });
            indexLayout.addView(textView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        TabsViewPagerFragmentActivity.isContactFragmentClicked = false;

        String isfirsttime = share.getValue(SharedPrefrence.FIRST_TIME);
        String isContactSyncRunning = share.getValue(SharedPrefrence.SYNC_ENABLE);
        if (isContactSyncRunning.equals("true") && isfirsttime.equals("true")) {
            DialogUtility.showProgressDialog(getActivity(), true, "Please wait...\nSearching associated participants...");
        }
    }

    public void openContactSyncView() {
        String syncStatus = share.getValue(SharedPrefrence.SYNC_ENABLE);
        if (syncStatus.equals("true")) {
            DialogUtility.showToast(getActivity(), "Please wait...");
        } else {
            if (TabsViewPagerFragmentActivity.isContactFragmentClicked) {
                Intent incontact = new Intent(getActivity(), ConfirmContactDialog.class);
                startActivityForResult(incontact, PreferenceConstants.CONFIRM_READ_CONTACT);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        DialogUtility.pauseProgressDialog();
    }

    class AllContactsAdapter extends BaseAdapter {
        ArrayList<Contact> allContactList;
        ArrayList<Contact> tempArrayList = new ArrayList<>();

        public AllContactsAdapter(ArrayList<Contact> list) {
            this.allContactList = list;
            this.tempArrayList.addAll(list);
        }

        @Override
        public int getCount() {
            return allContactList.size();

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
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(R.layout.contactsrow, null);
                holder = new ViewHolder();
                holder.contactName = (TextView) convertView.findViewById(R.id.contactName);
                ImageView contactImage = (ImageView) convertView.findViewById(R.id.contactImage);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //Contact contactbeanobject=allContactList.get(position);
            holder.contactName.setText(allContactList.get(position).getName());
            return convertView;
        }

        public void getFilter(String str) {
            allContactList.clear();
            String txt = str.toLowerCase(Locale.getDefault());
            if (str.length() == 0) {
                allContactList.addAll(tempArrayList);
            } else {
                for (Contact contact : tempArrayList) {
                    if (contact.getName().toLowerCase().contains(txt)) {
                        allContactList.add(contact);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    class F5ContactAdapter extends BaseAdapter {
        ArrayList<Contact> f5list;
        ArrayList<Contact> tempF5list = new ArrayList<>();

        public F5ContactAdapter(ArrayList<Contact> list) {
            this.f5list = list;
            this.tempF5list.addAll(list);
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
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater f5inflater = getActivity().getLayoutInflater();
                convertView = f5inflater.inflate(R.layout.contactsrow, null);
                holder = new ViewHolder();
                holder.contactName = (TextView) convertView.findViewById(R.id.contactName);
                Log.e("name--", holder.contactName + "--");
                ImageView contactImage = (ImageView) convertView.findViewById(R.id.contactImage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Contact contactbeanobject = f5list.get(position);
            holder.contactName.setText(contactbeanobject.getName());

            return convertView;
        }

        public void getFilter(String str) {
            f5list.clear();
            String txt = str.toLowerCase(Locale.getDefault());
            if (str.length() == 0) {
                f5list.addAll(tempF5list);
            } else {
                for (Contact contact : tempF5list) {
                    if (contact.getName().toLowerCase().contains(txt)) {
                        f5list.add(contact);
                    }
                }
            }
            notifyDataSetChanged();

        }

    }

    public void sortallcontacts() {
        if (contactList.size() != 0)
            Collections.sort(contactList, new Comparator<Contact>() {

                @Override
                public int compare(Contact lhs, Contact rhs) {
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                }
            });
    }

    public void sortf5contacts() {
        if (f5Contacts.size() != 0)
            Collections.sort(f5Contacts, new Comparator<Contact>() {
                @Override
                public int compare(Contact lhs, Contact rhs) {
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                }
            });
    }



    public void gotoFriendDetail(int position) {
        // share.setValue();
        String jid = f5Contacts.get(position).getJID();
        String mobile_no = f5Contacts.get(position).getNumber();
        String user_name = f5Contacts.get(position).getName();
        String countryCode = f5Contacts.get(position).getCountryCode();

        Intent frienddetail = new Intent(getActivity(), F5FriendDetail.class);
        frienddetail.putExtra("mobile", mobile_no);
        frienddetail.putExtra("user_name", user_name);
        frienddetail.putExtra(SharedPrefrence.JID, jid);
        frienddetail.putExtra("countryCode", countryCode);
        startActivity(frienddetail);
    }

    public void updateList() {
        try {
            sortallcontacts();
            sortf5contacts();
            contactList = share.getContactList(SharedPrefrence.CONTACT_LIST);
            f5Contacts = share.getContactList(SharedPrefrence.F5_CONTACT);
            contactsAdapter = new AllContactsAdapter(contactList);
            f5ContactAdapter = new F5ContactAdapter(f5Contacts);
            F5ContactsList.setAdapter(f5ContactAdapter);
            allcontactslist.setAdapter(contactsAdapter);

            DialogUtility.pauseProgressDialog();
        } catch (Exception ex) {

        }

    }

}

