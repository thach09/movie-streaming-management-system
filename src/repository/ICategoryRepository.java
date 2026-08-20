package repository;

import model.Category;
import java.util.List;

public interface ICategoryRepository {
    boolean saveAll(List<Category> categoryList);
    List<Category> loadAll();
}
