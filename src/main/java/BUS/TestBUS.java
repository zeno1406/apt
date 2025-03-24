package BUS;

import DAL.EmployeeDAL;
import DAL.RoleDAL;
import DAL.RolePermissionDAL;
import DTO.*;
import FACTORY.RoleBuilder;
import SERVICE.PrintService;
import SERVICE.RolePermissionService;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TestBUS {
    public static void main(String[] args) {
        RoleBuilder builder = new RoleBuilder();
        RoleDTO salerStaff = builder.name("Nhân viên quản lý khoaaab").description("Nhân viên quản lý kho").salaryCoefficient(new BigDecimal(2.5)).build();

        // Cập nhật dữ liệu vào local
//        RoleBUS.getInstance().loadLocal();
//        RolePermissionBUS.getInstance().loadLocal();
        // Xem các Role
//        RoleBUS.getInstance().getAllRoleLocal().forEach(System.out::println);
//        ArrayList<RoleDTO> a = RoleBUS.getInstance().getAllRoleLocal();
//        a.add(salerStaff);
//        RoleBUS.getInstance().getAllRoleLocal().forEach(System.out::println);
        // Tạo role mới
//        RolePermissionService.getInstance().createRoleWithPermissions(salerStaff, 1);
        // Xóa role id
//        RoleService.getInstance().deleteRoleWithPermissions(2);
        // Xem chi tiết phân quyền của role id
//        RolePermissionBUS.getInstance().getAllRolePermissionByRoleIdLocal(1).forEach(System.out::println);

//        RolePermissionService.getInstance().printPermissionsGroupedByModule();
//        CategoryDTO test = new CategoryDTO(1, "test", true);
//        System.out.println(test);
//        CategoryDTO clone = test.toBuilder().build();
//        clone.setId(2);
//        System.out.println(test);
//        System.out.println(clone);
//        AccountBUS.getInstance().loadLocal();
//        AccountDTO account1 = new AccountDTO(0, "huyhoang119762", "huyhoang123");
//        AccountDTO account2= new AccountDTO(1, "huyhoang119763", "huyhoang123");
//
//        System.out.println(LoginRegisterService.getInstance().checkLogin(account1));
//        System.out.println(LoginRegisterService.getInstance().checkLogin(account2));

//        RolePermissionService.getInstance().printPermissionsGroupedByModule();
//
//        String dateTimeString = "2025-03-17 14:00:00";
//
//        // Định dạng của chuỗi
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//        // Chuyển đổi từ chuỗi sang LocalDateTime
//        LocalDateTime createDate = LocalDateTime.parse(dateTimeString, formatter);
//
//        // In ra kết quả
//        System.out.println("LocalDateTime từ database: " + createDate);
//        PrintService.getInstance().printInvoiceForm(2);
//        RoleBUS.getInstance().loadLocal();
//        RoleBUS.getInstance().getAllLocal().forEach(System.out::println);
//        RoleDTO salerStaffErr = new RoleDTO(1, "Nhân viê ý kho\"Nhân viê ý kho\"Nhân viê ý kho\"Nhân viê ý kho\"", "asdasd", new BigDecimal(2.5));

//        RoleBUS.getInstance().loadLocal();
//        RolePermissionBUS.getInstance().loadLocal();
//        RoleBUS.getInstance().getAllLocal().forEach(System.out::println);
//        RolePermissionBUS.getInstance().getAllLocal().forEach(System.out::println);
//        System.out.println(RolePermissionService.getInstance().deleteRoleWithPermissions(1, 1));
//
//        RoleBUS.getInstance().getAllLocal().forEach(System.out::println);
//        RolePermissionBUS.getInstance().getAllLocal().forEach(System.out::println);

//        System.out.printf(RoleBUS.getInstance().getByIdLocal(8).toString());
//
//        RolePermissionBUS.getInstance().getAllRolePermissionByRoleIdLocal(8).forEach(System.out::println);


        EmployeeBUS.getInstance().loadLocal();
        EmployeeBUS.getInstance().getAllLocal().forEach(System.out::println);

    }


}
