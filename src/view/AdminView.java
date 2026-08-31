package view;

import controller.CategoryController;
import controller.MovieController;
import controller.UserController;
import model.Category;
import model.Movie;
import model.Statistics;
import model.User;
import utils.InputValidator;
import utils.TextUI;
import utils.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Menu CLI dành cho Admin — quản lý Category, Movie, User, và xem thống kê.
 */
public class AdminView {
    private Scanner scanner;
    private CategoryController categoryController;
    private MovieController movieController;
    private UserController userController;

    public AdminView(Scanner scanner, CategoryController categoryController,
                     MovieController movieController, UserController userController) {
        this.scanner = scanner;
        this.categoryController = categoryController;
        this.movieController = movieController;
        this.userController = userController;
    }

    public void showMenu() {
        int choice;
        do {
            System.out.println("\n" + TextUI.menuBox("ADMIN DASHBOARD", new String[]{
                    "  1. Quản lý Thể loại (Category)",
                    "  2. Quản lý Phim (Movie)",
                    "  3. Xem danh sách Người dùng",
                    "  4. Xem Thống kê hệ thống",
                    "  5. Xem Auto Ranking (Xếp hạng)",
                    "  6. Xem Trending Categories",
                    "  7. Khôi phục dữ liệu",
                    "  0. Đăng xuất"
            }));
            choice = InputValidator.readInt(scanner, "Chọn chức năng: ", 0, 7);

            switch (choice) {
                case 1: categoryMenu(); break;
                case 2: movieMenu(); break;
                case 3: userMenu(); break;
                case 4: viewStatistics(); break;
                case 5: viewAutoRanking(); break;
                case 6: viewTrendingCategories(); break;
                case 7: restoreMenu(); break;
                case 0: System.out.println("Đã đăng xuất."); break;
            }
        } while (choice != 0);
    }

    // ===================== CATEGORY MENU =====================

    private void categoryMenu() {
        int choice;
        do {
            System.out.println("\n" + TextUI.header("QUẢN LÝ THỂ LOẠI"));
            System.out.println("1. Thêm thể loại");
            System.out.println("2. Sửa thể loại");
            System.out.println("3. Xóa thể loại (Soft Delete)");
            System.out.println("4. Xem tất cả thể loại");
            System.out.println("0. Quay lại");
            choice = InputValidator.readInt(scanner, "Chọn: ", 0, 4);

            switch (choice) {
                case 1: addCategory(); break;
                case 2: updateCategory(); break;
                case 3: deleteCategory(); break;
                case 4: viewAllCategories(); break;
            }
        } while (choice != 0);
    }

    private void addCategory() {
        try {
            String id = InputValidator.readString(scanner, "Nhập mã thể loại: ");
            String name = InputValidator.readString(scanner, "Nhập tên thể loại: ");
            System.out.print("Nhập mô tả (Enter để bỏ qua): ");
            String desc = scanner.nextLine().trim();
            if (desc.isEmpty()) desc = null;

            if (categoryController.addCategory(id, name, desc)) {
                System.out.println("Thêm thể loại thành công!");
            } else {
                System.out.println("Lỗi ghi file!");
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateCategory() {
        try {
            String id = InputValidator.readString(scanner, "Nhập mã thể loại cần sửa: ");
            Category existing = categoryController.findById(id);
            if (existing == null) {
                System.out.println("Không tìm thấy thể loại!");
                return;
            }
            System.out.println("Thông tin hiện tại: " + existing);
            String name = InputValidator.readString(scanner, "Nhập tên mới: ");
            System.out.print("Nhập mô tả mới (Enter để giữ nguyên): ");
            String desc = scanner.nextLine().trim();
            if (desc.isEmpty()) desc = existing.getDescription();

            if (categoryController.updateCategory(id, name, desc)) {
                System.out.println("Cập nhật thành công!");
            } else {
                System.out.println("Lỗi ghi file!");
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteCategory() {
        try {
            String id = InputValidator.readString(scanner, "Nhập mã thể loại cần xóa: ");
            if (categoryController.deleteCategory(id)) {
                System.out.println("Đã xóa (Soft Delete) thành công!");
            } else {
                System.out.println("Lỗi ghi file!");
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    private void viewAllCategories() {
        List<Category> list = categoryController.getAllCategories();
        if (list.isEmpty()) {
            System.out.println("(Chưa có thể loại nào)");
            return;
        }
        System.out.println("\n" + TextUI.header("DANH SÁCH TẤT CẢ THỂ LOẠI"));
        for (Category cat : list) {
            System.out.println("  " + cat);
        }
    }

    // ===================== MOVIE MENU =====================

    private void movieMenu() {
        int choice;
        do {
            System.out.println("\n" + TextUI.header("QUẢN LÝ PHIM"));
            System.out.println("1. Thêm phim");
            System.out.println("2. Sửa phim");
            System.out.println("3. Xóa phim (Soft Delete)");
            System.out.println("4. Xem tất cả phim");
            System.out.println("5. Tìm kiếm phim");
            System.out.println("6. Sắp xếp phim");
            System.out.println("7. Lọc nâng cao");
            System.out.println("0. Quay lại");
            choice = InputValidator.readInt(scanner, "Chọn: ", 0, 7);

            switch (choice) {
                case 1: addMovie(); break;
                case 2: updateMovie(); break;
                case 3: deleteMovie(); break;
                case 4: viewAllMovies(); break;
                case 5: searchMovieMenu(); break;
                case 6: sortMovieMenu(); break;
                case 7: advancedFilterMenu(); break;
            }
        } while (choice != 0);
    }

    private void addMovie() {
        try {
            String id = InputValidator.readString(scanner, "Nhập mã phim: ");
            String title = InputValidator.readString(scanner, "Nhập tên phim: ");
            String categoryId = TextUI.selectCategoryByNumber(categoryController.getActiveCategories(), scanner);
            if (categoryId == null) {
                System.out.println("Đã hủy thêm phim.");
                return;
            }
            String director = InputValidator.readString(scanner, "Nhập đạo diễn: ");
            String actors = InputValidator.readString(scanner, "Nhập diễn viên: ");
            int year = InputValidator.readInt(scanner, "Nhập năm phát hành: ", 1888, java.time.Year.now().getValue());

            if (movieController.addMovie(id, title, categoryId, director, actors, year)) {
                System.out.println("Thêm phim thành công!");
            } else {
                System.out.println("Lỗi ghi file!");
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateMovie() {
        try {
            String id = InputValidator.readString(scanner, "Nhập mã phim cần sửa: ");
            Movie existing = movieController.findById(id);
            if (existing == null) {
                System.out.println("Không tìm thấy phim!");
                return;
            }
            System.out.println("Thông tin hiện tại: " + existing);
            String title = InputValidator.readString(scanner, "Nhập tên mới: ");

            Category currentCategory = categoryController.findById(existing.getCategoryId());
            String currentCategoryName = (currentCategory != null) ? currentCategory.getName() : existing.getCategoryId();
            System.out.println("Thể loại hiện tại: " + currentCategoryName);

            String categoryId = TextUI.selectCategoryByNumber(
                    categoryController.getActiveCategories(), scanner,
                    "Giữ nguyên: " + currentCategoryName);
            if (categoryId == null) {
                categoryId = existing.getCategoryId();
            }

            String director = InputValidator.readString(scanner, "Nhập đạo diễn mới: ");
            String actors = InputValidator.readString(scanner, "Nhập diễn viên mới: ");
            int year = InputValidator.readInt(scanner, "Nhập năm phát hành mới: ", 1888, java.time.Year.now().getValue());

            if (movieController.updateMovie(id, title, categoryId, director, actors, year)) {
                System.out.println("Cập nhật phim thành công!");
            } else {
                System.out.println("Lỗi ghi file!");
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteMovie() {
        try {
            String id = InputValidator.readString(scanner, "Nhập mã phim cần xóa: ");
            if (movieController.deleteMovie(id)) {
                System.out.println("Đã xóa phim (Soft Delete) thành công!");
            } else {
                System.out.println("Lỗi ghi file!");
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    private void viewAllMovies() {
        List<Movie> list = movieController.getAllMovies();
        if (list.isEmpty()) {
            System.out.println("(Chưa có phim nào)");
            return;
        }
        System.out.println("\n" + TextUI.header("DANH SÁCH TẤT CẢ PHIM"));
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                System.out.println();
            }
            System.out.println("  " + formatMovie(list.get(i)));
        }
    }

    // ===================== RESTORE =====================

    private void restoreMenu() {
        int choice;
        do {
            System.out.println("\n" + TextUI.header("KHÔI PHỤC DỮ LIỆU"));
            System.out.println("1. Khôi phục Thể loại");
            System.out.println("2. Khôi phục Phim");
            System.out.println("0. Quay lại");
            choice = InputValidator.readInt(scanner, "Chọn: ", 0, 2);
            switch (choice) {
                case 1: restoreCategoryMenu(); break;
                case 2: restoreMovieMenu(); break;
            }
        } while (choice != 0);
    }

    private void restoreCategoryMenu() {
        List<Category> inactive = new ArrayList<>();
        for (Category c : categoryController.getAllCategories()) {
            if (!c.isActive()) {
                inactive.add(c);
            }
        }
        if (inactive.isEmpty()) {
            System.out.println("(Không có thể loại nào cần khôi phục)");
            return;
        }
        System.out.println("\n" + TextUI.header("DANH SÁCH THỂ LOẠI ĐÃ XÓA"));
        for (int i = 0; i < inactive.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + inactive.get(i).getId() + " - " + inactive.get(i).getName());
        }
        System.out.println("  0. Quay lại");
        int choice = InputValidator.readInt(scanner, "Chọn thể loại cần khôi phục: ", 0, inactive.size());
        if (choice == 0) return;
        String id = inactive.get(choice - 1).getId();
        try {
            if (categoryController.restoreCategory(id)) {
                System.out.println("Đã khôi phục thể loại '" + id + "' thành công!");
            } else {
                System.out.println("Lỗi ghi file!");
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    private void restoreMovieMenu() {
        List<Movie> inactive = new ArrayList<>();
        for (Movie m : movieController.getAllMovies()) {
            if (!m.isActive()) {
                inactive.add(m);
            }
        }
        if (inactive.isEmpty()) {
            System.out.println("(Không có phim nào cần khôi phục)");
            return;
        }
        System.out.println("\n" + TextUI.header("DANH SÁCH PHIM ĐÃ XÓA"));
        for (int i = 0; i < inactive.size(); i++) {
            if (i > 0) {
                System.out.println();
            }
            System.out.println("  " + (i + 1) + ". " + formatMovie(inactive.get(i)));
        }
        System.out.println("\n  0. Quay lại");
        int choice = InputValidator.readInt(scanner, "Chọn phim cần khôi phục: ", 0, inactive.size());
        if (choice == 0) return;
        String id = inactive.get(choice - 1).getId();
        try {
            if (movieController.restoreMovie(id)) {
                System.out.println("Đã khôi phục phim '" + id + "' thành công!");
            } else {
                System.out.println("Lỗi ghi file!");
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    // ===================== SEARCH MOVIES =====================

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
                String title = InputValidator.sanitizeSearchInput(
                        InputValidator.readString(scanner, "Nhập từ khóa tên phim: "));
                results = movieController.searchMoviesByTitle(title);
                break;
            case 2:
                String actor = InputValidator.sanitizeSearchInput(
                        InputValidator.readString(scanner, "Nhập từ khóa diễn viên: "));
                results = movieController.searchMoviesByActor(actor);
                break;
            case 3:
                String director = InputValidator.sanitizeSearchInput(
                        InputValidator.readString(scanner, "Nhập từ khóa đạo diễn: "));
                results = movieController.searchMoviesByDirector(director);
                break;
            case 4:
                String catId = TextUI.selectCategoryByNumber(categoryController.getActiveCategories(), scanner);
                if (catId == null) return;
                results = movieController.searchMoviesByCategory(catId);
                break;
            default:
                return;
        }
        printMovieResults(results);
    }

    // ===================== SORT MOVIES =====================

    private void sortMovieMenu() {
        System.out.println("\n" + TextUI.header("SẮP XẾP PHIM"));
        System.out.println("1. Theo tên phim");
        System.out.println("2. Theo đánh giá (Rating)");
        System.out.println("3. Theo năm phát hành");
        System.out.println("4. Theo độ phổ biến (Popularity)");
        int choice = InputValidator.readInt(scanner, "Chọn tiêu chí: ", 1, 4);

        System.out.println("1. Tăng dần");
        System.out.println("2. Giảm dần");
        int dir = InputValidator.readInt(scanner, "Chọn thứ tự: ", 1, 2);
        boolean ascending = (dir == 1);

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

    // ===================== ADVANCED FILTER =====================

    private void advancedFilterMenu() {
        System.out.println("\n" + TextUI.header("LỌC NÂNG CAO"));

        String categoryId = TextUI.selectCategoryByNumber(categoryController.getActiveCategories(), scanner, "Bỏ qua (không lọc theo thể loại)");

        System.out.print("Năm phát hành từ (Enter để bỏ qua): ");
        String minYearStr = scanner.nextLine().trim();
        Integer minYear = null;
        if (!minYearStr.isEmpty()) {
            try { minYear = Integer.parseInt(minYearStr); }
            catch (NumberFormatException e) { System.out.println("(Bỏ qua — không phải số)"); }
        }

        System.out.print("Năm phát hành đến (Enter để bỏ qua): ");
        String maxYearStr = scanner.nextLine().trim();
        Integer maxYear = null;
        if (!maxYearStr.isEmpty()) {
            try { maxYear = Integer.parseInt(maxYearStr); }
            catch (NumberFormatException e) { System.out.println("(Bỏ qua — không phải số)"); }
        }

        System.out.print("Rating tối thiểu (Enter để bỏ qua): ");
        String minRatingStr = scanner.nextLine().trim();
        Double minRating = null;
        if (!minRatingStr.isEmpty()) {
            try { minRating = Double.parseDouble(minRatingStr); }
            catch (NumberFormatException e) { System.out.println("(Bỏ qua — không phải số)"); }
        }

        System.out.print("Đạo diễn từ khóa (Enter để bỏ qua): ");
        String director = InputValidator.sanitizeSearchInput(scanner.nextLine());
        if (director == null || director.isEmpty()) director = null;

        System.out.print("Diễn viên từ khóa (Enter để bỏ qua): ");
        String actor = InputValidator.sanitizeSearchInput(scanner.nextLine());
        if (actor == null || actor.isEmpty()) actor = null;

        List<Movie> results = movieController.advancedFilter(categoryId, minYear, maxYear, minRating, director, actor);
        printMovieResults(results);
    }

    // ===================== USER MANAGEMENT =====================

    private void userMenu() {
        List<User> users = userController.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("(Chưa có người dùng nào)");
            return;
        }
        System.out.println("\n" + TextUI.header("DANH SÁCH NGƯỜI DÙNG"));
        for (User user : users) {
            System.out.println("  " + user);
        }
    }

    // ===================== STATISTICS =====================

    private void viewStatistics() {
        Statistics stats = movieController.getViewingStatistics();
        System.out.println("\n" + stats);
    }

    private void viewAutoRanking() {
        List<Movie> ranked = movieController.getAutoRanking();
        if (ranked.isEmpty()) {
            System.out.println("(Chưa có phim nào để xếp hạng)");
            return;
        }
        System.out.println("\n" + TextUI.header("AUTO RANKING (Xếp hạng tự động)"));
        System.out.println("Công thức: score = rating×10 + views×0.01 + favourites×0.5");
        for (int i = 0; i < ranked.size(); i++) {
            Movie m = ranked.get(i);
            double score = m.getRating() * 10 + m.getViews() * 0.01 + m.getFavouritesCount() * 0.5;
            System.out.printf("  #%d. %s — Score: %.2f (Rating: %.1f | Views: %d | Fav: %d)\n",
                    i + 1, m.getTitle(), score, m.getRating(), m.getViews(), m.getFavouritesCount());
        }
    }

    private void viewTrendingCategories() {
        List<Category> trending = categoryController.getTrendingCategories();
        if (trending.isEmpty()) {
            System.out.println("(Chưa có thể loại nào)");
            return;
        }
        System.out.println("\n" + TextUI.header("TRENDING CATEGORIES (Thể loại thịnh hành)"));
        for (int i = 0; i < trending.size(); i++) {
            Category cat = trending.get(i);
            long views = movieController.getTotalViewsByCategory(cat.getId());
            System.out.printf("  #%d. %s — Tổng lượt xem: %d\n", i + 1, cat.getName(), views);
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
            System.out.println("  " + formatMovie(movies.get(i)));
        }
    }
}
