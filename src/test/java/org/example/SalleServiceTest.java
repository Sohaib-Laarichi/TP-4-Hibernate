package org.example;

import org.example.entities.Salle;
import org.example.service.SalleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SalleServiceTest {
    private SalleService salleService;
    private Salle salle;

    @BeforeEach
    void setUp() {
        salleService = new SalleService();
        // Using a unique code for each test run to ensure isolation
        salle = new Salle("Test-" + System.nanoTime(), "Test");
    }

    @AfterEach
    void tearDown() {
        // Ensure salle has a persisted ID before trying to delete
        if (salle != null && salle.getId() != 0) {
            Salle found = salleService.findById(salle.getId());
            if (found != null) {
                salleService.delete(found);
            }
        }
        salleService = null;
        salle = null;
    }

    @Test
    @DisplayName("Should create a Salle and assign an ID")
    void testCreate() {
        assertTrue(salleService.create(salle), "Create should return true on success");
        assertTrue(salle.getId() != 0, "ID should be assigned by Hibernate after creation");
        assertNotNull(salleService.findById(salle.getId()), "Salle should be findable by its new ID");
    }

    @Test
    @DisplayName("Should update an existing Salle")
    void testUpdate() {
        salleService.create(salle);
        salle.setCode("Updated Code");
        assertTrue(salleService.update(salle), "Update should return true on success");
        Salle updatedSalle = salleService.findById(salle.getId());
        assertEquals("Updated Code", updatedSalle.getCode(), "Salle's code should be updated in the database");
    }

    @Test
    @DisplayName("Should delete an existing Salle")
    void testDelete() {
        salleService.create(salle);
        assertTrue(salle.getId() != 0, "Salle must have an ID before it can be deleted");
        assertTrue(salleService.delete(salle), "Delete should return true on success");
        assertNull(salleService.findById(salle.getId()), "Salle should be null after deletion");
    }

    @Test
    @DisplayName("Should find a Salle by its ID")
    void testFindById() {
        salleService.create(salle);
        assertNotNull(salleService.findById(salle.getId()), "findById should retrieve the created Salle");
    }

    @Test
    @DisplayName("Should find all Salles and include the new one")
    void testFindAll() {
        salleService.create(salle);
        List<Salle> salles = salleService.findAll();
        assertNotNull(salles, "findAll should not return null");
        assertFalse(salles.isEmpty(), "findAll result should not be empty after creation");
        // Verify that the list contains the salle we just created
        assertTrue(salles.stream().anyMatch(s -> s.getId() == salle.getId()), "The created salle should be in the list of all salles");
    }
}
