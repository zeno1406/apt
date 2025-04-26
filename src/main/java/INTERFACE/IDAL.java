package INTERFACE;

import java.util.ArrayList;

public interface IDAL <T, K> {
    ArrayList<T> getAll();
    T getById(K id);
    boolean insert(T obj);
    boolean update(T obj);
    boolean delete(K id);
}
