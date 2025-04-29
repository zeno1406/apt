package BUS;

import DAL.StatisticDAL;
import DTO.StatisticDTO;

import java.time.LocalDate;
import java.util.ArrayList;

public class StatisticBUS {
    private static final StatisticBUS INSTANCE = new StatisticBUS();

    private StatisticBUS() {}

    public static StatisticBUS getInstance() {
        return INSTANCE;
    }

    public ArrayList<StatisticDTO.ProductRevenue> getProductRevenue(LocalDate start, LocalDate end) {
        LocalDate today = java.time.LocalDate.now();
        if(start.isAfter(today) || end.isAfter(today) || start.isAfter(end) || end.isBefore(start) ) {
            throw new IllegalArgumentException("Khoảng thời gian không hợp lệ.");
        }
        return (ArrayList<StatisticDTO.ProductRevenue>) StatisticDAL.getInstance().getProductRevenue(start,end);
    }

    public ArrayList<StatisticDTO.QuarterlyEmployeeRevenue> getQuarterlyEmployeeRevenue(int year) {
        int currentYear = java.time.LocalDate.now().getYear();
        if (year < 2000 || year > currentYear) {
            throw new IllegalArgumentException("Năm thống kê không hợp lệ.");
        }
        return (ArrayList<StatisticDTO.QuarterlyEmployeeRevenue>) StatisticDAL.getInstance().getQuarterlyEmployeeRevenue(year);
    }
}