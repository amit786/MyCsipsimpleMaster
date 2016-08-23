package com.csipsimple.f5chat.group;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.csipsimple.R;
import com.csipsimple.f5chat.ImageUtility;
import com.csipsimple.f5chat.bean.Contact;
import com.csipsimple.f5chat.bean.Group;
import com.csipsimple.f5chat.bean.GroupMember;
import com.csipsimple.f5chat.bean.GroupNotify;
import com.csipsimple.f5chat.compressimage.ImageLoadingUtils;
import com.csipsimple.f5chat.http.HTTPPost;
import com.csipsimple.f5chat.parser.JSONParser;
import com.csipsimple.f5chat.utility.DialogUtility;
import com.csipsimple.f5chat.utility.PreferenceConstants;
import com.csipsimple.f5chat.utility.RoundedCornersDrawable;
import com.csipsimple.f5chat.utility.SharedPrefrence;
import com.csipsimple.f5chat.utility.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



/**
 * Created by Administrator on 7/22/2016.
 */
public class GroupDetail extends Activity implements View.OnClickListener {


    private RelativeLayout exit_group;
    private ImageView edit_profile, iv_profile, clearFiled, addParticipant;
    private EditText et_groupName;

    private ImageLoadingUtils utils;
    private Bitmap scaledbitmap = null;
    // String gName, gImage,
    String groupId;
    Map<String, Integer> mapIndex;
    SharedPrefrence share;
    ListView F5ContactsList;
    groupAdapter groupAdapter;

    LinearLayout layout_done, addpartview, back_arrow;
    TextView tv_group_name_limit, tv_change_group_image, tv_group_creation_detail;
    String group_name, group_jid, creation_date, time_stamp, admin_jid, group_image_url;
    ArrayList<GroupMember> groupMember = new ArrayList<GroupMember>();
    String members = "", filename;
    private Uri srcImageUri = null;
    private int preLast;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    TextView participantsCount;
    int participantLimit = 78;

    Group groupInfo = null;
    int deletePosition = 0;
    String deleteMemberJid = "";

    GroupNotify groupNotify;

    private BroadcastReceiver updateGroup;

    static class ViewHolder {
        private TextView groupName, groupAdmin;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ui_group_detail);

        share = SharedPrefrence.getInstance(this);

        groupNotify = new GroupNotify();

        back_arrow = (LinearLayout) findViewById(R.id.back_arrow);
        layout_done = (LinearLayout) findViewById(R.id.layout_done);
//        btn_addMembers = (Button) findViewById(R.id.btn_addMembers);
//        btn_removeMembers = (Button) findViewById(R.id.btn_removeMembers);
        addpartview = (LinearLayout) findViewById(R.id.addpartview);
        participantsCount = (TextView) findViewById(R.id.participantsCount);
        exit_group = (RelativeLayout) findViewById(R.id.exit_group);
        edit_profile = (ImageView) findViewById(R.id.edit_profile);
        addParticipant = (ImageView) findViewById(R.id.addParticipant);
        iv_profile = (ImageView) findViewById(R.id.groupImage);
        clearFiled = (ImageView) findViewById(R.id.clearFiled);
        et_groupName = (EditText) findViewById(R.id.groupName);
        tv_group_name_limit = (TextView) findViewById(R.id.tv_group_name_limit);
        tv_change_group_image = (TextView) findViewById(R.id.tv_change_group_image);
        tv_group_creation_detail = (TextView) findViewById(R.id.tv_group_creation_detail);
        F5ContactsList = (ListView) findViewById(R.id.participantsList);

        groupId = getIntent().getExtras().getString(PreferenceConstants.user_jid);
        groupInfo = getMyGroup(groupId);

        groupDetail(groupInfo);

        edit_profile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addpartview.setVisibility(View.GONE);
                edit_profile.setVisibility(View.GONE);
                layout_done.setVisibility(View.VISIBLE);
                tv_group_name_limit.setVisibility(View.VISIBLE);
                tv_change_group_image.setVisibility(View.VISIBLE);
                et_groupName.setEnabled(true);
                et_groupName.setSelection(et_groupName.getText().length());
                clearFiled.setVisibility(View.VISIBLE);

//                editProfile();
            }
        });

        exit_group.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteMemberJid = share.getValue(SharedPrefrence.JID);
                deleteFromGroup();
            }
        });

        addParticipant.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addMember();
            }
        });

        clearFiled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                et_groupName.setText(group_name);
                addpartview.setVisibility(View.VISIBLE);
                edit_profile.setVisibility(View.VISIBLE);
                layout_done.setVisibility(View.GONE);
                tv_group_name_limit.setVisibility(View.GONE);
                tv_change_group_image.setVisibility(View.GONE);
                clearFiled.setVisibility(View.GONE);
                et_groupName.setEnabled(false);
            }
        });

        layout_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addpartview.setVisibility(View.VISIBLE);
                edit_profile.setVisibility(View.VISIBLE);
                layout_done.setVisibility(View.GONE);
                tv_group_name_limit.setVisibility(View.GONE);
                tv_change_group_image.setVisibility(View.GONE);
                et_groupName.setEnabled(false);

                updateGroup(group_name);
            }
        });

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        F5ContactsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deletePosition = position;
                deleteMemberJid = groupMember.get(position).getMember_jid();
                if (!deleteMemberJid.equals(share.getValue(SharedPrefrence.JID))) {
                    participantOptions();
                }
                return false;
            }
        });

        F5ContactsList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                // Make your calculation stuff here. You have all your
                // needed info from the parameters of this function.

                // Sample calculation to determine if the last
                // item is fully visible.
                final int lastItem = firstVisibleItem + visibleItemCount;
                System.out.println("Last : " + lastItem + " , totalItemCount : " + totalItemCount);
                if (lastItem == totalItemCount) {
                    if (preLast != lastItem) { //to avoid multiple calls for last item
                        DialogUtility.showLOG("Last");
                        preLast = lastItem;
                    }

//                    exit_group.setVisibility(View.VISIBLE);
                } else {

                    exit_group.setVisibility(View.GONE);
                }
            }
        });

        et_groupName.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    tv_group_name_limit.setText(25 - s.toString().length()
                            + " characters");

                    if ((25 - s.toString().length()) < 0) {
                        String str = et_groupName.getText().toString();
                        str = str.substring(0, str.length() - 1);
                        et_groupName.setText(str);
                        et_groupName.setSelection(str.length());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        iv_profile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    public Group getMyGroup(String JID)
    {
        HashMap<String, Group> groupHashMap = share.getGroup(SharedPrefrence.GROUP_LST);
        Group group= groupHashMap.get(JID);
        return group;
    }

    public void updateGroupObj(String JID)
    {
        HashMap<String, Group> groupHashMap = share.getGroup(SharedPrefrence.GROUP_LST);
        groupHashMap.put(JID,groupInfo);
        share.setGroupLst(SharedPrefrence.GROUP_LST, groupHashMap);
    }



    protected void participantOptions() {

        final CharSequence[] items = {"Remove From Group"};
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupDetail.this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    deleteFromGroup();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void deleteFromGroup() {
        if (Utils.checkInternetConn(this)) {
            {
                DialogUtility.showProgressDialog(this, true, "Processing...");

                try {
                    new HTTPPost(GroupDetail.this, deleteMemberPrameters(deleteMemberJid)).execute(PreferenceConstants.API_DELETE_MEMBER, PreferenceConstants.EXIT_FROM_GROUP);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Utils.showAlertDialog(this, getString(R.string.alert), getString(R.string.checknet));
        }
    }

    protected void updateGroup(String groupName) {
        if (et_groupName.getText().length() > 0) {
            DialogUtility.showProgressDialog(this,true,"Processing...");
            new updateGrpData().execute(groupName);
        } else {
            Toast.makeText(GroupDetail.this, "Group name should not be empty.", Toast.LENGTH_SHORT).show();
        }
    }

    public class updateGrpData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            sendUpdateGroupData(params[0]);

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            DialogUtility.pauseProgressDialog();
            super.onPostExecute(result);
        }
    }

    public void sendUpdateGroupData(String groupName) {
        try {
            ImageUtility iu = new ImageUtility(PreferenceConstants.API_UPDATE_GROUP);
            iu.connectForMultipart();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                if (scaledbitmap != null) {
                    scaledbitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            iu.addFormPart(PreferenceConstants.GroupName, et_groupName.getText().toString());
            iu.addFormPart(PreferenceConstants.GroupJidWithoutIp, Utils.getJIDWithoutIP(group_jid));
            iu.addFilePart(PreferenceConstants.GroupImage, filename, baos.toByteArray());

            iu.finishMultipart();
            String data = iu.getResponse();

            GetGroupUpdatedResponse(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GetGroupUpdatedResponse(String parseRes) {
        DialogUtility.pauseProgressDialog();

        JSONParser parser = new JSONParser(this, parseRes);
        if (parser.STATUS.equals(PreferenceConstants.PASS_STATUS))
        {
            String group_image_url = JSONParser.getJsonString(parser.jObj, "group_image_url");

            addpartview.setVisibility(View.VISIBLE);
            edit_profile.setVisibility(View.VISIBLE);
            layout_done.setVisibility(View.GONE);
            tv_group_name_limit.setVisibility(View.GONE);
            tv_change_group_image.setVisibility(View.GONE);
            et_groupName.setEnabled(false);
            groupInfo.setGroup_name(et_groupName.getText().toString());
            groupInfo.setGroup_image_url(group_image_url);
            updateGroupObj(groupId);

            groupNotify.setGroupUpdateBy(share.getValue(SharedPrefrence.JID));
            groupNotify.setGroupSubject(PreferenceConstants.GN_UPDATE_GROUP);
            notifyGroupUsers();

            finish();
        }
        else
        {

        }
    }

    public void notifyGroupUsers() {
        if (groupInfo != null)
        {
            groupNotify.setGroupAdmin(groupInfo.getAdmin_jid());
            groupNotify.setGroupImageUrl(groupInfo.getGroup_image_url());
            groupNotify.setGroupJID(groupInfo.getGroup_jid());
            groupNotify.setGroupName(groupInfo.getGroup_name());

            com.csipsimple.f5chat.rooster.XmppConnect.getInstance().sendGroupNotify(groupNotify);
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Photos",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(GroupDetail.this);
        //builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    srcImageUri = Uri.fromFile(Utils.getOutputMediaFile());
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, srcImageUri);
                        startActivityForResult(intent, REQUEST_CAMERA);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (items[item].equals("Choose from Photos")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }


    public class CompressImage extends AsyncTask<String, String, String> {

        private Uri srcImageUri;
        private String local_filePath;


        public CompressImage(Uri srcImageUri, String local_filePath) {
            this.srcImageUri = srcImageUri;
            this.local_filePath = local_filePath;
        }

        @Override
        protected String doInBackground(String... params) {
            scaledbitmap = compressImage(srcImageUri, local_filePath);
            System.out.println("<---scalbitmap " + scaledbitmap);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            RoundedCornersDrawable drawable = new RoundedCornersDrawable(getResources(), scaledbitmap);
            iv_profile.setImageDrawable(drawable);
        }
    }


    public Bitmap compressImage(Uri imageUri, String localfilepath) {
        String filePath;
        filePath = Utils.getRealPathFromURI(getApplicationContext(), imageUri.toString());
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }
        utils = new ImageLoadingUtils(GroupDetail.this);
        options.inSampleSize = utils.calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 4.0f;
        float middleY = actualHeight / 4.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 4, middleY - bmp.getHeight() / 4, new Paint(Paint.FILTER_BITMAP_FLAG));


        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            DialogUtility.showLOG("Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                DialogUtility.showLOG("Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                DialogUtility.showLOG("Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                DialogUtility.showLOG("Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        filename = localfilepath;
        try {
            out = new FileOutputStream(filename);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        finalimagepath(filename);
        return scaledBitmap;
    }


    public void finalimagepath(String path) {
        filename = path;
    }


    public void addMember() {

        Intent intent = new Intent(GroupDetail.this, AddParticipants.class);
        intent.putExtra(PreferenceConstants.TYPE, PreferenceConstants.addMoreParticipants);
        intent.putExtra(PreferenceConstants.GroupJidWithoutIp, group_jid);
        startActivity(intent);
    }

    public void deleteMemberResponse(String msg,boolean check) {
        DialogUtility.pauseProgressDialog();
        DialogUtility.showToast(GroupDetail.this, msg);
        if(check)
        {
            if (!deleteMemberJid.equals(share.getValue(SharedPrefrence.JID))) {
                groupMember.remove(deletePosition);
                groupInfo.setGroupMember(groupMember);
                updateGroupObj(groupId);
                groupAdapter = new groupAdapter(groupMember);
                F5ContactsList.setAdapter(groupAdapter);

                groupNotify.setMembersJID(deleteMemberJid);
                groupNotify.setGroupSubject(PreferenceConstants.GN_DELETE_FROM_GROUP);
                notifyGroupUsers();
            }
        }
    }

    private List<NameValuePair> deleteMemberPrameters(String jid) {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        nameValuePair.add(new BasicNameValuePair("group_jid_without_ip", getGroupIdWithoutIP(groupId)));
        nameValuePair.add(new BasicNameValuePair("member_jid", Utils.getJIDWithoutIP(jid)));
        nameValuePair.add(new BasicNameValuePair("admin_jid_without_ip ", Utils.getJIDWithoutIP(admin_jid)));
        return nameValuePair;
    }

    private List<NameValuePair> getGroupDetailPrameters() {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        nameValuePair.add(new BasicNameValuePair("group_jid_without_ip", getGroupIdWithoutIP(groupId)));
        return nameValuePair;
    }

    public String getGroupIdWithoutIP(String groupJId) {
        return groupJId.replace("@broadcast." + PreferenceConstants.SERVER, "");
    }



    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
        getGroupDetail();
        groupInfo = getMyGroup(groupId);
        groupDetail(groupInfo);
        updateGroup = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                groupInfo = getMyGroup(groupId);
                groupDetail(groupInfo);
            }
        };
        IntentFilter filter = new IntentFilter(com.csipsimple.f5chat.rooster.RoosterConnectionService.GROUP_UPDATE);
        registerReceiver(updateGroup, filter);

    }
    public void checkConnection() {
        Intent i1 = new Intent(this, com.csipsimple.f5chat.rooster.RoosterConnectionService.class);
        startService(i1);
    }
    public void getGroupDetail()
    {
        if (Utils.checkInternetConn(GroupDetail.this)) {
            {
                try {
                    DialogUtility.showProgressDialog(this,true,"Please wait");
                    new HTTPPost(GroupDetail.this, getGroupDetailPrameters()).execute(PreferenceConstants.API_GET_GROUP_DETAIL, PreferenceConstants.GET_GROUP_DETAIL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Utils.showAlertDialog(this, getString(R.string.alert), getString(R.string.checknet));
        }
    }
    public void updateView()
    {
        DialogUtility.pauseProgressDialog();
        groupInfo = getMyGroup(groupId);
        groupDetail(groupInfo);
    }

    public void groupDetail(Group group) {

        if (group != null) {

            group_name = group.getGroup_name();
            group_jid = group.getGroup_jid();
            creation_date = group.getCreation_date();
            time_stamp = group.getTime_stamp();
            admin_jid = group.getAdmin_jid();
            group_image_url = group.getGroup_image_url();

            groupMember = group.getGroupMember();

            String creation = "Created by " + Utils.getContactNameByNumber(GroupDetail.this, admin_jid) + ", " + creation_date;
            tv_group_creation_detail.setText(creation);

            try {
                et_groupName.setText(group_name);

                groupAdapter = new groupAdapter(groupMember);
                F5ContactsList.setAdapter(groupAdapter);

                if (participantLimit >= groupMember.size()) {
                    participantsCount.setText(groupMember.size() + "/" + participantLimit);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    public void onClick(View view) {
        TextView selectedIndex = (TextView) view;
        F5ContactsList.setSelection(mapIndex.get(selectedIndex.getText().toString()));
        Log.e("index size--", mapIndex.get(selectedIndex.getText()) + "");
    }

    class groupAdapter extends BaseAdapter {
        ArrayList<GroupMember> tempF5list = new ArrayList<GroupMember>();

        public groupAdapter(ArrayList<GroupMember> list) {
            this.tempF5list.addAll(list);
        }

        @Override
        public int getCount() {
            return tempF5list.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater f5inflater = GroupDetail.this.getLayoutInflater();
                convertView = f5inflater.inflate(R.layout.ui_group_item_list, null);
                holder = new ViewHolder();
                holder.groupName = (TextView) convertView.findViewById(R.id.groupName);
                holder.groupAdmin = (TextView) convertView.findViewById(R.id.groupAdmin);
                Log.e("name--", holder.groupName + "--");
                ImageView groupImg = (ImageView) convertView.findViewById(R.id.groupImg);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            GroupMember contactbeanobject = tempF5list.get(position);

            String memberJid = contactbeanobject.getMember_jid();
            if (memberJid.equalsIgnoreCase(admin_jid)) {
                holder.groupAdmin.setVisibility(View.VISIBLE);
            } else {
                holder.groupAdmin.setVisibility(View.GONE);
            }

            if (!members.contains(memberJid)) {
                members = members + " " + memberJid;
            }

            String participantName = "You";
            if (!memberJid.contains(Utils.getMyJidWithoutIP(GroupDetail.this))) {
                participantName = Utils.getContactNameByNumber(GroupDetail.this, memberJid);
            }

            holder.groupName.setText(participantName);

            return convertView;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(updateGroup);
    }
}


