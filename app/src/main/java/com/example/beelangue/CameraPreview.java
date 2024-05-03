package com.example.beelangue;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CameraPreview extends AppCompatActivity {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_page);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e("CameraX", "Error getting camera provider", e);
                Toast.makeText(CameraPreview.this, "Error starting camera", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        }

        imageCapture = new ImageCapture.Builder().build();

        ImageButton captureButton = findViewById(R.id.capture_button);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(((PreviewView) findViewById(R.id.previewView)).getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageCapture);
    }

    private void captureImage() {
        File outputFile = new File(getExternalFilesDir(null), "capture.jpg");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(outputFile).build();
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                String savedImagePath = outputFile.getAbsolutePath();
                detectObjects(savedImagePath);
//                Toast.makeText(CameraPreview.this, "Image Saved at: " + savedImagePath, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e("CameraX", "Error capturing image", exception);
                Toast.makeText(CameraPreview.this, "Error capturing image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void detectObjects(String imagePath) {
        try {
            Log.d("ObjectDetection", "Image Saved at: " + imagePath);
            InputImage image = InputImage.fromFilePath(getApplicationContext(), Uri.fromFile(new File(imagePath)));
            ObjectDetectorOptions options =
                    new ObjectDetectorOptions.Builder()
                            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
                            .enableClassification()
                            .build();
            ObjectDetector objectDetector = ObjectDetection.getClient(options);
            objectDetector.process(image)
                    .addOnSuccessListener(detectedObjects -> {
                        if (detectedObjects.isEmpty()) {
                            Log.d("ObjectDetection", "No objects detected");
                            Toast.makeText(CameraPreview.this, "No objects detected", Toast.LENGTH_SHORT).show();
                        } else {
                            for (DetectedObject detectedObject : detectedObjects) {
                                List<DetectedObject.Label> labels = detectedObject.getLabels();
                                for (DetectedObject.Label label : labels) {
                                    String labelText = label.getText();
                                    float confidence = label.getConfidence();
                                    Log.d("ObjectDetection", "Object Detected: " + labelText + " Confidence: " + confidence);
                                    Toast.makeText(CameraPreview.this, "Object Detected: " + labelText, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ObjectDetection", "Object detection failed: " + e.getMessage());
                        Toast.makeText(CameraPreview.this, "Object detection failed", Toast.LENGTH_SHORT).show();
                    });
        } catch (IOException e) {
            Log.e("ObjectDetection", "Object detection failed: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(CameraPreview.this, "Object detection failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (cameraProviderFuture != null && cameraProviderFuture.isDone()) {
                    try {
                        ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                        bindPreview(cameraProvider);
                    } catch (ExecutionException | InterruptedException e) {
                        Log.e("CameraX", "Error getting camera provider", e);
                        Toast.makeText(CameraPreview.this, "Error starting camera", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}