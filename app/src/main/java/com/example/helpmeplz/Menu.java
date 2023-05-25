package com.example.helpmeplz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button button_MyFriend = findViewById(R.id.button_MyFriend);
        Button button_myGroup = findViewById(R.id.button_myGroup);
        Button button_myTable = findViewById(R.id.button_myTable);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            String uid=user.getUid();
            Log.d("uid",uid);
        }


        button_MyFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Menu.this, MyFriend.class);
                startActivity(myIntent);
                finish();
            }
        });

        button_myGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, GroupList.class);
                startActivity(intent);
                finish();
            }
        });

        button_myTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, UploadImage.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
