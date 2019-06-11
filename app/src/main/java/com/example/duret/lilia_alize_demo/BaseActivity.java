package com.example.duret.lilia_alize_demo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;

import AlizeSpkRec.AlizeException;
import AlizeSpkRec.SimpleSpkDetSystem;

import static android.Manifest.permission.RECORD_AUDIO;

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

        try {
            textToSpeech = new TextToSpeech(BaseActivity.this,this);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected boolean checkPermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    protected void requestPermission() {
        ActivityCompat.requestPermissions(BaseActivity.this, new
                String[]{RECORD_AUDIO}, 42);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == 42) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                requestPermission();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(SettingsActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
