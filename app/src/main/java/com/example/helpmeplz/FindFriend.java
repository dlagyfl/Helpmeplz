package com.example.helpmeplz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class FindFriend extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        Button button_Prev = findViewById(R.id.button_prev);
        Button button_next = findViewById(R.id.button_next);

        button_Prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(FindFriend.this, MyFriend.class);
                startActivity(myIntent);
                finish();
            }
        });
        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(FindFriend.this, AddFriendComplete.class);
                startActivity(myIntent);
                finish();
            }
        });
    }
}
