package com.example.duret.lilia_alize_demo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Locale;
import java.util.Map;

import static android.Manifest.permission.RECORD_AUDIO;

public class BaseActivity extends AppCompatActivity {

    protected Locale defaultLanguage;
    protected TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defaultLanguage = Locale.getDefault();

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(defaultLanguage);
                }
            }
        });
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
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "");
    }

    protected void recordAudio() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, defaultLanguage);
        intent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR");
        intent.putExtra("android.speech.extra.GET_AUDIO", true);

        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException a) {
            makeToast(a.getMessage());
        }
    }

    protected boolean checkPermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    protected void requestPermission() {
        ActivityCompat.requestPermissions(BaseActivity.this, new
                String[]{RECORD_AUDIO}, 1);
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

}
