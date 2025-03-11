package com.example.expensetracker;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.expensetracker.adapter.ExpenseAdapter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.PercentFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ExpenseViewModel viewModel;
    private PieChart pieChart;
    private ExpenseAdapter adapter;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        pieChart = findViewById(R.id.pieChart);
        setupPieChart();

        RecyclerView recyclerView = findViewById(R.id.expense_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab_add_expense);
        fab.setOnClickListener(v -> showAddExpenseDialog());

        adapter = new ExpenseAdapter();
        recyclerView.setAdapter(adapter);

        viewModel.getAllExpenses().observe(this, expenses -> {
            adapter.setExpenses(expenses);
        });

        // Observe category sums for chart
        viewModel.getCategorySums().observe(this, categorySums -> {
            updatePieChart(categorySums);
        });
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Expenses by Category");
        pieChart.setCenterTextSize(24f);
        pieChart.getDescription().setEnabled(false);
    }

    private void updatePieChart(List<ExpenseDao.CategorySum> categorySums) {
        List<PieEntry> entries = new ArrayList<>();
        for (ExpenseDao.CategorySum sum : categorySums) {
            entries.add(new PieEntry((float) sum.total, sum.category));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Categories");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        
        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        data.setValueFormatter(new PercentFormatter(pieChart));
        
        pieChart.setData(data);
        pieChart.invalidate();
    }

    private void showAddExpenseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_expense, null);
        
        EditText editTitle = view.findViewById(R.id.edit_title);
        EditText editAmount = view.findViewById(R.id.edit_amount);
        Spinner spinnerCategory = view.findViewById(R.id.spinner_category);
        Button buttonDate = view.findViewById(R.id.button_date);

        // Setup category spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinnerAdapter);

        // Setup date picker
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = dateFormat.format(calendar.getTime());
        buttonDate.setText(selectedDate);

        buttonDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view1, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        selectedDate = dateFormat.format(calendar.getTime());
                        buttonDate.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        builder.setView(view)
                .setTitle("Add Expense")
                .setPositiveButton("Save", (dialog, which) -> {
                    String title = editTitle.getText().toString();
                    double amount = Double.parseDouble(editAmount.getText().toString());
                    String category = spinnerCategory.getSelectedItem().toString();

                    Expense expense = new Expense(title, amount, category, selectedDate);
                    viewModel.insert(expense);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
} 