package utils;

import model.Movie;
import java.util.ArrayList;
import java.util.List;

public class SearchUtils {

    // Thuật toán Linear Search cho Title
    public static List<Movie> searchByTitle(List<Movie> movies, String keyword) {
        List<Movie> result = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) return result;
        String kw = keyword.toLowerCase();
        
        for (Movie movie : movies) {
            if (movie.getTitle().toLowerCase().contains(kw)) {
                result.add(movie);
            }
        }
        return result;
    }

    // Thuật toán Linear Search cho Actor
    public static List<Movie> searchByActor(List<Movie> movies, String keyword) {
        List<Movie> result = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) return result;
        String kw = keyword.toLowerCase();
        
        for (Movie movie : movies) {
            if (movie.getActors().toLowerCase().contains(kw)) {
                result.add(movie);
            }
        }
        return result;
    }

    // Thuật toán Linear Search cho Director
    public static List<Movie> searchByDirector(List<Movie> movies, String keyword) {
        List<Movie> result = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) return result;
        String kw = keyword.toLowerCase();
        
        for (Movie movie : movies) {
            if (movie.getDirector().toLowerCase().contains(kw)) {
                result.add(movie);
            }
        }
        return result;
    }

    // Thuật toán Linear Search cho CategoryId (chính xác)
    public static List<Movie> searchByCategoryId(List<Movie> movies, String categoryId) {
        List<Movie> result = new ArrayList<>();
        if (categoryId == null || categoryId.trim().isEmpty()) return result;
        
        for (Movie movie : movies) {
            if (movie.getCategoryId().equalsIgnoreCase(categoryId)) {
                result.add(movie);
            }
        }
        return result;
    }
}
