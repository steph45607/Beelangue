package com.example.beelangue;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LearnActivityInstrumentedTest {

    private static final String SELECTED_LANGUAGE = "indonesian";

    @Rule
    public ActivityScenarioRule<LearnActivity> activityScenarioRule =
            new ActivityScenarioRule<>(LearnActivity.class);

    private ActivityScenario<LearnActivity> activityScenario;

    @Before
    public void setUp() {
        activityScenario = activityScenarioRule.getScenario();
        activityScenario.onActivity(activity -> {
            Intent intent = new Intent(activity, LearnActivity.class);
            intent.putExtra("selected_language", SELECTED_LANGUAGE);
            activity.startActivity(intent);
        });
    }

    @Test
    public void testClickBackButton() {
        onView(withId(R.id.backBtn)).perform(click());
        onView(withId(R.id.learnBtn)).check(matches(isDisplayed()));
    }

    @Test
    public void testClickCreateDeckButton() {
        onView(withId(R.id.createDeck)).perform(click());
        onView(withId(R.id.deckTitle)).check(matches(isDisplayed()));
    }

    @Test
    public void testClickSearchButton() {
        onView(withId(R.id.searchBtn)).perform(click());
        onView(withId(R.id.searchEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.searchBtn)).perform(click());
        onView(withId(R.id.searchEditText)).check(matches(not(isDisplayed())));
    }
}