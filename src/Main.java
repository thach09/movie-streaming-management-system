import controller.CategoryController;
import controller.MovieController;
import repository.CategoryRepository;
import repository.MovieRepository;

public class Main {
    public static void main(String[] args) {
        // Khởi tạo Repositories và Controllers thông qua Dependency Injection
        CategoryController categoryController = new CategoryController(new CategoryRepository());
        MovieController movieController = new MovieController(new MovieRepository(), categoryController);

        // Liên kết ngược để CategoryController có thể check FK
        categoryController.setMovieController(movieController); 
    }
}
