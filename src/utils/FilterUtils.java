package utils;

import model.Movie;
import java.util.ArrayList;
import java.util.List;

public class FilterUtils {
    /**
     * Lọc kết hợp nhiều tiêu chí (AND). Tham số null/rỗng nghĩa là "không lọc theo
     * tiêu chí đó". Dùng linear scan tự viết, không dùng Stream API.
     */
    public static List<Movie> advancedFilter(List<Movie> movies, String categoryId,
            Integer minYear, Integer maxYear, Double minRating,
            String director, String actor) {
        List<Movie> result = new ArrayList<>();
        for (Movie m : movies) {
            if (categoryId != null && !categoryId.trim().isEmpty()
                    && !m.getCategoryId().equalsIgnoreCase(categoryId)) continue;
            if (minYear != null && m.getReleaseYear() < minYear) continue;
            if (maxYear != null && m.getReleaseYear() > maxYear) continue;
            if (minRating != null && m.getRating() < minRating) continue;
            if (director != null && !director.trim().isEmpty()
                    && !m.getDirector().toLowerCase().contains(director.toLowerCase())) continue;
            if (actor != null && !actor.trim().isEmpty()
                    && !m.getActors().toLowerCase().contains(actor.toLowerCase())) continue;
            result.add(m);
        }
        return result;
    }
}
