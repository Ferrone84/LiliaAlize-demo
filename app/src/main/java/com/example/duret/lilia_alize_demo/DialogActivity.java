package com.example.duret.lilia_alize_demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

public class DialogActivity extends RecordActivity {

    private Thread thread;
    private SendMessage message = null;
    private TextView dialogText;
    private String recordResult, speakerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        setTitle(R.string.dialog_activity_name);

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(startButtonListener);

        Button reconnectButton = findViewById(R.id.reconnectButton);
        reconnectButton.setOnClickListener(reconnectButtonListener);

        Button generateGoalButton = findViewById(R.id.generateGoalButton);
        generateGoalButton.setOnClickListener(generateGoalButtonListener);

        dialogText = findViewById(R.id.text);
        toggleButton = findViewById(R.id.toggleButton);
        speakerName = getIntent().getStringExtra("speakerName"); //null if not set

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    speech.startListening(recognizerIntent);
                } else {
                    speech.stopListening();
                }
            }
        });
    }

    private View.OnClickListener startButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (message != null) {
                message.start();
            }
        }
    };

    private View.OnClickListener reconnectButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            message = new SendMessage();
            thread = new Thread(message);
            thread.start();
        }
    };

    private View.OnClickListener generateGoalButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (message != null) {
                message.generateGoal();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String recordResult = result.get(0);

                    message.send(recordResult);
                }
                break;
            }
        }
    }

    @Override
    public void onResults(Bundle results) {
        super.onResults(results);
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        recordResult = matches != null ? matches.get(0) : null;

        if (recordResult != null) {
            dialogText.setText(recordResult);
        }
    }

    private class SendMessage implements Runnable {

        private Socket sock;
        private BufferedReader in;
        private Handler handler = new Handler();

        private void setTextofView(String text, int idView)
        {
            final String finalText = text;
            final int finalIdView = idView;

            handler.post(new Runnable() {
                @Override
                public void run() {
                    TextView textView = findViewById(finalIdView);
                    textView.setText(finalText);
                }
            });
        }

        @Override
        public void run() {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try
            {
                String ip = Globals.getInstance().getIP();
                int port = Integer.parseInt(Globals.getInstance().getPORT());
                sock = new Socket(ip, port); //TODO: menu connection with text input for host and port
                in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);

                String fromClient;
                while(true)
                {
                    try
                    {
                        fromClient = in.readLine();
                        if(fromClient != null && fromClient.startsWith("t;"))
                        {
                            System.out.println("Receive: " + fromClient.substring(2));
                            setTextofView(fromClient.substring(2), R.id.text);
                            recordAudioSpeakToText();
                        }
                        else if(fromClient != null && fromClient.startsWith("f;"))
                        {
                            String str_fruit = fromClient.substring(2);
                            System.out.println("Receive fruit: " + str_fruit);
                            setTextofView(str_fruit, R.id.fruit);

                            if(str_fruit.equals("fraise"))
                            {
                                ImageView image = (ImageView) findViewById(R.id.imgfruit);
                                image.setImageResource(R.drawable.fraise);
                            }
                            else if(str_fruit.equals("citron"))
                            {
                                ImageView image = (ImageView) findViewById(R.id.imgfruit);
                                image.setImageResource(R.drawable.citron);
                            }
                            else if(str_fruit.equals("poire"))
                            {
                                ImageView image = (ImageView) findViewById(R.id.imgfruit);
                                image.setImageResource(R.drawable.poire);
                            }
                            else if(str_fruit.equals("pomme"))
                            {
                                ImageView image = (ImageView) findViewById(R.id.imgfruit);
                                image.setImageResource(R.drawable.pomme);
                            }
                            else if(str_fruit.equals("framboise"))
                            {
                                ImageView image = (ImageView) findViewById(R.id.imgfruit);
                                image.setImageResource(R.drawable.framboise);
                            }
                            else if(str_fruit.equals("aubergine"))
                            {
                                ImageView image = (ImageView) findViewById(R.id.imgfruit);
                                image.setImageResource(R.drawable.aubergine);
                            }
                            else
                            {
                                ImageView image = (ImageView) findViewById(R.id.imgfruit);
                                image.setImageResource(android.R.color.transparent);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        System.err.println(e.getStackTrace());
                        //makeToast(e.getMessage());
                    }
                }
            }
            catch (java.net.UnknownHostException e)
            {
                System.err.println(e.getStackTrace());
                //makeToast(e.getMessage());
            }
            catch (java.io.IOException e)
            {
                System.err.println(e.getStackTrace());
                //makeToast(e.getMessage());
            }
            catch (Exception e)
            {
                System.err.println(e.getStackTrace());
                //makeToast(e.getMessage());
            }
        }

        protected void start()
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try
            {
                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                out.println("start");
            }
            catch (java.io.IOException e)
            {
                System.err.println(e.getStackTrace());
                //makeToast(e.getMessage());
            }
            catch (Exception e)
            {
                System.err.println(e.getStackTrace());
                //makeToast(e.getMessage());
            }
        }

        protected void send(String message)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try
            {
                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                out.println("r;"+message);
            }
            catch (java.io.IOException e)
            {
                System.err.println(e.getStackTrace());
                //makeToast(e.getMessage());
            }
            catch (Exception e)
            {
                System.err.println(e.getStackTrace());
                //makeToast(e.getMessage());
            }
        }

        protected void generateGoal()
        {
            System.out.println("GenerateGoal");
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try
            {
                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                out.println("goal");
            }
            catch (java.io.IOException e)
            {
                System.err.println(e.getStackTrace());
                //makeToast(e.getMessage());
            }
            catch (Exception e)
            {
                System.err.println(e.getStackTrace());
                //makeToast(e.getMessage());
            }
        }
    }

}
