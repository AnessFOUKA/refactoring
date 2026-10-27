package fr.efrei.transport;

import java.util.ArrayList;
import java.util.List;

public class Flotte {

    public static List<Vehicule> vehicules = new ArrayList<Vehicule>();

    public static void initialiser() {
        vehicules.clear();
        vehicules.add(new Camion("AB-123-CD", 19000, false));
        vehicules.add(new Camion("EF-456-GH", 12000, true));
        vehicules.add(new CamionFrigorifique("IJ-789-KL", 8000));
        vehicules.add(new Train("TR-0001", 600000));
        vehicules.add(new Bateau("CMA-CGM-01", 20000000, true));
        vehicules.add(new Avion("F-GTXA", 100000));
    }
}
