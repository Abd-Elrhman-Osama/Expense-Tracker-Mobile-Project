package com.example.expensetracker

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddExpenseScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun addExpenseFlow() {
        // 1. افتح شاشة الإضافة
        composeTestRule.onNodeWithContentDescription("Add Expense")
            .performClick()

        // 2. تحقق من ظهور الحقول
        composeTestRule.onNodeWithText("Add New Expense")
            .assertExists()

        composeTestRule.onNodeWithText("Expense Name")
            .assertExists()

        composeTestRule.onNodeWithText("Amount")
            .assertExists()

        // 3. اكتب في حقل الاسم
        composeTestRule.onNodeWithText("Expense Name")
            .performTextInput("Test Lunch")

        // 4. اكتب في حقل المبلغ
        composeTestRule.onNodeWithText("Amount")
            .performTextInput("50")

        // 5. اضغط على زر الحفظ
        composeTestRule.onNodeWithText("Save Expense")
            .performClick()

        // 6. عد للشاشة الرئيسية وتحقق (هذا اختبار بسيط)
        Thread.sleep(500) // انتظر قليلاً

        composeTestRule.onNodeWithText("Expense Tracker")
            .assertExists()
    }
}