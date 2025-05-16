package persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import model.Employee;
import model.Flight;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Repository
public class FlightHibernateRepository implements FlightRepositoryInterface{

    @Override
    public List<Flight> findbyDestDeparture(String destination, LocalDateTime departureTime) {
        long dateMidnightMillis = departureTime.toLocalDate().toEpochDay() * 24L * 60 * 60 * 1000;

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createNativeQuery(
                            "SELECT * FROM flight WHERE destination = ? AND departureDate = ?",
                            Flight.class)
                    .setParameter(1, destination)
                    .setParameter(2, dateMidnightMillis)  // Compare as longs
                    .getResultList();
        }
    }

    @Override
    public Optional<Flight> findOne(String s) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.createSelectionQuery("from Flight where id=:idM ", Flight.class)
                    .setParameter("idM", s)
                    .getSingleResultOrNull());
        }
    }

    @Override
    public List<Flight> findAll() {
        try( Session session=HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from Flight ", Flight.class).getResultList();
        }
    }

    @Override
    public Optional<Flight> save(Flight entity) {
        HibernateUtils.getSessionFactory().inTransaction(session -> session.persist(entity));
        return Optional.of(entity);
    }

    @Override
    public Optional<Flight> delete(String s) {
        Optional<Flight> flightDeleted = findOne(s);
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            Flight flight=session.createQuery("from Flight where id=?1",Flight.class).
                    setParameter(1,s).uniqueResult();
            System.out.println("In delete am gasit zborul "+flight);
            if (flight!=null) {
                session.remove(flight);
                session.flush();
            }
        });
        return flightDeleted;
    }

    @Override
    public Optional<Flight> update(Flight entity) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            // load the persistent object
            Flight managed = session.find(Flight.class, entity.getId());
            if (managed != null) {
                // only overwrite the fields you actually want to change
                managed.setNumberOfAvailableSeats(entity.getNumberOfAvailableSeats());
            }

            tx.commit();
            return Optional.ofNullable(managed);
        }
    }
}
