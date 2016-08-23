package com.csipsimple.f5chat;

import android.util.Log;

import com.csipsimple.f5chat.utility.MySSLSocketFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ImageUtility {
    private String url;
    private HttpsURLConnection con;
    private OutputStream os;

    private String delimiter = "--";
    private String boundary = "SwA" + Long.toString(System.currentTimeMillis()) + "SwA";

    public ImageUtility(String url) {
        this.url = url;
     }


    public void connectForMultipart() throws Exception {
        con = MySSLSocketFactory.getHttpUrlConnection(new URL(url));
        con.setRequestMethod("POST");
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        con.connect();
        os = con.getOutputStream();

    }

    public void addFormPart(String paramName, String value) throws Exception {
        writeParamData(paramName, value);
    }

    public void addFilePart(String paramName, String fileName, byte[] data) throws Exception {
        os.write((delimiter + boundary + "\r\n").getBytes());
        os.write(("Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"" + fileName + "\"\r\n").getBytes());
        os.write(("Content-Type: application/octet-stream\r\n").getBytes());
        os.write(("Content-Transfer-Encoding: binary\r\n").getBytes());
        os.write("\r\n".getBytes());
        os.write(data);
        os.write("\r\n".getBytes());
        Log.e("dddddddddddddddddd", "2"+"ddddddddddddddddd");

    }

    public void finishMultipart() throws Exception {
        os.write((delimiter + boundary + delimiter + "\r\n").getBytes());
    }

    public String getResponse() throws Exception {
        InputStream is = con.getInputStream();
        byte[] b1 = new byte[1024];
        StringBuffer buffer = new StringBuffer();
        while (is.read(b1) != -1)
            buffer.append(new String(b1));
        con.disconnect();
        Log.e("dddddddddddddddddd", buffer.toString()+"ddddddddddddddddd");

        return buffer.toString();
     }

    private void writeParamData(String paramName, String value) throws Exception {

        os.write((delimiter + boundary + "\r\n").getBytes());
        os.write("Content-Type: text/plain\r\n".getBytes());
        os.write(("Content-Disposition: form-data; name=\"" + paramName + "\"\r\n").getBytes());
        ;
        os.write(("\r\n" + value + "\r\n").getBytes());
    }
}