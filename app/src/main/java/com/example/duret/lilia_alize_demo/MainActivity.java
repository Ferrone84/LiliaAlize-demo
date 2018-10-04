package com.example.duret.lilia_alize_demo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import org.w3c.dom.Text;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(startButtonListener);
        Button skipIdentificationButton = findViewById(R.id.skip_identification_button);
        skipIdentificationButton.setOnClickListener(skipIdentificationListener);


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
            Globals.getInstance().setHUMOUR(sharedPref.getBoolean("switchHumour_key", true));
        }

    }

    /*@Override
    public void onInit(int status)
    {
        System.out.println("TTS try");
        if(status == TextToSpeech.SUCCESS)
        {
            System.out.println("TTS Started");
            //say("Bonjour, allez c'est parti !");

            //Button startButton = findViewById(R.id.startButton);
            //startButton.setText("Coucou");
        }
    }*/

    private View.OnClickListener startButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(HelloActivity.class);
        }
    };

    private View.OnClickListener skipIdentificationListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(DialogActivity.class);
        }
    };
}
