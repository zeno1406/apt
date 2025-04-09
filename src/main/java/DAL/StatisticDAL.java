
package DAL;

import DTO.StatisticRevenue;
import INTERFACE.ConnectionFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class StatisticDAL {
    private ConnectionFactory connectionFactory = ConnectApplication.getInstance().getConnectionFactory();
    private static final StatisticDAL INSTANCE = new StatisticDAL();
    private StatisticDAL() {

    }

    public static StatisticDAL getInstance() {
        return INSTANCE;
    }

    public ArrayList<StatisticRevenue> getStatisticRevenueByDate(LocalDate fromDate, LocalDate toDate) {
        ArrayList<StatisticRevenue> list = new ArrayList<>();

        final String query = """
                            SELECT 
                                d.date AS time,
                                COALESCE(r.total_revenue, 0) AS total_revenue,
                                COALESCE(c.total_cost, 0) AS total_cost,
                                COALESCE(r.total_revenue, 0) - COALESCE(c.total_cost, 0) AS total_profit
                            FROM (
                                SELECT DISTINCT DATE(create_date) AS date
                                FROM invoice
                                WHERE DATE(create_date) BETWEEN ? AND ?
                                UNION
                                SELECT DISTINCT DATE(create_date) AS date
                                FROM import
                                WHERE DATE(create_date) BETWEEN ? AND ?
                            ) d
                            LEFT JOIN (
                                SELECT DATE(create_date) AS date, SUM(total_price) AS total_revenue
                                FROM invoice
                                WHERE DATE(create_date) BETWEEN ? AND ?
                                GROUP BY DATE(create_date)
                            ) r ON d.date = r.date
                            LEFT JOIN (
                                SELECT DATE(create_date) AS date, SUM(total_price) AS total_cost
                                FROM import
                                WHERE DATE(create_date) BETWEEN ? AND ?
                                GROUP BY DATE(create_date)
                            ) c ON d.date = c.date
                            ORDER BY d.date
                            """;

        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // G+�n tham s�+� cho c+�u query (8 lߦ�n t�+� v+� -�ߦ+n v+� d+�ng trong 4 ch�+� kh+�c nhau)
            for (int i = 1; i <= 8; i += 2) {
                statement.setDate(i, Date.valueOf(fromDate));
                statement.setDate(i + 1, Date.valueOf(toDate));
            }

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String time = rs.getString("time");
                    BigDecimal totalRevenue = rs.getBigDecimal("total_revenue");
                    BigDecimal totalCost = rs.getBigDecimal("total_cost");
                    BigDecimal totalProfit = rs.getBigDecimal("total_profit");

                    StatisticRevenue stat = new StatisticRevenue(time, totalRevenue, totalCost, totalProfit);
                    list.add(stat);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting statistic revenue: " + e.getMessage());
        }

        return list;
    }

    public ArrayList<StatisticRevenue> getStatisticRevenueByMonth(int year) {
        ArrayList<StatisticRevenue> list = new ArrayList<>();

        final String query = """
            SELECT 
                d.month AS time,
                COALESCE(r.total_revenue, 0) AS total_revenue,
                COALESCE(c.total_cost, 0) AS total_cost,
                COALESCE(r.total_revenue, 0) - COALESCE(c.total_cost, 0) AS total_profit
            FROM (
                SELECT DISTINCT DATE_FORMAT(create_date, '%Y-%m') AS month FROM invoice WHERE YEAR(create_date) = ?
                UNION
                SELECT DISTINCT DATE_FORMAT(create_date, '%Y-%m') AS month FROM import WHERE YEAR(create_date) = ?
            ) d
            LEFT JOIN (
                SELECT DATE_FORMAT(create_date, '%Y-%m') AS month, SUM(total_price) AS total_revenue
                FROM invoice WHERE YEAR(create_date) = ?
                GROUP BY DATE_FORMAT(create_date, '%Y-%m')
            ) r ON d.month = r.month
            LEFT JOIN (
                SELECT DATE_FORMAT(create_date, '%Y-%m') AS month, SUM(total_price) AS total_cost
                FROM import WHERE YEAR(create_date) = ?
                GROUP BY DATE_FORMAT(create_date, '%Y-%m')
            ) c ON d.month = c.month
            ORDER BY d.month
        """;

        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // G+�n 4 lߦ�n YEAR(?)
            for (int i = 1; i <= 4; i++) {
                statement.setInt(i, year);
            }

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String time = rs.getString("time");
                    BigDecimal totalRevenue = rs.getBigDecimal("total_revenue");
                    BigDecimal totalCost = rs.getBigDecimal("total_cost");
                    BigDecimal totalProfit = rs.getBigDecimal("total_profit");

                    list.add(new StatisticRevenue(time, totalRevenue, totalCost, totalProfit));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting monthly statistic revenue: " + e.getMessage());
        }

        return list;
    }

    public ArrayList<StatisticRevenue> getStatisticRevenueByYearRange(int fromYear, int toYear) {
        ArrayList<StatisticRevenue> list = new ArrayList<>();

        final String query = """
            SELECT 
                d.year AS time,
                COALESCE(r.total_revenue, 0) AS total_revenue,
                COALESCE(c.total_cost, 0) AS total_cost,
                COALESCE(r.total_revenue, 0) - COALESCE(c.total_cost, 0) AS total_profit
            FROM (
                SELECT DISTINCT YEAR(create_date) AS year FROM invoice WHERE YEAR(create_date) BETWEEN ? AND ?
                UNION
                SELECT DISTINCT YEAR(create_date) AS year FROM import WHERE YEAR(create_date) BETWEEN ? AND ?
            ) d
            LEFT JOIN (
                SELECT YEAR(create_date) AS year, SUM(total_price) AS total_revenue
                FROM invoice WHERE YEAR(create_date) BETWEEN ? AND ?
                GROUP BY YEAR(create_date)
            ) r ON d.year = r.year
            LEFT JOIN (
                SELECT YEAR(create_date) AS year, SUM(total_price) AS total_cost
                FROM import WHERE YEAR(create_date) BETWEEN ? AND ?
                GROUP BY YEAR(create_date)
            ) c ON d.year = c.year
            ORDER BY d.year
        """;

        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // G+�n tham s�+�: 4 lߦ�n (fromYear, toYear)
            for (int i = 1; i <= 8; i += 2) {
                statement.setInt(i, fromYear);
                statement.setInt(i + 1, toYear);
            }

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String time = rs.getString("time"); // time = n-�m
                    BigDecimal totalRevenue = rs.getBigDecimal("total_revenue");
                    BigDecimal totalCost = rs.getBigDecimal("total_cost");
                    BigDecimal totalProfit = rs.getBigDecimal("total_profit");

                    list.add(new StatisticRevenue(time, totalRevenue, totalCost, totalProfit));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting yearly statistic revenue: " + e.getMessage());
        }

        return list;
    }

}