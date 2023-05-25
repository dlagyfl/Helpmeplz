package com.example.helpmeplz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MyGroup extends AppCompatActivity {
    private Button btn;
    private ListView listView_member;
    private TextView txtView_groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);

        btn = findViewById(R.id.btn);
        listView_member = findViewById(R.id.listView_member);
        txtView_groupName = findViewById(R.id.txtView_groupName);

        String groupName = getIntent().getStringExtra("groupName");

        txtView_groupName.setText(groupName);




    }
}