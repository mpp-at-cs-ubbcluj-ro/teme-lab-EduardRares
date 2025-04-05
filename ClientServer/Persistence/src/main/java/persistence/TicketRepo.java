package persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import model.Flight;
import model.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class TicketRepo implements TicketRepoInterface {
    private static final Logger logger= LogManager.getLogger();
    private JdbcUtils dbUtils;
    public TicketRepo(Properties props) {
        logger.info("Initializing TicketRepo with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    public Flight findOneFlight(String s) {
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
                    return flight;
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        return null;
    }

    @Override
    public Optional<Ticket> findOne(Integer integer) {
        logger.traceEntry();
        Connection conn= dbUtils.getConnection();
        Ticket ticket = null;
        try(PreparedStatement preStmt = conn.prepareStatement("select * from ticket where id = ?")) {
            preStmt.setInt(1, integer);
            try(ResultSet rs = preStmt.executeQuery()) {
                while(rs.next()) {
                    int id = rs.getInt("id");
                    int noOfTickets = rs.getInt("noOfTickets");
                    String flightId = rs.getString("flightId");
                    List<String> names = new ArrayList<>();
                    try(PreparedStatement preStmt1 = conn.prepareStatement("select * from ticketNames where ticketId = ?")) {
                        preStmt1.setInt(1, id);
                        try (ResultSet rs1 = preStmt1.executeQuery()) {
                            while (rs1.next()) {
                                String fullname = rs1.getString("fullname");
                                names.add(fullname);
                            }
                        }
                    }

                    ticket = new Ticket(names, noOfTickets, findOneFlight(flightId));
                    ticket.setId(id);
                    logger.traceExit(ticket);
                    return Optional.of(ticket);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        logger.traceExit(ticket);
        return Optional.empty();
    }

    @Override
    public List<Ticket> findAll() {
        logger.traceEntry();
        Connection conn= dbUtils.getConnection();
        List<Ticket> tickets = new ArrayList<>();
        try(PreparedStatement preStmt = conn.prepareStatement("select * from flight")) {
            try(ResultSet rs = preStmt.executeQuery()) {
                while(rs.next()) {
                    int id = rs.getInt("id");
                    int noOfTickets = rs.getInt("noOfTickets");
                    String flightId = rs.getString("flightId");
                    List<String> names = new ArrayList<>();
                    try(PreparedStatement preStmt1 = conn.prepareStatement("select * from ticketNames where ticketId = ?")) {
                        preStmt1.setInt(1, id);
                        try (ResultSet rs1 = preStmt1.executeQuery()) {
                            while (rs1.next()) {
                                String fullname = rs1.getString("fullname");
                                names.add(fullname);
                            }
                        }
                    }

                    Ticket ticket = new Ticket(names, noOfTickets, findOneFlight(flightId));
                    ticket.setId(id);
                    tickets.add(ticket);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        logger.traceExit(tickets);
        return tickets;
    }

    @Override
    public Optional<Ticket> save(Ticket elem) {
        logger.traceEntry("saving ticket {} ", elem);
        Connection conn= dbUtils.getConnection();
        try(PreparedStatement preStmt = conn.prepareStatement("insert into ticket(noOfTickets, flightId) values (?, ?)")) {
            preStmt.setInt(1, elem.getNoOfTickets());
            preStmt.setString(2, elem.getFlight().getId());
            int id = 0;
            int result=preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);
            try (ResultSet generatedKeys = preStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    id = generatedKeys.getInt(1);
                }
            }
            for(String s : elem.getNames()) {
                try (PreparedStatement preStmt1 = conn.prepareStatement("insert into ticketNames(ticketId, fullname) values (?, ?)")) {
                    preStmt1.setInt(1, id);
                    preStmt1.setString(2, s);
                    preStmt1.executeUpdate();
                }
            }
            return Optional.of(elem);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Ticket> delete(Integer integer) {
        logger.traceEntry("deleting ticket id {} ", integer);
        Connection conn= dbUtils.getConnection();
        try(PreparedStatement preStmt = conn.prepareStatement("delete from ticket where id=?")) {
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
    public Optional<Ticket> update(Ticket elem) {
        logger.traceEntry("updating flight {} ", elem);
        Connection conn= dbUtils.getConnection();
        try(PreparedStatement preStmt = conn.prepareStatement("update ticket set noOfTickets = ?, flightId = ? where id=?")) {
            preStmt.setInt(1, elem.getNoOfTickets());
            preStmt.setString(2, elem.getFlight().getId());
            preStmt.setInt(3, elem.getId());
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
