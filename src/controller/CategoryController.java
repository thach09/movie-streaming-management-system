package controller;

import model.Category;
import repository.ICategoryRepository;
import utils.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class CategoryController {
    private List<Category> categoryList;
    private ICategoryRepository categoryRepository;
    private MovieController movieController; // Có thể null nếu chưa được set

    public CategoryController(ICategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryList = categoryRepository.loadAll();
    }

    public void setMovieController(MovieController movieController) {
        this.movieController = movieController;
    }

    public boolean addCategory(String id, String name, String description) throws ValidationException {
        // Kiểm tra trùng ID
        for (Category cat : categoryList) {
            if (cat.getId().equalsIgnoreCase(id)) {
                throw new ValidationException("Mã danh mục '" + id + "' đã tồn tại!");
            }
        }

        // Kiểm tra trùng Tên danh mục (case-insensitive)
        for (Category cat : categoryList) {
            if (cat.getName().equalsIgnoreCase(name)) {
                throw new ValidationException("Tên danh mục '" + name + "' đã tồn tại!");
            }
        }


        Category newCategory = new Category(id, name, description);
        categoryList.add(newCategory);
        return categoryRepository.saveAll(categoryList);
    }

    public boolean updateCategory(String id, String newName, String newDescription) throws ValidationException {
        Category category = findReferenceById(id);
        if (category == null) {
            throw new ValidationException("Không tìm thấy danh mục có ID '" + id + "'");
        }

        // Kiểm tra trùng Tên danh mục với danh mục khác (loại trừ chính mình)
        for (Category cat : categoryList) {
            if (!cat.getId().equalsIgnoreCase(id) && cat.getName().equalsIgnoreCase(newName)) {
                throw new ValidationException("Tên danh mục '" + newName + "' đã tồn tại ở danh mục khác!");
            }
        }


        // 1. Tạo bản sao tạm để test Validation
        Category temp = new Category(category);
        temp.setName(newName);
        temp.setDescription(newDescription);

        // 2. Nếu qua hết các Setter không lỗi, mới cập nhật vào Object thật
        category.setName(newName);
        category.setDescription(newDescription);
        
        return categoryRepository.saveAll(categoryList);
    }

    public boolean deleteCategory(String id) throws ValidationException {
        Category category = findReferenceById(id);
        if (category == null) {
            throw new ValidationException("Không tìm thấy danh mục có ID '" + id + "'");
        }
        if (!category.isActive()) {
            throw new ValidationException("Danh mục có ID '" + id + "' đã bị xóa trước đó!");
        }
        if (movieController != null && movieController.existsActiveMovieInCategory(id)) {
            throw new ValidationException("Không thể xóa danh mục vì còn phim đang active tham chiếu tới!");
        }
        category.setActive(false); // Soft Delete: đánh dấu inactive thay vì xóa khỏi danh sách
        return categoryRepository.saveAll(categoryList);
    }

    // Dành cho Admin: Lấy toàn bộ danh mục (kể cả inactive)
    public List<Category> getAllCategories() {
        List<Category> copies = new ArrayList<>();
        for (Category cat : categoryList) {
            copies.add(new Category(cat));
        }
        return copies;
    }

    // Dành cho Customer: Chỉ lấy danh mục đang active
    public List<Category> getActiveCategories() {
        List<Category> activeCopies = new ArrayList<>();
        for (Category cat : categoryList) {
            if (cat.isActive()) {
                activeCopies.add(new Category(cat));
            }
        }
        return activeCopies;
    }

    private Category findReferenceById(String id) {
        for (Category cat : categoryList) {
            if (cat.getId().equalsIgnoreCase(id)) {
                return cat;
            }
        }
        return null;
    }

    public Category findById(String id) {
        Category cat = findReferenceById(id);
        return cat != null ? new Category(cat) : null;
    }

    // --- TRENDING CATEGORIES (GIAI ĐOẠN 4) ---

    /**
     * Trả về danh sách Category active sắp theo tổng views giảm dần.
     * Đếm tổng views của Movie active thuộc mỗi Category qua MovieController.
     * Dùng thuật toán Bubble Sort tự viết (không dùng Collections.sort).
     * Yêu cầu: movieController phải đã được set trước khi gọi.
     */
    public List<Category> getTrendingCategories() {
        List<Category> activeCategories = getActiveCategories(); // Đã là bản sao deep copy

        if (movieController == null || activeCategories.isEmpty()) {
            return activeCategories;
        }

        // Tính tổng views cho mỗi category và lưu vào mảng song song
        long[] viewCounts = new long[activeCategories.size()];
        for (int i = 0; i < activeCategories.size(); i++) {
            viewCounts[i] = movieController.getTotalViewsByCategory(activeCategories.get(i).getId());
        }

        // Bubble Sort giảm dần theo viewCounts — swap cả Category và viewCount
        int n = activeCategories.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (viewCounts[j] < viewCounts[j + 1]) {
                    // Swap viewCounts
                    long tempCount = viewCounts[j];
                    viewCounts[j] = viewCounts[j + 1];
                    viewCounts[j + 1] = tempCount;

                    // Swap Category tương ứng
                    Category tempCat = activeCategories.get(j);
                    activeCategories.set(j, activeCategories.get(j + 1));
                    activeCategories.set(j + 1, tempCat);
                }
            }
        }

        return activeCategories;
    }
}