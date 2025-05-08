package persistence;

import model.Employee;
import model.Flight;
import org.hibernate.Session;

import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;

public class FlightHibernateRepository implements FlightRepositoryInterface{
    @Override
    public List<Flight> findbyDestDeparture(String destination, LocalDateTime departureTime) {
        long targetMillis = departureTime.atZone(UTC).toInstant().toEpochMilli();

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createNativeQuery(
                            "SELECT * FROM flight WHERE destination = ? AND departureDate = ?",
                            Flight.class)
                    .setParameter(1, destination)
                    .setParameter(2, targetMillis)  // Compare as longs
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
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            Flight flight=session.createQuery("from Flight where id=?1",Flight.class).
                    setParameter(1,s).uniqueResult();
            System.out.println("In delete am gasit zborul "+flight);
            if (flight!=null) {
                session.remove(flight);
                session.flush();
            }
        });
        return Optional.empty();
    }

    @Override
    public Optional<Flight> update(Flight entity) {
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            if (!Objects.isNull(session.find(Flight.class, entity.getId()))) {
                System.out.println("In update, am gasit zborul cu id-ul "+entity.getId());
                session.merge(entity);
                session.flush();
            }
        });
        return Optional.of(entity);
    }
}
