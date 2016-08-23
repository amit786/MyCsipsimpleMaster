package com.csipsimple.f5chat.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.csipsimple.f5chat.bean.Contact;
import com.csipsimple.f5chat.bean.Group;
import com.csipsimple.f5chat.bean.RecentChat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class SharedPrefrence {
    public static SharedPreferences myPrefs;
    public static SharedPreferences.Editor prefsEditor;
    public static SharedPrefrence myObj;
    public static String outLetDetailList = "outLetDetailList";
    public static String lat = "lat";
    public static String lng = "lng";
    public static String GET_USER_DETAILS = "getUserDetails";
    public static String OTP = "getOtp";
    public static String MOBILE = "getMobile";
    public static String DEVICETOKEN = "getDeviceToken";
    public static String CONTACT_LIST = "contactList";
    public static String F5_CONTACT = "f5Contact";
    public static String SYNC_ENABLE = "syncEnable";
    public static String NAME = "name";

    /* ContactSync  Rajesh */
    public static String FIRST_TIME = "contact_string";
    public static String CONTACT_STRING = "contact_string";
    public static String LASTSYNCING = "last_syncing";
    public static String SYNCING_REQUEST = "syncing_request";
    public static String MAPPING_OBJ = "mapping_object";

    public static String GROUP_LST = "groupLst";

    public  static final String JID = "xmpp_jid";
    public  static final String PASSWORD = "xmpp_password";
    public static  final String ISLOGIN = "xmpp_logged_in";
    public static final String OUTGOING = "outgoing";
    public static final String INCOMING = "incoming";

    public  static final String GROUPID = "group_id";
    public  static final String ADMINID = "admin_id";
    public  static final String GROUPNAME = "group_name";
    public static final String GROUPS_RESPONSE = "group_response";

    public static final String COUNTRY_NAME = "countryName";
    public static final String COUNTRY_CODE = "countryCode";

    /**
     * Create private constructor
     */
    private SharedPrefrence() {

    }

    /**
     * Create a static method to get instance.
     */
    public static SharedPrefrence getInstance(Context ctx) {
        if (myObj == null) {
            myObj = new SharedPrefrence();
            myPrefs = ctx.getSharedPreferences("com.csipsimple.f5chat", Context.MODE_WORLD_READABLE);
            prefsEditor = myPrefs.edit();
        }
        return myObj;
    }




    public String getValue(String Tag) {
        if (Tag.equals("dealercode"))
            return myPrefs.getString(Tag, "");
        if (Tag.equals("dealername"))
            return myPrefs.getString(Tag, "");
        if (Tag.equals("lat"))
            return myPrefs.getString(Tag, "22.7684");
        if (Tag.equals("lng"))
            return myPrefs.getString(Tag, "75.8957");
        if (Tag.equals("status"))
            return myPrefs.getString(Tag, "");
        if (Tag.equals("Name"))
            return myPrefs.getString(Tag, "");
        if (Tag.equals("Password"))
            return myPrefs.getString(Tag, "");
        if (Tag.equals("Mobile"))
            return myPrefs.getString(Tag, "");
        if (Tag.equals("Email"))
            return myPrefs.getString(Tag, "");
        if (Tag.equals("State"))
            return myPrefs.getString(Tag, "");
        if (Tag.equals("City"))
            return myPrefs.getString(Tag, "");
        if (Tag.equals("Address"))
            return myPrefs.getString(Tag, "");
        if (Tag.equals("DOB"))
            return myPrefs.getString(Tag, "");
        if (Tag.equals("PINCode"))
            return myPrefs.getString(Tag, "");
        if (Tag.equals("IMPdate"))
            return myPrefs.getString(Tag, "");
        if (Tag.equals("Profession"))
            return myPrefs.getString(Tag, "");
        if (Tag.equals("USERNAME"))
            return myPrefs.getString(Tag, "");
        if (Tag.equals("SESSION_KEY"))
            return myPrefs.getString(Tag, "1");
        if (Tag.equals("PROMPTOUTLET"))
            return myPrefs.getString(Tag, "false");
        if (Tag.equals("REMINDER"))
            return myPrefs.getString(Tag, "true");
        if (Tag.equals("dealercode"))
            return myPrefs.getString(Tag, "false");
        return myPrefs.getString(Tag, "");
    }

//	public void setUserData(String Tag, UserDetails userDetails){
//
//		Gson gson = new Gson();
//		String json = gson.toJson(userDetails);
//		prefsEditor.putString(Tag, json);
//		prefsEditor.commit();
//
//	}
//		public UserDetails getUserDetails(String Tag) {
//		String obj = myPrefs.getString(Tag, "defValue");
//		if (obj.equals("defValue")) {
//			return new UserDetails();
//		} else {
//			Type type = new TypeToken<UserDetails>() {
//			}.getType();
//			Gson gson = new Gson();
//			UserDetails userDetails = gson.fromJson(obj, type);
//			return userDetails;
//		}
//	}
//

//
//	public HashMap<String, String> getUserProfile(String Tag) {
//		String obj = myPrefs.getString(Tag, "defValue");
//		if (obj.equals("defValue")) {
//			return new HashMap<String, String>();
//		} else {
//			Type type = new TypeToken<HashMap<String, String>>() {
//			}.getType();
//			Gson gson = new Gson();
//			HashMap<String, String> List = gson.fromJson(obj, type);
//			return List;
//		}
//
//		//return myPrefs.getString(key, defValue)
//	}

    public void setValue(String Tag, String value) {
        prefsEditor.putString(Tag, value);
        prefsEditor.commit();
    }

    public ArrayList<Contact> getContactList(String Tag) {
        String obj = myPrefs.getString(Tag, "defValue");
        if (obj.equals("defValue")) {
            return new ArrayList<Contact>();
        } else {
            Type type = new TypeToken<ArrayList<Contact>>() {
            }.getType();
            Gson gson = new Gson();
            ArrayList<Contact> List = gson.fromJson(obj, type);
            return List;
        }
    }

    public void setContactList(String Tag, ArrayList<Contact> lst) {
        Gson gson = new Gson();
        String json = gson.toJson(lst);
        prefsEditor.putString(Tag, json);
        prefsEditor.commit();
    }


    public HashMap<String, Contact> getMappingObjectList(String Tag) {
        String obj = myPrefs.getString(Tag, "defValue");
        if (obj.equals("defValue")) {
            return new HashMap<String, Contact>();
        } else {
            Type type = new TypeToken<HashMap<String, Contact>>() {
            }.getType();
            Gson gson = new Gson();
            HashMap<String, Contact> List = gson.fromJson(obj, type);
            return List;
        }
    }

    public void setMappingObjectList(String Tag, HashMap<String, Contact> lst) {
        Gson gson = new Gson();
        String json = gson.toJson(lst);
        prefsEditor.putString(Tag, json);
        prefsEditor.commit();
    }

    public HashMap<String, Group> getGroup(String Tag) {
        String obj = myPrefs.getString(Tag, "defValue");
        if (obj.equals("defValue")) {
            return new HashMap<String, Group>();
        } else {
            Type type = new TypeToken<HashMap<String, Group>>() {
            }.getType();
            Gson gson = new Gson();
            HashMap<String, Group> List = gson.fromJson(obj, type);
            return List;
        }
    }

//    public ArrayList<Group> getGroupLst(String Tag) {
//        String obj = myPrefs.getString(Tag, "defValue");
//        if (obj.equals("defValue")) {
//            return new ArrayList<Group>();
//        } else {
//            Type type = new TypeToken<ArrayList<Group>>() {
//            }.getType();
//            Gson gson = new Gson();
//            ArrayList<Group> List = gson.fromJson(obj, type);
//            return List;
//        }
//    }

    public void setGroupLst(String Tag, HashMap<String, Group> lst) {
        Gson gson = new Gson();
        String json = gson.toJson(lst);
        prefsEditor.putString(Tag, json);
        prefsEditor.commit();
    }
}