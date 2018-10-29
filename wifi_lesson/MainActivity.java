package com.example.user.wificontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        wifi.setWifiEnabled(true);
    }

    public void connect(View view) throws InterruptedException {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);
        EditText passEdit = (EditText) findViewById(R.id.pass);
        EditText ssidEdit = (EditText) findViewById(R.id.ssid);
        TextView errorView = (TextView) findViewById(R.id.errorV);

        String ssid = ssidEdit.getText().toString();
        String pass = passEdit.getText().toString();
        boolean knownConnectionSuccess = connectToKnownWiFi(wifi, ssid);
        if( !knownConnectionSuccess ) {
            boolean isSuccess = connectToUnknown(wifi, ssid, pass);
            if( !isSuccess ){
                errorView.append("Error while connecting\r\n");
            }else if(isSuccess){
                errorView.append(wifi.getConnectionInfo().toString());
            }
        }else if(knownConnectionSuccess){
            errorView.append(wifi.getConnectionInfo().toString());
        }
    }

    /*
    Connects to already known host by ssid.
    Returns true if success or false if not
    @param wifi
    @param ssid
     */
    boolean connectToKnownWiFi(WifiManager wifi, String ssid)
    {
        TextView errorView = (TextView) findViewById(R.id.errorV);
        wifi.setWifiEnabled(true);
        List<WifiConfiguration> knownList = wifi.getConfiguredNetworks();
        for(WifiConfiguration i : knownList){
            if(i.SSID  != null && i.SSID.equals("\""+ssid+"\"")){
                wifi.disconnect();
                errorView.append("Connecting to " + ("\"" + ssid + "\"\r\n") );
                boolean rs = wifi.enableNetwork(i.networkId, true);
                errorView.append("Enable network returned:" + rs + "\r\n");
                wifi.reconnect();
                return true;
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
    boolean connectToUnknown(WifiManager wifi, String ssid, String pass)
    {
        TextView errorView = (TextView) findViewById(R.id.errorV);
        wifi.setWifiEnabled(true);
        WifiConfiguration wifiConf = new WifiConfiguration();
        wifiConf.SSID = ("\""+ssid+"\"");
        wifiConf.preSharedKey = ("\"" + pass + "\"");
        wifiConf.hiddenSSID = true;
        wifiConf.status = WifiConfiguration.Status.ENABLED;
        wifiConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wifiConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifiConf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wifiConf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        int res = wifi.addNetwork(wifiConf);
        errorView.append("add Network returned " + res +"\n");
        boolean b = wifi.enableNetwork(res, true);
        errorView.append("enableNetwork returned " + b + "\n");
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
        /*
        if(connectToKnownWiFi(wifi, ssid)) {
            return true;
        }else{
            return false;
        }
        */
        return true;
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
}
