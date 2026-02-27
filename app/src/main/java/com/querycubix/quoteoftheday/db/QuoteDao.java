package com.querycubix.quoteoftheday.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.querycubix.quoteoftheday.model.Quote;

import java.util.List;

@Dao
public interface QuoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuote(Quote quote);

    @Delete
    void deleteQuote(Quote quote);

    @Query("SELECT * FROM quotes WHERE isFavorite = 1")
    LiveData<List<Quote>> getFavoriteQuotes();

    @Query("SELECT * FROM quotes WHERE text = :text LIMIT 1")
    Quote getQuoteByText(String text);

    @Update
    void updateQuote(Quote quote);
}