package api;

import connection.Connect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

@SpringBootApplication
@RestController
public class MyApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyApiApplication.class, args);
    }

    @GetMapping("/movies")
    public String getMovies() {
        Connect connect = new Connect();
        Connection connection = connect.getConnection();
        String movies = "No movies found";
        if (connection != null) {
            try {
                // Create SQL query
                String query = "SELECT * FROM movies";

                // Execute the query
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                // Process query results
                movies = "";
                while (resultSet.next()) {
                    // Get values from query result columns
                    String title = resultSet.getString("title");
                    String genre = resultSet.getString("genre");
                    int releaseYear = resultSet.getInt("release_year");

                    // Append movie details to the response string
                    movies += "Title: " + title + ", Genre: " + genre + ", Release Year: " + releaseYear + "\n";
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
        return movies;
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
}