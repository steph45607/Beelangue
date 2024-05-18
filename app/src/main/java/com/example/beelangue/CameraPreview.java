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
import com.google.mlkit.nl.translate.TranslateLanguage;
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

        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CameraPreview.this, LearnExploreActivity.class);
                startActivity((i));
            }
        });

        selectedLanguage = getIntent().getStringExtra("selected_language");

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

        PreviewView previewView = findViewById(R.id.previewView);

        previewView.setImplementationMode(PreviewView.ImplementationMode.COMPATIBLE);

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }

    private void captureImage() {
        File outputFile = new File(getExternalFilesDir(null), "capture.jpg");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(outputFile).build();
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                String savedImagePath = outputFile.getAbsolutePath();
                detectObjects(savedImagePath, selectedLanguage);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e("CameraX", "Error capturing image", exception);
                Toast.makeText(CameraPreview.this, "Error capturing image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void translate(String word, String targetLanguage) {
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

        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi() // Optional: Require WiFi for download
                .build();

        Translator translator = Translation.getClient(translatorOptions);

        translator.translate(word)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String translatedText) {
                        Log.d("translate", String.format("%s (%s)", word, translatedText));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("translate", "Translation Failed: " + exception.getMessage());
                    }
                });
    }

    private void detectObjects(String imagePath, String targetLanguage) {
        object = findViewById(R.id.objectDetected);

        try {
            Log.d("ObjectDetection", "Image Saved at: " + imagePath);
            InputImage image = InputImage.fromFilePath(getApplicationContext(), Uri.fromFile(new File(imagePath)));

            ImageLabelerOptions options =
                    new ImageLabelerOptions.Builder()
                            .setConfidenceThreshold(0.8f)
                            .build();

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

            DownloadConditions conditions = new DownloadConditions.Builder()
                    .requireWifi() // Optional: Require WiFi for download
                    .build();

            Translator englishindoTranslator = Translation.getClient(translatorOptions);

            // Check if the model is downloaded
            englishindoTranslator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Model downloaded, proceed with translation
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
                                                englishindoTranslator.translate(allLabels)
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
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e("ObjectDetection", "Model download failed: " + exception.getMessage());
                            // Handle model download failure (e.g., display error message)
                        }
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