import controller.CategoryController;
import controller.MovieController;
import controller.UserController;
import controller.CustomerController;
import repository.CategoryRepository;
import repository.MovieRepository;
import repository.UserRepository;

public class Main {
    public static void main(String[] args) {
        // Khởi tạo Repositories và Controllers thông qua Dependency Injection
        CategoryController categoryController = new CategoryController(new CategoryRepository());
        MovieController movieController = new MovieController(new MovieRepository(), categoryController);

        // Liên kết ngược để CategoryController có thể check FK
        categoryController.setMovieController(movieController); 

        // UserController phải được tạo TRƯỚC CustomerController
        UserController userController = new UserController(new UserRepository());
        CustomerController customerController = new CustomerController(userController, movieController);
    }
}
