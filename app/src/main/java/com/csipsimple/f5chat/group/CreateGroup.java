package com.csipsimple.f5chat.group;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
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
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.csipsimple.R;
import com.csipsimple.f5chat.compressimage.ImageLoadingUtils;
import com.csipsimple.f5chat.utility.DialogUtility;
import com.csipsimple.f5chat.utility.PreferenceConstants;
import com.csipsimple.f5chat.utility.RoundedCornersDrawable;
import com.csipsimple.f5chat.utility.SharedPrefrence;
import com.csipsimple.f5chat.utility.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by User on 7/19/2016.
 */
public class CreateGroup extends Activity {

    private EditText et_group_name;
    private TextView tv_group_name_limit;
    private Button btn_create;
    private ImageView iv_profile;
    private ProgressDialog pDialog;
    private LinearLayout next_layout, Back_arrow;

    private SharedPrefrence share;
    private Uri srcImageUri = null;
    private ImageLoadingUtils utils;
    private Bitmap scaledbitmap = null;
    private String localfilepath, groupName, parameters, filename;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String image_path;
    Bitmap scaledBitmap = null;
    Uri uri = null;
    private ExecutorService mExecutor;

    private TextView headerTitle;
    String GroupJidWithoutIp, GroupName, GroupImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_create_group);

        next_layout = (LinearLayout) findViewById(R.id.next_layout);
        Back_arrow = (LinearLayout) findViewById(R.id.Back_arrow);
        headerTitle = (TextView) findViewById(R.id.headerTitle);
        iv_profile = (ImageView) findViewById(R.id.iv_profile);
        et_group_name = (EditText) findViewById(R.id.et_group_name);
        tv_group_name_limit = (TextView) findViewById(R.id.tv_group_name_limit);
        btn_create = (Button) findViewById(R.id.btn_create);

        share = SharedPrefrence.getInstance(CreateGroup.this);

        iv_profile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        next_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                performAction();
            }
        });

        btn_create.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                performAction();
            }
        });

        Back_arrow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        et_group_name.addTextChangedListener(new TextWatcher() {

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
                        String str = et_group_name.getText().toString();
                        str = str.substring(0, str.length() - 1);
                        et_group_name.setText(str);
                        et_group_name.setSelection(str.length());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void performAction() {
        try {
            if (et_group_name.getText().toString().length() > 0) {
                groupName = et_group_name.getText().toString();
                    createGroup(groupName);

            } else {
                Toast.makeText(CreateGroup.this, "Please enter the group name", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void createGroup(String groupName) {

        Intent intent = new Intent(CreateGroup.this, AddParticipants.class);
        intent.putExtra(PreferenceConstants.TYPE, PreferenceConstants.addParticipants);
        intent.putExtra(PreferenceConstants.group_image, filename);
        intent.putExtra(PreferenceConstants.group_name, groupName);
        startActivity(intent);

//        new createGrpData().execute();
    }

    private void onSelectFromGalleryResult(Intent data) {
        //Uri selectedImageUri = data.getData();
        uri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        image_path = cursor.getString(column_index);
//        Bitmap bm;
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(selectedImagePath, options);
//        final int REQUIRED_SIZE = 200;
//        int scale = 1;
//        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
//                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
//            scale *= 2;
//        options.inSampleSize = scale;
//        options.inJustDecodeBounds = false;
//        bm = BitmapFactory.decodeFile(selectedImagePath, options);
        //iv_addphoto.setImageBitmap(bm);
     //   openCrop();
    }

    private void onCaptureImage(Intent data) {
        try {
//        uri = data.getData(); //srcImageUri

        uri = srcImageUri;
        String thumbString = Utils.getImageThumb(uri.getPath());
        scaledBitmap = Utils.decodeBase64(thumbString);

//        scaledBitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        uri = Uri.fromFile(destination);

        image_path = destination.toString();
        FileOutputStream fo;

            destination.createNewFile();
            fo = new FileOutputStream(destination);
            System.out.println("destination " + destination.toString());
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //groupImg.setImageBitmap(scaledBitmap);
      //  openCrop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            }
            else if (requestCode == REQUEST_CAMERA) {
                onCaptureImage(data);
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (PreferenceConstants.uri != null) {
            mExecutor = Executors.newSingleThreadExecutor();
            final Uri uri = PreferenceConstants.uri;
            mExecutor.submit(new LoadScaledImageTask(this, uri, iv_profile, calcImageSize()));
        }
    }

    @Override
    protected void onDestroy() {
        if (PreferenceConstants.uri != null)
            mExecutor.shutdown();
        super.onDestroy();
    }
//    private void openCrop() {
//        Intent in = new Intent(CreateGroup.this, com.csipsimple.f5chat.group.MainActivity.class);
//        in.putExtra("uri", uri.toString());
//        startActivity(in);
//        //finish();
//    }
    public boolean isLargeImage(Bitmap bm) {
        return bm.getWidth() > 2048 || bm.getHeight() > 2048;
    }

    public static class LoadScaledImageTask implements Runnable {
        private Handler mHandler = new Handler(Looper.getMainLooper());
        Context context;
        Uri uri;
        ImageView imageView;
        int width;

        public LoadScaledImageTask(Context context, Uri uri, ImageView imageView, int width) {
            this.context = context;
            this.uri = uri;
            this.imageView = imageView;
            this.width = width;
        }

        @Override
        public void run() {
            final int exifRotation = com.isseiaoki.simplecropview.util.Utils.getExifOrientation(context, uri);
            int maxSize = com.isseiaoki.simplecropview.util.Utils.getMaxSize();
            int requestSize = Math.min(width, maxSize);
            try {
                final Bitmap sampledBitmap = com.isseiaoki.simplecropview.util.Utils.decodeSampledBitmapFromUri(context, uri, requestSize);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageMatrix(com.isseiaoki.simplecropview.util.Utils.getMatrixFromExifOrientation(exifRotation));
                        imageView.setImageBitmap(sampledBitmap);
                    }
                });
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int calcImageSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        return Math.min(Math.max(metrics.widthPixels, metrics.heightPixels), 2048);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // TODO Auto-generated method stub
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK)
//        {
//            InputStream stream;
//            String filePath = null;
//            if (requestCode == SELECT_FILE) {
//
//                onSelectFromGalleryResult(data);
//                try {
//                    localfilepath = data.getDataString();
//                    if (localfilepath.contains("com.google.android.apps.photos")) {
//                        Uri contentUri = Uri.parse(localfilepath);
//                        String unusablePath = contentUri.getPath();
//                        int startIndex = unusablePath.indexOf("external/");
//                        int endIndex = unusablePath.indexOf("/ACTUAL");
//                        String embeddedPath = unusablePath.substring(startIndex, endIndex);
//                        Uri.Builder builder = contentUri.buildUpon();
//                        builder.path(embeddedPath);
//                        builder.authority("media");
//                        Uri newUri = builder.build();
//                        localfilepath = newUri.toString();
//                    }
//
//                    String converted_Path = Utils.getRealPathFromURI(CreateGroup.this, localfilepath);
//                    Uri srcimageuri = Uri.parse(localfilepath);
//
//
//                    new CompressImage(srcimageuri, converted_Path).execute();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } else if (requestCode == REQUEST_CAMERA) {
//                if (srcImageUri == null) {
//                } else {
//                    try {
//                        String local_filePath = srcImageUri.getPath();
//                        //      Uri srcimageuri = Uri.parse(localfilepath);
//                        String converted_Path = Utils.getRealPathFromURI(CreateGroup.this, local_filePath);
//                        Uri srcimageuri = Uri.parse(local_filePath);
//                        new CompressImage(srcimageuri, converted_Path).execute();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//        }
//    }
//

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Photos",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroup.this);
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
        utils = new ImageLoadingUtils(CreateGroup.this);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
