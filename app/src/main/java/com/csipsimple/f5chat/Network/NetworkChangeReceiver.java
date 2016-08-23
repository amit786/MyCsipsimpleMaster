package com.csipsimple.f5chat.Network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.csipsimple.f5chat.rooster.RoosterConnectionService;
import com.csipsimple.f5chat.utility.Utils;


public class NetworkChangeReceiver extends BroadcastReceiver {
    Context context;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        try {
            this.context = context;
            String status = NetworkUtil.getConnectivityStatusString(context);
            Toast.makeText(context, status, Toast.LENGTH_LONG).show();
            if (NetworkUtil.getConnectivityStatus(context) == NetworkUtil.TYPE_WIFI) {
                startService();
            } else if (NetworkUtil.getConnectivityStatus(context) == NetworkUtil.TYPE_MOBILE) {
                startService();
            } else if (NetworkUtil.getConnectivityStatus(context) == NetworkUtil.TYPE_NOT_CONNECTED) {

            }
        } catch (Exception e) {
        }
    }

    public void startService() {
        try {
            if (Utils.isMyServiceRunning(RoosterConnectionService.class, context)) {
                Intent i1 = new Intent(context, RoosterConnectionService.class);
                context.stopService(i1);
            }
            Intent i1 = new Intent(context, RoosterConnectionService.class);
            context.startService(i1);
        } catch (Exception e) {
        }
    }
}