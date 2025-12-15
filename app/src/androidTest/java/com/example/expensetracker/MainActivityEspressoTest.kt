package com.example.expensetracker

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityEspressoTest {

    // هذا Rule مهم جداً لـ Compose Tests
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appTitleIsDisplayed() {
        // التحقق من ظهور عنوان التطبيق
        composeTestRule.onNodeWithText("Expense Tracker")
            .assertExists()
    }

    @Test
    fun fabButtonIsDisplayed() {
        // التحقق من ظهور زر الإضافة
        composeTestRule.onNodeWithContentDescription("Add Expense")
            .assertExists()
    }

    @Test
    fun categoriesButtonIsDisplayed() {
        // التحقق من ظهور زر الفئات
        composeTestRule.onNodeWithContentDescription("Categories")
            .assertExists()
    }

    @Test
    fun summaryButtonIsDisplayed() {
        // التحقق من ظهور زر التقارير
        composeTestRule.onNodeWithContentDescription("Summary")
            .assertExists()
    }

    @Test
    fun clickFabOpensAddScreen() {
        // الضغط على زر الإضافة
        composeTestRule.onNodeWithContentDescription("Add Expense")
            .performClick()

        // التحقق من ظهور شاشة الإضافة
        composeTestRule.onNodeWithText("Add New Expense")
            .assertExists()
    }
}