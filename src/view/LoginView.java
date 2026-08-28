package view;

import controller.UserController;
import model.User;
import utils.InputValidator;
import utils.ValidationException;

import java.util.Scanner;

/**
 * Menu CLI đăng nhập / đăng ký.
 * Trả về User đã đăng nhập thành công (Admin hoặc Customer).
 */
public class LoginView {
    private Scanner scanner;
    private UserController userController;

    public LoginView(Scanner scanner, UserController userController) {
        this.scanner = scanner;
        this.userController = userController;
    }

    /**
     * Hiển thị menu đăng nhập/đăng ký.
     * Trả về User đã login thành công, hoặc null nếu chọn thoát.
     */
    public User showMenu() {
        int choice;
        do {
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║  🎬 MOVIE STREAMING MANAGEMENT 🎬   ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║  1. Đăng nhập                        ║");
            System.out.println("║  2. Đăng ký tài khoản mới            ║");
            System.out.println("║  0. Thoát chương trình               ║");
            System.out.println("╚══════════════════════════════════════╝");
            choice = InputValidator.readInt(scanner, "Chọn chức năng: ", 0, 2);

            switch (choice) {
                case 1:
                    User loggedIn = login();
                    if (loggedIn != null) return loggedIn;
                    break;
                case 2:
                    register();
                    break;
                case 0:
                    System.out.println("Tạm biệt! 👋");
                    return null;
            }
        } while (true);
    }

    private User login() {
        try {
            String username = InputValidator.readString(scanner, "Tên đăng nhập: ");
            String password = InputValidator.readString(scanner, "Mật khẩu: ");
            User user = userController.login(username, password);
            System.out.println("✅ Đăng nhập thành công! Xin chào " + user.getFullName() + " [" + user.getRole() + "]");
            return user;
        } catch (ValidationException e) {
            System.out.println("❌ " + e.getMessage());
            return null;
        }
    }

    private void register() {
        try {
            System.out.println("\n--- ĐĂNG KÝ TÀI KHOẢN MỚI ---");
            String id = InputValidator.readString(scanner, "Nhập ID: ");
            String username = InputValidator.readString(scanner, "Nhập tên đăng nhập: ");
            String password = InputValidator.readString(scanner, "Nhập mật khẩu (≥6 ký tự): ");
            String fullName = InputValidator.readString(scanner, "Nhập họ và tên: ");
            String email = InputValidator.readString(scanner, "Nhập email: ");

            if (userController.register(id, username, password, fullName, email)) {
                System.out.println("✅ Đăng ký thành công! Vui lòng đăng nhập.");
            } else {
                System.out.println("❌ Lỗi ghi file!");
            }
        } catch (ValidationException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }
}
