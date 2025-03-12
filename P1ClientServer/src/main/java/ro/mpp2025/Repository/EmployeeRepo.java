package ro.mpp2025.Repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2025.model.Employee;
import ro.mpp2025.model.Flight;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class EmployeeRepo implements EmployeeRepoInterface{
    private static final Logger logger= LogManager.getLogger();
    private JdbcUtils dbUtils;
    public EmployeeRepo(Properties props) {
        logger.info("Initializing CarsDBRepository with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    @Override
    public Optional<Employee> findOne(Integer integer) {
        logger.traceEntry();
        Connection conn= dbUtils.getConnection();
        Employee employee = null;
        try(PreparedStatement preStmt = conn.prepareStatement("select * from employee where id = ?")) {
            preStmt.setInt(1, integer);
            try(ResultSet rs = preStmt.executeQuery()) {
                while(rs.next()) {
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String email = rs.getString("email");
                    employee = new Employee(username, password, email);
                    employee.setId(id);
                    logger.traceExit(employee);
                    return Optional.of(employee);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        logger.traceExit(employee);
        return Optional.empty();
    }

    @Override
    public Iterable<Employee> findAll() {
        logger.traceEntry();
        Connection conn= dbUtils.getConnection();
        List<Employee> employees = new ArrayList<>();
        try(PreparedStatement preStmt = conn.prepareStatement("select * from employee")) {
            try(ResultSet rs = preStmt.executeQuery()) {
                while(rs.next()) {
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String email = rs.getString("email");
                    Employee employee = new Employee(username, password, email);
                    employee.setId(id);
                    employees.add(employee);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        logger.traceExit(employees);
        return employees;
    }

    @Override
    public Optional<Employee> save(Employee elem) {
        logger.traceEntry("saving employee {} ", elem);
        Connection conn= dbUtils.getConnection();
        try(PreparedStatement preStmt = conn.prepareStatement("insert into employee(username, password, email) values (?, ?, ?)")) {
            preStmt.setString(1, elem.getUsername());
            preStmt.setString(2, elem.getPassword());
            preStmt.setString(3, elem.getEmail());
            int result=preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);
            return Optional.of(elem);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Employee> delete(Integer integer) {
        logger.traceEntry("deleting employee id {} ", integer);
        Connection conn= dbUtils.getConnection();
        try(PreparedStatement preStmt = conn.prepareStatement("delete from employee where id=?")) {
            preStmt.setInt(1, integer);
            int result=preStmt.executeUpdate();
            logger.trace("Deleted {} instances", result);
            return Optional.empty();
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Employee> update(Employee elem) {
        logger.traceEntry("updating employee {} ", elem);
        Connection conn= dbUtils.getConnection();
        try(PreparedStatement preStmt = conn.prepareStatement("update employee set username = ?, password = ?, email = ? where id=?")) {
            preStmt.setString(1, elem.getUsername());
            preStmt.setString(2, elem.getPassword());
            preStmt.setString(3, elem.getEmail());
            preStmt.setInt(4, elem.getId());
            int result=preStmt.executeUpdate();
            logger.trace("Updated {} instances", result);
            return Optional.of(elem);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        return Optional.empty();
    }
}
