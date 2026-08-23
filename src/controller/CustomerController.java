package controller;

import model.Customer;
import model.Movie;
import utils.ValidationException;

import java.util.List;

public class CustomerController {
    private UserController userController;
    private MovieController movieController;

    // Constructor Injection: nhận UserController (dùng CHUNG nguồn dữ liệu User)
    // và MovieController (validate FK chéo movieId)
    public CustomerController(UserController userController, MovieController movieController) {
        this.userController = userController;
        this.movieController = movieController;
    }

    // ===================== WATCHLIST =====================

    /**
     * Thêm phim vào danh sách chờ xem.
     * Validate: Customer tồn tại + active, Movie tồn tại + active, chưa có trong watchlist.
     */
    public boolean addToWatchlist(String customerId, String movieId) throws ValidationException {
        Customer customer = userController.findActiveCustomerReferenceForController(customerId);
        validateActiveMovieExists(movieId);

        List<String> watchlist = customer.getWatchlist(); // Lấy bản sao
        if (watchlist.contains(movieId)) {
            throw new ValidationException("Phim '" + movieId + "' đã có trong danh sách chờ xem!");
        }
        watchlist.add(movieId);
        customer.setWatchlist(watchlist); // Gán lại bản sao đã sửa
        return userController.persistUserChanges();
    }

    /**
     * Xóa phim khỏi danh sách chờ xem.
     * Không cần check movie active — phim đã bị gỡ vẫn được phép xóa khỏi watchlist.
     */
    public boolean removeFromWatchlist(String customerId, String movieId) throws ValidationException {
        Customer customer = userController.findActiveCustomerReferenceForController(customerId);

        List<String> watchlist = customer.getWatchlist();
        if (!watchlist.remove(movieId)) {
            throw new ValidationException("Phim '" + movieId + "' không có trong danh sách chờ xem!");
        }
        customer.setWatchlist(watchlist);
        return userController.persistUserChanges();
    }

    // ===================== FAVOURITES =====================

    /**
     * Thêm phim vào danh sách yêu thích.
     * Khi thành công, gọi movieController.incrementFavouritesCount() để tăng tổng lượt thích.
     */
    public boolean addToFavourites(String customerId, String movieId) throws ValidationException {
        Customer customer = userController.findActiveCustomerReferenceForController(customerId);
        validateActiveMovieExists(movieId);

        List<String> favourites = customer.getFavouriteList();
        if (favourites.contains(movieId)) {
            throw new ValidationException("Phim '" + movieId + "' đã có trong danh sách yêu thích!");
        }
        favourites.add(movieId);
        customer.setFavouriteList(favourites);

        // Tăng số lượt yêu thích đang active trên Movie (Phương án A - Current State)
        movieController.incrementFavouritesCount(movieId);

        return userController.persistUserChanges();
    }

    /**
     * Xóa phim khỏi danh sách yêu thích.
     * Giảm favouritesCount trên Movie (Phương án A — phản ánh số lượng đang active yêu thích).
     */
    public boolean removeFromFavourites(String customerId, String movieId) throws ValidationException {
        Customer customer = userController.findActiveCustomerReferenceForController(customerId);

        List<String> favourites = customer.getFavouriteList();
        if (!favourites.remove(movieId)) {
            throw new ValidationException("Phim '" + movieId + "' không có trong danh sách yêu thích!");
        }
        customer.setFavouriteList(favourites);

        // Giảm số lượt yêu thích đang active trên Movie (Phương án A - Current State)
        movieController.decrementFavouritesCount(movieId);

        return userController.persistUserChanges();
    }

    // ===================== WATCH HISTORY =====================

    /**
     * Thêm phim vào lịch sử xem.
     * Cho phép trùng — xem lại nhiều lần sẽ ghi nhận nhiều lần.
     * Khi thành công, gọi movieController.incrementViews() để tăng tổng lượt xem.
     */
    public boolean addToWatchHistory(String customerId, String movieId) throws ValidationException {
        Customer customer = userController.findActiveCustomerReferenceForController(customerId);
        validateActiveMovieExists(movieId);

        List<String> history = customer.getWatchHistory();
        history.add(movieId); // Cho phép trùng — xem lại nhiều lần
        customer.setWatchHistory(history);

        // Tăng lượt xem trên Movie
        movieController.incrementViews(movieId);

        return userController.persistUserChanges();
    }

    // ===================== PRIVATE HELPERS =====================

    /**
     * Kiểm tra phim tồn tại và đang active.
     * Giống cách MovieController validate categoryId qua CategoryController.
     */
    private void validateActiveMovieExists(String movieId) throws ValidationException {
        if (movieId == null || movieId.trim().isEmpty()) {
            throw new ValidationException("Mã phim không được để trống!");
        }
        Movie movie = movieController.findById(movieId);
        if (movie == null) {
            throw new ValidationException("Phim có mã '" + movieId + "' không tồn tại!");
        }
        if (!movie.isActive()) {
            throw new ValidationException("Phim có mã '" + movieId + "' đã bị gỡ khỏi hệ thống!");
        }
    }
}
