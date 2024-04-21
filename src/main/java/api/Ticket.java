package api;

public class Ticket {
    private String miejsce;

    private double cena;

    public Ticket() {
        // Pusty konstruktor wymagany przez deserializację JSON
    }

    public Ticket(double cena, String miejsce) {
        this.cena = cena;
        this.miejsce = miejsce;
    }

       public double getCena() {
        return cena;
    }
    public String getMiejsce() {
        return miejsce;
    }

    public void setMiejsce(String miejsce) {
        this.miejsce = miejsce;
    }

    public void setCena(double cena) {
        this.cena = cena;
    }
}
