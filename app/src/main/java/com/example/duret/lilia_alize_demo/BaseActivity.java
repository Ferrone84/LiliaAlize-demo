package com.example.duret.lilia_alize_demo;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;

import AlizeSpkRec.AlizeException;
import AlizeSpkRec.SimpleSpkDetSystem;

public class BaseActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    protected Locale defaultLanguage;
    protected TextToSpeech textToSpeech;
    protected SimpleSpkDetSystem alizeSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defaultLanguage = Locale.getDefault();

        try {
            simpleSpkDetSystemInit();
        }
        catch (AlizeException | IOException e) {
            e.printStackTrace();
        }

        textToSpeech = new TextToSpeech(BaseActivity.this,this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void startActivity(Class targetActivity) {
        startActivity(targetActivity, null);
    }

    protected void startActivity(Class targetActivity, Map<String, Object> params) {
        Intent intent = new Intent(BaseActivity.this, targetActivity);

        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue().toString());
            }
        }
        startActivity(intent);
    }

    protected void say(CharSequence text) {
        say(text, false);

    }

    protected void say(CharSequence text, boolean synchronous) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "");

        //wait the tts to finish
        if(synchronous)
        {
            while (textToSpeech.isSpeaking()) {
                //System.out.println(); //dummy content
            }
        }

    }

    protected void makeToast(String text) {
        Toast.makeText(BaseActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    protected short[] bytesToShorts(byte[] byteArray) {
        int size = byteArray.length;
        short[] shortArray = new short[size];

        for (int index = 0; index < size; index++)
            shortArray[index] = (short) byteArray[index];

        return shortArray;
    }

    private void simpleSpkDetSystemInit() throws IOException, AlizeException {
        // Initialization:
        alizeSystem = SharedAlize.getInstance(getApplicationContext());

        // We also load the background model from the application assets
        InputStream backgroundModelAsset = getApplicationContext().getAssets().open("gmm/world.gmm");
        alizeSystem.loadBackgroundModel(backgroundModelAsset);
        backgroundModelAsset.close();
    }

    @Override
    public void onInit(int i) {
        System.out.println("[BaseActivity] TTS started");
    }
}
