package model;

import utils.ValidationException;
import java.util.Objects;

public class Movie {
    // Encapsulation
    private String id;
    private String title;
    private String categoryId;
    private String director;
    private String actors;
    private int releaseYear;
    private double rating;
    private long views;
    private long favouritesCount;
    private boolean isActive;

    // 2. Constructor không tham số (Mặc định)
    public Movie() {
        this.isActive = true;
        this.views = 0;
        this.favouritesCount = 0;
        this.rating = 0.0;
    }

    // Copy Constructor: Tạo bản sao sâu bằng cách gán field trực tiếp
    // (Bỏ qua Setter để tránh ném ValidationException thừa thãi)
    public Movie(Movie source) {
        this.id = source.id;
        this.title = source.title;
        this.categoryId = source.categoryId;
        this.director = source.director;
        this.actors = source.actors;
        this.releaseYear = source.releaseYear;
        this.rating = source.rating;
        this.views = source.views;
        this.favouritesCount = source.favouritesCount;
        this.isActive = source.isActive;
    }

    // Constructor đầy đủ tham số
    public Movie (String id, String title, String categoryId, String director, String actors, int releaseYear, double rating, long views, long favouritesCount, boolean isActive) throws ValidationException {
        setId(id);
        setTitle(title);
        setCategoryId(categoryId);
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

    public void setId(String id) throws ValidationException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Mã phim không được để trống!");
        }
        if (id.contains("|")) throw new ValidationException("Dữ liệu không được chứa ký tự '|'");
        if (id.contains(",")) throw new ValidationException("Mã phim không được chứa ký tự ','");
        this.id = id.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) throws ValidationException {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Tên phim không được để trống!");
        }
        if (title.contains("|")) throw new ValidationException("Dữ liệu không được chứa ký tự '|'");
        this.title = title.trim();
    }
    
    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String categoryId) throws ValidationException {
        if (categoryId == null || categoryId.trim().isEmpty()) {
            throw new ValidationException("Mã thể loại phim không được để trống");
        }
        if (categoryId.contains("|")) throw new ValidationException("Dữ liệu không được chứa ký tự '|'");
        this.categoryId = categoryId.trim();
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) throws ValidationException {
        if (director == null || director.trim().isEmpty()) {
            throw new ValidationException("Đạo diễn không được để trống!");
        }
        if (director.contains("|")) throw new ValidationException("Dữ liệu không được chứa ký tự '|'");
        this.director = director.trim();
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) throws ValidationException {
        if (actors == null || actors.trim().isEmpty()) {
            throw new ValidationException("Danh sách diễn viên không được để trống!");
        }
        if (actors.contains("|")) throw new ValidationException("Dữ liệu không được chứa ký tự '|'");
        this.actors = actors.trim();
    }

    public int getReleaseYear() {
        return releaseYear;
    }
    
    public void setReleaseYear(int releaseYear) throws ValidationException {
        int currentYear = java.time.Year.now().getValue();
        if (releaseYear < 1888 || releaseYear > currentYear) {
            throw new ValidationException("Năm phát hành không hợp lệ. Phải nằm trong khoảng 1888 đến " + currentYear);
        }
        this.releaseYear = releaseYear;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) throws ValidationException {
        if (rating < 0.0 || rating > 10.0) {
            throw new ValidationException("Điểm đánh giá phải nằm trong khoảng 0.0 đến 10.0");
        }
        this.rating = rating;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) throws ValidationException {
        if (views < 0) {
            throw new ValidationException("Lượt xem không được âm");
        }
        this.views = views;
    }

    public long getFavouritesCount() {
        return favouritesCount;
    }

    public void setFavouritesCount(long favouritesCount) throws ValidationException {
        if (favouritesCount < 0) {
            throw new ValidationException("Lượt yêu thích không được âm");
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
        return String.format("Phim [ID: %s | Tên: '%s' | Mã Thể loại: %s | Đạo diễn: %s | Diễn viên: %s | Năm: %d | Rating: %.1f★ | Lượt xem: %d | Yêu thích: %d | Trạng thái: %s]",
                id, title, categoryId, director, actors, releaseYear, rating, views, favouritesCount, (isActive ? "ACTIVE" : "INACTIVE"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(id, movie.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
}




