package com.example.expensetracker

import org.junit.Assert.*
import org.junit.Test

class ExpenseValidationTest {

    // هذا الـ test بسيط جداً ومش محتاج Mockito
    @Test
    fun `test valid expense with positive amount`() {
        assertTrue(isValidExpense("Lunch", 50.0))
    }

    @Test
    fun `test invalid expense with negative amount`() {
        assertFalse(isValidExpense("Lunch", -50.0))
    }

    @Test
    fun `test invalid expense with empty name`() {
        assertFalse(isValidExpense("", 50.0))
        assertFalse(isValidExpense("   ", 50.0))
    }

    @Test
    fun `test invalid expense with zero amount`() {
        assertFalse(isValidExpense("Lunch", 0.0))
    }

    // هذه function بسيطة جداً للاختبار
    private fun isValidExpense(name: String, amount: Double): Boolean {
        return name.trim().isNotBlank() && amount > 0
    }
}