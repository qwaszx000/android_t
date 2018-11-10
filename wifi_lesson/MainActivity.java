package com.example.user.wificontroller;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import android.net.wifi.*;
import android.content.Context;
//import android.content.Intent;
//import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;
import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void enableWiFi(View view)
    {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);
    }

    public void disableWiFi(View view)
    {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(false);
    }

    public void bruteWiFi(View view)
    {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        TextView errorView = (TextView) findViewById(R.id.errorV);
        EditText ssidEdit = (EditText) findViewById(R.id.ssid);
        wifi.setWifiEnabled(true);
        String ssid = ssidEdit.getText().toString();

        CrackClass cr = new CrackClass(wifi, ssid);
        cr.execute();
    }

    public void connect(View view) throws InterruptedException {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);
        EditText passEdit = (EditText) findViewById(R.id.pass);
        EditText ssidEdit = (EditText) findViewById(R.id.ssid);
        TextView errorView = (TextView) findViewById(R.id.errorV);

        String ssid = ssidEdit.getText().toString();
        String pass = passEdit.getText().toString();
        boolean knownConnectionSuccess = connectToKnownWiFi(wifi, ssid, false);
        if( !knownConnectionSuccess ) {
            boolean isSuccess = connectToUnknown(wifi, ssid, pass, false);
            if( !isSuccess ){
                errorView.append("Error while connecting\r\n");
            }else if(isSuccess){
                String conInfo[] = wifi.getConnectionInfo().toString().split(", ");
                for(String i : conInfo) {
                    errorView.append(i+"\r\n");
                }
            }
        }else if(knownConnectionSuccess){
            String conInfo[] = wifi.getConnectionInfo().toString().split(", ");
            for(String i : conInfo) {
                errorView.append(i+"\r\n");
            }
        }
    }

    /*
    Connects to already known host by ssid.
    Returns true if success or false if not
    @param wifi
    @param ssid
     */
    boolean connectToKnownWiFi(WifiManager wifi, String ssid, boolean silent)
    {
        TextView errorView = (TextView) findViewById(R.id.errorV);
        wifi.setWifiEnabled(true);
        List<WifiConfiguration> knownList = wifi.getConfiguredNetworks();

        for(WifiConfiguration i : knownList){
            if(i.SSID  != null && i.SSID.equals("\"" + ssid + "\"")){
                wifi.disconnect();
                if(!silent)
                    errorView.append("Connecting to " + ("\"" + ssid + "\"\r\n") );
                //wifi.enableNetwork(i.networkId, false);

                boolean rs = wifi.enableNetwork(i.networkId, true);
                if(!silent)
                    errorView.append("Enable network returned:" + rs + "\r\n");

                rs = wifi.reconnect();
                if(!silent)
                    errorView.append("Reconnect returned:" + rs + "\r\n");

                WifiInfo winf = wifi.getConnectionInfo();
                if(winf.getSupplicantState() == SupplicantState.ASSOCIATED ||
                        winf.getSupplicantState() == SupplicantState.COMPLETED) {
                    errorView.append("Connected!\r\n");
                    return true;
                }
            }
        }
        return false;
    }
    /*
    Connects to unknown wifi.
    If success return true. Else returns false.
    @param wifi
    @param ssid
    @param pass
     */
    boolean connectToUnknown(WifiManager wifi, String ssid, String pass, boolean silent)
    {
        TextView errorView = (TextView) findViewById(R.id.errorV);
        wifi.setWifiEnabled(true);
        WifiConfiguration wifiConf = new WifiConfiguration();
        wifiConf.SSID = ("\"" + ssid + "\"");
        wifiConf.preSharedKey = ("\"" + pass + "\"");
        //wifiConf.hiddenSSID = true;
        //wifiConf.status = WifiConfiguration.Status.ENABLED;
        //wifiConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        //wifiConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        //wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        //wifiConf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        //wifiConf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        //wifiConf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        int res = wifi.addNetwork(wifiConf);
        if(!silent)
            errorView.append("add Network returned " + res +"\r\n");
        //boolean b = wifi.enableNetwork(res, true);
        //errorView.append("enableNetwork returned " + b + "\r\n");
        //wifiConf.hiddenSSID = true;
        //wifiConf.status = WifiConfiguration.Status.ENABLED;
        //wifiConf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        //wifiConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        //wifiConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        //wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        //wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        //wifiConf.allowedGroupCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        //wifiConf.allowedGroupCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        //wifiConf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        //wifiConf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        //wifiConf.wepKeys[0] = ("\""+pass+"\"");
        //wifiConf.wepTxKeyIndex = 0;
        //wifiConf.priority = 40;
        //wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        //wifiConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        //wifiConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        //wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        //ifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
        //wifiConf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        //wifiConf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        //wifiConf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        //wifiConf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        //wifiConf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.LEAP);
        //wifiConf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        //wifiConf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.NONE);
        //wifiConf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);

        //int res = wifi.addNetwork(wifiConf);
        //wifi.enableNetwork(res, true);

        if(connectToKnownWiFi(wifi, ssid, false)) {
            return true;
        }else{
            return false;
        }
        //wifi.disconnect();
        //wifi.enableNetwork(res, true);
        //wifi.reconnect();
        /*
        wifi.disconnect();
        wifi.enableNetwork(iid, true);
        wifi.reconnect();
        */
        //wifi.removeNetwork(iid);

    }

    protected class CrackClass extends AsyncTask<Integer, Integer, Integer>
    {
        String passList[] = {"00000000", "87654321", "12345678"};
        WifiManager wifi = null;
        String ssid;
        TextView errorView = null;
        CrackClass(WifiManager wifi, String ssid)
        {
            super();
            this.wifi = wifi;
            this.ssid = ssid;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            this.errorView = (TextView) findViewById(R.id.errorV);
            this.errorView.append("Pre execute settings complete!\r\n");

        }
        @Override
        protected Integer doInBackground(Integer... params)
        {
            WifiManager wifi = this.wifi;
            for(String p : this.passList){
                WifiConfiguration wc = new WifiConfiguration();

                wc.SSID = ("\"" + this.ssid + "\"");
                wc.preSharedKey = "\"" + p + "\"";
                int nid = wifi.addNetwork(wc);
                this.errorView.append("AddNetwork returned: " + nid + "\r\n");
                //wifi.disconnect();
                /*
                this.errorView.append("Disconnected, connecting to new...\r\n");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                boolean rs = wifi.enableNetwork(nid, true);
                this.errorView.append("enableNetwork returned: " + rs + "\r\n");
                //rs = wifi.reconnect();
                //this.errorView.append("Reconnect returned: " + rs + "\r\n");

                this.errorView.append("Getting info\r\n");
                WifiInfo wInfo = wifi.getConnectionInfo();
                try {
                    if (wInfo.getBSSID() != "00:00:00:00:00:00") {
                        this.errorView.append("Found pass by bssid: " + p + "\r\n");
                    }
                }catch(Exception e){
                    this.errorView.append(e.toString() + "\r\n");
                    this.errorView.append("Found pass by exception: " + p + "\r\n");
                }
                try {
                    this.errorView.append("Sleeping\r\n");
                    wifi.removeNetwork(nid);
                    Thread.sleep(300);
                }catch (InterruptedException e){
                    this.errorView.append(e.toString() + "\r\n");
                }
            }
            return null;
        }
    }

}

