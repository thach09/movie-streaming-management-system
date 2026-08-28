package controller;

import model.Category;
import model.Movie;
import repository.IMovieRepository;
import utils.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class MovieController {
    private List<Movie> movieList;
    private IMovieRepository movieRepository;
    private CategoryController categoryController; // Liên kết để check Khóa Ngoại

    // Nhận categoryController từ Main hoặc Container
    public MovieController(IMovieRepository movieRepository, CategoryController categoryController) {
        this.categoryController = categoryController;
        this.movieRepository = movieRepository;
        this.movieList = movieRepository.loadAll();
    }

    // --- CÁC HÀM TRUY XUẤT (READ) ---

    // Hàm nội bộ: trả về tham chiếu thật để mutate
    private Movie findReferenceById(String id) {
        for (Movie movie : movieList) {
            if (movie.getId().equalsIgnoreCase(id)) {
                return movie;
            }
        }
        return null;
    }

    // Trả về bản sao sâu, dùng public an toàn
    public Movie findById(String id) {
        Movie movie = findReferenceById(id);
        return movie != null ? new Movie(movie) : null;
    }

    // Dành cho Admin: Lấy toàn bộ phim (kể cả inactive)
    public List<Movie> getAllMovies() {
        List<Movie> copies = new ArrayList<>();
        for (Movie movie : movieList) {
            copies.add(new Movie(movie));
        }
        return copies;
    }

    // Dành cho Customer: Chỉ lấy phim đang active
    public List<Movie> getActiveMovies() {
        List<Movie> activeCopies = new ArrayList<>();
        for (Movie movie : movieList) {
            if (movie.isActive()) {
                activeCopies.add(new Movie(movie));
            }
        }
        return activeCopies;
    }

    public boolean existsActiveMovieInCategory(String categoryId) {
        for (Movie movie : movieList) {
            if (movie.getCategoryId().equalsIgnoreCase(categoryId) && movie.isActive()) {
                return true;
            }
        }
        return false;
    }

    // --- CÁC HÀM NGHIỆP VỤ TẠO/SỬA (WRITE) ---

    public boolean addMovie(String id, String title, String categoryId, String director, String actors, int releaseYear) throws ValidationException {
        // 1. Kiểm tra trùng ID phim
        if (findReferenceById(id) != null) {
            throw new ValidationException("Mã phim '" + id + "' đã tồn tại!");
        }

        // 2. Ràng buộc Khóa Ngoại: Category phải tồn tại và đang active
        Category category = categoryController.findById(categoryId);
        if (category == null) {
            throw new ValidationException("Danh mục có mã '" + categoryId + "' không tồn tại!");
        }
        if (!category.isActive()) {
            throw new ValidationException("Danh mục có mã '" + categoryId + "' đã bị vô hiệu hóa, không thể thêm phim mới!");
        }

        // 3. Khởi tạo đối tượng (Tự validate ký tự |, rỗng...)
        // Phim mới mặc định: rating=0.0, views=0, favourites=0, isActive=true
        Movie newMovie = new Movie(id, title, categoryId, director, actors, releaseYear, 0.0, 0, 0, true);
        
        movieList.add(newMovie);
        return movieRepository.saveAll(movieList);
    }

    public boolean updateMovie(String id, String title, String categoryId, String director, String actors, int releaseYear) throws ValidationException {
        Movie movie = findReferenceById(id);
        if (movie == null) {
            throw new ValidationException("Không tìm thấy phim có mã '" + id + "'");
        }

        // Check Khóa ngoại mới (nếu đổi category)
        if (!movie.getCategoryId().equalsIgnoreCase(categoryId)) {
            Category category = categoryController.findById(categoryId);
            if (category == null) {
                throw new ValidationException("Danh mục có mã '" + categoryId + "' không tồn tại!");
            }
            if (!category.isActive()) {
                throw new ValidationException("Danh mục có mã '" + categoryId + "' đã bị vô hiệu hóa, không thể chuyển phim sang!");
            }
        }

        // 1. Tạo bản sao tạm để test Validation
        Movie temp = new Movie(movie);
        temp.setTitle(title);
        temp.setCategoryId(categoryId);
        temp.setDirector(director);
        temp.setActors(actors);
        temp.setReleaseYear(releaseYear);

        // 2. Nếu qua hết các Setter không lỗi, mới cập nhật vào Object thật
        movie.setTitle(title);
        movie.setCategoryId(categoryId);
        movie.setDirector(director);
        movie.setActors(actors);
        movie.setReleaseYear(releaseYear);
        
        return movieRepository.saveAll(movieList);
    }

    public boolean deleteMovie(String id) throws ValidationException {
        Movie movie = findReferenceById(id);
        if (movie == null) {
            throw new ValidationException("Không tìm thấy phim có mã '" + id + "'");
        }
        if (!movie.isActive()) {
            throw new ValidationException("Phim có mã '" + id + "' đã bị xóa trước đó!");
        }
        movie.setActive(false); // Soft Delete
        return movieRepository.saveAll(movieList);
    }

    // --- CÁC HÀM THỐNG KÊ (AUTO-CALCULATED FIELDS) ---

    public boolean incrementViews(String id) {
        Movie movie = findReferenceById(id);
        if (movie != null && movie.isActive()) {
            try {
                movie.setViews(movie.getViews() + 1);
                return movieRepository.saveAll(movieList);
            } catch (ValidationException e) {
                // Không bao giờ xảy ra vì số lượt xem chỉ tăng lên
            }
        }
        return false;
    }

    public boolean incrementFavouritesCount(String id) {
        Movie movie = findReferenceById(id);
        if (movie != null && movie.isActive()) {
            try {
                movie.setFavouritesCount(movie.getFavouritesCount() + 1);
                return movieRepository.saveAll(movieList);
            } catch (ValidationException e) {
                // Không bao giờ xảy ra vì số lượt thích chỉ tăng lên
            }
        }
        return false;
    }

    public boolean decrementFavouritesCount(String id) {
        Movie movie = findReferenceById(id);
        if (movie != null && movie.isActive() && movie.getFavouritesCount() > 0) {
            try {
                movie.setFavouritesCount(movie.getFavouritesCount() - 1);
                return movieRepository.saveAll(movieList);
            } catch (ValidationException e) {
                // Không bao giờ xảy ra vì đã check > 0 trước khi giảm
            }
        }
        return false;
    }

    // --- CÁC HÀM TÌM KIẾM & SẮP XẾP (TÍCH HỢP SORTUTILS / SEARCHUTILS) ---

    public List<Movie> sortMoviesByTitle(boolean ascending) {
        return utils.SortUtils.sortByTitle(getActiveMovies(), ascending);
    }

    public List<Movie> sortMoviesByRating(boolean ascending) {
        return utils.SortUtils.sortByRating(getActiveMovies(), ascending);
    }

    public List<Movie> sortMoviesByReleaseYear(boolean ascending) {
        return utils.SortUtils.sortByReleaseYear(getActiveMovies(), ascending);
    }

    public List<Movie> sortMoviesByPopularity(boolean ascending) {
        return utils.SortUtils.sortByPopularity(getActiveMovies(), ascending);
    }

    public List<Movie> searchMoviesByTitle(String keyword) {
        return utils.SearchUtils.searchByTitle(getActiveMovies(), keyword);
    }

    public List<Movie> searchMoviesByActor(String keyword) {
        return utils.SearchUtils.searchByActor(getActiveMovies(), keyword);
    }

    public List<Movie> searchMoviesByDirector(String keyword) {
        return utils.SearchUtils.searchByDirector(getActiveMovies(), keyword);
    }

    public List<Movie> searchMoviesByCategory(String categoryId) {
        return utils.SearchUtils.searchByCategoryId(getActiveMovies(), categoryId);
    }

    // --- THỐNG KÊ & XẾP HẠNG (GIAI ĐOẠN 4) ---

    /**
     * Tính tổng views của tất cả Movie active thuộc 1 categoryId.
     * Dùng cho CategoryController.getTrendingCategories().
     */
    public long getTotalViewsByCategory(String categoryId) {
        long totalViews = 0;
        for (Movie movie : movieList) {
            if (movie.isActive() && movie.getCategoryId().equalsIgnoreCase(categoryId)) {
                totalViews += movie.getViews();
            }
        }
        return totalViews;
    }

    /**
     * Trả về thống kê tổng quan hệ thống dưới dạng DTO Statistics.
     * Bao gồm: tổng số phim active, tổng views, tổng favourites, rating TB, top 5 phim xem nhiều nhất.
     */
    public model.Statistics getViewingStatistics() {
        List<Movie> activeMovies = getActiveMovies(); // Đã là bản sao

        long totalMovies = activeMovies.size();
        long totalViews = 0;
        long totalFavourites = 0;
        double sumRating = 0.0;

        for (Movie movie : activeMovies) {
            totalViews += movie.getViews();
            totalFavourites += movie.getFavouritesCount();
            sumRating += movie.getRating();
        }

        double averageRating = (totalMovies > 0) ? sumRating / totalMovies : 0.0;

        // Top 5 phim xem nhiều nhất — dùng lại SortUtils đã viết (Giai đoạn 3)
        List<Movie> sortedByViews = utils.SortUtils.sortByViews(activeMovies, false); // Giảm dần
        List<Movie> top5 = new ArrayList<>();
        for (int i = 0; i < sortedByViews.size() && i < 5; i++) {
            top5.add(sortedByViews.get(i));
        }

        return new model.Statistics(totalMovies, totalViews, totalFavourites, averageRating, top5);
    }

    /**
     * Auto Ranking: sắp xếp phim theo công thức điểm xếp hạng.
     * Công thức: score = rating * 10 + views * 0.01 + favouritesCount * 0.5
     * Dùng thuật toán Bubble Sort tự viết (không dùng Collections.sort).
     * Trả về danh sách phim active sắp theo score giảm dần.
     */
    public List<Movie> getAutoRanking() {
        List<Movie> activeMovies = getActiveMovies(); // Đã là bản sao
        int n = activeMovies.size();

        // Bubble Sort theo score giảm dần
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                double score1 = calculateRankingScore(activeMovies.get(j));
                double score2 = calculateRankingScore(activeMovies.get(j + 1));
                // Giảm dần: nếu score trước < score sau thì swap
                if (score1 < score2) {
                    Movie temp = activeMovies.get(j);
                    activeMovies.set(j, activeMovies.get(j + 1));
                    activeMovies.set(j + 1, temp);
                }
            }
        }
        return activeMovies;
    }

    /**
     * Công thức tính điểm xếp hạng
     * score = rating * 10 + views * 0.01 + favouritesCount * 0.5
     */
    private double calculateRankingScore(Movie movie) {
        return movie.getRating() * 10
                + movie.getViews() * 0.01
                + movie.getFavouritesCount() * 0.5;
    }
}
