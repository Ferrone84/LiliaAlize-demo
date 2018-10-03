package com.example.duret.lilia_alize_demo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(startButtonListener);

        Button dialogButton = findViewById(R.id.dialogButton);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(DialogActivity.class);
            }
        });

        ImageButton settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(SettingsActivity.class);
            }
        });

        if (Globals.getInstance().getIP() == null || Globals.getInstance().getPORT() == null) {
            final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            Globals.getInstance().setIP(sharedPref.getString("editIP_key", ""));
            Globals.getInstance().setPORT(sharedPref.getString("editPORT_key", ""));
        }
    }

    private View.OnClickListener startButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(HelloActivity.class);
        }
    };
}
