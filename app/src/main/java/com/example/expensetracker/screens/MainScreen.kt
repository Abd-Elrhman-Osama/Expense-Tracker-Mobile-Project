package com.example.expensetracker.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PieChartOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.database.AppDatabase
import com.example.expensetracker.database.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // States للتحكم في الشاشات
    var showAddScreen by remember { mutableStateOf(false) }
    var showCategoryScreen by remember { mutableStateOf(false) }
    var showSummaryScreen by remember { mutableStateOf(false) }
    var showEditScreen by remember { mutableStateOf(false) }
    var expenseToEdit by remember { mutableStateOf<Expense?>(null) }

    // البيانات
    var expenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var totalAmount by remember { mutableStateOf(0.0) }

    // دالة لتحميل البيانات
    fun loadData() {
        scope.launch {
            val db = AppDatabase.getInstance(context)
            expenses = withContext(Dispatchers.IO) {
                db.expenseDao().getAllExpenses()
            }
            totalAmount = withContext(Dispatchers.IO) {
                db.expenseDao().getTotalAmount() ?: 0.0
            }
        }
    }

    // دالة لفتح Flutter App
    fun openFlutterApp() {
        try {
            // 1. إنشاء قائمة بسيطة من Amount و Date فقط
            val simpleData = ArrayList<HashMap<String, Any>>()

            for (expense in expenses) {
                val item = HashMap<String, Any>()
                item["date"] = expense.date
                item["amount"] = expense.amount
                simpleData.add(item)
            }

            // 2. تحويل إلى JSON بسيط
            val jsonArray = JSONArray()
            for (item in simpleData) {
                val jsonObject = JSONObject()
                jsonObject.put("date", item["date"])
                jsonObject.put("amount", item["amount"])
                jsonArray.put(jsonObject)
            }

            val jsonString = jsonArray.toString()
            Log.d("FlutterApp", "Sending data: $jsonString")
            Log.d("FlutterApp", "Total amount: $totalAmount")

            // 3. فتح Flutter App
            val intent = Intent().apply {
                // استخدام package name الصحيح
                setClassName(
                    "com.example.expense_tracker",  // package name
                    "com.example.expense_tracker.MainActivity"  // activity name
                )

                // إرسال البيانات
                putExtra("EXPENSE_DATA", jsonString)
                putExtra("TOTAL", totalAmount)

                // flags
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(intent)

        } catch (e: Exception) {
            Log.e("FlutterApp", "Error: ${e.message}")
            Toast.makeText(
                context,
                "Cannot open chart: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // تحميل البيانات عند البدء
    LaunchedEffect(Unit) {
        loadData()
    }

    // إذا ضغطنا على زر Add، ننتقل لشاشة الإضافة
    if (showAddScreen) {
        AddExpenseScreen(
            onBack = {
                showAddScreen = false
                loadData()
            }
        )
        return
    }

    // إذا ضغطنا على زر Categories، ننتقل لشاشة الفئات
    if (showCategoryScreen) {
        CategoryScreen(
            onBack = {
                showCategoryScreen = false
                loadData()
            }
        )
        return
    }

    // إذا ضغطنا على زر Summary، ننتقل لشاشة التقارير
    if (showSummaryScreen) {
        SummaryScreen(
            expenses = expenses,
            onBack = { showSummaryScreen = false }
        )
        return
    }

    // إذا ضغطنا على زر Edit، ننتقل لشاشة التعديل
    if (showEditScreen && expenseToEdit != null) {
        AddExpenseScreen(
            onBack = {
                showEditScreen = false
                expenseToEdit = null
                loadData()
            },
            expenseToEdit = expenseToEdit
        )
        return
    }

    // الشاشة الرئيسية
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Expense Tracker") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { showCategoryScreen = true }) {
                        Icon(Icons.Default.List, contentDescription = "Categories")
                    }
                    IconButton(onClick = { showSummaryScreen = true }) {
                        Icon(Icons.Default.PieChartOutline, contentDescription = "Summary")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddScreen = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // قسم الترحيب والإحصائيات
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Welcome!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Total Expenses",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        "$${String.format("%.2f", totalAmount)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "${expenses.size} transactions",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // قائمة المصروفات
            if (expenses.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "No expenses yet",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Tap the + button to add your first expense",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    items(expenses) { expense ->
                        ExpenseItem(
                            expense = expense,
                            onDelete = {
                                scope.launch {
                                    withContext(Dispatchers.IO) {
                                        AppDatabase.getInstance(context).expenseDao().delete(expense)
                                    }
                                    loadData()
                                    Toast.makeText(context, "Expense deleted", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onEdit = {
                                expenseToEdit = expense
                                showEditScreen = true
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // زر فتح Flutter App (بسيط جداً)
            Button(
                onClick = { openFlutterApp() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Open Flutter Expense Report")
            }
        }
    }
}

@Composable
fun ExpenseItem(
    expense: Expense,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        expense.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            expense.category,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    "Date: ${expense.date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "$${String.format("%.2f", expense.amount)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    TextButton(
                        onClick = onEdit,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text("Edit")
                    }

                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}