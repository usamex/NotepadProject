package com.example.notepadproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.render.AreTextView;

public class TextViewActivity extends AppCompatActivity {
    public static final String HTML_TEXT = "html_desc";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_are_tv);
        AreTextView areTextView = findViewById(R.id.areTextView);
        String s = getIntent().getStringExtra(HTML_TEXT);
        if(s == null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        areTextView.fromHtml(s);
    }
}
