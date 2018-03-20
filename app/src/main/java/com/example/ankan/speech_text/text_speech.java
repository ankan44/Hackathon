package com.example.ankan.speech_text;

/**
 * Created by ankan on 18/03/18.
 */

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class text_speech extends Activity {
    TextToSpeech t1;
    TextView ed1;
    TextView ed2;
    Button b1;
    TextToSpeech t2;
    String voiceInput;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_speech);
        ed1 = (TextView) findViewById(R.id.editText);
        b1 = (Button) findViewById(R.id.button);
        ed2 = (TextView) findViewById(R.id.editText1);

        final Intent intent = getIntent();


        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent != null) {
                    String toSpeak = intent.getStringExtra("text_name");
                    ed1.setText(toSpeak);
                    Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                    t1.speak(toSpeak, TextToSpeech.QUEUE_ADD, null);

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // this code will be executed after 2 seconds
                            t2.speak("Thank You", TextToSpeech.QUEUE_ADD, null);
                        }
                    }, 2000);

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // this code will be executed after 2 seconds
                            t2.speak("Do you want to send SMS? Reply with yes or no ", TextToSpeech.QUEUE_ADD, null);
                        }
                    }, 3000);


                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // this code will be executed after 2 seconds
                            askSpeechInput();
                            //ContactSearch(voiceInput);

                        }
                    }, 7000);
                    //new Timer().schedule(new TimerTask() {
                    //@Override
                    //public void run()
                    //{
                    //this code will be executed after 2 seconds
                    //    ContactSearch(voiceInput);

                    //}
                    //}, 1000);

                    // this code will be executed after 2 seconds


                }
            }
        });

        t2 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t2.setLanguage(Locale.US);
                    String toSpeak = "Converting the text";
                    t2.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    //askSpeechInput();

                }
            }
        });

        //String toSpeak = ed1.getText().toString();


    }

    public void onPause() {
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }
    // Showing google speech input dialog

    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Reply with yes or now");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    // Receiving speech input

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    voiceInput = result.get(0);
                    ed2.setText(voiceInput);
                    ContactSearch(voiceInput);

                }
                break;
            }

        }
    }

    private void askSpeechInput1() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Reply with a ten digit number");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    private void ContactSearch(String stuff) {
        if (stuff.equals("yes")) {
            t2.speak("Speak the number", TextToSpeech.QUEUE_ADD, null);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    // this code will be executed after 2 seconds
                    askSpeechInput1();

                    //SendSms();


                    //ContactSearch(voiceInput);

                }
            }, 2000);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    // this code will be executed after 2 seconds


                    SendSms(voiceInput);


                    //ContactSearch(voiceInput);

                }
            }, 17000);


        } else {

            t2.speak("Thank you for using our app", TextToSpeech.QUEUE_ADD, null);
        }
    }

    private void SendSms(String voiceInput) {
        Intent intent = new Intent(text_speech.this, MainActivity.class);

        String phone = voiceInput;
        Uri uri = Uri.parse("Hey, I am blind but you suck");
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, uri.toString(), null, null);
        if (ActivityCompat.checkSelfPermission(text_speech.this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(text_speech.this, new String[]{Manifest.permission.SEND_SMS}, 1);
            return;

        }
        startActivity(intent);
    }
}
    // Receiving speech input




