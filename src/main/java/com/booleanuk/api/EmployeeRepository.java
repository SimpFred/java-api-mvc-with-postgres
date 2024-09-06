package com.booleanuk.api;

import org.postgresql.ds.PGSimpleDataSource;
import javax.sql.DataSource;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EmployeeRepository {
    private DataSource datasource;
    private String dbUser;
    private String dbURL;
    private String dbPassword;
    private String dbDatabase;
    private Connection connection;

    public EmployeeRepository() throws SQLException {
        setDatabaseCredentials();
        createAndSetDataSource();
        this.connection = this.datasource.getConnection();
    }

    private void setDatabaseCredentials() {
        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            this.dbUser = prop.getProperty("db.user");
            this.dbURL = prop.getProperty("db.url");
            this.dbPassword = prop.getProperty("db.password");
            this.dbDatabase = prop.getProperty("db.database");
        } catch(Exception e) {
            System.out.println("Whats wrong?: " + e);
        }
    }

    private void createAndSetDataSource() {
        final String url = "jdbc:postgresql://" + this.dbURL + ":5432/" + this.dbDatabase + "?user=" + this.dbUser +"&password=" + this.dbPassword;
        final PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(url);
        this.datasource = dataSource;
    }

    public List<Employee> getAll() throws SQLException {
        List<Employee> everyone = new ArrayList<>();
        PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM Employees");
        ResultSet results = statement.executeQuery();
        while (results.next()) {
            Employee theEmployee = new Employee(results.getLong("id"), results.getString("name"), results.getString("jobName"), results.getString("salaryGrade"), results.getString("department"));
            everyone.add(theEmployee);
        }
        return everyone;
    }

    public Employee getOne(long id) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM Employees WHERE id = ?");
        statement.setLong(1, id);
        ResultSet results = statement.executeQuery();
        if (results.next()) {
            return new Employee(results.getLong("id"), results.getString("name"), results.getString("jobName"), results.getString("salaryGrade"), results.getString("department"));
        }
        return null;
    }

    public Employee add(Employee employee) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement("INSERT INTO Employees (name, jobName, salaryGrade, department) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, employee.getName());
        statement.setString(2, employee.getJobName());
        statement.setString(3, employee.getSalaryGrade());
        statement.setString(4, employee.getDepartment());
        int rowsAffected = statement.executeUpdate();
        if (rowsAffected > 0) {
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return getOne(generatedKeys.getLong(1));
                }
            } catch (SQLException e) {
                System.out.println("Oops: " + e);
            }
        }
        return null;
    }

    public Employee update(long id, Employee employee) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(
                "UPDATE Employees " +
                        "SET name = ? ," +
                        "jobName = ? ," +
                        "salaryGrade = ? ," +
                        "department = ? " +
                        "WHERE id = ? ");
        statement.setString(1, employee.getName());
        statement.setString(2, employee.getJobName());
        statement.setString(3, employee.getSalaryGrade());
        statement.setString(4, employee.getDepartment());
        statement.setLong(5, id);
        int rowsAffected = statement.executeUpdate();
        if (rowsAffected > 0) {
            return getOne(id);
        }
        return null;
    }

    public Employee delete(long id) throws SQLException {
        Employee employeeToDelete = getOne(id);
        PreparedStatement statement = this.connection.prepareStatement("DELETE FROM Employees WHERE id = ?");
        statement.setLong(1, id);
        int rowsAffected = statement.executeUpdate();
        if (rowsAffected > 0) {
            return employeeToDelete;
        }
        return null;
    }

}
