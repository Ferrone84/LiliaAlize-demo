package com.example.duret.lilia_alize_demo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

public class SettingsActivity extends BaseActivity {

    EditText editIp;
    EditText editPort;
    Switch switchHumour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editIp = findViewById(R.id.editIP);
        editPort = findViewById(R.id.editPORT);
        switchHumour = (Switch)findViewById(R.id.switch_humour);

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editIp.setText(sharedPref.getString("editIP", "demo-lia.univ-avignon.fr"));
        editPort.setText(sharedPref.getString("editPORT", "13558"));
        switchHumour.setChecked(sharedPref.getBoolean("switchHumour", true));
    }

    public void end(View v) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        Globals.getInstance().setIP(editIp.getText().toString());
        sharedPref.edit().putString("editIP", editIp.getText().toString()).apply();

        Globals.getInstance().setPORT(editPort.getText().toString());
        sharedPref.edit().putString("editPORT", editPort.getText().toString()).apply();

        Globals.getInstance().setHUMOUR(switchHumour.isChecked());
        sharedPref.edit().putBoolean("switchHumour", switchHumour.isChecked()).apply();

        finish();
    }
}
