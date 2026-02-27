package com.querycubix.quoteoftheday.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.querycubix.quoteoftheday.api.ZenQuotesApi;
import com.querycubix.quoteoftheday.db.QuoteDao;
import com.querycubix.quoteoftheday.db.QuoteDatabase;
import com.querycubix.quoteoftheday.model.Quote;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuoteRepository {
    private QuoteDao quoteDao;
    private ZenQuotesApi api;
    private ExecutorService executorService;

    public QuoteRepository(Application application) {
        QuoteDatabase db = QuoteDatabase.getInstance(application);
        quoteDao = db.quoteDao();
        executorService = Executors.newSingleThreadExecutor();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://zenquotes.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(ZenQuotesApi.class);
    }

    public void getRandomQuote(QuoteCallback callback) {
        api.getRandomQuote().enqueue(new Callback<List<Quote>>() {
            @Override
            public void onResponse(Call<List<Quote>> call, Response<List<Quote>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    callback.onSuccess(response.body().get(0));
                } else {
                    callback.onError("Failed to fetch quote");
                }
            }

            @Override
            public void onFailure(Call<List<Quote>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void insertFavorite(Quote quote) {
        executorService.execute(() -> {
            quote.setFavorite(true);
            quoteDao.insertQuote(quote);
        });
    }

    public void deleteFavorite(Quote quote) {
        executorService.execute(() -> quoteDao.deleteQuote(quote));
    }

    public LiveData<List<Quote>> getFavoriteQuotes() {
        return quoteDao.getFavoriteQuotes();
    }

    public interface QuoteCallback {
        void onSuccess(Quote quote);
        void onError(String message);
    }
}