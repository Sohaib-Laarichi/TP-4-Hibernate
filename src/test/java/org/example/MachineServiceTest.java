package org.example;

import org.example.entities.Machine;
import org.example.entities.Salle;
import org.example.service.MachineService;
import org.example.service.SalleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MachineService Tests")
public class MachineServiceTest {
    private MachineService machineService;
    private SalleService salleService;
    private Machine machine;
    private Salle salle;

    @BeforeEach
    void setUp() {
        machineService = new MachineService();
        salleService = new SalleService();
        salle = new Salle("TestSalle-" + System.nanoTime(), "Test");
        salleService.create(salle);
        machine = new Machine("TestMachine-" + System.nanoTime(), "Test", 1000, new Date(), salle);
    }

    @AfterEach
    void tearDown() {
        if (machine != null && machine.getId() != 0) {
            Machine m = machineService.findById(machine.getId());
            if (m != null) {
                machineService.delete(m);
            }
        }
        if (salle != null && salle.getId() != 0) {
            Salle s = salleService.findById(salle.getId());
            if (s != null) {
                salleService.delete(s);
            }
        }
        machineService = null;
        salleService = null;
        machine = null;
        salle = null;
    }

    @Test
    @DisplayName("Should create a Machine and assign an ID")
    void testCreate() {
        assertTrue(machineService.create(machine), "Create should return true on success");
        assertTrue(machine.getId() != 0, "ID should be assigned by Hibernate after creation");
        assertNotNull(machineService.findById(machine.getId()), "Machine should be findable by its new ID");
    }

    @Test
    @DisplayName("Should update an existing Machine")
    void testUpdate() {
        machineService.create(machine);
        machine.setRef("Updated Ref");
        assertTrue(machineService.update(machine), "Update should return true on success");
        Machine updatedMachine = machineService.findById(machine.getId());
        assertEquals("Updated Ref", updatedMachine.getRef(), "Machine's reference should be updated");
    }

    @Test
    @DisplayName("Should delete an existing Machine")
    void testDelete() {
        machineService.create(machine);
        assertTrue(machine.getId() != 0, "Machine must have an ID to be deleted");
        assertTrue(machineService.delete(machine), "Delete should return true on success");
        assertNull(machineService.findById(machine.getId()), "Machine should be null after deletion");
    }

    @Test
    @DisplayName("Should find a Machine by its ID")
    void testFindById() {
        machineService.create(machine);
        assertNotNull(machineService.findById(machine.getId()), "findById should retrieve the created Machine");
    }

    @Test
    @DisplayName("Should find all Machines and include the new one")
    void testFindAll() {
        machineService.create(machine);
        List<Machine> machines = machineService.findAll();
        assertNotNull(machines, "findAll should not return null");
        assertFalse(machines.isEmpty(), "findAll should not be empty after creation");
        assertTrue(machines.stream().anyMatch(m -> m.getId() == machine.getId()), "The created machine should be in the list of all machines");
    }
}
