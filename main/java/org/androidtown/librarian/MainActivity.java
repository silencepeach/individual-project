package org.androidtown.librarian;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
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
