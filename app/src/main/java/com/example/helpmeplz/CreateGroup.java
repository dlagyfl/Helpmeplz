package com.example.helpmeplz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateGroup extends AppCompatActivity implements MemberAdapter.OnMemberCheckedChangeListener {

    private EditText editTextGroupName;
    private Button buttonCreateGroup;
    private DatabaseReference reference;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Member> selectedMembers;

    private MemberAdapter friendAdapter;
    private RecyclerView recyclerViewFriend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        editTextGroupName = findViewById(R.id.editTextGroupName);
        buttonCreateGroup = findViewById(R.id.buttonCreateGroup);

        reference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        selectedMembers = new ArrayList<>();
        friendAdapter = new MemberAdapter(selectedMembers, this);
        recyclerViewFriend = findViewById(R.id.rv_members);
        recyclerViewFriend.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFriend.setAdapter(friendAdapter);

        loadFriendList();

        buttonCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = editTextGroupName.getText().toString();

                ArrayList<Member> selectedMembers = friendAdapter.getSelectedMembers();

                GroupNameInfo newGroup = new GroupNameInfo(groupName, selectedMembers);
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
        //mDatabase.child("users").child(uid).child("name").setValue(name);
        String userId = firebaseAuth.getCurrentUser().getUid();
        String groupId = reference.child("groups").child("users").child(userId).push().getKey();

        HashMap<String, Object> groupMap = new HashMap<>();
        groupMap.put("name", group.getName());

        HashMap<String, Object> memberMap = new HashMap<>();
        for(Member member : selectedMembers){
            memberMap.put(member.getId(), true);
        }
        groupMap.put("members", memberMap);

        reference.child("groups").child("users").child(userId).child(groupId).updateChildren(groupMap)
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


    private void loadFriendList() {
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference friendRef = reference.child("users").child(userId).child("friendlist");

        friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                selectedMembers.clear(); // Clear the existing friend list

                // Read friend data from dataSnapshot and add them to friendList
                for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                    String friendId = friendSnapshot.getKey();
                    String friendName = friendSnapshot.child("name").getValue(String.class);
                    selectedMembers.add(new Member(friendName, friendId));
                }

                // Update the RecyclerView
                friendAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    // ...

    @Override
    public void onMemberCheckedChange(Member member, boolean isChecked) {
        // Handle member checkbox state change
        if (isChecked) {
            // Add member to selected members
            // You can perform any additional logic here if needed
            selectedMembers.add(member);
        } else {
            // Remove member from selected members
            // You can perform any additional logic here if needed
            selectedMembers.remove(member);
        }
    }
}