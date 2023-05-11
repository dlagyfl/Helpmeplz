package com.example.helpmeplz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MyGroup extends AppCompatActivity {

    private Button btn_createG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);

        btn_createG = findViewById(R.id.btn_createG);

        btn_createG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyGroup.this, CreateGroup.class);
                startActivity(intent);
            }
        });
    }
}