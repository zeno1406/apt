package DTO;

public class AccountDTO {
    private int employeeId;
    private String username;
    private String password;

    // Constructors
    public AccountDTO() {
    }

    public AccountDTO(int employeeId, String username, String password) {
        this.employeeId = employeeId;
        this.username = username;
        this.password = password;
    }

    public AccountDTO(AccountDTO other) {
        if (other != null) {
            this.employeeId = other.employeeId;
            this.username = other.username;
            this.password = other.password;
        }
    }

    // Getters and Setters
    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
