package DAL;
import BUS.RoleBUS;
import BUS.RolePermissionBUS;
import DTO.ModuleDTO;
import DTO.RoleDTO;
import FACTORY.RoleBuilder;
import SERVICE.RolePermissionService;

import java.math.BigDecimal;


public class TestDal {
    public static void main(String[] args) {
        // Khởi tạo
//        RoleDTO role = new RoleDTO(0, "Hoàng test", "Test", new BigDecimal("3"));
//        RoleDTO role1 = new RoleDTO(1, "Admin 2", "Admin xx", new BigDecimal("123.45"));

        ModuleDTO role = new ModuleDTO(0, "aihi");
        ModuleDTO role1 = new ModuleDTO(12, "aihi");

        // Code kiểm tra insert
//        boolean insertResult = RoleDAL.getInstance().insert(role);
//        boolean insertResult = ModuleDAL.getInstance().insert(role);
//        System.out.println(insertResult);

        // Code kiểm tra update
//        boolean updateResult = RoleDAL.getInstance().update(role1);
//        System.out.println(updateResult);

        // Code kiểm tra tìm kiếm theo ID
//        RoleDTO getByIdResult = RoleDAL.getInstance().getById(role1.getId());
//        ModuleDTO getByIdResult = ModuleDAL.getInstance().getById(role1.getId());
//        System.out.println(getByIdResult.getId() + " " + getByIdResult.getName() + " " + getByIdResult.getDescription() + " " + getByIdResult.getSalaryCoefficient());
//        System.out.println(getByIdResult.getId() + " " + getByIdResult.getName());


        // Code kiểm tra lấy toàn bộ
//        ArrayList<PermissionDTO> getAllResult = PermissionDAL.getInstance().getAll();
//        for (PermissionDTO x : getAllResult) {
////            System.out.println(x.getId() + " " + x.getName() + " " + x.getDescription() + " " + x.getSalaryCoefficient());
//            System.out.println(x.getId() + " " + x.getName());
//        }

        // Code kiểm tra delete
//        boolean deleteResult = RoleDAL.getInstance().delete(role1.getId());
//        System.out.println(deleteResult);


        // Kiểm tra chức năng tạo Role mới
        RoleBuilder builder = new RoleBuilder();
        RoleDTO salerStaff = builder.name("Nhân viên quản lý kho 1").description("Nhân viên quản lý kho 1").salaryCoefficient(new BigDecimal(2.5)).build();
        RolePermissionService.getInstance().createRoleWithPermissions(salerStaff);

        // Xem các Role
        RoleBUS.getInstance().getAll().forEach(System.out::println);

        // Xem chi tiết phân quyền của Role by RoleId
        System.out.println(RolePermissionBUS.getInstance().getAllRolePermissionByRoleIdLocal(1));

        // Kiểm tra coi có quyền không
//        System.out.println(RolePermissionBUS.getInstance().hasPermission(1, 1));
    }
}
