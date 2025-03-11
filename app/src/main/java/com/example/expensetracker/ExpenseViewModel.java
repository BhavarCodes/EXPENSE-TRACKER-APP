package com.example.expensetracker;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.expensetracker.dao.ExpenseDao;
import com.example.expensetracker.database.ExpenseDatabase;
import com.example.expensetracker.model.Expense;
import java.util.List;

public class ExpenseViewModel extends AndroidViewModel {
    private ExpenseDao expenseDao;
    private LiveData<List<Expense>> allExpenses;
    private LiveData<List<ExpenseDao.CategorySum>> categorySums;

    public ExpenseViewModel(Application application) {
        super(application);
        ExpenseDatabase database = ExpenseDatabase.getInstance(application);
        expenseDao = database.expenseDao();
        allExpenses = expenseDao.getAllExpenses();
        categorySums = expenseDao.getCategorySum();
    }

    public void insert(Expense expense) {
        new Thread(() -> expenseDao.insert(expense)).start();
    }

    public void update(Expense expense) {
        new Thread(() -> expenseDao.update(expense)).start();
    }

    public void delete(Expense expense) {
        new Thread(() -> expenseDao.delete(expense)).start();
    }

    public LiveData<List<Expense>> getAllExpenses() {
        return allExpenses;
    }

    public LiveData<List<ExpenseDao.CategorySum>> getCategorySums() {
        return categorySums;
    }
} 