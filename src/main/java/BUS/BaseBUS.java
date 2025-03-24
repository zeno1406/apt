package BUS;

import INTERFACE.IBUS;
import SERVICE.AuthorizationService;

import java.util.ArrayList;

public abstract class BaseBUS<T, K> implements IBUS<T, K> {
    protected final ArrayList<T> arrLocal = new ArrayList<>();

    @Override
    public abstract ArrayList<T> getAll();

    @Override
    public ArrayList<T> getAllLocal() {
        return new ArrayList<>(arrLocal);
    }

    @Override
    public abstract boolean delete(K id, int employee_roleId);

    @Override
    public void loadLocal() {
        arrLocal.clear();
        arrLocal.addAll(getAll());
    }

    @Override
    public boolean isLocalEmpty() {
        return arrLocal.isEmpty();
    }

    protected boolean hasPermission(int userId, int permissionId) {
        return AuthorizationService.getInstance().hasPermission(userId, permissionId);
    }
}
