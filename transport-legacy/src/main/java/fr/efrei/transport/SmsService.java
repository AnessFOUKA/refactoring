package fr.efrei.transport;

import java.util.ArrayList;
import java.util.List;

public class SmsService {

    // garde une trace des SMS envoyés (facturés par l'opérateur)
    public static List<String[]> journal = new ArrayList<String[]>();

    private String apiKey = "sk_live_8f7d6e5c4b3a2918";

    public void envoyerSms(String numero, String message) {
        if (message.length() > 160) {
            message = message.substring(0, 157) + "...";
        }
        System.out.println("[SMS] -> " + numero + " : " + message);
        journal.add(new String[]{numero, message});
    }
}
