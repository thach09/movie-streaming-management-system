package repository;

import model.Category;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {

    private static final String FILE_PATH = "data/categories.txt";

    private static final String DELIMITER = "|";

    // Lưu danh sách Category xuống file
    public boolean saveAll(List<Category> categoriesList) {
        File file = new File(FILE_PATH);
        // Tự động tạo thư mục chưa có
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        // Try-with-resources: mở BufferedWriter để ghi file (tự động đóng file)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Category cat : categoriesList) {
                // Ghép các thuộc tính lại thành một chuỗi và ngăn cách bởi DELIMITER |
                              String desc = cat.getDescription();
                              String line = cat.getId() + DELIMITER +
                              cat.getName() + DELIMITER +
                              (desc != null ? desc : "") + DELIMITER +
                              cat.isActive();
                bw.write(line); // ghi chuỗi vào file
                bw.newLine(); // xuống dòng và ghi Category tiếp theo
            }
            return true;
        
        } catch (IOException e) { // Nếu ổ cứng bị đầy hoặc file bị khóa thì báo lỗi
            System.err.println("Lỗi ghi file categories: " + e.getMessage());
            return false;
        }
        
    }

    // Đọc dữ liệu từ file lên RAM
    public List<Category>  loadAll() {
        List<Category> categoriesList = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) { 
            return categoriesList;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line= br.readLine()) != null) { 
                // Graceful Degradation
                try {
                    String[] parts = line.split("\\|", -1);
                    
                    // Cắt chuỗi bằng dấu | (phải có \\ vì | là ký tự đặc biệt)
                    if (parts.length >= 4) {
                        String id = parts[0];
                        String name = parts[1];
                        String description = parts[2];
                        boolean isActive = Boolean.parseBoolean(parts[3]);

                        // Tạo đối tượng Category và set trạng thái Active
                        Category cat = new Category(id, name, description);
                        cat.setActive(isActive); 
                        categoriesList.add(cat);
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi đọc file categories - bỏ qua dòng: " + line);
                }

            
            }
        } catch (IOException e) {
            System.err.println("Lỗi đọc file categories: " + e.getMessage());
        }
        return categoriesList;
    }
    
}
