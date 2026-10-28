package fr.efrei.transfret;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Flotte.initialiser();
        ServiceClient serviceClient = new ServiceClient();
        ServiceExpedition serviceExpedition = new ServiceExpedition();

        System.out.println("\n----- Clients -----");
        Client dupont = serviceClient.creerClient("Jean Dupont", "jean.dupont@mail.fr", "0601020304", "12 rue de la Paix, 75002 Paris", TypeClient.PARTICULIER, null);
        Client transco = serviceClient.creerClient("Transco SA", "contact@transco.fr", "0144556677", "ZI des Chanteraines, 92390 Villeneuve", TypeClient.ENTREPRISE, "12345678900012");
        Client logiplus = serviceClient.creerClient("LogiPlus", "ops@logiplus.com", "0788990011", "3 quai du Port, 13002 Marseille", TypeClient.PREMIUM, null);

        System.out.println("\n----- Expéditions -----");
        List<Colis> colis1 = new ArrayList<Colis>();
        colis1.add(new Colis("Cartons livres", 35, 60, 40, 40, TypeMarchandise.STANDARD, 300));
        colis1.add(new Colis("Vaisselle", 12, 50, 50, 30, TypeMarchandise.FRAGILE, 800));
        Expedition exp1 = serviceExpedition.creerExpedition(dupont, colis1, "Paris", "Lyon", ModeTransport.ROUTE, false, true);

        List<Colis> colis2 = new ArrayList<Colis>();
        colis2.add(new Colis("Fûts de solvant", 5000, 240, 120, 150, TypeMarchandise.DANGEREUX, 20000));
        Expedition exp2 = serviceExpedition.creerExpedition(transco, colis2, "Paris", "Marseille", ModeTransport.ROUTE, true, false);

        List<Colis> colis3 = new ArrayList<Colis>();
        colis3.add(new Colis("Conteneur machines-outils", 15000, 1200, 235, 239, TypeMarchandise.STANDARD, 250000));
        serviceExpedition.creerExpedition(logiplus, colis3, "Le Havre", "New York", ModeTransport.MER, false, true);

        List<Colis> colis4 = new ArrayList<Colis>();
        colis4.add(new Colis("Batteries lithium", 80, 80, 60, 50, TypeMarchandise.DANGEREUX, 4000));
        serviceExpedition.creerExpedition(dupont, colis4, "Paris", "New York", ModeTransport.AIR, false, false); // refusée

        System.out.println("\n----- Devis -----");
        List<Colis> palette = new ArrayList<Colis>();
        palette.add(new Colis("Palette", 800, 120, 80, 150, TypeMarchandise.STANDARD, 2000));
        System.out.println("Paris -> Bordeaux ROUTE : " + serviceExpedition.calculerDevis(palette, "Paris", "Bordeaux", ModeTransport.ROUTE, false, false, TypeClient.PARTICULIER) + " € TTC");

        System.out.println("\n----- Cycle de vie -----");
        serviceExpedition.demarrerExpedition(exp1.getId());
        serviceExpedition.livrerExpedition(exp1.getId());
        serviceExpedition.demarrerExpedition(exp2.getId());
        serviceExpedition.annulerExpedition(exp2.getId());

        System.out.println("\n----- Factures -----");
        GenerateurFacture generateurFacture = new GenerateurFacture();
        System.out.println(generateurFacture.generer(exp1));
        System.out.println(generateurFacture.generer(exp2));
    }
}
