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

import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.TimeUnit;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void enableWiFi(View view) {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);
    }

    public void disableWiFi(View view) {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(false);
    }

    public void bruteWiFi(View view) {
        try {
            PrintWriter w = new PrintWriter("List.txt", "UTF-8");
            w.printf("%d num", 5);
            w.close();
        } catch (Exception e){

        }
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        TextView errorView = (TextView) findViewById(R.id.errorV);
        EditText ssidEdit = (EditText) findViewById(R.id.ssid);
        wifi.setWifiEnabled(true);
        String ssid = ssidEdit.getText().toString();
        boolean res = false;
        String passList[] = {"00000000", "87654321", "12345678"};
        for (String i : passList) {
            res = connectToUnknown(wifi, ssid, i, true);
            if(res)
                break;
        }
        /*
        int pass_len = 8;
        String pass = "        ";//8 spaces
        char passChars[] = pass.toCharArray();
        for(int i = 0;i<pass_len;i++){
            while(passChars[i] <= 126){
                if(passChars[i] == 126){
                }
                i++;
            }
        }*/
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
        if (!knownConnectionSuccess) {
            boolean isSuccess = connectToUnknown(wifi, ssid, pass, false);
            if (!isSuccess) {
                errorView.append("Error while connecting\r\n");
            } else if (isSuccess) {
                String conInfo[] = wifi.getConnectionInfo().toString().split(", ");
                for (String i : conInfo) {
                    errorView.append(i + "\r\n");
                }
            }
        } else if (knownConnectionSuccess) {
            String conInfo[] = wifi.getConnectionInfo().toString().split(", ");
            for (String i : conInfo) {
                errorView.append(i + "\r\n");
            }
        }
    }



    /*
    Connects to already known host by ssid.
    Returns true if success or false if not
    @param wifi
    @param ssid
     */

    boolean connectToKnownWiFi(WifiManager wifi, String ssid, boolean silent) {
        TextView errorView = (TextView) findViewById(R.id.errorV);
        wifi.setWifiEnabled(true);
        List<WifiConfiguration> knownList = wifi.getConfiguredNetworks();

        for (WifiConfiguration i : knownList) {
            if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                wifi.disconnect();
                if (!silent)
                    errorView.append("Connecting to " + ("\"" + ssid + "\"\r\n"));
                //wifi.enableNetwork(i.networkId, false);

                boolean rs = wifi.enableNetwork(i.networkId, true);
                if (!silent)
                    errorView.append("Enable network returned:" + rs + "\r\n");

                rs = wifi.reconnect();
                if (!silent)
                    errorView.append("Reconnect returned:" + rs + "\r\n");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                WifiInfo winf = wifi.getConnectionInfo();
                if (winf.getSupplicantState() == SupplicantState.ASSOCIATED ||
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
    boolean connectToUnknown(WifiManager wifi, String ssid, String pass, boolean silent) {
        TextView errorView = (TextView) findViewById(R.id.errorV);
        wifi.setWifiEnabled(true);
        WifiConfiguration wifiConf = new WifiConfiguration();
        wifiConf.SSID = ("\"" + ssid + "\"");
        wifiConf.preSharedKey = ("\"" + pass + "\"");
        int res = wifi.addNetwork(wifiConf);
        if (!silent)
            errorView.append("add Network returned " + res + "\r\n");

        if (connectToKnownWiFi(wifi, ssid, false)) {
            return true;
        } else {
            wifi.removeNetwork(res);
            return false;
        }
    }
}