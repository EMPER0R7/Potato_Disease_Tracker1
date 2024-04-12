package com.example.potatodiseasetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class Result extends AppCompatActivity {
    TextView data,precautions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        data=findViewById(R.id.data);
        precautions=findViewById(R.id.precautions);
        String result = getIntent().getStringExtra("RESULT");
        data.setText(result);
        String pre=getIntent().getStringExtra("PRE");
        precautions.setText(pre);


    }
}