import controller.CategoryController;
import controller.CustomerController;
import controller.MovieController;
import controller.UserController;
import java.io.PrintStream;
import java.util.Scanner;
import model.User;
import repository.CategoryRepository;
import repository.MovieRepository;
import repository.UserRepository;
import view.AdminView;
import view.CustomerView;
import view.LoginView;

public class Main {
    public static void main(String[] args) throws Exception {
        // Trên Windows: tự động đổi console sang UTF-8 (codepage 65001) trước khi in
        // bất kỳ text tiếng Việt nào. Cần thiết vì Java 8 không có -Dstdout.encoding,
        // và mỗi lần Debug/Run trong VS Code có thể mở console mới với codepage khác.
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            try {
                String systemRoot = System.getenv("SystemRoot");
                String chcpPath = (systemRoot != null)
                        ? systemRoot + "\\System32\\chcp.com"
                        : "chcp";
                new ProcessBuilder(chcpPath, "65001")
                        .inheritIO()
                        .start()
                        .waitFor();
            } catch (Exception e) {
                // Bỏ qua nếu thất bại — không chặn chương trình chạy tiếp
            }
        }

        // Đảm bảo output stream dùng UTF-8 bất kể console được cấu hình đúng hay chưa
        System.setOut(new PrintStream(System.out, true, "UTF-8"));

        // Khởi tạo Repositories và Controllers thông qua Dependency Injection
        CategoryController categoryController = new CategoryController(new CategoryRepository());
        MovieController movieController = new MovieController(new MovieRepository(), categoryController);

        // Liên kết ngược để CategoryController có thể check FK
        categoryController.setMovieController(movieController); 

        // UserController phải được tạo TRƯỚC CustomerController
        UserController userController = new UserController(new UserRepository());
        CustomerController customerController = new CustomerController(userController, movieController);

        // Scanner dùng chung cho toàn bộ CLI — đọc input bằng UTF-8
        Scanner scanner = new Scanner(System.in, "UTF-8");

        // Vòng lặp chính: Login → Dispatch Admin/Customer → Logout → Quay lại Login
        while (true) {
            LoginView loginView = new LoginView(scanner, userController);
            User loggedInUser = loginView.showMenu();

            // Nếu user chọn thoát (0) → kết thúc chương trình
            if (loggedInUser == null) {
                break;
            }

            // Dispatch đúng View dựa trên Role
            if (loggedInUser.getRole().equals("ADMIN")) {
                AdminView adminView = new AdminView(scanner, categoryController, movieController, userController);
                adminView.showMenu();
            } else {
                CustomerView customerView = new CustomerView(scanner, movieController,
                        customerController, categoryController, loggedInUser.getId());
                customerView.showMenu();
            }
            // Sau khi đăng xuất → quay lại vòng lặp Login
        }

        scanner.close();
        System.out.println("Chương trình kết thúc.");
    }
}
