package com.example.beelangue;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class CameraPreviewUITest {

    @Rule
    public ActivityScenarioRule<CameraPreview> activityRule = new ActivityScenarioRule<>(CameraPreview.class);

    @Mock
    Translator mockTranslator;

    @Mock
    InputImage mockInputImage;

    @Mock
    ImageLabeler mockImageLabeler;

    private AutoCloseable closeable;

    @Before
    public void setUp() {
        Intents.init();
        closeable = MockitoAnnotations.openMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        Intents.release();
        closeable.close();
    }

    @Test
    public void testBackButtonNavigatesToLearnExploreActivity() {
        // Launch the activity with an intent containing the selected language
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra("selected_language", "indonesian");
        ActivityScenario.launch(intent);

        // Click the back button
        onView(withId(R.id.backButton)).perform(click());

        // Verify that the intent has the correct target activity and the correct extra
        intended(hasComponent(LearnExploreActivity.class.getName()));
        intended(hasExtra("selected_language", "indonesian"));
    }

    @Test
    public void testCaptureButtonIsDisplayed() {
        // Check if the capture button is displayed
        onView(withId(R.id.capture_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testObjectTextViewIsDisplayed() {
        // Check if the object TextView is displayed
        onView(withId(R.id.objectDetected)).check(matches(isDisplayed()));
    }

    // Mock long-running operations like object detection and translation
    @Test
    public void testMockedObjectDetectionAndTranslation() {
        ActivityScenario<CameraPreview> scenario = activityRule.getScenario();

        scenario.onActivity(activity -> {
            String imagePath = "path/to/image.jpg";
            String targetLanguage = "INDONESIAN";

            File mockFile = mock(File.class);
            when(mockFile.getAbsolutePath()).thenReturn(imagePath);
            try {
                when(InputImage.fromFilePath(any(), any())).thenReturn(mockInputImage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            when(mockImageLabeler.process(mockInputImage)).thenReturn((Task<List<ImageLabel>>) mock(List.class)); // Mock the process method


            TranslatorOptions translatorOptions = new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.INDONESIAN)
                    .build();
            when(Translation.getClient(translatorOptions)).thenReturn(mockTranslator);

            // Perform object detection
            activity.detectObjects(imagePath, targetLanguage);

            // Verify interactions and expected behavior
            verify(mockTranslator, times(1)).translate(any());
        });
    }
}