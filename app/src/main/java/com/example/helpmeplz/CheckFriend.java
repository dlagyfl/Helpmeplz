package com.example.helpmeplz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;


public class CheckFriend extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_friend);

        Button button_Accept2 = findViewById(R.id.button_next2);

        TextView view = findViewById(R.id.textView_checkfriend_id);

        Intent intent1 = getIntent();
        String friendId = intent1.getStringExtra("nickname");

        Log.d("MainActivity", "ValueEventListener - onDataChange : " + friendId + " in CheckFriend onCreate");
        view.setText(friendId);

        button_Accept2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent5 = new Intent(CheckFriend.this, Menu.class);
                startActivity(myIntent5);
                finish();
            }
        });
    }
}