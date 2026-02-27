package com.querycubix.quoteoftheday.api;

import com.querycubix.quoteoftheday.model.Quote;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ZenQuotesApi {
    @GET("api/random")
    Call<List<Quote>> getRandomQuote();
}