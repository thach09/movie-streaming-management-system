# PROJECT CONTEXT & ARCHITECTURAL GUIDELINES
## Movie Streaming Management System (Netflix-like CLI Application)

---

## 1. THÔNG TIN CHUNG (OVERVIEW)
- **Tên dự án:** Movie Streaming Management System
- **Loại hình:** Ứng dụng dòng lệnh (CLI Console Application) mô phỏng hệ thống xem phim trực tuyến kiểu Netflix.
- **Mục đích:** Đồ án tốt nghiệp / Kết thúc môn học Lập trình hướng đối tượng (OOP) cấp Đại học.
- **Phương thức hợp tác:** AI Agent (Antigravity IDE) làm việc trực tiếp với Developer & Senior Mentor theo từng bước nhỏ (Task-by-Task execution).

---

## 2. RÀNG BUỘC KỸ THUẬT NGHIÊM NGẶT (TECHNICAL CONSTRAINTS)
1. **Ngôn ngữ:** Pure Java (Java Standard SDK thuần). 
   - **TUYỆT ĐỐI KHÔNG** dùng bất kỳ Framework nào (No Spring, No Hibernate, No JPA, No Lombok, v.v.).
   - **TUYỆT ĐỐI KHÔNG** dùng thư viện bên thứ 3 nào ngoại trừ Java SDK tiêu chuẩn.
2. **Kiến trúc:** Model-View-Controller (MVC) nghiêm ngặt.
3. **Lưu trữ dữ liệu:** Dùng **FILE THUẦN** (Text File / CSV / Object Stream).
   - **TUYỆT ĐỐI KHÔNG** sử dụng CSDL SQL (MySQL, PostgreSQL, SQLite...) hay NoSQL (MongoDB...).
4. **Chất lượng Code & OOP:**
   - Đảm bảo đầy đủ 4 tính chất OOP: **Encapsulation** (Đóng gói), **Inheritance** (Kế thừa), **Polymorphism** (Đa hình), **Abstraction** (Trừu tượng).
   - Áp dụng kỹ thuật xử lý ngoại lệ (Exception Handling) với Custom Exception riêng.
   - Tự viết các thuật toán Tìm kiếm (Search), Sắp xếp (Sort) và Cấu trúc dữ liệu tự định nghĩa (Custom Stack, Custom Queue...).

---

## 3. CHIẾN LƯỢC TÍNH NGHIỆP VỤ & CHỐNG BẮT LỖI (BUSINESS & BULLETPROOF VALIDATION)

### 3.1. Tính Nghiệp Vụ Thực Tế (Business Realism)
- **Trạng thái đối tượng (Object State):** Áp dụng Soft Delete / Active Flag (`ACTIVE`, `INACTIVE`) thay vì hard delete dữ liệu nguy hiểm.
- **Ràng buộc toàn vẹn dữ liệu (Data Integrity):**
  - Không cho phép xóa Danh mục (`Category`) khi đang có Phim (`Movie`) tham chiếu tới.
  - Không cho phép trùng Tên danh mục (Case-insensitive check) hoặc trùng Mã định danh (ID).
  - Tự động đồng bộ số lượng phim / thống kê liên quan.

### 3.2. Chiến Lược Validate 3 Lớp (Bulletproof Strategy against Edge Cases)
1. **Lớp Model (Domain Entities):**
   - Đóng gói thuộc tính `private`.
   - Tất cả `Setter` và `Constructor` đều phải qua kiểm tra logic validation nghiêm ngặt (Null check, Blank/Empty check, Range check, Regex check).
   - Ném ngoại lệ `IllegalArgumentException` hoặc `ValidationException` khi dữ liệu vi phạm.
2. **Lớp View / Console (CLI Input Protection):**
   - Xây dựng lớp utility **`InputValidator`** chuyên trách đọc và kiểm tra input từ người dùng.
   - Xử lý triệt để bug trôi dòng của `Scanner` và ngăn chặn crash ứng dụng khi người dùng nhập sai định dạng (ví dụ cố tình nhập chữ vào ô số).
   - Luôn cho phép tùy chọn Hủy/Quay lại (`0` hoặc `cancel`) trong các luồng nhập liệu.
3. **Lớp Repository (Data Integrity & File I/O Protection):**
   - Thao tác ghi file an toàn, tránh trích xuất sai cấu trúc dòng do ký tự phân cách (Escape Delimiters).
   - Đọc file an toàn: Tự động bỏ qua hoặc log cảnh báo các dòng dữ liệu bị hư hỏng bên ngoài file mà không làm sập ứng dụng (Graceful Degradation).

---

## 4. CẤU TRÚC THƯ MỤC NGUỒN (FOLDER STRUCTURE)
```text
src/
 ├── model/         # Các Entity đại diện dữ liệu (Category, Movie, User, Customer, Admin...)
 ├── view/          # Giao diện dòng lệnh CLI (Menus, Screens, Input Readers)
 ├── controller/    # Bộ điều phối luồng & xử lý logic nghiệp vụ
 ├── repository/    # Thao tác đọc/ghi file lưu trữ dữ liệu (File I/O)
 ├── utils/         # Hàm tiện ích, InputValidator, Custom Exceptions, Algorithms, Custom DS
 └── Main.java      # Điểm khởi chạy ứng dụng (Application Entry Point)
```

---

## 5. DANH SÁCH CHỨC NĂNG THEO ĐỘ KHÓ (REQUIREMENTS MATRIX)

| Cấp độ | Chức năng | Mô tả chi tiết |
| :--- | :--- | :--- |
| **Basic (B)** | • Category CRUD<br>• Movie CRUD<br>• Search<br>• Sort<br>• Watchlist & Favorites<br>• View Details | Quản lý danh mục & phim, tìm kiếm theo tên/diễn viên/đạo diễn/thể loại, sắp xếp theo tên/rating/năm/độ phổ biến, danh sách yêu thích và danh sách chờ xem. |
| **Medium (M)** | • Watching History<br>• Continue Watching<br>• Browse by Category<br>• Statistics<br>• Recently Watched<br>• Trending Categories | Lịch sử xem phim, tính năng xem tiếp, duyệt phim theo thể loại, thống kê lượt xem, phim vừa xem gần đây, danh mục xu hướng. |
| **Hard (H)** | • Undo/Redo Watchlist<br>• Auto Ranking System<br>• Advanced Filtering<br>• Report Export | Quản lý Undo/Redo Watchlist bằng **Custom Stack**, tự động xếp hạng phim dựa trên công thức tính điểm (Rating, Views, Favorites), lọc đa điều kiện nâng cao, xuất báo cáo lịch sử xem ra file. |

---

## 6. NGUYÊN TẮC LÀM VIỆC CỦA AI AGENT (AGENT RULES)
1. **Làm việc từng bước nhỏ (Micro Step-by-Step):** Đi từng bước rất nhỏ, không vội vã.
2. **Vai trò Hướng dẫn & Dạy học (Mentoring & Pair Programming Mode):** AI đóng vai trò là Senior Developer / Giảng viên đồng hành: Phân tích tư duy lập trình, giải thích "Tại sao lại làm như vậy", đưa ra câu hỏi gợi mở và cấu trúc hướng dẫn để **User trực tiếp gõ code**, tuyệt đối **KHÔNG tự code 100%**.
3. **Giao tiếp Tiếng Việt:** Tất cả trao đổi, giải thích và phản hồi đều sử dụng Tiếng Việt.
4. **Chất lượng Code chuẩn Đại học:** Code sạch, tối ưu, đặt tên chuẩn Java Convention, có comment giải thích rõ ràng để phục vụ việc bảo vệ / review code.
5. **Xác nhận & Kiểm thử:** Luôn hỗ trợ hướng dẫn kiểm thử (`javac`) sau mỗi bước nhỏ.

---

## 7. TRẠNG THÁI HIỆN TẠI VÀ BƯỚC TIẾP THEO (SESSION PROGRESS & NEXT STEPS)
*Cập nhật lần cuối: 11/08/2026*

### ✅ Công việc đã hoàn thành hôm nay (11/08/2026):
1. **Cấu trúc Dự án & File Context:** Đã tạo repo Git, cấu trúc MVC trong `src/` và khởi tạo file `CONTEXT.md`.
2. **Category Model (Soft Delete):** Đã bổ sung trường `private boolean isActive;` vào `Category.java`, khởi tạo `isActive = true;` trong các Constructors và tạo Getter/Setter (`isActive()`, `setActive()`). Biên dịch thành công 100%.
3. **Thống nhất Kiến trúc Validation 2 Tầng:** 
   - *Tầng Model (`Category.java`):* Validate chống null/empty để bảo vệ tính toàn vẹn dữ liệu nội tại (Encapsulation).
   - *Tầng Utility (`InputValidator.java`):* File tiện ích riêng chuyên đọc Console input an toàn, chống trôi dòng `Scanner` và bắt người dùng nhập lại khi gõ sai.

### 🛑 Điểm dừng hiện tại (Current Stop Point):
- Đang ở file [Category.java](file:///d:/GitHub/movie-streaming-management-system/src/model/Category.java).
- Đã chuẩn bị sẵn sàng tư duy để viết Model Validation trong các Setters (`setId`, `setName`).

### ⏭️ Việc cần làm ngay khi mở lại dự án (Next Immediate Steps):
1. **Nhiệm vụ 1:** Thêm `isActive` vào hàm `toString()` của `Category.java`.
2. **Nhiệm vụ 2:** Viết Model-level Validation trong `setId()` và `setName()` của `Category.java` (Throw `IllegalArgumentException` nếu `null` hoặc `trim().isEmpty()`).
3. **Nhiệm vụ 3:** Tạo Custom Exception `ValidationException.java` trong thư mục `src/utils/`.
4. **Nhiệm vụ 4:** Tạo file tiện ích riêng `InputValidator.java` trong `src/utils/` để bắt lỗi nhập từ bàn phím.


