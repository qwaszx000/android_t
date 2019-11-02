package com.kwas.hacktool;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
//widget
import android.widget.EditText;
import android.widget.Button;
import android.widget.CheckBox;
//io
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
//net
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private EditText TargetText = null;
    private EditText TargetPortEdit = null;
    private EditText DataEdit = null;
    private EditText ResponseEdit = null;

    private Button SendBtn = null;
    private Button ConnectBtn = null;
    private Button DisconnectBtn = null;
    private CheckBox UDPCheck = null;

    private Boolean udp = false;
    private InetAddress target = null;
    private int targetPort = 0;
    private Socket socket = null;
    private OutputStream out = null;
    private PrintWriter output = null;
    private InputStream in = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //widgets
        TargetText = (EditText) findViewById(R.id.TargetText);
        TargetPortEdit = (EditText) findViewById(R.id.PortText);
        DataEdit = (EditText) findViewById(R.id.DataText);
        ResponseEdit = (EditText) findViewById(R.id.ResponseText);
        SendBtn = (Button) findViewById(R.id.SendButton);
        ConnectBtn = (Button) findViewById(R.id.ConnectButton);
        DisconnectBtn = (Button) findViewById(R.id.DisconnectButton);
        UDPCheck = (CheckBox) findViewById(R.id.UDPbox);
    }

    //UDPbox change
    public void checkUDPchange(android.view.View v){
        udp = UDPCheck.isChecked();
        //use udp
        if(udp){
            ConnectBtn.setEnabled(false);
            DisconnectBtn.setEnabled(false);
            SendBtn.setEnabled(true);
        } else { // use tcp
            ConnectBtn.setEnabled(true);
            DisconnectBtn.setEnabled(false);
            SendBtn.setEnabled(false);
        }
    }

    //clear response and data edits
    public void clearAll(android.view.View v){
        DataEdit.setText("");
        ResponseEdit.setText("");
    }

    //Connect to target(tcp only)
    public void connect(android.view.View v){
        ConnectBtn.setEnabled(false);
        DisconnectBtn.setEnabled(true);
        SendBtn.setEnabled(true);
        UDPCheck.setEnabled(false);
        //connect to target
        try{
            target = InetAddress.getByName(TargetText.getText().toString());
            targetPort = Integer.parseInt(TargetPortEdit.getText().toString());
            socket = new Socket(target, targetPort);
            if(socket.isConnected()){
                ResponseEdit.append("Successful connect\n");
                out = socket.getOutputStream();
                output = new PrintWriter(out);
                in = socket.getInputStream();
            }
        } catch(UnknownHostException e){
            ResponseEdit.append("Host not found: "+e.getMessage()+"\n");
            //enable disabled buttons if have error
            ConnectBtn.setEnabled(true);
            DisconnectBtn.setEnabled(false);
            SendBtn.setEnabled(false);
            UDPCheck.setEnabled(true);
        } catch(IOException e){
            ResponseEdit.append("IOException: "+e.getMessage()+"\n");
            //enable disabled buttons if have error
            ConnectBtn.setEnabled(true);
            DisconnectBtn.setEnabled(false);
            SendBtn.setEnabled(false);
            UDPCheck.setEnabled(true);
        }
    }

    //close connection(tcp only)
    public void closeConnection(android.view.View v){
        ConnectBtn.setEnabled(true);
        DisconnectBtn.setEnabled(false);
        SendBtn.setEnabled(false);
        try{
            socket.close();
            out.flush();
            out.close();
            in.close();
            ResponseEdit.append("Closed\n");
        } catch(IOException e){
            ResponseEdit.append("Closing error"+e.getMessage()+"\n");
            //don`t closed, so try again
            ConnectBtn.setEnabled(false);
            DisconnectBtn.setEnabled(true);
            SendBtn.setEnabled(false);
        }
    }

    //https://stackoverflow.com/questions/10752919/how-can-i-convert-inputstream-data-to-string-in-android-soap-webservices
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            ResponseEdit.append("Error: " + e.getMessage()+"\n");
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                ResponseEdit.append("Error: " + e.getMessage()+"\n");
            }
        }
        return sb.toString();
    }

    //send data to target
    public void sendData(android.view.View v){
        udp = UDPCheck.isChecked();
        targetPort = Integer.parseInt(TargetPortEdit.getText().toString());
        String data = DataEdit.getText().toString();
        String response = "";
        if(udp){ //send data by udp
            try {
                target = InetAddress.getByName(TargetText.getText().toString());
                DatagramSocket s = new DatagramSocket();
                DatagramPacket p = new DatagramPacket(data.getBytes(), data.length(), target, targetPort);
                s.send(p);
                //Todo get response
                s.close();
            } catch(SocketException e){
                ResponseEdit.append("Sending udp error: "+e.getMessage()+"\n");
            } catch(UnknownHostException e){
                ResponseEdit.append("Unknown host error: "+e.getMessage()+"\n");
            } catch(IOException e){
                ResponseEdit.append("IO error: "+e.getMessage()+"\n");
            }
        } else { //send data by tcp
            try {
                output.print(data);
                out.flush();
                response = convertStreamToString(in);
                ResponseEdit.append(response);
            } catch(IOException e){
                ResponseEdit.append("Sending tcp error: "+e.getMessage()+"\n");
            }
        }
    }
}
