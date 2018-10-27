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

    public void connect(View view)
    {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        EditText passEdit = (EditText) findViewById(R.id.pass);
        EditText ssidEdit = (EditText) findViewById(R.id.ssid);
        TextView errorView = (TextView) findViewById(R.id.errorV);

        String ssid = ssidEdit.getText().toString();
        String pass = passEdit.getText().toString();

        if( !connectToKnownWiFi(wifi, ssid) ) {
            if(connectToUnknown(wifi, ssid, pass)){
                errorView.setText("Error while connecting");
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
        List<WifiConfiguration> knownList = wifi.getConfiguredNetworks();
        for(WifiConfiguration i : knownList){
            if(i.SSID  != null && i.SSID.equals("\""+ssid+"\"")){
                wifi.disconnect();
                wifi.enableNetwork(i.networkId, true);
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
        WifiConfiguration wifiConf = new WifiConfiguration();
        wifiConf.SSID = ssid;
        wifiConf.preSharedKey = ("\"" + pass + "\"");
        int iid = wifi.addNetwork(wifiConf);
        /*
        if(connectToKnownWiFi(wifi, ssid)){
            return true;
        }*/
        wifi.disconnect();
        wifi.enableNetwork(iid, true);
        wifi.reconnect();
        wifi.setWifiEnabled(true);
        if(iid == -1) {
            wifi.removeNetwork(iid);
            return false;
        }
        return true;
    }
}
