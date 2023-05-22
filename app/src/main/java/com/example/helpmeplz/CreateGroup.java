package com.example.helpmeplz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateGroup extends AppCompatActivity {

    private EditText editTextGroupName;
    private Button buttonCreateGroup;
    private DatabaseReference database;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        editTextGroupName = findViewById(R.id.editTextGroupName);
        buttonCreateGroup = findViewById(R.id.buttonCreateGroup);

        database = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        buttonCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = editTextGroupName.getText().toString();

                GroupNameInfo newGroup = new GroupNameInfo(groupName);
                createGroup(newGroup);
            }
        });

        editTextGroupName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTextGroupName.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }

    private void createGroup(GroupNameInfo group) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        String groupId = database.child("groups").child("users").child(userId).push().getKey();
        database.child("groups").child("users").child(userId).child(groupId).setValue(group)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 그룹 생성 성공 시 처리할 작업
                        Toast.makeText(CreateGroup.this, "Group created successfully!", Toast.LENGTH_SHORT).show();
                        editTextGroupName.setText(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 그룹 생성 실패 시 처리할 작업
                        Toast.makeText(CreateGroup.this, "Failed to create group.", Toast.LENGTH_SHORT).show();
                        editTextGroupName.setText(null);
                    }
                });
    }
}