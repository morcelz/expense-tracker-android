package com.example.tracker.network;

import com.example.tracker.model.Expense;
import com.example.tracker.model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("register")
    Call<User> register(@Body User user);

    @POST("login")
    Call<User> login(@Body User user);

    @GET("expenses")
    Call<List<Expense>> getExpenses(@Query("userId") String userId);

    @POST("expenses")
    Call<Expense> addExpense(@Body Expense expense);

    @PUT("expenses/{id}")
    Call<Expense> updateExpense(@Path("id") String id, @Body Expense expense);

    @DELETE("expenses/{id}")
    Call<Void> deleteExpense(@Path("id") String id, @Query("userId") String userId);
}
