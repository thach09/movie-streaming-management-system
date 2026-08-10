package model;

public class Category {
    // 1. Encapsulation: các thuộc tính private để bảo vệ dữ liệu
    private String id;
    private String name;
    private String description;

    // 2. Constructor không tham số (Mặc định)
    public Category() {
    }

    // 3. Constructor đầy đủ tham số (Giúp khởi tạo đối tượng nhanh gọn)
    public Category(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // 4. Getters & Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // 5. Override hàm toString() để in thông tin Category ra màn hình dễ đọc
    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
