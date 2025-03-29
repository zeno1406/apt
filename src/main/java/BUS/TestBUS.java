package BUS;

import DAL.EmployeeDAL;
import DAL.RoleDAL;
import DAL.RolePermissionDAL;
import DTO.*;
import FACTORY.RoleBuilder;
import SERVICE.LoginService;
import SERVICE.PrintService;
import SERVICE.RolePermissionService;
import UTILS.AvailableUtils;
import UTILS.PasswordUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;


import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.function.BinaryOperator;

public class TestBUS {
    public static void main(String[] args) {
        RolePermissionBUS.getInstance().loadLocal();
        EmployeeBUS.getInstance().loadLocal();
        AccountBUS.getInstance().loadLocal();
        RoleBuilder builder = new RoleBuilder();
        RoleDTO salerStaff = builder.name("Nhân viên quản lý kho a").description("Nhân viên quản lý kho").salaryCoefficient(new BigDecimal(2.5)).build();

        // Test tạo role mới - test bằng quyền role 1
//        Boolean result = RolePermissionService.getInstance().createRoleWithPermissions(salerStaff, 1);
//        System.out.println(result);

        // Test phân quyền bằng role không có quyền
//        RolePermissionDTO rolePermission = new RolePermissionDTO(1, 1,  false);

//        Boolean result1 = RolePermissionBUS.getInstance().update(rolePermission, 1);

//        EmployeeDTO employee = new EmployeeDTO(2 , "Thanh", "Điền aaasdsd abasdaa", new BigDecimal(3), "", null, 4, false);
//        //EmployeeBUS.getInstance().insert(employee, 1, 1);
//
//        boolean result1 = EmployeeBUS.getInstance().update(employee, 1, 1);
//
//        System.out.println(result1);


//        RolePermissionService.getInstance().createRoleWithPermissions(salerStaff, 1, 1);
                EmployeeDTO employee = new EmployeeDTO(2 , "Thanh", "Điền aaasdsd abasdaa", new BigDecimal(3), null, 2, false);

        RolePermissionService.getInstance().deleteRoleWithPermissions(2, 1, 1);
//        boolean result = EmployeeBUS.getInstance().delete(2, 1, 1);
//        boolean result = EmployeeBUS.getInstance().insert(employee, 0, 2);
//        System.out.println(result);

        AccountDTO account2 = new AccountDTO(1, "thanhlong", "huyhoang123");
//        System.out.println(AccountBUS.getInstance().update(account2, 1, 1));
//        System.out.println(LoginService.getInstance().checkLogin(account2));
//        System.out.println(EmployeeBUS.getInstance().getByIdLocal(2).isStatus());
//        RolePermissionService.getInstance().printPermissionsGroupedByModule();

    }

}
