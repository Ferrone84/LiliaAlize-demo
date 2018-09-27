package com.example.duret.lilia_alize_demo;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private String recordResult = "";
    private TextView resultAudioRecord;
    private ImageButton recordButton;
    private Button dialogButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultAudioRecord = findViewById(R.id.resultRecord);
        recordButton = findViewById(R.id.recordButton);
        dialogButton = findViewById(R.id.dialogButton);

        recordButton.setOnClickListener(recordButtonListener);
        dialogButton.setOnClickListener(dialogButtonListener);
    }

    private View.OnClickListener recordButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean canRecord = false;

            if (checkPermission()) {
                canRecord = true;
            }
            else {
                requestPermission();
            }

            if (canRecord) {
                recordAudio();
            }
        }
    };

    private View.OnClickListener dialogButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, DialogActivity.class);
            startActivity(intent);
        }
    };

    public void recordAudio() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    recordResult = result.get(0);
                    resultAudioRecord.setText(recordResult);

                    this.readText(resultAudioRecord.getText());
                }
                break;
            }
        }
    }
}
