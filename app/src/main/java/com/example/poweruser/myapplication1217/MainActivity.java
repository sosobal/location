package com.example.poweruser.myapplication1217;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    protected Button btConnect;
    protected TextView tvRecog, TextVoice;

    protected TextToSpeech tts;
    private static final int CODE_CONNECT = 1111, CODE_CALL1 = 2222, CODE_CALL2 = 3333, CODE_SMS1 = 4444, CODE_SMS2 = 5555;
    protected boolean bService = false;
    private String Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btLocation = (Button) findViewById(R.id.btLocation);
        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                startActivity(intent);
            }
        });

        Button btInfor = (Button) findViewById(R.id.btInfor);
        btInfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.tashu.or.kr/userStationAction.do?process=stationTotalList"));
                startActivity(intent);
            }
        });

        Button btMusic = (Button) findViewById(R.id.btMusic);
        btMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MusicActivity.class);
                startActivity(intent);
            }
        });

        tvRecog = (TextView) findViewById(R.id.tvRecog);
        tts = new TextToSpeech(this, this);

        TextVoice = (TextView) findViewById(R.id.TextVoice);
        btConnect = (Button) findViewById(R.id.btConnect);
        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hello = TextVoice.getText().toString();
                speakStr(hello);

                voiceRecog(CODE_CONNECT);
            }
        });

    }
    private void voiceRecog(int nCode) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak.");
        startActivityForResult(intent, nCode);
    }

    private void speakStr(String str) {
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null, null);
        while (tts.isSpeaking()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String getPhoneNumFromName(String sName) {
        String sPhoneNum = "";
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, Uri.encode(sName));
        String[] arProjection = new String[]{ContactsContract.Contacts._ID};
        Cursor cursor = getContentResolver().query(uri, arProjection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String sId = cursor.getString(0);
            String[] arProjNum = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            String sWhereNum = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = ?";
            String[] sWhereNumParam = new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, sId};
            Cursor cursorNum = getContentResolver().query(ContactsContract.Data.CONTENT_URI, arProjNum, sWhereNum, sWhereNumParam, null);
            if (cursorNum != null && cursorNum.moveToFirst()) {
                sPhoneNum = cursorNum.getString(0);
            }
            cursorNum.close();
        }
        cursor.close();
        return sPhoneNum;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == CODE_CONNECT) {
                ArrayList<String> arList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String str = arList.get(0);
                if (str.equals("전화 걸기") == true) {
                    speakStr("누구에게 전화 걸까요");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    voiceRecog(CODE_CALL1);
                } else if (str.equals("문자 하기") == true) {
                    speakStr("누구에게 문자 할까요");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    voiceRecog(CODE_SMS1);
                }

            }else if (requestCode == CODE_CALL1) {
                ArrayList<String> arList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                Name = arList.get(0);
                tvRecog.setText(Name);

                if (Name.equals(Name) == true) {
                    speakStr(Name + "에게 전화를 연결합니다");
                    Toast.makeText(getApplicationContext(), Name, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getPhoneNumFromName(Name)));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(intent);
                }

            } else if (requestCode == CODE_SMS1) {
                ArrayList<String> arList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                Name = arList.get(0);
                tvRecog.setText(Name);

                if (Name.equals(Name) == true) {
                    speakStr(Name + "에게 문자 보낼 수 있도록 연결합니다");
                    Toast.makeText(getApplicationContext(), Name, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:" + getPhoneNumFromName(Name)));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(intent);
                }

            }
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.KOREAN);
            tts.setPitch(1.0f);
            tts.setSpeechRate(1.0f);
        }
    }
}


