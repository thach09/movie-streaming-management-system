package model;

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

    // 3. Constructor đầy đủ tham số (Giúp khởi tạo đối tượng nhanh gọn)
    public Category(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isActive = true;
    }

    // 4. Getters & Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã danh mục (ID) không được để trống!");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống!");
        }
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
