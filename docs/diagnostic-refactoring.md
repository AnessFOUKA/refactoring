# Diagnostic du code historique

Ce diagnostic porte sur la copie de référence présente dans
`transport-refactored/src/main/java/fr/efrei/transfret`. Le module `transport-legacy`
sert de référence et reste inchangé.

## Défauts, emplacements et principes concernés

| Défaut observé | Emplacement(s) à examiner | Principe concerné |
|---|---|---|
| Le calcul du prix cumule l'agrégation des colis, les tarifs par mode, les suppléments, l'urgence, les remises, l'assurance et l'arrondi. | `CalculateurPrix` | **S** — plusieurs responsabilités ; **O** — chaque variante ajoutée impose de modifier la classe. |
| Les tarifs et remises sont sélectionnés par des conditions sur des chaînes. | `CalculateurPrix` | **O** — ajout d'un mode ou d'un type de client dans les branches existantes. |
| Validation, règles d'acceptation, affectation, calcul du prix et délai, transitions, libération du véhicule et notifications sont réunis. | `ServiceExpedition` | **S** — trop de raisons de modifier le service ; **O** — les règles métier sont codées dans des cascades de conditions. |
| Validation et persistance du client sont combinées, et le service choisit lui-même le canal de bienvenue. | `ServiceClient` | **S** — règles de validation, création, persistance et notification couplées ; **D** — dépendance directe aux services statiques. |
| L'entité produit et formate le texte de facture. | `Expedition.genererFacture()` | **S** — le modèle métier prend aussi en charge la présentation. |
| Les clients, marchandises, expéditions, modes et états sont représentés en partie par des chaînes ou des codes entiers, avec des setters ouverts. | `Client`, `Colis`, `Expedition`, puis les appels dans `CalculateurPrix`, `ServiceExpedition` et `AffectationVehicule` | **O** — les nouveaux codes touchent plusieurs conditions ; **L** — les invariants de ces objets ne sont pas protégés par leurs types. |
| L'affectation connaît les classes concrètes de la flotte, teste leur type et leur capacité particulière. | `AffectationVehicule`, `Flotte`, `Vehicule`, `Camion`, `CamionFrigorifique`, `Train`, `Bateau`, `Avion` | **O** — un nouveau véhicule ou mode nécessite de modifier le répartiteur ; **D** — le haut niveau dépend des classes concrètes. |
| Une interface unique impose toutes les opérations de déplacement et de réfrigération à chaque véhicule ; les véhicules lèvent des exceptions pour les opérations non applicables. | `IVehicule`, `Camion`, `Train`, `Bateau`, `Avion` | **I** — interface trop large ; **L** — une implémentation ne peut pas remplacer uniformément un véhicule si ses opérations échouent selon son type. |
| Le camion frigorifique hérite du camion mais refuse certains colis dans `charger`, alors qu'un camion ordinaire les accepte. | `CamionFrigorifique.charger()`, `AffectationVehicule` | **L** — le sous-type restreint le comportement attendu du type parent ; le répartiteur compense avec une condition spéciale. |
| Les services accèdent à un singleton, à des listes statiques, aux services de notification statiques, et construisent directement plusieurs collaborateurs. | `Database`, `Flotte`, `EmailService`, `SmsService`, `ServiceClient`, `ServiceExpedition` | **D** — les politiques métier dépendent d'implémentations concrètes et de l'état global ; **S** — responsabilités métier et infrastructure sont mêlées. |
| Des valeurs métier et détails de présentation sont dispersés : tarifs, seuils, délais, pourcentages, unités, codes et format de date. | `CalculateurPrix`, `ServiceExpedition`, `Expedition`, véhicules, `Utils`, `Colis` | Lisibilité et **S** — les règles n'ont pas de nom ni de point de modification unique. |
| Les journaux de notification et leur état global rendent l'isolation des scénarios dépendante d'un nettoyage manuel. | `EmailService.journal`, `SmsService.journal`, `Database`, `ServiceExpedition.compteur`, adaptateur de test | **D** — dépendance à l'état global ; testabilité et isolation compromises. |

### Responsabilités et dépendances par classe

| Classe | Responsabilité actuelle | Couplages principaux |
|---|---|---|
| `Main` | Démonstration et assemblage manuel du scénario. | Services métier, classes de modèle et flotte statique. |
| `ServiceClient` | Valider, créer et enregistrer un client, puis envoyer le courriel de bienvenue. | `Database`, `EmailService`, `Client`. |
| `ServiceExpedition` | Créer et valider une expédition, calculer prix et délai, gérer le cycle de vie, les véhicules et les notifications. | `Database`, `CalculateurPrix`, `AffectationVehicule`, `Utils`, `EmailService`, `SmsService`, modèle et flotte. |
| `CalculateurPrix` | Calculer le tarif complet et la remise à partir des données de colis et d'expédition. | `Colis` et codes chaîne de mode/client. |
| `AffectationVehicule` | Choisir le premier véhicule disponible compatible. | `Flotte` statique, types concrets, codes de marchandises et de mode. |
| `Flotte` | Créer et conserver les véhicules disponibles. | Toutes les classes concrètes de véhicule ; liste statique mutable. |
| `IVehicule` | Décrire stockage, chargement, déplacement, réfrigération et plein. | `Colis` ; force les capacités propres à chaque transport sur chaque véhicule. |
| `Vehicule` | Porter l'immatriculation, capacité, disponibilité, charge et chargement communs. | `IVehicule`, `Colis`. |
| `Camion` | Spécialiser le véhicule routier et son autorisation de matières dangereuses. | `Vehicule`, interface multi-opérations. |
| `CamionFrigorifique` | Spécialiser le camion et limiter le chargement aux marchandises réfrigérées. | `Camion`, code entier de marchandise. |
| `Train` | Implémenter le transport ferroviaire et ses opérations de véhicule. | `Vehicule`, interface multi-opérations. |
| `Bateau` | Implémenter la navigation et la disponibilité des conteneurs réfrigérés. | `Vehicule`, interface multi-opérations. |
| `Avion` | Implémenter le transport aérien et ses opérations de véhicule. | `Vehicule`, interface multi-opérations. |
| `Client` | Porter les données d'un client, son type et son nombre d'expéditions. | Type de client sous forme de chaîne. |
| `Colis` | Porter dimensions, poids, valeur et type de marchandise ; calculer le volume et son libellé. | Code entier de marchandise et ses libellés. |
| `Expedition` | Porter l'état et les données de l'expédition ; calculer des totaux et générer une facture. | `Client`, `Colis`, `Vehicule`, `Utils`, formatage et statuts chaîne. |
| `Database` | Persister en mémoire clients et expéditions et exposer leur état. | Modèles métier ; singleton et collections statiques. |
| `EmailService` | Simuler l'envoi et journaliser les courriels. | État statique et journal global. |
| `SmsService` | Simuler l'envoi et journaliser les SMS. | État statique et journal global. |
| `Utils` | Formater date/prix, valider sommairement un email, arrondir et fournir les distances. | Détails de présentation et table de trajets dans une classe utilitaire statique. |

## Emplacements à modifier pour l'extension `FLUVIAL`

Le périmètre de l'atelier demande de laisser `transport-contrat` et
`transport-legacy` inchangés. Le mode doit donc être ajouté et vérifié dans le
nouveau module avec des tests propres à celui-ci ; le contrat commun actuel ne
possède pas de valeur `FLUVIAL`.

| Emplacement existant | Adaptation attendue |
|---|---|
| `CalculateurPrix` | Ajouter la base à 0,40 €/km et 0,02 €/kg, avec un minimum de 300 €, ou fournir la nouvelle stratégie tarifaire lorsque l'étape Strategy sera réalisée. |
| `ServiceExpedition` | Autoriser le mode selon ses règles, calculer le délai `ceil(distance / 360) + 1`, et refuser l'urgence et les marchandises réfrigérées. À l'étape Strategy, placer ces règles dans des politiques dédiées. |
| `Utils.getDistance` | Ajouter le trajet Paris–Le Havre de 365 km sans modifier les trajets existants. |
| `AffectationVehicule` | Reconnaître la capacité fluviale et affecter un véhicule fluvial ; à terme, remplacer le branchement sur types concrets par une capacité polymorphe. |
| `Flotte` | Enregistrer la péniche `PEN-0001` d'une capacité de 2 500 tonnes (soit 2 500 000 kg si la capacité reste exprimée en kg). |
| Véhicules (`IVehicule`, `Vehicule` et nouvelles classes de navigation fluviale) | Représenter la péniche et son comportement de déplacement sans lui imposer des opérations incompatibles. Réévaluer le choix d'hériter de `Bateau` au regard de la substituabilité. |
| `Expedition` et génération de facture | Vérifier que le mode est conservé et présenté correctement ; après l'extraction SRP, traiter la facture dans son générateur. |
| Tests de `transport-refactored` | Couvrir le tarif/minimum, le délai, les refus (urgence et réfrigéré), l'affectation et le trajet dans les deux sens. Ne pas modifier le contrat commun. |
| `Main` (facultatif) | Ajouter un scénario fluvial de démonstration si la démonstration doit exposer le nouveau mode. |

L'adaptateur `NouveauSystemeAdapter` ne peut pas traduire `FLUVIAL` à partir
des DTO actuels : les types `ModeTransport` du module `transport-contrat` ne le
définissent pas. Ne pas contourner cette limite par une modification du contrat
si la règle de l'atelier de préserver ce module s'applique.

## Emplacements à modifier pour le supplément « hors gabarit »

| Emplacement existant | Adaptation attendue |
|---|---|
| `Colis` | Les trois dimensions sont déjà disponibles. Ajouter si utile un prédicat calculant si une dimension dépasse 200 cm, ou laisser cette règle au composant tarifaire. |
| `CalculateurPrix` | Détecter au moins un colis avec longueur, largeur ou hauteur strictement supérieure à 200 cm et appliquer ×1,10 une seule fois sur le prix au point défini par l'ordre métier. |
| Assemblage des décorateurs (étape Decorator) | Ajouter le décorateur hors gabarit à la composition sans modifier les autres suppléments. |
| Tests de `transport-refactored` | Couvrir une dimension égale à 200 cm (pas de supplément), supérieure à 200 cm (supplément appliqué), plusieurs colis hors gabarit (application unique) et l'interaction avec assurance, remise et autres suppléments. |

## Mesure pour l'étape 8

Les principales classes existantes susceptibles d'être modifiées lors de
l'extension sont `CalculateurPrix`, `ServiceExpedition`, `Utils`,
`AffectationVehicule`, `Flotte`, `IVehicule`, `Vehicule` et `Expedition`.
L'étape 8 devrait réduire ces modifications par rapport au code initial en
ajoutant des stratégies, décorateurs, politiques de délai et capacités de
véhicule plutôt qu'en augmentant les cascades conditionnelles. Les tests et les
nouvelles classes ne sont pas comptés dans cette liste de classes existantes.
