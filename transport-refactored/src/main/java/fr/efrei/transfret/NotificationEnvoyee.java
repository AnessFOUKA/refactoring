package fr.efrei.transfret;

public record NotificationEnvoyee(CanalMessage canal, String destinataire, String sujet, String contenu) {
}
