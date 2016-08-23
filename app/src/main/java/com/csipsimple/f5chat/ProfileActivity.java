package com.csipsimple.f5chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.csipsimple.R;
import com.csipsimple.f5chat.compressimage.ImageLoadingUtils;
import com.csipsimple.f5chat.parser.JSONParser;
import com.csipsimple.f5chat.utility.Chatutility;
import com.csipsimple.f5chat.utility.DialogUtility;
import com.csipsimple.f5chat.utility.PreferenceConstants;
import com.csipsimple.f5chat.utility.RoundedCornersDrawable;
import com.csipsimple.f5chat.utility.SharedPrefrence;
import com.csipsimple.f5chat.utility.Utils;
import com.csipsimple.ui.TabsViewPagerFragmentActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.FacebookSdk;
//import com.facebook.GraphRequest;
//import com.facebook.GraphResponse;
//import com.facebook.appevents.AppEventsLogger;
//import com.facebook.login.LoginResult;
//import com.facebook.login.widget.LoginButton;
//import com.google.android.gms.auth.api.Auth;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.auth.api.signin.GoogleSignInResult;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by SHRIG on 5/26/2016.
 */
public class ProfileActivity extends FragmentActivity implements View.OnClickListener{// GoogleApiClient.OnConnectionFailedListener {
    String encodedImage;
    //  Bitmap bm;
    com.csipsimple.f5chat.view.OpenRegularEditText entername;
    com.csipsimple.f5chat.font_family.Font_Welcome_scr_Tv_light next;
    ImageView iv_profile;
    ImageView fbImageView, googleimage;
  //  LoginButton fbLoginBTN;
    //SignInButton gPlusSignIn;
    //ProgressDialog pd;
    String localfilepath;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    LinearLayout back, linear_header;
    private Uri srcImageUri = null;
    ImageLoadingUtils utils;
    String accessToken, firstname, lastname;
//    private CallbackManager callbackManager;
//    private GoogleApiClient client;
//    private GoogleSignInOptions gso;
    private int RC_SIGN_IN = 100;
    String filename, name, profile;
    boolean isprofileset = false;
    Bitmap scaledbitmap = null;
    SharedPrefrence share;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//        client = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this /* OnConnectionFailedListener */)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();
//
//        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.profile_screen);

        linear_header = (LinearLayout) findViewById(R.id.linear_header);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Chatutility.changeStatusBarCustomColor(this, "#2c3342");
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//            params.setMargins(0, 20, 0, 0);
//            linear_header.setLayoutParams(params);

        } else {

        }
        //Chatutility.changeStatusBarColor(ProfileActivity.this);
        share = SharedPrefrence.getInstance(this);

        share.setValue(SharedPrefrence.FIRST_TIME, "true");

//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(getApplication());
//        callbackManager = CallbackManager.Factory.create();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        next = (com.csipsimple.f5chat.font_family.Font_Welcome_scr_Tv_light) findViewById(R.id.next);
        next.setOnClickListener(this);
        iv_profile = (ImageView) findViewById(R.id.iv_profile);

        back = (LinearLayout) findViewById(R.id.Back_arrow);
        back.setVisibility(View.INVISIBLE);

        entername = (com.csipsimple.f5chat.view.OpenRegularEditText) findViewById(R.id.entername);
        fbImageView = (ImageView) findViewById(R.id.facebook_img);
        fbImageView.setOnClickListener(this);
        googleimage = (ImageView) findViewById(R.id.googleimage);
        googleimage.setOnClickListener(this);

//        fbLoginBTN = (LoginButton) findViewById(R.id.fbLoginBtn);
//        fbLoginBTN.setReadPermissions(Arrays.asList(
//                "public_profile", "email", "user_birthday", "user_friends"));
//        fbLoginBTN.setOnClickListener(this);


        iv_profile.setOnClickListener(this);
        back.setOnClickListener(this);
        entername.setOnClickListener(this);
        entername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (entername.getText().toString().length() > 0) {
                    name = entername.getText().toString();
                    next.setEnabled(true);
                    next.setAlpha(1f);
                } else if (entername.getText().toString().length() == 0) {
                    next.setEnabled(false);
                    next.setAlpha(0.5f);
                }
            }
        });


    }


    public void finalimagepath(String path) {
        filename = path;
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Photos",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
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

    private void signIn() {
        //Creating an intent
//        pd.setMessage("loading...");
//        pd.show();
        DialogUtility.showProgressDialog(this, true, "Please wait...");
//        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(client);
//
//        //Starting intent for result
//        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            InputStream stream;
            String filePath = null;
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
                try {
                    localfilepath = data.getDataString();
                    if (localfilepath.contains("com.google.android.apps.photos")) {
                        Uri contentUri = Uri.parse(localfilepath);
                        String unusablePath = contentUri.getPath();
                        int startIndex = unusablePath.indexOf("external/");
                        int endIndex = unusablePath.indexOf("/ACTUAL");
                        String embeddedPath = unusablePath.substring(startIndex, endIndex);
                        Uri.Builder builder = contentUri.buildUpon();
                        builder.path(embeddedPath);
                        builder.authority("media");
                        Uri newUri = builder.build();
                        localfilepath = newUri.toString();
                    }

                    String converted_Path = Utils.getRealPathFromURI(ProfileActivity.this, localfilepath);
                    Uri srcimageuri = Uri.parse(localfilepath);


                    new CompressImage(srcimageuri, converted_Path).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CAMERA) {
                if (srcImageUri == null) {
                } else {
                    try {
                        String local_filePath = srcImageUri.getPath();
                        //      Uri srcimageuri = Uri.parse(localfilepath);
                        String converted_Path = Utils.getRealPathFromURI(ProfileActivity.this, local_filePath);
                        Uri srcimageuri = Uri.parse(local_filePath);
                        new CompressImage(srcimageuri, converted_Path).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }

       // callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //startActivity(new Intent(this, NumberSelection.class));
        }
        if (requestCode == RC_SIGN_IN) {
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            //Calling a new function to handle signin
//            handleSignInResult(result);
        }
        if (resultCode == PreferenceConstants.CONFIRM_READ_CONTACT) {
            finish();
            Intent allContactIntent = new Intent(ProfileActivity.this, TabsViewPagerFragmentActivity.class);
            allContactIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            allContactIntent.putExtra("EXIT", true);
            startActivity(allContactIntent);

        }
    }

//    private void handleSignInResult(GoogleSignInResult result) {
//        //If the login succeed
//        if (result.isSuccess()) {
//            DialogUtility.pauseProgressDialog();
//            //Getting google account
//            GoogleSignInAccount acct = result.getSignInAccount();
//            //Toast.makeText(this, "Login Success , Name :"+acct.getPhotoUrl(), Toast.LENGTH_LONG).show();
//            System.out.println("Google Pic  -> " + acct.getPhotoUrl());
//            String googlename = acct.getDisplayName();
//            name = acct.getDisplayName();
//            entername.setText(googlename);
//
//            try {
//                Uri picUri = acct.getPhotoUrl();
//                String googleimage = picUri.toString();
//                new DownloadImagesTask().execute(googleimage);
//                System.out.println("Google Pic URI -> " + picUri.getPath());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            //Displaying name and email
////            textViewName.setText(acct.getDisplayName());
////            textViewEmail.setText(acct.getEmail());
//
//            //Initializing image loader
////            imageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext())
////                    .getImageLoader();
//
////            imageLoader.get(acct.getPhotoUrl().toString(),
////                    ImageLoader.getImageListener(profilePhoto,
////                            R.mipmap.ic_launcher,
////                            R.mipmap.ic_launcher));
//
//            //Loading image
////            profilePhoto.setImageUrl(acct.getPhotoUrl().toString(), imageLoader);
//
//
////            String googleimageurl=acct.getPhotoUrl().toString();
////            new DownloadImagesTask().execute(googleimageurl);
//        } else {
//            //If login fails
//            DialogUtility.pauseProgressDialog();
//            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
//        }
//    }


    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);
        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);
        //iv_addphoto.setImageBitmap(bm);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.facebook_img:
//                fbLoginBTN.performClick();
//                break;
            case R.id.iv_profile:
                selectImage();
                break;
            case R.id.Back_arrow:
                finish();
                break;
            case R.id.entername:
                entername.setCursorVisible(true);
                break;
//            case R.id.fbLoginBtn:
//                //fbLogin();
//                break;
            case R.id.googleimage:
                signIn();
                break;
            case R.id.next:
                uploadData();
                break;

        }

    }

    public void uploadData() {
//        next.setAlpha(1f);
//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
        DialogUtility.showProgressDialog(this, true, "Processing...");
        new uploadData().execute();
//
//            }
//        });
    }

    public void sendProfileData() {
        try {
            ImageUtility iu = new ImageUtility(PreferenceConstants.UPDATE_PROFILE);
            iu.connectForMultipart();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                if (scaledbitmap != null) {
                    scaledbitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
                    isprofileset = true;
                } else {
                    isprofileset = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            iu.addFilePart("profle_pic", filename, baos.toByteArray());
            iu.addFormPart("user_id", Utils.getMyJidWithoutIP(this));
            iu.addFormPart("name", name);
            iu.addFormPart("is_file", isprofileset + "");
            iu.finishMultipart();
            String data = iu.getResponse();
            updateProfileResponse(data);
            //   System.out.println("<-- data");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateProfileResponse(String data) {
        JSONParser jsonPraser = new JSONParser(this, data);
        if (jsonPraser.STATUS.equals(PreferenceConstants.PASS_STATUS)) {

            share.setValue(SharedPrefrence.NAME, data);
            Intent incontact = new Intent(ProfileActivity.this, ConfirmContactDialog.class);
            startActivityForResult(incontact, PreferenceConstants.CONFIRM_READ_CONTACT);
        } else {
            DialogUtility.showToast(this, getString(R.string.try_later));
        }
    }


//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        Log.e("G+ Connection Failed", connectionResult.getErrorMessage());
//    }


    /*@Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_profile:
               // selectImage();
                break;
            case R.id.Back_arrow:
                finish();
                break;
        }

    }*/

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
//            String thumb = Base64.encodeBytes(Utils.getBytesFromBitmap(ThumbnailUtils.extractThumbnail(scaledbitmap, 240, 240)));
//            encodedImage = thumb;

        }
    }


    public class uploadData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            sendProfileData();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            DialogUtility.pauseProgressDialog();
            super.onPostExecute(result);
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
        utils = new ImageLoadingUtils(ProfileActivity.this);
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


//    public void fbLogin() {
//        fbLoginBTN.registerCallback(callbackManager,
//                new FacebookCallback<LoginResult>()
//
//                {
//                    @Override
//                    public void onSuccess(LoginResult loginResult) {
//
//                        System.out.println("onSuccess");
//
//                        accessToken = loginResult.getAccessToken()
//                                .getToken();
//                        Log.i("accessToken", accessToken);
//
//                        GraphRequest request = GraphRequest.newMeRequest(
//                                loginResult.getAccessToken(),
//
//                                new GraphRequest.GraphJSONObjectCallback() {
//                                    @Override
//                                    public void onCompleted(JSONObject object,
//                                                            GraphResponse response) {
//
//
//                                        Log.i("LoginActivity",
//                                                response.toString());
//
//                                        getFacebookData(object);
//
//                                    }
//                                });
//                        Bundle parameters = new Bundle();
//                        parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location");
//                        request.setParameters(parameters);
//                        request.executeAsync();
//
//
//                    }
//
//                    @Override
//                    public void onCancel() {
//
//                        System.out.println("onCancel");
//                    }
//
//                    @Override
//                    public void onError(FacebookException exception) {
//                        System.out.println("onError");
//
//                        exception.printStackTrace();
//                        //Log.v("Login", exception.getCause().toString());
//                    }
//                }
//
//        );
//
//    }

    private Bundle getFacebookData(JSONObject object) {

        Bundle bundle = new Bundle();
        try {
            String id = null;
            id = object.getString("id");


            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                String url = "https://graph.facebook.com/" + id + "/picture?width=200&height=150";
                finalimagepath(url);
                new DownloadImagesTask().execute(url);
                Log.e("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            Log.e("idFacebook", id);

            if (object.has("first_name")) {
                Log.e("first_name", object.getString("first_name"));
                firstname = object.getString("first_name");

                bundle.putString("first_name", object.getString("first_name"));
            }
            if (object.has("last_name")) {
                Log.e("last_name", object.getString("last_name"));
                lastname = object.getString("last_name");
                bundle.putString("last_name", object.getString("last_name"));

            }
            if (object.has("email")) {
                Log.e("email", object.getString("email"));
                bundle.putString("email", object.getString("email"));

            }
            if (object.has("gender")) {
                Log.e("gender", object.getString("gender"));
                bundle.putString("gender", object.getString("gender"));

            }
            if (object.has("birthday")) {
                Log.e("birthday", object.getString("birthday"));
                bundle.putString("birthday", object.getString("birthday"));

            }
            if (object.has("location")) {
                Log.e("location", object.getJSONObject("location").getString("name"));
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        name = firstname + " " + lastname;
        entername.setText(name);

        return bundle;
    }

    //asynctast to get image from the facebook id
    public class DownloadImagesTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            return download_Image(urls[0]);
        }

        @Override
        protected void onPreExecute() {
            DialogUtility.showProgressDialog(ProfileActivity.this, true, "Processing....");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            DialogUtility.pauseProgressDialog();
            iv_profile.setImageBitmap(result);              // how do I pass a reference to mChart here ?
        }


        private Bitmap download_Image(String url) {
            //---------------------------------------------------
            Bitmap bm = null;
            try {
                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {
                Log.e("Hub", "Error getting the image from server : " + e.getMessage().toString());
            }
            return bm;
            //---------------------------------------------------
        }


    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
