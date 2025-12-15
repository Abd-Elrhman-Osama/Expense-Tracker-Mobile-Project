import 'package:flutter/material.dart';
import 'package:fl_chart/fl_chart.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Expense Chart',
      theme: ThemeData(
        primarySwatch: Colors.deepPurple,
      ),
      home: ExpenseChartScreen(),
    );
  }
}


class ExpenseChartScreen extends StatefulWidget {
  const ExpenseChartScreen({super.key});

  @override
  State<ExpenseChartScreen> createState() => _ExpenseChartScreenState();
}

class _ExpenseChartScreenState extends State<ExpenseChartScreen> {

  Map<String, double> expenseData = {
    'Oct 30': 50.0,
    'Oct 31': 100.0,
    'Nov 1': 125.0,
  };


  void updateData(Map<String, double> newData) {
    setState(() {
      expenseData = newData;
    });
  }

  @override
  Widget build(BuildContext context) {
    List<String> dates = expenseData.keys.toList();
    List<double> amounts = expenseData.values.toList();

    return Scaffold(
      appBar: AppBar(
        title: const Text('Expense Chart'),
        backgroundColor: Colors.deepPurpleAccent,
      ),
      body: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          children: [
            const Text(
              'Total Expense',
              style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
                color: Colors.deepPurple,
              ),
            ),

            const SizedBox(height: 20),

            // Chart
            Container(
              height: 200,
              padding: const EdgeInsets.symmetric(vertical: 20),
              child: BarChart(
                BarChartData(
                  barTouchData: BarTouchData(enabled: false),

                  barGroups: dates.asMap().entries.map((entry) {
                    int index = entry.key;
                    return BarChartGroupData(
                      x: index,
                      barRods: [
                        BarChartRodData(
                          toY: amounts[index],
                          color: Colors.blue,
                          width: 40,
                        ),
                      ],
                    );
                  }).toList(),

                  titlesData: const FlTitlesData(
                    bottomTitles: AxisTitles(sideTitles: SideTitles(showTitles: false)),
                    leftTitles: AxisTitles(sideTitles: SideTitles(showTitles: false)),
                    topTitles: AxisTitles(sideTitles: SideTitles(showTitles: false)),
                    rightTitles: AxisTitles(sideTitles: SideTitles(showTitles: false)),
                  ),

                  gridData: const FlGridData(show: false),
                  borderData: FlBorderData(show: false),
                ),
              ),
            ),

            // Dates
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 30),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: dates.map((date) {
                  return Text(
                    date,
                    style: const TextStyle(
                      fontSize: 14,
                      fontWeight: FontWeight.w500,
                    ),
                  );
                }).toList(),
              ),
            ),

            const SizedBox(height: 30),

            // Expense Details
            const Text(
              'Expense Details',
              style: TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.bold,
              ),
            ),

            const SizedBox(height: 15),

            // Expense List
            ...expenseData.entries.map((entry) {
              return Padding(
                padding: const EdgeInsets.symmetric(vertical: 8, horizontal: 40),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      entry.key,
                      style: const TextStyle(fontSize: 16),
                    ),
                    Text(
                      '${entry.value.toInt()} Egp',
                      style: const TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                        color: Colors.green,
                      ),
                    ),
                  ],
                ),
              );
            }).toList(),

          ],
        ),
      ),
    );
  }
}