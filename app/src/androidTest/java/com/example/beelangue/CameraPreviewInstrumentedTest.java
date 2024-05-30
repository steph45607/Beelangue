package com.example.beelangue;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabeler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class CameraPreviewInstrumentedTest {

    @Mock
    Translator mockTranslator;

    @Mock
    ImageLabeler mockImageLabeler;

    @Test
    public void testDetectObjects_withValidImagePath() throws Exception {
        // Launch the activity using ActivityScenario
        ActivityScenario.launch(CameraPreview.class).onActivity(activity -> {
            String imagePath = "path/to/image.jpg";
            String targetLanguage = "INDONESIAN";

            // Create a mock ImageLabeler
            when(mockImageLabeler.process(any(InputImage.class))).thenReturn(null);

            // Mock the Translator
            when(mockTranslator.translate(anyString())).thenReturn(null);

            // Perform object detection
            activity.detectObjects(imagePath, targetLanguage);

            // Verify interactions and expected behavior
            verify(mockImageLabeler, times(1)).process(any(InputImage.class));
            verify(mockTranslator, times(1)).translate(anyString());
        });
    }
}
