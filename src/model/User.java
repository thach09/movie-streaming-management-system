package model;
import utils.ValidationException;
import java.util.Objects;

public abstract class User {

    // Attributes (Thuộc tính)
    protected String id;
    protected String username;
    protected String password;
    protected String fullName;
    protected String email;
    protected String role;
    protected boolean isActive;

    // Constructor 1: Mặc định (Không có tham số)
    public User () {
        this.isActive = true; // Mặc định account mới luôn ở trạng thái active
    }

    // Constructor 2: Đầy đủ tham số
    public User (String id, String username, String password, String fullName, String email, String role) throws ValidationException {
        setId(id);
        setUsername(username);
        setPassword(password);
        setFullName(fullName);
        setEmail(email);
        setRole(role);
        this.isActive = true;
    }

    // Constructor 3: Copy Constructor (protected — chỉ cho subclass gọi qua super)
    protected User(User source) {
        this.id = source.id;
        this.username = source.username;
        this.password = source.password;
        this.fullName = source.fullName;
        this.email = source.email;
        this.role = source.role;
        this.isActive = source.isActive;
    }

    // === Getters & Setters ===

    public String getId() {
        return id;
    }

    public void setId(String id) throws ValidationException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("ID không được để trống!");
        }
        if (id.contains("|")) throw new ValidationException("ID không được chứa ký tự '|'");
        this.id = id.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) throws ValidationException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Tên đăng nhập không được để trống!");
        }
        if (username.contains("|")) throw new ValidationException("Tên đăng nhập không được chứa ký tự '|'");
        this.username = username.trim();
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) throws ValidationException {
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Mật khẩu không được để trống!");
        }
        if (password.trim().length() < 6) {
            throw new ValidationException("Mật khẩu phải có ít nhất 6 ký tự!");
        }
        if (password.contains("|")) throw new ValidationException("Mật khẩu không được chứa ký tự '|'");
        this.password = password.trim();
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) throws ValidationException {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new ValidationException("Họ và tên không được để trống!");
        }
        if (fullName.contains("|")) throw new ValidationException("Họ và tên không được chứa ký tự '|'");
        this.fullName = fullName.trim();
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) throws ValidationException {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email không được để trống!");
        }
        String cleanEmail = email.trim();
        if (cleanEmail.contains("|")) throw new ValidationException("Email không được chứa ký tự '|'");
        if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            throw new ValidationException("Email không đúng định dạng (phải chứa '@' và domain)!");
        }
        this.email = cleanEmail;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) throws ValidationException {
        if (role == null || role.trim().isEmpty()) {
            throw new ValidationException("Vai trò không được để trống!");
        }
        String cleanRole = role.trim().toUpperCase();
        if (!cleanRole.equals("ADMIN") && !cleanRole.equals("CUSTOMER")) {
            throw new ValidationException("Vai trò không hợp lệ (chỉ chấp nhận ADMIN hoặc CUSTOMER)!");
        }
        this.role = cleanRole;
    }
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return String.format("User [ID=%s, Username=%s, FullName=%s, Email=%s, Role=%s, Status=%s]",
                id, username, fullName, email, role, (isActive ? "ACTIVE" : "INACTIVE"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
