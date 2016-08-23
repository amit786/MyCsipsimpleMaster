package com.csipsimple.f5chat.http;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.csipsimple.f5chat.bean.ChatBody;
import com.csipsimple.f5chat.database.DatabaseHandler;
import com.csipsimple.f5chat.parser.JSONParser;
import com.csipsimple.f5chat.rooster.RoosterConnectionService;
import com.csipsimple.f5chat.utility.PreferenceConstants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class UploadFileToServer extends AsyncTask<String, String, String> {

    DatabaseHandler db;

    String responseString = "";

    Context ctx;
    File F5ChatFile = null;
    ChatBody chatBody;

    public UploadFileToServer(Context context, ChatBody chatBody) {

        this.ctx = context;
        this.chatBody = chatBody;
        db = new DatabaseHandler(ctx);
    }

    @Override
    protected String doInBackground(String... params)
    {
        responseString = uploadFile(chatBody.getLocalUri());
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        Intent intent = new Intent(RoosterConnectionService.SEND_MESSAGE);
        //intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY,msg);
        intent.putExtra("CHAT", chatBody);
        //intent.putExtra(RoosterConnectionService.BUNDLE_TO, contactJid);
        ctx.sendBroadcast(intent);
    }


    @SuppressLint("TrulyRandom")
    private javax.net.ssl.SSLSocketFactory createSslSocketFactory() throws Exception {
        TrustManager[] byPassTrustManagers = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        }};
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, byPassTrustManagers, new SecureRandom());
        return sslContext.getSocketFactory();
    }

    @SuppressWarnings({"unused", "deprecation"})
    public String uploadFile(String sourceFileUri) {

        try {
            File actualFile = new File(sourceFileUri);

            if (actualFile.exists()) {
                String ext = getFileExtension(actualFile);
                F5ChatFile = getFilename(ext);
                copy(actualFile, F5ChatFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        DataInputStream inStream;
        String fileName = sourceFileUri;
        HttpsURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        // int maxBufferSize = 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            return "Fail";
        } else {
            try {
                HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, new X509TrustManager[]{new NullX509TrustManager()}, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());

                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(PreferenceConstants.API_UPLOAD_FILE);

                // Open a HTTP connection to the URL
                conn = (HttpsURLConnection) url.openConnection();

                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
//                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                conn.setRequestProperty("file", fileName);
                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=file;filename=" + fileName + "" + lineEnd);
                dos.writeBytes(lineEnd);
                // create a buffer of maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);

                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                int downloadedSize = 0;


                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);

                    downloadedSize += bufferSize;
                    // publishProgress((int)(downloadedSize * 100 /
                    // fileSize));
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                fileInputStream.close();
                dos.flush();
                dos.close();

                // Responses from the server (code and message)
                // ------------------ read the SERVER RESPONSE
                try {
                    inStream = new DataInputStream(conn.getInputStream());
                    responseString = inStream.readLine();
                    inStream.close();
                    JSONParser parser = new JSONParser(ctx, responseString);
                    responseString = parser.getUrlFromResponse();

                    chatBody.setProgress("100");
                    chatBody.setStatus(PreferenceConstants.fileUploded);
                    chatBody.setUrl(responseString);
                    chatBody.setLocalUri(F5ChatFile.getAbsolutePath());
                    db.updateFileInfo(chatBody);

                } catch (Exception ioex) {
                    Log.e("Debug", "error: " + ioex.getMessage(), ioex);
                }
                // close the streams //

            } catch (Exception e) {
                e.printStackTrace();
            }

            return responseString;
        }
    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public File getFilename(String ext) {

        String filePath = PreferenceConstants.rootDirectory + File.separator +
                PreferenceConstants.subRootMediaDirectory + File.separator;
        String fileName = "";

        if (chatBody.getMessageType().equals(PreferenceConstants.IMAGE) ||
                chatBody.getMessageType().equals(PreferenceConstants.G_IMAGE)) {
            filePath = filePath + PreferenceConstants.dirImages + File.separator + PreferenceConstants.dirSent;
            fileName = "IMG_" + System.currentTimeMillis();
        } else if (chatBody.getMessageType().equals(PreferenceConstants.VIDEO) ||
                chatBody.getMessageType().equals(PreferenceConstants.G_VIDEO)) {
            filePath = filePath + PreferenceConstants.dirVideo + File.separator + PreferenceConstants.dirSent;
            fileName = "VID_" + System.currentTimeMillis();
        } else if (chatBody.getMessageType().equals(PreferenceConstants.AUDIO) ||
                chatBody.getMessageType().equals(PreferenceConstants.G_AUDIO)) {
            filePath = filePath + PreferenceConstants.dirAudio + File.separator + PreferenceConstants.dirSent;
            fileName = "AUD_" + System.currentTimeMillis();
        }

        File file = new File(Environment.getExternalStorageDirectory().getPath(), filePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        return new File(file.getPath() + File.separator + fileName + "." + ext);
    }

    private static String getFileExtension(File file) {

        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else return "";
    }
}