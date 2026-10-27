package fr.efrei.transport;

public class ServiceClient {

    public Client creerClient(String nom, String email, String telephone, String adresse, String type, String siret) {
        if (nom == null || nom.trim().length() == 0) {
            System.out.println("ERREUR : nom obligatoire");
            return null;
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            System.out.println("ERREUR : email invalide (" + email + ")");
            return null;
        }
        if (!type.equals("PARTICULIER") && !type.equals("ENTREPRISE") && !type.equals("PREMIUM")) {
            System.out.println("ERREUR : type de client inconnu");
            return null;
        }
        if (type.equals("ENTREPRISE") && (siret == null || siret.length() != 14)) {
            System.out.println("ERREUR : SIRET obligatoire (14 chiffres) pour une entreprise");
            return null;
        }
        Client c = new Client(nom, email, telephone, adresse, type, siret);
        Database.getInstance().sauvegarderClient(c);
        EmailService.envoyer(c.getEmail(), "Bienvenue chez TransFret", "Bonjour " + c.getNom() + ", votre compte " + c.getType().toLowerCase() + " a été créé.");
        return c;
    }
}
