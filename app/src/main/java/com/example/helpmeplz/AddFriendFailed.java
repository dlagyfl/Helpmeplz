package com.example.helpmeplz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AddFriendFailed extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_failed);

        Button buttonPrev = findViewById(R.id.button_prev);
        Button buttonHome = findViewById(R.id.button_home);

        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(AddFriendFailed.this, FindFriend.class);
                startActivity(myIntent);
                finish();
            }
        });
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(AddFriendFailed.this, MyFriend.class);
                startActivity(myIntent);
                finish();
            }
        });
    }
}
