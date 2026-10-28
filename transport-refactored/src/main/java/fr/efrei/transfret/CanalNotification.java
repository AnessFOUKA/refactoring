package fr.efrei.transfret;

public interface CanalNotification {

    CanalMessage canal();

    boolean envoyer(String destinataire, String sujet, String contenu);
}
