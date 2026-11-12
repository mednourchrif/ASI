package tn.esprit.autoloc.service;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

@Service
@RequiredArgsConstructor
public class VehiculeServiceImpl implements IVehiculeService {

    private final IVehiculeRepository vehiculeRepository;
    private final IAgenceRepository agenceRepository;
    private final IEquipementRepository equipementRepository;

    @Override
    public Vehicule create(Vehicule vehicule) {
        if (vehicule.getId() != null) {
            throw new IllegalArgumentException("Un nouveau vehicule ne doit pas avoir d'identifiant");
        }
        validateVehicule(vehicule);
        return vehiculeRepository.save(vehicule);
    }

    @Override
    public Vehicule findById(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
    }

    @Override
    public List<Vehicule> findAll() {
        return vehiculeRepository.findAll();
    }

    @Override
    public Vehicule update(Long id, Vehicule vehicule) {
        Vehicule existant = findById(id);
        validateVehicule(vehicule);
        existant.setImmatriculation(vehicule.getImmatriculation());
        existant.setMarque(vehicule.getMarque());
        existant.setModele(vehicule.getModele());
        existant.setKilometrage(vehicule.getKilometrage());
        existant.setTarifJournalier(vehicule.getTarifJournalier());
        existant.setDisponible(vehicule.getDisponible());
        existant.setAgence(vehicule.getAgence());
        return vehiculeRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!vehiculeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicule", id);
        }
        vehiculeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Vehicule assignAgence(Long idVehicule, Long idAgence) {
        Vehicule vehicule = findById(idVehicule);
        Agence agence = agenceRepository.findById(idAgence)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", idAgence));
        vehicule.setAgence(agence);
        return vehiculeRepository.save(vehicule);
    }

    @Override
    @Transactional
    public Vehicule assignEquipment(Long idVehicule, Long idEquipment) {
        Vehicule vehicule = findById(idVehicule);
        Equipement equipement = equipementRepository.findById(idEquipment)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", idEquipment));
        if (vehicule.getEquipements() == null) {
            vehicule.setEquipements(new ArrayList<>());
        }
        if (!vehicule.getEquipements().contains(equipement)) {
            vehicule.getEquipements().add(equipement);
        }
        return vehiculeRepository.save(vehicule);
    }

    @Override
    @Transactional
    public Vehicule unassignEquipment(Long idVehicule, Long idEquipment) {
        Vehicule vehicule = findById(idVehicule);
        Equipement equipement = equipementRepository.findById(idEquipment)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", idEquipment));
        if (vehicule.getEquipements() != null) {
            vehicule.getEquipements().remove(equipement);
        }
        return vehiculeRepository.save(vehicule);
    }

    @Override
    @Transactional
    public Vehicule addAndAssignAgence(Vehicule vehicule, Long idAgence) {
        if (vehicule.getId() != null) {
            throw new IllegalArgumentException("Un nouveau vehicule ne doit pas avoir d'identifiant");
        }
        Agence agence = agenceRepository.findById(idAgence)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", idAgence));
        validateVehicule(vehicule);
        vehicule.setAgence(agence);

        if (vehicule.getEquipements() != null && !vehicule.getEquipements().isEmpty()) {
            List<Equipement> resolvedEquipements = new ArrayList<>();
            for (Equipement eq : vehicule.getEquipements()) {
                if (eq.getId() != null) {
                    Equipement existing = equipementRepository.findById(eq.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Equipement", eq.getId()));
                    resolvedEquipements.add(existing);
                } else {
                    resolvedEquipements.add(eq);
                }
            }
            vehicule.setEquipements(resolvedEquipements);
        }

        return vehiculeRepository.save(vehicule);
    }

    private void validateVehicule(Vehicule vehicule) {
        if (vehicule.getKilometrage() != null && vehicule.getKilometrage() < 0) {
            throw new IllegalArgumentException("Le kilometrage du vehicule ne doit pas etre negatif");
        }
        if (vehicule.getTarifJournalier() != null && vehicule.getTarifJournalier() < 0) {
            throw new IllegalArgumentException("Le tarif journalier du vehicule ne doit pas etre negatif");
        }
    }
}
