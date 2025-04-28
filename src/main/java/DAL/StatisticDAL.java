
package DAL;

import DTO.StatisticQuarterDTO;
import INTERFACE.ConnectionFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

public class StatisticDAL {
    private ConnectionFactory connectionFactory = ConnectApplication.getInstance().getConnectionFactory();
    private static final StatisticDAL INSTANCE = new StatisticDAL();
    private StatisticDAL() {

    }

    public static StatisticDAL getInstance() {
        return INSTANCE;
    }

    public ArrayList<StatisticQuarterDTO> getStatisticRevenueByQuarter(int year) {
        ArrayList<StatisticQuarterDTO> list = new ArrayList<>();

        final String query = """
        SELECT\s
                    e.id AS employee_id,
                    COALESCE(SUM(CASE WHEN QUARTER(i.create_date) = 1 THEN i.total_price ELSE 0 END), 0) AS q1,
                    COALESCE(SUM(CASE WHEN QUARTER(i.create_date) = 2 THEN i.total_price ELSE 0 END), 0) AS q2,
                    COALESCE(SUM(CASE WHEN QUARTER(i.create_date) = 3 THEN i.total_price ELSE 0 END), 0) AS q3,
                    COALESCE(SUM(CASE WHEN QUARTER(i.create_date) = 4 THEN i.total_price ELSE 0 END), 0) AS q4,
                    COALESCE(SUM(i.total_price), 0) AS total
                FROM\s
                    employee e
                LEFT JOIN\s
                    invoice i ON e.id = i.employee_id AND YEAR(i.create_date) = ?
                GROUP BY\s
                    e.id
                ORDER BY\s
                    e.id;
    """;

        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, year);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    int employeeId = rs.getInt("employee_id");
                    BigDecimal q1 = rs.getBigDecimal("q1");
                    BigDecimal q2 = rs.getBigDecimal("q2");
                    BigDecimal q3 = rs.getBigDecimal("q3");
                    BigDecimal q4 = rs.getBigDecimal("q4");
                    BigDecimal total = rs.getBigDecimal("total");

                    list.add(new StatisticQuarterDTO(employeeId, q1, q2, q3, q4, total));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting quarterly statistic revenue: " + e.getMessage());
        }

        return list;
    }

}