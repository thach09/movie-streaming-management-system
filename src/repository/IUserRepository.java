package repository;

import model.User;
import java.util.List;

public interface IUserRepository {
    boolean saveAll(List<User> userList);
    List<User> loadAll();
}
