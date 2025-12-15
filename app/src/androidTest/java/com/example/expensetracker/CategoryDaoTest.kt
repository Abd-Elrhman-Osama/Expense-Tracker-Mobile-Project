package com.example.expensetracker

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.expensetracker.database.AppDatabase
import com.example.expensetracker.database.Category
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class CategoryDaoTest {

    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
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
    fun insertAndReadCategory() = runBlocking {
        // Add category
        val category = Category(name = "Food")
        db.categoryDao().insert(category)

        // Read categories
        val allCategories = db.categoryDao().getAllCategories()

        // Verify
        assert(allCategories.isNotEmpty())
        assert(allCategories[0].name == "Food")
    }

    @Test
    fun deleteCategory() = runBlocking {
        // Add category
        val category = Category(name = "To Delete")
        db.categoryDao().insert(category)

        // Get and delete
        val allCategories = db.categoryDao().getAllCategories()
        val addedCategory = allCategories.first()
        db.categoryDao().delete(addedCategory)

        // Verify deletion
        val afterDelete = db.categoryDao().getAllCategories()
        assert(afterDelete.isEmpty())
    }

    @Test
    fun getCategoryByName() = runBlocking {
        // Add category
        db.categoryDao().insert(Category(name = "Transport"))

        // Search by name
        val foundCategory = db.categoryDao().getCategoryByName("Transport")

        // Verify
        assert(foundCategory != null)
        assert(foundCategory?.name == "Transport")
    }
}