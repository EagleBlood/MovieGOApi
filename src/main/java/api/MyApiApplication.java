package api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import connection.Connect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.Base64;

@SpringBootApplication
@RestController
public class MyApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyApiApplication.class, args);
    }

    @GetMapping("/movies")
    public ResponseEntity<String> getMovies() {
        Connect connect = new Connect();
        Connection connection = connect.getConnection();
        if (connection != null) {
            try {
                // Create SQL query
                String query = "SELECT f.id_filmu, f.tytul, f.czas_trwania, f.ocena, f.opis, f.okladka, f.cena, g.nazwa_gatunku, s.data, s.pora_emisji " +
                        "FROM film f " +
                        "INNER JOIN gatunek g ON f.id_gatunku = g.id_gatunku " +
                        "INNER JOIN seanse s ON f.id_filmu = s.id_filmu";

                // Execute the query
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                // Process query results
                ObjectMapper objectMapper = new ObjectMapper();
                ArrayNode moviesArray = objectMapper.createArrayNode();
                while (resultSet.next()) {
                    // Get values from query result columns
                    int id_filmu = resultSet.getInt("id_filmu");
                    String tytul = resultSet.getString("tytul");
                    int czas_trwania = resultSet.getInt("czas_trwania");
                    double ocena = resultSet.getInt("ocena");
                    String opis = resultSet.getString("opis");
                    Blob okladka = resultSet.getBlob("okladka");
                    Double cena = resultSet.getDouble("cena");

                    String nazwa_gatunku = resultSet.getString("nazwa_gatunku");

                    String data = resultSet.getString("data");
                    String pora_emisji = resultSet.getString("pora_emisji");


                    byte[] imageBytes = okladka.getBytes(1, (int) okladka.length()); // Read the Blob data as a byte array
                    String base64Image = Base64.getEncoder().encodeToString(imageBytes); // Convert the byte array to a Base64 encoded string


                    // Append movie details to the response string
                    ObjectNode movieObject = objectMapper.createObjectNode();
                    movieObject.put("id_filmu", id_filmu);
                    movieObject.put("tytul", tytul);
                    movieObject.put("czas_trwania", czas_trwania);
                    movieObject.put("ocena", ocena);
                    movieObject.put("opis", opis);
                    movieObject.put("okladka", base64Image);
                    movieObject.put("cena", cena);

                    movieObject.put("nazwa_gatunku", nazwa_gatunku);

                    movieObject.put("data", data);
                    movieObject.put("pora_emisji", pora_emisji);

                    moviesArray.add(movieObject);
                }

                // Close ResultSet and Statement objects
                resultSet.close();
                statement.close();

                // Convert the moviesArray to a JSON string
                String moviesJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(moviesArray);

                // Set the response headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                // Return the response with the JSON string and headers
                return new ResponseEntity<>(moviesJson, headers, HttpStatus.OK);

            } catch (SQLException | IOException e) {
                e.printStackTrace();
            } finally {
                connect.close(); // Close the connection
            }
        }

        // Return a response indicating no movies found
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/tickets")
    public String getTickets() {
        Connect connect = new Connect();
        Connection connection = connect.getConnection();
        String moviesJson = "No movies found";
        if (connection != null) {
            try {
                // Create SQL query
                String query = "SELECT rezerwacje.nr_rezerwacji, film.tytul, uzytkownicy.login, COUNT(bilet.id_rezer) AS ilosc_biletow, GROUP_CONCAT(CONCAT(miejsca.rzad, ':', miejsca.fotel) SEPARATOR ' | ') AS miejsca, SUM(bilet.cena) AS cena, CONCAT(seanse.data,' ', seanse.pora_emisji) data FROM bilet " +
                    "INNER JOIN rezerwacje ON bilet.id_rezer = rezerwacje.id_rezer " +
                    "INNER JOIN uzytkownicy ON rezerwacje.id_uzyt = uzytkownicy.id_uzyt " +
                    "INNER JOIN miejsca ON bilet.id_miejsca = miejsca.id_miejsca " +
                    "INNER JOIN seanse ON bilet.id_seansu = seanse.id_seansu " +
                    "INNER JOIN film ON seanse.id_filmu = film.id_filmu " +
                    "GROUP BY bilet.id_rezer;";

                // Execute the query
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                // Process query results
                moviesJson = "";

                ObjectMapper objectMapper = new ObjectMapper();
                ArrayNode moviesArray = objectMapper.createArrayNode();
                while (resultSet.next()) {
                    // Get values from query result columns
                    String reservationNumber = resultSet.getString("nr_rezerwacji");
                    String movieTitle = resultSet.getString("tytul");
                    String userLogin = resultSet.getString("login");
                    int reservationId = resultSet.getInt("ilosc_biletow");
                    String seatDescription = resultSet.getString("miejsca");
                    double orderValue = resultSet.getDouble("cena");
                    String dateReservation = resultSet.getString("data");

                    // Append movie details to the response string
                    ObjectNode movieObject = objectMapper.createObjectNode();
                    movieObject.put("nr_rezerwacji", reservationNumber);
                    movieObject.put("tytul", movieTitle);
                    movieObject.put("login", userLogin);
                    movieObject.put("ilosc_biletow", reservationId);
                    movieObject.put("miejsca", seatDescription);
                    movieObject.put("cena", orderValue);
                    movieObject.put("data", dateReservation);

                    moviesArray.add(movieObject);
                }

                moviesJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(moviesArray);

                // Close ResultSet and Statement objects
                resultSet.close();
                statement.close();

            } catch (SQLException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } finally {
                connect.close(); // Close the connection
            }
        }
        return moviesJson;
    }

    @PostMapping("/book")
    public void bookTickets(@RequestParam(name = "movieId") int movieId, @RequestParam(name = "numTickets") int numTickets) {
        Connect connect = new Connect();
        Connection connection = connect.getConnection();
        if (connection != null) {
            try {
                connection.setAutoCommit(false); // Enable manual transaction management

                // Check if movie exists
                String movieCheckQuery = "SELECT * FROM movies WHERE id = ?";
                PreparedStatement movieCheckStatement = connection.prepareStatement(movieCheckQuery);
                movieCheckStatement.setInt(1, movieId);
                ResultSet movieCheckResult = movieCheckStatement.executeQuery();

                if (movieCheckResult.next()) {
                    int availableTickets = movieCheckResult.getInt("tickets");

                    // Check if sufficient tickets are available
                    if (numTickets <= availableTickets) {
                        // Update the ticket count for the movie
                        String updateQuery = "UPDATE movies SET tickets = tickets - ? WHERE id = ?";
                        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                        updateStatement.setInt(1, numTickets);
                        updateStatement.setInt(2, movieId);
                        int rowsAffected = updateStatement.executeUpdate();

                        if (rowsAffected == 1) {
                            connection.commit(); // Commit the transaction if the update was successful
                            System.out.println("Tickets booked successfully");
                        } else {
                            connection.rollback(); // Rollback the transaction if the update failed
                            System.out.println("Failed to book tickets");
                        }

                        updateStatement.close();
                    } else {
                        System.out.println("Insufficient tickets available");
                    }
                } else {
                    System.out.println("Movie not found");
                }

                movieCheckResult.close();
                movieCheckStatement.close();

            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    connection.rollback(); // Rollback the transaction in case of SQL error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } finally {
                connect.close(); // Close the connection to the database
            }
        }
    }


    @GetMapping("/login")
    public String checkLogin(@RequestParam(name = "login") String login, @RequestParam(name = "password") String password){
        String logSucces = "false";
        int count = 0;
        Connect connect = new Connect();
        Connection connection = connect.getConnection();
        if (connection != null) {
            try {
                // Tworzenie zapytania SQL
                String query = "SELECT id_uzyt FROM uzytkownicy WHERE login = ? AND haslo = ?";

                PreparedStatement statement = connection.prepareStatement(query);

                // Ustawienie wartości parametrów
                statement.setString(1, login);
                statement.setString(2, password);

                // Wykonanie zapytania
                ResultSet resultSet = statement.executeQuery();


                // Przetwarzanie wyników zapytania
                while(resultSet.next()){
                    count++;
                }
                if(count>0){
                    logSucces="true";
                }
                // Zamknięcie obiektów ResultSet i Statement
                resultSet.close();
                statement.close();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                connect.close(); // Zamknięcie połączenia
            }

        }
        return logSucces;
    }

    @PostMapping("/register")
    public void addUser(@RequestParam(name = "login") String login, @RequestParam(name = "password") String password, @RequestParam(name = "email") String email) {
        int count = 0;
        Connect connect = new Connect();
        Connection connection = connect.getConnection();
        if (connection != null) {
            try {
                connection.setAutoCommit(false); // enable manual transaction management

                // Create SQL query
                String query = "SELECT id_uzyt FROM uzytkownicy WHERE login = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, login);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    count++;
                }
                if (count != 0) {
                    System.out.println("User with the given login already exists");
                } else {
                    // Create SQL query
                    query = "INSERT INTO uzytkownicy (login, haslo, email) VALUES (?, ?, ?)";

                    // Prepare SQL statement with parameters
                    PreparedStatement statement2 = connection.prepareStatement(query);
                    statement2.setString(1, login);
                    statement2.setString(2, password);
                    statement2.setString(3, email);

                    // Execute SQL statement
                    int rowsAffected = statement2.executeUpdate();

                    if (rowsAffected == 1) { // If exactly one row was inserted
                        connection.commit(); // commit the transaction
                    } else {
                        connection.rollback(); // rollback the transaction
                    }

                    // Close Statement and Connection objects
                    statement.close();
                    statement2.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    connection.rollback(); // rollback the transaction in case of SQL error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } finally {
                connect.close(); // close the database connection
            }
        }
    }



}