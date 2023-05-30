package com.example.helpmeplz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class GroupList extends AppCompatActivity implements GroupAdapter.GroupItemClickListener {

    private RecyclerView recyclerViewGroup;
    private ImageButton btn_makeGroup;
    private GroupAdapter groupAdapter;
    private List<String> groupList;
    private DatabaseReference database;
    private FirebaseAuth firebaseAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        recyclerViewGroup = findViewById(R.id.recyclerViewGroup);
        btn_makeGroup = findViewById(R.id.btn_makeGroup);

        database = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        groupList = new ArrayList<>();

        recyclerViewGroup.setLayoutManager(new LinearLayoutManager(this));

        groupAdapter = new GroupAdapter(groupList, this);
        recyclerViewGroup.setAdapter(groupAdapter);

        getGroups();



        btn_makeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupList.this, CreateGroup.class);
                startActivity(intent);
                finish();
            }
        });
    } //onCreate

    private void getGroups() {
        String userId = firebaseAuth.getCurrentUser().getUid();

        DatabaseReference userRef = database.child("groups").child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupList.clear();

                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    DatabaseReference groupRef = groupSnapshot.getRef();

                    groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Get the group name
                            String groupName = dataSnapshot.child("name").getValue(String.class);

                            // Add the group name to the list
                            groupList.add(groupName);

                            // Notify the adapter about the data change
                            groupAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("Firebase", "Error fetching group name: " + databaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching groups: " + databaseError.getMessage());
            }
        });
    }
//    @Override
//    public void onGroupItemClick(int position) {
//        String selectedGroup = groupList.get(position);
//
//        Intent intent = new Intent(GroupList.this, MyGroup.class);
//        intent.putExtra("groupName", selectedGroup);
//        startActivity(intent);
//    }
    @Override
    public void onGroupItemClick(int position) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        String selectedGroup = groupList.get(position);

        DatabaseReference userRef = database.child("groups").child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    String groupId = groupSnapshot.getKey();

                    DatabaseReference groupRef = database.child("groups").child("users").child(userId).child(groupId);
                    groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String groupName = dataSnapshot.child("name").getValue(String.class); // Get the group name

                            if (selectedGroup.equals(groupName)) {
                                Intent intent = new Intent(GroupList.this, MyGroup.class);
                                intent.putExtra("groupId", groupId);
                                intent.putExtra("groupName", selectedGroup);
                                startActivity(intent);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("Firebase", "Error fetching group data: " + databaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching user groups: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onGroupItemLongClick(int position) {
        // 그룹 항목 롱클릭 이벤트 처리
        String groupName = groupList.get(position);
        Toast.makeText(this, "Long clicked: " + groupName, Toast.LENGTH_SHORT).show();
    }
}