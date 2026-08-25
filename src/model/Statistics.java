package model;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO (Data Transfer Object) chứa thống kê tổng quan hệ thống.
 * Không có validation vì đây là dữ liệu chỉ đọc, được tính toán từ Controller.
 */
public class Statistics {
    private long totalMovies;
    private long totalViews;
    private long totalFavourites;
    private double averageRating;
    private List<Movie> top5MostViewed;

    public Statistics(long totalMovies, long totalViews, long totalFavourites,
                      double averageRating, List<Movie> top5MostViewed) {
        this.totalMovies = totalMovies;
        this.totalViews = totalViews;
        this.totalFavourites = totalFavourites;
        this.averageRating = averageRating;
        this.top5MostViewed = (top5MostViewed != null) ? new ArrayList<>(top5MostViewed) : new ArrayList<>();
    }

    // === Getters (chỉ đọc, không cần Setters) ===

    public long getTotalMovies() {
        return totalMovies;
    }

    public long getTotalViews() {
        return totalViews;
    }

    public long getTotalFavourites() {
        return totalFavourites;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public List<Movie> getTop5MostViewed() {
        return new ArrayList<>(top5MostViewed); // Trả bản sao bảo vệ đóng gói
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== THỐNG KÊ HỆ THỐNG ===\n");
        sb.append(String.format("Tổng số phim active   : %d\n", totalMovies));
        sb.append(String.format("Tổng lượt xem         : %d\n", totalViews));
        sb.append(String.format("Tổng lượt yêu thích   : %d\n", totalFavourites));
        sb.append(String.format("Điểm đánh giá TB      : %.2f★\n", averageRating));
        sb.append("--- Top 5 phim xem nhiều nhất ---\n");
        if (top5MostViewed.isEmpty()) {
            sb.append("  (Chưa có dữ liệu)\n");
        } else {
            for (int i = 0; i < top5MostViewed.size(); i++) {
                Movie m = top5MostViewed.get(i);
                sb.append(String.format("  %d. %s — %d lượt xem\n",
                        i + 1, m.getTitle(), m.getViews()));
            }
        }
        return sb.toString();
    }
}
