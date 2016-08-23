package com.csipsimple.f5chat.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.csipsimple.f5chat.bean.ChatBody;
import com.csipsimple.f5chat.bean.Contact;
import com.csipsimple.f5chat.bean.FriendPresence;
import com.csipsimple.f5chat.bean.RecentChat;
import com.csipsimple.f5chat.utility.PreferenceConstants;
import com.csipsimple.f5chat.utility.SharedPrefrence;
import com.csipsimple.f5chat.utility.Utils;

import java.io.File;
import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "F5CHAT.db";

    // Contacts table name
    private static final String TABLE_CHAT = "chat";

    // Contacts table name
    private static final String TABLE_OPEN_CONVERSATION = "allOpenConversation";

    // Contacts table name
    private static final String TABLE_CONTACT = "contact";

    // Contacts table name
    private static final String TABLE_GROUP_CHAT = "groupChat";

    // Contacts table name
    private static final String TABLE_FILES = "filesInfo";

    // Contacts table name
    private static final String TABLE_FRIEND_PRESENCE = "friendPresence";

    // Contacts table name
//    private static final String TABLE_CONTACT_SHARE = "contact_share";


    // Contacts Table Columns names
    //private static final String KEY_ID = "id";
    private static final String JID = "JID";
    private static final String IMAGE = "Image";
    private static final String NAME = "Name";
    private static final String NUMBER = "Number";
    private static final String TYPE = "Type";
    private static final String OWNER = "Owner";

    /* Contact Attributes */
    private static final String CONTACT_NAME = "ContactName";
    private static final String CONTACT_NUMBER = "ContactNumber";

    // Contacts Table Columns names
    //private static final String KEY_ID = "id";
    private static final String OWNER_JID = "MyJID";
    private static final String FRIEND_JID = "FriendJID";
    private static final String MESSAGE_ID = "MessageID";
    private static final String MESSAGE = "Message";
    private static final String MESSAGE_TYPE = "MessageType";
    private static final String IN_OUT_BOUND = "InOutBound";
    private static final String DATE = "DateParam";
    private static final String TIME_STAMP = "TimeStamp";
    private static final String PROGRESS = "Progress";
    private static final String STATUS = "Status";
    private static final String CATEGORY = "Category";

    private static final String GROUP_JID = "GroupJID";
    private static final String MESSAGE_FROM = "Msg_From";

    //User Presence Table
    private static final String PRESENCE_TYPE = "PresenceType";
    private static final String PRESENCE_MODE = "PresenceMode";

    // File Table
    private static final String FILE_URL = "Url";
    private static final String LOCAL_URI = "LocalUri";
    private static final String THUMB = "Thumb";
    private static final String FILE_EXTENSION = "Extension";


    SharedPrefrence share;
    Context ctx;

    public DatabaseHandler(Context context) {
        //  super(context, DATABASE_NAME, null, DATABASE_VERSION);
        super(context, makeDBFilePath(), null, DATABASE_VERSION);
        ctx = context;
        share = SharedPrefrence.getInstance(ctx);
    }

    public static String makeDBFilePath() {
        File file = new File(Environment.getExternalStorageDirectory()
                .getPath(), PreferenceConstants.rootDirectory + File.separator + PreferenceConstants.subRootDBDirectory);
        if (!file.exists()) {
            file.mkdirs();
        }

        String dbPath = (file.getPath() + File.separator + DATABASE_NAME);

        return dbPath;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACT + "("
                + JID + " TEXT,"
                + IMAGE + " TEXT,"
                + NAME + " TEXT,"
                + NUMBER + " TEXT,"
                + TYPE + " TEXT,"
                + OWNER + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);


        String CREATE_CHAT_TABLE = "CREATE TABLE " + TABLE_CHAT + "("
                + OWNER_JID + " TEXT,"
                + FRIEND_JID + " TEXT,"
                + MESSAGE_ID + " TEXT,"
                + MESSAGE + " TEXT,"
                + MESSAGE_TYPE + " TEXT,"
                + IN_OUT_BOUND + " TEXT,"
                + PROGRESS + " TEXT,"
                + STATUS + " TEXT,"
                + CATEGORY + " TEXT,"
                + TIME_STAMP + " TEXT,"
                + DATE + " DATETIME" + ")";
        db.execSQL(CREATE_CHAT_TABLE);


        String CREATE_OPEN_CONVERSATION = "CREATE TABLE " + TABLE_OPEN_CONVERSATION + "("
                + OWNER_JID + " TEXT,"
                + FRIEND_JID + " TEXT,"
                + NAME + " TEXT,"
                + MESSAGE + " TEXT,"
                + TIME_STAMP + " TEXT" + ")";
        db.execSQL(CREATE_OPEN_CONVERSATION);


        String CREATE_GROUP_CHAT_TABLE = "CREATE TABLE " + TABLE_GROUP_CHAT + "("
                + OWNER_JID + " TEXT,"
                + GROUP_JID + " TEXT,"
                + MESSAGE_ID + " TEXT,"
                + MESSAGE + " TEXT,"
                + MESSAGE_TYPE + " TEXT,"
                + MESSAGE_FROM + " TEXT,"
                + IN_OUT_BOUND + " TEXT,"
                + PROGRESS + " TEXT,"
                + STATUS + " TEXT,"
                + TIME_STAMP + " TEXT,"
                + DATE + " DATETIME" + ")";
        db.execSQL(CREATE_GROUP_CHAT_TABLE);

        String CREATE_FRIEND_PRESENCE = "CREATE TABLE " + TABLE_FRIEND_PRESENCE + "("
                + FRIEND_JID + " TEXT,"
                + PRESENCE_TYPE + " TEXT,"
                + PRESENCE_MODE + " TEXT" + ")";
        db.execSQL(CREATE_FRIEND_PRESENCE);


        /*Create File Table*/
        String CREATE_FILES_TABLE = "CREATE TABLE " + TABLE_FILES + "("
                + MESSAGE_ID + " TEXT,"
                + MESSAGE_TYPE + " TEXT,"
                + PROGRESS + " TEXT,"
                + FILE_URL + " TEXT,"
                + LOCAL_URI + " TEXT,"
                + FILE_EXTENSION + " TEXT,"
                + THUMB + " TEXT,"
                + STATUS + " TEXT,"
                + IN_OUT_BOUND + " TEXT" + ")";
        db.execSQL(CREATE_FILES_TABLE);


        /*Contact Share*/
//        String CONTACT_SHARE = "CREATE TABLE " + TABLE_CONTACT_SHARE + "("
//                + MESSAGE_ID + " TEXT,"
//                + OWNER_JID + " TEXT,"
//                + FRIEND_JID + " TEXT,"
//                + CONTACT_NAME + " TEXT,"
//                + CONTACT_NUMBER + " TEXT" + ")";
//        db.execSQL(CONTACT_SHARE);

    }//public void onCreate(SQLiteDatabase db)

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPEN_CONVERSATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP_CHAT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIEND_PRESENCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILES);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT_SHARE);
        onCreate(db);
    }//	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion

    public void addContactToDB(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        Log.e("contact.getName()", "" + contact.getName());
        values.put(JID, contact.getJID());
        values.put(IMAGE, contact.getImage());
        values.put(NAME, contact.getName());
        values.put(NUMBER, contact.getNumber());
        values.put(TYPE, contact.getType());
        values.put(OWNER, contact.getOwner());

        db.insert(TABLE_CONTACT, null, values);
        db.close(); // Closing database connection
    }

    public void deleteOldContent() {
        SQLiteDatabase db = this.getWritableDatabase();
        String delete = "DELETE FROM " + TABLE_CONTACT + "";
        db.execSQL(delete);
        db.close();
    }

    public String getF5ChatName(String JID) {

        String selectQuery = "SELECT  * FROM " + TABLE_CONTACT + " WHERE JID ='" + JID + "'";
        Log.e("selectQuery", "" + selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String name = "";
        // looping through all rows and adding to list

        // Log.e("cursor size",""+cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                name = cursor.getString(2);
                // Log.e("database handler name",""+name);
            } while (cursor.moveToNext());
        }
        if (name == null || name.length() == 0) {
            name = Utils.getNumberFromJID(JID);
        }
        cursor.close();
        db.close();
        return name;
    }

    public String getUnreadCount(String MyJID, String FriendJID) {

        String selectQuery = "SELECT  COUNT(*) FROM " + TABLE_CHAT + " WHERE " + OWNER_JID + " ='" + MyJID + "' AND " + FRIEND_JID + "='" + FriendJID + "' AND " + STATUS + "='unread'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String count = "";
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                count = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return count;
    }

    public String getTotalUnread() {

        String selectQuery = "SELECT  COUNT(*) FROM " + TABLE_CHAT + " WHERE " + STATUS + " ='unread'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String count = "";
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                count = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();

/*  GET UNREAD   */
        String sQuery = "SELECT  COUNT(*) FROM " + TABLE_CHAT + " WHERE " + STATUS + " ='unread'";
        Cursor cursr = db.rawQuery(sQuery, null);

        // looping through all rows and adding to list
        if (cursr.moveToFirst()) {
            do {
                count = count + cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursr.close();


        db.close();
        return count;
    }

    public String getOpenConversation_ofUnread() {

        SQLiteDatabase db = this.getWritableDatabase();
        int count = 0;

        try {
            //select * from chat where Status='unread' group by MyJID,FriendJID
            String selectQuery = "SELECT  COUNT(*) FROM " + TABLE_CHAT + " WHERE " + STATUS + " ='unread' GROUP BY " + OWNER_JID + " ," + FRIEND_JID;

            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    count = cursor.getCount();
                } while (cursor.moveToNext());
            }
            cursor.close();



        /* Unread For Group*/
            //select * from chat where Status='unread' group by MyJID,FriendJID
            String sQuery = "SELECT  COUNT(*) FROM " + TABLE_GROUP_CHAT + " WHERE " + STATUS + " ='unread' GROUP BY " + OWNER_JID + " ," + GROUP_JID;
            SQLiteDatabase dbase = this.getWritableDatabase();
            Cursor cursr = db.rawQuery(sQuery, null);
            // looping through all rows and adding to list
            if (cursr.moveToFirst()) {
//                do {
                    count = count + cursr.getCount();
//                } while (cursr.moveToNext());
            }
            cursr.close();

        }catch (Exception e) {
            e.printStackTrace();
        }


        db.close();
        return String.valueOf(count);
    }

    public ArrayList<ChatBody> getOpenConversationsForNotification() {

        //select * from chat where Status='unread' group by MyJID,FriendJID
        String selectQuery = "SELECT  * FROM " + TABLE_CHAT + " WHERE " + STATUS + " ='unread' GROUP BY " + OWNER_JID + " ," + FRIEND_JID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<ChatBody> chatBodies = new ArrayList<ChatBody>();

        // looping through all rows and adding to list
        if (cursor != null) {

            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {

                    do {
                        try {
                            ChatBody chat = new ChatBody();
                            chat.setOwnerJID(cursor.getString(cursor.getColumnIndex(OWNER_JID)));
                            chat.setFriendJID(cursor.getString(cursor.getColumnIndex(FRIEND_JID)));
                            chat.setMessageID(cursor.getString(cursor.getColumnIndex(MESSAGE_ID)));

                            String name = Utils.getContactNameByNumber(ctx, chat.getFriendJID());
                            chat.setMessage(name + " : " + cursor.getString(cursor.getColumnIndex(MESSAGE)));

                            chat.setMessageType(cursor.getString(cursor.getColumnIndex(MESSAGE_TYPE)));
                            chat.setInOutBound(cursor.getString(cursor.getColumnIndex(IN_OUT_BOUND)));
                            chat.setDate(cursor.getString(cursor.getColumnIndex(DATE)));
                            chat.setTimeStamp(cursor.getString(cursor.getColumnIndex(TIME_STAMP)));
                            chat.setProgress(cursor.getString(cursor.getColumnIndex(PROGRESS)));
                            chat.setStatus(cursor.getString(cursor.getColumnIndex(STATUS)));
                            chat.setCategory(cursor.getString(cursor.getColumnIndex(CATEGORY)));

                            chatBodies.add(chat);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } while (cursor.moveToNext());
                }
            } else {
                System.out.println("No new chats available");
            }
        }
        cursor.close();


        /* READ UNREAD GROUP MESSAGES  */
        //select * from chat where Status='unread' group by MyJID,FriendJID
        String sQuery = "SELECT  * FROM " + TABLE_GROUP_CHAT + " WHERE " + STATUS + " ='unread' GROUP BY " + OWNER_JID + " ," + GROUP_JID;

        Cursor cursr = db.rawQuery(sQuery, null);


        // looping through all rows and adding to list
        if (cursr != null) {

            if (cursr.getCount() > 0) {
                if (cursr.moveToFirst()) {

                    do {
                        try {

                            ChatBody chat = new ChatBody();
                            chat.setOwnerJID(cursr.getString(cursr.getColumnIndex(OWNER_JID)));
                            chat.setFriendJID(cursr.getString(cursr.getColumnIndex(GROUP_JID)));
                            chat.setMessageID(cursr.getString(cursr.getColumnIndex(MESSAGE_ID)));
                            chat.setMessage(cursr.getString(cursr.getColumnIndex(MESSAGE)));
                            chat.setMessageType(cursr.getString(cursr.getColumnIndex(MESSAGE_TYPE)));
                            chat.setMsgFrom(cursr.getString(cursr.getColumnIndex(MESSAGE_FROM)));
                            chat.setInOutBound(cursr.getString(cursr.getColumnIndex(IN_OUT_BOUND)));
                            chat.setDate(cursr.getString(cursr.getColumnIndex(DATE)));
                            chat.setTimeStamp(cursr.getString(cursr.getColumnIndex(TIME_STAMP)));
                            chat.setProgress(cursr.getString(cursr.getColumnIndex(PROGRESS)));
                            chat.setStatus(cursr.getString(cursr.getColumnIndex(STATUS)));

                            chatBodies.add(chat);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } while (cursr.moveToNext());
                }
            } else {
                System.out.println("No new chats available");
            }
        }
        cursr.close();




        db.close();

        return chatBodies;
    }




    public void updateUnreadCount(String MyJID, String FriendJID) {

        SQLiteDatabase db = this.getWritableDatabase();
        String delete = "UPDATE " + TABLE_CHAT + " SET " + STATUS + "='read' WHERE " + OWNER_JID + "='" + MyJID + "' AND " + FRIEND_JID + "='" + FriendJID + "'";
        db.execSQL(delete);
        db.close();
    }


    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    public void addChatMessageToDB(ChatBody chat) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(OWNER_JID, chat.getOwnerJID());
            values.put(FRIEND_JID, chat.getFriendJID());
            values.put(MESSAGE_ID, chat.getMessageID());
            values.put(MESSAGE, chat.getMessage());
            values.put(MESSAGE_TYPE, chat.getMessageType());
            values.put(IN_OUT_BOUND, chat.getInOutBound());
            values.put(DATE, chat.getDate());
            values.put(TIME_STAMP, chat.getTimeStamp());
            values.put(PROGRESS, chat.getProgress());
            values.put(STATUS, chat.getStatus());
            values.put(CATEGORY, chat.getCategory());

            addIntoOpenCon(chat, db);
            db.insert(TABLE_CHAT, null, values);
            db.close(); // Closing database connection
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    //========================================================================
    public void addIntoOpenCon(ChatBody chat, SQLiteDatabase db) {
        if (checkOpenConversation(chat.getFriendJID(), db)) {
            updateOpenCon(chat, db);
        } else {
            addEntryInOpenConversation(chat, db);
        }
    }

    public boolean checkOpenConversation(String friendJID, SQLiteDatabase db) {

        String selectQuery = "SELECT  * FROM " + TABLE_OPEN_CONVERSATION + " WHERE " + FRIEND_JID + " ='" + friendJID + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean check = false;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                check = true;
                break;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return check;
    }

    public void addEntryInOpenConversation(ChatBody chat, SQLiteDatabase db) {
        try {
            ContentValues values = new ContentValues();

            String name = chat.getContactName();
            String friendJID = chat.getFriendJID();
            if (name == null) {
                name = chat.getFriendJID();
            }

            values.put(NAME, name);
            values.put(OWNER_JID, chat.getOwnerJID());
            values.put(FRIEND_JID, friendJID);
            values.put(MESSAGE, chat.getMessage());
            values.put(TIME_STAMP, chat.getTimeStamp());
            db.insert(TABLE_OPEN_CONVERSATION, null, values);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void updateOpenCon(ChatBody chat, SQLiteDatabase db) {
        String delete = "UPDATE " + TABLE_OPEN_CONVERSATION + " SET " + OWNER_JID + "='" + chat.getOwnerJID() + "'," + FRIEND_JID + "='" + chat.getFriendJID() + "'," + MESSAGE + "='" + chat.getMessage() + "'," + TIME_STAMP + "='" + chat.getTimeStamp() + "'  WHERE " + OWNER_JID + "='" + chat.getOwnerJID() + "' AND " + FRIEND_JID + "='" + chat.getFriendJID() + "'";
        db.execSQL(delete);
    }


    public ArrayList<RecentChat> getOpenConLst() {
        ArrayList<RecentChat> recentLst = new ArrayList<RecentChat>();
        String selectQuery;
        selectQuery = "SELECT  * FROM " + TABLE_OPEN_CONVERSATION + "  GROUP BY " + OWNER_JID + " ," + FRIEND_JID + " ORDER BY " + TIME_STAMP + " DESC";


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {

                RecentChat recentChat = new RecentChat();

                String friendJID = getF5ChatName(cursor.getString(cursor.getColumnIndex(FRIEND_JID)));
//                String name = getF5ChatName(cursor.getString(1));
                recentChat.setName(getF5ChatName(cursor.getString(cursor.getColumnIndex(NAME))));
                recentChat.setFriendJID(cursor.getString(cursor.getColumnIndex(FRIEND_JID)));
                recentChat.setChatCount(getUnreadCount(share.getValue(SharedPrefrence.JID), friendJID));

                recentChat.setImageFirst("");
                recentChat.setImageSec("");
                recentChat.setImageThird("");
                recentChat.setImageFour("");
                recentChat.setLastMessage(cursor.getString(cursor.getColumnIndex(MESSAGE)));
                recentChat.setTime(Utils.getFormattedTime(Long.parseLong(cursor.getString(cursor.getColumnIndex(TIME_STAMP)))));
                // Adding contact to list
                recentLst.add(recentChat);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recentLst;
    }//	public List<Contact> getChatByGroup()

    //========================================================================
    public ArrayList<RecentChat> getOpnConForGroupAndPrivate() {
        ArrayList<RecentChat> recentLst = new ArrayList<RecentChat>();
        String selectQuery;
        selectQuery = "SELECT  * FROM " + TABLE_CHAT + " GROUP BY " + OWNER_JID + " ," + FRIEND_JID + " ORDER BY " + TIME_STAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                RecentChat recentChat = new RecentChat();

                String name = getF5ChatName(cursor.getString(1));

                recentChat.setName(getF5ChatName(cursor.getString(1)));
                recentChat.setFriendJID(cursor.getString(1));
                recentChat.setChatCount(getUnreadCount(share.getValue(SharedPrefrence.JID), cursor.getString(1)));

                recentChat.setImageFirst("");
                recentChat.setImageSec("");
                recentChat.setImageThird("");
                recentChat.setImageFour("");
                recentChat.setLastMessage(cursor.getString(3));
                recentChat.setTime(Utils.getFormattedTime(Long.parseLong(cursor.getString(9))));

                // Adding contact to list
                recentLst.add(recentChat);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recentLst;
    }

    // Getting All Contacts
    public ArrayList<ChatBody> getChat(String me, String you) {
        ArrayList<ChatBody> chatBodyLst = new ArrayList<ChatBody>();
        // Select All Query

        String selectQuery = "SELECT  * FROM " + TABLE_CHAT + " WHERE " + OWNER_JID + "='" + you + "' OR  " + FRIEND_JID + "='" + you + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ChatBody chatBody = new ChatBody();
                //contact.setID(Integer.parseInt(cursor.getString(0)));
                chatBody.setOwnerJID(cursor.getString(0));
                chatBody.setFriendJID(cursor.getString(1));
                chatBody.setMessageID(cursor.getString(2));
                chatBody.setMessage(cursor.getString(3));
                chatBody.setMessageType(cursor.getString(4));
                chatBody.setInOutBound(cursor.getString(5));
                chatBody.setProgress(cursor.getString(6));
                chatBody.setStatus(cursor.getString(7));
                chatBody.setCategory(cursor.getString(8));
                chatBody.setTimeStamp(cursor.getString(9));
                chatBody.setDate(cursor.getString(10));
                // Adding contact to list
                chatBodyLst.add(chatBody);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return chatBodyLst;
    }//List<Contact> getChat(String me,String you)

    //	// Getting All Contacts
    public ArrayList<RecentChat> getChatByGroup(String Type) {
        ArrayList<RecentChat> recentLst = new ArrayList<RecentChat>();
        String selectQuery;
        //PRIVATE//GROUP//ALL
        if (Type.equals("ALL")) {
            recentLst = getOpenConLst();
        } else {
            recentLst = getOpnConForGroupAndPrivate();
        }
        return recentLst;
    }//	public List<Contact> getChatByGroup()

    public ArrayList<RecentChat> getAllOpenConversation() {
        ArrayList<RecentChat> recentLst = new ArrayList<RecentChat>();
        String selectQuery;

        selectQuery = "SELECT  * FROM " + TABLE_OPEN_CONVERSATION + "  GROUP BY " + OWNER_JID + " ," + FRIEND_JID + " ORDER BY " + TIME_STAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                RecentChat recentChat = new RecentChat();

                String name = getF5ChatName(cursor.getString(1));

                recentChat.setName(getF5ChatName(cursor.getString(1)));
                recentChat.setFriendJID(cursor.getString(1));
                recentChat.setChatCount(getUnreadCount(share.getValue(SharedPrefrence.JID), cursor.getString(1)));

                recentChat.setImageFirst("");
                recentChat.setImageSec("");
                recentChat.setImageThird("");
                recentChat.setImageFour("");
                recentChat.setLastMessage(cursor.getString(3));
                recentChat.setTime(Utils.getFormattedTime(Long.parseLong(cursor.getString(9))));

                // Adding contact to list
                recentLst.add(recentChat);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recentLst;
    }

    //===============================================
    public void addChatToGroupTable(ChatBody chat) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(OWNER_JID, chat.getOwnerJID());
            values.put(GROUP_JID, chat.getFriendJID());
            values.put(MESSAGE_ID, chat.getMessageID());
            values.put(MESSAGE, chat.getMessage());
            values.put(MESSAGE_TYPE, chat.getMessageType());
            values.put(MESSAGE_FROM, chat.getMsgFrom());
            values.put(IN_OUT_BOUND, chat.getInOutBound());
            values.put(DATE, chat.getDate());
            values.put(TIME_STAMP, chat.getTimeStamp());
            values.put(PROGRESS, chat.getProgress());
            values.put(STATUS, chat.getStatus());

            addIntoOpenCon(chat, db);
            db.insert(TABLE_GROUP_CHAT, null, values);
            db.close(); // Closing database connection
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    // Getting All Contacts
    public ArrayList<ChatBody> getGroupChat(String me, String groupjid) {
        ArrayList<ChatBody> chatBodyLst = new ArrayList<ChatBody>();
        // Select All Query

        String selectQuery = "SELECT  * FROM " + TABLE_GROUP_CHAT + " WHERE " + OWNER_JID + "='" + me + "' AND  " + GROUP_JID + "='" + groupjid + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ChatBody chatBody = new ChatBody();
                chatBody.setOwnerJID(cursor.getString(cursor.getColumnIndex(OWNER_JID)));
                chatBody.setFriendJID(cursor.getString(cursor.getColumnIndex(GROUP_JID)));
                chatBody.setMessageID(cursor.getString(cursor.getColumnIndex(MESSAGE_ID)));
                chatBody.setMessage(cursor.getString(cursor.getColumnIndex(MESSAGE)));
                chatBody.setMessageType(cursor.getString(cursor.getColumnIndex(MESSAGE_TYPE)));
                chatBody.setMsgFrom(cursor.getString(cursor.getColumnIndex(MESSAGE_FROM)));
                chatBody.setInOutBound(cursor.getString(cursor.getColumnIndex(IN_OUT_BOUND)));
                chatBody.setDate(cursor.getString(cursor.getColumnIndex(DATE)));
                chatBody.setTimeStamp(cursor.getString(cursor.getColumnIndex(TIME_STAMP)));
                chatBody.setProgress(cursor.getString(cursor.getColumnIndex(PROGRESS)));
                chatBody.setStatus(cursor.getString(cursor.getColumnIndex(STATUS)));


                // Adding contact to list
                chatBodyLst.add(chatBody);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return chatBodyLst;
    }
    //===============================================

    /*

        User Presence Maintain

     */


    public void addIntoPresence(FriendPresence friendPresence) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (checkIntoPresence(friendPresence.getFriendJID(), db)) {
            updatePresence(friendPresence, db);
        } else {
            addPresence(friendPresence, db);
        }
        db.close();
    }

    public boolean checkIntoPresence(String friendJID, SQLiteDatabase db) {

        String selectQuery = "SELECT  * FROM " + TABLE_FRIEND_PRESENCE + " WHERE " + FRIEND_JID + " ='" + friendJID + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean check = false;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                check = true;
                break;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return check;
    }

    public void updatePresence(FriendPresence friendPresence, SQLiteDatabase db) {
        try {
            String delete = "UPDATE " + TABLE_FRIEND_PRESENCE + " SET " + PRESENCE_TYPE + "='" + friendPresence.getType() + "'," + PRESENCE_MODE + "='" + friendPresence.getMode() + "'  WHERE " + FRIEND_JID + "='" + friendPresence.getFriendJID() + "'";
            db.execSQL(delete);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addPresence(FriendPresence presence, SQLiteDatabase db) {

        ContentValues values = new ContentValues();

        values.put(FRIEND_JID, presence.getFriendJID());
        values.put(PRESENCE_TYPE, presence.getType());
        values.put(PRESENCE_MODE, presence.getMode());

        db.insert(TABLE_FRIEND_PRESENCE, null, values);
    }

    public FriendPresence getFriendPresence(String JID) {

        String selectQuery = "SELECT  * FROM " + TABLE_FRIEND_PRESENCE + " WHERE " + FRIEND_JID + "='" + JID + "'";
        Log.e("selectQuery", "" + selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        FriendPresence friendPresence = null;
        if (cursor.moveToFirst()) {
            do {
                String friendJID = cursor.getString(0);
                String type = cursor.getString(1);
                String mode = cursor.getString(2);

                friendPresence = new FriendPresence();
                friendPresence.setFriendJID(friendJID);
                friendPresence.setMode(mode);
                friendPresence.setType(type);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        if (friendPresence != null)
            return friendPresence;
        else
            return new FriendPresence();
    }

    //---------------------------------------------------------------------------------------------------------------------------
    // Deleting single contact
    public void deleteChatCloseConversation(String me, String you) {
        SQLiteDatabase db = this.getWritableDatabase();
        String delete = "DELETE FROM chat WHERE userNameMe='" + me + "' AND userNameYou='" + you + "'";
        db.execSQL(delete);
        db.close();
    }//deleteChatCloseConversation(String me,String you)




    /*
    *
    * Files Operational Methods
    *
    * */

    public void addFilesToDB(ChatBody fileInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(MESSAGE_ID, fileInfo.getMessageID());
        values.put(MESSAGE_TYPE, fileInfo.getMessageType());
        values.put(PROGRESS, fileInfo.getProgress());
        values.put(FILE_URL, fileInfo.getUrl());
        values.put(LOCAL_URI, fileInfo.getLocalUri());
        values.put(FILE_EXTENSION, fileInfo.getExtension());
        values.put(THUMB, fileInfo.getThumb());
        values.put(STATUS, fileInfo.getStatus());
        values.put(IN_OUT_BOUND, fileInfo.getInOutBound());

        db.insert(TABLE_FILES, null, values);
        db.close(); // Closing database connection
    }

    public ChatBody getFileInfo(String messageID) {
        String selectQuery;

        ChatBody filesInfo = new ChatBody();

        selectQuery = "SELECT  * FROM " + TABLE_FILES + "  WHERE " + MESSAGE_ID + "='" + messageID + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                filesInfo.setMessageID(cursor.getString(cursor.getColumnIndex(MESSAGE_ID)));
                filesInfo.setMessageType(cursor.getString(cursor.getColumnIndex(MESSAGE_TYPE)));
                filesInfo.setProgress(cursor.getString(cursor.getColumnIndex(PROGRESS)));
                filesInfo.setUrl(cursor.getString(cursor.getColumnIndex(FILE_URL)));
                filesInfo.setLocalUri(cursor.getString(cursor.getColumnIndex(LOCAL_URI)));
                filesInfo.setExtension(cursor.getString(cursor.getColumnIndex(FILE_EXTENSION)));
                filesInfo.setThumb(cursor.getString(cursor.getColumnIndex(THUMB)));
                filesInfo.setStatus(cursor.getString(cursor.getColumnIndex(STATUS)));
                filesInfo.setInOutBound(cursor.getString(cursor.getColumnIndex(IN_OUT_BOUND)));

                // Adding contact to list
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return filesInfo;
    }

    public void updateFileInfo(ChatBody filesInfo) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String update = "UPDATE " + TABLE_FILES + " SET "
                    + PROGRESS + "='" + filesInfo.getProgress() +
                    "'," + FILE_URL + "='" + filesInfo.getUrl() +
                    "'," + LOCAL_URI + "='" + filesInfo.getLocalUri() +
                    "'," + STATUS + "='" + filesInfo.getStatus() +
                    "'  WHERE " + MESSAGE_ID + "='" + filesInfo.getMessageID() + "'";
            db.execSQL(update);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* End Files Operational Methods*/








    /* Contact Sharing  */


    /*
    *
    * Files Operational Methods
    *
    * */

//    public void addContactToDB(ChatBody body) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//
//        values.put(MESSAGE_ID, body.getMessageID());
//        values.put(OWNER_JID, body.getOwnerJID());
//        values.put(FRIEND_JID, body.getFriendJID());
//        values.put(CONTACT_NAME, body.getContactName());
//        values.put(CONTACT_NUMBER, body.getContactNumber());
//
//        db.insert(TABLE_CONTACT_SHARE, null, values);
//        db.close(); // Closing database connection
//    }
//
//    public ChatBody getContactInfo(String messageID) {
//        String selectQuery;
//
//        ChatBody body = new ChatBody();
//
//        selectQuery = "SELECT  * FROM " + TABLE_CONTACT_SHARE + "  WHERE " + MESSAGE_ID + "='" + messageID + "'";
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//        if (cursor.moveToFirst()) {
//            do {
//                body.setMessageID(cursor.getString(cursor.getColumnIndex(MESSAGE_ID)));
//                body.setOwnerJID(cursor.getString(cursor.getColumnIndex(OWNER_JID)));
//                body.setFriendJID(cursor.getString(cursor.getColumnIndex(FRIEND_JID)));
//                body.setContactName(cursor.getString(cursor.getColumnIndex(CONTACT_NAME)));
//                body.setContactNumber(cursor.getLong(cursor.getColumnIndex(CONTACT_NUMBER)));
//
//                // Adding contact to list
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        db.close();
//
//        return body;
//    }

    /* Contact Sharing*/

}
