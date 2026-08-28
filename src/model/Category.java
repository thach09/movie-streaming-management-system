package model;

import utils.ValidationException;
import java.util.Objects;

public class Category {
    // 1. Encapsulation: các thuộc tính private để bảo vệ dữ liệu
    private String id;
    private String name;
    private String description;
    private boolean isActive;

    // 2. Constructor không tham số (Mặc định)
    public Category() {
        this.isActive = true;
    }

    // Copy constructor để tạo bản sao (deep copy) bảo vệ dữ liệu nội bộ
    public Category(Category source) {
        this.id = source.id;
        this.name = source.name;
        this.description = source.description;
        this.isActive = source.isActive;
    }

    // 3. Constructor đầy đủ tham số (Giúp khởi tạo đối tượng nhanh gọn)
    public Category(String id, String name, String description) throws ValidationException {
        setId(id);
        setName(name);
        setDescription(description);
        this.isActive = true;
    }

    // 4. Getters & Setters
    public String getId() {
        return id;
    }

    public void setId(String id) throws ValidationException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Mã danh mục (ID) không được để trống!");
        }
        if (id.contains("|")) throw new ValidationException("ID không được chứa ký tự '|'");
        if (id.contains(",")) throw new ValidationException("ID không được chứa ký tự ','");
        this.id = id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Tên danh mục không được để trống!");
        }
        if (name.contains("|")) throw new ValidationException("Tên danh mục không được chứa ký tự '|'");
        this.name = name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) throws ValidationException {
        if (description != null && description.contains("|")) {
            throw new ValidationException("Mô tả không được chứa ký tự '|'");
        }
        this.description = (description != null) ? description.trim() : null;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    // 5. Override hàm toString() để in thông tin Category ra màn hình dễ đọc
    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + (description != null ? description : "(không có)") + '\'' +
                ", isActive=" + isActive +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
