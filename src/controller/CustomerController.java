package controller;

import model.Customer;
import model.Movie;
import model.WatchlistAction;
import utils.CustomStack;
import utils.ValidationException;

import java.util.List;

public class CustomerController {
    private UserController userController;
    private MovieController movieController;

    // Undo/Redo Watchlist — lưu RAM theo session, KHÔNG ghi xuống file
    private CustomStack<WatchlistAction> undoStack;
    private CustomStack<WatchlistAction> redoStack;

    // Constructor Injection: nhận UserController (dùng CHUNG nguồn dữ liệu User)
    // và MovieController (validate FK chéo movieId)
    public CustomerController(UserController userController, MovieController movieController) {
        this.userController = userController;
        this.movieController = movieController;
        this.undoStack = new CustomStack<>();
        this.redoStack = new CustomStack<>();
    }

    // ===================== WATCHLIST =====================

    /**
     * Thêm phim vào danh sách chờ xem.
     * Validate: Customer tồn tại + active, Movie tồn tại + active, chưa có trong watchlist.
     */
    public boolean addToWatchlist(String customerId, String movieId) throws ValidationException {
        Customer customer = userController.findActiveCustomerReferenceForController(customerId);
        String canonicalId = resolveActiveMovieId(movieId);

        List<String> watchlist = customer.getWatchlist(); // Lấy bản sao
        if (watchlist.contains(canonicalId)) {
            throw new ValidationException("Phim '" + canonicalId + "' đã có trong danh sách chờ xem!");
        }
        watchlist.add(canonicalId);
        customer.setWatchlist(watchlist); // Gán lại bản sao đã sửa
        boolean saved = userController.persistUserChanges();

        // Ghi nhận hành động vào Undo Stack + xóa Redo Stack (hành động mới phá chuỗi redo)
        if (saved) {
            undoStack.push(new WatchlistAction(WatchlistAction.ActionType.ADD, canonicalId));
            redoStack.clear();
        }
        return saved;
    }

    /**
     * Xóa phim khỏi danh sách chờ xem.
     * Không cần check movie active — phim đã bị gỡ vẫn được phép xóa khỏi watchlist.
     */
    public boolean removeFromWatchlist(String customerId, String movieId) throws ValidationException {
        Customer customer = userController.findActiveCustomerReferenceForController(customerId);
        String canonicalId = resolveMovieId(movieId);

        List<String> watchlist = customer.getWatchlist();
        if (!watchlist.remove(canonicalId)) {
            throw new ValidationException("Phim '" + canonicalId + "' không có trong danh sách chờ xem!");
        }
        customer.setWatchlist(watchlist);
        boolean saved = userController.persistUserChanges();

        // Ghi nhận hành động vào Undo Stack + xóa Redo Stack
        if (saved) {
            undoStack.push(new WatchlistAction(WatchlistAction.ActionType.REMOVE, canonicalId));
            redoStack.clear();
        }
        return saved;
    }

    // ===================== FAVOURITES =====================

    /**
     * Thêm phim vào danh sách yêu thích.
     * Khi thành công, gọi movieController.incrementFavouritesCount() để tăng tổng lượt thích.
     */
    public boolean addToFavourites(String customerId, String movieId) throws ValidationException {
        Customer customer = userController.findActiveCustomerReferenceForController(customerId);
        String canonicalId = resolveActiveMovieId(movieId);

        List<String> favourites = customer.getFavouriteList();
        if (favourites.contains(canonicalId)) {
            throw new ValidationException("Phim '" + canonicalId + "' đã có trong danh sách yêu thích!");
        }
        favourites.add(canonicalId);
        customer.setFavouriteList(favourites);

        // Lưu ý: Nếu increment thất bại (hiếm khi xảy ra vì movie đã validate active ở trên),
        // Customer vẫn được cập nhật — chấp nhận được vì CLI đơn luồng, không có race condition.
        movieController.incrementFavouritesCount(canonicalId);

        return userController.persistUserChanges();
    }

    /**
     * Xóa phim khỏi danh sách yêu thích.
     * Giảm favouritesCount trên Movie (Phương án A — phản ánh số lượng đang active yêu thích).
     */
    public boolean removeFromFavourites(String customerId, String movieId) throws ValidationException {
        Customer customer = userController.findActiveCustomerReferenceForController(customerId);
        String canonicalId = resolveMovieId(movieId);

        List<String> favourites = customer.getFavouriteList();
        if (!favourites.remove(canonicalId)) {
            throw new ValidationException("Phim '" + canonicalId + "' không có trong danh sách yêu thích!");
        }
        customer.setFavouriteList(favourites);

        // Giảm số lượt yêu thích đang active trên Movie (Phương án A - Current State)
        movieController.decrementFavouritesCount(canonicalId);

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
        String canonicalId = resolveActiveMovieId(movieId);

        List<String> history = customer.getWatchHistory();
        history.add(canonicalId); // Cho phép trùng — xem lại nhiều lần
        customer.setWatchHistory(history);

        // Lưu ý: Nếu increment thất bại (hiếm khi xảy ra vì movie đã validate active ở trên),
        // Customer vẫn được cập nhật — chấp nhận được vì CLI đơn luồng, không có race condition.
        // Đã cân nhắc và quyết định không rollback (xem quyết định Nhóm 2).
        movieController.incrementViews(canonicalId);

        return userController.persistUserChanges();
    }

    // ===================== UNDO / REDO WATCHLIST =====================

    /**
     * Undo hành động Watchlist gần nhất.
     * Nếu hành động gần nhất là ADD → thực hiện REMOVE (ngược lại).
     * Nếu hành động gần nhất là REMOVE → thực hiện ADD.
     * Hành động bị undo được đẩy sang redoStack.
     */
    public boolean undoWatchlist(String customerId) throws ValidationException {
        if (undoStack.isEmpty()) {
            throw new ValidationException("Không có hành động nào để Undo!");
        }

        WatchlistAction lastAction = undoStack.pop();
        String movieId = lastAction.getMovieId();
        Customer customer = userController.findActiveCustomerReferenceForController(customerId);
        List<String> watchlist = customer.getWatchlist();

        if (lastAction.getActionType() == WatchlistAction.ActionType.ADD) {
            // Undo ADD = thực hiện REMOVE
            watchlist.remove(movieId);
        } else {
            // Undo REMOVE = thực hiện ADD (chỉ thêm lại nếu chưa có)
            if (!watchlist.contains(movieId)) {
                watchlist.add(movieId);
            }
        }

        customer.setWatchlist(watchlist);
        boolean saved = userController.persistUserChanges();

        // Đẩy hành động vừa undo sang Redo Stack (KHÔNG clear redo)
        if (saved) {
            redoStack.push(lastAction);
        }
        return saved;
    }

    /**
     * Redo hành động Watchlist vừa bị Undo.
     * Nếu hành động bị undo là ADD → thực hiện lại ADD.
     * Nếu hành động bị undo là REMOVE → thực hiện lại REMOVE.
     * Hành động được redo sẽ đẩy ngược lại undoStack.
     */
    public boolean redoWatchlist(String customerId) throws ValidationException {
        if (redoStack.isEmpty()) {
            throw new ValidationException("Không có hành động nào để Redo!");
        }

        WatchlistAction redoAction = redoStack.pop();
        String movieId = redoAction.getMovieId();
        Customer customer = userController.findActiveCustomerReferenceForController(customerId);
        List<String> watchlist = customer.getWatchlist();

        if (redoAction.getActionType() == WatchlistAction.ActionType.ADD) {
            // Redo ADD = thực hiện ADD lại
            if (!watchlist.contains(movieId)) {
                watchlist.add(movieId);
            }
        } else {
            // Redo REMOVE = thực hiện REMOVE lại
            watchlist.remove(movieId);
        }

        customer.setWatchlist(watchlist);
        boolean saved = userController.persistUserChanges();

        // Đẩy hành động vừa redo ngược lại Undo Stack
        if (saved) {
            undoStack.push(redoAction);
        }
        return saved;
    }

    /**
     * Kiểm tra có thể Undo hay không (stack không rỗng).
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * Kiểm tra có thể Redo hay không (stack không rỗng).
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    // ===================== READ-ONLY (cho View) =====================

    /**
     * Lấy danh sách Watchlist của Customer (bản sao).
     */
    public java.util.List<String> getWatchlist(String customerId) throws ValidationException {
        Customer customer = userController.findActiveCustomerReferenceForController(customerId);
        return customer.getWatchlist(); // Đã trả bản sao từ Customer.getWatchlist()
    }

    /**
     * Lấy danh sách Favourite của Customer (bản sao).
     */
    public java.util.List<String> getFavouriteList(String customerId) throws ValidationException {
        Customer customer = userController.findActiveCustomerReferenceForController(customerId);
        return customer.getFavouriteList();
    }

    /**
     * Lấy danh sách Watch History của Customer (bản sao).
     */
    public java.util.List<String> getWatchHistory(String customerId) throws ValidationException {
        Customer customer = userController.findActiveCustomerReferenceForController(customerId);
        return customer.getWatchHistory();
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

    /**
     * Validate phim tồn tại + active, rồi trả về ID chuẩn của phim.
     * Chống lệch case-sensitive khi lưu vào Watchlist/Favourites/History.
     */
    private String resolveActiveMovieId(String movieId) throws ValidationException {
        validateActiveMovieExists(movieId);
        return movieController.findById(movieId).getId();
    }

    /**
     * Trả về ID chuẩn của phim nếu phim tồn tại (kể cả inactive).
     * Nếu không tồn tại thì trả nguyên input để phép remove báo lỗi bình thường.
     */
    private String resolveMovieId(String movieId) {
        Movie movie = movieController.findById(movieId);
        return (movie != null) ? movie.getId() : movieId;
    }
}
