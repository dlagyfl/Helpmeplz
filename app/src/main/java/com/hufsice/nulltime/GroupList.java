package com.hufsice.nulltime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hufsice.nulltime.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class GroupList extends AppCompatActivity implements GroupAdapter.GroupItemClickListener {

    //레이아웃에서 사용하는 뷰 요소들의 변수 선언
    private RecyclerView recyclerViewGroup;
    private ImageButton btn_makeGroup;
    //Firebase 관련 객체들의 변수 선언
    private DatabaseReference database;
    private FirebaseAuth firebaseAuth;

    private GroupAdapter groupAdapter;
    private List<String> groupList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        //변수 연결
        recyclerViewGroup = findViewById(R.id.recyclerViewGroup);
        btn_makeGroup = findViewById(R.id.btn_makeGroup);

        //Firebase 실시간 데이터베이스와 Firebase 인증을 사용하기 위해 필요한 초기화
        database = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        //그룹명 저장할 list 생성
        groupList = new ArrayList<>();

        //RecyclerView 선형 형태로 세팅
        recyclerViewGroup.setLayoutManager(new LinearLayoutManager(this));

        //그룹명 RecyclerView에 보여주기 위한 메소드 호출
        getGroups();
        //그룹 어댑터 생성 및 RecyclerView에 설정
        groupAdapter = new GroupAdapter(groupList, this);
        recyclerViewGroup.setAdapter(groupAdapter);

        //그룹 생성하기 버튼 클릭시에 CreateGroup Activity로 intent
        btn_makeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupList.this, CreateGroup.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //Firebase에서 그룹 목록을 가져오는 메소드
    private void getGroups() {
        //현재 사용자의 UID를 가져옴
        String userId = firebaseAuth.getCurrentUser().getUid();

        //사용자가 포함된 그룹 정보 접근
        DatabaseReference userRef = database.child("groups").child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupList.clear();

                //데이터 스냅샷을 순회하며 그룹명을 가져와 리스트에 추가
                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    DatabaseReference groupRef = groupSnapshot.getRef();

                    groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //그룹명 가져오기
                            String groupName = dataSnapshot.child("name").getValue(String.class);

                            //그룹명을 리스트에 추가
                            groupList.add(groupName);

                            //어댑터에 데이터 변경을 알림
                            groupAdapter.notifyDataSetChanged();
                        }

                        //데이터 읽기 실패할 경우 오류 발생
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("Firebase", databaseError.getMessage());
                        }
                    });
                }
            }

            //데이터 읽기 실패할 경우 오류 발생
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", databaseError.getMessage());
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

    //그룹 항목 클릭 이벤트 처리
    @Override
    public void onGroupItemClick(int position) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        //클릭된 그룹의 위치(position)를 통해 그룹명 가져오기
        String selectedGroup = groupList.get(position);

        //사용자 그룹 정보에 접근
        DatabaseReference userRef = database.child("groups").child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //사용자의 그룹 목록을 순회하며 클릭된 그룹을 찾음
                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    String groupId = groupSnapshot.getKey();

                    //해당 그룹 정보에 접근
                    DatabaseReference groupRef = database.child("groups").child("users").child(userId).child(groupId);
                    groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String groupName = dataSnapshot.child("name").getValue(String.class); // Get the group name

                            if (selectedGroup.equals(groupName)) {
                                //MyGroup Activity로 이동하는 인텐트 생성
                                Intent intent = new Intent(GroupList.this, MyGroup.class);
                                intent.putExtra("groupId", groupId);
                                intent.putExtra("groupName", selectedGroup);
                                startActivity(intent);
                                finish();

                            }
                        }

                        //데이터 읽기 실패시 오류 발생
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("Firebase", databaseError.getMessage());
                        }
                    });
                }
            }

            //데이터 읽기 실패시 오류 발생
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", databaseError.getMessage());
            }
        });
    }

    @Override
    public void onGroupItemLongClick(int position) {
        // 그룹 항목 롱클릭 이벤트 처리
        String groupName = groupList.get(position);
        Toast.makeText(this, groupName, Toast.LENGTH_SHORT).show();
    }
}