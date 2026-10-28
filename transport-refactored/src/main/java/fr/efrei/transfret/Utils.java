package fr.efrei.transfret;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("dd/MM/yyyy");

    public static double arrondir(double valeur) {
        return Math.round(valeur * 100.0) / 100.0;
    }

    public static String formaterDate(Date d) {
        if (d == null) {
            return "";
        }
        return FORMAT_DATE.format(d);
    }

    public static String formaterPrix(double prix) {
        return String.format("%.2f €", prix);
    }

    public static boolean emailValide(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    // retourne la distance en km entre deux villes, -1 si inconnue
    public static int getDistance(String villeA, String villeB) {
        String a = villeA.toUpperCase();
        String b = villeB.toUpperCase();
        if ((a.equals("PARIS") && b.equals("LYON")) || (a.equals("LYON") && b.equals("PARIS"))) {
            return 465;
        } else if ((a.equals("PARIS") && b.equals("MARSEILLE")) || (a.equals("MARSEILLE") && b.equals("PARIS"))) {
            return 775;
        } else if ((a.equals("PARIS") && b.equals("LILLE")) || (a.equals("LILLE") && b.equals("PARIS"))) {
            return 225;
        } else if ((a.equals("PARIS") && b.equals("BORDEAUX")) || (a.equals("BORDEAUX") && b.equals("PARIS"))) {
            return 585;
        } else if ((a.equals("LYON") && b.equals("MARSEILLE")) || (a.equals("MARSEILLE") && b.equals("LYON"))) {
            return 315;
        } else if ((a.equals("LE HAVRE") && b.equals("NEW YORK")) || (a.equals("NEW YORK") && b.equals("LE HAVRE"))) {
            return 5850;
        } else if ((a.equals("MARSEILLE") && b.equals("ALGER")) || (a.equals("ALGER") && b.equals("MARSEILLE"))) {
            return 760;
        } else if ((a.equals("PARIS") && b.equals("NEW YORK")) || (a.equals("NEW YORK") && b.equals("PARIS"))) {
            return 5840;
        }
        return -1;
    }
}
