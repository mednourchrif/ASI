package tn.esprit.autoloc.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IEquipementRepository;

@Service
@RequiredArgsConstructor
public class EquipementServiceImpl implements IEquipementService {

    private final IEquipementRepository equipementRepository;

    @Override
    public Equipement create(Equipement equipement) {
        if (equipement.getId() != null) {
            throw new IllegalArgumentException("Un nouvel equipement ne doit pas avoir d'identifiant");
        }
        validateEquipement(equipement);
        return equipementRepository.save(equipement);
    }

    @Override
    public Equipement findById(Long id) {
        return equipementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", id));
    }

    @Override
    public List<Equipement> findAll() {
        return equipementRepository.findAll();
    }

    @Override
    public Equipement update(Long id, Equipement equipement) {
        Equipement existant = findById(id);
        validateEquipement(equipement);
        existant.setLibelle(equipement.getLibelle());
        existant.setQuantite(equipement.getQuantite());
        existant.setDescription(equipement.getDescription());
        return equipementRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!equipementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Equipement", id);
        }
        equipementRepository.deleteById(id);
    }

    private void validateEquipement(Equipement equipement) {
        if (equipement.getLibelle() == null || equipement.getLibelle().isBlank()) {
            throw new IllegalArgumentException("Le libelle de l'equipement est obligatoire");
        }
        if (equipement.getQuantite() != null && equipement.getQuantite() < 0) {
            throw new IllegalArgumentException("La quantite de l'equipement ne doit pas etre negative");
        }
    }
}
