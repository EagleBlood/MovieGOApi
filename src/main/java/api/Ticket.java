package api;

public class Ticket {
    private int id_seansu;
    private int id_sali;
    private int b_rzad;
    private int b_kolumna;

    private double cena;

    public Ticket() {
        // Pusty konstruktor wymagany przez deserializację JSON
    }

    public Ticket(int id_sali, int id_seansu, double cena, int b_rzad, int b_kolumna) {
        this.id_sali = id_sali;
        this.id_seansu = id_seansu;
        this.cena = cena;
        this.b_rzad = b_rzad;
        this.b_kolumna = b_kolumna;
    }

    public int getId_seansu() {
        return id_seansu;
    }

    public double getCena() {
        return cena;
    }

    public int getId_sali() {
        return id_sali;
    }

    public int getB_rzad() {
        return b_rzad;
    }

    public int getB_kolumna() {
        return b_kolumna;
    }

    public void setId_seansu(int id_seansu) {
        this.id_seansu = id_seansu;
    }

    public void setId_sali(int id_sali) {
        this.id_sali = id_sali;
    }

    public void setB_rzad(int b_rzad) {
        this.b_rzad = b_rzad;
    }

    public void setB_kolumna(int b_kolumna) {
        this.b_kolumna = b_kolumna;
    }

    public void setCena(double cena) {
        this.cena = cena;
    }
}
