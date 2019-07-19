package com.example.duret.lilia_alize_demo;

import android.os.Bundle;
import android.view.View;

import java.util.HashMap;

import AlizeSpkRec.AlizeException;
import AlizeSpkRec.SimpleSpkDetSystem;

public class HelloActivity extends RecordActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        setTitle(R.string.hello_activity_name);

        startRecordButton = findViewById(R.id.startRecordButton);
        stopRecordButton = findViewById(R.id.stopRecordButton);
        timeText = findViewById(R.id.timeText);

        startRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecording();
            }
        });

        stopRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
            }
        });
    }

    @Override
    public void onResume() {
        try {
            super.onResume();
            timeText.setText(R.string.default_time);
            alizeSystem.resetAudio();
            alizeSystem.resetFeatures();

        } catch (Throwable e) {
            e.printStackTrace();
            makeToast(e.getMessage());
        }
    }

    @Override
    protected void afterRecordProcessing() {
        try {
            //try to identify the speaker
            final SimpleSpkDetSystem.SpkRecResult identificationResult = alizeSystem.identifySpeaker();

            if (identificationResult.speakerId.equals("UBM")) { //no speaker in the list
                startActivity(NewSpeakerActivity.class);
            }
            else {
                startActivity(DialogActivity.class, new HashMap<String, Object>(){{
                    put("speakerName", identificationResult.speakerId);
                }});
            }
        } catch (AlizeException e) {
            e.printStackTrace();
        }
    }
}
