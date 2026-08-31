package model;

import utils.ValidationException;

public class WatchProgress {
    private String movieId;
    private int percent; // 0-100

    public WatchProgress(String movieId, int percent) throws ValidationException {
        setMovieId(movieId);
        setPercent(percent);
    }

    // Copy Constructor
    public WatchProgress(WatchProgress source) {
        this.movieId = source.movieId;
        this.percent = source.percent;
    }

    public String getMovieId() { return movieId; }

    public void setMovieId(String movieId) throws ValidationException {
        if (movieId == null || movieId.trim().isEmpty())
            throw new ValidationException("Mã phim không được để trống!");
        if (movieId.contains("|") || movieId.contains(",") || movieId.contains(":"))
            throw new ValidationException("Mã phim không được chứa ký tự đặc biệt!");
        this.movieId = movieId.trim();
    }

    public int getPercent() { return percent; }

    public void setPercent(int percent) throws ValidationException {
        if (percent < 0 || percent > 100)
            throw new ValidationException("Tiến độ xem phải nằm trong khoảng 0-100!");
        this.percent = percent;
    }

    @Override
    public String toString() {
        return "WatchProgress[movieId=" + movieId + ", percent=" + percent + "%]";
    }
}
