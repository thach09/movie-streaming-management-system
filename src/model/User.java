package model;
import utils.ValidationException;

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

    // === Getters & Setters ===

    public String getId() {
        return id;
    }

    public void setId(String id) throws ValidationException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("ID không được để trống!");
        }
        this.id = id.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) throws ValidationException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Tên đăng nhập không được để trống!");
        }
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
        this.password = password.trim();
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) throws ValidationException {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new ValidationException("Họ và tên không được để trống!");
        }
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

    
}
