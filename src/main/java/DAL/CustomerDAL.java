package DAL;

import DTO.CustomerDTO;
import java.sql.*;

public class CustomerDAL extends BaseDAL<CustomerDTO, Integer> {
    public static final CustomerDAL INSTANCE = new CustomerDAL();

    private CustomerDAL() {
        super(ConnectApplication.getInstance().getConnectionFactory(), "customer", "id");
    }

    public static CustomerDAL getInstance() {
        return INSTANCE;
    }

    @Override
    protected CustomerDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new CustomerDTO(
                resultSet.getInt("id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getString("phone"),
                resultSet.getString("address"),
                resultSet.getString("image_url"),
                resultSet.getDate("date_of_birth"),
                resultSet.getBoolean("status")
        );
    }

    @Override
    protected boolean shouldUseGeneratedKeys() {
        return true;
    }

    @Override
    protected void setGeneratedKey(CustomerDTO obj, ResultSet generatedKeys) throws SQLException {
        if (generatedKeys.next()) {
            obj.setId(generatedKeys.getInt(1));
        }
    }

    @Override
    protected String getInsertQuery() {
        return "(first_name, last_name, phone, address, image_url, date_of_birth, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, CustomerDTO obj) throws SQLException {
        statement.setString(1, obj.getFirstName());
        statement.setString(2, obj.getLastName());
        statement.setString(3, obj.getPhone());
        statement.setString(4, obj.getAddress());
        statement.setString(5, obj.getImageUrl());
        statement.setDate(6, new java.sql.Date(obj.getDateOfBirth().getTime()));
        statement.setBoolean(7, obj.isStatus());
    }

    @Override
    protected String getUpdateQuery() {
        return "SET first_name = ?, last_name = ?, phone = ?, address = ?, image_url = ?, date_of_birth = ?, status = ? WHERE id = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement statement, CustomerDTO obj) throws SQLException {
        statement.setString(1, obj.getFirstName());
        statement.setString(2, obj.getLastName());
        statement.setString(3, obj.getPhone());
        statement.setString(4, obj.getAddress());
        statement.setString(5, obj.getImageUrl());
        statement.setDate(6, new java.sql.Date(obj.getDateOfBirth().getTime()));
        statement.setBoolean(7, obj.isStatus());
        statement.setInt(8, obj.getId());
    }

    @Override
    protected boolean hasSoftDelete() {
        return true;
    }
}
