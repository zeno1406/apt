package BUS;

import DAL.StatisticDAL;
import DTO.StatisticQuarterDTO;

import java.time.LocalDate;
import java.util.ArrayList;

public class StatisticBUS {
    private static final StatisticBUS INSTANCE = new StatisticBUS();

    private StatisticBUS() {}

    public static StatisticBUS getInstance() {
        return INSTANCE;
    }

    public ArrayList<StatisticQuarterDTO> getStatisticRevenueByDate(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("Ng+�y bߦ�t -�ߦ�u v+� kߦ+t th+�c kh+�ng -榦�+�c -��+� tr�+�ng.");
        }

        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("Ng+�y bߦ�t -�ߦ�u kh+�ng -榦�+�c l�+�n h��n ng+�y kߦ+t th+�c.");
        }

        return StatisticDAL.getInstance().getStatisticRevenueByDate(fromDate, toDate);
    }

    public ArrayList<StatisticQuarterDTO> getStatisticRevenueByMonth(int year) {
        if (year < 2000 || year > LocalDate.now().getYear()) {
            throw new IllegalArgumentException("N-�m th�+�ng k+� kh+�ng h�+�p l�+�.");
        }

        return StatisticDAL.getInstance().getStatisticRevenueByMonth(year);
    }

    public ArrayList<StatisticQuarterDTO> getStatisticRevenueByYearRange(int fromYear, int toYear) {
        int currentYear = LocalDate.now().getYear();

        if (fromYear < 2000 || toYear > currentYear) {
            throw new IllegalArgumentException("Khoߦ�ng n-�m th�+�ng k+� kh+�ng h�+�p l�+�.");
        }

        if (fromYear > toYear) {
            throw new IllegalArgumentException("N-�m bߦ�t -�ߦ�u kh+�ng -榦�+�c l�+�n h��n n-�m kߦ+t th+�c.");
        }

        return StatisticDAL.getInstance().getStatisticRevenueByYearRange(fromYear, toYear);
    }
}