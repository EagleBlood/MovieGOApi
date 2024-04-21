package api;

public class Hall {

    int id_sali;
    int numer;
    int s_rzedy;
    int s_kolumny;

    public Hall() {
        // Pusty konstruktor wymagany przez deserializację JSON
    }

    public Hall(int numer, int s_rzedy, int s_kolumny) {
        this.numer = numer;
        this.s_rzedy = s_rzedy;
        this.s_kolumny = s_kolumny;
    }

    public int getId_sali() {
        return id_sali;
    }

    public int getNumer() {
        return numer;
    }

    public int getS_rzedy() {
        return s_rzedy;
    }
    
}
