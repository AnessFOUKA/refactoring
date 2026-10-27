package fr.efrei.transport.contrat;

/** Dimensions en centimètres, poids en kilogrammes, valeur en euros. */
public record ColisDto(String description, double poidsKg, double longueurCm, double largeurCm, double hauteurCm,
                       TypeMarchandise type, double valeurDeclaree) {
}
