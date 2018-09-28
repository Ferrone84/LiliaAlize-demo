package com.example.duret.lilia_alize_demo;

import android.os.Bundle;
import android.view.View;

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
        //si la base est vide on vérifie rien et on propose l'ajout
        //si la base contient des users on fait une identification
        makeToast("recordProcessing");
    }
}
