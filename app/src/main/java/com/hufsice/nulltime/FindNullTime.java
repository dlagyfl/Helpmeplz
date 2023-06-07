package com.hufsice.nulltime;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hufsice.nulltime.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FindNullTime extends AppCompatActivity {

    // UI 요소 선언
    private Button back_nullTime;
    View monday1, monday2, monday3, monday4, monday5, monday6, monday7, monday8, monday9, monday10, monday11, monday12, monday13;
    View tuesday1, tuesday2, tuesday3, tuesday4, tuesday5, tuesday6, tuesday7, tuesday8, tuesday9, tuesday10, tuesday11, tuesday12, tuesday13;
    View wednesday1, wednesday2, wednesday3, wednesday4, wednesday5, wednesday6, wednesday7, wednesday8, wednesday9, wednesday10, wednesday11, wednesday12, wednesday13;
    View thursday1, thursday2, thursday3, thursday4, thursday5, thursday6, thursday7, thursday8, thursday9, thursday10, thursday11, thursday12, thursday13;
    View friday1, friday2, friday3, friday4, friday5, friday6, friday7, friday8, friday9, friday10, friday11, friday12, friday13;


    // 이미지 이름을 저장할 List 선언
    private List<String> imageNames;

    // 이미지를 저장할 List 선언
    private List<Bitmap> imageList;

    // Firebase Realtime Database에 대한 참조 객체
    private DatabaseReference databaseRef;

    // 이미지 개수를 세는 변수
    private int count_image = 0;

    // 사람 수를 세는 변수
    private int count_people = 0;
    //OpenCV 라이브러리를 초기화
    static {
        if (!OpenCVLoader.initDebug()) {

        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_null_time);

        // UI 요소 초기화
        monday1 = findViewById(R.id.monday1);
        monday2 = findViewById(R.id.monday2);
        monday3 = findViewById(R.id.monday3);
        monday4 = findViewById(R.id.monday4);
        monday5 = findViewById(R.id.monday5);
        monday6 = findViewById(R.id.monday6);
        monday7 = findViewById(R.id.monday7);
        monday8 = findViewById(R.id.monday8);
        monday9 = findViewById(R.id.monday9);
        monday10 = findViewById(R.id.monday10);
        monday11 = findViewById(R.id.monday11);
        monday12 = findViewById(R.id.monday12);
        monday13 = findViewById(R.id.monday13);

        // UI 요소 초기화
        tuesday1 = findViewById(R.id.tuesday1);
        tuesday2 = findViewById(R.id.tuesday2);
        tuesday3 = findViewById(R.id.tuesday3);
        tuesday4 = findViewById(R.id.tuesday4);
        tuesday5 = findViewById(R.id.tuesday5);
        tuesday6 = findViewById(R.id.tuesday6);
        tuesday7 = findViewById(R.id.tuesday7);
        tuesday8 = findViewById(R.id.tuesday8);
        tuesday9 = findViewById(R.id.tuesday9);
        tuesday10 = findViewById(R.id.tuesday10);
        tuesday11 = findViewById(R.id.tuesday11);
        tuesday12 = findViewById(R.id.tuesday12);
        tuesday13 = findViewById(R.id.tuesday13);

        // UI 요소 초기화
        wednesday1 = findViewById(R.id.wednesday1);
        wednesday2 = findViewById(R.id.wednesday2);
        wednesday3 = findViewById(R.id.wednesday3);
        wednesday4 = findViewById(R.id.wednesday4);
        wednesday5 = findViewById(R.id.wednesday5);
        wednesday6 = findViewById(R.id.wednesday6);
        wednesday7 = findViewById(R.id.wednesday7);
        wednesday8 = findViewById(R.id.wednesday8);
        wednesday9 = findViewById(R.id.wednesday9);
        wednesday10 = findViewById(R.id.wednesday10);
        wednesday11 = findViewById(R.id.wednesday11);
        wednesday12 = findViewById(R.id.wednesday12);
        wednesday13 = findViewById(R.id.wednesday13);

        // UI 요소 초기화
        thursday1 = findViewById(R.id.thursday1);
        thursday2 = findViewById(R.id.thursday2);
        thursday3 = findViewById(R.id.thursday3);
        thursday4 = findViewById(R.id.thursday4);
        thursday5 = findViewById(R.id.thursday5);
        thursday6 = findViewById(R.id.thursday6);
        thursday7 = findViewById(R.id.thursday7);
        thursday8 = findViewById(R.id.thursday8);
        thursday9= findViewById(R.id.thursday9);
        thursday10 = findViewById(R.id.thursday10);
        thursday11 = findViewById(R.id.thursday11);
        thursday12 = findViewById(R.id.thursday12);
        thursday13 = findViewById(R.id.thursday13);

        // UI 요소 초기화
        friday1 = findViewById(R.id.friday1);
        friday2 = findViewById(R.id.friday2);
        friday3 = findViewById(R.id.friday3);
        friday4 = findViewById(R.id.friday4);
        friday5 = findViewById(R.id.friday5);
        friday6 = findViewById(R.id.friday6);
        friday7 = findViewById(R.id.friday7);
        friday8 = findViewById(R.id.friday8);
        friday9 = findViewById(R.id.friday9);
        friday10 = findViewById(R.id.friday10);
        friday11 = findViewById(R.id.friday11);
        friday12 = findViewById(R.id.friday12);
        friday13 = findViewById(R.id.friday13);

        back_nullTime = findViewById(R.id.back_nullTime);

        // 이미지 이름과 이미지 목록을 저장할 ArrayList 초기화
        imageNames = new ArrayList<>();
        imageList = new ArrayList<>();

        // Firebase Realtime Database에 대한 참조 객체
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // 이전 액티비티로부터 전달된 사용자 UID 목록을 저장할 변수
        Intent intent2 = getIntent();
        ArrayList<String> userUIDs = intent2.getStringArrayListExtra("memberList");

        // 이미지가 없는 사용자 UID 목록을 저장할 ArrayList 선언 및 초기화
        ArrayList<String> noImageUIDs = new ArrayList<>();

        //Menu로 이동하는 버튼
        back_nullTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindNullTime.this, Menu.class);
                startActivity(intent);
                finish();
            }
        });

        //userUIDs에 있는 모든 uid 검사
        for (String uid : userUIDs) {
            //db내 users 밑에 개인 uid 밑에 있는 자식들 검사
            DatabaseReference timetableRef = databaseRef.child("users").child(uid);
            timetableRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //개인 uid 밑에 key값이 timetable의 value값을 imageName에 저장
                    String imageName = dataSnapshot.child("timetable").getValue(String.class);
                    //개인 uid 밑에 key값이 name의 value값을 noExitImageName에 저장
                    String noExitImageName = dataSnapshot.child("name").getValue(String.class);

                    if (imageName != null) {
                        //이미지가 있으면 imageNames에 추가
                        imageNames.add(imageName);
                        count_image++;
                    } else {
                        //이미지가 없다면 noImageUIDs에 해당 이름 추가
                        noImageUIDs.add(noExitImageName);
                    }
                    //이미지 개수와 그룹 내 인원 수가 일치한지 확인하기 위한 변수 count2
                    count_people++;

                    //for문을 전부 돌렸는지 확인
                    if (count_people == userUIDs.size()) {
                        //이미지 개수랑 그룹 내 인원과 같은지 확인
                        if (count_image != count_people) {
                            //이미지가 없는 인원 전부 보여주기 위해 문자열에 추가
                            StringBuilder toastMessage = new StringBuilder();
                            for (String userName : noImageUIDs) {
                                toastMessage.append(userName + ", ");
                            }
                            //문자열 마지막에 위치한 한칸 공백과 콤마 없애기
                            if (toastMessage.length() > 0 && toastMessage.charAt(toastMessage.length() - 2) == ',') {
                                toastMessage.deleteCharAt(toastMessage.length() - 1);
                                toastMessage.deleteCharAt(toastMessage.length() - 1);
                            }
                            //이미지가 없는 인원 이름 텍스트로 보여주기
                            toastMessage.append("님의 시간표 이미지가 없습니다");
                            Toast.makeText(FindNullTime.this, toastMessage, Toast.LENGTH_SHORT).show();
                        }
                        //만약 그룹 내 전부 이미지가 있다면 downloadImageUsingHttp 메소드 수행
                        else {
                            downloadImageUsingHttp();
                        }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    //downloadImageUsingHttp() 메서드를 사용하여 Firebase Storage에서 이미지를 다운로드하고,
    //다운로드된 이미지는 Bitmap 객체로 변환하여 imageList에 저장됩니다.
    private void downloadImageUsingHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String imageUrl : imageNames) { // 이미지 URL 목록을 반복
                    try {
                        URL url = new URL(imageUrl); // 이미지 URL을 사용하여 새 URL 개체를 만든다
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // URL에 대한 연결을 연다
                        connection.setDoInput(true); // 연결이 입력을 허용하도록 지정합니다
                        connection.connect(); // 연결을 설정합니다

                        InputStream inputStream = connection.getInputStream();  // 연결에서 입력 스트림을 검색한다
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream); // 입력 스트림을 비트맵으로 디코딩한다
                        imageList.add(bitmap); // 다운로드한 이미지를 imageList에 추가

                        inputStream.close(); // 입력 스트림을 닫는다
                        connection.disconnect(); // 연결을 끊는다
                    } catch (IOException e) {
                        Log.e("DownloadImage", "이미지 다운을 실패했습니다"); // 다운로드 중에 예외가 있는 경우 오류 기록
                    }
                }

                runOnUiThread(new Runnable() { // UI 스레드에서 다음 코드를 실행
                    @Override
                    public void run() {
                        findBlackPixels();
                    } // 다운로드한 이미지에서 findBlackPixels 메서드를 호출
                });
            }
        }).start(); // 이미지 다운로드 및 처리를 위해 스레드를 시작
    }


    private void findBlackPixels() {
        // 먼저, findBlackPixels 메소드는 imageList에 두 개 이상의 이미지가 포함되어 있는지 확인한다.
        // 만약 이 조건이 충족되지 않으면 메서드는 다음 코드를 실행하지 않고 종료된다.
        if (imageList.size() >= 2) {

            //다음으로, OpenCV Utils의 bitmapToMat() 메서드를 사용하여 imageList에서 첫 번째 이미지를 OpenCV의 Mat 객체로 변환한다.
            // 그리고 startX와 startY를 28과 30으로 설정하고, width와 height 변수에 이미지의 너비와 높이를 저장한다.
            // 검은색의 픽셀을 저장할 리스트를 선언한다.
            Mat firstMat = new Mat();
            Utils.bitmapToMat(imageList.get(0), firstMat);

            int startY = 30;
            int startX = 28;

            int width = firstMat.cols();
            int height = firstMat.rows();

            List<int[]> blackList = new ArrayList<>();

            //이제 시작점인 startX와 startY로 정의된 좌표에서부터 40씩 증가하면서 반복한다.
            for (int y = startY; y < height; y += 40) {
                for (int x = startX; x < width; x += 40) {
                    int totalRed = 0;
                    int totalGreen = 0;
                    int totalBlue = 0;

                    //imageList의 이미지를 반복하여 지정된 좌표에서 픽셀을 검색하고 빨간색, 녹색 및 파란색 값을 누적하여 저장한다.
                    // 이후에는 평균 RGB 값을 계산한다.
                    for (Bitmap bitmap : imageList) {
                        Mat mat = new Mat();
                        Utils.bitmapToMat(bitmap, mat);

                        double[] pixel = mat.get(y, x);

                        totalRed += pixel[0];
                        totalGreen += pixel[1];
                        totalBlue += pixel[2];
                    }

                    int numImages = imageList.size();
                    int red = totalRed / numImages;
                    int green = totalGreen / numImages;
                    int blue = totalBlue / numImages;

                    //이 방법은 계산된 평균 색상이 특정 범위에 포함되는지 여부를 확인한다.
                    // 세 가지 색상 성분(빨간색, 녹색 및 파란색)이 모두 0에서 31 사이인지 확인한다.
                    // 이 기준을 충족하면 픽셀 좌표가 blackList에 추가된다.
                    if (red >= 0 && red <= 31 && green >= 0 && green <= 31 && blue >= 0 && blue <= 31) {
                        int[] coordinates = {x, y};
                        blackList.add(coordinates);
                    }
                }
            }

            //검은색 픽셀 좌표 값이 저장되어 있는 blackList를 applyColorToTextViews() 메서드로 보낸다.
            //applyColorToTextViews() 메서드는 검은색 픽셀의 좌표를 기준으로 특정 텍스트 뷰에 색상을 적용하는 역할을 한다.
            applyColorToTextViews(blackList);
        }
    }


    private void applyColorToTextViews(List<int[]> blackList) {
//      색상은 #FCEF85로 설정
        int color = Color.parseColor("#FCEF85");

        //리스트 내에 있는 값들을 불러오면서
        //x좌표 값과 y좌표 값을 가지고옴
        for (int[] point : blackList) {
            int x = point[0];
            int y = point[1];



            //1교시
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            if (x == 28 && y == 30) {
                TextView mon1 = (TextView) findViewById(R.id.monday1);
                monday1.setBackgroundColor(color);
                mon1.setTextColor(Color.TRANSPARENT);

            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 68 && y == 30) {
                TextView tue1 = (TextView) findViewById(R.id.tuesday1);
                tuesday1.setBackgroundColor(color);
                tue1.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 108 && y == 30) {
                TextView wed1 = (TextView) findViewById(R.id.wednesday1);
                wednesday1.setBackgroundColor(color);
                wed1.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 148 && y == 30) {
                TextView thu1 = (TextView) findViewById(R.id.thursday1);
                thursday1.setBackgroundColor(color);
                thu1.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 188 && y == 30) {
                TextView fri1 = (TextView) findViewById(R.id.friday1);
                friday1.setBackgroundColor(color);
                fri1.setTextColor(Color.TRANSPARENT);


            }

            //2교시
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 28 && y == 70) {
                TextView mon2 = (TextView) findViewById(R.id.monday2);
                monday2.setBackgroundColor(color);
                mon2.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 68 && y == 70) {
                TextView tue2 = (TextView) findViewById(R.id.tuesday2);
                tuesday2.setBackgroundColor(color);
                tue2.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 108 && y == 70) {
                TextView wed2 = (TextView) findViewById(R.id.wednesday2);
                wednesday2.setBackgroundColor(color);
                wed2.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 148 && y == 70) {
                TextView thu2 = (TextView) findViewById(R.id.thursday2);
                thursday2.setBackgroundColor(color);
                thu2.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 188 && y == 70) {
                TextView fri2 = (TextView) findViewById(R.id.friday2);
                friday2.setBackgroundColor(color);
                fri2.setTextColor(Color.TRANSPARENT);
            }


            //3교시
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 28 && y == 110) {
                TextView mon3 = (TextView) findViewById(R.id.monday3);
                monday3.setBackgroundColor(color);
                mon3.setTextColor(Color.TRANSPARENT);

            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 68 && y == 110) {
                TextView tue3 = (TextView) findViewById(R.id.tuesday3);
                tuesday3.setBackgroundColor(color);
                tue3.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 108 && y == 110) {
                TextView wed3 = (TextView) findViewById(R.id.wednesday3);
                wednesday3.setBackgroundColor(color);
                wed3.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 148 && y == 110) {
                TextView thu3 = (TextView) findViewById(R.id.thursday3);
                thursday3.setBackgroundColor(color);
                thu3.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 188 && y == 110) {
                TextView fri3 = (TextView) findViewById(R.id.friday3);
                friday3.setBackgroundColor(color);
                fri3.setTextColor(Color.TRANSPARENT);
            }



            //4교시
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 28 && y == 150) {
                TextView mon4 = (TextView) findViewById(R.id.monday4);
                monday4.setBackgroundColor(color);
                mon4.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 68 && y == 150) {
                TextView tue4 = (TextView) findViewById(R.id.tuesday4);
                tuesday4.setBackgroundColor(color);
                tue4.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 108 && y == 150) {
                TextView wed4 = (TextView) findViewById(R.id.wednesday4);
                wednesday4.setBackgroundColor(color);
                wed4.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 148 && y == 150) {
                TextView thu4 = (TextView) findViewById(R.id.thursday4);
                thursday4.setBackgroundColor(color);
                thu4.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 188 && y == 150) {
                TextView fri4 = (TextView) findViewById(R.id.friday4);
                friday4.setBackgroundColor(color);
                fri4.setTextColor(Color.TRANSPARENT);
            }


            //5교시
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 28 && y == 190) {
                TextView mon5 = (TextView) findViewById(R.id.monday5);
                monday5.setBackgroundColor(color);
                mon5.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 68 && y == 190) {
                TextView tue5 = (TextView) findViewById(R.id.tuesday5);
                tuesday5.setBackgroundColor(color);
                tue5.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 108 && y == 190) {
                TextView wed5 = (TextView) findViewById(R.id.wednesday5);
                wednesday5.setBackgroundColor(color);
                wed5.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 148 && y == 190) {
                TextView thu5 = (TextView) findViewById(R.id.thursday5);
                thursday5.setBackgroundColor(color);
                thu5.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 188 && y == 190) {
                TextView fri5 = (TextView) findViewById(R.id.friday5);
                friday5.setBackgroundColor(color);
                fri5.setTextColor(Color.TRANSPARENT);
            }


            //6교시
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 28 && y == 230) {
                TextView mon6 = (TextView) findViewById(R.id.monday6);
                monday6.setBackgroundColor(color);
                mon6.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 68 && y == 230) {
                TextView tue6 = (TextView) findViewById(R.id.tuesday6);
                tuesday6.setBackgroundColor(color);
                tue6.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 108 && y == 230) {
                TextView wed6 = (TextView) findViewById(R.id.wednesday6);
                wednesday6.setBackgroundColor(color);
                wed6.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 148 && y == 230) {
                TextView thu6 = (TextView) findViewById(R.id.thursday6);
                thursday6.setBackgroundColor(color);
                thu6.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 188 && y == 230) {
                TextView fri6 = (TextView) findViewById(R.id.friday6);
                friday6.setBackgroundColor(color);
                fri6.setTextColor(Color.TRANSPARENT);
            }


            //7교시
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 28 && y == 270) {
                TextView mon7 = (TextView) findViewById(R.id.monday7);
                monday7.setBackgroundColor(color);
                mon7.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 68 && y == 270) {
                TextView tue7 = (TextView) findViewById(R.id.tuesday7);
                tuesday7.setBackgroundColor(color);
                tue7.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 108 && y == 270) {
                TextView wed7 = (TextView) findViewById(R.id.wednesday7);
                wednesday7.setBackgroundColor(color);
                wed7.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 148 && y == 270) {
                TextView thu7 = (TextView) findViewById(R.id.thursday7);
                thursday7.setBackgroundColor(color);
                thu7.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 188 && y == 270) {
                TextView fri7 = (TextView) findViewById(R.id.friday7);
                friday7.setBackgroundColor(color);
                fri7.setTextColor(Color.TRANSPARENT);
            }


            //8교시
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 28 && y == 310) {
                TextView mon8 = (TextView) findViewById(R.id.monday8);
                monday8.setBackgroundColor(color);
                mon8.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 68 && y == 310) {
                TextView tue8 = (TextView) findViewById(R.id.tuesday8);
                tuesday8.setBackgroundColor(color);
                tue8.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 108 && y == 310) {
                TextView wed8 = (TextView) findViewById(R.id.wednesday8);
                wednesday8.setBackgroundColor(color);
                wed8.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 148 && y == 310) {
                TextView thu8 = (TextView) findViewById(R.id.thursday8);
                thursday8.setBackgroundColor(color);
                thu8.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 188 && y == 310) {
                TextView fri8 = (TextView) findViewById(R.id.friday8);
                friday8.setBackgroundColor(color);
                fri8.setTextColor(Color.TRANSPARENT);
            }


            //9교시
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 28 && y == 350) {
                TextView mon9 = (TextView) findViewById(R.id.monday9);
                monday9.setBackgroundColor(color);
                mon9.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 68 && y == 350) {
                TextView tue9 = (TextView) findViewById(R.id.tuesday9);
                tuesday9.setBackgroundColor(color);
                tue9.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 108 && y == 350) {
                TextView wed9 = (TextView) findViewById(R.id.wednesday9);
                wednesday9.setBackgroundColor(color);
                wed9.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 148 && y == 350) {
                TextView thu9 = (TextView) findViewById(R.id.thursday9);
                thursday9.setBackgroundColor(color);
                thu9.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 188 && y == 350) {
                TextView fri9 = (TextView) findViewById(R.id.friday9);
                friday9.setBackgroundColor(color);
                fri9.setTextColor(Color.TRANSPARENT);
            }


            //10교시
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 28 && y == 390) {
                TextView mon10 = (TextView) findViewById(R.id.monday10);
                monday10.setBackgroundColor(color);
                mon10.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 68 && y == 390) {
                TextView tue10 = (TextView) findViewById(R.id.tuesday10);
                tuesday10.setBackgroundColor(color);
                tue10.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 108 && y == 390) {
                TextView wed10 = (TextView) findViewById(R.id.wednesday10);
                wednesday10.setBackgroundColor(color);
                wed10.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 148 && y == 390) {
                TextView thu10 = (TextView) findViewById(R.id.thursday10);
                thursday10.setBackgroundColor(color);
                thu10.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 188 && y == 390) {
                TextView fri10 = (TextView) findViewById(R.id.friday10);
                friday10.setBackgroundColor(color);
                fri10.setTextColor(Color.TRANSPARENT);
            }


            //11교시
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 28 && y == 430) {
                TextView mon11 = (TextView) findViewById(R.id.monday11);
                monday11.setBackgroundColor(color);
                mon11.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 68 && y == 430) {
                TextView tue11 = (TextView) findViewById(R.id.tuesday11);
                tuesday11.setBackgroundColor(color);
                tue11.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 108 && y == 430) {
                TextView wed11 = (TextView) findViewById(R.id.wednesday11);
                wednesday11.setBackgroundColor(color);
                wed11.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 148 && y == 430) {
                TextView thu11 = (TextView) findViewById(R.id.thursday11);
                thursday11.setBackgroundColor(color);
                thu11.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 188 && y == 430) {
                TextView fri11 = (TextView) findViewById(R.id.friday11);
                friday11.setBackgroundColor(color);
                fri11.setTextColor(Color.TRANSPARENT);
            }


            //12교시
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 28 && y == 470) {
                TextView mon12 = (TextView) findViewById(R.id.monday12);
                monday12.setBackgroundColor(color);
                mon12.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 68 && y == 470) {
                TextView tue12 = (TextView) findViewById(R.id.tuesday12);
                tuesday12.setBackgroundColor(color);
                tue12.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 108 && y == 470) {
                TextView wed12 = (TextView) findViewById(R.id.wednesday12);
                wednesday12.setBackgroundColor(color);
                wed12.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 148 && y == 470) {
                TextView thu12 = (TextView) findViewById(R.id.thursday12);
                thursday12.setBackgroundColor(color);
                thu12.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 188 && y == 470) {
                TextView fri12 = (TextView) findViewById(R.id.friday12);
                friday12.setBackgroundColor(color);
                fri12.setTextColor(Color.TRANSPARENT);
            }


            //13교시
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 28 && y == 510) {
                TextView mon13 = (TextView) findViewById(R.id.monday13);
                monday13.setBackgroundColor(color);
                mon13.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 68 && y == 510) {
                TextView tue13 = (TextView) findViewById(R.id.tuesday13);
                tuesday13.setBackgroundColor(color);
                tue13.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 108 && y == 510) {
                TextView wed13 = (TextView) findViewById(R.id.wednesday13);
                wednesday13.setBackgroundColor(color);
                wed13.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 148 && y == 510) {
                TextView thu13 = (TextView) findViewById(R.id.thursday13);
                thursday13.setBackgroundColor(color);
                thu13.setTextColor(Color.TRANSPARENT);


            }
            //조건이 충족되면 텍스트 뷰의 배경색을 이전에 설정한 색으로 변경 후
            //글자 색은 투명색으로 바꾼다.
            else if (x == 188 && y == 510) {
                TextView fri13 = (TextView) findViewById(R.id.friday13);
                friday13.setBackgroundColor(color);
                fri13.setTextColor(Color.TRANSPARENT);
            }
        }
    }
}

//                 월            화            수          목          금
//
//# 9 ~ 10      (28, 30) ,  (68,30),   (108, 30),   (148, 30),   (188, 30)
//
//# 10 ~ 11    (28, 70) ,  (68,70),   (108, 70),   (148, 70),    (188, 70)
//
//# 11 ~ 12    (28, 110),  (68,110),  (108, 110),  (148, 110),   (188, 110)
//
//# 12 ~ 1     (28, 150) , (68,150),   (108, 150),  (148, 150),  (188, 150)
//
//# 1 ~ 2       (28, 190) , (68,190),   (108, 190),  (148, 190),  (188, 190)
//
//# 2 ~ 3       (28, 230) , (68,230),   (108, 230),  (148, 230),  (188, 230)
//
//# 3 ~ 4      #(28, 270) , (68,270),   (108, 270),  (148, 270),  (188, 270)
//
//# 4 ~ 5      #(28, 310) , (68,310),   (108, 310),  (148, 310),  (188, 310)
//
//# 5 ~ 6      #(28, 350) , (68,350),  (108, 350),   (148, 350),  (188, 350)
//
//# 6 ~ 7      #(28, 390) , (68,390),  (108, 390),   (148, 390),  (188, 390)
//
//# 7 ~ 8      # (28, 430) , (68,430), (108, 430),   (148, 430),  (188, 430)
//
//# 8 ~ 9      # (28, 470) , (68,470), (108, 470),   (148, 470),  (188, 470)
//
//# 9 ~ 10    # (28, 510) , (68,510),  (108, 510),   (148, 510),  (188, 510)



