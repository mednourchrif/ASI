package tn.esprit.autoloc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AutolocApiMvcTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAgenceLifecycle() throws Exception {
        String payload = """
            {
                "nom": "Agence Tunis Centre",
                "adresse": "Avenue Habib Bourguiba",
                "ville": "Tunis",
                "telephone": "71123456"
            }
            """;

        mockMvc.perform(post("/agences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nom").value("Agence Tunis Centre"));

        mockMvc.perform(get("/agences"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testVehiculeLifecycle() throws Exception {
        String agencePayload = """
            {
                "nom": "Agence Sousse",
                "adresse": "Route touristique",
                "ville": "Sousse",
                "telephone": "73123456"
            }
            """;

        String res = mockMvc.perform(post("/agences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(agencePayload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String vehiculePayload = """
            {
                "immatriculation": "123-TN-456",
                "marque": "Renault",
                "modele": "Clio",
                "kilometrage": 50000,
                "tarifJournalier": 75.0,
                "disponible": true,
                "agence": { "id": 1 }
            }
            """;

        mockMvc.perform(post("/vehicules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(vehiculePayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.immatriculation").value("123-TN-456"));

        mockMvc.perform(get("/vehicules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testMaintenanceLifecycle() throws Exception {
        String maintenancePayload = """
            {
                "dateDebut": "2026-10-01",
                "dateFin": "2026-10-05",
                "cout": 350.0,
                "description": "Vidange et changement filtres"
            }
            """;

        mockMvc.perform(post("/maintenances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(maintenancePayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.cout").value(350.0));

        mockMvc.perform(get("/maintenances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testAssignmentEndpoints() throws Exception {
        String agencePayload = """
            {
                "nom": "Agence Assignment Test",
                "adresse": "Centre Ville",
                "ville": "Tunis",
                "telephone": "71000111"
            }
            """;

        mockMvc.perform(post("/agences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(agencePayload))
                .andExpect(status().isCreated());

        String vehiculePayload = """
            {
                "immatriculation": "999-TN-888",
                "marque": "Peugeot",
                "modele": "208",
                "kilometrage": 10000,
                "tarifJournalier": 80.0,
                "disponible": true,
                "equipements": [
                    {
                        "libelle": "Siege Bebe",
                        "quantite": 2,
                        "description": "Siege auto groupe 1"
                    }
                ]
            }
            """;

        // addAndAssignAgence (cascading equipments)
        mockMvc.perform(post("/vehicules/agence/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(vehiculePayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.agence.id").value(1))
                .andExpect(jsonPath("$.equipements[0].id").isNotEmpty())
                .andExpect(jsonPath("$.equipements[0].libelle").value("Siege Bebe"));

        // create equipment
        String equipementPayload = """
            {
                "libelle": "GPS Satellite",
                "quantite": 5,
                "description": "Navigation GPS"
            }
            """;

        mockMvc.perform(post("/equipements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(equipementPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty());

        // assignEquipment
        mockMvc.perform(put("/vehicules/1/equipements/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.equipements").isArray())
                .andExpect(jsonPath("$.equipements[0].id").value(1));

        // unassignEquipment
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/vehicules/1/equipements/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.equipements").isEmpty());

        // assignAgence
        mockMvc.perform(put("/vehicules/1/agence/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agence.id").value(1));
    }

    @Test
    void testContratAddWithPayments() throws Exception {
        String contratPayload = """
            {
                "dateDebut": "2026-11-01",
                "dateFin": "2026-11-10",
                "montantTotal": 750.0,
                "statut": "EN_COURS",
                "paiements": [
                    {
                        "montant": 250.0,
                        "datePaiement": "2026-11-01",
                        "modePaiement": "CARTE_BANCAIRE"
                    },
                    {
                        "montant": 500.0,
                        "datePaiement": "2026-11-05",
                        "modePaiement": "VIREMENT"
                    }
                ]
            }
            """;

        mockMvc.perform(post("/contrats/with-payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(contratPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.montantTotal").value(750.0))
                .andExpect(jsonPath("$.paiements").isArray())
                .andExpect(jsonPath("$.paiements.length()").value(2))
                .andExpect(jsonPath("$.paiements[0].id").isNotEmpty())
                .andExpect(jsonPath("$.paiements[0].montant").value(250.0));
    }

    @Test
    void testSwaggerEndpoints() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}

