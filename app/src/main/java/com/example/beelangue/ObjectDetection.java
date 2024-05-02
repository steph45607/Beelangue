package com.example.beelangue;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ObjectDetection extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PERMISSION = 2;

    private Interpreter interpreter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_page);

        try {
            interpreter = new Interpreter(loadModelFile(), new Interpreter.Options());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
        } else {
            dispatchTakePictureIntent();
        }
    }

    private ByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            performObjectDetection(imageBitmap);
        }
    }

    private void performObjectDetection(Bitmap imageBitmap) {
        ByteBuffer inputBuffer = convertBitmapToByteBuffer(imageBitmap);

        float[][] outputArray = new float[1][NUM_CLASSES];
        interpreter.run(inputBuffer, outputArray);

        String detectedObjects = getDetectedObjects(outputArray);

        translateToJapanese(detectedObjects);
    }

    private String getDetectedObjects(float[][] outputArray) {
        return "Person, Car, Dog"; // dummy data
    }

    private void translateToJapanese(String text) {
        String apiKey = "" // APIKey
        try {
            Translate translate = TranslateOptions.newBuilder().setApiKey(apiKey).build().getService();
            Translation translation = translate.translate(text, Translate.TranslateOPtion.targetLanguage("ja"));
            String translatedText = translation.getTranslatedText();

            Log.d("Translated Text", translatedText);
            runOnUiThread(() -> Toast.makeText(ObjectDetection.this, "Translated Text: " + translatedText, Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
