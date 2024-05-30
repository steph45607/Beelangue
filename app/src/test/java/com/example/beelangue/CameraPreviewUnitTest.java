package com.example.beelangue;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;

public class CameraPreviewUnitTest {

    private CameraPreview cameraPreview;

    @Mock
    Translator mockTranslator;

    @Mock
    InputImage mockInputImage;

    @Mock
    ImageLabeler mockImageLabeler;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        cameraPreview = new CameraPreview();
    }

    @Test
    public void testDetectObjects_withValidImagePath() throws Exception {
        String imagePath = "path/to/image.jpg";
        String targetLanguage = "INDONESIAN";

        File mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn(imagePath);
        when(mockInputImage.fromFilePath(any(), any())).thenReturn(mockInputImage);
        when(mockImageLabeler.process(mockInputImage)).thenReturn(null); // Mock the process method

        ImageLabelerOptions options = new ImageLabelerOptions.Builder().setConfidenceThreshold(0.8f).build();
        TranslatorOptions translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.INDONESIAN)
                .build();
        when(Translation.getClient(translatorOptions)).thenReturn(mockTranslator);

        // Perform object detection
        cameraPreview.detectObjects(imagePath, targetLanguage);

        // Verify interactions and expected behavior
        verify(mockTranslator, times(1)).translate(any());
    }
}