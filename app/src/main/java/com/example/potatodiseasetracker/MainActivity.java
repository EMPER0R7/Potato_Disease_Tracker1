package com.example.potatodiseasetracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;

import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.potatodiseasetracker.ml.TfliteModel;
import com.example.potatodiseasetracker.ml.TfliteModel1;
import com.example.potatodiseasetracker.ml.TfliteModel2;
import com.example.potatodiseasetracker.ml.TfliteModel3;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.tensorflow.lite.DataType;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    FloatingActionButton floatingActionButton;
    Button btn2,btn3;
    Bitmap bitmap;
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermission();
        String[] lab= {"Early Blight", "Late Blight","Healthy","Image is not a potato leaf"};
        String[] precautions={"Early Blight: Rotate potato crops annually, remove infected plant debris promptly, and use disease-resistant potato varieties whenever possible.",
        "Late Blight: Apply fungicides preventatively, ensure good air circulation around plants, and avoid overhead watering to minimize leaf wetness.",
                "Healthy: Maintain proper soil drainage, provide adequate spacing between plants, and practice crop rotation with non-host plants to prevent disease buildup in the soil.\n",
                "Please select Proper Image . selected image is not classified as Potato leaf"

                       };
        imageView=findViewById(R.id.imageView);

        floatingActionButton=findViewById(R.id.floatingActionButton);
        btn2=findViewById(R.id.button2);
        btn3=findViewById(R.id.predict);
        Glide.with(this).asGif().load(R.drawable.gg).into(imageView);




        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent=new Intent();
               intent.setAction(Intent.ACTION_GET_CONTENT);
               intent.setType("image/*");
               startActivityForResult(intent,10);
            }
        });


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
              startActivityForResult(intent,12);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap == null) {
                    Toast.makeText(MainActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                    return; // Exit the onClick method early
                }
                if (!ImageValidator.isPotatoLeaf(bitmap)) {
                    Toast.makeText(MainActivity.this, "Uploaded image does not appear to be a potato leaf", Toast.LENGTH_SHORT).show();
                    return; // Exit the onClick method early
                }

                try {
                    TfliteModel1 model = TfliteModel1.newInstance(getApplicationContext());

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 256, 256, 3}, DataType.FLOAT32);
                    bitmap=Bitmap.createScaledBitmap(bitmap,256,256,true);
                    TensorImage tensorImage=new TensorImage(DataType.FLOAT32);
                    tensorImage.load(bitmap);
                    ByteBuffer byteBuffer=tensorImage.getBuffer();
                    inputFeature0.loadBuffer(byteBuffer);

                    // Runs model inference and gets result.
                    TfliteModel1.Outputs outputs = model.process(inputFeature0);

                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    // Releases model resources if no longer used.
                    model.close();

                    String result = "Disease Predicted :" + "\n" + lab[getMax(outputFeature0.getFloatArray())] + "\n" +
                            "Confidence: "+outputFeature0.getFloatArray()[getMax(outputFeature0.getFloatArray())]*100+"%";
                    String pre=precautions[getMax(outputFeature0.getFloatArray())];

                    // Start ResultActivity and pass the result data
                    Intent intent = new Intent(MainActivity.this, Result.class);
                    intent.putExtra("RESULT", result);
                    intent.putExtra("PRE",pre);
                    startActivity(intent);
                } catch (IOException e) {
                    // TODO Handle the exception
                    e.printStackTrace();

                }

            }
        });





    }
    int getMax(float[] arr){
        int max=0;
        for(int i=0;i<arr.length;i++){
           if(arr[i]>arr[max]) {
               max=i;
           }

        }
        return max;
    }


     void getPermission() {
         if (checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
             ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},11);
         }
     }
    private MappedByteBuffer loadModelFile() throws IOException {
        FileInputStream inputStream = new FileInputStream("t.tflite");
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileChannel.position();
        long declaredLength = fileChannel.size();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==11){
            if(grantResults.length>0){
                if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                    this.getPermission();

                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       if(requestCode==10){
           if(data!=null){
               Uri uri=data.getData();
               try {
                   bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                   imageView.setImageBitmap(bitmap);

               }catch (IOException e){

               }

           }
       } else if (requestCode==12) {
           bitmap= (Bitmap) data.getExtras().get("data");
           imageView.setImageBitmap(bitmap);

       }
        super.onActivityResult(requestCode, resultCode, data);
    }
}