package com.csipsimple.f5chat.rooster;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.csipsimple.R;
import com.csipsimple.f5chat.background.UpdateGroupInfo;
import com.csipsimple.f5chat.bean.ChatBody;
import com.csipsimple.f5chat.bean.FriendPresence;
import com.csipsimple.f5chat.bean.Group;
import com.csipsimple.f5chat.bean.GroupNotify;
import com.csipsimple.f5chat.database.DatabaseHandler;
import com.csipsimple.f5chat.fragments.ChatWindow;
import com.csipsimple.f5chat.group.GroupChatWindow;
import com.csipsimple.f5chat.utility.DialogUtility;
import com.csipsimple.f5chat.utility.PreferenceConstants;
import com.csipsimple.f5chat.utility.SharedPrefrence;
import com.csipsimple.f5chat.utility.Utils;
import com.csipsimple.ui.TabsViewPagerFragmentActivity;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ChatMessageListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.address.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.commands.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.delay.provider.DelayInformationProvider;
import org.jivesoftware.smackx.disco.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.disco.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.iqlast.packet.LastActivity;
import org.jivesoftware.smackx.iqprivate.PrivateDataManager;
import org.jivesoftware.smackx.muc.packet.GroupChatInvitation;
import org.jivesoftware.smackx.muc.provider.MUCAdminProvider;
import org.jivesoftware.smackx.muc.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.muc.provider.MUCUserProvider;
import org.jivesoftware.smackx.offline.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.offline.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.privacy.provider.PrivacyProvider;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;
import org.jivesoftware.smackx.pubsub.provider.SubscriptionProvider;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.sharedgroups.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.si.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;
import org.jivesoftware.smackx.xdata.provider.DataFormProvider;
import org.jivesoftware.smackx.xhtmlim.provider.XHTMLExtensionProvider;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

//import com.csipsimple.f5chat.CustomViewIconTextTabsActivity;

//import android.support.v7.app.NotificationCompat;

/**
 * Created by gakwaya on 4/28/2016.
 */
public class XmppConnect implements ConnectionListener {

    private static final String TAG = "XmppConnect";

    int totalUnreadCount = 0, totalUnreadConversationCount = 0;
    String msg;
    int NOTIFICATION_ID = 15232;

    public Context mApplicationContext;
    private String mUsername;
    private String mPassword;
    private String mServiceName;
    private XMPPTCPConnection mConnection;
    SharedPrefrence share;
    DatabaseHandler db;
    //static ArrayList<CallBack> callBackLst = new ArrayList<CallBack>();
    private BroadcastReceiver uiThreadMessageReceiver;//Receives messages from the ui thread.

    private static XmppConnect xmppConnect = new XmppConnect();

    public static enum ConnectionState {
        CONNECTED, AUTHENTICATED, CONNECTING, DISCONNECTING, DISCONNECTED;
    }

    public static enum LoggedInState {
        LOGGED_IN, LOGGED_OUT;
    }

//    public static XmppConnect getInstance(Context context) {
//        if (xmppConnect == null) {
//            callBackLst.clear();
//            return new XmppConnect(context);
//        }
//        return xmppConnect;
//    }

//    public void addCallBack(CallBack callBack) {
//        callBackLst.add(callBack);
//    }

    private XmppConnect() {
    }

    public static XmppConnect getInstance() {
        return xmppConnect;
    }


    public void initObject(Context context) {
        try {
            DialogUtility.showLOG("<-- XmppConnect Constructor called.");
            mApplicationContext = context.getApplicationContext();
            share = SharedPrefrence.getInstance(context);
            db = new DatabaseHandler(context);
            String jid = share.getValue(SharedPrefrence.JID);
            mPassword = share.getValue(SharedPrefrence.PASSWORD);

            if (jid != null && !jid.equalsIgnoreCase("")) {
                if (jid.trim().length() > 0 && jid.contains("@"))
                    mUsername = jid.split("@")[0];
                mServiceName = jid.split("@")[1];
            } else {
                mUsername = "";
                mServiceName = "";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void connect() throws IOException, XMPPException, SmackException {
        DialogUtility.showLOG("Connecting to server " + mServiceName);
//        XMPPTCPConnectionConfiguration.XMPPTCPConnectionConfigurationBuilder builder=
//                XMPPTCPConnectionConfiguration.builder();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SmackConfiguration.DEBUG_ENABLED = true;
        XMPPTCPConnectionConfiguration.XMPPTCPConnectionConfigurationBuilder configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setUsernameAndPassword(mUsername, mPassword);
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        configBuilder.setResource("test");
        configBuilder.setServiceName("85.214.17.140");
        configBuilder.setHost("85.214.17.140");
        configBuilder.setPort(5858);
        configBuilder.setSendPresence(true);
        configBuilder.setDebuggerEnabled(true);

        //Set up the ui thread broadcast message receiver.
        setupUiThreadBroadCastMessageReceiver();

        mConnection = new XMPPTCPConnection(configBuilder.build());
        mConnection.addConnectionListener(this);


        mConnection.connect();
        setmConnection(mConnection);

        ChatManager.getInstanceFor(mConnection).addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                chat.addMessageListener(new ChatMessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {
                        if (message.getBody() != null) {
                            receiveMessage(chat, message);
//                            for (int i = 0; i < callBackLst.size(); i++)
//                                callBackLst.get(i).getMessage();
                        }
                    }
                });
            }
        });

        mConnection.getRoster().addRosterListener(new RosterListener() {

            @Override
            public void presenceChanged(Presence presence) {
                System.out.println("<-- change presence from " + presence.getFrom());
                changePresence(presence);
            }

            @Override
            public void entriesUpdated(Collection<String> arg0) {
                System.out.println("<-- change");
            }

            @Override
            public void entriesDeleted(Collection<String> arg0) {
                System.out.println("<-- change");
            }

            @Override
            public void entriesAdded(Collection<String> arg0) {
                System.out.println("<-- change");
            }
        });


//        PacketFilter filter = MessageTypeFilter.CHAT;
//        mConnection.addPacketListener(new PacketListener() {
//            @Override
//            public void processPacket(Packet packet) throws SmackException.NotConnectedException {
//
//                Message message = (Message) packet;
//                System.out.println("<-- packet");
//               if(message.getBody() != null)
//               {
//                   Log.d(TAG, "message.getBody() :" + message.getBody());
//                   Log.d(TAG, "message.getFrom() :" + message.getFrom());
//
//                   String from = message.getFrom();
//                   String contactJid = "";
//                   if (from.contains("/")) {
//                       contactJid = from.split("/")[0];
//                       Log.d(TAG, "The real jid is :" + contactJid);
//                   } else {
//                       contactJid = from;
//                   }
//
//                   //Bundle up the intent and send the broadcast.
//                   Intent intent = new Intent(RoosterConnectionService.NEW_MESSAGE);
//                   intent.setPackage(mApplicationContext.getPackageName());
//                   intent.putExtra(RoosterConnectionService.BUNDLE_FROM_JID, contactJid);
//                   intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY, message.getBody());
//                   mApplicationContext.sendBroadcast(intent);
//                   Log.d(TAG, "Received message from :" + contactJid + " broadcast sent.");
//               }
//
//
//            }
//        }, filter);


        mConnection.login();


        // receiveMessage ( mUsername);
        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(mConnection);
        reconnectionManager.setEnabledPerDefault(true);
        reconnectionManager.enableAutomaticReconnection();

    }

    public void receiveMessage(Chat chat, Message message) {
        System.out.println("<-- message");
        try {
            if (message.getBody() != null) {
                DialogUtility.showLOG("message.getBody() :" + message.getBody());
                DialogUtility.showLOG("message.getFrom() :" + message.getFrom());
                DialogUtility.showLOG("message.getSubject() :" + message.getSubject());

                if (message.getSubject().equals(PreferenceConstants.TEXT)
                        || message.getSubject().equals(PreferenceConstants.IMAGE)
                        || message.getSubject().equals(PreferenceConstants.VIDEO)
                        || message.getSubject().equals(PreferenceConstants.AUDIO)
                        || message.getSubject().equals(PreferenceConstants.CONTACT)) {

                    receiveSingleTxtMsg(message);

                } else if (message.getSubject().equals(PreferenceConstants.G_TEXT)
                        || message.getSubject().equals(PreferenceConstants.G_IMAGE)
                        || message.getSubject().equals(PreferenceConstants.G_VIDEO)
                        || message.getSubject().equals(PreferenceConstants.G_AUDIO)
                        || message.getSubject().equals(PreferenceConstants.G_CONTACT)) {

                    receiveGroupTxtMsg(message);

                } else if (message.getSubject().equals(PreferenceConstants.GN_CREATE_GROUP)) {
                    receiveGroupNotifyForGroupCreate(message);

                } else if (message.getSubject().equals(PreferenceConstants.GN_ADD_TO_GROUP)) {
                    receiveGroupNotifyForAddParticipant(message);

                } else if (message.getSubject().equals(PreferenceConstants.GN_UPDATE_GROUP)) {
                    receiveGroupNotifyForGroupUpdate(message);

                } else if (message.getSubject().equals(PreferenceConstants.GN_DELETE_FROM_GROUP)) {
                    receiveGroupNotifyForDeleteParticipant(message);

                }

            }

        } catch (Exception rx) {
            rx.printStackTrace();
        }

    }

    public void receiveGroupNotifyForGroupCreate(Message message) {

        String from = message.getFrom();
        String contactJid = "";
        if (from.contains("/")) {
            contactJid = from.split("/")[0];
            DialogUtility.showLOG("The real jid is :" + contactJid);
        } else {
            contactJid = from;
        }

        if (contactJid.equals(share.getValue(SharedPrefrence.JID))) {
            return;
        }

        String msgBody = "";
        try {
            msgBody = Utils.utf8UrlDecoding(message.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }


        String displayMessage = createGroupEntry(msgBody, message.getSubject());
        ChatBody chatBody = Utils.getChatBodyForChatType(displayMessage, Utils.getMyJID(mApplicationContext), contactJid,
                PreferenceConstants.GN_CREATE_GROUP, PreferenceConstants.IN_BOUND, PreferenceConstants.UNREAD, message, PreferenceConstants.MESSAGE_TYPE_CHAT);

        //Bundle up the intent and send the broadcast.
        Intent intent = new Intent(RoosterConnectionService.NEW_GROUP_MESSAGE);
        intent.setPackage(mApplicationContext.getPackageName());
        intent.putExtra("CHAT", chatBody);
        intent.putExtra(RoosterConnectionService.BUNDLE_FROM_JID, contactJid);
        intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY, displayMessage);
        mApplicationContext.sendBroadcast(intent);
        DialogUtility.showLOG("<-- receiveGroupNotifyForGroupCreate :" + contactJid + " broadcast sent.");
    }

    public String createGroupEntry(String msgBody, String subject) {

        String groupJID = Utils.getStringFromIndex(msgBody, 0);
        String groupName = Utils.getStringFromIndex(msgBody, 1);
        String groupImageUrl = Utils.getStringFromIndex(msgBody, 2);
        String groupAdmin = Utils.getStringFromIndex(msgBody, 3);

        Group group = new Group();
        group.setGroup_name(groupName);
        group.setGroup_jid(Utils.formatGroupJID(groupJID));
        group.setAdmin_jid(groupAdmin);
        group.setGroup_image_url(groupImageUrl);

        HashMap<String, Group> lst = share.getGroup(SharedPrefrence.GROUP_LST);
        lst.put(Utils.formatGroupJID(groupJID), group);
        share.setGroupLst(SharedPrefrence.GROUP_LST, lst);

        return "New group created by " + groupAdmin;
    }


    public void receiveSingleTxtMsg(Message message) {
        String from = message.getFrom();
        String contactJid = "";
        if (from.contains("/")) {
            contactJid = from.split("/")[0];
            DialogUtility.showLOG("The real jid is :" + contactJid);
        } else {
            contactJid = from;
        }

        String msg = message.getBody();
        try {
            msg = Utils.utf8UrlDecoding(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String type = message.getSubject();
        String msgType = "";
        if (type.equals(PreferenceConstants.IMAGE)) {
            msgType = "Image";
        } else if (type.equals(PreferenceConstants.CONTACT)) {
            msgType = msg;
        } else if (type.equals(PreferenceConstants.AUDIO)) {
            msgType = "Audio";
        } else if (type.equals(PreferenceConstants.TEXT)) {
            msgType = msg;
        }

        ChatBody chatBody = Utils.getChatBodyForChatType(msgType, Utils.getMyJID(mApplicationContext), contactJid,
                message.getSubject(), PreferenceConstants.IN_BOUND, PreferenceConstants.UNREAD, message, PreferenceConstants.MESSAGE_TYPE_CHAT);

        db.addChatMessageToDB(chatBody);

        if (type.equals(PreferenceConstants.IMAGE)) {

            chatBody.setUrl(Utils.getStringFromIndex(msg, 0));
            chatBody.setThumb(Utils.getStringFromIndex(msg, 1));
            chatBody.setMessage(msgType);
            db.addFilesToDB(chatBody);

        } else if (type.equals(PreferenceConstants.VIDEO)) {
            chatBody.setUrl(Utils.getStringFromIndex(msg, 0));
            chatBody.setThumb(Utils.getStringFromIndex(msg, 1));

        } else if (type.equals(PreferenceConstants.AUDIO)) {
            chatBody.setUrl(Utils.getStringFromIndex(msg, 0));
            chatBody.setAudio_title(Utils.getStringFromIndex(msg, 1));
            chatBody.setMessage(msgType);
            db.addFilesToDB(chatBody);

        } else if (type.equals(PreferenceConstants.CONTACT)) {
            chatBody.setcName(Utils.getStringFromIndex(msg, 1));
            chatBody.setcNumber(Long.parseLong(Utils.getStringFromIndex(msg, 2)));
        }

//        addNotification("", msg);

        addNotification(chatBody);

        //Bundle up the intent and send the broadcast.
        Intent intent = new Intent(RoosterConnectionService.NEW_MESSAGE);
        intent.setPackage(mApplicationContext.getPackageName());
        intent.putExtra("CHAT", chatBody);
        intent.putExtra(RoosterConnectionService.BUNDLE_FROM_JID, contactJid);
        intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY, message.getBody());
        mApplicationContext.sendBroadcast(intent);
        DialogUtility.showLOG("Received message from :" + contactJid + " broadcast sent.");
    }

    public void receiveGroupTxtMsg(Message message) {
        String from = message.getFrom();


        String contactJid = "";
        String groupJID = "";
        String readableMsg = "";
        if (from.contains("/")) {
            contactJid = from.split("/")[0];
            DialogUtility.showLOG("The real jid is :" + contactJid);
        } else {
            contactJid = from;
        }

        if (contactJid.equals(share.getValue(SharedPrefrence.JID))) {
            return;
        }

        String msg = message.getBody();
        try {
            msg = URLDecoder.decode(msg, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }


        String type = message.getSubject();
        String msgType = "";

        if (type.equals(PreferenceConstants.G_IMAGE)) {
            msgType = "Image";
            groupJID = Utils.getStringFromIndex(msg, 2);

        } else if (type.equals(PreferenceConstants.G_CONTACT)) {
            msgType = msg;
            groupJID = Utils.getStringFromIndex(msg, 3);

        } else if (type.equals(PreferenceConstants.G_AUDIO)) {
            msgType = "Audio";
            groupJID = Utils.getStringFromIndex(msg, 2);

        } else if (type.equals(PreferenceConstants.G_TEXT)) {
            readableMsg = Utils.getStringFromIndex(msg, 0);
            groupJID = Utils.getStringFromIndex(msg, 1);

            msgType = readableMsg;
        }

        ChatBody chatBody = Utils.getChatBodyForChatType(msgType, Utils.getMyJID(mApplicationContext), groupJID,
                message.getSubject(), PreferenceConstants.IN_BOUND, PreferenceConstants.UNREAD, message,
                PreferenceConstants.MESSAGE_TYPE_GROUP_CHAT, contactJid);

        String groupName = getMyGroup(Utils.formatGroupJID(groupJID)).getGroup_name();
        chatBody.setContactName(groupName);

        db.addChatToGroupTable(chatBody);

        if (type.equals(PreferenceConstants.G_IMAGE)) {
            chatBody.setUrl(Utils.getStringFromIndex(msg, 0));
            chatBody.setThumb(Utils.getStringFromIndex(msg, 1));
            chatBody.setMessage(msgType);
            db.addFilesToDB(chatBody);

        } else if (type.equals(PreferenceConstants.G_VIDEO)) {
            chatBody.setUrl(Utils.getStringFromIndex(msg, 0));
            chatBody.setThumb(Utils.getStringFromIndex(msg, 1));

        } else if (type.equals(PreferenceConstants.G_AUDIO)) {
            chatBody.setUrl(Utils.getStringFromIndex(msg, 0));
            chatBody.setAudio_title(Utils.getStringFromIndex(msg, 1));
            chatBody.setMessage(msgType);
            db.addFilesToDB(chatBody);

        } else if (type.equals(PreferenceConstants.G_CONTACT)) {
            chatBody.setcName(Utils.getStringFromIndex(msg, 1));
            chatBody.setcNumber(Long.parseLong(Utils.getStringFromIndex(msg, 2)));
        }

//        addNotification("", msgType);

        addNotification(chatBody);

        //Bundle up the intent and send the broadcast.
        Intent intent = new Intent(RoosterConnectionService.NEW_GROUP_MESSAGE);
        intent.setPackage(mApplicationContext.getPackageName());
        intent.putExtra("CHAT", chatBody);
        intent.putExtra(RoosterConnectionService.BUNDLE_FROM_JID, groupJID);
        intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY, readableMsg);
        mApplicationContext.sendBroadcast(intent);
        DialogUtility.showLOG("Received message from :" + contactJid + " broadcast sent.");
    }

    public static Group getMyGroup(String JID) {
        SharedPrefrence share = SharedPrefrence.getInstance(getInstance().mApplicationContext);
        HashMap<String, Group> groupHashMap = share.getGroup(SharedPrefrence.GROUP_LST);
        Group group = groupHashMap.get(JID);
        return group;
    }

    /*

    Delete participant from group

     */

    public void receiveGroupNotifyForDeleteParticipant(Message message) {

        String from = message.getFrom();
        String contactJid = "";
        if (from.contains("/")) {
            contactJid = from.split("/")[0];
            DialogUtility.showLOG("The real jid is :" + contactJid);
        } else {
            contactJid = from;
        }
        if (contactJid.equals(share.getValue(SharedPrefrence.JID))) {
            return;
        }

        String msgBody = "";
        try {
            msgBody = Utils.utf8UrlDecoding(message.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String groupJID = Utils.getStringFromIndex(msgBody, 0);
        if (Utils.checkInternetConn(mApplicationContext)) {
            {
                try {
                    new UpdateGroupInfo(mApplicationContext, groupJID).execute(PreferenceConstants.API_GET_GROUP_DETAIL, PreferenceConstants.GET_GROUP_DETAIL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Utils.showAlertDialog(this, getString(R.string.alert), getString(R.string.checknet));
        }


//
//        String displayMessage = updateGroupForDeleteParticipant(msgBody, message.getSubject());
//        ChatBody chatBody = Utils.getChatBodyForChatType(displayMessage, Utils.getMyJID(mApplicationContext), contactJid,
//                PreferenceConstants.GN_CREATE_GROUP, PreferenceConstants.IN_BOUND, PreferenceConstants.UNREAD, message, PreferenceConstants.MESSAGE_TYPE_CHAT);
//
//        //Bundle up the intent and send the broadcast.
//        Intent intent = new Intent(RoosterConnectionService.NEW_GROUP_MESSAGE);
//        intent.setPackage(mApplicationContext.getPackageName());
//        intent.putExtra("CHAT", chatBody);
//        intent.putExtra(RoosterConnectionService.BUNDLE_FROM_JID, contactJid);
//        intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY, displayMessage);
//        mApplicationContext.sendBroadcast(intent);
//
//
//        //Bundle up the intent and send the broadcast.
//        Intent groupUpdate = new Intent(RoosterConnectionService.GROUP_UPDATE);
//        mApplicationContext.sendBroadcast(groupUpdate);
//        DialogUtility.showLOG("<-- receiveGroupNotifyForGroupCreate :" + contactJid + " broadcast sent.");
    }

//    public String updateGroupForDeleteParticipant(String msgBody, String subject) {
//
//        String groupJID = Utils.getStringFromIndex(msgBody, 0);
//        String deleteMemberJID = Utils.getStringFromIndex(msgBody, 1);
//        String groupAdmin = Utils.getStringFromIndex(msgBody, 2);
//
//        HashMap<String, Group> lst = share.getGroup(SharedPrefrence.GROUP_LST);
//
//        //Group group = lst.get(groupJID);
//       // Group group = Utils.getGroupObj(Utils.formatGroupJID(groupJID), lst);
//        Group group = null;
//        try {
//            group = lst.get(Utils.formatGroupJID(groupJID));
//        } catch (Exception ex) {
//            group = new Group();
//        }
//        if (group == null) {
//            group = new Group();
//        }
//
//        ArrayList<GroupMember> member = group.getGroupMember();
//
//        for (int i = 0; i < member.size(); i++) {
//            if(member.get(i).getMember_jid().equals(deleteMemberJID))
//            {
//                member.remove(i);
//                break;
//            }
//        }
//        group.setGroupMember(member);
//        group.setAdmin_jid(groupAdmin);
//        group.setGroup_jid(groupJID);
//
//        lst.put(Utils.formatGroupJID(groupJID), group);
//        share.setGroupLst(SharedPrefrence.GROUP_LST, lst);
//
//        return "Add Group Members " + groupJID;
//    }


    /*

    Add Group Participant in to goup

     */


    public void receiveGroupNotifyForAddParticipant(Message message) {

        String from = message.getFrom();
        String contactJid = "";
        if (from.contains("/")) {
            contactJid = from.split("/")[0];
            DialogUtility.showLOG("The real jid is :" + contactJid);
        } else {
            contactJid = from;
        }
        if (contactJid.equals(share.getValue(SharedPrefrence.JID))) {
            return;
        }
        String msgBody = "";
        try {
            msgBody = Utils.utf8UrlDecoding(message.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String groupJID = Utils.getStringFromIndex(msgBody, 0);
        if (Utils.checkInternetConn(mApplicationContext)) {
            {
                try {
                    new UpdateGroupInfo(mApplicationContext, groupJID).execute(PreferenceConstants.API_GET_GROUP_DETAIL, PreferenceConstants.GET_GROUP_DETAIL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Utils.showAlertDialog(this, getString(R.string.alert), getString(R.string.checknet));
        }


//        String displayMessage = updateGroupParticipant(msgBody, message.getSubject());
//        ChatBody chatBody = Utils.getChatBodyForChatType(displayMessage, Utils.getMyJID(mApplicationContext), contactJid,
//                PreferenceConstants.GN_CREATE_GROUP, PreferenceConstants.IN_BOUND, PreferenceConstants.UNREAD, message, PreferenceConstants.MESSAGE_TYPE_CHAT);
//
//        //Bundle up the intent and send the broadcast.
//        Intent intent = new Intent(RoosterConnectionService.NEW_GROUP_MESSAGE);
//        intent.setPackage(mApplicationContext.getPackageName());
//        intent.putExtra("CHAT", chatBody);
//        intent.putExtra(RoosterConnectionService.BUNDLE_FROM_JID, contactJid);
//        intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY, displayMessage);
//        mApplicationContext.sendBroadcast(intent);
//
//
//        //Bundle up the intent and send the broadcast.
//        Intent groupUpdate = new Intent(RoosterConnectionService.GROUP_UPDATE);
//        mApplicationContext.sendBroadcast(groupUpdate);
        //       DialogUtility.showLOG("<-- receiveGroupNotifyForGroupCreate :" + contactJid + " broadcast sent.");
    }

//    public String updateGroupParticipant(String msgBody, String subject) {
//
//        String groupJID = Utils.getStringFromIndex(msgBody, 0);
//        String newMemberJID = Utils.getStringFromIndex(msgBody, 1);
//        String groupAdmin = Utils.getStringFromIndex(msgBody, 2);
//
//        String groupName = Utils.getStringFromIndex(msgBody, 3);
//        String groupImage = Utils.getStringFromIndex(msgBody, 4);
//
//        String newMembers[] = Utils.getArrayFromString(newMemberJID, PreferenceConstants.COMMA_SEPARATOR);
//        HashMap<String, Group> lst = share.getGroup(SharedPrefrence.GROUP_LST);
//
//        //Group group = lst.get(groupJID);
//        //Group group = Utils.getGroupObj(Utils.formatGroupJID(groupJID), lst);
//        Group group = null;
//        try {
//            group = lst.get(Utils.formatGroupJID(groupJID));
//        } catch (Exception ex) {
//            group = new Group();
//        }
//        if (group == null) {
//            group = new Group();
//        }
//
//
//        ArrayList<GroupMember> member = group.getGroupMember();
//
//        for (int i = 0; i < newMembers.length; i++) {
//            GroupMember members = new GroupMember();
//            members.setMember_jid(newMembers[i]);
//            member.add(members);
//        }
//        group.setGroupMember(member);
//        group.setAdmin_jid(groupAdmin);
//        group.setGroup_jid(groupJID);
//        group.setGroup_name(groupName);
//        group.setGroup_image_url(groupImage);
//
//        lst.put(Utils.formatGroupJID(groupJID), group);
//        share.setGroupLst(SharedPrefrence.GROUP_LST, lst);
//
//        return "Add Group Members " + groupJID;
//    }

    //=============================================

    /*
        Update Group info receive.
     */

    public void receiveGroupNotifyForGroupUpdate(Message message) {

        String from = message.getFrom();
        String contactJid = "";
        if (from.contains("/")) {
            contactJid = from.split("/")[0];
            DialogUtility.showLOG("The real jid is :" + contactJid);
        } else {
            contactJid = from;
        }
        if (contactJid.equals(share.getValue(SharedPrefrence.JID))) {
            return;
        }


        String msgBody = "";
        try {
            msgBody = Utils.utf8UrlDecoding(message.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String displayMessage = updateGroupInfo(msgBody, message.getSubject());
        ChatBody chatBody = Utils.getChatBodyForChatType(displayMessage, Utils.getMyJID(mApplicationContext), contactJid,
                PreferenceConstants.GN_CREATE_GROUP, PreferenceConstants.IN_BOUND, PreferenceConstants.UNREAD, message, PreferenceConstants.MESSAGE_TYPE_CHAT);

        //Bundle up the intent and send the broadcast.
        Intent intent = new Intent(RoosterConnectionService.NEW_GROUP_MESSAGE);
        intent.setPackage(mApplicationContext.getPackageName());
        intent.putExtra("CHAT", chatBody);
        intent.putExtra(RoosterConnectionService.BUNDLE_FROM_JID, contactJid);
        intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY, displayMessage);
        mApplicationContext.sendBroadcast(intent);


        //Bundle up the intent and send the broadcast.
        Intent groupUpdate = new Intent(RoosterConnectionService.GROUP_UPDATE);
        mApplicationContext.sendBroadcast(groupUpdate);
        DialogUtility.showLOG("<-- receiveGroupNotifyForGroupCreate :" + contactJid + " broadcast sent.");
    }

    public String updateGroupInfo(String msgBody, String subject) {

        String groupJID = Utils.getStringFromIndex(msgBody, 0);
        String groupName = Utils.getStringFromIndex(msgBody, 1);
        String groupImageUrl = Utils.getStringFromIndex(msgBody, 2);
        String groupAdmin = Utils.getStringFromIndex(msgBody, 3);
        String groupUpdateBy = Utils.getStringFromIndex(msgBody, 4);

        HashMap<String, Group> lst = share.getGroup(SharedPrefrence.GROUP_LST);

        Group group = lst.get(groupJID);
        group.setGroup_name(groupName);
        group.setGroup_jid(Utils.formatGroupJID(groupJID));
        group.setAdmin_jid(groupAdmin);
        group.setGroup_image_url(groupImageUrl);


        lst.put(Utils.formatGroupJID(groupJID), group);
        share.setGroupLst(SharedPrefrence.GROUP_LST, lst);

        return "Change by " + groupUpdateBy;
    }

    //===========================

    private void setupUiThreadBroadCastMessageReceiver() {
        uiThreadMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Check if the Intents purpose is to send the message.
                String action = intent.getAction();
                if (action.equals(RoosterConnectionService.SEND_MESSAGE)) {
                    //Send the message.
                    //String msg = intent.getStringExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY);
                    ChatBody chatBody = (ChatBody) intent.getSerializableExtra("CHAT");
                    sendMessage(chatBody);

                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(RoosterConnectionService.SEND_MESSAGE);
        mApplicationContext.registerReceiver(uiThreadMessageReceiver, filter);

    }

    private void sendMessage(ChatBody chatBody) {
        try {

            DialogUtility.showLOG("Sending message to :" + chatBody.getFriendJID());
            Chat chat = ChatManager.getInstanceFor(mConnection)
                    .createChat(chatBody.getFriendJID(), new ChatMessageListener() {
                        @Override
                        public void processMessage(Chat chat, Message message) {
                            DialogUtility.showLOG("message.getFrom() :" + message.getFrom());
                        }
                    });

            Message msg = new Message(chatBody.getFriendJID(), Message.Type.chat);
            msg.setSubject(chatBody.getMessageType());
            msg.setBody(Utils.utf8UrlEncoding(formatMsg(chatBody)));
            msg.setPacketID(chatBody.getMessageID());
            chat.sendMessage(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String formatMsg(ChatBody chat) {
        String msgBody = "";

        /*
        * SINGLE
        * */
        if (chat.getMessageType().equals(PreferenceConstants.TEXT)) {
            msgBody = chat.getMessage();

        } else if (chat.getMessageType().equals(PreferenceConstants.IMAGE)) {
            msgBody = Utils.fileTransferSingleImageVideo(chat);

        } else if (chat.getMessageType().equals(PreferenceConstants.VIDEO)) {
//            msgBody = Utils.fileTransferSingleImageVideo(chat);

        } else if (chat.getMessageType().equals(PreferenceConstants.AUDIO)) {
            msgBody = Utils.fileTransferSingleAudio(chat);

        } else if (chat.getMessageType().equals(PreferenceConstants.CONTACT)) {
            msgBody = chat.getMessage();

        }
//        else if (chat.getMessageType().equals(PreferenceConstants.CONTACT)) {
//            msgBody = Utils.fileTransferSingleContact(chat);
//        }

        /*
        * GROUP
        * */
        else if (chat.getMessageType().equals(PreferenceConstants.G_TEXT)) {
            msgBody = Utils.addGroupJIDIntoMessage(chat);

        } else if (chat.getMessageType().equals(PreferenceConstants.G_IMAGE)) {
            msgBody = Utils.fileTransferGroupImageVideo(chat);

        } else if (chat.getMessageType().equals(PreferenceConstants.G_VIDEO)) {
//            msgBody = Utils.fileTransferGroupImageVideo(chat);

        } else if (chat.getMessageType().equals(PreferenceConstants.G_AUDIO)) {
            msgBody = Utils.fileTransferGroupAudio(chat);

        } else if (chat.getMessageType().equals(PreferenceConstants.G_CONTACT)) {
//            msgBody = Utils.addGroupJIDIntoMessage(chat);
            msgBody = chat.getMessage();
        }


        return msgBody;
    }


    /*  *
    Send Notify Group Message
     *  */
    public void sendGroupNotify(GroupNotify groupNotify) {
        DialogUtility.showLOG("Sending message to :" + groupNotify.getGroupJID());

        Chat chat = ChatManager.getInstanceFor(mConnection)
                .createChat(groupNotify.getGroupJID(), new ChatMessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {
                        DialogUtility.showLOG("message.getFrom() :" + message.getFrom());
                    }
                });

        try {
            Message msg = new Message(groupNotify.getGroupJID(), Message.Type.chat);
            msg.setSubject(groupNotify.getGroupSubject());
            msg.setBody(Utils.utf8UrlEncoding(formateGroupNotifyMsg(groupNotify)));
            //msg.setPacketID(groupNotify.getGnPacketID());
            chat.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String formateGroupNotifyMsg(GroupNotify groupNotify) {
        String formatedString = "";

        switch (groupNotify.getGroupSubject()) {

            case PreferenceConstants.GN_CREATE_GROUP:
                formatedString = createGroupNotifyMsg(groupNotify);
                break;


            case PreferenceConstants.GN_ADD_TO_GROUP:
                formatedString = addParticipantToGroup(groupNotify);
                break;


            case PreferenceConstants.GN_DELETE_FROM_GROUP:
                formatedString = deleteParticipantFromGroup(groupNotify);
                break;


            case PreferenceConstants.GN_UPDATE_GROUP:
                formatedString = updateGroupBody(groupNotify);
                break;
        }

        return formatedString;
    }

    public String createGroupNotifyMsg(GroupNotify groupNotify) {
        return groupNotify.getGroupJID() + PreferenceConstants.SEPERATOR +
                groupNotify.getGroupName() + PreferenceConstants.SEPERATOR +
                groupNotify.getGroupImageUrl() + PreferenceConstants.SEPERATOR +
                groupNotify.getGroupAdmin();
    }

    public String updateGroupBody(GroupNotify groupNotify) {
        return groupNotify.getGroupJID() + PreferenceConstants.SEPERATOR +
                groupNotify.getGroupName() + PreferenceConstants.SEPERATOR +
                groupNotify.getGroupImageUrl() + PreferenceConstants.SEPERATOR +
                groupNotify.getGroupAdmin() + PreferenceConstants.SEPERATOR +
                groupNotify.getGroupUpdateBy();
    }

    public String addParticipantToGroup(GroupNotify groupNotify) {
        return groupNotify.getGroupJID() + PreferenceConstants.SEPERATOR +
                groupNotify.getMembersJID() + PreferenceConstants.SEPERATOR +
                groupNotify.getGroupAdmin() + PreferenceConstants.SEPERATOR +
                groupNotify.getGroupName() + PreferenceConstants.SEPERATOR +
                groupNotify.getGroupImageUrl();
    }


    public String deleteParticipantFromGroup(GroupNotify groupNotify) {
        return groupNotify.getGroupJID() + PreferenceConstants.SEPERATOR +
                groupNotify.getMembersJID() + PreferenceConstants.SEPERATOR +
                groupNotify.getGroupAdmin();
    }


//    @Override
//    public void processMessage(ChatBody chat, Message message) {
//
//        Log.d(TAG,"message.getBody() :"+message.getBody());
//        Log.d(TAG,"message.getFrom() :"+message.getFrom());
//
//        String from = message.getFrom();
//        String contactJid="";
//        if ( from.contains("/"))
//        {
//            contactJid = from.split("/")[0];
//            Log.d(TAG,"The real jid is :" +contactJid);
//        }else
//        {
//            contactJid=from;
//        }
//
//        //Bundle up the intent and send the broadcast.
//        Intent intent = new Intent(RoosterConnectionService.NEW_MESSAGE);
//        intent.setPackage(mApplicationContext.getPackageName());
//        intent.putExtra(RoosterConnectionService.BUNDLE_FROM_JID,contactJid);
//        intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY,message.getBody());
//        mApplicationContext.sendBroadcast(intent);
//        Log.d(TAG,"Received message from :"+contactJid+" broadcast sent.");
//
//    }


    public void disconnect() {
        DialogUtility.showLOG("Disconnecting from serser " + mServiceName);
        try {
            if (mConnection != null) {
                mConnection.disconnect();
            }

        } catch (SmackException.NotConnectedException e) {
            RoosterConnectionService.sConnectionState = ConnectionState.DISCONNECTED;
            e.printStackTrace();

        }
        mConnection = null;
        // Unregister the message broadcast receiver.
        if (uiThreadMessageReceiver != null) {
            mApplicationContext.unregisterReceiver(uiThreadMessageReceiver);
            uiThreadMessageReceiver = null;
        }

    }


    @Override
    public void connected(XMPPConnection connection) {
        RoosterConnectionService.sConnectionState = ConnectionState.CONNECTED;
        DialogUtility.showLOG("Connected Successfully");
    }

    @Override
    public void authenticated(XMPPConnection connection) {
        RoosterConnectionService.sConnectionState = ConnectionState.CONNECTED;
        DialogUtility.showLOG("<-- Authenticated Successfully");
        showContactListActivityWhenAuthenticated();

    }

    @Override
    public void connectionClosed() {
        RoosterConnectionService.sConnectionState = ConnectionState.DISCONNECTED;
        DialogUtility.showLOG("<--Connectionclosed()");
        Intent i1 = new Intent(mApplicationContext, RoosterConnectionService.class);
        mApplicationContext.startService(i1);

    }

    @Override
    public void connectionClosedOnError(Exception e) {
        RoosterConnectionService.sConnectionState = ConnectionState.DISCONNECTED;
        DialogUtility.showLOG("<--ConnectionClosedOnError, error " + e.toString());
        Intent i1 = new Intent(mApplicationContext, RoosterConnectionService.class);
        mApplicationContext.startService(i1);

    }

    @Override
    public void reconnectingIn(int seconds) {
        RoosterConnectionService.sConnectionState = ConnectionState.CONNECTING;
        Intent i1 = new Intent(mApplicationContext, RoosterConnectionService.class);
        mApplicationContext.startService(i1);
        DialogUtility.showLOG("<--ReconnectingIn() ");

    }

    @Override
    public void reconnectionSuccessful() {
        RoosterConnectionService.sConnectionState = ConnectionState.CONNECTED;
        DialogUtility.showLOG("<--ReconnectionSuccessful()");
//        Intent i1 = new Intent(mApplicationContext, RoosterConnectionService.class);
//        mApplicationContext.startService(i1);

    }

    @Override
    public void reconnectionFailed(Exception e) {
        RoosterConnectionService.sConnectionState = ConnectionState.DISCONNECTED;
        DialogUtility.showLOG("<--ReconnectionFailed()");
        Intent i1 = new Intent(mApplicationContext, RoosterConnectionService.class);
        mApplicationContext.startService(i1);

    }

    private void showContactListActivityWhenAuthenticated() {
        Intent i = new Intent(RoosterConnectionService.UI_AUTHENTICATED);
        i.setPackage(mApplicationContext.getPackageName());
        mApplicationContext.sendBroadcast(i);
        DialogUtility.showLOG("<--Sent the broadcast that we are authenticated");
    }

    public XMPPTCPConnection getmConnection() {
        return mConnection;
    }

    public void setmConnection(XMPPTCPConnection mConnection) {
        this.mConnection = mConnection;
    }

    public void connectDirect() {
        try {
            mConnection.connect();
        } catch (Exception e) {
            DialogUtility.showLOG("Trying to Connect but got error");
        }

    }

    private void addNotification(ChatBody chatBody) {
        String totalUnread = db.getTotalUnread();
        String totalUnreadConversation = db.getOpenConversation_ofUnread();

        totalUnreadCount = 0;
        totalUnreadConversationCount = 0;

//        NotificationCompat.Builder builder =
//                new NotificationCompat.Builder(mApplicationContext)
//                        .setSmallIcon(R.drawable.ic_launcher)
//                        .setLargeIcon(BitmapFactory.decodeResource(mApplicationContext.getResources(), R.drawable.ic_launcher))
//                        .setContentTitle(mApplicationContext.getString(R.string.app_name))
//                        .setContentText(msg);
//
//        Intent notificationIntent = new Intent(mApplicationContext, CustomViewIconTextTabsActivity.class);
//        PendingIntent contentIntents = PendingIntent.getActivity(mApplicationContext, 0, notificationIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(contentIntents);
//
//        // Add as notification
//        NotificationManager manager = (NotificationManager) mApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(PreferenceConstants.NOTIFY_MESSAGE, builder.build());


        /* Custom Notification */

        if (totalUnread != null) {
            totalUnreadCount = Integer.parseInt(totalUnread);
        }
        if (totalUnreadConversation != null) {
            totalUnreadConversationCount = Integer.parseInt(totalUnreadConversation);
        }

        if (totalUnreadConversationCount == 1) {
            CustomNotificationOne(chatBody);
        } else {
            CustomNotificationThree(chatBody);
        }

        /* Custom Notification */
    }

    public void CustomNotificationOne(ChatBody chatBody) {

        String jid = "";

        msg = totalUnreadCount + " messages from this chats";

        Bitmap icon1 = BitmapFactory.decodeResource(mApplicationContext.getResources(), R.drawable.ic_launcher);

        String getNameOrNumber = "";
        boolean isMessageBelongsToChat = Utils.isMessageBelongsToChat(chatBody.getFriendJID());

        if(isMessageBelongsToChat) {
            getNameOrNumber = Utils.getContactNameByNumber(mApplicationContext, chatBody.getFriendJID());
        } else {
//            getNameOrNumber = getMyGroup(Utils.formatGroupJID(chatBody.getFriendJID())).getGroup_name();
            getNameOrNumber = chatBody.getContactName();
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mApplicationContext).setAutoCancel(true)
                .setContentTitle("F5 Chat from " + getNameOrNumber)
                .setSmallIcon(R.drawable.ic_launcher).setLargeIcon(icon1)
                .setContentText(msg);

        mBuilder.setPriority(Notification.PRIORITY_HIGH);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(chatBody.getMessage());
        bigText.setBigContentTitle(getNameOrNumber);
        bigText.setSummaryText(msg);
        mBuilder.setStyle(bigText);

        // Creates an explicit intent for an Activity in your app

        Intent resultIntent = null;
        if(isMessageBelongsToChat) {

            resultIntent = new Intent(mApplicationContext, ChatWindow.class);
            jid = chatBody.getFriendJID();
        } else {

            resultIntent = new Intent(mApplicationContext, GroupChatWindow.class);
            jid = Utils.formatGroupJID(chatBody.getFriendJID());
        }

        resultIntent.putExtra(SharedPrefrence.JID, jid);
        resultIntent.putExtra("mobile", Utils.getNumberFromJID(jid));
        resultIntent.putExtra("user_name", getNameOrNumber);

//        Intent resultIntent = new Intent(mApplicationContext, ChatWindow.class);

        // The stack builder object will contain an artificial back stack for
        // the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mApplicationContext);

        // Adds the back stack for the Intent (but not the Intent itself)
        if(isMessageBelongsToChat) {
            stackBuilder.addParentStack(ChatWindow.class);
        } else {
            stackBuilder.addParentStack(GroupChatWindow.class);
        }
//        stackBuilder.addParentStack(ChatWindow.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) mApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


    public void CustomNotificationThree(ChatBody chatBody) {

        msg = totalUnreadCount + " messages from " + totalUnreadConversationCount + " chats";

        ArrayList<ChatBody> chats = db.getOpenConversationsForNotification();

        String[] events = new String[chats.size()];
        for(int i=0 ; i<chats.size() ; i++) {

            ChatBody chat = chats.get(i);

            if(Utils.isMessageBelongsToChat(chatBody.getFriendJID())) {
                events[i] = chat.getMessage();
            } else {
                events[i] = getMyGroup(Utils.formatGroupJID(chat.getFriendJID())).getGroup_name() + " : " + chat.getMessage();
            }
        }

        Bitmap icon1 = BitmapFactory.decodeResource(mApplicationContext.getResources(),
                R.drawable.ic_launcher);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mApplicationContext).setAutoCancel(true)
                .setContentTitle("F5 Chat Messages")
                .setSmallIcon(R.drawable.ic_launcher).setLargeIcon(icon1)
                .setContentText(msg);

        mBuilder.setPriority(Notification.PRIORITY_HIGH);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle("F5 Chat Conversations");

        // Moves events into the big view
        for (int i = 0; i < events.length; i++) {

            inboxStyle.addLine(events[i]);
        }

        inboxStyle.setSummaryText(msg);

        // Moves the big view style object into the notification object.
        mBuilder.setStyle(inboxStyle);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mApplicationContext, TabsViewPagerFragmentActivity.class);

        // The stack builder object will contain an artificial back stack for
        // the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mApplicationContext);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(TabsViewPagerFragmentActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) mApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public void changePresence(Presence presence) {
        String fromJID = Utils.getBareJID(presence.getFrom());
        if (!fromJID.equals(share.getValue(SharedPrefrence.JID))) {
            if (presence != null) {
                FriendPresence friendPresence = new FriendPresence();
                friendPresence.setFriendJID(fromJID);
                Presence.Type type = presence.getType();
                if (type == Presence.Type.available) {
                    friendPresence.setType("available");
                    Presence.Mode mode = presence.getMode();
                    if (mode == Presence.Mode.away) {
                        friendPresence.setMode("away");
                    } else if (mode == Presence.Mode.xa) {
                        friendPresence.setMode("xa");
                    } else if (mode == Presence.Mode.dnd) {
                        friendPresence.setMode("dnd");
                    } else if (mode == Presence.Mode.chat) {
                        friendPresence.setMode("chat");
                    } else {
                        friendPresence.setMode("online");
                    }
                } else if (type == Presence.Type.error) {
                    friendPresence.setType("offline");
                    friendPresence.setMode("none");
                } else {
                    friendPresence.setType("offline");
                    friendPresence.setMode("none");
                }
                db.addIntoPresence(friendPresence);
                //Bundle up the intent and send the broadcast.
                Intent intent = new Intent(RoosterConnectionService.PRESENCE_CHANGE);
                mApplicationContext.sendBroadcast(intent);
            }

        }
    }


    /*
     * This was grabbed from the Ignite Realtime Board, and is the META-INF file that is
	 * ignored by Android.  That's why this is here.
	 */
    private void configure(ProviderManager pm) {

        //  Private Data Storage
        pm.addIQProvider("query", "jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());

        //  Time
        try {
            pm.addIQProvider("query", "jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (ClassNotFoundException e) {
            Log.w("TestClient", "Can't load class for org.jivesoftware.smackx.packet.Time");
        }

//        //  Roster Exchange
//        pm.addExtensionProvider("x","jabber:x:roster", new RosterExchangeProvider());
//
//        //  Message Events
//        pm.addExtensionProvider("x","jabber:x:event", new MessageEventProvider());

        //  Chat State
        pm.addExtensionProvider("active", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

        pm.addExtensionProvider("composing", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

        pm.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

        pm.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

        pm.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

        //  XHTML
        pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());

        //  Group Chat Invitations
        pm.addExtensionProvider("x", "jabber:x:conference", new GroupChatInvitation.Provider());

        //  Service Discovery # Items
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());

        //  Service Discovery # Info
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());

        //  Data Forms
        pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

        //  MUC User
        pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider());

        //  MUC Admin
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin", new MUCAdminProvider());


        //  MUC Owner
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());

        //  Delayed Delivery
        pm.addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider());

        //  Version
        try {
            pm.addIQProvider("query", "jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
            //  Not sure what's happening here.
        }

        //  VCard
        pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());

        //  Offline Message Requests
        pm.addIQProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());

        //  Offline Message Indicator
        pm.addExtensionProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());

        //  Last Activity
        pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

        //  User Search
        pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

        //  SharedGroupsInfo
        pm.addIQProvider("sharedgroup", "http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());

        //  JEP-33: Extended Stanza Addressing
        pm.addExtensionProvider("addresses", "http://jabber.org/protocol/address", new MultipleAddressesProvider());

        //   FileTransfer
        pm.addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider());

        pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());

//        pm.addIQProvider("open","http://jabber.org/protocol/ibb", new IBBProviders.Open());
//
//        pm.addIQProvider("close","http://jabber.org/protocol/ibb", new IBBProviders.Close());
//
//        pm.addExtensionProvider("data","http://jabber.org/protocol/ibb", new IBBProviders.Data());

        //  Privacy
        pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());

        pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
        pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
        pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
        pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
        pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
        pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());

//        // PubSub
//        pm.addIQProvider(
//                "query", "http://jabber.org/protocol/disco#items",
//                new org.jivesoftware.smackx.provider.DiscoverItemsProvider()
//        );
//
//        pm.addIQProvider("query",
//                "http://jabber.org/protocol/disco#info",
//                new org.jivesoftware.smackx.provider.DiscoverInfoProvider());

        pm.addIQProvider("pubsub",
                "http://jabber.org/protocol/pubsub",
                new org.jivesoftware.smackx.pubsub.provider.PubSubProvider());

        pm.addExtensionProvider("subscription", PubSubNamespace.BASIC.getXmlns(), new SubscriptionProvider());

        pm.addExtensionProvider(
                "create",
                "http://jabber.org/protocol/pubsub",
                new org.jivesoftware.smackx.pubsub.provider.SimpleNodeProvider());

        pm.addExtensionProvider("items",
                "http://jabber.org/protocol/pubsub",
                new org.jivesoftware.smackx.pubsub.provider.ItemsProvider());

        pm.addExtensionProvider("item",
                "http://jabber.org/protocol/pubsub",
                new org.jivesoftware.smackx.pubsub.provider.ItemProvider());

        pm.addExtensionProvider("item", "",
                new org.jivesoftware.smackx.pubsub.provider.ItemProvider());

        pm.addExtensionProvider(
                "subscriptions",
                "http://jabber.org/protocol/pubsub",
                new org.jivesoftware.smackx.pubsub.provider.SubscriptionsProvider());

        pm.addExtensionProvider(
                "subscriptions",
                "http://jabber.org/protocol/pubsub#owner",
                new org.jivesoftware.smackx.pubsub.provider.SubscriptionsProvider());

        pm.addExtensionProvider(
                "affiliations",
                "http://jabber.org/protocol/pubsub",
                new org.jivesoftware.smackx.pubsub.provider.AffiliationsProvider());

        pm.addExtensionProvider(
                "affiliation",
                "http://jabber.org/protocol/pubsub",
                new org.jivesoftware.smackx.pubsub.provider.AffiliationProvider());

        pm.addExtensionProvider("options",
                "http://jabber.org/protocol/pubsub",
                new org.jivesoftware.smackx.pubsub.provider.FormNodeProvider());

        pm.addIQProvider("pubsub",
                "http://jabber.org/protocol/pubsub#owner",
                new org.jivesoftware.smackx.pubsub.provider.PubSubProvider());

        pm.addExtensionProvider("configure",
                "http://jabber.org/protocol/pubsub#owner",
                new org.jivesoftware.smackx.pubsub.provider.FormNodeProvider());

        pm.addExtensionProvider("default",
                "http://jabber.org/protocol/pubsub#owner",
                new org.jivesoftware.smackx.pubsub.provider.FormNodeProvider());


        pm.addExtensionProvider("event",
                "http://jabber.org/protocol/pubsub#event",
                new org.jivesoftware.smackx.pubsub.provider.EventProvider());

        pm.addExtensionProvider(
                "configuration",
                "http://jabber.org/protocol/pubsub#event",
                new org.jivesoftware.smackx.pubsub.provider.ConfigEventProvider());

        pm.addExtensionProvider(
                "delete",
                "http://jabber.org/protocol/pubsub#event",
                new org.jivesoftware.smackx.pubsub.provider.SimpleNodeProvider());

        pm.addExtensionProvider("options",
                "http://jabber.org/protocol/pubsub#event",
                new org.jivesoftware.smackx.pubsub.provider.FormNodeProvider());

        pm.addExtensionProvider("items",
                "http://jabber.org/protocol/pubsub#event",
                new org.jivesoftware.smackx.pubsub.provider.ItemsProvider());

        pm.addExtensionProvider("item",
                "http://jabber.org/protocol/pubsub#event",
                new org.jivesoftware.smackx.pubsub.provider.ItemProvider());

//        pm.addExtensionProvider("headers",
//                "http://jabber.org/protocol/shim",
//                new org.jivesoftware.smackx.provider.HeaderProvider());
//
//        pm.addExtensionProvider("header",
//                "http://jabber.org/protocol/shim",
//                new org.jivesoftware.smackx.provider.HeadersProvider());


        pm.addExtensionProvider(
                "retract",
                "http://jabber.org/protocol/pubsub#event",
                new org.jivesoftware.smackx.pubsub.provider.RetractEventProvider());

        pm.addExtensionProvider(
                "purge",
                "http://jabber.org/protocol/pubsub#event",
                new org.jivesoftware.smackx.pubsub.provider.SimpleNodeProvider());


        //SmackConfiguration.setKeepAliveInterval(-1);


    }
}
