package api;

import java.util.List;

public class BookResponse {

    private int userId;
    private int showId;
    private int hallId;
    private double price;
    private List<Ticket> ticketList;

    public BookResponse() {
        // Pusty konstruktor wymagany przez deserializację JSON
    }

    public BookResponse(int userId, int showId, int hallId, double price, List<Ticket> ticketList) {
        this.userId = userId;
        this.showId = showId;
        this.hallId = hallId;
        this.price = price;
        this.ticketList = ticketList;
    }

    public int getShowId() {
        return showId;
    }

    public int getHallId() {
        return hallId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Ticket> getTicketList() {
        return ticketList;
    }

    public void setTicketList(List<Ticket> ticketList) {
        this.ticketList = ticketList;
    }
}
