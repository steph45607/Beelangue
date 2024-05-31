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
public class CameraPreviewInstrumentedTest {

    @Rule
    public ActivityScenarioRule<CameraPreview> activityRule =
            new ActivityScenarioRule<>(CameraPreview.class);

    @Test
    public void testCaptureAndObjectDetection() {
        onView(withId(R.id.capture_button)).perform(click());

        // Wait for the image to be captured and processed
        try {
            Thread.sleep(5000);
            onView(withId(R.id.objectDetected)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBackButton() {
        onView(withId(R.id.backButton)).perform(click());

        // Check if the LearnExploreActivity is launched
        onView(withId(R.id.learnBtn)).check(matches(isDisplayed()));
    }
}