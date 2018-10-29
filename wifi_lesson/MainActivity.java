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

        if( !connectToKnownWiFi(wifi, ssid) ) {
            boolean isSuccess = connectToUnknown(wifi, ssid, pass);
            if( !isSuccess ){
                errorView.append("Error while connecting");
            }else if(isSuccess){
                errorView.append(wifi.getConnectionInfo().toString());
            }
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
        wifi.setWifiEnabled(true);
        List<WifiConfiguration> knownList = wifi.getConfiguredNetworks();
        for(WifiConfiguration i : knownList){
            if(i.SSID  != null && i.SSID.equals("\""+ssid+"\"")){
                wifi.disconnect();
                wifi.enableNetwork(i.networkId, true);
                if(!wifi.reconnect())
                    return false;
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
        wifi.setWifiEnabled(true);
        WifiConfiguration wifiConf = new WifiConfiguration();
        wifiConf.status = WifiConfiguration.Status.DISABLED;
        wifiConf.SSID = ("\""+ssid+"\"");
        wifiConf.wepKeys[0] = ("\""+pass+"\"");
        wifiConf.wepTxKeyIndex = 0;
        wifiConf.priority = 40;
        wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wifiConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        wifiConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        wifiConf.preSharedKey = ("\"" + pass + "\"");
        wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
        wifiConf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wifiConf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wifiConf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wifiConf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        wifiConf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.LEAP);
        wifiConf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.NONE);
        wifiConf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);

        int res = wifi.addNetwork(wifiConf);

        wifi.saveConfiguration();
        if(connectToKnownWiFi(wifi, ssid)) {
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
        //return false;

    }
}
