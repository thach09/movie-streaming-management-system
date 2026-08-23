package utils;

import model.Movie;
import java.util.ArrayList;
import java.util.List;

public class SortUtils {
    
    // Thuật toán Bubble Sort cho Title
    public static List<Movie> sortByTitle(List<Movie> movies, boolean ascending) {
        List<Movie> result = new ArrayList<>(movies);
        int n = result.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                int cmp = result.get(j).getTitle().compareToIgnoreCase(result.get(j + 1).getTitle());
                // Nếu ascending: nếu trước > sau thì swap
                // Nếu descending: nếu trước < sau thì swap
                if (ascending ? cmp > 0 : cmp < 0) {
                    Movie temp = result.get(j);
                    result.set(j, result.get(j + 1));
                    result.set(j + 1, temp);
                }
            }
        }
        return result;
    }

    // Thuật toán Bubble Sort cho Rating
    public static List<Movie> sortByRating(List<Movie> movies, boolean ascending) {
        List<Movie> result = new ArrayList<>(movies);
        int n = result.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                double val1 = result.get(j).getRating();
                double val2 = result.get(j + 1).getRating();
                if (ascending ? val1 > val2 : val1 < val2) {
                    Movie temp = result.get(j);
                    result.set(j, result.get(j + 1));
                    result.set(j + 1, temp);
                }
            }
        }
        return result;
    }

    // Thuật toán Bubble Sort cho Release Year
    public static List<Movie> sortByReleaseYear(List<Movie> movies, boolean ascending) {
        List<Movie> result = new ArrayList<>(movies);
        int n = result.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                int val1 = result.get(j).getReleaseYear();
                int val2 = result.get(j + 1).getReleaseYear();
                if (ascending ? val1 > val2 : val1 < val2) {
                    Movie temp = result.get(j);
                    result.set(j, result.get(j + 1));
                    result.set(j + 1, temp);
                }
            }
        }
        return result;
    }

    // Thuật toán Bubble Sort cho Popularity = views + favouritesCount * 10
    public static List<Movie> sortByPopularity(List<Movie> movies, boolean ascending) {
        List<Movie> result = new ArrayList<>(movies);
        int n = result.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                long pop1 = result.get(j).getViews() + (result.get(j).getFavouritesCount() * 10);
                long pop2 = result.get(j + 1).getViews() + (result.get(j + 1).getFavouritesCount() * 10);
                if (ascending ? pop1 > pop2 : pop1 < pop2) {
                    Movie temp = result.get(j);
                    result.set(j, result.get(j + 1));
                    result.set(j + 1, temp);
                }
            }
        }
        return result;
    }
}
