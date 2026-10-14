package tn.esprit.autoloc.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IVehiculeRepository;

@Service
@RequiredArgsConstructor
public class VehiculeServiceImpl implements IVehiculeService {

    private final IVehiculeRepository vehiculeRepository;

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

    private void validateVehicule(Vehicule vehicule) {
        if (vehicule.getKilometrage() != null && vehicule.getKilometrage() < 0) {
            throw new IllegalArgumentException("Le kilometrage du vehicule ne doit pas etre negatif");
        }
        if (vehicule.getTarifJournalier() != null && vehicule.getTarifJournalier() < 0) {
            throw new IllegalArgumentException("Le tarif journalier du vehicule ne doit pas etre negatif");
        }
    }
}
