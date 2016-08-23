package com.csipsimple.f5chat.background;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.csipsimple.f5chat.http.MySSLSocketFactory;
import com.csipsimple.f5chat.parser.JSONParser;
import com.csipsimple.f5chat.rooster.RoosterConnectionService;
import com.csipsimple.f5chat.utility.SharedPrefrence;
import com.csipsimple.f5chat.utility.DialogUtility;
import com.csipsimple.f5chat.utility.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by User on 7/29/2016.
 */
public class UpdateGroupInfo extends AsyncTask<String, String, String> {
    private Context context;
    SharedPrefrence share;
    private String apiresponse;
    private List<NameValuePair> nameValuePairs;

    public UpdateGroupInfo(Context context,String groupJID) {
        this.context = context;
        share = SharedPrefrence.getInstance(context);
        nameValuePairs = getParam(groupJID);
    }
    private List<NameValuePair> getParam(String groupJID) {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        nameValuePair.add(new BasicNameValuePair("group_jid_without_ip", Utils.getJIDWithoutIP(groupJID)));
        return nameValuePair;
    }


    @Override
    protected String doInBackground(String... uri) {
        String responseString = "";
        responseString = callHttpRequest(uri[0]);
        return responseString;
    }

    public String callHttpRequest(String url1) {
        try {
            //Also change in HttpClient class
            //for https
            URL url = new URL(url1);
            HttpsURLConnection conn = MySSLSocketFactory.getHttpUrlConnection(url);
            //for https

            //for http
//            URL url = new URL(url1);
//            URLConnection urlConnection = url.openConnection();
//            HttpURLConnection conn = (HttpURLConnection) urlConnection;
            //for http


            conn.setReadTimeout(60 * 1000);
            conn.setConnectTimeout(60 * 1000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            //  System.setProperty("http.keepAlive", "false");
            conn.setRequestProperty("connection", "close");
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(nameValuePairs));
            writer.flush();
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                //System.out.println("<-- line "+line);
                apiresponse = line;
            }
            writer.close();
            reader.close();
            os.close();
            conn.connect();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return apiresponse;
    }
    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        try {
            for (NameValuePair pair : params) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            }

            return result.toString();
        } catch (Exception e) {
        }
        return result.toString();
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            JSONParser parser = new JSONParser(context, result);
            parser.getGroupDetail(context);
            //groupDetail.groupDetail(group);
            Intent groupUpdate = new Intent(RoosterConnectionService.GROUP_UPDATE);
            context.sendBroadcast(groupUpdate);
            DialogUtility.showLOG("<-- update Group and send Notify broadcast.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
