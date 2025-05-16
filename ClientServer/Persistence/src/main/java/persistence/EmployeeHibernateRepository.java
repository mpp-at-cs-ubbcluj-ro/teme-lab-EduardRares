package persistence;

import model.Employee;
import org.hibernate.Session;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EmployeeHibernateRepository implements EmployeeRepoInterface {
    @Override
    public Optional<Employee> login(String username, String password) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.createSelectionQuery("from Employee where username=:usernameM and password=:passwordM ", Employee.class)
                    .setParameter("usernameM", username)
                    .setParameter("passwordM", password)
                    .getSingleResultOrNull());
        }
    }

    @Override
    public Optional<Employee> findOne(Integer integer) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.createSelectionQuery("from Employee where id=:idM ", Employee.class)
                    .setParameter("idM", integer)
                    .getSingleResultOrNull());
        }
    }

    @Override
    public List<Employee> findAll() {
        try( Session session=HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from Employee ", Employee.class).getResultList();
        }
    }

    @Override
    public Optional<Employee> save(Employee entity) {
        HibernateUtils.getSessionFactory().inTransaction(session -> session.persist(entity));
        return Optional.of(entity);
    }

    @Override
    public Optional<Employee> delete(Integer integer) {
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            Employee employee=session.createQuery("from Employee where id=?1",Employee.class).
                    setParameter(1,integer).uniqueResult();
            System.out.println("In delete am gasit angajatul "+employee);
            if (employee!=null) {
                session.remove(employee);
                session.flush();
            }
        });
        return Optional.empty();
    }

    @Override
    public Optional<Employee> update(Employee entity) {
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            if (!Objects.isNull(session.find(Employee.class, entity.getId()))) {
                System.out.println("In update, am gasit userul cu id-ul "+entity.getId());
                session.merge(entity);
                session.flush();
            }
        });
        return Optional.of(entity);
    }
}
