package com.example.storyapp.view


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.storyapp.R
import com.example.storyapp.utils.LoginIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainActivityTest{
    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)
    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(LoginIdlingResource.countingIdlingResource)
    }
    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(LoginIdlingResource.countingIdlingResource)
    }

    @Test
    fun test_logout_success() {
        Intents.init()
        onView(withId(R.id.logoutButton)).check(matches(isDisplayed()))
        onView(withId(R.id.logoutButton)).perform(click())
        intended(hasComponent(LoginActivity::class.java.name))
        onView(withId(R.id.loginEmail)).check(matches(isDisplayed()))
        onView(withId(R.id.loginPassword)).check(matches(isDisplayed()))
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()))
    }
}