package controller;

import model.Category;
import repository.CategoryRepository;
import utils.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class CategoryController {
    private List<Category> categoryList;
    private CategoryRepository categoryRepository;

    public CategoryController() {
        this.categoryRepository = new CategoryRepository();
        this.categoryList = categoryRepository.loadAll();
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
        
        category.setName(newName);
        category.setDescription(newDescription);
        return categoryRepository.saveAll(categoryList);
    }

    public boolean deleteCategory(String id) {
        Category category = findReferenceById(id);
        if (category != null) {
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