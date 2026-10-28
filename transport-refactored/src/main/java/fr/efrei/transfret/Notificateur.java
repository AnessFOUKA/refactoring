package fr.efrei.transfret;

import java.util.ArrayList;
import java.util.List;

public class Notificateur {

    private final CanalNotification email;
    private final CanalNotification sms;
    private final List<NotificationEnvoyee> journal = new ArrayList<>();

    public Notificateur(CanalNotification email, CanalNotification sms) {
        if (email.canal() != CanalMessage.EMAIL || sms.canal() != CanalMessage.SMS) {
            throw new IllegalArgumentException("Les canaux email et SMS doivent être fournis dans le bon ordre");
        }
        this.email = email;
        this.sms = sms;
    }

    public void envoyerEmail(String destinataire, String sujet, String corps) {
        enregistrerSiEnvoye(email, destinataire, sujet, corps);
    }

    public void notifier(Client client, String sujet, String corps, String sms) {
        envoyerEmail(client.getEmail(), sujet, corps);
        if (client.getType() == TypeClient.PREMIUM) {
            enregistrerSiEnvoye(this.sms, client.getTelephone(), sujet, sms);
        }
    }

    public List<NotificationEnvoyee> notifications() {
        return List.copyOf(journal);
    }

    private void enregistrerSiEnvoye(CanalNotification canal, String destinataire, String sujet, String contenu) {
        if (canal.envoyer(destinataire, sujet, contenu)) {
            journal.add(new NotificationEnvoyee(canal.canal(), destinataire, sujet, contenu));
        }
    }
}
