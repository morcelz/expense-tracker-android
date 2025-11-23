package com.example.tracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tracker.model.Expense;
import com.example.tracker.network.RetrofitClient;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView rvExpenses;
    private ExpenseAdapter adapter;
    private TextView tvTotalBalance;
    private List<Expense> expenseList = new ArrayList<>();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        SharedPreferences prefs = getSharedPreferences("TrackerPrefs", MODE_PRIVATE);
        userId = prefs.getString("userId", null);

        if (userId == null) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        }

        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        rvExpenses = findViewById(R.id.rvExpenses);
        rvExpenses.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ExpenseAdapter(expenseList);
        rvExpenses.setAdapter(adapter);

        loadExpenses();

        findViewById(R.id.fabAdd).setOnClickListener(v -> {
            startActivity(new Intent(this, AddExpenseActivity.class));
        });

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            prefs.edit().clear().apply();
            startActivity(new Intent(this, WelcomeActivity.class));
            finishAffinity();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses();
    }

    private void loadExpenses() {
        RetrofitClient.getApiService().getExpenses(userId).enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    expenseList.clear();
                    expenseList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    updateTotalBalance();
                }
            }

            @Override
            public void onFailure(Call<List<Expense>> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Error loading expenses: " + t.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void updateTotalBalance() {
        double total = 0;
        for (Expense e : expenseList) {
            total += e.getAmount();
        }
        tvTotalBalance.setText(String.format(Locale.FRANCE, "%.2f €", total));
    }

    class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
        private List<Expense> expenses;

        ExpenseAdapter(List<Expense> expenses) {
            this.expenses = expenses;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Expense expense = expenses.get(position);
            if (expense != null) {
                holder.tvAmount.setText(String.format(Locale.FRANCE, "%.2f €", expense.getAmount()));
                holder.tvDescription.setText(expense.getDescription());
                holder.tvCategory.setText(expense.getCategory());
                holder.tvDate.setText(
                        new SimpleDateFormat("dd MMM yyyy", Locale.FRANCE).format(new Date(expense.getDate())));

                holder.btnDelete.setOnClickListener(v -> {
                    RetrofitClient.getApiService().deleteExpense(expense.getId(), userId).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                loadExpenses();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(DashboardActivity.this, "Error deleting: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                });

                holder.btnEdit.setOnClickListener(v -> {
                    Intent intent = new Intent(DashboardActivity.this, AddExpenseActivity.class);
                    intent.putExtra("EXPENSE_ID", expense.getId());
                    intent.putExtra("EXPENSE_AMOUNT", expense.getAmount());
                    intent.putExtra("EXPENSE_DESC", expense.getDescription());
                    intent.putExtra("EXPENSE_CAT", expense.getCategory());
                    intent.putExtra("EXPENSE_DATE", expense.getDate());
                    startActivity(intent);
                });
            }
        }

        @Override
        public int getItemCount() {
            return expenses.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvAmount, tvDescription, tvCategory, tvDate;
            View btnDelete, btnEdit;

            ViewHolder(View itemView) {
                super(itemView);
                tvAmount = itemView.findViewById(R.id.tvAmount);
                tvDescription = itemView.findViewById(R.id.tvDescription);
                tvCategory = itemView.findViewById(R.id.tvCategory);
                tvDate = itemView.findViewById(R.id.tvDate);
                btnDelete = itemView.findViewById(R.id.btnDelete);
                btnEdit = itemView.findViewById(R.id.btnEdit);
            }
        }
    }
}
