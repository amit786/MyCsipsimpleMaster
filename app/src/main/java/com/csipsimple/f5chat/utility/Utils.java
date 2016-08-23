package com.csipsimple.f5chat.utility;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

import com.csipsimple.R;
import com.csipsimple.f5chat.bean.AddressBook;
import com.csipsimple.f5chat.bean.ChatBody;
import com.csipsimple.f5chat.bean.CountryCodeBean;

import org.jivesoftware.smack.packet.Message;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {
    public static ArrayList<CountryCodeBean> countrylist = new ArrayList<CountryCodeBean>();
    private static AlertDialog dialog = null;

    //Scale image
    public static Bitmap getBitmap(byte[] mAvatar, Context context) {
        Bitmap ava = null;
        if (mAvatar != null) {
            ava = scaleImage(69, 69, mAvatar);
        } else {
            ava = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.img);
        }
        return ava;
    }

    public static void fragmentPageChange(Context context, Fragment fragment, int page_id) {
        FragmentTransaction transaction = fragment.getActivity().getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(page_id, fragment, "setting").commit();
    }

    public static Bitmap scaleImage(int widht, int hight, byte[] arrey) {
        // Part 1: Decode image
        Bitmap unscaledBitmap = ScalingUtilities.decodeByte(arrey, 0,
                widht, hight, ScalingUtilities.ScalingLogic.CROP);

        // Part 2: Scale image
        Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, widht,
                hight, ScalingUtilities.ScalingLogic.CROP);
        unscaledBitmap.recycle();

        return scaledBitmap;

    }

    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();
        values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);
        context.getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public static void addVideoToGallery(final String filepath, final Context context) {
        ContentValues values = new ContentValues();
        values.put(Video.Media.TITLE, System.currentTimeMillis());
        values.put(Video.Media.MIME_TYPE, "video/mp4");
        values.put(Video.Media.DATA, filepath);
        /*values.put(MediaStore.Video.Media.TITLE, System.currentTimeMillis());
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
	    values.put(MediaStore.Video.Media.DATA, filepath);*/
        context.getContentResolver().insert(Video.Media.EXTERNAL_CONTENT_URI, values);
    }

    public static boolean checkInternetConn(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        //NetworkInfo roaming = connMgr.getNetworkInfo(ConnectivityManager.ty)
        if (mobile.isConnected() || wifi.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static int getNetworkType(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi.isConnected()) {
            return ConnectivityManager.TYPE_WIFI;
        } else {
            return ConnectivityManager.TYPE_MOBILE;
        }
    }

    public static Boolean checkRoaming(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkType = connManager.getActiveNetworkInfo();
        return networkType.isRoaming();

    }

    public static void Dialog(final Activity activity, String dialogmsg, String Message) {
        try {
            AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
            alertbox.setTitle(dialogmsg);
            alertbox.setMessage(Message);
            alertbox.setCancelable(false);
            alertbox.setPositiveButton("OK", new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {

                            arg0.dismiss();
                        }
                    });
            alertbox.show();
        } catch (Exception e) {

        }

    }


    public static String getRealPathFromURI(Context context, String contentURI) {
        Uri contentUri = Uri.parse(contentURI);

        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(Images.ImageColumns.DATA);

            return cursor.getString(idx);
        }
    }


    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 76, stream);
        return stream.toByteArray();
    }


    public static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getPath(), "F5Chat" + "/F5Chat Images/Sent");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }


    public static String splitToComponentTimes(long longVal) {
        // TODO Auto-generated method stub
        String ret = "00:00";
        int hours = (int) longVal / 3600;
        int remainder = (int) longVal - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;

        int[] ints = {hours, mins, secs};
        ret = String.format("%02d", ints[1]) + ":" + String.format("%02d", ints[2]);
        return ret;
    }

    public static String getTimeString(long milliSeconds) {
        SimpleDateFormat dateFormater = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Date date = new Date(milliSeconds);
        return dateFormater.format(date.getTime());
    }

    public static String getDateString(long milliSeconds) {

        SimpleDateFormat dateFormater = new SimpleDateFormat("d MMMM yyyy", Locale.US);
        Date date = new Date(milliSeconds);
        return dateFormater.format(date);
    }


    public static StringBuilder CreateBaseStringBuilder() {
        StringBuilder BaseDocument = new StringBuilder();
        BaseDocument.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        return (BaseDocument);
    }

    /*  latest change */
    public static String getImageUrlWithAuthority(Context context, Uri uri) {
        InputStream is = null;
        if (uri.getAuthority() != null) {
            try {
                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                return writeToTempImageAndGetPathUri(context, bmp).toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Uri writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(CompressFormat.JPEG, 100, bytes);
        String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

//    public static boolean hasPermissionInManifest(Activity activity, int requestCode, String permissionName) {
//        if (ContextCompat.checkSelfPermission(activity,
//                permissionName)
//                != PackageManager.PERMISSION_GRANTED) {
//// No explanation needed, we can request the permission.
//            ActivityCompat.requestPermissions(activity,
//                    new String[]{permissionName},
//                    requestCode);
//        } else {
//            return true;
//        }
//        return false;
//    }

    public static void showAlertDialog(Context context, String title,
                                       String msg, String btnText,
                                       DialogInterface.OnClickListener listener) {

        if (listener == null)
            listener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface,
                                    int paramInt) {

                    Log.e("value", paramInt + "----");

                    paramDialogInterface.dismiss();
                    //paramDialogInterface.dismiss();
                }
            };


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(btnText, listener);
        dialog = builder.create();
        dialog.setCancelable(false);
        try {
            dialog.show();
        } catch (Exception e) {
// TODO: handle exception
        }

    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context ctx) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String getNumberFromJID(String JID) {
        if (JID != null) {
            return JID.split("@")[0];
        }
        return JID;
    }

    public static String getJIDWithoutHost(Context ctx) {
        SharedPrefrence share = SharedPrefrence.getInstance(ctx);
        if (share.getValue(SharedPrefrence.JID) != null) {
            return share.getValue(SharedPrefrence.JID).split("@")[0];
        }
        return share.getValue(SharedPrefrence.JID).split("@")[0];
    }

    public static String getMyJID(Context ctx) {
        SharedPrefrence share = SharedPrefrence.getInstance(ctx);
        return share.getValue(SharedPrefrence.JID);
    }

    public static String getCurentDateIFormate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static String getFormattedTime(long timestamp) {

        long oneDayInMillis = 24 * 60 * 60 * 1000;
        long timeDifference = System.currentTimeMillis() - timestamp;


        if (timeDifference < oneDayInMillis) {
            return DateFormat.format("hh:mm a", timestamp).toString();
        } else {
            return DateFormat.format("dd MMM - hh:mm a", timestamp).toString();
//            return DateFormat.format("dd MMM YYYY", timestamp).toString();
        }
    }

    public static ChatBody getChatBodyForChatType(String txt, String OwnerJID, String FreindJID, String type,
                                                  String bound, String status, Message newMessage, String Category) {

        ChatBody chat = new ChatBody();
        chat.setOwnerJID(OwnerJID);
        chat.setFriendJID(FreindJID);
        chat.setMessageID(newMessage.getPacketID());
        chat.setMessage(txt);
        chat.setMessageType(type);
        chat.setInOutBound(bound);
        chat.setDate(Utils.getCurentDateIFormate());
        chat.setTimeStamp(String.valueOf(System.currentTimeMillis()));
        chat.setProgress(PreferenceConstants.CHAT_PROGRESS);
        chat.setStatus(status);
        chat.setCategory(Category);
        return chat;
    }

    public static ChatBody getChatBodyForChatType(String txt, String OwnerJID, String FreindJID, String type,
                                                  String bound, String status, Message newMessage, String Category, String MsgFrom) {


        ChatBody chat = new ChatBody();
        chat.setOwnerJID(OwnerJID);
        chat.setFriendJID(FreindJID);
        chat.setMessageID(newMessage.getPacketID());
        chat.setMessage(txt);
        chat.setMessageType(type);
        chat.setInOutBound(bound);
        chat.setDate(Utils.getCurentDateIFormate());
        chat.setTimeStamp(String.valueOf(System.currentTimeMillis()));
        chat.setProgress(PreferenceConstants.CHAT_PROGRESS);
        chat.setStatus(status);
        chat.setCategory(Category);
        chat.setMsgFrom(MsgFrom);

        return chat;
    }

    public static void showAlertDialog(final Activity activity, String title, String message) {
        QustomDialogBuilder qustomDialogBuilder = new QustomDialogBuilder(activity).
                setTitle(title).
                setTitleColor(PreferenceConstants.BLUE).
                setDividerColor(PreferenceConstants.BLUE).
                setMessage(message);

        qustomDialogBuilder.setCancelable(false);

        qustomDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        qustomDialogBuilder.show();
    }

    public static int generateRandomNumber() {
        Random r = new Random();
        int Low = 100000;
        int High = 999999;
        return r.nextInt(High - Low) + Low;
    }

    public static String getDeviceToken(Context ctx) {
        TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceid = telephonyManager.getDeviceId();
        return deviceid;
    }


    public static String getContactNameByNumber(Context ctx, String phoneNumber) {

//        if (phoneNumber.contains(PreferenceConstants.SERVERATTHERSTE)) {
//            phoneNumber = phoneNumber.replace(PreferenceConstants.SERVERATTHERSTE, "");
//        }

        phoneNumber = Utils.getNumberFromJID(phoneNumber);

        Uri uri;
        String[] projection;
        Uri mBaseUri = Contacts.Phones.CONTENT_FILTER_URL;
        projection = new String[]{Contacts.People.NAME};
        try {
            Class<?> c = Class
                    .forName("android.provider.ContactsContract$PhoneLookup");
            mBaseUri = (Uri) c.getField("CONTENT_FILTER_URI").get(mBaseUri);
            projection = new String[]{"display_name"};
        } catch (Exception e) {
        }

        uri = Uri.withAppendedPath(mBaseUri, Uri.encode(phoneNumber));
        Cursor cursor = ctx.getContentResolver().query(uri, projection, null,
                null, null);

        if (cursor.moveToFirst()) {
            phoneNumber = cursor.getString(0);
        }

        cursor.close();
        cursor = null;

        return phoneNumber;
    }

    public static String getUserId(Context ctx) {
        SharedPrefrence share = SharedPrefrence.getInstance(ctx);
        String mobileWithCountryCode = share.getValue(SharedPrefrence.COUNTRY_CODE) +
                share.getValue(SharedPrefrence.MOBILE);
        return mobileWithCountryCode;
    }

    public static String getMobile(Context ctx) {
        SharedPrefrence share = SharedPrefrence.getInstance(ctx);
        String mobileWithCountryCode = share.getValue(SharedPrefrence.COUNTRY_CODE) +
                share.getValue(SharedPrefrence.MOBILE);
        return mobileWithCountryCode;
    }

    public static String getMyJidWithoutIP(Context ctx) {
        SharedPrefrence share = SharedPrefrence.getInstance(ctx);
        String jid = share.getValue(SharedPrefrence.JID);
        jid = jid.replace(PreferenceConstants.SERVERATTHERSTE, "");
        jid = jid.replace("+", "");
        return jid;
    }

    public static String getGroupJID(String msg) {
        try {
            String[] val = msg.split(PreferenceConstants.SEPERATOR);
            return val[1];
        } catch (Exception ex) {
            return "";
        }
    }

    public static String getMessageFromGroup(String msg) {
        try {
            String[] val = msg.split(PreferenceConstants.SEPERATOR);
            return val[0];
        } catch (Exception ex) {
            return "";
        }
    }

    /*Single Message*/

    public static String addGroupJIDIntoMessage(ChatBody chat) {
        return chat.getMessage() + PreferenceConstants.SEPERATOR + chat.getFriendJID() + PreferenceConstants.SEPERATOR +"GROUP_NAME";
    }

    public static String fileTransferSingleImageVideo(ChatBody filesInfo) {
        return filesInfo.getUrl() + PreferenceConstants.SEPERATOR + filesInfo.getThumb();
    }

    public static String fileTransferSingleAudio(ChatBody chat) {
        return chat.getUrl() + PreferenceConstants.SEPERATOR + chat.getAudio_title();
    }

    public static String fileTransferSingleContact(AddressBook addressBook) {
        return "Contact" + PreferenceConstants.SEPERATOR + addressBook.getName() + PreferenceConstants.SEPERATOR + addressBook.getNumber();
    }

    /*Group Message */

    public static String fileTransferGroupImageVideo(ChatBody filesInfo) {
        return filesInfo.getUrl() + PreferenceConstants.SEPERATOR + filesInfo.getThumb() + PreferenceConstants.SEPERATOR + filesInfo.getFriendJID() + PreferenceConstants.SEPERATOR +"GROUP_NAME";
    }

    public static String fileTransferGroupAudio(ChatBody chat) {
        return chat.getUrl() + PreferenceConstants.SEPERATOR + chat.getAudio_title() + PreferenceConstants.SEPERATOR + chat.getFriendJID() + PreferenceConstants.SEPERATOR +"GROUP_NAME";
    }

    public static String fileTransferGroupContact(AddressBook addressBook, ChatBody chat) {
        return "Contact" + PreferenceConstants.SEPERATOR + addressBook.getName() + PreferenceConstants.SEPERATOR + addressBook.getNumber() + PreferenceConstants.SEPERATOR + chat.getFriendJID() + PreferenceConstants.SEPERATOR +"GROUP_NAME";
    }



    public static String formatGroupJID(String JID) {
        if (JID.contains(PreferenceConstants.SERVERATTHERSTE)) {
            JID = JID.replace(PreferenceConstants.SERVERATTHERSTE, "");
        }
        if (!JID.contains("@broadcast." + PreferenceConstants.SERVER)) {
            JID = JID + "@broadcast." + PreferenceConstants.SERVER;
        }
        return JID;
    }

    public static String getJIDWithoutIP(String JID) {
        try {
            String[] val = JID.split("@");
            return val[0];
        } catch (Exception ex) {
            return "";
        }
    }

    public static String utf8UrlEncoding(String message) throws UnsupportedEncodingException {

        try {
            message = URLEncoder.encode(message, "UTF-8").replaceAll("\\+", "%20");
        }catch (Exception e) {}

        return message;
    }

    public static String utf8UrlDecoding(String message) throws UnsupportedEncodingException {

        try {
            message = URLDecoder.decode(message, "UTF-8");
        }catch (Exception e) {}

        return message;
    }

//    public static Group getGroupObj(String JID, HashMap<String, Group> map) {
//        Group group = null;
//        try {
//            group = map.get(Utils.formatGroupJID(JID));
//            return group;
//        }
//        catch (Exception ex)
//        {
//            if (group == null) {
//                group = new Group();
//                return group;
//            } else
//                return new Group();
//        }
//    }

//    public static String createGroup(String message) throws UnsupportedEncodingException {
//        return URLDecoder.decode(message, "UTF-8");
//    }
//
//    public static Group getGroupObj(String JID,HashMap<String, Group> map) {
//        try {
//            return map.get(JID);
//        }
//        catch (Exception ex)
//        {
//            return new Group();
//        }
//    }


    //============================================

    //    public static String getGroupIdFromGroupNotity(String msg) {
//        try {
//            String[] val = msg.split(PreferenceConstants.SEPERATOR);
//            return val[0];
//        }
//        catch (Exception ex)
//        {
//            return "";
//        }
//    }
//
//    public static String getGroupNameFromGroupNotity(String msg) {
//        try {
//            String[] val = msg.split(PreferenceConstants.SEPERATOR);
//            return val[1];
//        }
//        catch (Exception ex)
//        {
//            return "";
//        }
//    }
//
//    public static String getGroupImageUrlFromGroupNotity(String msg) {
//        try {
//            String[] val = msg.split(PreferenceConstants.SEPERATOR);
//            return val[2];
//        }
//        catch (Exception ex)
//        {
//            return "";
//        }
//    }
//
//    public static String getGroupAdminJIDFromGroupNotity(String msg, String subject) {
//
//        String adminJID = "";
//        try {
//            String[] val = msg.split(PreferenceConstants.SEPERATOR);
//            if(subject.equals(PreferenceConstants.GN_CREATE_GROUP) || subject.equals(PreferenceConstants.GN_UPDATE_GROUP)) {
//                adminJID = val[3];
//            } else if(subject.equals(PreferenceConstants.GN_ADD_TO_GROUP) || subject.equals(PreferenceConstants.GN_DELETE_FROM_GROUP)) {
//                adminJID = val[2];
//            }
//        }
//        catch (Exception ex)
//        { }
//
//        return adminJID;
//    }
//
//    public static String getGroupUpdatedByUserFromGroupNotify(String msg) {
//        try {
//            String[] val = msg.split(PreferenceConstants.SEPERATOR);
//            return val[4];
//        }
//        catch (Exception ex)
//        {
//            return "";
//        }
//    }
    public static String getBareJID(String fromJID) {
        String from = fromJID;
        String contactJid = "";
        if (from.contains("/")) {
            contactJid = from.split("/")[0];
            DialogUtility.showLOG("The real jid is :" + contactJid);
        } else {
            contactJid = from;
        }
        return contactJid;
    }

    //============================================
    public static String getStringFromIndex(String msg, int index) {
        try {
            String[] val = msg.split(PreferenceConstants.SEPERATOR);
            return val[index];
        } catch (Exception ex) {
            return "";
        }
    }

    public static String[] getArrayFromString(String val, String seperator) {
        String[] array = null;
        try {
            array = val.split(seperator);
            return array;
        } catch (Exception ex) {
            return array;
        }
    }


    public static void WritePhoneContact(String displayName, String number, Context cntx /*App or Activity Ctx*/) {
        Context contetx = cntx; //Application's context or Activity's context
        String strDisplayName = displayName; // Name of the Person to add
        String strNumber = number; //number of the person to add with the Contact

        ArrayList<ContentProviderOperation> cntProOper = new ArrayList<ContentProviderOperation>();
        int contactIndex = cntProOper.size();//ContactSize

        //Newly Inserted contact
        // A raw contact will be inserted ContactsContract.RawContacts table in contacts database.
        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)//Step1
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

        //Display name will be inserted in ContactsContract.Data table
        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)//Step2
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, contactIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, strDisplayName) // Name of the contact
                .build());
        //Mobile number will be inserted in ContactsContract.Data table
        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)//Step 3
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, contactIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, strNumber) // Number to be added
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build()); //Type like HOME, MOBILE etc
        try {
            // We will do batch operation to insert all above data
            //Contains the output of the app of a ContentProviderOperation.
            //It is sure to have exactly one of uri or count set
            ContentProviderResult[] contentProresult = null;
            contentProresult = cntx.getContentResolver().applyBatch(ContactsContract.AUTHORITY, cntProOper); //apply above data insertion into contacts list

            DialogUtility.showDialog(cntx, "Add Contact", displayName + " is successfully added in your phone address book.", null, true);

        } catch (RemoteException exp) {
            //logs;
        } catch (OperationApplicationException exp) {
            //logs
        }
    }


    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.e("LOOK", imageEncoded);
        return imageEncoded;
    }

    public static Bitmap decodeBase64(String input) {
        String temp = input.replace(" ","+");
//        String temp = input.replaceAll(" ", "\\+");

        byte[] decodedByte = Base64.decode(temp, Base64.DEFAULT);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }


    /*Get File Extension*/
    public static String getFileExtension(File file) {

        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else return "";
    }

    public static String getImageThumb(String localUri) {

        String thumbBase64 = "";
        try {
            Bitmap bitmap = ThumbnailUtils.extractThumbnail(
                    BitmapFactory.decodeFile(localUri),
                    300, 300);
            thumbBase64 = encodeTobase64(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return thumbBase64;
    }

    public static String getSDCardPathForUIL(String fileName) {
        String loadURL = "file://" + fileName;
        return loadURL;
    }

    /* Check for slow internet connectivity */
    public static boolean hasActiveInternetConnection(Context context) {
        String LOG_TAG = "Check for slow internet connectivity";

        if (checkInternetConn(context)) {

            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(2000);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error checking internet connection", e);
            }
        } else {
            Log.d(LOG_TAG, "No network available!");
        }
        return false;
    }


//    http://www.goyalsbit.com/5-code-samples/how-to-check-if-string-contains-only-numeric-digits/

    //This method would apply two checks on the received string :
    //1: It should contain all numeric values only
    //2. Number of numerics should be exact 7
    public static boolean areAllNumericWayOne(String id) {
        Pattern p = Pattern.compile("\\d{7}");
        Matcher m = p.matcher(id);
        boolean b = m.matches();
        return b;
    }

    //This method would apply two checks on the received string :
    //1: It should contain all numeric values only
    //2. Number of numerics can be one or more
    public static boolean areAllNumericWayTwo(String id) {
        Pattern p = Pattern.compile("[0-9]+");
        Matcher m = p.matcher(id);
        boolean b = m.matches();
        return b;
    }

    //This method would apply two checks on the string & is an alternate approach to the above one:
    //1: It should contain all numeric values only
    //2. Number of numerics can be one or more
    public static boolean areAllNumericWayThree(String id) {
        String regex = "[0-9]+";
        boolean b = id.matches(regex);
        return b;
    }

//    System.out.println("1234567 contains 7 digits only : " + areAllNumericWayOne("1234567"));
//    1234567 contains 7 digits only : true

//    System.out.println("12345678 contains 7 digits only : " + areAllNumericWayOne("12345678"));
//    12345678 contains 7 digits only : false

//    System.out.println("1 contains Numeric only : " + areAllNumericWayTwo("1"));
//    1 contains Numeric only : true

//    System.out.println("123456 contains Numeric only : " + areAllNumericWayTwo("123456"));
//    123456 contains Numeric only : true

//    System.out.println("12345678 contains Numeric only : " + areAllNumericWayThree("12345678"));
//    12345678 contains Numeric only : true

//    System.out.println("123456789 contains Numeric only : " + areAllNumericWayThree("123456789"));
//    123456789 contains Numeric only : true

//    System.out.println("ABC123 contains Numeric digits only : " + areAllNumericWayOne("ABC123"));
//    ABC123 contains Numeric digits only : false

//    System.out.println("ABC123 contains Numeric digits only : " + areAllNumericWayTwo("ABC123"));
//    ABC123 contains Numeric digits only : false

//    System.out.println("ABC123 contains Numeric digits only: " + areAllNumericWayThree("ABC123"));
//    ABC123 contains Numeric digits only: false


    public static void expand(final View v) {
        v.measure(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? RelativeLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }


    public static String getAudioTitleUsingPath(Context ctx, String filePath) {

        MediaMetadataRetriever mediaMetadataRetriever = (MediaMetadataRetriever) new MediaMetadataRetriever();
        Uri uri = (Uri) Uri.fromFile(new File(filePath));
        mediaMetadataRetriever.setDataSource(ctx, uri);
        String audioTitle = (String) mediaMetadataRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

        if(audioTitle == null) {
            audioTitle = "Recording";
        }

        return audioTitle;
    }


    public static boolean isMessageBelongsToChat(String freind_ID) {
        boolean isMessageBelongsToChat = false;

        if(freind_ID.contains(PreferenceConstants.SERVERATTHERSTE)) {
            isMessageBelongsToChat = true;
        }

        return isMessageBelongsToChat;
    }
}
