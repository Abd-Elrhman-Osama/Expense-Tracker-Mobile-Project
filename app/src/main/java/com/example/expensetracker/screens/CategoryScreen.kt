package com.example.expensetracker.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.database.AppDatabase
import com.example.expensetracker.database.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var newCategoryName by remember { mutableStateOf("") }

    // تحميل الفئات عند البدء
    LaunchedEffect(Unit) {
        scope.launch {
            val db = AppDatabase.getInstance(context)
            categories = withContext(Dispatchers.IO) {
                db.categoryDao().getAllCategories()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Manage Categories") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // إضافة فئة جديدة
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Add New Category",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newCategoryName,
                            onValueChange = { newCategoryName = it },
                            label = { Text("Category Name") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (newCategoryName.isNotBlank()) {
                                    scope.launch {
                                        // التحقق من عدم تكرار الفئة
                                        val db = AppDatabase.getInstance(context)
                                        val existing = withContext(Dispatchers.IO) {
                                            db.categoryDao().getCategoryByName(newCategoryName)
                                        }

                                        if (existing == null) {
                                            // إضافة الفئة الجديدة
                                            withContext(Dispatchers.IO) {
                                                db.categoryDao().insert(Category(name = newCategoryName))
                                            }
                                            newCategoryName = ""

                                            // تحديث القائمة
                                            categories = withContext(Dispatchers.IO) {
                                                db.categoryDao().getAllCategories()
                                            }
                                        }
                                    }
                                }
                            },
                            enabled = newCategoryName.isNotBlank()
                        ) {
                            Text("Add")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // قائمة الفئات
            Text(
                "Existing Categories",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (categories.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No categories yet. Add your first category!")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(categories) { category ->
                        CategoryItem(
                            category = category,
                            onDelete = {
                                scope.launch {
                                    withContext(Dispatchers.IO) {
                                        AppDatabase.getInstance(context).categoryDao().delete(category)
                                    }
                                    // تحديث القائمة
                                    val db = AppDatabase.getInstance(context)
                                    categories = withContext(Dispatchers.IO) {
                                        db.categoryDao().getAllCategories()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                category.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}