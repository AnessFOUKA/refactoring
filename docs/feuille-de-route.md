# Feuille de route du refactoring

Deux séances de 7 h. On ne réécrit pas TransFret d'un bloc : on le **transforme par petites étapes**, en
gardant les 68 tests de contrat verts à chaque étape. Si un test passe au rouge, on corrige tout de suite ou on
annule la dernière modification.

## Règles du jeu

1. **On ne modifie jamais `transport-contrat` ni `transport-legacy`.**
2. **Une étape = un commit**, avec un message qui nomme le refactoring (ex. `Strategy : tarif par mode`).
3. **On lance `mvn install` avant chaque commit.**
4. On privilégie les refactorings automatiques de l'IDE (*Rename*, *Extract Method*, *Extract Interface*…).
5. On travaille en binôme : l'un code, l'autre relit, et on change de rôle à chaque étape.

| Séance | Étapes |
|---|---|
| 1 | 0 Mise en place · 1 Diagnostic · 2 Nombres magiques et enums · 3 Responsabilité unique · 4 Inversion des dépendances |
| 2 | 5 Strategy · 6 Decorator · 7 Véhicules (Liskov, ségrégation des interfaces) · 8 Preuve par l'extension |

---

# Séance 1

## Étape 0 – Mise en place (30 min)

**But :** obtenir dans `transport-refactored` une copie du code historique qui passe déjà tous les tests.

1. `git init`, puis un premier commit du projet tel quel.
2. Copier toutes les classes de `transport-legacy/src/main/java/fr/efrei/transport` dans
   `transport-refactored/src/main/java/fr/efrei/transfret`. Dans IntelliJ : sélectionner les classes, faire
   *F5 (Copy)* et choisir le package `fr.efrei.transfret` comme cible.
3. Remplacer le contenu de `NouveauSystemeAdapter` par celui de `LegacyAdapter`, en adaptant le package et
   le nom de la classe.
4. Retirer `@Disabled` de `NouveauSystemeContratTest`.

**Terminé quand :** `mvn install` affiche deux fois `Tests run: 68, Failures: 0`, une fois pour le legacy et
une fois pour votre module.

À partir d'ici, on ne touche plus qu'à `transport-refactored`.

## Étape 1 – Diagnostic (1 h)

Avant de modifier quoi que ce soit, on lit le code.

- Pour chaque classe : qu'est-ce qu'elle fait, de quoi dépend-elle ?
- Pour chaque principe SOLID, trouver au moins une violation.
- Lister **tous** les endroits à modifier pour :
  - ajouter un mode de transport `FLUVIAL` ;
  - ajouter un supplément « hors gabarit » (+10 % si un colis dépasse 2 m).

  Conserver ces listes : elles serviront à l'étape 8.

**Livrable :** un tableau *défaut → emplacement → principe violé*.

## Étape 2 – Nombres magiques et enums (1 h 30)

| Problème | Refactoring |
|---|---|
| Nombres magiques (`1.2`, `0.10`, `167`, `5000`, `70`, `1440`…) | *Replace Magic Literal* → constantes nommées, placées dans la classe qui les utilise |
| Mode, statut et type de client en `String`, type de colis en `int` | *Replace Type Code with Class* → enums `ModeTransport`, `StatutExpedition`, `TypeClient`, `TypeMarchandise` |
| `Colis.getLibelleType()` est un `switch` sur un entier | le libellé devient un attribut de l'enum |
| `Math.round(x * 100.0) / 100.0` répété | *Extract Function* (ou un objet valeur `Montant`, en bonus) |

**Terminé quand :** plus aucun `equals("ROUTE")`, `equals("PREMIUM")` ni `getType() == 3` dans le code.

## Étape 3 – Responsabilité unique, le S de SOLID (1 h 30)

| Problème | Piste |
|---|---|
| `Expedition` fabrique sa propre facture (`genererFacture()`) : une entité métier ne devrait pas s'occuper de la mise en forme | *Move Function* → `GenerateurFacture` |
| `ServiceExpedition` calcule aussi les délais | *Extract Class* → `EstimateurDelai` |
| `ServiceExpedition.creerExpedition` commence par 30 lignes de validation | *Extract Class* → `ValidateurExpedition` |
| `ServiceExpedition` envoie lui-même les emails et les SMS | *Extract Class* → `Notificateur` |
| `ServiceClient` valide et enregistre | à vous de juger : est-ce une ou deux responsabilités ? |

**Terminé quand :** chaque classe se décrit en une phrase sans « et ».

## Étape 4 – Inversion des dépendances, le D de SOLID (1 h)

**Problèmes :** `Database.getInstance()` (singleton), `EmailService.envoyer()` (méthode statique),
`new SmsService()`, `new CalculateurPrix()` et `new AffectationVehicule()` écrits en dur dans
`ServiceExpedition`, et la `Flotte` statique. Regardez `LegacyAdapter` : il doit vider des champs statiques
pour pouvoir tester.

1. Créer des interfaces côté métier : `ExpeditionRepository`, `ClientRepository`, `CanalNotification`…
2. Écrire les implémentations : en mémoire pour les repositories, sur la console pour l'email et le SMS.
3. Passer les dépendances par le constructeur (injection de dépendances).
4. Un seul endroit construit tout le graphe d'objets : `Main`, et l'adaptateur pour les tests.
   `reinitialiser()` revient alors à « construire un nouveau graphe ».

**Terminé quand :** plus aucun `static` modifiable, plus aucun singleton, plus aucun `new` d'un service à
l'intérieur d'un autre service.

---

# Séance 2

## Étape 5 – Strategy : le tarif et la remise, le O de SOLID (2 h 30)

**Problème :** `CalculateurPrix` enchaîne des `if/else` sur le mode, puis sur le type de client. Pour ajouter
un mode, il faut modifier cette classe, et de même pour un type de client : elle n'est pas **fermée à la
modification**.

**Le patron Strategy :** on isole chaque variante d'un algorithme dans sa propre classe, derrière une
interface commune. Le code appelant ne connaît que l'interface.

1. **Tarif de base par mode.** Une interface (par exemple `TarifTransport`) et une implémentation par mode :
   route, rail, mer, air. Le minimum de facturation et le poids volumétrique restent dans la stratégie
   concernée.
2. **Remise par type de client.** Une interface (par exemple `PolitiqueRemise`) et une implémentation par
   type de client.
3. Qui choisit la stratégie ? L'enum peut la porter (`ModeTransport.ROUTE` connaît son tarif), ou une
   `Map<ModeTransport, TarifTransport>` peut être injectée. Discutez des deux options.
4. Bonus : le délai par mode suit exactement le même schéma.

**Terminé quand :** `CalculateurPrix` ne contient plus aucun `if` sur un mode ou un type de client, et chaque
stratégie a son test unitaire.

## Étape 6 – Decorator : les suppléments (2 h)

**Problème :** les suppléments (fragile, dangereux, réfrigéré), l'urgence et l'assurance s'appliquent l'un
après l'autre. Chacun est une ligne `if` de plus dans `CalculateurPrix`, et en ajouter un oblige à modifier la
classe.

**Le patron Decorator :** un objet en « enveloppe » un autre qui a la même interface, et ajoute son propre
comportement avant ou après l'avoir appelé. On empile les enveloppes comme des poupées russes :

```
Assurance( Remise( Urgence( Dangereux( Fragile( TarifRoute ) ) ) ) )
```

1. Une interface commune au tarif de base et aux suppléments, par exemple `Prix calculer(Expedition)`.
2. Un décorateur abstrait qui contient le composant enveloppé.
3. Un décorateur par supplément : `SupplementFragile`, `SupplementDangereux`, `SupplementRefrigere`,
   `SupplementUrgence`, `Assurance`. Et la remise ? Elle peut devenir un décorateur qui délègue à la
   stratégie de l'étape 5.
4. Une classe qui **assemble** la chaîne selon l'expédition (quels suppléments, dans quel ordre).
5. **Attention à l'ordre :** la remise entreprise dépend du prix *après* suppléments et urgence, et
   l'assurance s'ajoute *après* la remise. Les tests vous le rappelleront.

**Terminé quand :** ajouter un supplément = créer un décorateur + l'ajouter à l'assemblage, sans toucher
aux autres décorateurs.

## Étape 7 – Véhicules : le L et le I de SOLID (1 h 30)

**Problèmes :**
- `IVehicule` impose `rouler`, `naviguer`, `voler` et `activerRefrigeration` à tous les véhicules. Résultat :
  des `UnsupportedOperationException` partout (violation de la **ségrégation des interfaces**).
- `CamionFrigorifique.charger()` refuse les colis non réfrigérés : on ne peut pas l'utiliser partout où un
  `Camion` est attendu (violation de **Liskov**). D'où le `continue` « sinon ça plante » dans
  `AffectationVehicule`.
- `Train.faireLePlein()` lève une exception, cachée par un `catch` vide.
- `AffectationVehicule` est une cascade d'`instanceof`.

**Pistes :**
1. Découper `IVehicule` en interfaces plus petites (*Segregate Interface*).
2. *Replace Conditional with Polymorphism* : chaque véhicule répond lui-même à « puis-je prendre en charge
   ces colis, pour ce mode ? ». `AffectationVehicule` ne fait plus d'`instanceof`.
3. `demarrerExpedition` appelle `rouler`, `naviguer` ou `voler` selon le mode : une seule méthode
   `partir(distance)` suffit, et chaque véhicule l'implémente à sa façon.

**Terminé quand :** plus aucun `instanceof` ni `UnsupportedOperationException`.

## Étape 8 – Preuve par l'extension (1 h)

Implémenter dans votre code :

- **Mode `FLUVIAL`** : 0,40 €/km + 0,02 €/kg, minimum 300 €, délai = distance / 360 arrondi au supérieur
  + 1 jour, réfrigéré interdit, pas d'urgence, péniche `PEN-0001` de 2 500 t ;
- **Supplément « hors gabarit »** : ×1,10 si un colis dépasse 200 cm sur une dimension.

Ajouter un trajet fluvial à la table des distances (par exemple Paris → Le Havre, 365 km).

**Questions :**
- Combien de fichiers **existants** avez-vous modifiés ? Comparez avec les listes de l'étape 1.
- Les 68 tests passent-ils toujours ?
- Écrivez les tests unitaires des nouvelles classes.

---

## Grille d'auto-évaluation

| Critère | ✔ |
|---|---|
| 68 tests de contrat verts | |
| Aucun nombre magique | |
| Aucun `if` / `switch` sur un mode, un type de client ou un type de marchandise | |
| Tarifs et remises en Strategy, suppléments en Decorator, chacun avec son test unitaire | |
| Aucun `instanceof`, aucune `UnsupportedOperationException` | |
| Aucun `static` modifiable, aucun singleton | |
| L'adaptateur ne contient que de la traduction | |
| Ajouter `FLUVIAL` et « hors gabarit » n'a demandé que de nouvelles classes et l'assemblage | |
