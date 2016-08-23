package com.csipsimple.f5chat.group;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.csipsimple.R;
import com.csipsimple.f5chat.bean.AddressBook;
import com.csipsimple.f5chat.bean.ChatBody;
import com.csipsimple.f5chat.bean.Contact;
import com.csipsimple.f5chat.bean.Group;
import com.csipsimple.f5chat.database.DatabaseHandler;
import com.csipsimple.f5chat.drawingfun.DrawingMainActivity;
import com.csipsimple.f5chat.http.UploadFileToServer;
//import com.csipsimple.f5chat.map.MapsActivity;
import com.csipsimple.f5chat.rooster.RoosterConnectionService;
import com.csipsimple.f5chat.rooster.XmppConnect;
import com.csipsimple.f5chat.utility.DialogUtility;
import com.csipsimple.f5chat.utility.PreferenceConstants;
import com.csipsimple.f5chat.utility.SharedPrefrence;
import com.csipsimple.f5chat.utility.Utils;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import org.jivesoftware.smack.packet.Message;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * Created by Kashish1 on 7/5/2016.
 */
public class GroupChatWindow extends FragmentActivity implements TextWatcher {
    LinearLayout back, userName;
    ImageView sendMessagebutton, Chat_Keyboard;
    com.csipsimple.f5chat.view.OpenRegularEditText inputEditText;
    ListView chatlist;
    com.csipsimple.f5chat.font_family.Font_welcome_scr_Tv_regular tv_user_name;
    com.csipsimple.f5chat.font_family.Font_Welcome_scr_Tv_light tv_user_status;
    ChatListAdapter chatListAdapter;
    //private ChatView mChatView;
    private static final String TAG = "ChatWindow";
    ArrayList<ChatBody> populateChat = new ArrayList<ChatBody>();
    private String groupID;// admin_jid, group_name;
    private BroadcastReceiver mGroupBroadcastReceiver;
    SharedPrefrence share;
    DatabaseHandler db;
    Group groupInfo = null;



/* File Transfer Code */


    /* Audio Record Start */
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private MediaRecorder recorder = null;
    private int currentFormat = 0;
    private int output_formats[] = {MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP};
    private String file_exts[] = {AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP};
    private String audioFilePath;
    /* Audio Record End */


    LinearLayout v_takePhoto;
    LinearLayout v_gallery;
    LinearLayout v_audioMessage;
    LinearLayout v_sendLocation;
    LinearLayout v_sendDrawing;
    LinearLayout v_sendContact;

    LinearLayout attachment_view_one, attachment_view_two;
    RelativeLayout audioRecorderView;
    Button audioRecorder;
    ImageView iv_attachement;

    private ImageView mediaAudioStop = null;
    private ImageView mediaAudioPlay = null;
    private ImageView mediaAudioPause = null;
    private TextView audio_title = null;

    private SeekBar audio_player = null;

    private VideoView mVideoView = null;
    private LinearLayout mediaPlayer = null;
    MediaPlayer mMediaPlayer = null;
    Handler mHandler;

    private Uri fileUri = null; // file url to store image

    // File upload url (replace the ip with your server address)
    public static final String FILE_UPLOAD_URL = "http://192.168.0.104/AndroidFileUpload/fileUpload.php";

    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "F5Chat Upload";

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int GALLERY_REQUEST_CODE = 300;
    private static final int PICK_CONTACT_REQUEST_CODE = 400;
    private static final int RECORD_AUDIO_REQUEST_CODE = 500;

    public static final int EDITED_IMAGE = 2001;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;


    /* File Transfer Code*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_interface);

        mHandler = new Handler();

        share = SharedPrefrence.getInstance(this);
        db = new DatabaseHandler(this);

        groupID = getIntent().getExtras().getString(SharedPrefrence.JID);

        tv_user_status = (com.csipsimple.f5chat.font_family.Font_Welcome_scr_Tv_light) findViewById(R.id.tv_user_status);
        tv_user_status.setVisibility(View.GONE);

        back = (LinearLayout) findViewById(R.id.layout_back);
        userName = (LinearLayout) findViewById(R.id.userName);
        tv_user_name = (com.csipsimple.f5chat.font_family.Font_welcome_scr_Tv_regular) findViewById(R.id.tv_user_name);



/* File Transfer Code */
        v_takePhoto = (LinearLayout) findViewById(R.id.v_takePhoto);
        v_gallery = (LinearLayout) findViewById(R.id.v_gallery);
        v_audioMessage = (LinearLayout) findViewById(R.id.v_audioMessage);
        v_sendLocation = (LinearLayout) findViewById(R.id.v_sendLocation);
        v_sendDrawing = (LinearLayout) findViewById(R.id.v_sendDrawing);
        v_sendContact = (LinearLayout) findViewById(R.id.v_sendContact);

        attachment_view_one = (LinearLayout) findViewById(R.id.attachment_view_one);
        attachment_view_two = (LinearLayout) findViewById(R.id.attachment_view_two);

//        viewOne = (RelativeLayout) findViewById(R.id.viewOne);
//        viewTwo = (RelativeLayout) findViewById(R.id.viewOne);

        audioRecorderView = (RelativeLayout) findViewById(R.id.audioRecorderView);
        audioRecorder = (Button) findViewById(R.id.audioRecorder);

        iv_attachement = (ImageView) findViewById(R.id.iv_attachement);
/* File Transfer Code */


        sendMessagebutton = (ImageView) findViewById(R.id.sendmessagebutton);
        Chat_Keyboard = (ImageView) findViewById(R.id.Chat_Keyboard);

        chatlist = (ListView) findViewById(R.id.chatlist);
        inputEditText = (com.csipsimple.f5chat.view.OpenRegularEditText) findViewById(R.id.message_ET);

        inputEditText.addTextChangedListener(this);

        inputEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeAllView();
                return false;
            }
        });

        Chat_Keyboard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(Chat_Keyboard,
                        InputMethodManager.SHOW_IMPLICIT);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToDetail = new Intent(GroupChatWindow.this, GroupDetail.class);
                goToDetail.putExtra(PreferenceConstants.user_jid, groupID);
                startActivity(goToDetail);
            }
        });

        sendMessagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });



/* File Transfer Code */
        v_takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vTakePhotoVideo();
            }
        });
        v_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vGallery();
            }
        });
        v_audioMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vAudioMessage();
            }
        });
        v_sendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vSendLocation();
            }
        });
        v_sendDrawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vSendDrawing();
            }
        });
        v_sendContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vSendContact();
            }
        });

        iv_attachement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vAttachement();
            }
        });

        audioRecorder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.v("F5 Chat Recorder", "Start Recording");
                        startRecording();

                        break;
                    case MotionEvent.ACTION_UP:
                        Log.v("F5 Chat Recorder", "stop Recording");
                        stopRecording();

                        break;
                }

                return false;
            }
        });


/* File Transfer Code */

    }


    /* File Transfer Code */
    /**//**//**//**//**//**//**//**//**//**//**//**//**//**//**//**//**//**//**//**//**//**//**//**/
    public void vAttachement() {

        hideKeyBoard();

        closeAllView();

        if (attachment_view_one.getVisibility() == View.VISIBLE) {
            attachment_view_one.setVisibility(View.GONE);
            attachment_view_two.setVisibility(View.GONE);
        } else {
            attachment_view_one.setVisibility(View.VISIBLE);
            attachment_view_two.setVisibility(View.VISIBLE);
        }
    }

    public void hideKeyBoard() {
        // Then just use the following:
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(iv_attachement.getWindowToken(), 0);
    }

    public void vTakePhotoVideo() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "F5_IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "F5_VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    /* Helper Methods */

    public void vGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    public void vAudioMessage() {

        vAudioView();
    }

    public void vAudioView() {
        /* To remove attachement view */

        closeAllView();

        if (audioRecorderView.getVisibility() == View.VISIBLE) {
            audioRecorderView.setVisibility(View.GONE);
        } else {
            audioRecorderView.setVisibility(View.VISIBLE);
        }
    }




    /*
     *
     * Record Audio Start
     *
     * */


    public String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]);
    }

    private void startRecording() {

        audioFilePath = getFilename();

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(output_formats[currentFormat]);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audioFilePath);
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Log.e("F5 Chat Recorder", "Error: " + what + ", " + extra);
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Log.w("F5 Chat Recorder", "Warning: " + what + ", " + extra);
        }
    };

    private void stopRecording() {
        if (null != recorder) {
            recorder.stop();
            recorder.reset();
            recorder.release();

            recorder = null;

            /* Path of Recorded Audio File */
            fileUri = Uri.parse(audioFilePath);

            /* Code To Send Audio */
            closeAllView();


            ChatBody chatBody = getChatBodyForFile(audioFilePath, PreferenceConstants.G_AUDIO);
            new UploadFileToServer(this, chatBody).execute();

            /* Code To Send Audio */
        }
    }

    /*
    * Record Audio End
    * */

    public void vSendLocation() {
//        Intent in = new Intent(GroupChatWindow.this, MapsActivity.class);
//        startActivity(in);
    }

    public void vSendDrawing() {
        Intent in = new Intent(this, DrawingMainActivity.class);
        startActivityForResult(in, EDITED_IMAGE);
    }

    public void vSendContact() {
//        Intent intent = new Intent(Intent.ACTION_PICK, Contacts.People.CONTENT_URI);
//        startActivityForResult(intent, PICK_CONTACT_REQUEST_CODE);

        Intent in = new Intent(this, ContactSelector.class);
        startActivityForResult(in, PICK_CONTACT_REQUEST_CODE);
    }

    public void closeAllView() {

        if (attachment_view_one.getVisibility() == View.VISIBLE) {
            attachment_view_one.setVisibility(View.GONE);
            attachment_view_two.setVisibility(View.GONE);
        } else if (audioRecorderView.getVisibility() == View.VISIBLE) {
            audioRecorderView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {

        if (attachment_view_one.getVisibility() == View.VISIBLE || audioRecorderView.getVisibility() == View.VISIBLE) {
            closeAllView();
        } else {
            finish();
        }
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image


        /*Close All View In ChatWindow*/
        closeAllView();


        /* CAPTURE IMAGE FROM CAMERA */
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ChatBody chatBody = getChatBodyForFile(fileUri.getPath(), PreferenceConstants.G_IMAGE);
                new UploadFileToServer(this, chatBody).execute();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        }

        /* CAPTURE VIDEO FROM CAMERA */
        else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Uri videoUri = data.getData();

                String filePath = getRealPathFromURI(videoUri);

                if (filePath.contains("com.google.android.apps.photos")) {
                    Uri contentUri = Uri.parse(filePath);
                    String unusablePath = contentUri.getPath();
                    int startIndex = unusablePath.indexOf("external/");
                    int endIndex = unusablePath.indexOf("/ACTUAL");
                    String embeddedPath = unusablePath.substring(startIndex, endIndex);
                    Uri.Builder builder = contentUri.buildUpon();
                    builder.path(embeddedPath);
                    builder.authority("media");
                    Uri newUri = builder.build();
                    filePath = newUri.toString();
                }

                /* Code To Send Video */

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }

        /* TAKE IMAGE FROM GALLERY */
        else if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                String filePath = data.getDataString();
                filePath = getImageUrlWithAuthority(getApplicationContext(), Uri.parse(filePath));

                String converted_Path = Utils.getRealPathFromURI(getApplicationContext(), filePath);
                /* Code To Send Gallery File */
                fileUri = Uri.parse(data.getDataString());
                ChatBody chatBody = getChatBodyForFile(converted_Path, PreferenceConstants.G_IMAGE);
                new UploadFileToServer(this, chatBody).execute();
            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled gallery", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to take resource", Toast.LENGTH_SHORT)
                        .show();
            }
        }

        /* PICK CONTACT FROM CONTACT PICKER*/
        else if (requestCode == PICK_CONTACT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                int position = data.getExtras().getInt("contactPosition");
                Contact selectContact = share.getContactList(SharedPrefrence.CONTACT_LIST).get(position);

//                Uri contactData = data.getData();
//                Cursor c = managedQuery(contactData, null, null, null, null);
//                if (c.moveToFirst()) {
//                    String name = c.getString(c.getColumnIndexOrThrow(Contacts.People.NAME));
//                    long number = c.getInt(c.getColumnIndexOrThrow(Contacts.People.NUMBER));

                AddressBook addressBook = new AddressBook();
                addressBook.setName(selectContact.getName());
                long lng = Long.parseLong(selectContact.getNumber());
                addressBook.setNumber(lng);

                sendContact(addressBook);
//                  }
//                    /* Code To Send Contact */

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled contact", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to take contact", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (data != null && requestCode == EDITED_IMAGE) {

            if (resultCode == RESULT_OK) {
                String filePath = data.getExtras().getString("FilePath");
                String ActualPath = getRealPathFromURI(Uri.parse(filePath));

                ChatBody chatBody = getChatBodyForFile(ActualPath, PreferenceConstants.G_IMAGE);
                new UploadFileToServer(this, chatBody).execute();
            }
        }
    }


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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Uri writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void sendContact(AddressBook addressBook) {
        ChatBody chat = null;
        String msg = "";
        String messageType = null;

        if (RoosterConnectionService.getState().equals(XmppConnect.ConnectionState.CONNECTED)) {
            messageType = PreferenceConstants.SENT;
        } else {
            messageType = PreferenceConstants.SENT_LATER;
        }

        //Send the message to the server
        chat = Utils.getChatBodyForChatType(msg.trim(), Utils.getMyJID(this), groupID,
                PreferenceConstants.G_CONTACT, PreferenceConstants.OUT_BOUND, PreferenceConstants.SENT,
                new Message(), PreferenceConstants.MESSAGE_TYPE_GROUP_CHAT, Utils.getMyJID(this));

        msg = Utils.fileTransferGroupContact(addressBook, chat);

        chat.setMessage(msg);

        db.addChatToGroupTable(chat);

        Intent intent = new Intent(RoosterConnectionService.SEND_MESSAGE);
        intent.putExtra("CHAT", chat);
        sendBroadcast(intent);

        populateChat.add(chat);
        updateChatList();
    }


    public ChatBody getChatBodyForFile(String localUri, String messageType) {
        ChatBody chatBody;
        String msg = "";
        String progress = "0";
        Message message = new Message();
        if (messageType.equals(PreferenceConstants.G_IMAGE)) {
            msg = "Image";
        } else if (messageType.equals(PreferenceConstants.G_AUDIO)) {
            msg = "Audio";
        }

        if (RoosterConnectionService.getState().equals(XmppConnect.ConnectionState.CONNECTED)) {
            DialogUtility.showLOG("The client is connected to the server,Sendint Message");
            //Send the message to the server

            chatBody = Utils.getChatBodyForChatType(msg.trim(), Utils.getMyJID(this), groupID,
                    messageType, PreferenceConstants.OUT_BOUND, PreferenceConstants.SENT,
                    new Message(), PreferenceConstants.MESSAGE_TYPE_GROUP_CHAT, Utils.getMyJID(this));
        } else {


            chatBody = Utils.getChatBodyForChatType(msg.trim(), Utils.getMyJID(this), groupID,
                    messageType, PreferenceConstants.OUT_BOUND, PreferenceConstants.SENT,
                    new Message(), PreferenceConstants.MESSAGE_TYPE_GROUP_CHAT, Utils.getMyJID(this));
        }

        db.addChatToGroupTable(chatBody);
        /* Add File Info To DB */
        chatBody.setLocalUri(localUri);
        chatBody.setProgress("0");
        /* SET FILE EXTENSION */
        chatBody.setExtension(Utils.getFileExtension(new File(localUri)));
        /* SET FILE THUMB */

        if (messageType.equals(PreferenceConstants.G_IMAGE)) {
            chatBody.setThumb(Utils.getImageThumb(localUri));
        } else if (messageType.equals(PreferenceConstants.G_AUDIO)) {
            chatBody.setAudio_title(Utils.getAudioTitleUsingPath(this, localUri));
        }


        db.addFilesToDB(chatBody);

        /*UI*/
//        Intent intent = new Intent(RoosterConnectionService.SEND_MESSAGE);
//        //intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY,msg);
//        intent.putExtra("CHAT", chatBody);
//        intent.putExtra("FILe_INFO", filesInfo);
//        //intent.putExtra(RoosterConnectionService.BUNDLE_TO, contactJid);
//        sendBroadcast(intent);

        populateChat.add(chatBody);
        updateChatList();

        return chatBody;
    }

    public void checkConnection() {
        XmppConnect mConnection = XmppConnect.getInstance();
        mConnection.connectDirect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGroupBroadcastReceiver);
        updateUnreadCount();
    }

    public void updateUnreadCount() {
        db.updateUnreadCount(share.getValue(SharedPrefrence.JID), groupID);
    }

    public void showListContent() {
        populateChat = db.getGroupChat(Utils.getMyJID(this), groupID);
        appendImageContent();

        chatListAdapter = new ChatListAdapter(populateChat);
        chatlist.setAdapter(chatListAdapter);
    }


    public void appendImageContent() {
        for (int i = 0; i < populateChat.size(); i++) {
            ChatBody filesInfo = populateChat.get(i);
            ChatBody temp = db.getFileInfo(filesInfo.getMessageID());
            filesInfo.setUrl(temp.getUrl());
            filesInfo.setThumb(temp.getThumb());
            filesInfo.setLocalUri(temp.getLocalUri());
            populateChat.set(i, filesInfo);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        try {

            showListContent();

            groupInfo = getMyGroup(groupID);
            tv_user_name.setText(groupInfo.getGroup_name());

            mGroupBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    switch (action) {
                        case RoosterConnectionService.NEW_GROUP_MESSAGE:
                            //String groupjid = intent.getStringExtra(RoosterConnectionService.BUNDLE_FROM_GROUP_JID);
                            String groupjid = intent.getStringExtra(RoosterConnectionService.BUNDLE_FROM_JID);
                            String body = intent.getStringExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY);

                            if (groupjid.equals(groupID)) {
                                //mChatView.receiveMessage(body);
                                ChatBody chatBody = (ChatBody) intent.getSerializableExtra("CHAT");
                                populateChat.add(chatBody);
                                updateChatList();
                            } else {
                                DialogUtility.showLOG("Got a message from jid :" + groupjid);
                            }

                            return;
                    }

                }
            };

            IntentFilter filter = new IntentFilter(RoosterConnectionService.NEW_GROUP_MESSAGE);
            registerReceiver(mGroupBroadcastReceiver, filter);
            updateUnreadCount();

            checkConnection();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Group getMyGroup(String JID) {
        HashMap<String, Group> groupHashMap = share.getGroup(SharedPrefrence.GROUP_LST);
        Group group = groupHashMap.get(JID);
        return group;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (inputEditText.getText().length() > 0) {

            sendMessagebutton.setVisibility(View.VISIBLE);
            // Chat_Record.setVisibility(View.GONE);
            Chat_Keyboard.setVisibility(View.GONE);
        } else {

            sendMessagebutton.setVisibility(View.GONE);
            // Chat_Record.setVisibility(View.VISIBLE);
            Chat_Keyboard.setVisibility(View.VISIBLE);
        }
    }

    public class ChatListAdapter extends BaseAdapter {
        ArrayList<ChatBody> populateChat;
        DisplayImageOptions options;

        public ChatListAdapter(ArrayList<ChatBody> populateChat) {
            this.populateChat = populateChat;
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_launcher)
                    .showImageForEmptyUri(R.drawable.ic_launcher)
                    .showImageOnFail(R.drawable.ic_launcher)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .cacheOnDisc(true)
                    .displayer(new SimpleBitmapDisplayer())
                    .build();
        }

        public int getCount() {
            return populateChat.size();
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            final ChatBody populate = populateChat.get(position);
            LayoutInflater inflater = getLayoutInflater();

            if (populate.getMessageType().equals(PreferenceConstants.G_TEXT)) {
                if (populate.getInOutBound().equals(PreferenceConstants.OUT_BOUND)) {
                    convertView = inflater.inflate(R.layout.outgoing_view, null);
                } else {
                    convertView = inflater.inflate(R.layout.incoming_view, null);
                }

                TextView message_body = (TextView) convertView.findViewById(R.id.message_body);
                message_body.setText(populate.getMessage());

            } else if (populate.getMessageType().equals(PreferenceConstants.G_IMAGE)) {
                if (populate.getInOutBound().equals(PreferenceConstants.OUT_BOUND)) {
                    convertView = inflater.inflate(R.layout.image_outgoing_view, null);
                } else {
                    convertView = inflater.inflate(R.layout.image_incoming_view, null);
                    final DonutProgress downloadProgress = (DonutProgress) convertView.findViewById(R.id.downloadProgress);
                    final ImageView downloadMedia = (ImageView) convertView.findViewById(R.id.downloadMedia);
                    if (populate.getLocalUri() != null) {
                        downloadMedia.setVisibility(View.GONE);
                        downloadProgress.setVisibility(View.GONE);
                    }
                    downloadMedia.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            downloadMedia.setVisibility(View.GONE);
                            downloadProgress.setVisibility(View.VISIBLE);
                            downloadFile(populate, downloadProgress);
                        }
                    });
                }

                ImageView imageBubble = (ImageView) convertView.findViewById(R.id.imageBubble);


                if (populate.getLocalUri() != null) {
                    //setImageUsingFilePath(imageBubble, populate.getLocalUri());
                    ImageLoader.getInstance().displayImage(Utils.getSDCardPathForUIL(populate.getLocalUri()), imageBubble, options);

                } else {
                    String thumb = populate.getThumb();
                    try {
                        if (thumb != null) {
                            Bitmap bitmap = Utils.decodeBase64(thumb);
                            if (bitmap != null) {
                                imageBubble.setImageBitmap(bitmap);
                            } else {
                                imageBubble.setImageResource(R.drawable.ic_launcher);
                            }
                        } else {
                            imageBubble.setImageResource(R.drawable.ic_launcher);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        imageBubble.setImageResource(R.drawable.ic_launcher);
                    }

                }


                imageBubble.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse("file://" + populate.getLocalUri()), "image/*");
                        startActivity(intent);

//                      intent.setDataAndType(Uri.parse("file://" + path), "video/*");
                    }
                });

            } else if (populate.getMessageType().equals(PreferenceConstants.G_CONTACT)) {

                if (populate.getInOutBound().equals(PreferenceConstants.OUT_BOUND)) {
                    convertView = inflater.inflate(R.layout.contact_outgoing_view, null);
                } else {
                    convertView = inflater.inflate(R.layout.contact_incoming_view, null);
                }

                final TextView addToCotact = (TextView) convertView.findViewById(R.id.addToCotact);
                if (!populate.getInOutBound().equals(PreferenceConstants.OUT_BOUND)) {
                    addToCotact.setVisibility(View.VISIBLE);
                }

                String c;
                String cName;
                String cNumber;
                try {
                    c = Utils.getStringFromIndex(populate.getMessage(), 0);
                    cName = Utils.getStringFromIndex(populate.getMessage(), 1);
                    cNumber = Utils.getStringFromIndex(populate.getMessage(), 2);

                    populate.setcName(cName);
                    populate.setcNumber(Long.parseLong(cNumber));

                    TextView contactName = (TextView) convertView.findViewById(R.id.contactName);
                    TextView contactNumber = (TextView) convertView.findViewById(R.id.contactNumber);

                    contactName.setText(cName);
                    contactNumber.setText(cNumber);
                } catch (Exception e) {
                }


                addToCotact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        addContactToAddressBook(populate);
                    }
                });

            } else if (populate.getMessageType().equals(PreferenceConstants.G_AUDIO)) {
                if (populate.getInOutBound().equals(PreferenceConstants.OUT_BOUND)) {
                    convertView = inflater.inflate(R.layout.audio_outgoing_view, null);
                } else {
                    convertView = inflater.inflate(R.layout.audio_incoming_view, null);
                }

                convertView = setAudioOperation(convertView, populate);
            }

            TextView time = (TextView) convertView.findViewById(R.id.time);
            //time.setText(populate.getDate());
            time.setText(Utils.getFormattedTime(Long.parseLong(populate.getTimeStamp())));

            return convertView;
        }
    }

    public void downloadFile(ChatBody populate, DonutProgress downloadProgress) {
        new FileDownloader(this, populate, downloadProgress).start();
    }

    public View setAudioOperation(final View convertView, final ChatBody chat) {


        if (!chat.getInOutBound().equals(PreferenceConstants.OUT_BOUND)) {
            final DonutProgress downloadProgress = (DonutProgress) convertView.findViewById(R.id.downloadProgress);
            final ImageView downloadMedia = (ImageView) convertView.findViewById(R.id.downloadMedia);

            if (chat.getLocalUri() != null) {
                downloadMedia.setVisibility(View.GONE);
                downloadProgress.setVisibility(View.GONE);
                getAudioPlay(convertView).setVisibility(View.VISIBLE);
            }

            downloadMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    downloadMedia.setVisibility(View.GONE);
                    downloadProgress.setVisibility(View.VISIBLE);
                    downloadFile(chat, downloadProgress);
                }
            });
        }

        if (chat.getLocalUri() != null) {
            getAudioPlay(convertView).setVisibility(View.VISIBLE);
        }

        getAudioPlay(convertView).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mMediaPlayer == null) {

                    mMediaPlayer = new MediaPlayer();
                    try {
                        String path = URLDecoder.decode(chat.getLocalUri());
                        System.out.println("PATH IS : " + path);

                        mMediaPlayer.setDataSource(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        mMediaPlayer.prepare();
                        mMediaPlayer.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();

                    getAudioPlay(convertView).setVisibility(View.GONE);
                    getAudioPause(convertView).setVisibility(View.VISIBLE);
                    getAudioStop(convertView).setVisibility(View.VISIBLE);
                    getAudioProgress(convertView).setVisibility(View.VISIBLE);

                    getAudioProgress(convertView).setMax(mMediaPlayer.getDuration());

                    // Make sure you update Seekbar on UI thread
                    GroupChatWindow.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (mMediaPlayer != null) {
                                try {
                                    int prog = mMediaPlayer.getCurrentPosition();
                                    getAudioProgress(convertView).setProgress(prog);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            mHandler.postDelayed(this, 100);
                        }
                    });

                    getAudioProgress(convertView).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (mMediaPlayer != null && fromUser) {
                                mMediaPlayer.seekTo(progress * mMediaPlayer.getDuration());
                            }
                        }
                    });

                    getAudioPause(convertView).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (mMediaPlayer.isPlaying()) {
                                mMediaPlayer.pause();

                                getAudioStop(convertView).setVisibility(View.VISIBLE);
                                getAudioPause(convertView).setVisibility(View.GONE);
                                getAudioProgress(convertView).setVisibility(View.VISIBLE);

                                getAudioPlay(convertView).setVisibility(View.VISIBLE);
                            }
                        }
                    });

                    getAudioStop(convertView).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (mMediaPlayer.isPlaying()) {

                                mMediaPlayer.reset();
                                mMediaPlayer.release();

                                mMediaPlayer = null;

                                getAudioStop(convertView).setVisibility(View.GONE);
                                getAudioPause(convertView).setVisibility(View.GONE);
                                getAudioProgress(convertView).setVisibility(View.GONE);

                                getAudioPlay(convertView).setVisibility(View.VISIBLE);
                            }
                        }
                    });

                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        public void onCompletion(MediaPlayer mp) {

                            getAudioPlay(convertView).setVisibility(View.VISIBLE);
                            getAudioPause(convertView).setVisibility(View.GONE);
                            getAudioStop(convertView).setVisibility(View.GONE);
                            getAudioProgress(convertView).setVisibility(View.GONE);

                            mMediaPlayer = null;
                        }
                    });
                } else {
                    try {
                        int prog = mMediaPlayer.getCurrentPosition();
                        mMediaPlayer.seekTo(prog);
                        mMediaPlayer.start();

                        getAudioPlay(convertView).setVisibility(View.GONE);
                        getAudioPause(convertView).setVisibility(View.VISIBLE);
                        getAudioStop(convertView).setVisibility(View.VISIBLE);
                        getAudioProgress(convertView).setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return convertView;
    }


    LinearLayout getMediaPlayer(View convertView) {
        mediaPlayer = (LinearLayout) convertView
                .findViewById(R.id.mediaPlayer);
        return mediaPlayer;
    }

    ImageView getAudioStop(View convertView) {
        mediaAudioStop = (ImageView) convertView
                .findViewById(R.id.mediaAudioStop);
        return mediaAudioStop;
    }

    ImageView getAudioPlay(View convertView) {
        mediaAudioPlay = (ImageView) convertView
                .findViewById(R.id.mediaAudioPlay);
        return mediaAudioPlay;
    }

    ImageView getAudioPause(View convertView) {
        mediaAudioPause = (ImageView) convertView
                .findViewById(R.id.mediaAudioPause);
        return mediaAudioPause;
    }

//    TextView getAudioTitle(View convertView) {
//        if (audio_title == null) {
//            audio_title = (TextView) convertView
//                    .findViewById(R.id.audio_title);
//        }
//        return audio_title;
//    }

    SeekBar getAudioProgress(View convertView) {
        audio_player = (SeekBar) convertView
                .findViewById(R.id.audio_player);
        return audio_player;
    }

    public void addContactToAddressBook(ChatBody populate) {

        Utils.WritePhoneContact(populate.getcName(), populate.getcNumber() + "", this);
    }

    public void updateChatList() {

        chatListAdapter = new ChatListAdapter(populateChat);
        chatlist.setAdapter(chatListAdapter);
    }

    public void sendMessage() {
        if (RoosterConnectionService.getState().equals(XmppConnect.ConnectionState.CONNECTED)) {
            DialogUtility.showLOG("The client is connected to the server,Sendint Message");
            //Send the message to the server
            String msg = inputEditText.getText().toString();

//            try {
//                msg = URLEncoder.encode(msg, "UTF-8");
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }

            ChatBody chat = Utils.getChatBodyForChatType(msg.trim(), Utils.getMyJID(this), groupID,
                    PreferenceConstants.G_TEXT, PreferenceConstants.OUT_BOUND, PreferenceConstants.SENT,
                    new Message(), PreferenceConstants.MESSAGE_TYPE_GROUP_CHAT, Utils.getMyJID(this));

            chat.setContactName(groupInfo.getGroup_name());
            db.addChatToGroupTable(chat);

            Intent intent = new Intent(RoosterConnectionService.SEND_MESSAGE);
            //intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY,msg);
            intent.putExtra("CHAT", chat);
            //intent.putExtra(RoosterConnectionService.BUNDLE_TO, contactJid);
            sendBroadcast(intent);
            populateChat.add(chat);
            updateChatList();
            inputEditText.setText("");

        } else {
            Toast.makeText(getApplicationContext(),
                    "Client not connected to server ,Message not sent!",
                    Toast.LENGTH_LONG).show();
        }
    }


    public class FileDownloader extends Thread {

        private static final int DOWNLOAD_BUFFER_SIZE = 4096;

        String filePath = "";
        GroupChatWindow groupChatWindow;
        DonutProgress downloadProgress;
        int count = 0;
        int lenghtOfFile = 0;
        ChatBody populate;
        String mediaDirectory = PreferenceConstants.rootDirectory + File.separator +
                PreferenceConstants.subRootMediaDirectory + File.separator;

        public FileDownloader(GroupChatWindow groupChatWindow, ChatBody populate, DonutProgress downloadProgress) {
            System.out.println("<<-- Download Start");

            this.downloadProgress = downloadProgress;
            this.populate = populate;
            this.groupChatWindow = groupChatWindow;
        }


        /**
         * Connects to the URL of the file, begins the download, and notifies the
         * AndroidFileDownloader activity of changes in state. Writes the file to
         * the root of the SD card.
         */
        @Override
        public void run() {
            android.os.Message msg;
            try {

                String downloadUrl = populate.getUrl();
                String type = populate.getMessageType();

                if (type.contains(PreferenceConstants.G_IMAGE)) {
                    filePath = getFilename();
                } else if (type.equals(PreferenceConstants.G_VIDEO)) {
                    filePath = getVideoFilename();
                } else if (type.equals(PreferenceConstants.G_AUDIO)) {
                    filePath = getAudioFilename();
                }

                URL u = new URL(downloadUrl);

                FileOutputStream f = new FileOutputStream(new File(filePath));
                InputStream in;

//			HttpsURLConnection c = (HttpsURLConnection) u.openConnection();
//			javax.net.ssl.SSLSocketFactory sslSocketFactory = createSslSocketFactory();
//			c.setSSLSocketFactory(sslSocketFactory);
//			c.setHostnameVerifier(new AllowAllHostnameVerifier());

                HttpURLConnection c = (HttpURLConnection) u.openConnection();

                c.setUseCaches(false);

                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();

                lenghtOfFile = c.getContentLength();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        downloadProgress.setMax(100);
                        downloadProgress.setProgress(0);
                    }
                });

                in = c.getInputStream();


                byte[] buffer = new byte[1024];
                int len1 = 0;


                while ((len1 = in.read(buffer)) > 0) {
                    count += len1;
                    f.write(buffer, 0, len1);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int prog = (count * 100) / lenghtOfFile;
                            System.out.println("Progress : " + prog);

                            if (prog == 100) {
                                populate.setLocalUri(filePath);
                                db.updateFileInfo(populate);
                                groupChatWindow.fileDownloaded(populate);
                            }

                            downloadProgress.setProgress(prog);
                        }
                    });
                }


                f.flush();
                f.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private javax.net.ssl.SSLSocketFactory createSslSocketFactory()
                throws Exception {
            TrustManager[] byPassTrustManagers = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) {
                }
            }};
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
            return sslContext.getSocketFactory();
        }

        public String getFilename() {
            File file = new File(Environment.getExternalStorageDirectory()
                    .getPath(), mediaDirectory + PreferenceConstants.dirImages);
            if (!file.exists()) {
                file.mkdirs();
            }

            String uriSting = (file.getPath() + "/" + System.currentTimeMillis() + ".jpg");
            return uriSting;
        }

        public String getVideoFilename() {
            File file = new File(Environment.getExternalStorageDirectory()
                    .getPath(), mediaDirectory + PreferenceConstants.dirVideo);
            if (!file.exists()) {
                file.mkdirs();
            }

            String uriSting = (file.getPath() + "/" + System.currentTimeMillis() + ".mp4");
            return uriSting;
        }

        public String getAudioFilename() {
            File file = new File(Environment.getExternalStorageDirectory()
                    .getPath(), mediaDirectory + PreferenceConstants.dirAudio);
            if (!file.exists()) {
                file.mkdirs();
            }

            String uriSting = (file.getPath() + "/" + System.currentTimeMillis() + ".mp3");
            return uriSting;
        }
    }

    public void fileDownloaded(ChatBody populate) {
        DialogUtility.showToast(this, "File Downloaded");
        updateChatList();
    }
}