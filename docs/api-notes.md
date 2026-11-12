# AutoLoc API - Atelier 5

## A. Endpoints

Le préfixe commun est défini une seule fois par `server.servlet.context-path=/api`.
Les contrôleurs restent donc mappés sur des routes sans `/api`.

| Contrôleur | Verbe et route | Service appelé | Succès | Erreurs |
|---|---|---|---|---|
| Agence | GET `/api/agences` | `IAgenceService.findAll()` | 200 | - |
| Agence | GET `/api/agences/{id}` | `IAgenceService.findById(id)` | 200 | 404 |
| Agence | POST `/api/agences` | `IAgenceService.create(agence)` | 201 | 400 |
| Agence | PUT `/api/agences/{id}` | `IAgenceService.update(id, agence)` | 200 | 400, 404 |
| Agence | DELETE `/api/agences/{id}` | `IAgenceService.deleteById(id)` | 204 | 404 |
| Client | GET `/api/clients` | `IClientService.findAll()` | 200 | - |
| Client | GET `/api/clients/{id}` | `IClientService.findById(id)` | 200 | 404 |
| Client | POST `/api/clients` | `IClientService.create(client)` | 201 | 400 |
| Client | PUT `/api/clients/{id}` | `IClientService.update(id, client)` | 200 | 400, 404 |
| Client | DELETE `/api/clients/{id}` | `IClientService.deleteById(id)` | 204 | 404 |
| Employe | GET `/api/employes` | `IEmployeService.findAll()` | 200 | - |
| Employe | GET `/api/employes/{id}` | `IEmployeService.findById(id)` | 200 | 404 |
| Employe | POST `/api/employes` | `IEmployeService.create(employe)` | 201 | 400 |
| Employe | PUT `/api/employes/{id}` | `IEmployeService.update(id, employe)` | 200 | 400, 404 |
| Employe | DELETE `/api/employes/{id}` | `IEmployeService.deleteById(id)` | 204 | 404 |
| Vehicule | GET `/api/vehicules` | `IVehiculeService.findAll()` | 200 | - |
| Vehicule | GET `/api/vehicules/{id}` | `IVehiculeService.findById(id)` | 200 | 404 |
| Vehicule | POST `/api/vehicules` | `IVehiculeService.create(vehicule)` | 201 | 400 |
| Vehicule | PUT `/api/vehicules/{id}` | `IVehiculeService.update(id, vehicule)` | 200 | 400, 404 |
| Vehicule | DELETE `/api/vehicules/{id}` | `IVehiculeService.deleteById(id)` | 204 | 404 |
| Equipement | GET `/api/equipements` | `IEquipementService.findAll()` | 200 | - |
| Equipement | GET `/api/equipements/{id}` | `IEquipementService.findById(id)` | 200 | 404 |
| Equipement | POST `/api/equipements` | `IEquipementService.create(equipement)` | 201 | 400 |
| Equipement | PUT `/api/equipements/{id}` | `IEquipementService.update(id, equipement)` | 200 | 400, 404 |
| Equipement | DELETE `/api/equipements/{id}` | `IEquipementService.deleteById(id)` | 204 | 404 |
| Reservation | GET `/api/reservations` | `IReservationService.findAll()` | 200 | - |
| Reservation | GET `/api/reservations/{id}` | `IReservationService.findById(id)` | 200 | 404 |
| Reservation | POST `/api/reservations` | `IReservationService.create(reservation)` | 201 | 400 |
| Reservation | PUT `/api/reservations/{id}` | `IReservationService.update(id, reservation)` | 200 | 400, 404 |
| Reservation | DELETE `/api/reservations/{id}` | `IReservationService.deleteById(id)` | 204 | 404 |
| Contrat | GET `/api/contrats` | `IContratService.findAll()` | 200 | - |
| Contrat | GET `/api/contrats/{id}` | `IContratService.findById(id)` | 200 | 404 |
| Contrat | POST `/api/contrats` | `IContratService.create(contrat)` | 201 | 400 |
| Contrat | PUT `/api/contrats/{id}` | `IContratService.update(id, contrat)` | 200 | 400, 404 |
| Contrat | DELETE `/api/contrats/{id}` | `IContratService.deleteById(id)` | 204 | 404 |
| Paiement | GET `/api/paiements` | `IPaiementService.findAll()` | 200 | - |
| Paiement | GET `/api/paiements/{id}` | `IPaiementService.findById(id)` | 200 | 404 |
| Maintenance | GET `/api/maintenances` | `IMaintenanceService.findAll()` | 200 | - |
| Maintenance | GET `/api/maintenances/{id}` | `IMaintenanceService.findById(id)` | 200 | 404 |
| Maintenance | POST `/api/maintenances` | `IMaintenanceService.create(maintenance)` | 201 | 400 |
| Maintenance | PUT `/api/maintenances/{id}` | `IMaintenanceService.update(id, maintenance)` | 200 | 400, 404 |
| Maintenance | DELETE `/api/maintenances/{id}` | `IMaintenanceService.deleteById(id)` | 204 | 404 |

## B. Annotations Jackson

`Contrat.paiements` et `Paiement.contrat` forment une association bidirectionnelle.
La paire retenue est :

- `@JsonManagedReference("contrat-paiements")` sur `Contrat.paiements` ;
- `@JsonBackReference("contrat-paiements")` sur `Paiement.contrat`.

Le parent est donc écrit dans le JSON et la référence inverse n'est pas réémise dans
chaque enfant. Cela évite la boucle infinie Jackson. Le service rattache chaque paiement
au contrat avant la sauvegarde afin que `cascade = CascadeType.ALL` puisse persister les
enfants avec une clé étrangère valide.

`@JsonIgnore` ignore complètement une propriété à la lecture et à l'écriture. Il est
utile pour une association inverse qui ne doit jamais apparaître dans l'API, mais il ne
convient pas au cas Contrat/Paiement où les paiements doivent être envoyés dans le POST
du contrat. `@JsonBackReference` conserve la propriété Java et la désérialisation du
parent, tout en supprimant seulement la référence inverse du JSON de sortie.

## C. Postman et Swagger

Postman sert à construire et rejouer des requêtes, à vérifier les codes HTTP avec des
scripts et à tester les cas nominaux et les erreurs. Exemple : POST `/api/contrats` doit
retourner 201, alors que GET `/api/contrats/9999` doit retourner 404.

Swagger UI, fourni par SpringDoc, sert à documenter le contrat de l'API et à essayer les
endpoints depuis le navigateur. Il est accessible à :

- `/api/swagger-ui.html`
- `/api/v3/api-docs`

Les deux outils sont complémentaires : Postman est adapté à la collection de tests et
Swagger est synchronisé automatiquement avec les contrôleurs et leurs annotations.

## D. Anomalies SonarQube et corrections

| Règle / anomalie | Problème | Correction |
|---|---|---|
| Injection par attribut | Une injection directe par champ rend les dépendances moins explicites et complique les tests. | Les contrôleurs utilisent un champ `final` injecté par constructeur via `@RequiredArgsConstructor`. |
| Accès direct au repository depuis le web | Le contrôleur deviendrait couplé à la persistance et violerait la séparation des couches. | Chaque contrôleur dépend uniquement de l'interface `I...Service`. |
| Boucle de sérialisation Jackson | Contrat et paiement se référencent mutuellement. | Paire `@JsonManagedReference` / `@JsonBackReference`. |
| Méthodes HTTP incohérentes | Un endpoint de suppression qui renvoie un corps ou un POST de paiement écrivable ne respecte pas le contrat. | DELETE renvoie 204 ; `PaiementController` reste en lecture seule et un POST renvoie 405. |

## E. Questions de compréhension

### 1. Quel composant reçoit toutes les requêtes ?

Le `DispatcherServlet` reçoit les requêtes HTTP. Il consulte les mappings Spring MVC
(`@RequestMapping`, `@GetMapping`, etc.) afin de trouver le contrôleur et la méthode qui
correspondent au verbe et à l'URL, puis il fait convertir le corps JSON par Jackson.

### 2. Pourquoi dépendre de `IContratService` ?

Le contrôleur dépend de l'abstraction métier, pas de l'implémentation ni de la base de
données. Cela respecte l'inversion de dépendance, garde la couche web testable et permet
de remplacer `ContratServiceImpl` sans modifier le contrôleur.

### 3. Pourquoi 201, 204, 404 et non 500 ?

201 signifie qu'une nouvelle ressource a été créée. 204 signifie que la suppression a
réussi sans contenu à retourner. Une ressource absente est un cas fonctionnel connu :
`ResourceNotFoundException` est convertie en 404 par `GlobalExceptionHandler`. Le 500 est
réservé aux erreurs inattendues du serveur.

### 4. Pourquoi Paiement est-il en lecture seule ?

Les paiements sont créés dans le JSON d'un contrat grâce à la cascade JPA. Il n'existe
pas de méthode `create` dans `IPaiementService`, donc aucun POST n'est déclaré. Un POST
`/api/paiements` reçoit 405 Method Not Allowed.

### 5. Quels risques présente l'exposition directe des entités JPA ?

Elle expose la structure interne de la base, crée un couplage entre API et persistance,
peut retourner des associations LAZY ou des champs non prévus et rend les changements de
modèle risqués. Les annotations Jackson évitent les boucles dans l'immédiat, mais les
DTO sont la solution propre prévue au chapitre suivant.
