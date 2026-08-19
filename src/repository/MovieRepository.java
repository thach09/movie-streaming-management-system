package repository;

import model.Movie;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MovieRepository {
    private static final String FILE_PATH = "data/movies.txt";
    private static final String DELIMITER = "|";

    // Lưu toàn bộ danh sách phim xuống file
    public boolean saveAll(List<Movie> movieList) {
        File file = new File(FILE_PATH);
        
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs(); 
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Movie movie : movieList) { // Duyệt qua từng Movie
                StringBuilder line = new StringBuilder();
                
                line.append(movie.getId()).append(DELIMITER)
                    .append(movie.getTitle()).append(DELIMITER)
                    .append(movie.getCategoryId()).append(DELIMITER)
                    .append(movie.getDirector()).append(DELIMITER)
                    .append(movie.getActors()).append(DELIMITER)
                    .append(movie.getReleaseYear()).append(DELIMITER)
                    .append(movie.getRating()).append(DELIMITER) // Auto change double to String
                    .append(movie.getViews()).append(DELIMITER) // Auto change int to String
                    .append(movie.getFavouritesCount()).append(DELIMITER) // Auto change long to String
                    .append(movie.isActive());

                    bw.write(line.toString());
                    bw.newLine();
            }
            return true;

        } catch (IOException e) {
            System.err.println("Lỗi ghi file: " + e.getMessage());
            return false;
        } 
        
    } 

    // Hàm đọc toàn bộ Movie từ ổ cứng lên RAM
    public List<Movie> loadAll() {
        List<Movie> movieList = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        if (!file.exists()) {
            return movieList;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                try { 
                    String[] parts = line.split("\\|", -1);

                    // Phim phải có đủ 10 cột dữ liệu
                    if (parts.length >= 10) {
                        String id = parts[0];
                        String title = parts[1];
                        String categoryId = parts[2];
                        String director = parts[3];
                        String actors = parts[4];
                        // PARSING
                        int releaseYear = Integer.parseInt(parts[5]);
                        double rating = Double.parseDouble(parts[6]);
                        long views = Long.parseLong(parts[7]);
                        long favouritesCount = Long.parseLong(parts[8]);
                        boolean isActive = Boolean.parseBoolean(parts[9]);

                        // Khởi tạo Movie bằng Constructor đầy đủ

                        Movie movie = new Movie(id, title, categoryId, director, actors, releaseYear, rating, views, favouritesCount, isActive);
                        movieList.add(movie);

                    }

                } catch (Exception e) {
                    System.err.println("Caution: Bỏ qua dòng phim bị lỗi dữ liệu - " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi đọc file movies: " + e.getMessage());
        }

        return movieList;
    } 

}
