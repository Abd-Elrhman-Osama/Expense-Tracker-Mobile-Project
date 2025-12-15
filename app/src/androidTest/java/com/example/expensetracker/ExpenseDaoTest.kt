package com.example.expensetracker

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.expensetracker.database.AppDatabase
import com.example.expensetracker.database.Expense
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class) // هذا مهم جداً
class ExpenseDaoTest {

    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        // نستخدم قاعدة بيانات في الذاكرة للاختبار
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndReadExpense() = runBlocking {
        // Create expense
        val expense = Expense(
            name = "Lunch",
            amount = 50.0,
            category = "Food",
            date = "2025-01-15"
        )

        // Insert
        db.expenseDao().insert(expense)

        // Read
        val allExpenses = db.expenseDao().getAllExpenses()

        // Verify
        assert(allExpenses.isNotEmpty())
        assert(allExpenses[0].name == "Lunch")
        assert(allExpenses[0].amount == 50.0)
    }

    @Test
    fun deleteExpense() = runBlocking {
        // Add expense
        val expense = Expense(
            name = "To Delete",
            amount = 100.0,
            category = "Test",
            date = "2025-01-01"
        )

        db.expenseDao().insert(expense)

        // Get and delete
        val allExpenses = db.expenseDao().getAllExpenses()
        val addedExpense = allExpenses.first()
        db.expenseDao().delete(addedExpense)

        // Verify deletion
        val afterDelete = db.expenseDao().getAllExpenses()
        assert(afterDelete.isEmpty())
    }

    @Test
    fun calculateTotalAmount() = runBlocking {
        // Add multiple expenses
        db.expenseDao().insert(Expense(name = "A", amount = 100.0, category = "Food", date = "2025-01-01"))
        db.expenseDao().insert(Expense(name = "B", amount = 200.0, category = "Transport", date = "2025-01-02"))
        db.expenseDao().insert(Expense(name = "C", amount = 300.0, category = "Shopping", date = "2025-01-03"))

        // Calculate total
        val total = db.expenseDao().getTotalAmount()

        // Verify
        assert(total == 600.0)
    }
}