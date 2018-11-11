package org.androidtown.librarian;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private Realm realm;
    private RecyclerView rcv;
    private org.androidtown.librarian.RcvAdapter rcvAdapter;
    private org.androidtown.librarian.Memo memo_Main;
    public List<org.androidtown.librarian.Memo> list = new ArrayList<>();

    AlarmManager alarm_manager;
    TimePicker alarm_timepicker;
    Context context;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        textView = findViewById(R.id.mContextTextView);
        rcv = findViewById(R.id.rcvMain);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcv.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));
        rcv.setLayoutManager(linearLayoutManager);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        RealmResults<org.androidtown.librarian.Memo> realmResults = realm.where(org.androidtown.librarian.Memo.class)
                .findAllAsync();

        for(org.androidtown.librarian.Memo memo : realmResults) {
            list.add(new org.androidtown.librarian.Memo(memo.getText()));
            rcvAdapter = new org.androidtown.librarian.RcvAdapter(MainActivity.this,list);
            rcv.setAdapter(rcvAdapter);
        }

        FloatingActionButton button = findViewById(R.id.floating);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, org.androidtown.librarian.AddActivity.class);
                startActivityForResult(intent,1);
            }
        });

        this.context = this;

        // �˶��Ŵ��� ����
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Ÿ����Ŀ ����
        alarm_timepicker = findViewById(R.id.time_picker);

        // Calendar ��ü ����
        final Calendar calendar = Calendar.getInstance();

        // �˶����ù� intent ����
        final Intent my_intent = new Intent(this.context, Alarm_Reciver.class);

        // �˶� ���� ��ư
        Button alarm_on = findViewById(R.id.btn_start);
        alarm_on.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                // calendar�� �ð� ����
                calendar.set(Calendar.HOUR_OF_DAY, alarm_timepicker.getHour());
                calendar.set(Calendar.MINUTE, alarm_timepicker.getMinute());

                // �ð� ������
                int hour = alarm_timepicker.getHour();
                int minute = alarm_timepicker.getMinute();
                Toast.makeText(MainActivity.this,"Alarm ���� " + hour + "�� " + minute + "��",Toast.LENGTH_SHORT).show();

                // reveiver�� string �� �Ѱ��ֱ�
                my_intent.putExtra("state","alarm on");

                pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, my_intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                // �˶�����
                alarm_manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        pendingIntent);
            }
        });

        // �˶� ���� ��ư
        Button alarm_off = findViewById(R.id.btn_finish);
        alarm_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Alarm ����",Toast.LENGTH_SHORT).show();
                // �˶��Ŵ��� ���
                alarm_manager.cancel(pendingIntent);

                my_intent.putExtra("state","alarm off");

                // �˶����
                sendBroadcast(my_intent);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode == RESULT_OK) {

            String title = data.getStringExtra("title");
            String time = data.getStringExtra("time");
            Toast.makeText(this,title + "," + time,Toast.LENGTH_SHORT).show();

            realm.beginTransaction();
            memo_Main = realm.createObject(org.androidtown.librarian.Memo.class);
            memo_Main.setText(title);

            realm.commitTransaction();

            RealmResults<org.androidtown.librarian.Memo> realmResults = realm.where(org.androidtown.librarian.Memo.class)
                    .equalTo("text",title)
                    .findAllAsync();

            list.add(new org.androidtown.librarian.Memo(title));
            rcvAdapter = new org.androidtown.librarian.RcvAdapter(MainActivity.this,list);
            rcv.setAdapter(rcvAdapter);

        }
    }
}
