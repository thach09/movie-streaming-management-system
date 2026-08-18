import model.Category;
import repository.CategoryRepository;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        CategoryRepository repo = new CategoryRepository();
        
        // 1. Tạo thử 2 danh mục trên RAM
        List<Category> list = new ArrayList<>();
        list.add(new Category("C01", "Hanh Dong", "Phim co nhieu canh chay no"));
        list.add(new Category("C02", "Hai Huoc", "Phim xem de cuoi"));
        
        // 2. Lưu danh sách xuống ổ cứng
        boolean isSaved = repo.saveAll(list);
        if (isSaved) {
            System.out.println("✅ Đã lưu thành công xuống file: data/categories.txt");
        }
        
        // 3. Đọc ngược lại từ ổ cứng lên RAM
        System.out.println("\n--- Dữ liệu đọc lên từ file ---");
        List<Category> loadedList = repo.loadAll();
        for (Category c : loadedList) {
            System.out.println(c.toString());
        }
    }
}
