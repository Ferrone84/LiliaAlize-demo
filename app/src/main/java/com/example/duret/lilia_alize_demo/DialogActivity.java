package com.example.duret.lilia_alize_demo;

import android.os.Bundle;

public class DialogActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        setTitle(R.string.dialog_activity_name);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int test = Integer.parseInt(extras.getString("testInt"));
            String test2 = extras.getString("testStr");
            System.out.println(test + " / " + test2);
        }
    }

}
