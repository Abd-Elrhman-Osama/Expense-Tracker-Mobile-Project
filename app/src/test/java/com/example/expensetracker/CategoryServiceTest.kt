package com.example.expensetracker

import com.example.expensetracker.database.Category
import com.example.expensetracker.database.CategoryDao
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class CategoryServiceTest {

    @Mock
    private lateinit var mockCategoryDao: CategoryDao

    private lateinit var categoryService: CategoryService

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        categoryService = CategoryService(mockCategoryDao)
    }

    @Test
    fun `test add category`() = runBlocking {
        // Arrange
        val category = Category(name = "Food")

        // Act
        categoryService.addCategory(category)

        // Assert
        Mockito.verify(mockCategoryDao).insert(category)
    }

    @Test
    fun `test get all categories`() = runBlocking {
        // Arrange
        val mockCategories = listOf(
            Category(name = "Food"),
            Category(name = "Transport")
        )
        Mockito.`when`(mockCategoryDao.getAllCategories()).thenReturn(mockCategories)

        // Act
        val categories = categoryService.getAllCategories()

        // Assert
        assertEquals(2, categories.size)
        assertEquals("Food", categories[0].name)
    }
}

// Service بسيط للـ Categories
class CategoryService(private val categoryDao: CategoryDao) {
    suspend fun addCategory(category: Category) {
        categoryDao.insert(category)
    }

    suspend fun getAllCategories(): List<Category> {
        return categoryDao.getAllCategories()
    }
}