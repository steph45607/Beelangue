package com.example.beelangue;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CameraPreview extends AppCompatActivity {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;
    ImageButton backBtn;
    TextView object;
    private String selectedLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_page);

        // get selected language from last activity
        selectedLanguage = getIntent().getStringExtra("selected_language");

        // define back button to last page
        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigate back to LearnExploreActivity with the selected language
                Intent i = new Intent(CameraPreview.this, LearnExploreActivity.class);
                i.putExtra("selected_language", selectedLanguage);
                startActivity((i));
            }
        });

        // initialize camera provider
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        // Add listener to bind the camera when provider is available
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e("CameraX", "Error getting camera provider", e);
                Toast.makeText(CameraPreview.this, "Error starting camera", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));

        // request camera permissions if not yet granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        }

        // initialize image capture
        imageCapture = new ImageCapture.Builder().build();

        // set up capture button to take picture when clicked
        ImageButton captureButton = findViewById(R.id.capture_button);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        // set up preview
        Preview preview = new Preview.Builder().build();

        // select back-facing camera
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // get reference to the preview view
        PreviewView previewView = findViewById(R.id.previewView);

        // set implementation mode of the preview
        previewView.setImplementationMode(PreviewView.ImplementationMode.COMPATIBLE);

        // bind the preview ot the camera provider lifecycle
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // bind the preview and image capture to the camera
        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }

    private void captureImage() {
        // create a file to save captured image
        File outputFile = new File(getExternalFilesDir(null), "capture.jpg");

        // set up output file options for image capture
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(outputFile).build();
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                // when image saved, perform object detection
                String savedImagePath = outputFile.getAbsolutePath();
                detectObjects(savedImagePath, selectedLanguage);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                // handle image capture error
                Log.e("CameraX", "Error capturing image", exception);
                Toast.makeText(CameraPreview.this, "Error capturing image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void detectObjects(String imagePath, String targetLanguage) {
        object = findViewById(R.id.objectDetected);

        try {
            Log.d("ObjectDetection", "Image Saved at: " + imagePath);
            // get image from path
            InputImage image = InputImage.fromFilePath(getApplicationContext(), Uri.fromFile(new File(imagePath)));

            // set up image labeler options
            ImageLabelerOptions options =
                    new ImageLabelerOptions.Builder()
                            .setConfidenceThreshold(0.8f)
                            .build();

            // get language code for the target language
            String targetLanguageCode;
            try {
                Field field = TranslateLanguage.class.getField(targetLanguage.toUpperCase());
                targetLanguageCode = (String) field.get(null);
            } catch (Exception e) {
                targetLanguageCode = TranslateLanguage.INDONESIAN; // Default to Indonesian if not found
            }

            assert targetLanguageCode != null;
            TranslatorOptions translatorOptions = new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(targetLanguageCode)
                    .build();

            // set up download conditions for the translation model
            DownloadConditions conditions = new DownloadConditions.Builder()
                    .requireWifi() // Require WiFi for download
                    .build();

            Translator translator = Translation.getClient(translatorOptions);

            RemoteModelManager modelManager = RemoteModelManager.getInstance();
            TranslateRemoteModel model = new TranslateRemoteModel.Builder(targetLanguageCode).build();

            // Check if the model is downloaded
            modelManager.isModelDownloaded(model)
                    .addOnSuccessListener(isDownloaded -> {
                        if (isDownloaded) {
                            // Model is already downloaded, proceed with object detection and translation
                            performObjectDetectionAndTranslation(translator, image, options);
                        } else {
                            // Model is not downloaded, show a toast message
                            Toast.makeText(CameraPreview.this, "Downloading translation model...", Toast.LENGTH_SHORT).show();

                            // Download the model
                            translator.downloadModelIfNeeded(conditions)
                                    .addOnSuccessListener(unused -> {
                                        // Model download successful, proceed with object detection and translation
                                        performObjectDetectionAndTranslation(translator, image, options);
                                    })
                                    .addOnFailureListener(exception -> {
                                        // Model download failed, show a toast message
                                        Log.e("ObjectDetection", "Model download failed: " + exception.getMessage());
                                        Toast.makeText(CameraPreview.this, "Model download failed", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(exception -> {
                        // Failed to check if model is downloaded
                        Log.e("ObjectDetection", "Failed to check if model is downloaded: " + exception.getMessage());
                        Toast.makeText(CameraPreview.this, "Failed to check model status", Toast.LENGTH_SHORT).show();
                    });
        } catch (IOException e) {
            Log.e("ObjectDetection", "Object detection failed: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(CameraPreview.this, "Object detection failed", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to perform object detection and translation
    private void performObjectDetectionAndTranslation(Translator translator, InputImage image, ImageLabelerOptions options) {
        ImageLabeler labeler = ImageLabeling.getClient(options);

        labeler.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        if (labels.isEmpty()) {
                            Log.d("ObjectDetection", "No objects detected");
                            Toast.makeText(CameraPreview.this, "No objects detected", Toast.LENGTH_SHORT).show();
                        } else {
                            StringBuilder labelTextBuilder = new StringBuilder();
                            int totalLabels = labels.size();
                            for (int i = 0; i < totalLabels; i++) {
                                ImageLabel label = labels.get(i);
                                String labelText = label.getText();
                                float confidence = label.getConfidence();
                                Log.d("ObjectDetection", "Object Detected: " + labelText + " Confidence: " + confidence);
                                labelTextBuilder.append(labelText);
                                if (i < totalLabels - 1) {
                                    labelTextBuilder.append(", ");
                                }
                            }
                            String allLabels = labelTextBuilder.toString().trim();
                            translator.translate(allLabels)
                                    .addOnSuccessListener(new OnSuccessListener<String>() {
                                        @Override
                                        public void onSuccess(String translatedText) {
                                            object.setText(String.format("%s (%s)", allLabels, translatedText));
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            Log.e("ObjectDetection", "Translation Failed: " + exception.getMessage());
                                            // Handle translation failure (e.g., display error message)
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ObjectDetection", "Object detection failed: " + e.getMessage());
                        Toast.makeText(CameraPreview.this, "Object detection failed", Toast.LENGTH_SHORT).show();
                    }
                });
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