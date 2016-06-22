/*
    시스템에 의해 무조건 액티비티를 생성하면서 태그를 처리해야 하는 문제를 해결해본다

    1.현재의 액티비티 유지하기  - Task 의 single top 이용
    2.특정 시점에 인텐트를 사용하기 - PendingIntent 이용
 */
package com.example.zino.nfcreadstep2;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    String TAG=this.getClass().getName();
    NfcAdapter nfcAdapter;
    TextView txt_msg;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_msg =(TextView) findViewById(R.id.txt_msg);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        IntentFilter[] filters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent,filters, null);

        Log.d(TAG, "MainActivity is "+this);

        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        readTag(intent);
        super.onNewIntent(intent);
    }

    //nfc 읽기
    public void readTag(Intent intent) {
        Parcelable[] message = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        Log.d(TAG, "message is " + message);
        if(message==null){
            showMsg("읽혀진 데이터가 없습니다.");
            return;
        }
        for (int i = 0; i < message.length; i++) {
            NdefMessage ndefMessage = (NdefMessage) message[i];

            NdefRecord[] records = ndefMessage.getRecords();
            for (int a = 0; a < records.length; a++) {
                NdefRecord record = records[a];

                byte[] b = record.getPayload();
                String msg = decode(b);
                txt_msg.setText(msg);
            }

        }
    }

    public String decode(byte[] buf) {
        String strText = "";
        String textEncoding = ((buf[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
        int langCodeLen = buf[0] & 0077;

        try {
            strText = new String(buf, langCodeLen + 1, buf.length - langCodeLen - 1, textEncoding);
        } catch (Exception e) {
            Log.d("tag1", e.toString());
        }
        return strText;
    }

    public void showMsg(String msg) {
        Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
    }
}
