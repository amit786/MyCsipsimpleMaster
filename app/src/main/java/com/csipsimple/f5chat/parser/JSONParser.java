package com.csipsimple.f5chat.parser;

import android.content.Context;
import android.util.Log;

import com.csipsimple.f5chat.background.ContactSyncAsyncTask;
import com.csipsimple.f5chat.bean.Contact;
import com.csipsimple.f5chat.bean.Group;
import com.csipsimple.f5chat.bean.GroupMember;
import com.csipsimple.f5chat.database.DatabaseHandler;
import com.csipsimple.f5chat.utility.PreferenceConstants;
import com.csipsimple.f5chat.utility.SharedPrefrence;
import com.csipsimple.f5chat.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class JSONParser {

    public Context context;
    public String jsonObjResponse;
    public JSONObject jObj;
    public SharedPrefrence share;
    public String STATUS = "";
    public String MESSAGE = "";
    public boolean VERIFY;
    public DatabaseHandler db;

    public JSONParser(Context context, String response) {
        this.context = context;
        this.jsonObjResponse = response;
        share = SharedPrefrence.getInstance(context);
        db = new DatabaseHandler(context);
        try {
            jObj = new JSONObject(response);
            MESSAGE = getJsonString(jObj, "message");
            STATUS = getJsonString(jObj, "status");

            if (STATUS.equals(PreferenceConstants.PASS_STATUS)) {
                VERIFY = true;
            } else {
                VERIFY = false;
            }
        } catch (JSONException e) {
            jObj = null;
            e.printStackTrace();
        }
    }



    public void getF5Contacts(ContactSyncAsyncTask contactSyncAsyncTask) {
        try {

            Log.e("Contact Syncing", "Syncing Started");
            System.out.println("<<< Mapping Object : " + contactSyncAsyncTask.mappingObj.size());

            JSONObject getf5contacts = new JSONObject(jsonObjResponse);
            MESSAGE = getJsonString(getf5contacts, "message");
            STATUS = getJsonString(getf5contacts, "status");

            if(STATUS.equals(PreferenceConstants.PASS_STATUS)) {
                            /* Check last syncing request */
                share.setValue(SharedPrefrence.FIRST_TIME, "false");
                share.setValue(SharedPrefrence.SYNCING_REQUEST, "true"); // Read Contact Finished, and API request success
            }

            JSONArray message = getJsonArray(getf5contacts, "contact");
            ArrayList<Contact> f5Contact = new ArrayList<Contact>();
            if (message.length() > 0)
                share.setContactList(SharedPrefrence.F5_CONTACT, f5Contact);
            for (int i = 0; i < message.length(); i++) {
                JSONObject F5contact = message.getJSONObject(i);
                String mobile = getJsonString(F5contact, "mobile");
                String jid = getJsonString(F5contact, "jid");

                // To get country code from Jabber ID
                String countryCode = "";
                if (jid.contains(mobile)) {
                    countryCode = jid.replace(mobile, "");
                    countryCode = countryCode.replace(PreferenceConstants.SERVERATTHERSTE, "");
                }

                try {
                    if(contactSyncAsyncTask.mappingObj.containsKey(mobile)) {
                        Contact contact = contactSyncAsyncTask.mappingObj.get(mobile);
                        contact.setType(PreferenceConstants.F5_CONTACT);
                        contact.setJID(jid);
                        contact.setCountryCode(countryCode);
                        contactSyncAsyncTask.mappingObj.put(mobile, contact);
                        f5Contact.add(contact);

                        System.out.println("<<< Mapping Object F5 Contact Added : " + mobile);
                    }
                    else {
                        System.out.println("<<< Mapping Object F5 Contact Not Found : " + mobile);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            share.setContactList(SharedPrefrence.F5_CONTACT, f5Contact);

            System.out.println("<<< F5 Contacts : " + f5Contact);

            ArrayList<Contact> allContact = new ArrayList<Contact>(contactSyncAsyncTask.mappingObj.values());

            DatabaseHandler db = new DatabaseHandler(context);
            if (allContact.size() > 0)
                db.deleteOldContent();
            for (int i = 0; i < allContact.size(); i++) {
                db.addContactToDB(allContact.get(i));
            }
            share.setContactList(SharedPrefrence.CONTACT_LIST, allContact);
            System.out.println("<-- syncing completed");


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject getJsonObject(JSONObject obj, String parameter) {
        try {
            return obj.getJSONObject(parameter);
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }

    }

    public static String getJsonString(JSONObject obj, String parameter) {
        try {
            return obj.getString(parameter);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public static JSONArray getJsonArray(JSONObject obj, String parameter) {
        try {
            return obj.getJSONArray(parameter);
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }

    }

    public void getGroupLst() {
        try {

            HashMap<String, Group> lst = share.getGroup(SharedPrefrence.GROUP_LST);
            JSONArray groups = getJsonArray(jObj, "groups");
            for (int i = 0; i < groups.length(); i++) {

                JSONObject arr = groups.getJSONObject(i);

                String group_jid = getJsonString(arr, "group_jid");
                String group_name = getJsonString(arr, "group_name");
                String group_admin_jid = getJsonString(arr, "group_admin_jid");
                String url = getJsonString(arr, "url");

                //Group group = Utils.getGroupObj(Utils.formatGroupJID(group_jid), lst);

                Group group = null;
                try {
                    group = lst.get(Utils.formatGroupJID(group_jid));
                } catch (Exception ex) {
                    group = new Group();
                }
                if (group == null) {
                    group = new Group();
                }

                try {
                    group_name = Utils.utf8UrlDecoding(group_name);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }

                group.setGroup_name(group_name);
                group.setGroup_jid(Utils.formatGroupJID(group_jid));
                group.setAdmin_jid(group_admin_jid);
                group.setGroup_image_url(url);

                lst.put(Utils.formatGroupJID(group_jid), group);
            }
            share.setGroupLst(SharedPrefrence.GROUP_LST, lst);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getUrlFromResponse() {

        return getJsonString(jObj, "url");
    }

    public Group createGroup(ArrayList<GroupMember> memberLst) {
        Group group = null;
        try {
            String group_name = getJsonString(jObj, "group_name");
            String group_jid = getJsonString(jObj, "group_jid");
            String creation_date = getJsonString(jObj, "creation_date");
            String time_stamp = getJsonString(jObj, "time_stamp");
            String admin_jid = getJsonString(jObj, "admin_jid");
            String group_image_url = getJsonString(jObj, "group_image_url");

            group = new Group();
            group.setGroup_name(group_name);
            group.setGroup_jid(Utils.formatGroupJID(group_jid));
            group.setCreation_date(creation_date);
            group.setTime_stamp(time_stamp);
            group.setAdmin_jid(admin_jid);
            group.setGroup_image_url(group_image_url);

            group.setGroupMember(memberLst);

            HashMap<String, Group> lst = share.getGroup(SharedPrefrence.GROUP_LST);
            lst.put(Utils.formatGroupJID(group_jid), group);

            share.setGroupLst(SharedPrefrence.GROUP_LST, lst);
            return group;
        } catch (Exception ex) {
            ex.printStackTrace();
            return group;
        }
    }

    public void getGroupDetail(Context ctx) {
        try {
            String group_name = getJsonString(jObj, "group_name");
            String group_jid = getJsonString(jObj, "group_jid");
            String creation_date = getJsonString(jObj, "creation_date");
            String time_stamp = getJsonString(jObj, "time_stamp");
            String admin_jid = getJsonString(jObj, "admin_jid");
            String group_image_url = getJsonString(jObj, "group_image_url");

            JSONArray groups = getJsonArray(jObj, "group_members");

            ArrayList<GroupMember> lst = new ArrayList<GroupMember>();
            for (int i = 0; i < groups.length(); i++) {
                JSONObject arr = groups.getJSONObject(i);
                String member_jid = getJsonString(arr, "member_jid");
                String is_admin = getJsonString(arr, "is_admin");
                String image_url = getJsonString(arr, "image_url");

                GroupMember gMember = new GroupMember();
                gMember.setIs_admin(is_admin);
                gMember.setName(Utils.getContactNameByNumber(ctx, member_jid));
                gMember.setImage_url(image_url);
                gMember.setMember_jid(member_jid);
                lst.add(gMember);
            }

            HashMap<String, Group> groupHashMap = share.getGroup(SharedPrefrence.GROUP_LST);
            //Group group = Utils.getGroupObj(Utils.formatGroupJID(group_jid), groupHashMap);//groupHashMap.get(Utils.formatGroupJID(group_jid));
            Group group = null;
            try {
                group = groupHashMap.get(Utils.formatGroupJID(group_jid));
            } catch (Exception ex) {
                group = new Group();
            }
            if (group == null) {
                group = new Group();
            }

            group.setGroup_name(group_name);
            group.setGroup_jid(Utils.formatGroupJID(group_jid));
            group.setCreation_date(creation_date);
            group.setTime_stamp(time_stamp);
            group.setAdmin_jid(admin_jid);
            group.setGroup_image_url(group_image_url);

            group.setGroupMember(lst);
            groupHashMap.put(Utils.formatGroupJID(group_jid), group);

            share.setGroupLst(SharedPrefrence.GROUP_LST, groupHashMap);
        } catch (Exception ex) {
        }
    }


}
