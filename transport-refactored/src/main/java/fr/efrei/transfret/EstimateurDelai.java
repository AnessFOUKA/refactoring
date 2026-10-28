package fr.efrei.transfret;

public class EstimateurDelai {

    private static final double VITESSE_ROUTE_KM_PAR_HEURE = 70.0;
    private static final double HEURES_CONDUITE_PAR_JOUR = 9;
    private static final double HEURES_CONDUITE_URGENTE_PAR_JOUR = 18;
    private static final double DISTANCE_RAIL_PAR_JOUR = 1_440.0;
    private static final int JOURS_SUPPLEMENTAIRES_RAIL = 1;
    private static final double DISTANCE_MER_PAR_JOUR = 720.0;
    private static final int JOURS_SUPPLEMENTAIRES_MER = 2;
    private static final int SEUIL_LONG_COURRIER_AERIEN_KM = 3_000;
    private static final int DELAI_AERIEN_COURT_COURRIER = 1;
    private static final int DELAI_AERIEN_LONG_COURRIER = 2;
    private static final double DISTANCE_FLUVIALE_PAR_JOUR = 360.0;
    private static final int JOURS_SUPPLEMENTAIRES_FLUVIAUX = 1;

    public int estimer(int distance, ModeTransport mode, boolean urgent) {
        int jours = switch (mode) {
            case ROUTE -> estimerRoute(distance, urgent);
            case RAIL -> (int) Math.ceil(distance / DISTANCE_RAIL_PAR_JOUR)
                    + JOURS_SUPPLEMENTAIRES_RAIL - (urgent ? 1 : 0);
            case MER -> (int) Math.ceil(distance / DISTANCE_MER_PAR_JOUR) + JOURS_SUPPLEMENTAIRES_MER;
            case AIR -> estimerAir(distance, urgent);
            case FLUVIAL -> (int) Math.ceil(distance / DISTANCE_FLUVIALE_PAR_JOUR)
                    + JOURS_SUPPLEMENTAIRES_FLUVIAUX;
        };
        return Math.max(jours, 1);
    }

    private int estimerRoute(int distance, boolean urgent) {
        double heures = distance / VITESSE_ROUTE_KM_PAR_HEURE;
        double heuresConduiteParJour = urgent
                ? HEURES_CONDUITE_URGENTE_PAR_JOUR
                : HEURES_CONDUITE_PAR_JOUR;
        return (int) Math.ceil(heures / heuresConduiteParJour);
    }

    private int estimerAir(int distance, boolean urgent) {
        if (urgent || distance <= SEUIL_LONG_COURRIER_AERIEN_KM) {
            return DELAI_AERIEN_COURT_COURRIER;
        }
        return DELAI_AERIEN_LONG_COURRIER;
    }
}
