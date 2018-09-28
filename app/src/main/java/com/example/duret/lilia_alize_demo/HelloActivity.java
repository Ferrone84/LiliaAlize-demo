package com.example.duret.lilia_alize_demo;

import android.os.Bundle;

public class HelloActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        setTitle(R.string.hello_activity_name);
    }
}
