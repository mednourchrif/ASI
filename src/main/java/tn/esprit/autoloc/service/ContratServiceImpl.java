package tn.esprit.autoloc.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IContratRepository;

@Service
@RequiredArgsConstructor
public class ContratServiceImpl implements IContratService {

    private final IContratRepository contratRepository;

    @Override
    public Contrat create(Contrat contrat) {
        if (contrat.getId() != null) {
            throw new IllegalArgumentException("Un nouveau contrat ne doit pas avoir d'identifiant");
        }
        validateMontant(contrat.getMontantTotal());
        return contratRepository.save(contrat);
    }

    @Override
    public Contrat findById(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
    }

    @Override
    public List<Contrat> findAll() {
        return contratRepository.findAll();
    }

    @Override
    public Contrat update(Long id, Contrat contrat) {
        Contrat existant = findById(id);
        validateMontant(contrat.getMontantTotal());
        existant.setDateDebut(contrat.getDateDebut());
        existant.setDateFin(contrat.getDateFin());
        existant.setMontantTotal(contrat.getMontantTotal());
        existant.setStatut(contrat.getStatut());
        existant.setReservation(contrat.getReservation());
        return contratRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!contratRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contrat", id);
        }
        contratRepository.deleteById(id);
    }

    private void validateMontant(Double montantTotal) {
        if (montantTotal != null && montantTotal < 0) {
            throw new IllegalArgumentException("Le montant total du contrat ne doit pas etre negatif");
        }
    }
}
