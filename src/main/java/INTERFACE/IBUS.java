package INTERFACE;

import java.util.ArrayList;

public interface IBUS <T, K> {
    ArrayList<T> getAll();
    ArrayList<T> getAllLocal();
    void loadLocal();
    boolean isLocalEmpty();
}
