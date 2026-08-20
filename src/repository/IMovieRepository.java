package repository;

import model.Movie;
import java.util.List;

public interface IMovieRepository {
    boolean saveAll(List<Movie> movieList);
    List<Movie> loadAll();
}
