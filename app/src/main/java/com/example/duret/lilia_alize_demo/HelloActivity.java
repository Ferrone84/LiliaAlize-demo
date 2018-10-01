package com.example.duret.lilia_alize_demo;

import android.os.Bundle;
import android.view.View;

import AlizeSpkRec.AlizeException;
import AlizeSpkRec.IdAlreadyExistsException;
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
    protected void recordProcessing() {
        try {
            long speakerCount = alizeSystem.speakerCount();

            if (speakerCount == 0) {
                //add the speaker to the system
                alizeSystem.createSpeakerModel("Steven");
            }

            //try to identify the speaker
            SimpleSpkDetSystem.SpkRecResult identificationResult = alizeSystem.identifySpeaker();
            System.out.println(identificationResult.match + "/" + identificationResult.score +"/"+identificationResult.speakerId);
            if (identificationResult.speakerId.equals("UBM")) {

            }
            else {
                say(
                    getResources().getString(R.string.hello_message_start)
                    + identificationResult.speakerId
                    + getResources().getString(R.string.hello_message_end)
                );
            }
            alizeSystem.resetAudio();
            alizeSystem.resetFeatures();
        } catch (AlizeException e) {
            e.printStackTrace();
        } catch (IdAlreadyExistsException e) {
            e.printStackTrace();
        }
    }
}
