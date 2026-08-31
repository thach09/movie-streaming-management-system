# KỸ THUẬT VÀ THUẬT TOÁN ỨNG DỤNG TRONG MOVIE STREAMING MANAGEMENT SYSTEM

Tài liệu này ghi chú chi tiết, ở mức độ file và function, về toàn bộ các pattern, kỹ thuật lập trình và thuật toán đã được triển khai trong dự án, đảm bảo tuân thủ 100% Java thuần (Pure Java) và các chuẩn mực OOP.

---

## 1. TẦNG MODEL (`src/model/`)
Tầng đại diện cho các thực thể dữ liệu (Domain Entities). Chịu trách nhiệm bảo vệ toàn vẹn dữ liệu ở cấp độ thấp nhất.

### 1.1. `User.java` (Abstract Base Class)
- **Kỹ thuật Abstraction (Trừu tượng):** Được khai báo là `abstract class`, ngăn không cho khởi tạo trực tiếp mà phải thông qua các class con.
- **Kỹ thuật Encapsulation (Đóng gói):** Sử dụng access modifier `protected` cho các thuộc tính (`id`, `username`, `password`,...) để các class con (Admin, Customer) có thể truy cập trực tiếp khi cần thiết lập, nhưng vẫn ẩn với bên ngoài.
- **Kỹ thuật Validation (Ràng buộc dữ liệu):** Trong mọi hàm `Setter` (ví dụ `setId`, `setEmail`), áp dụng ném ngoại lệ `ValidationException` thủ công nếu vi phạm điều kiện (chặn chuỗi rỗng `trim().isEmpty()`, chặn chứa dấu `|`, `,`, kiểm tra password độ dài >= 6, email chứa `@` và `.`).
- **Kỹ thuật Copy Constructor (Bảo vệ tham chiếu):** Hàm `protected User(User source)` được cài đặt để hỗ trợ việc deep copy object, tránh rò rỉ tham chiếu bộ nhớ (Encapsulation Leak) khi Controller trả dữ liệu cho View.

### 1.2. `Admin.java` & `Customer.java` (Subclasses)
- **Kỹ thuật Inheritance (Kế thừa) & Polymorphism (Đa hình):** Dùng từ khóa `extends User`. Gọi `super(...)` trong Constructor để kế thừa toàn bộ thuộc tính và hàm validate từ cha, giúp giảm thiểu code lặp.
- **Kỹ thuật Deep Copy Collection (Trong `Customer.java`):** Các field `watchlist`, `favouriteList`, `watchHistory` (kiểu `List<String>`) luôn được sao chép sâu thông qua `new ArrayList<>(sourceList)` cả ở Constructor và Getter. Tránh lỗi bên ngoài can thiệp làm thay đổi list bên trong Model.

### 1.3. `Category.java` & `Movie.java` (Domain Models)
- **Kỹ thuật Soft Delete (Xóa mềm):** Thay vì xóa hẳn bản ghi (Hard delete), áp dụng trường trạng thái `boolean isActive`. Xóa dữ liệu chỉ đơn thuần là set `isActive = false`, giúp dễ dàng khôi phục và giữ lịch sử.
- **Constructor Delegation:** Constructor gọi trực tiếp đến các hàm Setter (ví dụ `this.setId(id)`) để tận dụng lại toàn bộ logic validation đã viết, thay vì phải kiểm tra lại (DRY Principle).

### 1.4. `Statistics.java`
- **Design Pattern: Data Transfer Object (DTO):** Là object chỉ đọc (Immutable), không có Setter. Nhiệm vụ duy nhất là đóng gói (pack) các dữ liệu thống kê từ Controller trả về (totalMovies, averageRating, top5MostViewed) để in ra View.

---

## 2. TẦNG REPOSITORY (`src/repository/`)
Tầng giao tiếp dữ liệu File I/O, thực hiện chuẩn hóa việc đọc/ghi xuống ổ cứng.

### 2.1. Các Interfaces (`ICategoryRepository.java`, `IMovieRepository.java`, `IUserRepository.java`)
- **Nguyên lý Dependency Inversion (DIP trong SOLID):** Định nghĩa các hợp đồng (contracts) như `loadAll()` và `saveAll()`. Đảm bảo Tầng Controller chỉ phụ thuộc vào Interface, không bị ràng buộc vào cách đọc/ghi file text cụ thể. 

### 2.2. `CategoryRepository.java` & `MovieRepository.java` (File I/O Classes)
- **Thuật toán File Parsing (Tách chuỗi I/O):**
  - **Đọc file:** Dùng `BufferedReader`. Sử dụng hàm `line.split("\\|", -1)` (tham số `-1` rất quan trọng) để tách dòng bằng dấu delimiter `|` mà không làm rơi rớt các chuỗi rỗng (empty string) ở cuối dòng (như field `description` của Category).
  - **Ghi file:** Dùng `BufferedWriter` và `StringBuilder` nối chuỗi tuần tự bằng dấu `|`.
- **Kỹ thuật Graceful Degradation (Bẫy lỗi chống sập dòng):** Toàn bộ vòng lặp đọc từng dòng dữ liệu (`while ((line = br.readLine()) != null)`) được bọc `try-catch` cục bộ. Nếu 1 dòng file bị hỏng hoặc cố tình bị sửa sai định dạng, hệ thống chỉ in ra log `System.err.println("Caution...")` và nhảy sang đọc dòng tiếp theo, không làm crash toàn bộ vòng lặp.

### 2.3. `UserRepository.java`
- **Thuật toán Object Mapping & Đa hình (Polymorphic Instantiation):** Khi đọc dòng, kiểm tra trường Role (`parts[5]`). 
  - Nếu là `ADMIN`, khởi tạo `new Admin(...)`. 
  - Nếu là `CUSTOMER`, tiếp tục dùng hàm nội bộ `parseList()` để tách mảng chuỗi con bằng dấu phẩy `,`, sau đó khởi tạo `new Customer(...)`. 
  - Trả về chung `List<User>`, tận dụng tính đa hình.
- **Thuật toán Tách/Nối Mảng con (List Delimiter):** Hàm `joinList` xử lý nối `List<String>` bằng `String.join(",", list)`, hàm `parseList` tách chuỗi thành List bằng `Arrays.asList(str.split(","))`.

---

## 3. TẦNG CONTROLLER (`src/controller/`)
Tầng điều phối luồng chạy và chứa Business Logic cốt lõi (Nghiệp vụ nền tảng).

### 3.1. `CategoryController.java` & `UserController.java`
- **Kỹ thuật Constructor/Setter Injection (Dependency Injection):**
  - Nhận Interface Repository qua tham số Constructor.
  - Xử lý **Circular Dependency** (Phụ thuộc vòng): `CategoryController` cần `MovieController` để check Khóa ngoại khi xóa danh mục, nhưng `MovieController` cũng cần `CategoryController` để check tồn tại khi tạo phim. Cách giải quyết: Dùng Setter Injection (`setMovieController()`) sau khi cả 2 đã khởi tạo xong.
- **Thuật toán Phân mảng (Array Isolation):** Hàm `getActiveCategories()` hoặc `getAllUsers()` duyệt danh sách gốc `List<...>`, lọc ra các object thỏa mãn (VD: `isActive == true`), gọi Copy Constructor tạo bản sao `new Category(cat)`, và gom vào List mới trả về. Bảo vệ dữ liệu gốc an toàn tuyệt đối.

### 3.2. `MovieController.java`
- **Kỹ thuật Atomicity (Toàn vẹn Transaction Memory):** Trong hàm `updateMovie(...)`:
  - Khởi tạo bản sao tạm: `Movie temp = new Movie(movie);`
  - Gán dữ liệu mới lên bản sao: `temp.setTitle(...)`, `temp.setReleaseYear(...)`. Nếu có bất kỳ field nào sai, Model sẽ throw Exception và dừng hàm ngay lập tức.
  - Rớt xuống cuối hàm: Chỉ khi toàn bộ dữ liệu hợp lệ, mới thực sự gọi Setter lên object thật (bản gốc). Điều này ngăn chặn tình trạng "Sửa được 1 nửa thì văng lỗi" (Data Corruption).
- **Thuật toán Auto Ranking (Công thức tính điểm):**
  - Hàm `getAutoRanking()` tích hợp công thức riêng: `score = rating * 10 + views * 0.01 + favouritesCount * 0.5`. Lấy kết quả đó làm tiêu chí cho thuật toán Bubble Sort, trả về danh sách phim đã xếp hạng.

### 3.3. `CustomerController.java`
- **Cross-Controller Foreign Key Validation (Kiểm tra Khóa ngoại liên kết):**
  - Hàm `addToWatchlist`, `addToFavourites`, `addToWatchHistory` đều gọi hàm nội bộ `validateActiveMovieExists()`. Hàm này liên kết sang `movieController.findById()` để đảm bảo phim muốn tương tác thực sự tồn tại và đang kích hoạt (chống rác dữ liệu).
- **Kỹ thuật thiết kế I/O Silent-Side-Effect:** Các hàm tương tác với phim gọi đến `movieController.incrementViews()` hoặc `incrementFavouritesCount()`. Vì kiến trúc đang dùng File I/O (không có atomic transaction chéo file), nếu việc ghi file `movies.txt` bị lỗi, hệ thống sẽ im lặng bắt lỗi (Return false) để không làm gián đoạn việc cập nhật file `users.txt`.

---

## 4. TẦNG UTILS (Thuật toán & Tiện ích) (`src/utils/`)
Toàn bộ thuật toán được **tự tay cài đặt thủ công**, tuyệt đối **không sử dụng** `Collections.sort()` hay Java Stream API (như `.filter()`, `.map()`).

### 4.1. `SortUtils.java` (Thuật toán Sắp xếp)
- **Thuật toán Áp dụng:** **Bubble Sort (Nổi bọt)**.
- **Độ phức tạp thuật toán (Time Complexity):** $O(N^2)$ (Chấp nhận được với file lưu trữ cục bộ vừa/nhỏ, dễ cài đặt bằng tay).
- **Cơ chế hoạt động:** 
  - Khởi tạo mảng copy: `List<Movie> result = new ArrayList<>(movies);`
  - Vòng lặp 2 cấp (Nested Loops) kiểm tra từng cặp phần tử liền kề `result.get(j)` và `result.get(j+1)`.
  - Khai báo biến trung gian `Movie temp` để tráo đổi vị trí (Swap).
- **Xử lý Đa hình Dữ liệu (Polymorphism trong thuật toán):**
  - Sắp xếp String (Tên phim): Dùng hàm `compareToIgnoreCase()`.
  - Sắp xếp Primitives (Rating, Year, Views): Dùng toán tử `>`, `<`.
  - Sắp xếp Dynamic (Tăng/Giảm): Dùng biến `boolean ascending` kết hợp toán tử 3 ngôi `(ascending ? cmp > 0 : cmp < 0)` để đảo chiều thuật toán ngay lập tức.
  - Hàm tiêu biểu: `sortByTitle`, `sortByViews`, `sortByReleaseYear`, `sortByPopularity`.

### 4.2. `SearchUtils.java` (Thuật toán Tìm kiếm)
- **Thuật toán Áp dụng:** **Linear Search (Tìm kiếm Tuyến tính)**.
- **Độ phức tạp thuật toán (Time Complexity):** $O(N)$.
- **Cơ chế hoạt động:** 
  - Duyệt tuần tự mảng bằng vòng lặp `for-each`.
  - Ép kiểu tất cả về chuỗi thường `.toLowerCase()` và dùng `.contains(keyword)` để tìm kiếm tương đối (Sub-string match).
  - Thu gom mọi kết quả thỏa mãn vào 1 List kết quả mới.
  - Hàm tiêu biểu: `searchByTitle`, `searchByActor`, `searchByDirector`, `searchByCategoryId`.

### 4.3. `InputValidator.java` (Tiện ích Giao diện)
- **Thuật toán Bẫy lỗi Input (Loop Until Valid):** Kỹ thuật bọc lệnh đọc bàn phím (Scanner) trong vòng lặp vô hạn `while(true)`. Nếu người dùng nhập sai, dùng `try-catch` bắt `NumberFormatException` và yêu cầu nhập lại thay vì văng ứng dụng.
- **Kỹ thuật Scanner Buffer Leak Fix:** Chặn triệt để lỗi "trôi lệnh dòng" (Enter/Newline leakage) kinh điển của class `Scanner` trong Java bằng cách luôn lấy toàn bộ chuỗi bằng `scanner.nextLine()` sau đó tự parse `Integer.parseInt()`, thay vì dùng hàm `scanner.nextInt()` gây bỏ sót ký tự `\n`.

### 4.4. `ValidationException.java`
- **Kỹ thuật Custom Exception Handling:** Kế thừa trực tiếp từ class `Exception`. Tách biệt rõ ràng lỗi do logic nghiệp vụ (Business Validation) với lỗi hệ thống (như IOException hay NullPointerException).

---

## 5. CẤU TRÚC DỮ LIỆU TỰ ĐỊNH NGHĨA & TÍNH NĂNG NÂNG CAO

### 5.1. `CustomStack.java` (Ngăn xếp LIFO cho tính năng Undo/Redo)
Dự án **KHÔNG SỬ DỤNG** bất kỳ Collection có sẵn nào như `java.util.Stack` hay `java.util.Deque` để thực hiện Undo/Redo, tuân thủ đúng yêu cầu môn học về "Tự cài đặt thuật toán".
- **Cấu trúc dữ liệu (Data Structure):** Danh sách liên kết đơn (Singly Linked List) với một con trỏ `top`.
- **Kỹ thuật Generic (`<T>`):** Cho phép tái sử dụng Stack cho bất kỳ kiểu dữ liệu nào (trong bài toán này là lưu trữ class `WatchlistAction`).
- **Phân tích độ phức tạp (Time Complexity):** 
  - `push(T item)`: Tạo Node mới trỏ vào `top` cũ, cập nhật `top`. **O(1)**.
  - `pop()` / `peek()`: Lấy/dời phần tử ngay tại `top`. **O(1)**.
  - `clear()`: Hủy liên kết `top` để Garbage Collector tự động dọn dẹp bộ nhớ. **O(1)**.
- **Tính đóng gói (Encapsulation):** Class `Node<T>` được khai báo dưới dạng `private static class` bên trong `CustomStack` để che giấu hoàn toàn chi tiết cài đặt con trỏ liên kết với bên ngoài. Khách hàng sử dụng class này chỉ được giao tiếp thông qua các hàm tiêu chuẩn.

### 5.2. `CustomerController.java` (Thuật toán Undo/Redo)
Áp dụng tư tưởng **Command Pattern** (Dạng thu gọn) cho tính năng Undo/Redo Watchlist:
- Sử dụng 2 biến RAM `undoStack` và `redoStack` (kiểu `CustomStack<WatchlistAction>`).
- Khi user thực hiện hành động mới (ADD/REMOVE), hành động đó được đẩy vào `undoStack`, đồng thời **xóa sạch (clear)** `redoStack` (vì một hành động mới sẽ rẽ nhánh lịch sử và phá vỡ chuỗi Redo hiện tại).
- Lệnh `undo()` sẽ lấy (pop) hành động cuối cùng, thực thi ngược lại (ADD thành REMOVE, REMOVE thành ADD), lưu file, và đẩy hành động đó sang `redoStack`.
- Dữ liệu Undo/Redo Stack chỉ tồn tại trên RAM (Memory-based) theo mỗi session (phiên đăng nhập), không ghi xuống file `.txt`, giúp tối ưu hiệu năng đọc/ghi File I/O.

### 5.3. `ReportExporter.java` (Tiện ích xuất báo cáo)
- **Kỹ thuật Functional Interface / Method Reference:** Khi xuất báo cáo CSV, `CustomerController` gọi đến hàm tĩnh bằng cách truyền tham chiếu hàm (Callback function) `movieController::findById`. Kỹ thuật này giúp Tầng Utility (ReportExporter) có thể tra cứu thông tin phim chi tiết mà hoàn toàn không bị phụ thuộc vòng (Circular Dependency) vào `MovieController`.
