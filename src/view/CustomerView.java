package view;

import controller.CategoryController;
import controller.CustomerController;
import controller.MovieController;
import java.util.List;
import java.util.Scanner;
import model.Category;
import model.Movie;
import utils.InputValidator;
import utils.TextUI;
import utils.ValidationException;

/**
 * Menu CLI dành cho Customer — duyệt phim, quản lý Watchlist/Favourite/History,
 * Undo/Redo Watchlist.
 */
public class CustomerView {
    private Scanner scanner;
    private MovieController movieController;
    private CustomerController customerController;
    private CategoryController categoryController;
    private String customerId; // ID của Customer đang đăng nhập

    public CustomerView(Scanner scanner, MovieController movieController,
                        CustomerController customerController, CategoryController categoryController,
                        String customerId) {
        this.scanner = scanner;
        this.movieController = movieController;
        this.customerController = customerController;
        this.categoryController = categoryController;
        this.customerId = customerId;
    }

    public void showMenu() {
        int choice;
        do {
            System.out.println("\n" + TextUI.menuBox("CUSTOMER DASHBOARD", new String[]{
                    "  1. Khám phá phim",
                    "  2. Thư viện của tôi",
                    "  0. Đăng xuất"
            }));
            choice = InputValidator.readInt(scanner, "Chọn chức năng: ", 0, 2);

            switch (choice) {
                case 1: browseMenu(); break;
                case 2: libraryMenu(); break;
                case 0: System.out.println("Đã đăng xuất."); break;
            }
        } while (choice != 0);
    }

    // ===================== KHÁM PHÁ PHIM =====================

    private void browseMenu() {
        int choice;
        do {
            System.out.println("\n" + TextUI.menuBox("KHÁM PHÁ PHIM", new String[]{
                    "  1. Duyệt tất cả phim",
                    "  2. Tìm kiếm phim",
                    "  3. Sắp xếp phim",
                    "  4. Duyệt theo thể loại",
                    "  5. Bảng xếp hạng phim",
                    "  0. Quay lại"
            }));
            choice = InputValidator.readInt(scanner, "Chọn chức năng: ", 0, 5);

            switch (choice) {
                case 1: browseMovies(); break;
                case 2: searchMovieMenu(); break;
                case 3: sortMovieMenu(); break;
                case 4: browseByCategory(); break;
                case 5: viewAutoRanking(); break;
            }
        } while (choice != 0);
    }

    // ===================== THƯ VIỆN CỦA TÔI =====================

    private void libraryMenu() {
        int choice;
        do {
            System.out.println("\n" + TextUI.menuBox("THƯ VIỆN CỦA TÔI", new String[]{
                    "  1. Danh sách chờ xem ",
                    "  2. Danh sách yêu thích ",
                    "  3. Lịch sử xem ",
                    "  0. Quay lại "
            }));
            choice = InputValidator.readInt(scanner, "Chọn chức năng: ", 0, 3);

            switch (choice) {
                case 1: watchlistMenu(); break;
                case 2: favouriteMenu(); break;
                case 3: watchHistoryMenu(); break;
            }
        } while (choice != 0);
    }

    // ===================== BROWSE MOVIES =====================

    private void browseMovies() {
        List<Movie> activeMovies = movieController.getActiveMovies();
        if (activeMovies.isEmpty()) {
            System.out.println("(Chưa có phim nào)");
            return;
        }
        System.out.println("\n" + TextUI.header("DANH SÁCH PHIM ĐANG CHIẾU"));
        for (int i = 0; i < activeMovies.size(); i++) {
            if (i > 0) {
                System.out.println();
            }
            System.out.printf("  %d. %s%n", i + 1, formatMovie(activeMovies.get(i)));
        }
        promptViewDetail();
    }

    // ===================== VIEW MOVIE DETAIL =====================

    private void promptViewDetail() {
        System.out.print("Nhập mã phim để xem chi tiết (Enter để bỏ qua): ");
        String id = scanner.nextLine().trim();
        if (!id.isEmpty()) {
            showMovieDetail(id);
        }
    }

    private void showMovieDetail(String id) {
        Movie movie = movieController.findById(id);
        if (movie == null) {
            System.out.println("Không tìm thấy phim!");
            return;
        }
        if (!movie.isActive()) {
            System.out.println("Phim này đã bị gỡ khỏi hệ thống.");
            return;
        }
        System.out.println("\n" + TextUI.header("CHI TIẾT PHIM"));
        System.out.println("  Mã phim    : " + movie.getId());
        System.out.println("  Tên phim   : " + movie.getTitle());
        Category cat = categoryController.findById(movie.getCategoryId());
        String categoryName = (cat != null) ? cat.getName() : movie.getCategoryId();
        System.out.println("  Thể loại   : " + categoryName);
        System.out.println("  Đạo diễn   : " + movie.getDirector());
        System.out.println("  Diễn viên  : " + movie.getActors());
        System.out.println("  Năm        : " + movie.getReleaseYear());
        System.out.printf("  Rating     : %.1f\n", movie.getRating());
        System.out.println("  Lượt xem   : " + movie.getViews());
        System.out.println("  Yêu thích  : " + movie.getFavouritesCount());
    }

    // ===================== BROWSE BY CATEGORY =====================

    private void browseByCategory() {
        List<Category> categories = categoryController.getActiveCategories();
        if (categories.isEmpty()) {
            System.out.println("(Chưa có thể loại nào)");
            return;
        }
        System.out.println("\n" + TextUI.header("CÁC THỂ LOẠI"));
        for (Category cat : categories) {
            System.out.println("  [" + cat.getId() + "] " + cat.getName());
        }
        String catId = InputValidator.readString(scanner, "Nhập mã thể loại để xem phim: ");
        List<Movie> movies = movieController.searchMoviesByCategory(catId);
        printMovieResults(movies);
    }

    // ===================== SEARCH =====================

    private void searchMovieMenu() {
        System.out.println("\n" + TextUI.header("TÌM KIẾM PHIM"));
        System.out.println("1. Theo tên phim");
        System.out.println("2. Theo diễn viên");
        System.out.println("3. Theo đạo diễn");
        System.out.println("4. Theo thể loại");
        int choice = InputValidator.readInt(scanner, "Chọn tiêu chí: ", 1, 4);

        List<Movie> results;
        switch (choice) {
            case 1:
                results = movieController.searchMoviesByTitle(
                        InputValidator.readString(scanner, "Nhập từ khóa tên phim: "));
                break;
            case 2:
                results = movieController.searchMoviesByActor(
                        InputValidator.readString(scanner, "Nhập từ khóa diễn viên: "));
                break;
            case 3:
                results = movieController.searchMoviesByDirector(
                        InputValidator.readString(scanner, "Nhập từ khóa đạo diễn: "));
                break;
            case 4:
                results = movieController.searchMoviesByCategory(
                        InputValidator.readString(scanner, "Nhập mã thể loại: "));
                break;
            default: return;
        }
        printMovieResults(results);
    }

    // ===================== SORT =====================

    private void sortMovieMenu() {
        System.out.println("\n" + TextUI.header("SẮP XẾP PHIM"));
        System.out.println("1. Theo tên phim");
        System.out.println("2. Theo đánh giá (Rating)");
        System.out.println("3. Theo năm phát hành");
        System.out.println("4. Theo độ phổ biến");
        int choice = InputValidator.readInt(scanner, "Chọn tiêu chí: ", 1, 4);

        System.out.println("1. Tăng dần");
        System.out.println("2. Giảm dần");
        boolean ascending = (InputValidator.readInt(scanner, "Chọn thứ tự: ", 1, 2) == 1);

        List<Movie> results;
        switch (choice) {
            case 1: results = movieController.sortMoviesByTitle(ascending); break;
            case 2: results = movieController.sortMoviesByRating(ascending); break;
            case 3: results = movieController.sortMoviesByReleaseYear(ascending); break;
            case 4: results = movieController.sortMoviesByPopularity(ascending); break;
            default: return;
        }
        printMovieResults(results);
    }

    // ===================== WATCHLIST =====================

    private void watchlistMenu() {
        int choice;
        do {
            System.out.println("\n" + TextUI.header("DANH SÁCH CHỜ XEM (WATCHLIST)"));
            System.out.println("1. Thêm phim vào Watchlist");
            System.out.println("2. Xóa phim khỏi Watchlist");
            System.out.println("3. Xem Watchlist hiện tại");
            String undoLabel = customerController.canUndo() ? "(Có thể Undo)" : "(Trống)";
            String redoLabel = customerController.canRedo() ? "(Có thể Redo)" : "(Trống)";
            System.out.println("4. Undo " + undoLabel);
            System.out.println("5. Redo " + redoLabel);
            System.out.println("0. Quay lại");
            choice = InputValidator.readInt(scanner, "Chọn: ", 0, 5);

            switch (choice) {
                case 1: addToWatchlist(); break;
                case 2: removeFromWatchlist(); break;
                case 3: viewWatchlist(); break;
                case 4: undoWatchlist(); break;
                case 5: redoWatchlist(); break;
            }
        } while (choice != 0);
    }

    private void addToWatchlist() {
        try {
            String movieId = InputValidator.readString(scanner, "Nhập mã phim: ");
            if (customerController.addToWatchlist(customerId, movieId)) {
                System.out.println("Đã thêm phim '" + movieId + "' vào Watchlist!");
            } else {
                System.out.println("Lỗi ghi file!");
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    private void removeFromWatchlist() {
        try {
            String movieId = InputValidator.readString(scanner, "Nhập mã phim cần xóa: ");
            if (customerController.removeFromWatchlist(customerId, movieId)) {
                System.out.println("Đã xóa phim '" + movieId + "' khỏi Watchlist!");
            } else {
                System.out.println("Lỗi ghi file!");
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    private void viewWatchlist() {
        try {
            List<String> watchlist = customerController.getWatchlist(customerId);
            if (watchlist.isEmpty()) {
                System.out.println("  (Watchlist trống)");
                return;
            }
            System.out.println("\n" + TextUI.header("WATCHLIST CỦA BẠN"));
            for (int i = 0; i < watchlist.size(); i++) {
                String movieId = watchlist.get(i);
                Movie movie = movieController.findById(movieId);
                String title = (movie != null) ? movie.getTitle() : "(Phim không tồn tại)";
                System.out.printf("  %d. [%s] %s\n", i + 1, movieId, title);
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    private void undoWatchlist() {
        try {
            if (customerController.undoWatchlist(customerId)) {
                System.out.println("Undo thành công!");
            } else {
                System.out.println("Lỗi ghi file!");
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    private void redoWatchlist() {
        try {
            if (customerController.redoWatchlist(customerId)) {
                System.out.println("Redo thành công!");
            } else {
                System.out.println("Lỗi ghi file!");
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    // ===================== FAVOURITES =====================

    private void favouriteMenu() {
        int choice;
        do {
            System.out.println("\n" + TextUI.header("DANH SÁCH YÊU THÍCH (FAVOURITES)"));
            System.out.println("1. Thêm phim vào Yêu thích");
            System.out.println("2. Xóa phim khỏi Yêu thích");
            System.out.println("3. Xem danh sách Yêu thích");
            System.out.println("0. Quay lại");
            choice = InputValidator.readInt(scanner, "Chọn: ", 0, 3);

            switch (choice) {
                case 1: addToFavourites(); break;
                case 2: removeFromFavourites(); break;
                case 3: viewFavouriteList(); break;
            }
        } while (choice != 0);
    }

    private void addToFavourites() {
        try {
            String movieId = InputValidator.readString(scanner, "Nhập mã phim: ");
            if (customerController.addToFavourites(customerId, movieId)) {
                System.out.println("Đã thêm phim '" + movieId + "' vào Yêu thích!");
            } else {
                System.out.println("Lỗi ghi file!");
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    private void removeFromFavourites() {
        try {
            String movieId = InputValidator.readString(scanner, "Nhập mã phim cần xóa: ");
            if (customerController.removeFromFavourites(customerId, movieId)) {
                System.out.println("Đã xóa phim '" + movieId + "' khỏi Yêu thích!");
            } else {
                System.out.println("Lỗi ghi file!");
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    private void viewFavouriteList() {
        try {
            List<String> favourites = customerController.getFavouriteList(customerId);
            if (favourites.isEmpty()) {
                System.out.println("  (Danh sách yêu thích trống)");
                return;
            }
            System.out.println("\n" + TextUI.header("DANH SÁCH YÊU THÍCH CỦA BẠN"));
            for (int i = 0; i < favourites.size(); i++) {
                String movieId = favourites.get(i);
                Movie movie = movieController.findById(movieId);
                String title = (movie != null) ? movie.getTitle() : "(Phim không tồn tại)";
                System.out.printf("  %d. [%s] %s\n", i + 1, movieId, title);
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    // ===================== WATCH HISTORY =====================

    private void watchHistoryMenu() {
        int choice;
        do {
            System.out.println("\n" + TextUI.header("LỊCH SỬ XEM"));
            System.out.println("1. Xem phim (Thêm vào lịch sử)");
            System.out.println("2. Xem lịch sử đã xem");
            System.out.println("0. Quay lại");
            choice = InputValidator.readInt(scanner, "Chọn: ", 0, 2);

            switch (choice) {
                case 1: addToWatchHistory(); break;
                case 2: viewWatchHistory(); break;
            }
        } while (choice != 0);
    }

    private void addToWatchHistory() {
        try {
            String movieId = InputValidator.readString(scanner, "Nhập mã phim muốn xem: ");
            if (customerController.addToWatchHistory(customerId, movieId)) {
                System.out.println("Đang phát phim '" + movieId + "'... Đã ghi nhận lịch sử!");
            } else {
                System.out.println("Lỗi ghi file!");
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    private void viewWatchHistory() {
        try {
            List<String> history = customerController.getWatchHistory(customerId);
            if (history.isEmpty()) {
                System.out.println("  (Chưa xem phim nào)");
                return;
            }
            System.out.println("\n" + TextUI.header("LỊCH SỬ XEM CỦA BẠN"));
            for (int i = 0; i < history.size(); i++) {
                String movieId = history.get(i);
                Movie movie = movieController.findById(movieId);
                String title = (movie != null) ? movie.getTitle() : "(Phim không tồn tại)";
                System.out.printf("  %d. [%s] %s\n", i + 1, movieId, title);
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    // ===================== RANKING =====================

    private void viewAutoRanking() {
        List<Movie> ranked = movieController.getAutoRanking();
        if (ranked.isEmpty()) {
            System.out.println("(Chưa có phim nào)");
            return;
        }
        System.out.println("\n" + TextUI.header("BẢNG XẾP HẠNG PHIM"));
        for (int i = 0; i < ranked.size(); i++) {
            Movie m = ranked.get(i);
            System.out.printf("  #%d. %s (%.1f | %d views | %d fav)\n",
                    i + 1, m.getTitle(), m.getRating(), m.getViews(), m.getFavouritesCount());
        }
    }

    // ===================== HELPER =====================

    private String formatMovie(Movie movie) {
        Category cat = categoryController.findById(movie.getCategoryId());
        String categoryName = (cat != null) ? cat.getName() : movie.getCategoryId();
        return movie.toString().replace(
            "Mã Thể loại: " + movie.getCategoryId(),
            "Thể loại: " + categoryName
        );
    }

    private void printMovieResults(List<Movie> movies) {
        if (movies.isEmpty()) {
            System.out.println("Không tìm thấy kết quả.");
            return;
        }
        System.out.println("Tìm thấy " + movies.size() + " phim:");
        for (int i = 0; i < movies.size(); i++) {
            if (i > 0) {
                System.out.println();
            }
            System.out.printf("  %d. %s%n", i + 1, formatMovie(movies.get(i)));
        }
        promptViewDetail();
    }
}
