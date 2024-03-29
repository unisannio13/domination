package net.yura.domination.mapstore;

/**
 * @author Yura
 */
public class Map {

    String id;
    String name;
    String authorId;
    String authorName;
    String numberOfDownloads;
    String rating;
    String numberOfRatings;
    String previewUrl;
    String mapUrl;
    String description;
    String version;
    String dateAdded;
    String mapWidth;
    String mapHeight;

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMapHeight() {
        return mapHeight;
    }

    public void setMapHeight(String mapHeight) {
        this.mapHeight = mapHeight;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    public String getMapWidth() {
        return mapWidth;
    }

    public void setMapWidth(String mapWidth) {
        this.mapWidth = mapWidth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumberOfDownloads() {
        return numberOfDownloads;
    }

    public void setNumberOfDownloads(String numberOfDownloads) {
        this.numberOfDownloads = numberOfDownloads;
    }

    public String getNumberOfRatings() {
        return numberOfRatings;
    }

    public void setNumberOfRatings(String numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    
/*
    String cardsFile;
    String cardsURL;
    String mapFile;
    String mapURL;
    String previewFile;
    String PreviewURL;
    String imagePicFile;
    String imagePicURL;
    String imageMapFile;
    String imageMapURL;
*/

    public String toString() {

        // print out full info with XML
        //ByteArrayOutputStream out = new ByteArrayOutputStream();
        //try { new XMLMapAccess().save(out, this); } catch (IOException ex) { RiskUtil.printStackTrace(ex); }
        //return out.toString();

        return name; // this is used in the list for the keyboard quick jump
    }

}
