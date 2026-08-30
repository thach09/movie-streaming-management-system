package repository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import model.Admin;
import model.Customer;
import model.User;
import model.WatchProgress;

public class UserRepository implements IUserRepository {
    private static final String FILE_PATH = "data/users.txt";
    private static final String DELIMITER = "|";
    private static final String LIST_DELIMITER = ",";

    // Hàm chuyển 1 List<String> thành chuỗi cách nhau bởi dấu phẩy
    private String joinList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return ""; // Trả về chuỗi rỗng khi không có dữ liệu
        }
        return String.join(LIST_DELIMITER, list); // Nối chuỗi khi có dữ liệu
    }

    // Hàm chuyển List<WatchProgress> thành chuỗi "movieId:percent,movieId:percent"
    private String joinProgressList(List<WatchProgress> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(LIST_DELIMITER);
            sb.append(list.get(i).getMovieId()).append(":").append(list.get(i).getPercent());
        }
        return sb.toString();
    }

    // Hàm lưu toàn bộ User xuống file
    public boolean saveAll(List<User> usersList) {
        File file = new File(FILE_PATH);

        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            for (User user : usersList) { // Duyệt qua từng thư mục User 
                // User Admin or Customer
                // StringBuilder để nối các thuộc tính basic giống nhau
                StringBuilder line = new StringBuilder();
                line.append(user.getId()).append(DELIMITER) // Method Chaining
                    .append(user.getUsername()).append(DELIMITER)
                    .append(user.getPassword()).append(DELIMITER)
                    .append(user.getFullName()).append(DELIMITER)
                    .append(user.getEmail()).append(DELIMITER)
                    .append(user.getRole()).append(DELIMITER)
                    .append(user.isActive());

                // Kiểm tra User có là Customer không
                if (user instanceof Customer) {
                    Customer cus = (Customer) user; // Ép kiểu từ User sang Customer
                    // Nối 3 danh sách phim vào cuối chuỗi
                    line.append(DELIMITER).append(joinList(cus.getWatchlist()))
                        .append(DELIMITER).append(joinList(cus.getFavouriteList()))
                        .append(DELIMITER).append(joinList(cus.getWatchHistory()))
                        .append(DELIMITER).append(joinProgressList(cus.getContinueWatching()));
                }

                // Ghi chuỗi xuống file
                bw.write(line.toString());
                bw.newLine(); 
            } 
            return true;
        } catch (IOException e) {
            System.err.println("Lỗi ghi file: " + e.getMessage());
            return false;
        }
    }

    // Hàm phụ trợ: biến chuỗi M01, M02 thành List<String>
    private List<String> parseList(String data) {
        List<String> list = new ArrayList<>();
        if (data == null || data.trim().isEmpty()) {
            return list;
        }
        String[] items = data.split(LIST_DELIMITER);
        for (String item : items) {
            list.add(item.trim());
        }
        return list;     
    }

    // Hàm phụ trợ: biến chuỗi "M01:45,M03:80" thành List<WatchProgress>
    private List<WatchProgress> parseProgressList(String data) {
        List<WatchProgress> list = new ArrayList<>();
        if (data == null || data.trim().isEmpty()) {
            return list;
        }
        String[] items = data.split(LIST_DELIMITER);
        for (String item : items) {
            try {
                String trimmed = item.trim();
                int colonIdx = trimmed.indexOf(':');
                if (colonIdx <= 0 || colonIdx >= trimmed.length() - 1) continue;
                String movieId = trimmed.substring(0, colonIdx);
                int percent = Integer.parseInt(trimmed.substring(colonIdx + 1));
                list.add(new WatchProgress(movieId, percent));
            } catch (Exception e) {
                // Graceful degradation: bỏ qua phần tử lỗi
            }
        }
        return list;
    }

    // Hàm đọc dữ liệu từ file lên RAM
    public List<User> loadAll() {
        List<User> userList = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        if (!file.exists()) {
            return userList;
        }
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    // Cắt chuỗi theo ký tự |
                    String[] parts = line.split("\\|", -1);
                    
                    if (parts.length >= 7) { // User basically cần 7 thuộc tính (ID, Username, Pass, Name, Email, Role, isActive)
                        String id = parts[0];
                        String username = parts[1];
                        String password = parts[2];
                        String fullName = parts[3];
                        String email = parts[4];
                        String role = parts[5];
                        boolean isActive = Boolean.parseBoolean(parts[6].trim());

                        User user = null;

                        // Tính đa hình khi khởi tạo (FACTORY PATTERN đơn giản)
                        if (role.equals("ADMIN")) {
                            user = new Admin(id, username, password, fullName, email);
                        } else if (role.equals("CUSTOMER") && parts.length >= 10) {
                            // Customer có tối thiểu 10 cột (3 danh sách), cột 11 (continueWatching) là tùy chọn
                            List<String> watchlist = parseList(parts[7]);
                            List<String> favouritelist = parseList(parts[8]);
                            List<String> watchHistorylist = parseList(parts[9]);

                            Customer customer = new Customer(id, username, password, fullName, email, watchlist, favouritelist, watchHistorylist);

                            // Tương thích ngược: chỉ đọc cột 11 nếu có
                            if (parts.length >= 11) {
                                customer.setContinueWatching(parseProgressList(parts[10]));
                            }

                            user = customer;
                    }

                    // Khởi tạo thành công sẽ thêm vào danh sách
                    if (user != null) {
                        user.setActive(isActive);
                        userList.add(user);
                    }
                }
                
            } catch (Exception e) {
                System.err.println("Caution: Bỏ qua dòng lỗi trong users.txt - " + line);

            } 
        }
    
        } catch ( IOException e) {
            e.printStackTrace();
        }
        return userList;
    }

}    

