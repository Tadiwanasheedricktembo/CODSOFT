package com.querycubix.quoteoftheday;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.querycubix.quoteoftheday.databinding.ActivityMainBinding;
import com.querycubix.quoteoftheday.model.Quote;
import com.querycubix.quoteoftheday.viewmodel.QuoteViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private QuoteViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(QuoteViewModel.class);

        viewModel.getCurrentQuote().observe(this, this::updateUI);
        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnRefresh.setOnClickListener(v -> viewModel.fetchNewQuote());
        binding.btnFavorite.setOnClickListener(v -> viewModel.toggleFavorite());
        binding.btnShare.setOnClickListener(v -> shareQuote());
    }

    private void updateUI(Quote quote) {
        if (quote != null) {
            binding.tvQuoteText.setText(quote.getText());
            binding.tvAuthor.setText("- " + quote.getAuthor());
            binding.btnFavorite.setText(quote.isFavorite() ? "Saved" : "Save");
        }
    }

    private void shareQuote() {
        Quote quote = viewModel.getCurrentQuote().getValue();
        if (quote != null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "\"" + quote.getText() + "\" - " + quote.getAuthor());
            startActivity(Intent.createChooser(intent, "Share Quote via"));
        }
    }
}