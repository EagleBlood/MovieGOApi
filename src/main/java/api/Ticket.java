package api;

public class Ticket {
    private int miejsce;

    private double cena;

    public Ticket() {
        // Pusty konstruktor wymagany przez deserializację JSON
    }

    public Ticket(double cena, int miejsce) {
        this.cena = cena;
        this.miejsce = miejsce;
    }

       public double getCena() {
        return cena;
    }
    public int getMiejsce() {
        return miejsce;
    }

    public void setMiejsce(int miejsce) {
        this.miejsce = miejsce;
    }

    public void setCena(double cena) {
        this.cena = cena;
    }
}
