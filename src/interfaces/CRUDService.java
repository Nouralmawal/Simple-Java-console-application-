package interfaces;

import java.util.List;

public interface CRUDService<T> {
    void create(T t);

    List<T> readAll();

    void update(T t);

    void delete(int id);
}
