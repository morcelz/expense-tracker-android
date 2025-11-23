package com.example.tracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tracker.model.Expense;
import com.example.tracker.network.RetrofitClient;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddExpenseActivity extends AppCompatActivity {

    private Calendar calendar;
    private String expenseId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        calendar = Calendar.getInstance();

        SharedPreferences prefs = getSharedPreferences("TrackerPrefs", MODE_PRIVATE);
        userId = prefs.getString("userId", null);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());

        EditText etAmount = findViewById(R.id.etAmount);
        EditText etDescription = findViewById(R.id.etDescription);
        Spinner spCategory = findViewById(R.id.spCategory);
        EditText etDate = findViewById(R.id.etDate);

        // Setup Spinner
        String[] categories = { "Alimentation", "Transport", "Loisirs", "Logement", "Santé", "Divers" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        // Setup DatePicker
        updateLabel(etDate);
        etDate.setOnClickListener(v -> {
            new DatePickerDialog(AddExpenseActivity.this, (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(etDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Check if editing
        expenseId = getIntent().getStringExtra("EXPENSE_ID");
        if (expenseId != null) {
            double amount = getIntent().getDoubleExtra("EXPENSE_AMOUNT", 0);
            String desc = getIntent().getStringExtra("EXPENSE_DESC");
            String cat = getIntent().getStringExtra("EXPENSE_CAT");
            long date = getIntent().getLongExtra("EXPENSE_DATE", System.currentTimeMillis());

            etAmount.setText(String.valueOf(amount));
            etDescription.setText(desc);

            int spinnerPosition = adapter.getPosition(cat);
            spCategory.setSelection(spinnerPosition);

            calendar.setTimeInMillis(date);
            updateLabel(etDate);

            ((android.widget.Button) findViewById(R.id.btnAdd)).setText("Modifier");
        }

        findViewById(R.id.btnAdd).setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString();
            String description = etDescription.getText().toString();
            String category = spCategory.getSelectedItem().toString();

            if (!amountStr.isEmpty() && !description.isEmpty()) {
                double amount = Double.parseDouble(amountStr);
                long date = calendar.getTimeInMillis();
                Expense expense = new Expense(userId, amount, description, category, date);

                if (expenseId != null) {
                    expense.setUserId(userId);
                    // Update
                    RetrofitClient.getApiService().updateExpense(expenseId, expense).enqueue(new Callback<Expense>() {
                        @Override
                        public void onResponse(Call<Expense> call, Response<Expense> response) {
                            if (response.isSuccessful()) {
                                startActivity(new Intent(AddExpenseActivity.this, DashboardActivity.class));
                                finish();
                            } else {
                                Toast.makeText(AddExpenseActivity.this, "Error updating", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Expense> call, Throwable t) {
                            Toast.makeText(AddExpenseActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                } else {
                    // Create
                    RetrofitClient.getApiService().addExpense(expense).enqueue(new Callback<Expense>() {
                        @Override
                        public void onResponse(Call<Expense> call, Response<Expense> response) {
                            if (response.isSuccessful()) {
                                startActivity(new Intent(AddExpenseActivity.this, DashboardActivity.class));
                                finish();
                            } else {
                                Toast.makeText(AddExpenseActivity.this, "Error adding", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Expense> call, Throwable t) {
                            Toast.makeText(AddExpenseActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }
            }
        });
    }

    private void updateLabel(EditText etDate) {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        etDate.setText(sdf.format(calendar.getTime()));
    }
}
