package com.csipsimple.f5chat.utility;

import android.net.Uri;

import com.csipsimple.f5chat.PhoneNumber;

import java.io.File;

/**
 * Created by Kashish1 on 7/5/2016.
 */
public class PreferenceConstants {

    public static final String SERVER = "85.214.17.140";
    public static final String SERVERATTHERSTE = "@" + SERVER;

    public static final String PASS_STATUS = "1";
    public static final String PASS_FAIL = "0";

    public static final String F5_CONTACT = "f5";
    public static final String ALL_CONTACT = "all";

    public static final int CONFIRM_READ_CONTACT = 101;

    public static final String MESSAGE_TYPE_CHAT = "5000";
    public static final String MESSAGE_TYPE_GROUP_CHAT = "6000";

    public static final String IN_BOUND = "IN";
    public static final String OUT_BOUND = "OUT";
    public static final String CHAT_PROGRESS = "normal";

    public static final String SENT = "sent";
    public static final String DELIVERED = "delivered";
    public static final String SEEN = "seen";
    public static final String UNREAD = "unread";
    public static final String READ = "read";
    public static final String SENT_LATER = "sent_later";

    public static final int NOTIFY_MESSAGE = 1001;


    //    Single Chat :-
    public static final String TEXT = "1000";
    public static final String IMAGE = "1001";
    public static final String VIDEO = "1002";
    public static final String AUDIO = "1003";
    public static final String LOCATION = "1004";
    public static final String SKETCH = "1005";
    public static final String EMOJI = "1006";
    public static final String CONTACT = "1007";


    //    Group Chat:-
    public static final String G_TEXT = "2000";
    public static final String G_IMAGE = "2001";
    public static final String G_VIDEO = "2002";
    public static final String G_AUDIO = "2003";
    public static final String G_LOCATION = "2004";
    public static final String G_SKETCH = "2005";
    public static final String G_EMOJI = "2006";
    public static final String G_CONTACT = "2007";


    //    Group Notification Subject :-
    public static final String GN_CREATE_GROUP = "3000";
    public static final String GN_ADD_TO_GROUP = "3001";
    public static final String GN_DELETE_FROM_GROUP = "3002";
    public static final String GN_UPDATE_GROUP = "3003";


    //    Profile Notification Subject:-
    public static final String PN_PROFILE_UPDATE = "4000";



    /* preferences */


    // "https://85.214.17.140/F5chat/send_otp.php";
    public static final String BASE_URL = "https://85.214.17.140/F5chat/";

    public static final String UPDATE_PROFILE = BASE_URL + "update_profile.php";

    public static final String CONTACT_SYNC = BASE_URL + "contact_syncing.php";

    public static final String SYNC_COMPELLED = "com.csipsimple.f5chat.sync.completed";

    //----------------------------------------------------------------------------------------------

    public static final String user_jid = "user_jid";
    public static final String group_name = "group_name";
    public static final String member_jid_without_ip = "member_jid_without_ip";
    public static final String group_image = "group_image";

    public static final String JID = "jid";

    public static final String CONSTANT_API_CREATE_GROUP = "create group";
    public static final String API_CREATE_GROUP = BASE_URL + "create_group.php";

    public static final String CONSTANT_API_GET_GROUP_LIST = "get group list";
    public static final String API_GET_GROUP_LIST = BASE_URL + "get_Grouplist_by_memberid.php";
    public static final String GROUP_LIST_RESPONSE = "getResponseOfGroupLst";

    public static final String SEND_OTP = BASE_URL + "send_otp.php";
    public static final String SEND_OTP_RESPONSE = "sendOTPResponse";

    public static final String REGISTRATION = BASE_URL + "registration.php";
    public static final String REGISTRATION_RESPONSE = "getRegistrationResponse";

    public static final String RESEND_SEND_OTP = BASE_URL + "send_otp.php";
    public static final String RESEND_SEND_OTP_RESPONSE = "ResendSendOTPResponse";

    public static final String API_UPDATE_GROUP = BASE_URL + "update_group.php";
    public static final String UPDATE_GROUP = "update_group";

    public static final String API_ADD_MEMBER = BASE_URL + "add_member.php";
    public static final String ADD_MEMBER = "add_member";

    public static final String API_GET_GROUP_DETAIL = BASE_URL + "get_GroupDetail_by_groupid.php";
    public static final String GET_GROUP_DETAIL = "getGroupDetail";

    public static final String DELETE_MEMBER = "delete member";
    public static final String API_DELETE_MEMBER = BASE_URL + "delete_member.php";
    public static final String EXIT_FROM_GROUP = "exitFromGroup";

    public static final String API_UPLOAD_FILE = BASE_URL + "file_transfer.php";
    public static final String UPLOAD_FILE = "file_transfer";

    public static Uri uri = null;
    public static String BLUE = "#0082C6";
    public static PhoneNumber phoneNumber = null;
    public static int phoneDialogCode = 9001;
    public static String DEVICE_TYPE = "android";

    public static int countryCode = 9002;
    public static int phoneEdit = 9003;

    public static int createGroup = 9004;
    public static int editGroupProfile = 9005;
    public static int deleteMembers = 9006;
    public static int addParticipants = 9007;
    public static int addMoreParticipants = 9008;

    public static String TYPE = "screen_type";

    public static String GroupJidWithoutIp = "group_jid_without_ip";
    public static String MemberJidWithoutIp = "member_jid_without_ip";
    public static String AdminJidWithoutIp = "admin_jid_without_ip";
    public static String GroupName = "group_name";
    public static String GroupImage = "group_image";
    public static String GroupMembers = "group_members";
    public static String MemberJID = "member_jid";
    public static String SEPERATOR = "#:#";
    public static String COMMA_SEPARATOR = ",";


    /* Directory Creation*/

    /*ROOT*/
    public static String rootDirectory = "F5Chat";

    /*SUBROOT  rootDirectory*/
    public static String subRootDBDirectory = "Databases";
    public static String subRootMediaDirectory = "Media";

    /*SUBROOT  subRootMediaDirectory */
    public static String dirVideo = "F5ChatVideo";
    public static String dirImages = "F5ChatImages";
    public static String dirAudio = "F5ChatAudio";

    public static String dirSent = "Sent";



    /* Files Status */

    public static String fileUploding = "uploading";
    public static String fileUploded = "uploaded";
    public static String fileDownloading = "downloading";
    public static String fileFail = "fail";

}
