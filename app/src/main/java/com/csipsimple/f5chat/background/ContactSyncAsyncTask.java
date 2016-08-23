package com.csipsimple.f5chat.background;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.provider.ContactsContract;
import android.util.Log;

import com.csipsimple.f5chat.bean.Contact;
import com.csipsimple.f5chat.parser.JSONParser;
import com.csipsimple.f5chat.utility.Chatutility;
import com.csipsimple.f5chat.utility.SharedPrefrence;
import com.csipsimple.f5chat.utility.PreferenceConstants;
import com.csipsimple.f5chat.utility.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactSyncAsyncTask extends AsyncTask<String, String, String> {
    //    String contact_syncing_url = "https://85.214.17.140/csipsimple.f5chat/contact_syncing.php";
    String parameters;

    private Context context;
    boolean isNetworkOnMainThreadExceptionOccurs = false;
    String all_contact_numbers = "";
    String res;
    SharedPrefrence share;
    JSONObject post_dict;
    ArrayList<Contact> Items = new ArrayList<Contact>();
    public HashMap<String, Contact> mappingObj = new HashMap<String, Contact>();

    public ContactSyncAsyncTask(Context context) {
        this.context = context;
        post_dict = new JSONObject();
        share = SharedPrefrence.getInstance(context);

        if(Utils.checkInternetConn(context)) {
            share.setValue(SharedPrefrence.SYNC_ENABLE, "true");
        } else {
            share.setValue(SharedPrefrence.SYNC_ENABLE, "false");
        }

        Log.e("contacts sync", "<-- contacts sync");
    }


    @Override
    protected String doInBackground(String... params) {
        String contacts = "";
        try {
            ReadPhoneContacts();

            /* Check last syncing request */
            share.setValue(SharedPrefrence.SYNCING_REQUEST, "false"); // Read Contact Finished, and now send for request

            all_contact_numbers = all_contact_numbers.substring(0, all_contact_numbers.length() - 1);
            parameters = "username=" + Utils.getMyJidWithoutIP(context) + "&contacts=" + all_contact_numbers;
            String f5Contacts = Chatutility.excutePost(PreferenceConstants.CONTACT_SYNC, parameters);

            if (!(f5Contacts == null)) {
                JSONParser jsonParser = new JSONParser(context, f5Contacts);
                jsonParser.getF5Contacts(this);
                contacts = jsonParser.MESSAGE;
            }
            return contacts;
        } catch (NetworkOnMainThreadException ex) {
            isNetworkOnMainThreadExceptionOccurs = true;
            share.setValue(SharedPrefrence.SYNC_ENABLE, "false");
            return contacts;
        } catch (Exception e) {
            share.setValue(SharedPrefrence.SYNC_ENABLE, "false");
            return contacts;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (isNetworkOnMainThreadExceptionOccurs) {

            if(Utils.hasActiveInternetConnection(context)) {
                new ContactSyncAsyncTask(context).execute();
            }
        }

        share.setValue(SharedPrefrence.SYNC_ENABLE, "false");
        Intent i = new Intent(PreferenceConstants.SYNC_COMPELLED);
        i.setPackage(context.getPackageName());
        context.sendBroadcast(i);
    }


    private void ReadPhoneContacts() {

        String strNum = share.getValue(SharedPrefrence.CONTACT_STRING);
        all_contact_numbers = share.getValue(SharedPrefrence.LASTSYNCING);

        mappingObj.clear();
        mappingObj = share.getMappingObjectList(SharedPrefrence.MAPPING_OBJ);

        Items.clear();
        Items = share.getContactList(SharedPrefrence.CONTACT_LIST);

        String number = "";
        String name = "";
        String image_uri = "";
        try {
            Cursor cur = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cur.getCount() > 0) {
                cur.moveToFirst();

                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));

                    if (!strNum.contains("," + id + ",")) {

                        strNum = strNum + "," + id;

                        name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        Cursor pCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                        image_uri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                        while (pCur.moveToNext()) {
                            if (name.equals(null) || name.length() > 0) {
                                number = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                number = number.replaceAll("[\\s\\-()]", "");
                                if (number.startsWith("+")) {
                                    number = number.replace("+", "").trim();
                                }
                                number = number.replaceAll("\\s+", "");

                                if (number.charAt(0) == '0') {
                                    number = number.substring(1).trim();
                                }
                                number = number.trim();
                                if (!all_contact_numbers.contains(number))

                                    all_contact_numbers = all_contact_numbers + number + ",";
//                            Log.e("name==", name + "");
//                            Log.e("number==", number + "");
                                Contact contact = new Contact(name, number, "", image_uri, PreferenceConstants.ALL_CONTACT, share.getValue(SharedPrefrence.JID));
                                mappingObj.put(number, contact);
                                Items.add(contact);

                                number = "";
                            }
                        }

                        pCur.close();
                        name = "";
                        image_uri = "";
                    }
                }

                share.setValue(SharedPrefrence.CONTACT_STRING, strNum);
                share.setValue(SharedPrefrence.LASTSYNCING, all_contact_numbers);
                share.setMappingObjectList(SharedPrefrence.MAPPING_OBJ, mappingObj);

                share.setContactList(SharedPrefrence.CONTACT_LIST, Items);

                // share.getContactList(SharedPrefrence.CONTACT_LIST);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
