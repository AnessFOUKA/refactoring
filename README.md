# TransFret – Atelier de refactoring

TransFret est une (fausse) PME de transport de marchandises. Son application de gestion a été écrite il y a
quelques années par des stagiaires successifs. Elle fonctionne, mais chaque évolution est pénible : ajouter un
mode de transport ou un supplément oblige à modifier du code un peu partout.

Votre mission : **refactorer ce code sans changer son comportement**, en appliquant les principes SOLID et les
patrons **Strategy** et **Decorator**.

## Organisation du projet

```
refactoring/
├── pom.xml                 POM parent (Java 25, JUnit 5)
├── transport-contrat/      Tests d'acceptation communs aux deux implémentations
├── transport-legacy/       Code historique : référence, ne pas modifier
└── transport-refactored/   Votre code
```

### Les tests de contrat
Le comportement attendu est décrit **une seule fois**, dans `transport-contrat` :

- `SystemeTransport` : interface qui expose les cas d'usage avec des types neutres
  (`DemandeClient`, `ColisDto`, `EtatExpedition`…) ;
- `ContratTransportTest` : 68 scénarios (clients, tarifs, devis, règles d'acceptation, véhicules, délais,
  cycle de vie, notifications, factures).

Chaque implémentation branche son code sur ces tests au moyen d'un **adaptateur** (patron *Adapter*) :

| Module | Adaptateur | Test |
|---|---|---|
| `transport-legacy` | `LegacyAdapter` | `LegacyContratTest` : passe ✅ |
| `transport-refactored` | `NouveauSystemeAdapter` (à écrire) | `NouveauSystemeContratTest` (`@Disabled` au départ) |

Les tests ne connaissent aucune classe interne : vous êtes libres de remodeler entièrement le code,
tant que l'adaptateur peut traduire les appels.

### Commandes

```bash
mvn install
```

Lance tous les tests. Pour la démo du code historique :

```bash
java -cp transport-legacy/target/classes fr.efrei.transport.Main
```

### Démarrer
Suivre la [feuille de route](docs/feuille-de-route.md).

## Le code historique

| Classe | Rôle |
|---|---|
| `ServiceClient` | création et validation des clients |
| `ServiceExpedition` | création d'une expédition, devis, délais, départ, livraison, annulation, notifications |
| `CalculateurPrix` | calcul du prix HT |
| `AffectationVehicule`, `Flotte` | choix du véhicule |
| `Vehicule`, `Camion`, `CamionFrigorifique`, `Train`, `Bateau`, `Avion` | la flotte |
| `Client`, `Colis`, `Expedition` | le modèle |
| `Database`, `EmailService`, `SmsService` | persistance et notifications (simulées) |
| `Utils` | dates et table des distances |

## Règles métier

Ces règles **doivent être conservées à l'identique**.

### Clients
| Type | Contraintes | Remise |
|---|---|---|
| PARTICULIER | — | aucune |
| ENTREPRISE | SIRET obligatoire (14 caractères) | 10 %, ou 15 % si le prix dépasse 5 000 € |
| PREMIUM | — | 20 % + notifications SMS en plus des emails |

Nom obligatoire, email valide (contient `@` et `.`).

### Marchandises
Chaque colis a un poids (kg), des dimensions (cm), une valeur déclarée et un type :
`STANDARD`, `FRAGILE`, `DANGEREUX`, `REFRIGERE`.

### Calcul du prix HT
1. **Tarif de base** selon le mode :

   | Mode | Tarif | Minimum |
   |---|---|---|
   | ROUTE | 1,20 €/km + 0,10 €/kg | — |
   | RAIL | 0,80 €/km + 0,07 €/kg | 150 € |
   | MER | 0,50 €/km + 0,03 €/kg | 500 € |
   | AIR | 0,60 €/km + 4,50 €/kg taxable | — |

   En aérien, le poids taxable est le maximum entre le poids réel et le poids volumétrique (volume m³ × 167).
2. **Suppléments**, appliqués l'un après l'autre s'il y a au moins un colis concerné :
   fragile ×1,15 ; dangereux ×1,30 ; réfrigéré ×1,20.
3. **Urgence** : ×1,5.
4. **Remise client** (tableau ci-dessus), calculée sur le prix obtenu.
5. **Assurance** (optionnelle) : + 2 % de la valeur déclarée totale, minimum 15 €.
6. Arrondi au centime. TTC = HT × 1,2, arrondi au centime.

### Règles d'acceptation
- Matières dangereuses interdites en aérien.
- Réfrigéré uniquement par route ou par mer.
- Pas d'option urgente en maritime.
- Le trajet doit être connu (table des distances).
- Un véhicule compatible et disponible est requis :
  - ROUTE : camion (frigorifique si réfrigéré, certifié pour les matières dangereuses si besoin) ; le camion
    frigorifique ne transporte que du réfrigéré ;
  - RAIL : train ; MER : bateau ; AIR : avion.

### Délais de livraison (jours, minimum 1)
| Mode | Calcul |
|---|---|
| ROUTE | distance / 70 km/h, 9 h de conduite par jour (18 h si urgent) |
| RAIL | distance / 1 440 arrondi au supérieur, + 1 jour (− 1 si urgent) |
| MER | distance / 720 arrondi au supérieur, + 2 jours |
| AIR | 1 jour (2 si plus de 3 000 km, 1 si urgent) |

### Cycle de vie
```
CREEE ──démarrer──▶ EN_COURS ──livrer──▶ LIVREE
  │                    │
  └──annuler──▶ ANNULEE ◀──annuler (frais : 50 % du TTC)
```
Chaque étape envoie un email au client, plus un SMS pour les clients PREMIUM.
