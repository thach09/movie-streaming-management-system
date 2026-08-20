package controller;

import model.Admin;
import model.Customer;
import model.User;
import repository.IUserRepository;
import utils.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class UserController {
    private List<User> userList;
    private IUserRepository userRepository;

    // Constructor Injection: nhận interface thay vì class cụ thể
    public UserController(IUserRepository userRepository) {
        this.userRepository = userRepository;
        this.userList = userRepository.loadAll();
    }

    // --- AUTHENTICATION (Xác thực) ---

    /**
     * Đăng nhập: kiểm tra username + password.
     * KHÔNG được tiết lộ sai username hay sai password riêng biệt (bảo mật).
     * Trả về bản sao User (Admin hoặc Customer nhờ đa hình) nếu đúng.
     */
    public User login(String username, String password) throws ValidationException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Tên đăng nhập không được để trống!");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Mật khẩu không được để trống!");
        }

        String cleanUsername = username.trim();
        String cleanPassword = password.trim();

        for (User user : userList) {
            if (user.getUsername().equalsIgnoreCase(cleanUsername) // Dùng equalsIgnoreCase để so sánh username không phân biệt hoa thường
                    && user.getPassword().equals(cleanPassword)
                    && user.isActive()) {
                // Trả về bản sao — dùng đa hình để gọi đúng Copy Constructor
                return copyUser(user);
            }
        }

        // Thông báo chung, KHÔNG tiết lộ username có tồn tại hay không
        throw new ValidationException("Sai tên đăng nhập hoặc mật khẩu!");
    }

    // --- REGISTRATION (Đăng ký) ---

    /**
     * Đăng ký tài khoản Customer mới.
     * Kiểm tra trùng ID và trùng username (case-insensitive).
     */
    public boolean register(String id, String username, String password, String fullName, String email) throws ValidationException {
        // Kiểm tra trùng ID
        for (User user : userList) {
            if (user.getId().equalsIgnoreCase(id)) {
                throw new ValidationException("ID '" + id + "' đã tồn tại!");
            }
        }

        // Kiểm tra trùng username (case-insensitive)
        for (User user : userList) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                throw new ValidationException("Tên đăng nhập '" + username + "' đã được sử dụng!");
            }
        }

        // Tạo Customer mới (Constructor sẽ tự validate tất cả field)
        Customer newCustomer = new Customer(id, username, password, fullName, email,
                null, null, null); // 3 danh sách khởi tạo rỗng
        userList.add(newCustomer);
        return userRepository.saveAll(userList);
    }

    // --- READ (Truy xuất) ---

    /**
     * Tìm User theo ID, trả về bản sao (deep copy qua đa hình).
     * Trả null nếu không tìm thấy.
     */
    public User findById(String id) {
        User user = findReferenceById(id);
        return user != null ? copyUser(user) : null;
    }

    /**
     * Dành cho Admin: Lấy toàn bộ danh sách User (kể cả inactive).
     * Trả về bản sao List chứa bản sao từng User.
     */
    public List<User> getAllUsers() {
        List<User> copies = new ArrayList<>();
        for (User user : userList) {
            copies.add(copyUser(user));
        }
        return copies;
    }

    // --- HÀM NỘI BỘ (PRIVATE HELPERS) ---

    /**
     * Tìm tham chiếu thật trong danh sách nội bộ (chỉ dùng nội bộ Controller).
     */
    private User findReferenceById(String id) {
        for (User user : userList) {
            if (user.getId().equalsIgnoreCase(id)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Tạo bản sao User đúng kiểu thực tế nhờ đa hình (instanceof).
     * Admin → new Admin(admin), Customer → new Customer(customer).
     */
    private User copyUser(User user) {
        if (user instanceof Customer) {
            return new Customer((Customer) user);
        } else if (user instanceof Admin) {
            return new Admin((Admin) user);
        }
        // Trường hợp này không bao giờ xảy ra vì User là abstract,
        // nhưng trả null để compiler không báo lỗi
        return null;
    }
}
