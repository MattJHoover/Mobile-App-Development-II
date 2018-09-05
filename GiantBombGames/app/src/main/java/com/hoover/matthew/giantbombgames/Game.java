package com.hoover.matthew.giantbombgames;

public class Game {

    private String name;
    private String imageUrl;
    private String description;
    private String releaseDate;
    private String aliases;
    private String summary;
    private String dateAdded;
    private String dateUpdated;

    public String getName() {
        return name;
    }

    public void setName(String gameName) {
        this.name = gameName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String url) {
        this.imageUrl = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String gameDescription) {
        this.description = gameDescription;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String release) {
        this.releaseDate = release;
    }

    public String getAliases() {
        return aliases;
    }

    public void setAliases(String alias) {
        this.aliases = alias;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String deck) {
        this.summary = deck;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String added) {
        this.dateAdded = added;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String updated) {
        this.dateUpdated = updated;
    }
}