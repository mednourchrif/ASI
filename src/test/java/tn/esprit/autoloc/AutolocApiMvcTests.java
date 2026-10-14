package tn.esprit.autoloc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        mockMvc.perform(post("/api/agences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nom").value("Agence Tunis Centre"));

        mockMvc.perform(get("/api/agences"))
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

        String res = mockMvc.perform(post("/api/agences")
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

        mockMvc.perform(post("/api/vehicules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(vehiculePayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.immatriculation").value("123-TN-456"));

        mockMvc.perform(get("/api/vehicules"))
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

        mockMvc.perform(post("/api/maintenances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(maintenancePayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.cout").value(350.0));

        mockMvc.perform(get("/api/maintenances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}

