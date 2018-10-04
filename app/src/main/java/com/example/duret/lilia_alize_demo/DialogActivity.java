package com.example.duret.lilia_alize_demo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

import static android.icu.lang.UProperty.INT_START;

public class DialogActivity extends RecordActivity {

    private Thread thread;
    private SendMessage message = null;
    private TextView dialogText;
    private String recordResult, speakerName;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        setTitle(R.string.dialog_activity_name);


        Button generateGoalButton = findViewById(R.id.generateGoalButton);
        generateGoalButton.setOnClickListener(generateGoalButtonListener);

        Button restartButton = findViewById(R.id.restart);
        restartButton.setOnClickListener(restartListener);

        dialogText = findViewById(R.id.dialogText);
        dialogText.setMovementMethod(new ScrollingMovementMethod());
        toggleButton = findViewById(R.id.toggleButton);
        speakerName = getIntent().getStringExtra("speakerName"); //null if not set
        if (speakerName == null) speakerName = "Jean";
        image = findViewById(R.id.imgfruit);

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

    @Override
    public void onInit(int i) {
        connect();
    }

    /*@Override
    public void onBackPressed() {
        makeToast("BackPressed");
        message.close();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startActivity(new Intent(DialogActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            message.close();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(DialogActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        return super.onKeyDown(keyCode, event);
    }

    private View.OnClickListener generateGoalButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (message != null) {
                if(message.isStarted())
                {
                    message.stop();
                    message.start();
                }
                else
                {
                    message.generateGoal();
                }

            }
        }
    };

    private View.OnClickListener restartListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            message.close();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(DialogActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
            if (recordResult.toLowerCase().equals("stop")) {
                speech.stopListening();
            }
            else {
                message.send(recordResult);
            }
        }
    }

    @Override
    protected void recordAudioSpeakToText() {
        speech.startListening(recognizerIntent);
    }

    protected void connect()
    {
        if(message != null)
        {
            message.close();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            thread = new Thread(message);
            thread.start();
        }
        else
        {
            message = new SendMessage();
            thread = new Thread(message);
            thread.start();
        }
    }

    private class SendMessage implements Runnable {

        private Socket sock;
        private BufferedReader in;
        private Handler handler = new Handler();

        private boolean started = false;

        private void close()
        {
            try {
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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

        private void setDialogText(final String text, final String author) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (author.isEmpty()) {
                        dialogText.setText(text);
                    }
                    else {
                        String boldText = "<b>" + author + " - </b>";
                        dialogText.append(Html.fromHtml("<br>" + boldText + text));
                    }

                    final int scrollAmount = dialogText.getLayout()
                            .getLineTop(dialogText.getLineCount()) - dialogText.getHeight();

                    if (scrollAmount > 0)
                        dialogText.scrollTo(0, scrollAmount);
                    else
                        dialogText.scrollTo(0, 0);
                }
            });
        }

        private void setFruitImage(final int fruitId) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    image.setImageResource(fruitId);
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

                setLocuteur(speakerName);
                start();

                String fromClient;

                while(!sock.isClosed())
                {
                    try
                    {
                        fromClient = in.readLine();
                        System.out.println("DEBUG  >>  " + fromClient);
                        if(fromClient != null && fromClient.startsWith("t;"))
                        {
                            System.out.println("Receive: " + fromClient.substring(2));
                            setDialogText(fromClient.substring(2), getString(R.string.server_name));
                            say(fromClient.substring(2), true);
                            Thread.sleep(100);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    recordAudioSpeakToText();
                                }
                            });
                        }
                        else if(fromClient != null && fromClient.startsWith("f;"))
                        {
                            String str_fruit = fromClient.substring(2);
                            System.out.println("Receive fruit: " + str_fruit);

                            if(str_fruit.equals("fraise"))
                            {
                                setFruitImage(R.drawable.fraise);
                            }
                            else if(str_fruit.equals("citron"))
                            {
                                setFruitImage(R.drawable.citron);
                            }
                            else if(str_fruit.equals("poire"))
                            {
                                setFruitImage(R.drawable.poire);
                            }
                            else if(str_fruit.equals("pomme"))
                            {
                                setFruitImage(R.drawable.pomme);
                            }
                            else if(str_fruit.equals("framboise"))
                            {
                                setFruitImage(R.drawable.framboise);
                            }
                            else if(str_fruit.equals("aubergine"))
                            {
                                setFruitImage(R.drawable.aubergine);
                            }
                            else
                            {
                                setFruitImage(android.R.color.transparent);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            catch (java.net.UnknownHostException e)
            {
                e.printStackTrace();
            }
            catch (java.io.IOException e)
            {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public boolean isStarted()
        {
            return started;
        }


        protected void start()
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try
            {

                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                out.println("start");
                started = true;
            }
            catch (java.io.IOException e)
            {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        protected void stop()
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try
            {
                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                out.println("stop");
                setDialogText("...", "");
                started = false;
            }
            catch (java.io.IOException e)
            {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        protected void setLocuteur(String locuteur)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try
            {
                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                out.println("l;"+locuteur);
            }
            catch (java.io.IOException e)
            {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
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
                setDialogText(message, speakerName);
            }
            catch (java.io.IOException e)
            {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
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
                setDialogText("...", "");
            }
            catch (java.io.IOException e)
            {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

}
