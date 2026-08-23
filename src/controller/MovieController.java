package controller;

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

        // 2. Ràng buộc Khóa Ngoại: Category phải tồn tại
        if (categoryController.findById(categoryId) == null) {
            throw new ValidationException("Danh mục có mã '" + categoryId + "' không tồn tại!");
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
            if (categoryController.findById(categoryId) == null) {
                throw new ValidationException("Danh mục có mã '" + categoryId + "' không tồn tại!");
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
}
