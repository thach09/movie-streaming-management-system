package model;

import utils.ValidationException;

public class Admin extends User {

    // Không cần khai báo lại các thuộc tính vì đã kế thừa từ User

    // Constructor mặc định

    public Admin() {
        super();
        this.role = "ADMIN"; //Thiết lập role mặc định
    }

    // Constructor đầy đủ tham số 

    public Admin(String id, String username, String password, String fullName, String email) throws ValidationException {
        // Gọi costructor của lớp cha (User), truyền cứng role là ADMIN
        super(id, username, password, fullName, email, "ADMIN");
    }

        @Override
    public String toString() {
        return "ADMIN " + super.toString();
    }
}
