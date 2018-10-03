package com.example.duret.lilia_alize_demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsActivity extends BaseActivity {

    EditText editIp;
    EditText editPort;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editIp = findViewById(R.id.editIP);
        editPort = findViewById(R.id.editPORT);


        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editIp.setText(sharedPref.getString("editIP_key", ""));
        editPort.setText(sharedPref.getString("editPORT_key", ""));
    }

    public void end(View v) {

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        Globals.getInstance().setIP(editIp.getText().toString());
        sharedPref.edit().putString("editIP_key", editIp.getText().toString()).apply();

        Globals.getInstance().setPORT(editPort.getText().toString());
        sharedPref.edit().putString("editPORT_key", editPort.getText().toString()).apply();

        finish();
    }
}
