package model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table( name = "employee" )
public class Employee implements model.Entity<Integer>, Comparable<Employee>, Serializable {
    @Column( name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String email;
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id = -1;

    public Employee(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Employee(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Employee() {}

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Employee [username=" + username + ", password=" + password + ", email=" + email + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(username, employee.username) && Objects.equals(password, employee.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }

    public int compareTo(Employee o) {
        return id.compareTo(o.id);
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer integer) {
        id = integer;
    }
}
