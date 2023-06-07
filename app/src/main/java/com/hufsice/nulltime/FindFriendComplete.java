package com.hufsice.nulltime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class FindFriendComplete extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend_complete);

        Button button_MyFriend = findViewById(R.id.button_next3);

        button_MyFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(FindFriendComplete.this, Menu.class);
                startActivity(myIntent);
                finish();
            }
        });
    }
}
