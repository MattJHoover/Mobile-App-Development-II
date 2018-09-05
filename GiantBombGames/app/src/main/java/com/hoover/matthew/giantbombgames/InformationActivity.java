package com.hoover.matthew.giantbombgames;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class InformationActivity extends AppCompatActivity {

    ImageView image;
    WebView gameDescription;
    Button add_favorites;
    Game game = new Game();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        Intent intent = getIntent();
        String name = intent.getStringExtra("Name");
        game.setName(name);
        setTitle(name);
        String aliases = intent.getStringExtra("Aliases");
        if(aliases.isEmpty()) {
            aliases = "No data";
        }
        game.setAliases(aliases);
        String summary = intent.getStringExtra("Summary");
        if(summary.isEmpty()) {
            summary = "No data";
        }
        game.setSummary(summary);
        String description = intent.getStringExtra("Description");
        if(description.isEmpty()) {
            description = "No data";
        }
        game.setDescription(description);
        gameDescription = findViewById(R.id.description);
        String release = intent.getStringExtra("Release");
        if(release.isEmpty()) {
            release = "No data";
        }
        String added = intent.getStringExtra("Added");
        if(added.isEmpty()) {
            added = "No data";
        }
        String updated = intent.getStringExtra("Updated");
        if(updated.isEmpty()) {
            updated = "No data";
        }
        SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat newFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date releaseDate = oldFormat.parse(release);
            release = newFormat.format(releaseDate);
            Date dateAdded = oldFormat.parse(added);
            added = newFormat.format(dateAdded);
            Date dateUpdated = oldFormat.parse(updated);
            updated = newFormat.format(dateUpdated);
        } catch (Exception e) {
            e.printStackTrace();
        }
        game.setReleaseDate(release);
        game.setDateAdded(added);
        game.setDateUpdated(updated);
        String webDescription = "<html><body style='color:#FFFFFF;background-color:#808080'><h1>Aliases</h1><p>" + aliases;
        webDescription += "</p><h1>Brief Summary</h1><p>" + summary;
        webDescription += "</p><h1>Description</h1>" + description;
        webDescription += "<h1>Original Release Date</h1><p>" + release;
        webDescription += "</p><h1>Date Added</h1><p>" + added;
        webDescription += "</p><h1>Date Last Updated</h1><p>" + updated;
        webDescription += "</p></body></html>";
        gameDescription.loadData(webDescription, "text/html", null);
        add_favorites = findViewById(R.id.addFavorites);
        String imageUrl = intent.getStringExtra("Image");
        game.setImageUrl(imageUrl);
        image = findViewById(R.id.image);
        GetImageTask task = new GetImageTask();
        task.execute(imageUrl);
    }

    public void addToFavorites(View view) {
        List<Game> favorites = MainActivity.getFavorites();
        if(favorites.isEmpty()) {
            favorites.add(game);
            FavoritesDialog favoritesDialog = new FavoritesDialog();
            favoritesDialog.show(getFragmentManager(), "dialog");
        } else {
            for (Game favoriteGame : favorites) {
                if (!favoriteGame.getName().equals(game.getName())) {
                    favorites.add(game);
                    FavoritesDialog favoritesDialog = new FavoritesDialog();
                    favoritesDialog.show(getFragmentManager(), "dialog");
                } else {
                    ErrorDialog errorDialog = new ErrorDialog();
                    errorDialog.show(getFragmentManager(), "dialog");
                }
            }
        }
    }

    private class GetImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            Bitmap image = null;
            try {
                URL imageUrl = new URL(url);
                InputStream stream = imageUrl.openStream();
                image = BitmapFactory.decodeStream(stream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return  image;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            image.setImageBitmap(bitmap);
        }
    }
}