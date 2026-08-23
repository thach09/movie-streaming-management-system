package controller;

import model.Customer;
import model.Movie;
import model.User;
import repository.IUserRepository;
import utils.ValidationException;

import java.util.List;

public class CustomerController {
    private List<User> userList;
    private IUserRepository userRepository;
    private MovieController movieController;

    // Constructor Injection: nhận IUserRepository và MovieController
    public CustomerController(IUserRepository userRepository, MovieController movieController) {
        this.userRepository = userRepository;
        this.userList = userRepository.loadAll();
        this.movieController = movieController;
    }

    // ===================== WATCHLIST =====================

    /**
     * Thêm phim vào danh sách chờ xem.
     * Validate: Customer tồn tại + active, Movie tồn tại + active, chưa có trong watchlist.
     */
    public boolean addToWatchlist(String customerId, String movieId) throws ValidationException {
        Customer customer = findActiveCustomerReference(customerId);
        validateActiveMovieExists(movieId);

        List<String> watchlist = customer.getWatchlist(); // Lấy bản sao
        if (watchlist.contains(movieId)) {
            throw new ValidationException("Phim '" + movieId + "' đã có trong danh sách chờ xem!");
        }
        watchlist.add(movieId);
        customer.setWatchlist(watchlist); // Gán lại bản sao đã sửa
        return userRepository.saveAll(userList);
    }

    /**
     * Xóa phim khỏi danh sách chờ xem.
     * Không cần check movie active — phim đã bị gỡ vẫn được phép xóa khỏi watchlist.
     */
    public boolean removeFromWatchlist(String customerId, String movieId) throws ValidationException {
        Customer customer = findActiveCustomerReference(customerId);

        List<String> watchlist = customer.getWatchlist();
        if (!watchlist.remove(movieId)) {
            throw new ValidationException("Phim '" + movieId + "' không có trong danh sách chờ xem!");
        }
        customer.setWatchlist(watchlist);
        return userRepository.saveAll(userList);
    }

    // ===================== FAVOURITES =====================

    /**
     * Thêm phim vào danh sách yêu thích.
     * Khi thành công, gọi movieController.incrementFavouritesCount() để tăng tổng lượt thích.
     */
    public boolean addToFavourites(String customerId, String movieId) throws ValidationException {
        Customer customer = findActiveCustomerReference(customerId);
        validateActiveMovieExists(movieId);

        List<String> favourites = customer.getFavouriteList();
        if (favourites.contains(movieId)) {
            throw new ValidationException("Phim '" + movieId + "' đã có trong danh sách yêu thích!");
        }
        favourites.add(movieId);
        customer.setFavouriteList(favourites);

        // Tăng tổng lượt thích trên Movie (tổng lịch sử, không giảm khi remove)
        movieController.incrementFavouritesCount(movieId);

        return userRepository.saveAll(userList);
    }

    /**
     * Xóa phim khỏi danh sách yêu thích.
     * KHÔNG giảm favouritesCount — đây là tổng lịch sử yêu thích, không phải số "đang active".
     */
    public boolean removeFromFavourites(String customerId, String movieId) throws ValidationException {
        Customer customer = findActiveCustomerReference(customerId);

        List<String> favourites = customer.getFavouriteList();
        if (!favourites.remove(movieId)) {
            throw new ValidationException("Phim '" + movieId + "' không có trong danh sách yêu thích!");
        }
        customer.setFavouriteList(favourites);
        // KHÔNG gọi decrementFavouritesCount — favouritesCount là tổng lịch sử
        return userRepository.saveAll(userList);
    }

    // ===================== WATCH HISTORY =====================

    /**
     * Thêm phim vào lịch sử xem.
     * Cho phép trùng — xem lại nhiều lần sẽ ghi nhận nhiều lần.
     * Khi thành công, gọi movieController.incrementViews() để tăng tổng lượt xem.
     */
    public boolean addToWatchHistory(String customerId, String movieId) throws ValidationException {
        Customer customer = findActiveCustomerReference(customerId);
        validateActiveMovieExists(movieId);

        List<String> history = customer.getWatchHistory();
        history.add(movieId); // Cho phép trùng — xem lại nhiều lần
        customer.setWatchHistory(history);

        // Tăng lượt xem trên Movie
        movieController.incrementViews(movieId);

        return userRepository.saveAll(userList);
    }

    // ===================== PRIVATE HELPERS =====================

    /**
     * Tìm Customer (tham chiếu thật) trong danh sách nội bộ.
     * Chỉ trả về Customer đang active.
     * Throw ValidationException nếu: không tìm thấy, không phải Customer, hoặc đã bị vô hiệu hóa.
     */
    private Customer findActiveCustomerReference(String customerId) throws ValidationException {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new ValidationException("ID khách hàng không được để trống!");
        }
        for (User user : userList) {
            if (user.getId().equalsIgnoreCase(customerId)) {
                if (!(user instanceof Customer)) {
                    throw new ValidationException("Tài khoản '" + customerId + "' không phải là khách hàng!");
                }
                if (!user.isActive()) {
                    throw new ValidationException("Tài khoản khách hàng '" + customerId + "' đã bị vô hiệu hóa!");
                }
                return (Customer) user;
            }
        }
        throw new ValidationException("Không tìm thấy khách hàng có ID '" + customerId + "'!");
    }

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
