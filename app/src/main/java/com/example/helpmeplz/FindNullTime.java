package com.example.helpmeplz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FindNullTime extends AppCompatActivity {

    private Button back_nullTime;
    View monday1, monday2, monday3, monday4, monday5, monday6, monday7, monday8, monday9, monday10, monday11, monday12, monday13;
    View tuesday1, tuesday2, tuesday3, tuesday4, tuesday5, tuesday6, tuesday7, tuesday8, tuesday9, tuesday10, tuesday11, tuesday12, tuesday13;
    View wednesday1, wednesday2, wednesday3, wednesday4, wednesday5, wednesday6, wednesday7, wednesday8, wednesday9, wednesday10, wednesday11, wednesday12, wednesday13;
    View thursday1, thursday2, thursday3, thursday4, thursday5, thursday6, thursday7, thursday8, thursday9, thursday10, thursday11, thursday12, thursday13;
    View friday1, friday2, friday3, friday4, friday5, friday6, friday7, friday8, friday9, friday10, friday11, friday12, friday13;



    private ArrayList<Mat> matList;
    private List<String> imageNames;
    private List<Bitmap> imageList;
    private StorageReference storageReference;
    private DatabaseReference databaseRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;

//    private FirebaseAuth firebaseAuth;
    private int count = 0;
    static {
        if (!OpenCVLoader.initDebug()) {

        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_null_time);

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

        imageNames = new ArrayList<>();
        imageList = new ArrayList<>();
        matList = new ArrayList<>();

        databaseRef = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        storageRef = storage.getReference();
//        firebaseAuth = FirebaseAuth.getInstance();
//        String userId = firebaseAuth.getCurrentUser().getUid();

        Intent intent2 = getIntent();
        ArrayList<String> userUIDs = intent2.getStringArrayListExtra("memberList");
//        userUIDs.add(userId);

//        String[] userUIDs = {
//                "dtKDwAFCL7aaH9vxVDYWczJWP653",
//                "Nt5bUeuLnOQaD0znKgzxOnfTwI82",
//                "5MWGzBocbGcKo6ZhWvgfWXRIh4k2",
//                "lIpFaKbm8eT01O9bVKo1OApxwmw2",
//                "96iFIzWGJ7ZUXGEfQcn6pCWADkj1"
//        };

        back_nullTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindNullTime.this, Menu.class);
                startActivity(intent);
            }
        });



        for (String uid : userUIDs) {
            DatabaseReference timetableRef = databaseRef.child("users").child(uid).child("timetable");
            timetableRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String imageName = dataSnapshot.getValue(String.class);
                    if (imageName != null) {
                        imageNames.add(imageName);
                    }
                    count++;

                    if (count == userUIDs.size()) {
                        downloadImageUsingHttp();

                    }
                    else {
                        Toast.makeText(FindNullTime.this, "누군가의 시간표 이미지가 없습니다", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void downloadImageUsingHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String imageUrl : imageNames) {
                    try {
                        URL url = new URL(imageUrl);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();

                        InputStream inputStream = connection.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageList.add(bitmap);

                        inputStream.close();
                        connection.disconnect();
                    } catch (IOException e) {
                        Log.e("DownloadImage", "Error downloading image: " + e.getMessage());
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processDownloadedImages();
                    }
                });
            }
        }).start();
    }

    private void processDownloadedImages() {
        if (imageList.size() >= 2) {

            Bitmap firstBitmap = imageList.get(0);
            int width = firstBitmap.getWidth();
            int height = firstBitmap.getHeight();

            Bitmap resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            int startY = 30;
            int startX = 28;

            List<int[]> whiteList = new ArrayList<>();

            for (int y = startY; y < height; y += 40) {
                for (int x = startX; x < width && x < startX + (40 * 5); x += 40) {
                    int red = 0;
                    int green = 0;
                    int blue = 0;

                    for (Bitmap bitmap : imageList) {
                        int pixel = bitmap.getPixel(x, y);

                        int add_red = Color.red(pixel);
                        int add_green = Color.green(pixel);
                        int add_blue = Color.blue(pixel);

                        red += add_red;
                        green += add_green;
                        blue += add_blue;
                    }

                    int numImages = imageList.size();
                    red /= numImages;
                    green /= numImages;
                    blue /= numImages;

                    if (red >= 0 && red <= 31 && green >= 0 && green <= 31 && blue >= 0 && blue <= 31) {
                        red = 255;
                        green = 255;
                        blue = 255;
                    }

                    if (red == 255 && green == 255 && blue == 255) {
                        int[] coordinates = {x, y};
                        whiteList.add(coordinates);
                    }

                    int combinedPixel = Color.rgb(red, green, blue);
                    resultBitmap.setPixel(x, y, combinedPixel);
                }
            }
//            imageView1.setImageBitmap(resultBitmap);
            startinput(whiteList);

                for (int[] coordinates : whiteList) {
                    int x = coordinates[0];
                    int y = coordinates[1];

                    Log.d("White Pixel", "x: " + x + ", y: " + y);
                }
            }
        }

    private void startinput(List<int[]> whiteList) {

        int color = Color.parseColor("#FCEF85");

        for (int[] point : whiteList) {
            int x = point[0];
            int y = point[1];



            //1교시
            if (x == 28 && y == 30) {
                TextView mon1 = (TextView) findViewById(R.id.monday1);
                monday1.setBackgroundColor(color);
                mon1.setTextColor(Color.TRANSPARENT);

            } else if (x == 68 && y == 30) {
                TextView tue1 = (TextView) findViewById(R.id.tuesday1);
                tuesday1.setBackgroundColor(color);
                tue1.setTextColor(Color.TRANSPARENT);


            } else if (x == 108 && y == 30) {
                TextView wed1 = (TextView) findViewById(R.id.wednesday1);
                wednesday1.setBackgroundColor(color);
                wed1.setTextColor(Color.TRANSPARENT);


            } else if (x == 148 && y == 30) {
                TextView thu1 = (TextView) findViewById(R.id.thursday1);
                thursday1.setBackgroundColor(color);
                thu1.setTextColor(Color.TRANSPARENT);


            } else if (x == 188 && y == 30) {
                TextView fri1 = (TextView) findViewById(R.id.friday1);
                friday1.setBackgroundColor(color);
                fri1.setTextColor(Color.TRANSPARENT);


            }

            //2교시
            else if (x == 28 && y == 70) {
                TextView mon2 = (TextView) findViewById(R.id.monday2);
                monday2.setBackgroundColor(color);
                mon2.setTextColor(Color.TRANSPARENT);


            } else if (x == 68 && y == 70) {
                TextView tue2 = (TextView) findViewById(R.id.tuesday2);
                tuesday2.setBackgroundColor(color);
                tue2.setTextColor(Color.TRANSPARENT);


            } else if (x == 108 && y == 70) {
                TextView wed2 = (TextView) findViewById(R.id.wednesday2);
                wednesday2.setBackgroundColor(color);
                wed2.setTextColor(Color.TRANSPARENT);


            } else if (x == 148 && y == 70) {
                TextView thu2 = (TextView) findViewById(R.id.thursday2);
                thursday2.setBackgroundColor(color);
                thu2.setTextColor(Color.TRANSPARENT);


            } else if (x == 188 && y == 70) {
                TextView fri2 = (TextView) findViewById(R.id.friday2);
                friday2.setBackgroundColor(color);
                fri2.setTextColor(Color.TRANSPARENT);
            }


            //3교시
            else if (x == 28 && y == 110) {
                TextView mon3 = (TextView) findViewById(R.id.monday3);
                monday3.setBackgroundColor(color);
                mon3.setTextColor(Color.TRANSPARENT);


            } else if (x == 68 && y == 110) {
                TextView tue3 = (TextView) findViewById(R.id.tuesday3);
                tuesday3.setBackgroundColor(color);
                tue3.setTextColor(Color.TRANSPARENT);


            } else if (x == 108 && y == 110) {
                TextView wed3 = (TextView) findViewById(R.id.wednesday3);
                wednesday3.setBackgroundColor(color);
                wed3.setTextColor(Color.TRANSPARENT);


            } else if (x == 148 && y == 110) {
                TextView thu3 = (TextView) findViewById(R.id.thursday3);
                thursday3.setBackgroundColor(color);
                thu3.setTextColor(Color.TRANSPARENT);


            } else if (x == 188 && y == 110) {
                TextView fri3 = (TextView) findViewById(R.id.friday3);
                friday3.setBackgroundColor(color);
                fri3.setTextColor(Color.TRANSPARENT);
            }



            //4교시
            else if (x == 28 && y == 150) {
                TextView mon4 = (TextView) findViewById(R.id.monday4);
                monday4.setBackgroundColor(color);
                mon4.setTextColor(Color.TRANSPARENT);


            } else if (x == 68 && y == 150) {
                TextView tue4 = (TextView) findViewById(R.id.tuesday4);
                tuesday4.setBackgroundColor(color);
                tue4.setTextColor(Color.TRANSPARENT);


            } else if (x == 108 && y == 150) {
                TextView wed4 = (TextView) findViewById(R.id.wednesday4);
                wednesday4.setBackgroundColor(color);
                wed4.setTextColor(Color.TRANSPARENT);


            } else if (x == 148 && y == 150) {
                TextView thu4 = (TextView) findViewById(R.id.thursday4);
                thursday4.setBackgroundColor(color);
                thu4.setTextColor(Color.TRANSPARENT);


            } else if (x == 188 && y == 150) {
                TextView fri4 = (TextView) findViewById(R.id.friday4);
                friday4.setBackgroundColor(color);
                fri4.setTextColor(Color.TRANSPARENT);
            }


            //5교시
            else if (x == 28 && y == 190) {
                TextView mon5 = (TextView) findViewById(R.id.monday5);
                monday5.setBackgroundColor(color);
                mon5.setTextColor(Color.TRANSPARENT);


            } else if (x == 68 && y == 190) {
                TextView tue5 = (TextView) findViewById(R.id.tuesday5);
                tuesday5.setBackgroundColor(color);
                tue5.setTextColor(Color.TRANSPARENT);


            } else if (x == 108 && y == 190) {
                TextView wed5 = (TextView) findViewById(R.id.wednesday5);
                wednesday5.setBackgroundColor(color);
                wed5.setTextColor(Color.TRANSPARENT);


            } else if (x == 148 && y == 190) {
                TextView thu5 = (TextView) findViewById(R.id.thursday5);
                thursday5.setBackgroundColor(color);
                thu5.setTextColor(Color.TRANSPARENT);


            } else if (x == 188 && y == 190) {
                TextView fri5 = (TextView) findViewById(R.id.friday5);
                friday5.setBackgroundColor(color);
                fri5.setTextColor(Color.TRANSPARENT);
            }


            //6교시
            else if (x == 28 && y == 230) {
                TextView mon6 = (TextView) findViewById(R.id.monday6);
                monday6.setBackgroundColor(color);
                mon6.setTextColor(Color.TRANSPARENT);


            } else if (x == 68 && y == 230) {
                TextView tue6 = (TextView) findViewById(R.id.tuesday6);
                tuesday6.setBackgroundColor(color);
                tue6.setTextColor(Color.TRANSPARENT);


            } else if (x == 108 && y == 230) {
                TextView wed6 = (TextView) findViewById(R.id.wednesday6);
                wednesday6.setBackgroundColor(color);
                wed6.setTextColor(Color.TRANSPARENT);


            } else if (x == 148 && y == 230) {
                TextView thu6 = (TextView) findViewById(R.id.thursday6);
                thursday6.setBackgroundColor(color);
                thu6.setTextColor(Color.TRANSPARENT);


            } else if (x == 188 && y == 230) {
                TextView fri6 = (TextView) findViewById(R.id.friday6);
                friday6.setBackgroundColor(color);
                fri6.setTextColor(Color.TRANSPARENT);
            }


            //7교시
            else if (x == 28 && y == 270) {
                TextView mon7 = (TextView) findViewById(R.id.monday7);
                monday7.setBackgroundColor(color);
                mon7.setTextColor(Color.TRANSPARENT);


            } else if (x == 68 && y == 270) {
                TextView tue7 = (TextView) findViewById(R.id.tuesday7);
                tuesday7.setBackgroundColor(color);
                tue7.setTextColor(Color.TRANSPARENT);


            } else if (x == 108 && y == 270) {
                TextView wed7 = (TextView) findViewById(R.id.wednesday7);
                wednesday7.setBackgroundColor(color);
                wed7.setTextColor(Color.TRANSPARENT);


            } else if (x == 148 && y == 270) {
                TextView thu7 = (TextView) findViewById(R.id.thursday7);
                thursday7.setBackgroundColor(color);
                thu7.setTextColor(Color.TRANSPARENT);


            } else if (x == 188 && y == 270) {
                TextView fri7 = (TextView) findViewById(R.id.friday7);
                friday7.setBackgroundColor(color);
                fri7.setTextColor(Color.TRANSPARENT);
            }


            //8교시
            else if (x == 28 && y == 310) {
                TextView mon8 = (TextView) findViewById(R.id.monday8);
                monday8.setBackgroundColor(color);
                mon8.setTextColor(Color.TRANSPARENT);


            } else if (x == 68 && y == 310) {
                TextView tue8 = (TextView) findViewById(R.id.tuesday8);
                tuesday8.setBackgroundColor(color);
                tue8.setTextColor(Color.TRANSPARENT);


            } else if (x == 108 && y == 310) {
                TextView wed8 = (TextView) findViewById(R.id.wednesday8);
                wednesday8.setBackgroundColor(color);
                wed8.setTextColor(Color.TRANSPARENT);


            } else if (x == 148 && y == 310) {
                TextView thu8 = (TextView) findViewById(R.id.thursday8);
                thursday8.setBackgroundColor(color);
                thu8.setTextColor(Color.TRANSPARENT);


            } else if (x == 188 && y == 310) {
                TextView fri8 = (TextView) findViewById(R.id.friday8);
                friday8.setBackgroundColor(color);
                fri8.setTextColor(Color.TRANSPARENT);
            }


            //9교시
            else if (x == 28 && y == 350) {
                TextView mon9 = (TextView) findViewById(R.id.monday9);
                monday9.setBackgroundColor(color);
                mon9.setTextColor(Color.TRANSPARENT);


            } else if (x == 68 && y == 350) {
                TextView tue9 = (TextView) findViewById(R.id.tuesday9);
                tuesday9.setBackgroundColor(color);
                tue9.setTextColor(Color.TRANSPARENT);


            } else if (x == 108 && y == 350) {
                TextView wed9 = (TextView) findViewById(R.id.wednesday9);
                wednesday9.setBackgroundColor(color);
                wed9.setTextColor(Color.TRANSPARENT);


            } else if (x == 148 && y == 350) {
                TextView thu9 = (TextView) findViewById(R.id.thursday9);
                thursday9.setBackgroundColor(color);
                thu9.setTextColor(Color.TRANSPARENT);


            } else if (x == 188 && y == 350) {
                TextView fri9 = (TextView) findViewById(R.id.friday9);
                friday9.setBackgroundColor(color);
                fri9.setTextColor(Color.TRANSPARENT);
            }


            //10교시
            else if (x == 28 && y == 390) {
                TextView mon10 = (TextView) findViewById(R.id.monday10);
                monday10.setBackgroundColor(color);
                mon10.setTextColor(Color.TRANSPARENT);


            } else if (x == 68 && y == 390) {
                TextView tue10 = (TextView) findViewById(R.id.tuesday10);
                tuesday10.setBackgroundColor(color);
                tue10.setTextColor(Color.TRANSPARENT);


            } else if (x == 108 && y == 390) {
                TextView wed10 = (TextView) findViewById(R.id.wednesday10);
                wednesday10.setBackgroundColor(color);
                wed10.setTextColor(Color.TRANSPARENT);


            } else if (x == 148 && y == 390) {
                TextView thu10 = (TextView) findViewById(R.id.thursday10);
                thursday10.setBackgroundColor(color);
                thu10.setTextColor(Color.TRANSPARENT);


            } else if (x == 188 && y == 390) {
                TextView fri10 = (TextView) findViewById(R.id.friday10);
                friday10.setBackgroundColor(color);
                fri10.setTextColor(Color.TRANSPARENT);
            }


            //11교시
            else if (x == 28 && y == 430) {
                TextView mon11 = (TextView) findViewById(R.id.monday11);
                monday11.setBackgroundColor(color);
                mon11.setTextColor(Color.TRANSPARENT);


            } else if (x == 68 && y == 430) {
                TextView tue11 = (TextView) findViewById(R.id.tuesday11);
                tuesday11.setBackgroundColor(color);
                tue11.setTextColor(Color.TRANSPARENT);


            } else if (x == 108 && y == 430) {
                TextView wed11 = (TextView) findViewById(R.id.wednesday11);
                wednesday11.setBackgroundColor(color);
                wed11.setTextColor(Color.TRANSPARENT);


            } else if (x == 148 && y == 430) {
                TextView thu11 = (TextView) findViewById(R.id.thursday11);
                thursday11.setBackgroundColor(color);
                thu11.setTextColor(Color.TRANSPARENT);


            } else if (x == 188 && y == 430) {
                TextView fri11 = (TextView) findViewById(R.id.friday11);
                friday11.setBackgroundColor(color);
                fri11.setTextColor(Color.TRANSPARENT);
            }


            //12교시
            else if (x == 28 && y == 470) {
                TextView mon12 = (TextView) findViewById(R.id.monday12);
                monday12.setBackgroundColor(color);
                mon12.setTextColor(Color.TRANSPARENT);


            } else if (x == 68 && y == 470) {
                TextView tue12 = (TextView) findViewById(R.id.tuesday12);
                tuesday12.setBackgroundColor(color);
                tue12.setTextColor(Color.TRANSPARENT);


            } else if (x == 108 && y == 470) {
                TextView wed12 = (TextView) findViewById(R.id.wednesday12);
                wednesday12.setBackgroundColor(color);
                wed12.setTextColor(Color.TRANSPARENT);


            } else if (x == 148 && y == 470) {
                TextView thu12 = (TextView) findViewById(R.id.thursday12);
                thursday12.setBackgroundColor(color);
                thu12.setTextColor(Color.TRANSPARENT);


            } else if (x == 188 && y == 470) {
                TextView fri12 = (TextView) findViewById(R.id.friday12);
                friday12.setBackgroundColor(color);
                fri12.setTextColor(Color.TRANSPARENT);
            }


            //13교시
            else if (x == 28 && y == 510) {
                TextView mon13 = (TextView) findViewById(R.id.monday13);
                monday13.setBackgroundColor(color);
                mon13.setTextColor(Color.TRANSPARENT);


            } else if (x == 68 && y == 510) {
                TextView tue13 = (TextView) findViewById(R.id.tuesday13);
                tuesday13.setBackgroundColor(color);
                tue13.setTextColor(Color.TRANSPARENT);


            } else if (x == 108 && y == 510) {
                TextView wed13 = (TextView) findViewById(R.id.wednesday13);
                wednesday13.setBackgroundColor(color);
                wed13.setTextColor(Color.TRANSPARENT);


            } else if (x == 148 && y == 510) {
                TextView thu13 = (TextView) findViewById(R.id.thursday13);
                thursday13.setBackgroundColor(color);
                thu13.setTextColor(Color.TRANSPARENT);


            } else if (x == 188 && y == 510) {
                TextView fri13 = (TextView) findViewById(R.id.friday13);
                friday13.setBackgroundColor(color);
                fri13.setTextColor(Color.TRANSPARENT);
            }







        }

//                 월            화            수          목          금
//
//# 9 ~ 10    #  (28, 30) ,  (68,30),   (108, 30),   (148, 30),   (188, 30)
//
//# 10 ~ 11  #   (28, 70) ,  (68,70),   (108, 70),   (148, 70),   (188, 70)
//
//# 11 ~ 12  #  (28, 110),  (68,110),  (108, 110),  (148, 110),   (188, 110)
//
//# 12 ~ 1    # (28, 150) , (68,150),   (108, 150),  (148, 150),  (188, 150)
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







    }


}
