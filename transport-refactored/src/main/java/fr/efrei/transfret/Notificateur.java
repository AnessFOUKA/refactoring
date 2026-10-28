package fr.efrei.transfret;

public class Notificateur {

    public void envoyerEmail(String destinataire, String sujet, String corps) {
        EmailService.envoyer(destinataire, sujet, corps);
    }

    public void notifier(Client client, String sujet, String corps, String sms) {
        envoyerEmail(client.getEmail(), sujet, corps);
        if (client.getType() == TypeClient.PREMIUM) {
            new SmsService().envoyerSms(client.getTelephone(), sms);
        }
    }
}
