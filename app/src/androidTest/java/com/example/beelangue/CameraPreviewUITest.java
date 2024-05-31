package com.example.beelangue;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CameraPreviewUITest {

    @Rule
    public ActivityScenarioRule<CameraPreview> activityRule = new ActivityScenarioRule<>(CameraPreview.class);

    @Test
    public void testCaptureButton() {
        // Click the capture button
        onView(withId(R.id.capture_button)).perform(click());
    }

    @Test
    public void testBackButton() {
        // Click the back button
        onView(withId(R.id.backButton)).perform(click());
    }

    @Test
    public void testObjectDetection() {
        // Take a picture of an object
        // (This requires manual interaction with the camera)

        // Check if the object detected text is displayed
        onView(withId(R.id.objectDetected)).check(matches(isDisplayed()));

    }
}