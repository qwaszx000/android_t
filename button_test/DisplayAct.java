package com.example.user.hello_android;

import android.content.res.ColorStateList;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DisplayAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String s = intent.getStringExtra(MainActivity.EXTRA);

        TextView textView = new TextView(this);
        textView.setTextSize(45);
        textView.setText(s);

        setContentView(textView);
        //setContentView(R.layout.activity_display);
    }
}
