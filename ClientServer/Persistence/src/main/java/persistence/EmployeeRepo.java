package persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import model.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class EmployeeRepo implements EmployeeRepoInterface{
    private static final Logger logger= LogManager.getLogger();
    private JdbcUtils dbUtils;
    public EmployeeRepo(Properties props) {
        logger.info("Initializing EmployeeRepo with properties: {} ",props);
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
                if(rs.next()) {
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
    public List<Employee> findAll() {
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

    @Override
    public Optional<Employee> login(String username, String password) {
        logger.traceEntry();
        Connection conn= dbUtils.getConnection();
        try(PreparedStatement preStmt = conn.prepareStatement("select * from employee where username = ? and password = ?")) {
            preStmt.setString(1, username);
            preStmt.setString(2, password);
            try(ResultSet rs = preStmt.executeQuery()) {
                if(rs.next()) {
                    int id = rs.getInt("id");
                    String email = rs.getString("email");
                    Employee employee = new Employee(username, password, email);
                    employee.setId(id);
                    return Optional.of(employee);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        return Optional.empty();
    }
}
