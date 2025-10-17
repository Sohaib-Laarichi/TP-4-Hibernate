package org.example;

import org.example.entities.Machine;
import org.example.entities.Salle;
import org.example.service.MachineService;
import org.example.service.SalleService;

import java.util.Date;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        SalleService ss = new SalleService();
        MachineService ms = new MachineService();

        // Création des salles
        Salle s1 = new Salle("SA01", "Informatique");
        Salle s2 = new Salle("SA02", "Réseau");
        ss.create(s1);
        ss.create(s2);

        // Création des machines
        ms.create(new Machine("MA01", "HP", 6000, new Date(), s1));
        ms.create(new Machine("MA02", "Dell", 5500, new Date(), s1));
        ms.create(new Machine("MA03", "Lenovo", 7000, new Date(), s2));

        // Afficher les machines d'une salle
        Salle salle = ss.findById(1);
        if (salle != null) {
            System.out.println("Salle : " + salle.getCode());
            for (Machine m : salle.getMachines()) {
                System.out.println("  Machine : " + m.getRef());
            }
        }
    }
}
