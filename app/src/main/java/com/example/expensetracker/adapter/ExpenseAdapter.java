package com.example.expensetracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensetracker.R;
import com.example.expensetracker.model.Expense;
import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private List<Expense> expenses = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_item, parent, false);
        return new ExpenseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense current = expenses.get(position);
        holder.titleTextView.setText(current.getTitle());
        holder.amountTextView.setText(String.format("$%.2f", current.getAmount()));
        holder.categoryTextView.setText(current.getCategory());
        holder.dateTextView.setText(current.getDate());
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
        notifyDataSetChanged();
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView amountTextView;
        private TextView categoryTextView;
        private TextView dateTextView;

        public ExpenseViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_title);
            amountTextView = itemView.findViewById(R.id.text_amount);
            categoryTextView = itemView.findViewById(R.id.text_category);
            dateTextView = itemView.findViewById(R.id.text_date);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(expenses.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Expense expense);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
} 