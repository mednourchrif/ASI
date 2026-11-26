package tn.esprit.autoloc.service;

import java.util.List;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.VehiculeRequestDTO;
import tn.esprit.autoloc.web.dto.VehiculeResponseDTO;
import tn.esprit.autoloc.web.dto.VehiculeResumeDTO;

@Component
public class VehiculeMapper {

    public VehiculeResponseDTO toDto(Vehicule v) {
        String agenceNom = v.getAgence() != null ? v.getAgence().getNom() : null;
        List<String> libelles = (v.getEquipements() == null) ? List.of()
                : v.getEquipements().stream().map(Equipement::getLibelle).toList();
        return new VehiculeResponseDTO(
                v.getId(),
                v.getImmatriculation(),
                v.getMarque(),
                v.getModele(),
                v.getCategorie(),
                v.getStatut(),
                v.getTarifJournalier(),
                agenceNom,
                libelles
        );
    }

    public VehiculeResumeDTO toResumeDto(Vehicule v) {
        return new VehiculeResumeDTO(v.getId(), v.getImmatriculation(), v.getMarque(), v.getModele());
    }

    public Vehicule toEntity(VehiculeRequestDTO dto) {
        Vehicule v = new Vehicule();
        v.setImmatriculation(dto.immatriculation());
        v.setMarque(dto.marque());
        v.setModele(dto.modele());
        v.setCategorie(dto.categorie());
        v.setTarifJournalier(dto.tarifJournalier());
        v.setStatut(StatutVehicule.DISPONIBLE);
        return v;
    }
}
