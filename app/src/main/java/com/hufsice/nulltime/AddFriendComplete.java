package com.hufsice.nulltime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class AddFriendComplete extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_complete);

        //button, textview선언
        Button button_Accept2 = findViewById(R.id.button_next2);
        TextView view = findViewById(R.id.textView_checkfriend_id);

        //addfriend에서 넘어온 값 받기
        Intent intent1 = getIntent();
        String friendId = intent1.getStringExtra("nickname");

        Log.d("MainActivity", "ValueEventListener - onDataChange : " + friendId + " in CheckFriend onCreate");
        view.setText(friendId);

        //다음 버튼 누를시 메뉴로 이동
        button_Accept2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent5 = new Intent(AddFriendComplete.this, Menu.class);
                startActivity(myIntent5);
                finish();
            }
        });
    }
}