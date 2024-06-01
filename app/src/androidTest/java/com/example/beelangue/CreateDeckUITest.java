package com.example.beelangue;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CreateDeckUITest {

    private AutoCloseable closeable;
    @Rule
    public ActivityScenarioRule<CreateDeckActivity> activityRule = new ActivityScenarioRule(CreateDeckActivity.class);

    @Before
    public void setUp() {
//        Intents.init();
//        closeable = MockitoAnnotations.openMocks(this);
        onView(withId(R.id.deckTitle)).perform(typeText("Science"), closeSoftKeyboard());
        onView(withId(R.id.wordText)).perform(typeText("Particles"), closeSoftKeyboard());
    }

//    @After
//    public void tearDown() throws Exception {
//        Intents.release();
//        closeable.close();
//    }
    @Test
    public void clearWordInputFiled() {
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.putExtra("selected_language", "indonesian");
//        ActivityScenario.launch(intent);
        onView(withId(R.id.addCardBtn)).perform(click());
        onView(withId(R.id.wordText)).check(matches(withText("")));
    }

    @Test
    public void checkTitleInputField() {
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.putExtra("selected_language", "indonesian");
//        ActivityScenario.launch(intent);
        onView(withId(R.id.addCardBtn)).perform(click());
        onView(withId(R.id.deckTitle)).check(matches(withText("Science")));
    }

    @Test
    public void finishCreating() {
        onView(withId(R.id.addCardBtn)).perform(click());
        onView(withId(R.id.finishDeckBtn)).perform(click());
//        intended(hasComponent(LearnExploreActivity.class.getName()));
//        intended(hasExtra("selected_language", "indonesian"));
    }

//    @Test
//    public void checkBackButtonToDeckList() {
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.putExtra("selected_language", "indonesian");
//        ActivityScenario.launch(intent);
//
//        // Click the back button
//        onView(withId(R.id.backButton)).perform(click());
//
//        // Verify that the intent has the correct target activity and the correct extra
//        intended(hasComponent(LearnExploreActivity.class.getName()));
//        intended(hasExtra("selected_language", "indonesian"));
//    }
}