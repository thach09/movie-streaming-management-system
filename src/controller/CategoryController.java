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
        
        Category newCategory = new Category(id, name, description);
        categoryList.add(newCategory);
        return categoryRepository.saveAll(categoryList);
    }

    public boolean updateCategory(String id, String newName, String newDescription) throws ValidationException {
        Category category = findReferenceById(id);
        if (category == null) {
            throw new ValidationException("Không tìm thấy danh mục có ID '" + id + "'");
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
        if (category != null) {
            // Chỉ check ràng buộc nếu movieController đã được gán
            if (movieController != null && movieController.existsActiveMovieInCategory(id)) {
                throw new ValidationException("Không thể xóa danh mục vì còn phim đang active tham chiếu tới!");
            }
            categoryList.remove(category); // Sử dụng equals() đã override trong Category
            return categoryRepository.saveAll(categoryList);
        }
        return false;
    }

    public List<Category> getAllCategories() {
        return new ArrayList<>(categoryList); // Trả về bản sao để bảo vệ tính đóng gói
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
}