package com.csipsimple.f5chat.http;


import android.content.Context;
import android.os.AsyncTask;

import com.csipsimple.f5chat.PhoneNumber;
import com.csipsimple.f5chat.Verification;
import com.csipsimple.f5chat.fragments.MessagesFragment;
import com.csipsimple.f5chat.group.AddParticipants;
import com.csipsimple.f5chat.group.DeleteMember;
import com.csipsimple.f5chat.group.GroupDetail;
import com.csipsimple.f5chat.parser.JSONParser;
import com.csipsimple.f5chat.utility.PreferenceConstants;

import org.apache.http.NameValuePair;

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
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


public class HTTPPost extends AsyncTask<String, String, String> {

    private Context context;
    private int responseCode;
    private String message;
    private String apiresponse;
    private String whichActivity;
    private String groupJID;
    private List<NameValuePair> nameValuePairs;

    //LoginActivity loginactivity;
    MessagesFragment messagesFragment;
    PhoneNumber phoneNumber;
    Verification verification;
    GroupDetail groupDetail;
    AddParticipants addParticipants;
    DeleteMember deleteMember;

    public HTTPPost(AddParticipants addParticipants, List<NameValuePair> nameValuePairs) {
        context = addParticipants;
        this.addParticipants = addParticipants;
        this.nameValuePairs = nameValuePairs;
    }
    public HTTPPost(MessagesFragment messagesFragment, List<NameValuePair> nameValuePairs) {
        context = messagesFragment.getActivity();
        this.messagesFragment = messagesFragment;
        this.nameValuePairs = nameValuePairs;
    }
    public HTTPPost(DeleteMember deleteMember, List<NameValuePair> nameValuePairs) {
        context = deleteMember;
        this.deleteMember = deleteMember;
        this.nameValuePairs = nameValuePairs;
    }
    public HTTPPost(PhoneNumber phoneNumber, List<NameValuePair> nameValuePairs) {
        context = phoneNumber;
        this.phoneNumber = phoneNumber;
        this.nameValuePairs = nameValuePairs;
    }
    public HTTPPost(Verification verification, List<NameValuePair> nameValuePairs) {
        context = verification;
        this.verification = verification;
        this.nameValuePairs = nameValuePairs;
    }
    public HTTPPost(GroupDetail groupDetail, List<NameValuePair> nameValuePairs) {
        context = groupDetail;
        this.groupDetail = groupDetail;
        this.nameValuePairs = nameValuePairs;
    }


    @Override
    protected String doInBackground(String... uri) {
        String responseString = "";
        whichActivity = uri[1];
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
        if (whichActivity.equals(PreferenceConstants.GROUP_LIST_RESPONSE)) {
            try {
                JSONParser parser = new JSONParser(context, result);
                if(parser.STATUS.equals(PreferenceConstants.PASS_STATUS))
                {
                    parser.getGroupLst();
                    messagesFragment.receiveResponse();
                }
                else
                {
                    messagesFragment.receiveResponse(parser.MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (whichActivity.equals(PreferenceConstants.SEND_OTP_RESPONSE)) {
            try {
                JSONParser parser = new JSONParser(context, result);
                if(parser.STATUS.equals(PreferenceConstants.PASS_STATUS))
                {
                    phoneNumber.receiveResponse();
                }
                else
                {
                    phoneNumber.receiveResponse(parser.MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        else if (whichActivity.equals(PreferenceConstants.REGISTRATION_RESPONSE)) {
            try {
                JSONParser parser = new JSONParser(context, result);
                if(parser.STATUS.equals(PreferenceConstants.PASS_STATUS))
                {
                    verification.receiveResponse();
                }
                else
                {
                    verification.receiveResponse(parser.MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (whichActivity.equals(PreferenceConstants.RESEND_SEND_OTP_RESPONSE)) {
            try {
                JSONParser parser = new JSONParser(context, result);
                if(parser.STATUS.equals(PreferenceConstants.PASS_STATUS))
                {
                    verification.receiveResponseResendOTP();
                }
                else
                {
                    verification.receiveResponseResendOTP(parser.MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        if (whichActivity.equals(PreferenceConstants.EXIT_FROM_GROUP)) {
            try {
                JSONParser parser = new JSONParser(context, result);
                groupDetail.deleteMemberResponse(parser.MESSAGE, parser.VERIFY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        if (whichActivity.equals(PreferenceConstants.GET_GROUP_DETAIL)) {
            try {
                JSONParser parser = new JSONParser(context, result);
                parser.getGroupDetail(context);
                groupDetail.updateView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        if (whichActivity.equals(PreferenceConstants.ADD_MEMBER)) {
            try {
                JSONParser parser = new JSONParser(context, result);
                addParticipants.addGroupMemberResponse(parser.MESSAGE,parser.VERIFY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        if (whichActivity.equals(PreferenceConstants.DELETE_MEMBER)) {
            try {
                JSONParser parser = new JSONParser(context, result);
                deleteMember.deleteGroupMemberResponse(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

