package com.example.expensetracker.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.expensetracker.database.AppDatabase
import com.example.expensetracker.database.Category
import com.example.expensetracker.database.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onBack: () -> Unit,
    expenseToEdit: Expense? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // متغيرات الحقول
    var expenseName by remember { mutableStateOf(expenseToEdit?.name ?: "") }
    var expenseAmount by remember { mutableStateOf(expenseToEdit?.amount?.toString() ?: "") }
    var selectedCategory by remember { mutableStateOf(expenseToEdit?.category ?: "") }

    // متغيرات Date Picker
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(
        expenseToEdit?.date ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    ) }

    // DatePicker State
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).parse(selectedDate)?.time ?: System.currentTimeMillis()
    )

    // قائمة الفئات من قاعدة البيانات
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }

    // تحميل الفئات عند البدء
    LaunchedEffect(Unit) {
        scope.launch {
            val db = AppDatabase.getInstance(context)
            categories = withContext(Dispatchers.IO) {
                db.categoryDao().getAllCategories()
            }

            // إذا كانت هناك فئات، اختر الأولى إذا لم يكن هناك فئة محددة
            if (categories.isNotEmpty() && selectedCategory.isEmpty()) {
                selectedCategory = categories[0].name
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { dateMillis ->
                            selectedDate = SimpleDateFormat(
                                "yyyy-MM-dd",
                                Locale.getDefault()
                            ).format(Date(dateMillis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            // DatePicker
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (expenseToEdit != null) "Edit Expense" else "Add New Expense"
                    )
                },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // حقل اسم المصروف
            OutlinedTextField(
                value = expenseName,
                onValueChange = { expenseName = it },
                label = { Text("Expense Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = expenseName.isBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // حقل المبلغ
            OutlinedTextField(
                value = expenseAmount,
                onValueChange = { expenseAmount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                placeholder = { Text("0.00") },
                isError = expenseAmount.toDoubleOrNull() == null || expenseAmount.toDoubleOrNull()!! <= 0
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown للفئات
            if (categories.isNotEmpty()) {
                var expanded by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxWidth()) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        selectedCategory = category.name
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                // إذا لم تكن هناك فئات، عرض حقل نصي
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = { selectedCategory = it },
                    label = { Text("Category (Add in Categories screen first)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date Picker
            OutlinedTextField(
                value = selectedDate,
                onValueChange = { /* لا يمكن التعديل يدوياً */ },
                label = { Text("Date") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date")
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // زر الحفظ/التحديث
            Button(
                onClick = {
                    if (expenseName.isBlank()) {
                        Toast.makeText(context, "Please enter expense name", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val amount = expenseAmount.toDoubleOrNull()
                    if (amount == null || amount <= 0) {
                        Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (selectedCategory.isBlank()) {
                        Toast.makeText(context, "Please select or enter a category", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    scope.launch {
                        withContext(Dispatchers.IO) {
                            val db = AppDatabase.getInstance(context)

                            if (expenseToEdit != null) {
                                // تحديث مصروف موجود
                                val updatedExpense = expenseToEdit.copy(
                                    name = expenseName,
                                    amount = amount,
                                    category = selectedCategory,
                                    date = selectedDate
                                )
                                db.expenseDao().update(updatedExpense)
                            } else {
                                // إضافة مصروف جديد
                                val expense = Expense(
                                    name = expenseName,
                                    amount = amount,
                                    category = selectedCategory,
                                    date = selectedDate
                                )
                                db.expenseDao().insert(expense)
                            }
                        }
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = expenseName.isNotBlank() &&
                        expenseAmount.isNotBlank() &&
                        selectedCategory.isNotBlank()
            ) {
                Text(
                    if (expenseToEdit != null) "Update Expense" else "Save Expense",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}