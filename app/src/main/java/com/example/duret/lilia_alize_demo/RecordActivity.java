package com.example.duret.lilia_alize_demo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import AlizeSpkRec.AlizeException;

import static android.Manifest.permission.RECORD_AUDIO;

public class RecordActivity extends BaseActivity implements RecognitionListener {

    protected static final int RECORDER_SAMPLERATE = 8000;
    protected static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    protected static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    protected int bufferElements2Rec = 2000;
    protected int bytesPerElement = 2; // 2 bytes in 16bit format

    protected long startTime;
    private boolean recordExists = false;
    protected AudioRecord recorder = null;
    protected Button startRecordButton, stopRecordButton;
    protected Thread recordingThread = null, addSamplesThread = null;
    protected TextView timeText;

    protected ToggleButton toggleButton;
    protected SpeechRecognizer speech = null;
    protected Intent recognizerIntent;
    protected String LOG_TAG = "VoiceRecognitionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected boolean checkPermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    protected void requestPermission() {
        ActivityCompat.requestPermissions(RecordActivity.this, new
                String[]{RECORD_AUDIO}, 42);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 42: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRecording();
                }
                else {
                    makeToast(getResources().getString(R.string.permission_error_message));
                }
                break;
            }
        }
    }

    protected void recordAudioSpeakToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, defaultLanguage);

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
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String recordResult = result.get(0).toLowerCase();
                }
                break;
            }
        }
    }

    protected void startRecording() {
        if (!checkPermission()) {
            requestPermission();
            return;
        }

        startRecordButton.setVisibility(View.INVISIBLE);
        stopRecordButton.setVisibility(View.VISIBLE);
        timeText.setText(R.string.default_time);

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, bufferElements2Rec * bytesPerElement);
        recorder.startRecording();

        if (recordExists) {
            try {
                alizeSystem.resetAudio();
                alizeSystem.resetFeatures();
            } catch (AlizeException e) {
                e.printStackTrace();
            }
            recordExists = false;
        }

        final List<short[]> audioPackets = Collections.synchronizedList(new ArrayList<short[]>());

        recordingThread = new Thread(new Runnable() {
            private Handler handler = new Handler();

            public void run() {
                startTime = System.currentTimeMillis();

                short[] tmpAudioSamples = new short[bufferElements2Rec];
                while (recorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                    int samplesRead = recorder.read(tmpAudioSamples, 0, bufferElements2Rec);
                    if (samplesRead > 0) {
                        short[] samples = new short[samplesRead];
                        System.arraycopy(tmpAudioSamples, 0, samples, 0, samplesRead);

                        synchronized (audioPackets) {
                            audioPackets.add(samples);
                        }
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            long currentTime = System.currentTimeMillis() - startTime;
                            String result = new SimpleDateFormat("mm:ss:SS", defaultLanguage)
                                    .format(new Date(currentTime));
                            timeText.setText(result);
                        }
                    });
                }
            }
        }, "AudioRecorder Thread");

        addSamplesThread = new Thread(new Runnable() {
            private Handler handler = new Handler();

            @Override
            public void run() {
                short[] nextElement;
                while((recorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING)
                        || (!audioPackets.isEmpty())) {
                    nextElement = null;
                    synchronized (audioPackets) {
                        if (!audioPackets.isEmpty()) {
                            nextElement = audioPackets.get(0);
                            audioPackets.remove(0);
                        }
                    }
                    if (nextElement != null) {
                        try {
                            alizeSystem.addAudio(nextElement);
                        } catch (AlizeException e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    recordingThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (!audioPackets.isEmpty()) {
                    nextElement = audioPackets.get(0);
                    audioPackets.remove(0);
                    if (nextElement != null) {
                        try {
                            alizeSystem.addAudio(nextElement);
                        } catch (AlizeException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, "addSamples Thread");

        recordingThread.start();
        addSamplesThread.start();
    }

    protected void stopRecording() {
        stopRecordButton.setVisibility(View.INVISIBLE);

        if (recorder != null) {
            recorder.stop();
            try {
                recordingThread.join();
                addSamplesThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            recorder.release();
            recorder = null;
            recordExists = true;
            recordingThread = null;
            addSamplesThread = null;
            startRecordButton.setVisibility(View.VISIBLE);

            makeToast(getResources().getString(R.string.recording_completed));
            afterRecordProcessing();
        }
    }

    protected void afterRecordProcessing() {}

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        toggleButton.setChecked(false);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        if (errorMessage.equals("No speech input")) {
            makeToast(getResources().getString(R.string.no_speech_input));
        }
        toggleButton.setChecked(false);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
}
