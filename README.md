# 🎬 Movie Streaming Management System

> Ứng dụng quản lý xem phim trực tuyến mô phỏng Netflix — xây dựng bằng **Pure Java** (Java Standard SDK) với kiến trúc **MVC**, lưu trữ **File I/O**, thể hiện đầy đủ các nguyên lý **Lập trình Hướng đối tượng (OOP)**.

[![Java](https://img.shields.io/badge/Java-Pure%20SDK-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Architecture](https://img.shields.io/badge/Architecture-MVC-blue?style=for-the-badge)](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller)
[![Storage](https://img.shields.io/badge/Storage-File%20I%2FO-green?style=for-the-badge)](https://docs.oracle.com/javase/tutorial/essential/io/)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](LICENSE)
[![Status](https://img.shields.io/badge/Status-Completed-success?style=for-the-badge)](https://github.com/thach09/movie-streaming-management-system)

---

## 📖 Mục lục

- [Giới thiệu](#-giới-thiệu)
- [Tính năng nổi bật](#-tính-năng-nổi-bật)
- [Công nghệ & Ràng buộc](#-công-nghệ--ràng-buộc)
- [Kiến trúc hệ thống](#-kiến-trúc-hệ-thống)
- [Cấu trúc thư mục](#-cấu-trúc-thư-mục)
- [Cài đặt & Chạy](#-cài-đặt--chạy)
- [Hướng dẫn sử dụng](#-hướng-dẫn-sử-dụng)
- [Nguyên lý OOP đã áp dụng](#-nguyên-lý-oop-đã-áp-dụng)
- [Thuật toán & Cấu trúc dữ liệu](#-thuật-toán--cấu-trúc-dữ-liệu)
- [Kỹ thuật nổi bật](#-kỹ-thuật-nổi-bật)
- [Dữ liệu mẫu](#-dữ-liệu-mẫu)
- [Tác giả](#-tác-giả)

---

## 📝 Giới thiệu

**Movie Streaming Management System** là ứng dụng dòng lệnh (CLI Console Application) mô phỏng hệ thống quản lý và xem phim trực tuyến kiểu **Netflix**. Dự án được xây dựng hoàn toàn bằng **Java thuần** (không Framework, không thư viện bên thứ 3), sử dụng **File I/O** thay cho cơ sở dữ liệu, nhằm thể hiện toàn diện các kiến thức:

- **4 tính chất OOP**: Đóng gói (Encapsulation), Kế thừa (Inheritance), Đa hình (Polymorphism), Trừu tượng (Abstraction)
- **Kiến trúc MVC** phân tầng rõ ràng
- **Thuật toán & Cấu trúc dữ liệu** tự cài đặt thủ công (Custom Stack, Bubble Sort, Linear Search)
- **Xử lý ngoại lệ** (Custom Exception Handling)

---

## Tính năng nổi bật

### Hệ thống phân quyền 2 vai trò (Dual-Role System)

<table>
<tr>
<th width="50%"> Admin (Quản trị viên)</th>
<th width="50%"> Customer (Người dùng)</th>
</tr>
<tr>
<td>

- Quản lý **Thể loại** (CRUD + Soft Delete + Restore)
- Quản lý **Phim** (CRUD + Soft Delete + Restore)
- Xem danh sách **Người dùng**
- **Tìm kiếm** phim (theo tên, diễn viên, đạo diễn, thể loại)
- **Sắp xếp** phim (theo tên, đánh giá, năm, độ phổ biến)
- **Thống kê** lượt xem tổng quan
- **Auto Ranking** — xếp hạng phim tự động
- **Trending Categories** — danh mục xu hướng

</td>
<td>

- **Duyệt phim** theo thể loại
- **Tìm kiếm, Sắp xếp & Lọc nâng cao** đa tiêu chí
- **Watchlist** — Danh sách chờ xem *(Undo/Redo bằng Custom Stack)*
- **Favourites** — Danh sách yêu thích
- **Watch History** — Lịch sử xem phim
- **Continue Watching** — Xem tiếp phim gần đây
- **Auto Ranking** — Bảng xếp hạng phim
- Xem **chi tiết phim** đầy đủ

</td>
</tr>
</table>

### 🔐 Xác thực & Bảo mật
- Đăng nhập / Đăng ký tài khoản
- Mã hóa thông tin bảo mật (không tiết lộ username khi đăng nhập thất bại)
- Validate 3 lớp chặt chẽ (Model → View → Repository)

---

## 🛠 Công nghệ & Ràng buộc

| Thành phần | Công nghệ |
|:---|:---|
| **Ngôn ngữ** | Java (Pure Standard SDK) |
| **Kiến trúc** | Model-View-Controller (MVC) |
| **Lưu trữ** | File I/O (Text File, delimiter `\|`) |
| **Build** | `javac` + `java` (Không Maven/Gradle) |
| **Encoding** | UTF-8 (hỗ trợ đầy đủ Tiếng Việt) |

### ❌ Không sử dụng
- ❌ Framework (Spring, Hibernate, JPA, Lombok...)
- ❌ Thư viện bên thứ 3
- ❌ Cơ sở dữ liệu (MySQL, PostgreSQL, MongoDB...)
- ❌ Collections.sort(), Stream API (.filter(), .map()...)
- ❌ java.util.Stack

---

## 🏗 Kiến trúc hệ thống

```
┌──────────────────────────────────────────────────────────────┐
│                        PRESENTATION                          │
│   LoginView  ·  AdminView  ·  CustomerView  ·  TextUI        │
│         (CLI Console — Giao diện dòng lệnh)                  │
├──────────────────────────────────────────────────────────────┤
│                      BUSINESS LOGIC                          │
│  CategoryController · MovieController · UserController       │
│             CustomerController (Watchlist/Fav/History)       │
│         ┌──────────────────────────────────┐                 │
│         │  SearchUtils · SortUtils         │                 │
│         │  CustomStack · InputValidator    │                 │
│         │  ValidationException             │                 │
│         └──────────────────────────────────┘                 │
├──────────────────────────────────────────────────────────────┤
│                      DATA ACCESS                             │
│  ICategoryRepository ← CategoryRepository                    │
│  IMovieRepository    ← MovieRepository                       │
│  IUserRepository     ← UserRepository                        │
├──────────────────────────────────────────────────────────────┤
│                       PERSISTENCE                            │
│        data/categories.txt · data/movies.txt                 │
│                    data/users.txt                            │
└──────────────────────────────────────────────────────────────┘
```

### Dependency Injection & Circular Dependency

```
Main.java
  ├─→ CategoryController(ICategoryRepository)
  ├─→ MovieController(IMovieRepository, CategoryController)
  ├─→ CategoryController.setMovieController(MovieController)   ← Setter Injection
  ├─→ UserController(IUserRepository)
  └─→ CustomerController(UserController, MovieController)
```

---

## 📁 Cấu trúc thư mục

```
movie-streaming-management-system/
├── src/
│   ├── Main.java                          # Entry point — Khởi tạo DI & vòng lặp chính
│   ├── model/                             # Domain Entities
│   │   ├── User.java                      # Abstract base class (người dùng)
│   │   ├── Admin.java                     # Subclass kế thừa User (role = ADMIN)
│   │   ├── Customer.java                  # Subclass kế thừa User (Watchlist/Fav/History)
│   │   ├── Category.java                  # Thể loại phim
│   │   ├── Movie.java                     # Phim (10+ thuộc tính, validation chặt)
│   │   ├── Statistics.java                # DTO thống kê (immutable, chỉ đọc)
│   │   ├── WatchProgress.java             # Entity tiến độ xem phim (phần trăm)
│   │   └── WatchlistAction.java           # Hành động Undo/Redo (enum ADD/REMOVE)
│   ├── view/                              # CLI Presentation Layer
│   │   ├── LoginView.java                 # Màn hình đăng nhập / đăng ký
│   │   ├── AdminView.java                 # Dashboard quản trị viên
│   │   └── CustomerView.java              # Dashboard người dùng
│   ├── controller/                        # Business Logic Layer
│   │   ├── CategoryController.java        # CRUD thể loại + Trending Categories
│   │   ├── MovieController.java           # CRUD phim + Search/Sort + Auto Ranking
│   │   ├── UserController.java            # Authentication + User management
│   │   └── CustomerController.java        # Watchlist/Favourites/History + Undo/Redo
│   ├── repository/                        # Data Access Layer (File I/O)
│   │   ├── ICategoryRepository.java       # Interface — Dependency Inversion
│   │   ├── IMovieRepository.java          # Interface — Dependency Inversion
│   │   ├── IUserRepository.java           # Interface — Dependency Inversion
│   │   ├── CategoryRepository.java        # Đọc/Ghi categories.txt
│   │   ├── MovieRepository.java           # Đọc/Ghi movies.txt
│   │   └── UserRepository.java            # Đọc/Ghi users.txt (Polymorphic)
│   └── utils/                             # Utilities & Algorithms
│       ├── CustomStack.java               # Stack (LIFO) tự cài bằng Linked Node
│       ├── FilterUtils.java               # Thuật toán lọc nâng cao kết hợp nhiều tiêu chí
│       ├── InputValidator.java            # Validate input CLI, chống trôi dòng Scanner
│       ├── ReportExporter.java            # Hàm tiện ích xuất báo cáo CSV chuẩn RFC 4180
│       ├── SearchUtils.java               # Linear Search tự cài (không dùng Stream)
│       ├── SortUtils.java                 # Bubble Sort tự cài (không dùng Collections)
│       ├── TextUI.java                    # Box-drawing cho menu CLI (căn lề Unicode)
│       └── ValidationException.java       # Custom Exception cho lỗi nghiệp vụ
├── data/                                  # Dữ liệu lưu trữ (Text Files)
│   ├── categories.txt                     # 6 thể loại phim
│   ├── movies.txt                         # 60 phim (Việt Nam & Quốc tế)
│   └── users.txt                          # Tài khoản Admin & Customer mẫu
├── run.bat                                # Script chạy nhanh trên Windows (UTF-8)
├── .gitignore
└── README.md
```

---

## 🚀 Cài đặt & Chạy

### Yêu cầu hệ thống

- **Java JDK** 8 trở lên (khuyến nghị JDK 17+)
- **Hệ điều hành**: Windows / macOS / Linux
- **Terminal** hỗ trợ UTF-8 (để hiển thị Tiếng Việt)

### Cách 1: Sử dụng `run.bat` (Windows)

```bash
# Clone repository
git clone https://github.com/thach09/movie-streaming-management-system.git
cd movie-streaming-management-system

# Chạy ứng dụng
run.bat
```

### Cách 2: Biên dịch thủ công

```bash
# Clone repository
git clone https://github.com/thach09/movie-streaming-management-system.git
cd movie-streaming-management-system

# Biên dịch toàn bộ source code (UTF-8)
javac -encoding UTF-8 -d out -sourcepath src src/Main.java

# Chạy ứng dụng
java -cp out Main
```

### Tài khoản mẫu

| Vai trò | Username | Password |
|:---|:---|:---|
| **Admin** | `admin` | `admin123` |
| **Customer** | `thiettthach09` | `thach123` |

---

## 📋 Hướng dẫn sử dụng

### Luồng hoạt động chính

```
Khởi chạy → Đăng nhập/Đăng ký → Phân quyền theo Role
                                    │
                     ┌──────────────┴──────────────┐
                     ▼                              ▼
              ADMIN DASHBOARD                CUSTOMER DASHBOARD
              ┌─────────────┐                ┌──────────────────┐
              │ 1. Quản lý  │                │ KHÁM PHÁ PHIM    │
              │    Thể loại │                │ 1. Duyệt phim    │
              │ 2. Quản lý  │                │ 2. Tìm kiếm      │
              │    Phim     │                │ 3. Sắp xếp       │
              │ 3. Xem Users│                │ 4. Browse by Cat │
              │ 4. Thống kê │                │ 5. Auto Ranking  │
              │ 5. Ranking  │                │ 6. Lọc nâng cao  │
              │ 6. Trending │                ├──────────────────┤
              │ 7. Khôi phục│                │ THƯ VIỆN CỦA TÔI │
              │ 0. Đăng xuất│                │ 1. Watchlist     │
              └─────────────┘                │ 2. Favourites    │
                                             │ 3. Watch History │
                                             │ 4. Xem tiếp      │
                                             │ 5. Phim gần đây  │
                                             │ 6. Xuất báo cáo  │
                                             │ 0. Quay lại      │
                                             └──────────────────┘
                     │                             │
                     └──────────────┬──────────────┘
                                    ▼
                          Quay lại Đăng nhập
```

### Watchlist — Undo/Redo

Tính năng **Undo/Redo** cho Watchlist được cài đặt bằng **Custom Stack** (tự xây dựng bằng Linked Node):

- Thêm phim vào Watchlist → Ghi nhận hành động `ADD` vào **Undo Stack**
- Xóa phim khỏi Watchlist → Ghi nhận hành động `REMOVE` vào **Undo Stack**
- **Undo**: Pop từ Undo Stack, đảo ngược hành động, push vào **Redo Stack**
- **Redo**: Pop từ Redo Stack, thực hiện lại hành động, push vào **Undo Stack**
- Hành động mới sẽ **xóa toàn bộ Redo Stack** (chuẩn hành vi Undo/Redo)

---

## 🎯 Nguyên lý OOP đã áp dụng

### 1. Encapsulation (Đóng gói)

- Thuộc tính `private`/`protected` trong tất cả Model
- Getter/Setter có validation logic chặt chẽ
- Deep Copy qua **Copy Constructor** — chống rò rỉ tham chiếu (Encapsulation Leak)
- Controller trả về bản sao (clone), không trả tham chiếu gốc

### 2. Inheritance (Kế thừa)

```
User (abstract)
  ├── Admin    (role = "ADMIN")
  └── Customer (role = "CUSTOMER", + watchlist/favourites/history)
```

- `Admin` và `Customer` kế thừa toàn bộ thuộc tính và validation từ `User`
- Constructor Delegation: `super(...)` tái sử dụng logic cha

### 3. Polymorphism (Đa hình)

- `UserRepository` đọc file → tạo `Admin` hoặc `Customer` tuỳ theo trường `role` → trả về `List<User>`
- `UserController.findById()` trả `User` — View cast tuỳ ngữ cảnh
- `SortUtils` xử lý đa kiểu: String (`compareToIgnoreCase`), số (`>`, `<`), hướng sắp xếp (`boolean ascending`)

### 4. Abstraction (Trừu tượng)

- `User.java` là **abstract class** — không thể khởi tạo trực tiếp
- Repository Interfaces (`ICategoryRepository`, `IMovieRepository`, `IUserRepository`) — Controller chỉ phụ thuộc interface
- **Dependency Inversion Principle** (SOLID) — tầng trên không phụ thuộc tầng dưới cụ thể

---

## 📊 Thuật toán & Cấu trúc dữ liệu

### Thuật toán tự cài đặt

| Thuật toán | File | Độ phức tạp | Mô tả |
|:---|:---|:---|:---|
| **Bubble Sort** | `SortUtils.java` | O(n²) | Sắp xếp theo tên, rating, năm, popularity. Hỗ trợ tăng/giảm dần |
| **Linear Search** | `SearchUtils.java` | O(n) | Tìm kiếm theo tên phim, diễn viên, đạo diễn, thể loại (substring match, case-insensitive) |
| **Advanced Filter** | `FilterUtils.java` | O(n) | Lọc kết hợp nhiều điều kiện (năm, rating, đạo diễn, thể loại) với logic AND |
| **Auto Ranking** | `MovieController.java` | O(n²) | Công thức: `score = rating×10 + views×0.01 + favouritesCount×0.5` |

### Cấu trúc dữ liệu tự cài đặt

| Cấu trúc | File | Mô tả |
|:---|:---|:---|
| **Custom Stack\<T\>** | `CustomStack.java` | Stack (LIFO) generic bằng Linked Node. Hỗ trợ `push`, `pop`, `peek`, `clear`, `isEmpty`, `size` — tất cả O(1). Dùng cho Undo/Redo Watchlist |

---

## 💡 Kỹ thuật nổi bật

### Validate 3 lớp (Bulletproof Validation)

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   MODEL LAYER   │    │   VIEW LAYER    │    │ REPOSITORY LAYER│
│                 │    │                 │    │                 │
│ • Setter guards │    │ • InputValidator│    │ • Graceful      │
│ • Null/blank    │    │ • Loop until    │    │   Degradation   │
│ • Range check   │    │   valid input   │    │ • Skip corrupted│
│ • Throw custom  │    │ • Scanner fix   │    │   lines (log)   │
│   Exception     │    │ • Cancel option │    │ • UTF-8 encoding│
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Soft Delete & Restore

Tất cả entity (Category, Movie, User) đều áp dụng trường `boolean isActive`. Xoá dữ liệu = `setActive(false)`, giữ nguyên bản ghi trong file, dễ dàng khôi phục (Restore) khi cần thiết.

### Atomicity trong Update

```java
// MovieController.updateMovie() — Ngăn Data Corruption
Movie temp = new Movie(original);   // Tạo bản sao tạm
temp.setTitle(newTitle);            // Nếu sai → throw Exception → dừng
temp.setRating(newRating);          // Nếu sai → throw Exception → dừng
// ... tất cả hợp lệ → áp dụng lên bản gốc
original.setTitle(newTitle);
original.setRating(newRating);
```

### Foreign Key Validation liên Controller

- **Tạo phim**: Kiểm tra `categoryId` tồn tại và active (qua `CategoryController`)
- **Xóa thể loại**: Kiểm tra không có phim nào đang tham chiếu (qua `MovieController`)
- **Thao tác Watchlist/Favourites**: Kiểm tra `movieId` tồn tại và active (qua `MovieController`)

### Xử lý Unicode & Tiếng Việt

- Biên dịch với `-encoding UTF-8`
- `System.setOut(new PrintStream(..., "UTF-8"))` cho output
- `Scanner(System.in, "UTF-8")` cho input
- `TextUI.java` tính toán **display width** chính xác (emoji = 2 cột, combining mark = 0) để căn lề khung menu

### Xuất báo cáo CSV chuẩn RFC 4180

- Cung cấp tính năng xuất **Lịch sử xem phim** ra file `.csv`.
- Logic tự cài đặt hoàn toàn (không dùng thư viện): tự động bao bọc dữ liệu văn bản bằng dấu `"` và escape dấu `"` bên trong chuỗi (bằng cách nhân đôi `""`) đảm bảo tuân thủ nghiêm ngặt định dạng RFC 4180.

---

## 📦 Dữ liệu mẫu

Hệ thống đi kèm bộ dữ liệu mẫu phong phú:

| Loại | Số lượng | Mô tả |
|:---|:---|:---|
| **Thể loại** | 6 | Hành Động, Tình Cảm, Kinh Dị, Hài Hước, Khoa Học Viễn Tưởng, Hoạt Hình |
| **Phim** | 60 | Phim Việt Nam & Quốc tế (1990–2024), bao gồm lượt xem và lượt thích |
| **Tài khoản** | Nhiều | Admin và Customer mẫu với dữ liệu watchlist/favourites/history |

---

## 👤 Tác giả

**Đỗ Thiết Thạch** — Sinh viên Đại học FPT

- GitHub: [@thach09](https://github.com/thach09)

---

<div align="center">

**⭐ Nếu dự án hữu ích, hãy cho mình một Star nhé!**

*Dự án môn Lập trình Hướng đối tượng (OOP) — Xây dựng bằng Pure Java*

</div>