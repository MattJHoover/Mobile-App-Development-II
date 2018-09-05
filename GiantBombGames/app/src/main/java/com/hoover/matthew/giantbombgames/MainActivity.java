package com.hoover.matthew.giantbombgames;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    private static final String API_URL = "https://www.giantbomb.com/api/games/?api_key=f68103452fd9806dbbabe18f27d873ba40b09718&field_list=aliases,date_added,date_last_updated,deck,description,image,name,original_release_date&offset=";
    private SearchView search;
    private ProgressBar progressBar;
    private ListView game_list;

    private List<Game> games = new ArrayList<>();
    private static List<Game> favorites = new ArrayList<>();

    private GameAdapter gameAdapter = new GameAdapter(games);
    private GameAdapter favoriteAdapter = new GameAdapter(favorites);

    private Filter gameFilter;

    private BottomNavigationView navigation;

    private Boolean gotGames = false;

    private TextView app_title;
    private ImageView app_logo;
    private TextView app_favorites;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_about:
                    search.setVisibility(View.GONE);
                    game_list.setVisibility(View.GONE);
                    app_favorites.setVisibility(View.GONE);
                    app_title.setVisibility(View.VISIBLE);
                    app_logo.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_games:
                    app_title.setVisibility(View.GONE);
                    app_logo.setVisibility(View.GONE);
                    app_favorites.setVisibility(View.GONE);
                    search.setVisibility(View.VISIBLE);
                    game_list.setVisibility(View.VISIBLE);
                    if(!gotGames) {
                        GetGameTask task = new GetGameTask();
                        task.execute(API_URL + "0");
                        gotGames = true;
                    }
                    game_list.setAdapter(gameAdapter);
                    return true;
                case R.id.navigation_favorites:
                    app_title.setVisibility(View.GONE);
                    app_logo.setVisibility(View.GONE);
                    search.setVisibility(View.GONE);
                    if(favorites.isEmpty()) {
                        app_favorites.setVisibility(View.VISIBLE);
                    } else {
                        app_favorites.setVisibility(View.GONE);
                    }
                    game_list.setVisibility(View.VISIBLE);
                    game_list.setAdapter(favoriteAdapter);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        search = findViewById(R.id.search);
        search.setQueryHint("Search games...");
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                gameAdapter.getFilter().filter(s);
                return true;
            }
        });
        search.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar);
        game_list = findViewById(R.id.game_list);
        game_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(navigation.getSelectedItemId() == R.id.navigation_favorites) {
                    Game game = favoriteAdapter.getItem(i);
                    displayGameInformation(game);
                } else {
                    Game game = gameAdapter.getItem(i);
                    displayGameInformation(game);
                }
            }
        });
        game_list.setVisibility(View.GONE);
        app_favorites = findViewById(R.id.app_favorites);
        app_favorites.setVisibility(View.GONE);
        app_title = findViewById(R.id.app_title);
        app_title.setVisibility(View.VISIBLE);
        app_logo = findViewById(R.id.app_logo);
        app_logo.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        navigation.setSelectedItemId(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    public void displayGameInformation(Game game) {
        Intent intent = new Intent(this, InformationActivity.class);
        String name = game.getName();
        String imageUrl = game.getImageUrl();
        String description = game.getDescription();
        String releaseDate = game.getReleaseDate();
        String aliases = game.getAliases();
        String summary = game.getSummary();
        String dateAdded = game.getDateAdded();
        String dateUpdated = game.getDateUpdated();
        intent.putExtra("Name", name);
        intent.putExtra("Image", imageUrl);
        intent.putExtra("Description", description);
        intent.putExtra("Release", releaseDate);
        intent.putExtra("Aliases", aliases);
        intent.putExtra("Summary", summary);
        intent.putExtra("Added", dateAdded);
        intent.putExtra("Updated", dateUpdated);
        startActivity(intent);
    }

    public static List<Game> getFavorites() {
        return favorites;
    }

    private class GetGameTask extends AsyncTask<String, Void, List<Game>> {

        @Override
        protected void onPreExecute() {
            search.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Game> doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.parse(connection.getInputStream());
                    NodeList nodeList = document.getElementsByTagName("game");
                    for(int i = 0; i < nodeList.getLength(); i++) {
                        Node node = nodeList.item(i);
                        if(node.getNodeType() == Node.ELEMENT_NODE){
                            Element element = (Element) node;
                            String name = element.getElementsByTagName("name").item(0).getTextContent();
                            String imageUrl = element.getElementsByTagName("medium_url").item(0).getTextContent();
                            String description = element.getElementsByTagName("description").item(0).getTextContent();
                            String releaseDate = element.getElementsByTagName("original_release_date").item(0).getTextContent();
                            String aliases = element.getElementsByTagName("aliases").item(0).getTextContent();
                            String summary = element.getElementsByTagName("deck").item(0).getTextContent();
                            String dateAdded = element.getElementsByTagName("date_added").item(0).getTextContent();
                            String dateUpdated = element.getElementsByTagName("date_last_updated").item(0).getTextContent();
                            Game game = new Game();
                            game.setName(name);
                            game.setImageUrl(imageUrl);
                            game.setDescription(description);
                            game.setReleaseDate(releaseDate);
                            game.setAliases(aliases);
                            game.setSummary(summary);
                            game.setDateAdded(dateAdded);
                            game.setDateUpdated(dateUpdated);
                            games.add(game);
                        }
                    }
                } finally {
                    connection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return games;
        }

        @Override
        protected void onPostExecute(List<Game> games) {
            progressBar.setVisibility(View.GONE);
            game_list.setAdapter(gameAdapter);
            search.setVisibility(View.VISIBLE);
        }
    }

    class GameAdapter extends BaseAdapter implements Filterable {

        private List<Game> games;

        private GameAdapter(List<Game> games) {
            this.games = games;
        }

        @Override
        public int getCount() {
            return games.size();
        }

        @Override
        public Game getItem(int position) {
            return games.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.game, parent, false);
            }
            Game game = getItem(position);
            TextView nameView = convertView.findViewById(R.id.game_name);
            nameView.setText(game.getName());
            return convertView;
        }

        @Override
        public Filter getFilter() {
            if(gameFilter == null) {
                gameFilter = new GameFilter();
            }
            return gameFilter;
        }
    }

    private class GameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();
            if(charSequence != null && charSequence.length() > 0) {
                List<Game> tempGames = new ArrayList<>();
                for(Game game : games) {
                    if(game.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        tempGames.add(game);
                    }
                }
                filterResults.count = tempGames.size();
                filterResults.values = tempGames;
            } else {
                filterResults.count = games.size();
                filterResults.values = games;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            gameAdapter.games = (List<Game>) filterResults.values;
            gameAdapter.notifyDataSetChanged();
        }
    }
}