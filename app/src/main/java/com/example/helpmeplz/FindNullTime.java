package com.example.helpmeplz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

    private TextView tt;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private ImageView imageView5;

    private ArrayList<Mat> matList;
    private List<String> imageNames;
    private List<Bitmap> imageList;
    private StorageReference storageReference;
    private DatabaseReference databaseRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private int count = 0;
    static {
        if (!OpenCVLoader.initDebug()) {

        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_null_time);

        tt = findViewById(R.id.tt);
        imageView1 = findViewById(R.id.image1);
//        imageView2 = findViewById(R.id.image2);
//        imageView3 = findViewById(R.id.image3);
//        imageView4 = findViewById(R.id.image4);
//        imageView5 = findViewById(R.id.image5);

        imageNames = new ArrayList<>();
        imageList = new ArrayList<>();
        matList = new ArrayList<>();

        databaseRef = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        storageRef = storage.getReference();

        String[] userUIDs = {
                "dtKDwAFCL7aaH9vxVDYWczJWP653",
                "Nt5bUeuLnOQaD0znKgzxOnfTwI82",
                "5MWGzBocbGcKo6ZhWvgfWXRIh4k2",
                "lIpFaKbm8eT01O9bVKo1OApxwmw2",
                "96iFIzWGJ7ZUXGEfQcn6pCWADkj1"
        };

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

                    if (count == userUIDs.length) {
                        downloadImageUsingHttp();

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
            startinput(whiteList);

                for (int[] coordinates : whiteList) {
                    int x = coordinates[0];
                    int y = coordinates[1];

                    Log.d("White Pixel", "x: " + x + ", y: " + y);
                }
            }
        }

    private void startinput(List<int[]> whiteList) {
//        int x = whiteList.get(0)[0];
//        tt.setText(String.valueOf(x));

    }


}
