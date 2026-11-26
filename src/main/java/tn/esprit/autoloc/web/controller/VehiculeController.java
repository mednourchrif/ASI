package tn.esprit.autoloc.web.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.service.IVehiculeService;
import tn.esprit.autoloc.web.dto.NbVehiculesAgenceDTO;
import tn.esprit.autoloc.web.dto.VehiculeRequestDTO;
import tn.esprit.autoloc.web.dto.VehiculeResponseDTO;
import tn.esprit.autoloc.web.dto.VehiculeResumeDTO;

@RestController
@RequestMapping("/vehicules")
@RequiredArgsConstructor
@Tag(name = "Véhicules", description = "Gestion des véhicules — CRUD, recherches (K1-K10) et jointures (J1-J8)")
public class VehiculeController {

    private final IVehiculeService vehiculeService;

    // ── CRUD ─────────────────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Lister les véhicules (résumé)")
    @ApiResponse(responseCode = "200", description = "Liste résumée des véhicules")
    public List<VehiculeResumeDTO> findAll() {
        return vehiculeService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un véhicule (détail complet)")
    @ApiResponse(responseCode = "200", description = "Véhicule trouvé")
    @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    public VehiculeResponseDTO findById(@PathVariable Long id) {
        return vehiculeService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Créer un véhicule (DTO validé, 201 Created)")
    @ApiResponse(responseCode = "201", description = "Véhicule créé")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Agence introuvable")
    public ResponseEntity<VehiculeResponseDTO> create(@Valid @RequestBody VehiculeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculeService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un véhicule (entité brute)")
    public Vehicule update(@PathVariable Long id, @RequestBody Vehicule vehicule) {
        return vehiculeService.update(id, vehicule);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un véhicule")
    public void deleteById(@PathVariable Long id) {
        vehiculeService.deleteById(id);
    }

    // ── Affectation ───────────────────────────────────────────────────────────

    @PutMapping("/{idVehicule}/agence/{idAgence}")
    @Operation(summary = "Affecter une agence à un véhicule")
    public Vehicule assignAgence(@PathVariable Long idVehicule, @PathVariable Long idAgence) {
        return vehiculeService.assignAgence(idVehicule, idAgence);
    }

    @PutMapping("/{idVehicule}/equipements/{idEquipment}")
    @Operation(summary = "Affecter un équipement à un véhicule")
    public Vehicule assignEquipment(@PathVariable Long idVehicule, @PathVariable Long idEquipment) {
        return vehiculeService.assignEquipment(idVehicule, idEquipment);
    }

    @DeleteMapping("/{idVehicule}/equipements/{idEquipment}")
    @Operation(summary = "Désaffecter un équipement d'un véhicule")
    public Vehicule unassignEquipment(@PathVariable Long idVehicule, @PathVariable Long idEquipment) {
        return vehiculeService.unassignEquipment(idVehicule, idEquipment);
    }

    @PostMapping("/agence/{idAgence}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Ajouter un véhicule (entité brute) et l'affecter à une agence")
    public Vehicule addAndAssignAgence(@RequestBody Vehicule vehicule, @PathVariable Long idAgence) {
        return vehiculeService.addAndAssignAgence(vehicule, idAgence);
    }

    // ── K1 : par marque ───────────────────────────────────────────────────────
    @GetMapping("/recherche/marque/{marque}")
    @Operation(summary = "K1 — Véhicules par marque")
    public List<VehiculeResumeDTO> findByMarque(@PathVariable String marque) {
        return vehiculeService.findByMarque(marque);
    }

    // ── K2 : par catégorie + statut ───────────────────────────────────────────
    @GetMapping("/recherche/categorie/{categorie}/statut/{statut}")
    @Operation(summary = "K2 — Véhicules par catégorie et statut")
    public List<VehiculeResumeDTO> findByCategorieAndStatut(
            @PathVariable CategorieVehicule categorie,
            @PathVariable StatutVehicule statut) {
        return vehiculeService.findByCategorieAndStatut(categorie, statut);
    }

    // ── K3 : plage de tarif ───────────────────────────────────────────────────
    @GetMapping("/recherche/tarif/{min}/{max}")
    @Operation(summary = "K3 — Véhicules dont le tarif est dans [min, max], triés ASC")
    public List<VehiculeResumeDTO> findByTarif(@PathVariable BigDecimal min, @PathVariable BigDecimal max) {
        return vehiculeService.findByTarifJournalierBetween(min, max);
    }

    // ── K4 : modèle (mot-clé, insensible à la casse) ─────────────────────────
    @GetMapping("/recherche/modele/{motCle}")
    @Operation(summary = "K4 — Véhicules dont le modèle contient le mot-clé")
    public List<VehiculeResumeDTO> findByModele(@PathVariable String motCle) {
        return vehiculeService.findByModeleContaining(motCle);
    }

    // ── K5 : plusieurs catégories ─────────────────────────────────────────────
    @GetMapping("/recherche/categories")
    @Operation(summary = "K5 — Véhicules dont la catégorie est dans la liste fournie (?categories=SUV&categories=BERLINE)")
    public List<VehiculeResumeDTO> findByCategorieIn(@RequestParam List<CategorieVehicule> categories) {
        return vehiculeService.findByCategorieIn(categories);
    }

    // ── K6 : existence par immatriculation ────────────────────────────────────
    @GetMapping("/recherche/existe/{immatriculation}")
    @Operation(summary = "K6 — Vérifier si une immatriculation est déjà utilisée")
    public boolean existsByImmatriculation(@PathVariable String immatriculation) {
        return vehiculeService.existsByImmatriculation(immatriculation);
    }

    // ── K7 : comptage par statut ──────────────────────────────────────────────
    @GetMapping("/recherche/count/statut/{statut}")
    @Operation(summary = "K7 — Nombre de véhicules ayant un statut donné")
    public long countByStatut(@PathVariable StatutVehicule statut) {
        return vehiculeService.countByStatut(statut);
    }

    // ── K8 : top 3 moins chers ────────────────────────────────────────────────
    @GetMapping("/recherche/top3-moins-chers")
    @Operation(summary = "K8 — Les 3 véhicules les moins chers")
    public List<VehiculeResumeDTO> findTop3() {
        return vehiculeService.findTop3ByOrderByTarifJournalierAsc();
    }

    // ── K9 : par agence ───────────────────────────────────────────────────────
    @GetMapping("/recherche/agence/{agenceId}")
    @Operation(summary = "K9 — Véhicules d'une agence")
    public List<VehiculeResumeDTO> findByAgence(@PathVariable Long agenceId) {
        return vehiculeService.findByAgenceId(agenceId);
    }

    // ── K10 : sans agence ─────────────────────────────────────────────────────
    @GetMapping("/recherche/sans-agence")
    @Operation(summary = "K10 — Véhicules sans agence affectée")
    public List<VehiculeResumeDTO> findSansAgence() {
        return vehiculeService.findByAgenceIsNull();
    }

    // ── J1 : par ville d'agence ───────────────────────────────────────────────
    @GetMapping("/ville/{ville}")
    @Operation(summary = "J1 — Véhicules d'une ville d'agence")
    public List<VehiculeResumeDTO> findByVille(@PathVariable String ville) {
        return vehiculeService.findByVilleAgence(ville);
    }

    // ── J2 : par équipement ───────────────────────────────────────────────────
    @GetMapping("/equipement/{libelle}")
    @Operation(summary = "J2 — Véhicules possédant un équipement donné")
    public List<VehiculeResumeDTO> findByEquipement(@PathVariable String libelle) {
        return vehiculeService.findByEquipement(libelle);
    }

    // ── J3 : au moins N équipements ───────────────────────────────────────────
    @GetMapping("/min-equipements/{n}")
    @Operation(summary = "J3 — Véhicules avec au moins N équipements")
    public List<VehiculeResumeDTO> findAvecAuMoinsN(@PathVariable long n) {
        return vehiculeService.findAvecAuMoinsNEquipements(n);
    }

    // ── J4 : réservés par un client ───────────────────────────────────────────
    @GetMapping("/client/{clientId}/reserves")
    @Operation(summary = "J4 — Véhicules réservés par un client")
    public List<VehiculeResumeDTO> findReservesParClient(@PathVariable Long clientId) {
        return vehiculeService.findReservesParClient(clientId);
    }

    // ── J5 : jamais réservés ──────────────────────────────────────────────────
    @GetMapping("/jamais-reserves")
    @Operation(summary = "J5 — Véhicules jamais réservés")
    public List<VehiculeResumeDTO> findJamaisReserves() {
        return vehiculeService.findJamaisReserves();
    }

    // ── J6 : en maintenance à une date ────────────────────────────────────────
    @GetMapping("/maintenance/{date}")
    @Operation(summary = "J6 — Véhicules en maintenance à une date donnée (format YYYY-MM-DD)")
    public List<VehiculeResumeDTO> findEnMaintenance(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return vehiculeService.findEnMaintenanceLe(date);
    }

    // ── J8 : comptage par agence ──────────────────────────────────────────────
    @GetMapping("/stats/par-agence")
    @Operation(summary = "J8 — Nombre de véhicules par agence")
    public List<NbVehiculesAgenceDTO> compterParAgence() {
        return vehiculeService.compterParAgence();
    }
}
