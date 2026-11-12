package tn.esprit.autoloc.web.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.service.IVehiculeService;

@RestController
@RequestMapping("/vehicules")
@RequiredArgsConstructor
@Tag(name = "Véhicules", description = "Gestion des véhicules")
public class VehiculeController {

    private final IVehiculeService vehiculeService;

    @GetMapping
    @Operation(summary = "Lister les véhicules")
    @ApiResponse(responseCode = "200", description = "Liste des véhicules")
    public List<Vehicule> findAll() {
        return vehiculeService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un véhicule")
    @ApiResponse(responseCode = "200", description = "Véhicule trouvé")
    @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    public Vehicule findById(@PathVariable Long id) {
        return vehiculeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un véhicule")
    @ApiResponse(responseCode = "201", description = "Véhicule créé")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public Vehicule create(@RequestBody Vehicule vehicule) {
        return vehiculeService.create(vehicule);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un véhicule")
    @ApiResponse(responseCode = "200", description = "Véhicule modifié")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    public Vehicule update(@PathVariable Long id, @RequestBody Vehicule vehicule) {
        return vehiculeService.update(id, vehicule);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un véhicule")
    @ApiResponse(responseCode = "204", description = "Véhicule supprimé")
    @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    public void deleteById(@PathVariable Long id) {
        vehiculeService.deleteById(id);
    }

    @PutMapping("/{idVehicule}/agence/{idAgence}")
    @Operation(summary = "Affecter une agence à un véhicule")
    @ApiResponse(responseCode = "200", description = "Agence affectée au véhicule")
    @ApiResponse(responseCode = "404", description = "Véhicule ou agence introuvable")
    public Vehicule assignAgence(@PathVariable Long idVehicule, @PathVariable Long idAgence) {
        return vehiculeService.assignAgence(idVehicule, idAgence);
    }

    @PutMapping("/{idVehicule}/equipements/{idEquipment}")
    @Operation(summary = "Affecter un équipement à un véhicule")
    @ApiResponse(responseCode = "200", description = "Équipement affecté au véhicule")
    @ApiResponse(responseCode = "404", description = "Véhicule ou équipement introuvable")
    public Vehicule assignEquipment(@PathVariable Long idVehicule, @PathVariable Long idEquipment) {
        return vehiculeService.assignEquipment(idVehicule, idEquipment);
    }

    @DeleteMapping("/{idVehicule}/equipements/{idEquipment}")
    @Operation(summary = "Désaffecter un équipement d'un véhicule")
    @ApiResponse(responseCode = "200", description = "Équipement désaffecté du véhicule")
    @ApiResponse(responseCode = "404", description = "Véhicule ou équipement introuvable")
    public Vehicule unassignEquipment(@PathVariable Long idVehicule, @PathVariable Long idEquipment) {
        return vehiculeService.unassignEquipment(idVehicule, idEquipment);
    }

    @PostMapping("/agence/{idAgence}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Ajouter un véhicule et lui affecter une agence")
    @ApiResponse(responseCode = "201", description = "Véhicule créé et agence affectée")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Agence introuvable")
    public Vehicule addAndAssignAgence(@RequestBody Vehicule vehicule, @PathVariable Long idAgence) {
        return vehiculeService.addAndAssignAgence(vehicule, idAgence);
    }
}
