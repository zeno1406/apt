package INTERFACE;

import java.util.ArrayList;

public interface IBUS <T, K> {
    ArrayList<T> getAll();
    ArrayList<T> getAllLocal();
    boolean delete(K id);
    void loadLocal();
    boolean isLocalEmpty();
}
