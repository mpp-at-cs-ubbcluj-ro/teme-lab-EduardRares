package ro.mpp2025.Repository;

import ro.mpp2025.model.Flight;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlightRepo implements FlightRepositoryInterface {
    private static final Logger logger= LogManager.getLogger();
    private JdbcUtils dbUtils;
    public FlightRepo(Properties props) {
        logger.info("Initializing FlightRepo with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    @Override
    public List<Flight> findbyDestDeparture(String destination, LocalDateTime departureDate) {
        logger.traceEntry();
        Connection conn= dbUtils.getConnection();
        List<Flight> flights = new ArrayList<>();
        try(PreparedStatement preStmt = conn.prepareStatement("select * from flight where destination = ? and departureDate = ?")) {
            preStmt.setString(1, destination);
            preStmt.setLong(2, departureDate.toLocalDate().toEpochDay() * 24L * 60 * 60 * 1000);
            try(ResultSet rs = preStmt.executeQuery()) {
                while(rs.next()) {
                    String id=rs.getString("id");
                    String destination1 = rs.getString("destination");
                    String airport = rs.getString("airport");
                    int numberOfAvailableSeats=rs.getInt("numberOfAvailableSeats");
                    LocalTime departureTime= LocalTime.from(rs.getTimestamp("departureTime").toLocalDateTime());
                    LocalDate departureDate1= LocalDate.from(rs.getTimestamp("departureDate").toLocalDateTime());
                    Flight flight = new Flight(destination1, LocalDateTime.of(departureDate1, departureTime), airport, numberOfAvailableSeats);
                    flight.setId(id);
                    flights.add(flight);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        logger.traceExit(flights);
        return flights;
    }

    @Override
    public Optional<Flight> findOne(String s) {
        logger.traceEntry();
        Connection conn= dbUtils.getConnection();
        Flight flight = null;
        try(PreparedStatement preStmt = conn.prepareStatement("select * from flight where id = ?")) {
            preStmt.setString(1, s);
            try(ResultSet rs = preStmt.executeQuery()) {
                while(rs.next()) {
                    String id=rs.getString("id");
                    String destination = rs.getString("destination");
                    String airport = rs.getString("airport");
                    int numberOfAvailableSeats=rs.getInt("numberOfAvailableSeats");
                    LocalTime departureTime= LocalTime.from(rs.getTimestamp("departureTime").toLocalDateTime());
                    LocalDate departureDate= LocalDate.from(rs.getTimestamp("departureDate").toLocalDateTime());
                    flight = new Flight(destination, LocalDateTime.of(departureDate, departureTime), airport, numberOfAvailableSeats);
                    flight.setId(id);
                    logger.traceExit(flight);
                    return Optional.of(flight);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        logger.traceExit(flight);
        return Optional.empty();
    }

    @Override
    public List<Flight> findAll() {
        logger.traceEntry();
        Connection conn= dbUtils.getConnection();
        List<Flight> flights = new ArrayList<>();
        try(PreparedStatement preStmt = conn.prepareStatement("select * from flight")) {
            try(ResultSet rs = preStmt.executeQuery()) {
                while(rs.next()) {
                    String id=rs.getString("id");
                    String destination = rs.getString("destination");
                    String airport = rs.getString("airport");
                    int numberOfAvailableSeats=rs.getInt("numberOfAvailableSeats");
                    LocalTime departureTime= LocalTime.from(rs.getTimestamp("departureTime").toLocalDateTime());
                    LocalDate departureDate= LocalDate.from(rs.getTimestamp("departureDate").toLocalDateTime());

                    Flight flight = new Flight(destination, LocalDateTime.of(departureDate, departureTime), airport, numberOfAvailableSeats);
                    flight.setId(id);
                    flights.add(flight);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        logger.traceExit(flights);
        return flights;
    }

    @Override
    public Optional<Flight> save(Flight elem) {
        logger.traceEntry("saving flight {} ", elem);
        Connection conn= dbUtils.getConnection();
        try(PreparedStatement preStmt = conn.prepareStatement("insert into flight(destination, airport, numberOfAvailableSeats, departureDate, departureTime) values (?, ?, ?, ?, ?)")) {
            preStmt.setString(1, elem.getDestination());
            preStmt.setString(2, elem.getAirport());
            preStmt.setInt(3, elem.getNumberOfAvailableSeats());
            preStmt.setDate(4, Date.valueOf(elem.getDepartureTime().toLocalDate()));
            preStmt.setTime(5, Time.valueOf(elem.getDepartureTime().toLocalTime()));
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
    public Optional<Flight> delete(String s) {
        logger.traceEntry("deleting flight id {} ", s);
        Connection conn= dbUtils.getConnection();
        try(PreparedStatement preStmt = conn.prepareStatement("delete from flight where id=?")) {
            preStmt.setString(1, s);
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
    public Optional<Flight> update(Flight elem) {
        logger.traceEntry("updating flight {} ", elem);
        Connection conn= dbUtils.getConnection();
        try(PreparedStatement preStmt = conn.prepareStatement("update flight set numberOfAvailableSeats = ? where id=?")) {
            preStmt.setInt(1, elem.getNumberOfAvailableSeats());
            preStmt.setString(2, elem.getId());
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
