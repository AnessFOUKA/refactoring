package fr.efrei.transfret;

public class CanalEmailConsole implements CanalNotification {

    private static final String SMTP_HOST = "smtp.transfret.fr";
    private static final String EXPEDITEUR = "noreply@transfret.fr";

    @Override
    public CanalMessage canal() {
        return CanalMessage.EMAIL;
    }

    @Override
    public boolean envoyer(String destinataire, String sujet, String contenu) {
        if (destinataire == null || !destinataire.contains("@")) {
            return false;
        }
        System.out.println("[EMAIL via " + SMTP_HOST + "] " + EXPEDITEUR + " -> " + destinataire + " | " + sujet);
        System.out.println("        " + contenu);
        return true;
    }
}
