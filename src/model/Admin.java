package model;

import utils.ValidationException;

public class Admin extends User {

    // Không cần khai báo lại các thuộc tính vì đã kế thừa từ User

    // Constructor mặc định

    public Admin() {
        super();
        try {
            this.setRole("ADMIN"); // Thiết lập role thông qua setter để đồng nhất logic
        } catch (ValidationException e) {
            // Không bao giờ xảy ra vì "ADMIN" luôn hợp lệ
        }
    }

    // Constructor đầy đủ tham số 

    public Admin(String id, String username, String password, String fullName, String email) throws ValidationException {
        // Gọi costructor của lớp cha (User), truyền cứng role là ADMIN
        super(id, username, password, fullName, email, "ADMIN");
    }

    // Copy Constructor
    public Admin(Admin source) {
        super(source); // Gọi copy constructor của User (protected)
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
