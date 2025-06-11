package gac.andrzej.sklep;

import java.sql.Date;
import java.util.Objects;

public class Towar {
    private final int idTowaru;
    private final String nazwa;
    private final String opis;
    private final double cenaJednostkowa;
    private final int iloscDostepna;
    private final Date dataDodania;

    public Towar(int idTowaru, String nazwa, String opis, double cenaJednostkowa, int iloscDostepna, Date dataDodania) {
        this.idTowaru = idTowaru;
        this.nazwa = nazwa;
        this.opis = opis;
        this.cenaJednostkowa = cenaJednostkowa;
        this.iloscDostepna = iloscDostepna;
        this.dataDodania = dataDodania;
    }

    public int getIdTowaru() {
        return idTowaru;
    }

    public String getNazwa() {
        return nazwa;
    }

    public String getOpis() {
        return opis;
    }

    public double getCenaJednostkowa() {
        return cenaJednostkowa;
    }

    public int getIloscDostepna() {
        return iloscDostepna;
    }

    public Date getDataDodania() {
        return dataDodania;
    }

    @Override
    public String toString() {
        return idTowaru + " " + nazwa + " " + opis + " " + cenaJednostkowa + " " + iloscDostepna + " " + dataDodania;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Towar towar = (Towar) o;
        return idTowaru == towar.idTowaru &&
                Double.compare(towar.cenaJednostkowa, cenaJednostkowa) == 0 &&
                iloscDostepna == towar.iloscDostepna &&
                nazwa.equals(towar.nazwa) &&
                Objects.equals(opis, towar.opis) &&
                dataDodania.equals(towar.dataDodania);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTowaru, nazwa, opis, cenaJednostkowa, iloscDostepna, dataDodania);
    }
}