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
                String query = "SELECT id_filmu, tytul, czas_trwania, ocena, opis, okladka, cena FROM film";

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

                    // Read the Blob data as a byte array
                    byte[] imageBytes = okladka.getBytes(1, (int) okladka.length());

                    // Convert the byte array to a Base64 encoded string
                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);

                    // Append movie details to the response string
                    ObjectNode movieObject = objectMapper.createObjectNode();
                    movieObject.put("id_filmu", id_filmu);
                    movieObject.put("tytul", tytul);
                    movieObject.put("czas_trwania", czas_trwania);
                    movieObject.put("ocena", ocena);
                    movieObject.put("opis", opis);
                    movieObject.put("okladka", base64Image);
                    movieObject.put("cena", cena);

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



    @GetMapping("/ranking")
    public String getMovieRanking() {
        Connect connect = new Connect();
        Connection connection = connect.getConnection();
        String ranking = "No ranking available";
        if (connection != null) {
            try {
                // Create SQL query
                String query = "SELECT movie_id, AVG(rating) AS average_rating FROM ratings GROUP BY movie_id ORDER BY average_rating DESC LIMIT 10";

                // Execute the query
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                // Process query results
                ranking = "";
                while (resultSet.next()) {
                    // Get values from query result columns
                    int movieId = resultSet.getInt("movie_id");
                    double averageRating = resultSet.getDouble("average_rating");

                    // Append movie ranking details to the response string
                    ranking += "Movie ID: " + movieId + ", Average Rating: " + averageRating + "\n";
                }

                // Close ResultSet and Statement objects
                resultSet.close();
                statement.close();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                connect.close(); // Close the connection
            }
        }
        return ranking;
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
                String query = "SELECT id_uzytkownika FROM milionerzy.uzytkownicy WHERE login = ? AND haslo = ?";

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
                connection.setAutoCommit(false); // włączenie ręcznego zarządzania transakcjami

                // Tworzenie zapytania SQL
                String query = "SELECT id_uzyt FROM uzytkownicy WHERE login = ?";
                PreparedStatement statement = connection.prepareStatement(query);

                statement.setString(1, login);
                ResultSet resultSet = statement.executeQuery();

                while(resultSet.next()){
                    count++;
                }
                if(count!=0){
                    System.out.println("Użytkownik o podanym loginie już istnieje");
                }else {
                    // Utworzenie zapytania SQL
                    query = "INSERT INTO uzytkownicy (login, haslo, email) VALUES (?, ?, ?)";

                    // Przygotowanie instrukcji SQL z parametrami
                    PreparedStatement statement2 = connection.prepareStatement(query);
                    statement2.setString(1, login);
                    statement2.setString(2, password);
                    statement2.setString(3, email);

                    // Wykonanie instrukcji SQL
                    int rowsAffected = statement2.executeUpdate();

                    if (rowsAffected == 1) { // Jeżeli wstawiono dokładnie jeden wiersz
                        connection.commit(); // zatwierdzenie tranzakcji
                    } else {
                        connection.rollback(); // wycofanie tranzakcji
                    }

                    // Zamknięcie obiektów Statement i Connection
                    statement.close();
                    statement2.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    connection.rollback(); // wycofanie tranzakcji w przypadku błędu SQL
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } finally {
                connect.close(); // zamknięcie połączenia z bazą danych
            }
        }
    }


    public String getImageUrlFromBlob(byte[] imageData) {
        // Convert the image data to Base64 encoding
        String base64Data = Base64.getEncoder().encodeToString(imageData);

        // Construct the data URL with the appropriate media type
        String imageUrl = "data:image/png;base64," + base64Data;

        return imageUrl;
    }
}