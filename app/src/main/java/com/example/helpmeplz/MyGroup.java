package com.example.helpmeplz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyGroup extends AppCompatActivity {
    private Button find_null_time;
    private ListView listView_member;
    private TextView txtView_groupName;

    private FirebaseAuth firebaseAuth;
    List<String> memberList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);

        find_null_time = findViewById(R.id.find_null_time);
        listView_member = findViewById(R.id.listView_member);
        txtView_groupName = findViewById(R.id.txtView_groupName);
        firebaseAuth = FirebaseAuth.getInstance();



        String groupName = getIntent().getStringExtra("groupName");

        txtView_groupName.setText(groupName);

        Intent intent1 = getIntent();
        String groupID = intent1.getStringExtra("groupId");
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference membersRef = FirebaseDatabase.getInstance().getReference().child("groups").child("users").child(userId).child(groupID).child("members");

        membersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot memberSnapshot : dataSnapshot.getChildren()) {
                    String memberId = memberSnapshot.getKey();
                    memberList.add(memberId);
                }

                findNullTime();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 에러 처리
                Log.e("Firebase", "Error fetching members: " + databaseError.getMessage());
            }
        });



//        find_null_time.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MyGroup.this, FindNullTime.class);
//                intent.putExtra("memberList", memberList);
//                startActivity(intent);
//            }
//        });




    }

    private void findNullTime() {
        find_null_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyGroup.this, FindNullTime.class);
                intent.putExtra("memberList",  (ArrayList<String>) memberList);
                startActivity(intent);
            }
        });
    }
}