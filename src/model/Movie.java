package model;

public class Movie {
    // Encapsulation
    private String id;
    private String title;
    private String category;
    private String director;
    private String actors;
    private int releaseYear;
    private double rating;
    private long views;
    private long favouritesCount;
    private boolean isActive;

    // Constructor không tham số
    public Movie () {
        this.isActive = true;
        this.views = 0;
        this.favouritesCount = 0;
        this.rating = 0.0;
    }

    // Constructor đầy đủ tham số
    public Movie (String id, String title, String category, String director, String actors, int releaseYear, double rating, long views, long favouritesCount, boolean isActive) {
        setId(id);
        setTitle(title);
        setCategory(category);
        setDirector(director);
        setActors(actors);
        setReleaseYear(releaseYear);
        setRating(rating);
        setViews(views);
        setFavouritesCount(favouritesCount);
        setActive(isActive);
    }

    //=== Getters & Setters ===
    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phim không được để trống!");
        }
        this.id = id.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên phim không được để trống!");
        }
        this.title = title.trim();
    }
    
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Thể loại phim không được để trống");
        }
        this.category = category.trim();
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        if (director == null || director.trim().isEmpty()) {
            throw new IllegalArgumentException("Đạo diễn không được để trống!");
        }
        this.director = director.trim();
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        if (actors == null || actors.trim().isEmpty()) {
            throw new IllegalArgumentException("Danh sách diễn viên không được để trống!");
        }
        this.actors = actors.trim();
    }

    public int getReleaseYear() {
        return releaseYear;
    }
    
    public void setReleaseYear(int releaseYear) {
        int currentYear = java.time.Year.now().getValue();
        if (releaseYear < 1888 || releaseYear > currentYear) {
            throw new IllegalArgumentException("Năm phát hành không hợp lệ. Phải nằm trong khoảng 1888 đến " + currentYear);
        }
        this.releaseYear = releaseYear;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        if (rating < 0.0 || rating > 10.0) {
            throw new IllegalArgumentException("Điểm đánh giá phải nằm trong khoảng 0.0 đến 10.0");
        }
        this.rating = rating;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        if (views < 0) {
            throw new IllegalArgumentException("Lượt xem không được âm");
        }
        this.views = views;
    }

    public long getFavouritesCount() {
        return favouritesCount;
    }

    public void setFavouritesCount(long favouritesCount) {
        if (favouritesCount < 0) {
            throw new IllegalArgumentException("Lượt yêu thích không được âm");
        }
        this.favouritesCount = favouritesCount;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
    
        @Override
    public String toString() {
        return String.format("Phim [ID: %s | Tên: '%s' | Thể loại: %s | Đạo diễn: %s | Diễn viên: %s | Năm: %d | Rating: %.1f★ | Lượt xem: %d | Yêu thích: %d | Trạng thái: %s]",
                id, title, category, director, actors, releaseYear, rating, views, favouritesCount, (isActive ? "ACTIVE" : "INACTIVE"));
    }
    
}




