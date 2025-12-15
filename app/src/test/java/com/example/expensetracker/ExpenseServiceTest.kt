package com.example.expensetracker

import com.example.expensetracker.database.Expense
import com.example.expensetracker.database.ExpenseDao
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class ExpenseServiceTest {

    // نصنع Mock لـ ExpenseDao
    @Mock
    private lateinit var mockExpenseDao: ExpenseDao

    private lateinit var expenseService: ExpenseService

    @Before
    fun setup() {
        // هذا السطر مهم جداً لتهيئة الـ Mocks
        MockitoAnnotations.openMocks(this)
        expenseService = ExpenseService(mockExpenseDao)
    }

    @Test
    fun `test calculate total expenses`() = runBlocking {
        // Arrange (التجهيز)
        val mockExpenses = listOf(
            Expense(name = "Lunch", amount = 100.0, category = "Food", date = "2025-01-01"),
            Expense(name = "Bus", amount = 50.0, category = "Transport", date = "2025-01-01")
        )

        // نحدد سلوك الـ Mock
        Mockito.`when`(mockExpenseDao.getAllExpenses()).thenReturn(mockExpenses)

        // Act (التنفيذ)
        val total = expenseService.calculateTotalExpenses()

        // Assert (التحقق)
        assertEquals(150.0, total, 0.001) // 0.001 tolerance للـ doubles
    }

    @Test
    fun `test add expense calls dao insert`() = runBlocking {
        // Arrange
        val expense = Expense(
            name = "Test Expense",
            amount = 100.0,
            category = "Test",
            date = "2025-01-01"
        )

        // Act
        expenseService.addExpense(expense)

        // Assert
        Mockito.verify(mockExpenseDao).insert(expense)
    }

    @Test
    fun `test expense validation logic`() {
        // Test بدون Mocking - فقط logic
        assertTrue(expenseService.isExpenseValid("Lunch", 50.0))
        assertFalse(expenseService.isExpenseValid("", 50.0))
        assertFalse(expenseService.isExpenseValid("Lunch", 0.0))
    }
}

// هذا Service بسيط للاختبار
class ExpenseService(private val expenseDao: ExpenseDao) {
    suspend fun calculateTotalExpenses(): Double {
        val expenses = expenseDao.getAllExpenses()
        return expenses.sumOf { it.amount }
    }

    suspend fun addExpense(expense: Expense) {
        expenseDao.insert(expense)
    }

    fun isExpenseValid(name: String, amount: Double): Boolean {
        return name.trim().isNotBlank() && amount > 0
    }
}