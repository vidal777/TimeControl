package com.example.timecontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.content.pm.PackageManager;
import android.net.wifi.SupplicantState;
import android.widget.Toast;

import static android.content.Context.WIFI_SERVICE;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MyBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo ();

        String ssid= info.getSSID().toString();
        String name="MOVISTAR_VIDAL1";  //Wifi Name



        if (ssid.startsWith("\"")) { //Substract addition characters
            ssid = ssid.substring(1, ssid.length() - 1);
        }


//        ProfileTab.btnFitxar.setEnabled(true);

        /*

        if( ssid.equals(name)){  //Enable button
            if(!ProfileTab.btnFitxar.isEnabled()) {
                ProfileTab.btnFitxar.setEnabled(true);
            }

        }else{  //disable button
            if(ProfileTab.btnFitxar.isEnabled()){

                ProfileTab.btnFitxar.setEnabled(false);
            }
        }

         */



        final PendingResult pendingResult = goAsync();
        Task asyncTask = new Task(pendingResult, intent);
        asyncTask.execute();


    }

    private static class Task extends AsyncTask<String, Integer, String> {

        private final PendingResult pendingResult;
        private final Intent intent;

        private Task(PendingResult pendingResult, Intent intent) {
            this.pendingResult = pendingResult;
            this.intent = intent;
        }

        @Override
        protected String doInBackground(String... strings) {


            StringBuilder sb = new StringBuilder();
            sb.append("Action: " + intent.getAction() + "\n");
            sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
            String log = sb.toString();
            return log;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Must call finish() so the BroadcastReceiver can be recycled.
            pendingResult.finish();
        }
    }
}
