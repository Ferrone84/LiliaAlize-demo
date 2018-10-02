package com.example.duret.lilia_alize_demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class DialogActivity extends RecordActivity {

    private Thread thread;
    private SendMessage message = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        setTitle(R.string.dialog_activity_name);

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(startButtonListener);

        Button reconnectButton = findViewById(R.id.reconnectButton);
        reconnectButton.setOnClickListener(reconnectButtonListener);
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
                String ip = ((EditText)findViewById(R.id.editIP)).getText().toString();
                int port = Integer.parseInt(((EditText)findViewById(R.id.editPORT)).getText().toString());
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
                            System.out.println("Receive fruit: " + fromClient.substring(2));
                            setTextofView(fromClient.substring(2), R.id.fruit);
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
    }

}
