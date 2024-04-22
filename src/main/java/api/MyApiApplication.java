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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
                String query = "SELECT f.id_filmu, f.tytul, f.czas_trwania, f.ocena, f.opis, f.okladka, f.cena, g.nazwa_gatunku " +
                        "FROM film f " +
                        "INNER JOIN gatunek g ON f.id_gatunku = g.id_gatunku;";

                // Execute the query
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);


                ObjectMapper objectMapper = new ObjectMapper();
                ArrayNode moviesArray = objectMapper.createArrayNode();


                while (resultSet.next()) {

                    ObjectNode movieObject = objectMapper.createObjectNode();
                    movieObject.put("movieId", resultSet.getInt("id_filmu"));
                    movieObject.put("movieTitle", resultSet.getString("tytul"));
                    movieObject.put("movieDuration", resultSet.getInt("czas_trwania"));
                    movieObject.put("movieScore", resultSet.getDouble("ocena"));
                    movieObject.put("movieDescription", resultSet.getString("opis"));
                    movieObject.put("movieCover", resultSet.getString("okladka"));
                    movieObject.put("moviePrice", resultSet.getDouble("cena"));
                    movieObject.put("movieGenre", resultSet.getString("nazwa_gatunku"));

                    moviesArray.add(movieObject);
                }



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

    @GetMapping("/movie")
    public ResponseEntity<String> getMovie(@RequestParam(name = "id_filmu") int id_filmu) {

        Connect connect = new Connect();
        Connection connection = connect.getConnection();
        if (connection != null) {
            try {
                // Create SQL query
                String query = "SELECT f.id_filmu, f.tytul, f.czas_trwania, f.ocena, f.opis, f.cena, f.okladka, g.nazwa_gatunku " +
                        "FROM film f " +
                        "INNER JOIN gatunek g ON f.id_gatunku = g.id_gatunku " +
                        "WHERE id_filmu = ? ";

                // Execute the query
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, id_filmu);
                ResultSet resultSet = statement.executeQuery();


                ObjectMapper objectMapper = new ObjectMapper();
                ArrayNode moviesArray = objectMapper.createArrayNode();


                while (resultSet.next()) {

                    ObjectNode movieObject = objectMapper.createObjectNode();
                    movieObject.put("movieId", resultSet.getInt("id_filmu"));
                    movieObject.put("movieTitle", resultSet.getString("tytul"));
                    movieObject.put("movieDuration", resultSet.getInt("czas_trwania"));
                    movieObject.put("movieScore", resultSet.getDouble("ocena"));
                    movieObject.put("movieDescription", resultSet.getString("opis"));
                    movieObject.put("movieCover", resultSet.getString("okladka"));
                    movieObject.put("moviePrice", resultSet.getDouble("cena"));
                    movieObject.put("movieGenre", resultSet.getString("nazwa_gatunku"));

                    moviesArray.add(movieObject);
                }

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

    @GetMapping("/schedules")
    public ResponseEntity<String> getSchedules() {
        Connect connect = new Connect();
        Connection connection = connect.getConnection();
        if (connection != null) {
            try {
                // Create SQL query
                String query = "SELECT s.data, s.pora_emisji, s.id_seansu, s.id_sala, s.id_filmu " +
                        "FROM seanse s;";

                // Execute the query
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);


                ObjectMapper objectMapper = new ObjectMapper();
                ArrayNode moviesArray = objectMapper.createArrayNode();

                Map<String, Map<String, List<Map<String, Object>>>> groupedData = new HashMap<>();

                while (resultSet.next()) {
                    String data = resultSet.getString("data");
                    String time = resultSet.getString("pora_emisji");

                    // Tworzenie obiektu z danymi filmu
                    Map<String, Object> movieData = new HashMap<>();
                    movieData.put("id_seansu", resultSet.getInt("id_seansu"));
                    movieData.put("id_sala", resultSet.getInt("id_sala"));
                    movieData.put("id_filmu", resultSet.getInt("id_filmu"));

                    // Sprawdzenie, czy istnieje już klucz dla danej daty
                    if (groupedData.containsKey(data)) {
                        Map<String, List<Map<String, Object>>> timeMap = groupedData.get(data);

                        // Sprawdzenie, czy istnieje już klucz dla danej godziny
                        if (timeMap.containsKey(time)) {
                            List<Map<String, Object>> movies = timeMap.get(time);
                            movies.add(movieData);
                        } else {
                            List<Map<String, Object>> movies = new ArrayList<>();
                            movies.add(movieData);
                            timeMap.put(time, movies);
                        }
                    } else {
                        Map<String, List<Map<String, Object>>> timeMap = new HashMap<>();
                        List<Map<String, Object>> movies = new ArrayList<>();
                        movies.add(movieData);
                        timeMap.put(time, movies);
                        groupedData.put(data, timeMap);
                    }
                }

// Tworzenie struktury JSON z pogrupowanymi danymi
                for (Map.Entry<String, Map<String, List<Map<String, Object>>>> entry : groupedData.entrySet()) {
                    String data = entry.getKey();
                    Map<String, List<Map<String, Object>>> timeMap = entry.getValue();

                    ObjectNode dataObject = objectMapper.createObjectNode();
                    dataObject.put("date", data);

                    ArrayNode showsArray = objectMapper.createArrayNode();

                    for (Map.Entry<String, List<Map<String, Object>>> timeEntry : timeMap.entrySet()) {
                        String time = timeEntry.getKey();
                        List<Map<String, Object>> movies = timeEntry.getValue();

                        ObjectNode showsObject = objectMapper.createObjectNode();
                        showsObject.put("time", time);

                        ArrayNode moviesArrayNode = objectMapper.createArrayNode();
                        for (Map<String, Object> movie : movies) {
                            ObjectNode movieObject = objectMapper.createObjectNode();
                            movieObject.put("showId", (int) movie.get("id_seansu"));
                            movieObject.put("hallScheduleId", (int) movie.get("id_sala"));
                            movieObject.put("movieScheduleId", (int) movie.get("id_filmu"));
                            moviesArrayNode.add(movieObject);
                        }

                        showsObject.set("movies", moviesArrayNode);
                        showsArray.add(showsObject);
                    }

                    dataObject.set("shows", showsArray);
                    moviesArray.add(dataObject);
                }



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

    @GetMapping("/halls")
    public ResponseEntity<String> getHalls() {
        Connect connect = new Connect();
        Connection connection = connect.getConnection();
        if (connection != null) {
            try {
                // Create SQL query
                String query = "SELECT * FROM sale;";

                // Execute the query
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);


                ObjectMapper objectMapper = new ObjectMapper();
                ArrayNode moviesArray = objectMapper.createArrayNode();


                while (resultSet.next()) {

                    ObjectNode movieObject = objectMapper.createObjectNode();
                    movieObject.put("hallId", resultSet.getInt("id_sali"));
                    movieObject.put("hallNumber", resultSet.getInt("numer"));
                    movieObject.put("hallRows", resultSet.getInt("s_rzedy"));
                    movieObject.put("hallColumns", resultSet.getInt("s_kolumny"));
                    moviesArray.add(movieObject);
                }



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
    public String getTickets(@RequestParam(name = "id_uzyt") int id_uzyt) {
        Connect connect = new Connect();
        Connection connection = connect.getConnection();
        String moviesJson = "No movies found";
        if (connection != null) {
            try {
                // Create SQL query
                String query = "SELECT rezerwacje.nr_rezerwacji, film.tytul, COUNT(bilet.id_rezer) AS ilosc_biletow, GROUP_CONCAT(CONCAT(bilet.miejsce) SEPARATOR ' | ') AS miejsca, rezerwacje.kwota_rezer, CONCAT(seanse.data,' ', seanse.pora_emisji) AS data FROM bilet " +
                        "INNER JOIN rezerwacje ON bilet.id_rezer = rezerwacje.id_rezer " +
                        "INNER JOIN uzytkownicy ON rezerwacje.id_uzyt = uzytkownicy.id_uzyt " +
                        "INNER JOIN seanse ON bilet.id_seansu = seanse.id_seansu " +
                        "INNER JOIN film ON seanse.id_filmu = film.id_filmu " +
                        "WHERE uzytkownicy.id_uzyt = ? " +
                        "GROUP BY bilet.id_rezer";

                // Execute the query
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, id_uzyt);
                ResultSet resultSet = statement.executeQuery();

                // Process query results
                moviesJson = "";

                ObjectMapper objectMapper = new ObjectMapper();
                ArrayNode moviesArray = objectMapper.createArrayNode();
                while (resultSet.next()) {
                    // Get values from query result columns
                    String reservationNumber = resultSet.getString("nr_rezerwacji");
                    String movieTitle = resultSet.getString("tytul");
                    int reservationId = resultSet.getInt("ilosc_biletow");
                    String seatDescription = resultSet.getString("miejsca");
                    double orderValue = resultSet.getDouble("kwota_rezer");
                    String dateReservation = resultSet.getString("data");

                    // Append movie details to the response string
                    ObjectNode movieObject = objectMapper.createObjectNode();
                    movieObject.put("reservationNumber", reservationNumber);
                    movieObject.put("movieTitle", movieTitle);
                    movieObject.put("reservationId", reservationId);
                    movieObject.put("seatDescription", seatDescription);
                    movieObject.put("orderValue", orderValue);
                    movieObject.put("dateReservation", dateReservation);

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

    @GetMapping("/seats/reserved")
    public String getReservedSeats(@RequestParam(name = "id_seansu") int id_seansu){

        Connect connect = new Connect();
        Connection connection = connect.getConnection();
        String seatsJson = "No seats found";
        if (connection != null) {
            try {

                String query = "SELECT miejsce FROM bilet WHERE bilet.id_seansu = ?";

                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, id_seansu);
                ResultSet resultSet = statement.executeQuery();

                seatsJson = "";

                ObjectMapper objectMapper = new ObjectMapper();
                ArrayNode seatsArray = objectMapper.createArrayNode();
                while (resultSet.next()) {
                    String seat = resultSet.getString("miejsce");

                    ObjectNode movieObject = objectMapper.createObjectNode();
                    movieObject.put("seat", seat);

                    seatsArray.add(movieObject);
                }

                seatsJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(seatsArray);

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
        return seatsJson;
    }

    @PostMapping("/book")
    public ResponseEntity<BookResponse> bookTickets(@RequestBody BookResponse bookResponse) {
        try {
            int userId = bookResponse.getUserId();
            int showId = bookResponse.getShowId();
            int hallId = bookResponse.getHallId();
            double price = bookResponse.getPrice();
            List<Ticket> ticketList = bookResponse.getTicketList();

            Connect connect = new Connect();
            Connection connection = connect.getConnection();
            if (connection != null) {
                try {
                    connection.setAutoCommit(false); // Enable manual transaction management

                    String lastOrderQuery = "SELECT nr_rezerwacji FROM rezerwacje ORDER BY nr_rezerwacji DESC LIMIT 1";
                    PreparedStatement lastOrderStatement = connection.prepareStatement(lastOrderQuery);
                    ResultSet lastOrderResult = lastOrderStatement.executeQuery();
                    String orderNumber;

                    if (lastOrderResult.next()) {
                        String lastOrderNumber = lastOrderResult.getString("nr_rezerwacji");
                        int orderIndex = Integer.parseInt(lastOrderNumber.substring(1)) + 1;
                        orderNumber = "K" + String.format("%04d", orderIndex);
                    } else {
                        orderNumber = "K0001";
                    }

                    String insertOrderQuery = "INSERT INTO rezerwacje (nr_rezerwacji, id_uzyt, kwota_rezer) VALUES (?, ?, ?)";
                    PreparedStatement insertOrderStatement = connection.prepareStatement(insertOrderQuery, Statement.RETURN_GENERATED_KEYS);
                    insertOrderStatement.setString(1, orderNumber);
                    insertOrderStatement.setInt(2, userId);
                    insertOrderStatement.setDouble(3, price);
                    insertOrderStatement.executeUpdate();
                    ResultSet generatedKeys = insertOrderStatement.getGeneratedKeys();
                    int orderId;

                    if (generatedKeys.next()) {
                        orderId = generatedKeys.getInt(1);

                       // Insert tickets
                        for (Ticket ticket : ticketList) {
                            /*String checkHallQuery = "SELECT * FROM sale WHERE id_sali = ?";
                            PreparedStatement checkHallStatement = connection.prepareStatement(checkHallQuery);
                            checkHallStatement.setInt(1, ticket.getId_sali());
                            ResultSet checkHallResult = checkHallStatement.executeQuery();

                            if (!checkHallResult.next()) {
                                System.out.println("Hall with id " + ticket.getId_sali() + " does not exist.");
                                connection.rollback(); // Rollback the transaction if the hall does not exist
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                            }*/

                            String insertTicketQuery = "INSERT INTO bilet (id_biletu, id_rezer, id_seansu, id_sali, miejsce, cena) VALUES (NULL, ?, ?, ?, ?, ?)";
                            PreparedStatement insertTicketStatement = connection.prepareStatement(insertTicketQuery);
                            insertTicketStatement.setInt(1, orderId);
                            insertTicketStatement.setInt(2, showId);
                            insertTicketStatement.setInt(3, hallId);
                            insertTicketStatement.setString(4, ticket.getMiejsce());
                            insertTicketStatement.setDouble(5, ticket.getCena());
                            insertTicketStatement.executeUpdate();
                            insertTicketStatement.close();
                        }

                        connection.commit(); // Commit the transaction if the update and insertion were successful
                        System.out.println("Tickets booked successfully. Order ID: " + orderId);
                        return ResponseEntity.ok(bookResponse);
                    } else {
                        connection.rollback(); // Rollback the transaction if the insertion failed
                        System.out.println("Failed to book tickets");
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    connection.rollback(); // Rollback the transaction in case of SQL error
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                } finally {
                    connect.close(); // Close the connection to the database
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    @GetMapping("/login")
    public ResponseEntity<Object> checkLogin(@RequestParam(name = "login") String login, @RequestParam(name = "password") String password) {
        Connect connect = new Connect();
        Connection connection = connect.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT * FROM uzytkownicy WHERE login = ? AND haslo = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, login);
                statement.setString(2, password);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {

                    int id = resultSet.getInt("id_uzyt");
                    String username = resultSet.getString("login");
                    String name = resultSet.getString("imie");
                    String surname = resultSet.getString("nazwisko");
                    String email = resultSet.getString("email");
                    String userPassword = resultSet.getString("haslo");
                    String address = resultSet.getString("adres");
                    String birthdate = resultSet.getString("data_ur");
                    int number = resultSet.getInt("numer_tel");

                    // Tworzenie obiektu JSON
                    ObjectMapper objectMapper = new ObjectMapper();
                    ObjectNode userJson = objectMapper.createObjectNode();
                    userJson.put("id", id);
                    userJson.put("username", username);
                    userJson.put("name", name);
                    userJson.put("surname", surname);
                    userJson.put("email", email);
                    userJson.put("password", userPassword);
                    userJson.put("address", address);
                    userJson.put("birthdate", birthdate);
                    userJson.put("number", number);

                    // Zamknięcie obiektów ResultSet i Statement
                    resultSet.close();
                    statement.close();

                    // Zwróć obiekt JSON jako odpowiedź
                    return new ResponseEntity<>(userJson.toString(), HttpStatus.OK);
                } else {
                    // Zamknięcie obiektów ResultSet i Statement
                    resultSet.close();
                    statement.close();

                    // Jeśli nie znaleziono użytkownika, zwróć odpowiedź z błędem
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Jeśli wystąpił błąd SQL, zwróć odpowiedź z błędem
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            } finally {
                connect.close(); // Zamknięcie połączenia
            }
        } else {
            // Jeśli nie udało się nawiązać połączenia z bazą danych, zwróć odpowiedź z błędem
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/users/{login}")
    public ResponseEntity<String> getUserByUsername(@PathVariable("login") String login) {
        Connect connect = new Connect();
        Connection connection = connect.getConnection();
        if (connection != null) {
            try {
                // Create SQL query
                String query = "SELECT * FROM uzytkownicy WHERE login = ?";

                // Execute the query
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, login);
                ResultSet resultSet = statement.executeQuery();

                // Process query result
                if (resultSet.next()) {
                    // Get values from query result columns
                    int id = resultSet.getInt("id_uzyt");
                    String name = resultSet.getString("imie");
                    String surname = resultSet.getString("nazwisko");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("haslo");
                    String address = resultSet.getString("adres");
                    String birthdate = resultSet.getString("data_ur");
                    int number = resultSet.getInt("numer_tel");

                    // Create a JSON object for the user
                    ObjectMapper objectMapper = new ObjectMapper();
                    ObjectNode userObject = objectMapper.createObjectNode();
                    userObject.put("id_uzyt", id);
                    userObject.put("login", login);
                    userObject.put("imie", name);
                    userObject.put("nazwisko", surname);
                    userObject.put("email", email);
                    userObject.put("haslo", password);
                    userObject.put("adres", address);
                    userObject.put("data_ur", birthdate);
                    userObject.put("numer_tel", number);

                    // Convert the user object to a JSON string
                    String userJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(userObject);

                    // Set the response headers
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    // Return the response with the JSON string and headers
                    return new ResponseEntity<>(userJson, headers, HttpStatus.OK);
                }

                // Close ResultSet and Statement objects
                resultSet.close();
                statement.close();
            } catch (SQLException | JsonProcessingException e) {
                e.printStackTrace();
            } finally {
                connect.close(); // Close the connection
            }
        }

        // Return a response indicating no user found
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> addUser(@RequestBody RegistrationResponse registrationResponse) {

        String login = registrationResponse.getLogin();
        String password = registrationResponse.getPassword();
        String email = registrationResponse.getEmail();

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

                String emailQuery = "SELECT id_uzyt FROM uzytkownicy WHERE email = ?";
                PreparedStatement emailStatement = connection.prepareStatement(emailQuery);
                emailStatement.setString(1, email);
                ResultSet emailResultSet = emailStatement.executeQuery();

                if (emailResultSet.next()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RegistrationResponse("Konto z podanym adresem e-mail już istnieje"));
                } else if (resultSet.next()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RegistrationResponse("Podany login jest już zajęty"));
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
                        return ResponseEntity.ok(new RegistrationResponse("User registered successfully"));
                    } else {
                        connection.rollback(); // rollback the transaction
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RegistrationResponse("Failed to register user"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    connection.rollback(); // rollback the transaction in case of SQL error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return ResponseEntity.ok(new RegistrationResponse("An error occurred during registration"));
            } finally {
                connect.close(); // close the database connection
            }
        }
        return null;
    }

    @PostMapping("/user/data")
    public ResponseEntity<UserData> editUser(@RequestParam(name = "id") String userID, @RequestBody UserData userData){
        String name = userData.getName();
        String surname = userData.getSurname();
        String email = userData.getEmail();
        String phoneNumber = userData.getPhoneNumber();
        String homeAddress = userData.getHomeAddress();
        String birthDate = userData.getBirthDate();

        int parseUserID = Integer.parseInt(userID);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Określ format daty
        LocalDate localDate = LocalDate.parse(birthDate, formatter); // Parsuj string jako LocalDate
        Date sqlDate = Date.valueOf(localDate);
        int parsePhoneNumber = Integer.parseInt(phoneNumber);

        Connect connect = new Connect();
        Connection connection = connect.getConnection();
        if (connection != null) {
            try {
                connection.setAutoCommit(false); // enable manual transaction management

                String emailQuery = "SELECT email FROM uzytkownicy WHERE uzytkownicy.id_uzyt = ?";
                PreparedStatement emailStatement = connection.prepareStatement(emailQuery);
                emailStatement.setInt(1, parseUserID);
                ResultSet emailResultSet = emailStatement.executeQuery();

                if (emailResultSet.next()) {
                    String currentEmail = emailResultSet.getString("email");

                    // If the new email is the same as the current one, proceed with the update
                    if (currentEmail.equals(email)) {
                        String updateQuery = "UPDATE uzytkownicy SET imie = ?, nazwisko = ?, email = ?, adres = ?, data_ur = ?, numer_tel = ? WHERE id_uzyt = ?";
                        PreparedStatement statement = connection.prepareStatement(updateQuery);
                        statement.setString(1, name);
                        statement.setString(2, surname);
                        statement.setString(3, email);
                        statement.setString(4, homeAddress);
                        statement.setDate(5, sqlDate);
                        statement.setInt(6, parsePhoneNumber);
                        statement.setInt(7, parseUserID);

                        int rowsAffected = statement.executeUpdate();

                        emailResultSet.close();
                        statement.close();

                        if (rowsAffected == 1) {
                            connection.commit();

                            return ResponseEntity.ok( new UserData("User data updated successfully"));
                        } else {
                            connection.rollback();
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserData("Failed to update user data"));
                        }
                    } else {
                        // Check if the new email already exists in the database
                        String emailExistQuery = "SELECT COUNT(*) AS count FROM uzytkownicy WHERE email = ?";
                        PreparedStatement emailExistStatement = connection.prepareStatement(emailExistQuery);
                        emailExistStatement.setString(1, email);
                        ResultSet emailExistResultSet = emailExistStatement.executeQuery();

                        if (emailExistResultSet.next()) {
                            int count = emailExistResultSet.getInt("count");

                            if(count > 0) {
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserData("User with the given email already exists"));
                            } else {
                                // Proceed with the update
                                String updateQuery = "UPDATE uzytkownicy SET imie = ?, nazwisko = ?, email = ?, adres = ?, data_ur = ?, numer_tel = ? WHERE id_uzyt = ?";
                                PreparedStatement statement = connection.prepareStatement(updateQuery);
                                statement.setString(1, name);
                                statement.setString(2, surname);
                                statement.setString(3, email);
                                statement.setString(4, homeAddress);
                                statement.setDate(5, sqlDate);
                                statement.setInt(6, parsePhoneNumber);
                                statement.setInt(7, parseUserID);

                                int rowsAffected = statement.executeUpdate();

                                emailExistResultSet.close();
                                statement.close();
                                if (rowsAffected == 1) {
                                    connection.commit();
                                    return ResponseEntity.ok(new UserData("User data updated successfully"));
                                } else {
                                    connection.rollback();
                                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserData("Failed to update user data"));
                                }
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    connection.rollback(); // rollback the transaction in case of SQL error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return ResponseEntity.ok(new UserData("An error occurred during changing data"));
            } finally {
                connect.close(); // close the database connection
            }
        }
        return null;
    }

    @PostMapping("/user/login")
    public ResponseEntity<String> editUserLogin(@RequestParam(name = "id") String userID, @RequestParam(name = "login") String login){

        int parseUserID = Integer.parseInt(userID);

        Connect connect = new Connect();
        Connection connection = connect.getConnection();
        if (connection != null) {
            try {
                connection.setAutoCommit(false); // enable manual transaction management

                String loginQuery = "SELECT login FROM uzytkownicy WHERE uzytkownicy.id_uzyt = ?";
                PreparedStatement loginStatement = connection.prepareStatement(loginQuery);
                loginStatement.setInt(1, parseUserID);
                ResultSet loginResultSet = loginStatement.executeQuery();


                if (loginResultSet.next()){

                    String currentLogin = loginResultSet.getString("login");

                    if (currentLogin.equals(login)){

                        String query = "UPDATE uzytkownicy SET login = ? WHERE uzytkownicy.id_uzyt = ?";

                        // Prepare SQL statement with parameters
                        PreparedStatement statement = connection.prepareStatement(query);
                        statement.setString(1, login);
                        statement.setInt(2, parseUserID);

                        // Execute SQL statement
                        int rowsAffected = statement.executeUpdate();

                        loginResultSet.close();
                        statement.close();
                        if (rowsAffected == 1) { // If exactly one row was inserted
                            connection.commit(); // commit the transaction
                            return ResponseEntity.status(HttpStatus.OK).build();
                        } else {
                            connection.rollback(); // rollback the transaction
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                        }
                    } else {
                        String loginExistQuery = "SELECT COUNT(*) AS count FROM uzytkownicy WHERE login = ?";
                        PreparedStatement loginExistStatement = connection.prepareStatement(loginExistQuery);
                        loginExistStatement.setString(1, login);
                        ResultSet loginExistResultSet = loginExistStatement.executeQuery();

                        if (loginExistResultSet.next()) {
                            int count = loginExistResultSet.getInt("count");
                            if(count > 0) {
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with the given login already exists");
                            } else {
                                String query = "UPDATE uzytkownicy SET login = ? WHERE uzytkownicy.id_uzyt = ?";

                                // Prepare SQL statement with parameters
                                PreparedStatement statement = connection.prepareStatement(query);
                                statement.setString(1, login);
                                statement.setInt(2, parseUserID);

                                // Execute SQL statement
                                int rowsAffected = statement.executeUpdate();

                                loginExistResultSet.close();
                                statement.close();
                                if (rowsAffected == 1) { // If exactly one row was inserted
                                    connection.commit(); // commit the transaction
                                    return ResponseEntity.status(HttpStatus.OK).build();
                                } else {
                                    connection.rollback(); // rollback the transaction
                                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                                }
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    connection.rollback(); // rollback the transaction in case of SQL error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            } finally {
                connect.close(); // close the database connection
            }
        }
        return null;
    }

    @PostMapping("/user/email")
    public ResponseEntity<String> forgotPassUserEmail(@RequestParam(name = "email") String email){

        Connect connect = new Connect();
        Connection connection = connect.getConnection();
        if (connection != null) {
            try {
                connection.setAutoCommit(false); // enable manual transaction management

                String emailQuery = "SELECT id_uzyt FROM uzytkownicy WHERE uzytkownicy.email = ?";
                PreparedStatement emailStatement = connection.prepareStatement(emailQuery);
                emailStatement.setString(1, email);
                ResultSet resultSet = emailStatement.executeQuery();



                if (resultSet.next()) {
                    int userID = resultSet.getInt("id_uzyt");
                    resultSet.close();
                    emailStatement.close();
                    return ResponseEntity.ok().body(String.valueOf(userID));
                } else {
                    resultSet.close();
                    emailStatement.close();
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    connection.rollback(); // rollback the transaction in case of SQL error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            } finally {
                connect.close(); // close the database connection
            }
        }
        return null;
    }

    @PostMapping("/user/password")
    public ResponseEntity<String> forgotPassUserPassword(@RequestParam(name = "id") String userID, @RequestParam(name = "password") String password){

        int parseUserID = Integer.parseInt(userID);

        Connect connect = new Connect();
        Connection connection = connect.getConnection();
        if (connection != null) {
            try {
                connection.setAutoCommit(false); // enable manual transaction management

                String passQuery = "UPDATE uzytkownicy SET haslo = ? WHERE uzytkownicy.id_uzyt = ?";
                PreparedStatement passwdStatement = connection.prepareStatement(passQuery);
                passwdStatement.setString(1, password);
                passwdStatement.setInt(2, parseUserID); // Poprawna numeracja parametru
                int rowsAffected = passwdStatement.executeUpdate(); // Użycie executeUpdate() do zapytania aktualizacji

                if (rowsAffected > 0) {
                    connection.commit(); // potwierdzenie transakcji w przypadku sukcesu
                    return ResponseEntity.status(HttpStatus.OK).build();
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    connection.rollback(); // rollback the transaction in case of SQL error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            } finally {
                connect.close(); // close the database connection
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}