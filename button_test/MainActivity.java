package com.example.user.hello_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static String EXTRA = "EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void buttonFunction(View v)
    {
        Intent intent = new Intent(this, DisplayAct.class);
        EditText edit = (EditText) findViewById(R.id.edit_msg);
        String text = edit.getText().toString();
        intent.putExtra(EXTRA, text);
        startActivity(intent);
    }
}
