package com.querycubix.quoteoftheday.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.querycubix.quoteoftheday.model.Quote;
import com.querycubix.quoteoftheday.repository.QuoteRepository;
import java.util.List;

public class QuoteViewModel extends AndroidViewModel {
    private QuoteRepository repository;
    private MutableLiveData<Quote> currentQuote = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();

    public QuoteViewModel(Application application) {
        super(application);
        repository = new QuoteRepository(application);
        fetchNewQuote();
    }

    public void fetchNewQuote() {
        repository.getRandomQuote(new QuoteRepository.QuoteCallback() {
            @Override
            public void onSuccess(Quote quote) {
                currentQuote.postValue(quote);
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
            }
        });
    }

    public void toggleFavorite() {
        Quote quote = currentQuote.getValue();
        if (quote != null) {
            if (quote.isFavorite()) {
                repository.deleteFavorite(quote);
            } else {
                repository.insertFavorite(quote);
            }
            quote.setFavorite(!quote.isFavorite());
            currentQuote.postValue(quote);
        }
    }

    public LiveData<Quote> getCurrentQuote() {
        return currentQuote;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<List<Quote>> getFavoriteQuotes() {
        return repository.getFavoriteQuotes();
    }
}