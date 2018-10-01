package com.example.duret.lilia_alize_demo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class DialogActivity extends RecordActivity {

    private SendMessage message;
    private Thread thread;

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
            message.start();
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
                    String recordResult = result.get(0).toLowerCase();

                    message.send(recordResult);
                }
                break;
            }
        }
    }



    private class SendMessage implements Runnable {

        private Socket sock;
        private BufferedReader in;

        public void run() {

            try
            {

                sock = new Socket("10.126.2.117", 8080); //TODO: menu connection with text input for host and port
                in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);

                String fromClient;
                while(true)
                {
                    fromClient = in.readLine();
                    if(fromClient != null && fromClient.startsWith("t;"))
                    {
                        System.out.println("Receive: " + fromClient.substring(2));
                        TextView textView = (TextView)findViewById(R.id.text);
                        textView.setText(fromClient.substring(2));
                        recordAudioSpeakToText();
                    }
                    else if(fromClient != null && fromClient.startsWith("f;"))
                    {
                        System.out.println("Receive fruit: " + fromClient.substring(2));
                        TextView textView = (TextView)findViewById(R.id.fruit);
                        textView.setText(fromClient.substring(2));
                    }
                }
            }
            catch (java.net.UnknownHostException e)
            {
                System.err.println(e);
                //makeToast(e.getMessage());
            }
            catch (java.io.IOException e)
            {
                System.err.println(e);
                //makeToast(e.getMessage());
            }
            catch (Exception e)
            {
                System.err.println(e);
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
                System.err.println(e);
                //makeToast(e.getMessage());
            }
            catch (Exception e)
            {
                System.err.println(e);
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
                System.err.println(e);
                //makeToast(e.getMessage());
            }
            catch (Exception e)
            {
                System.err.println(e);
            }


        }
    }

}
