package com.example.duret.lilia_alize_demo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkPermission()) {
            requestPermission();
            return;
        }

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(startButtonListener);
        Button skipIdentificationButton = findViewById(R.id.skip_identification_button);
        skipIdentificationButton.setOnClickListener(skipIdentificationListener);

        if (Globals.getInstance().getIP() == null || Globals.getInstance().getPORT() == null) {
            final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            Globals.getInstance().setIP(sharedPref.getString("editIP", "demo-lia.univ-avignon.fr"));
            Globals.getInstance().setPORT(sharedPref.getString("editPORT", "13558"));
            Globals.getInstance().setHUMOUR(sharedPref.getBoolean("switchHumour", true));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            say(
                    getResources().getString(R.string.hello_message_start) + " "
                            + "Fabrice" + " "
                            + getResources().getString(R.string.hello_message_end)
            );
            startActivity(DialogActivity.class);
        }
    };
}
