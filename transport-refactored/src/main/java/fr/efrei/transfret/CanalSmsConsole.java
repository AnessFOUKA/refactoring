package fr.efrei.transfret;

public class CanalSmsConsole implements CanalNotification {

    private static final int LONGUEUR_MAXIMALE_SMS = 160;
    private static final int LONGUEUR_CONTENU_SMS_AVANT_SUFFIXE = LONGUEUR_MAXIMALE_SMS - 3;

    @Override
    public CanalMessage canal() {
        return CanalMessage.SMS;
    }

    @Override
    public boolean envoyer(String destinataire, String sujet, String contenu) {
        String message = contenu;
        if (message.length() > LONGUEUR_MAXIMALE_SMS) {
            message = message.substring(0, LONGUEUR_CONTENU_SMS_AVANT_SUFFIXE) + "...";
        }
        System.out.println("[SMS] -> " + destinataire + " : " + message);
        return true;
    }
}
