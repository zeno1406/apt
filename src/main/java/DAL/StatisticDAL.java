package DAL;

import DTO.StatisticDTO;
import INTERFACE.ConnectionFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class StatisticDAL {
    public static final StatisticDAL INSTANCE = new StatisticDAL();

    private final ConnectionFactory connectionFactory;

    private StatisticDAL() {
        this.connectionFactory = ConnectApplication.getInstance().getConnectionFactory();
    }

    public static StatisticDAL getInstance() {
        return INSTANCE;
    }

    public List<StatisticDTO.QuarterlyEmployeeRevenue> getQuarterlyEmployeeRevenue(int year) {
        final String query = """
                    SELECT
                         e.id AS employee_id,
                         COALESCE(SUM(CASE WHEN QUARTER(i.create_date) = 1 THEN i.total_price - i.discount_amount ELSE 0 END), 0) AS quarter1,
                         COALESCE(SUM(CASE WHEN QUARTER(i.create_date) = 2 THEN i.total_price - i.discount_amount ELSE 0 END), 0) AS quarter2,
                         COALESCE(SUM(CASE WHEN QUARTER(i.create_date) = 3 THEN i.total_price - i.discount_amount ELSE 0 END), 0) AS quarter3,
                         COALESCE(SUM(CASE WHEN QUARTER(i.create_date) = 4 THEN i.total_price - i.discount_amount ELSE 0 END), 0) AS quarter4
                     FROM
                         employee e
                     LEFT JOIN
                         invoice i ON e.id = i.employee_id AND YEAR(i.create_date) = ?
                     GROUP BY
                         e.id;     
                """;

        List<StatisticDTO.QuarterlyEmployeeRevenue> list = new ArrayList<>();

        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
//            System.out.println("Connected to database: " + connection.getCatalog());
            statement.setInt(1, year);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(new StatisticDTO.QuarterlyEmployeeRevenue(
                            resultSet.getInt("employee_id"),
                            resultSet.getBigDecimal("quarter1"),
                            resultSet.getBigDecimal("quarter2"),
                            resultSet.getBigDecimal("quarter3"),
                            resultSet.getBigDecimal("quarter4")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving quarterly employee revenue statistics: " + e.getMessage());
        }
        return list;
    }

    public List<StatisticDTO.ProductRevenue> getProductRevenue(LocalDate start, LocalDate end) {
        final String query = """
                SELECT
                       p.id,
                       p.name AS product_name,
                       c.name AS category_name,
                       COALESCE(SUM(CASE WHEN i.id IS NOT NULL THEN di.quantity ELSE 0 END), 0) AS total_quantity
                   FROM
                       product p
                   JOIN
                       category c ON p.category_id = c.id
                   LEFT JOIN
                       detail_invoice di ON di.product_id = p.id
                   LEFT JOIN
                       invoice i ON di.invoice_id = i.id AND i.create_date >= ? AND i.create_date < DATE_ADD(?, INTERVAL 1 DAY)
                   GROUP BY
                       p.id, p.name, c.name
                   ORDER BY
                       total_quantity DESC;
                """;

        List<StatisticDTO.ProductRevenue> list = new ArrayList<>();
        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDate(1, java.sql.Date.valueOf(start));
            statement.setDate(2, java.sql.Date.valueOf(end));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(new StatisticDTO.ProductRevenue(
                            resultSet.getString("id"),
                            resultSet.getString("product_name"),
                            resultSet.getString("category_name"),
                            resultSet.getInt("total_quantity")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving product revenue statistics: " + e.getMessage());
        }
        return list;
    }
}
