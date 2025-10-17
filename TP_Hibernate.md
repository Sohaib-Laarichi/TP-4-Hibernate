# TP d'ingénierie sur Hibernate, MySQL et Java

## Introduction

L'objectif de ce TP est de vous apprendre à configurer et à utiliser Hibernate, un framework de mapping objet-relationnel (ORM) populaire en Java, pour gérer la persistance des données dans une base de données MySQL. À travers une série d'étapes guidées, vous construirez une application Java simple qui gère des salles et des machines, en utilisant les concepts clés de JPA (Java Persistence API) et Hibernate.

À la fin de ce TP, vous serez capable de :
- Configurer un projet Maven avec les dépendances Hibernate et MySQL.
- Créer des entités JPA et mapper leurs relations.
- Mettre en place la configuration d'Hibernate pour se connecter à une base de données.
- Implémenter une couche d'accès aux données (DAO) générique.
- Développer une couche de services pour la logique métier.
- Écrire des tests unitaires pour valider les opérations CRUD (Create, Read, Update, Delete).

## Étape 1 : Configuration du fichier pom.xml

Pour commencer, nous devons configurer notre projet Maven pour inclure les dépendances nécessaires. Ces dépendances sont Hibernate Core pour le mapping ORM, le connecteur MySQL pour la communication avec la base de données, et JUnit pour les tests unitaires.

**Consigne :**
Ouvrez le fichier `pom.xml` de votre projet Maven et ajoutez les dépendances suivantes à l'intérieur de la balise `<dependencies>`. Si la balise n'existe pas, créez-la à l'intérieur de la balise `<project>`.

**Code Maven (`pom.xml`) :**
```xml
<properties>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>

<dependencies>
    <!-- Hibernate ORM -->
    <dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-core</artifactId>
        <version>5.6.15.Final</version>
    </dependency>

    <!-- MySQL Connector -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.33</version>
    </dependency>

    <!-- JUnit 5 for testing -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-api</artifactId>
        <version>5.10.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Étape 2 : Création de l’interface DAO

Le modèle de conception DAO (Data Access Object) est une bonne pratique pour séparer la logique de persistance des données de la logique métier. Nous allons créer une interface générique `IDao<T>` qui définira les opérations CRUD de base. Cette interface pourra ensuite être implémentée pour chaque entité de notre application.

**Consigne :**
Créez un nouveau package `org.example.dao` dans `src/main/java`. Ensuite, à l'intérieur de ce package, créez une nouvelle interface Java nommée `IDao.java` et ajoutez le code suivant.

**Code Java (`IDao.java`) :**
```java
package org.example.dao;

import java.util.List;

public interface IDao<T> {
    boolean create(T o);
    boolean update(T o);
    boolean delete(T o);
    T findById(int id);
    List<T> findAll();
}
```

## Étape 3 : Création des entités JPA

Les entités JPA sont des classes Java qui correspondent à des tables dans la base de données. Nous allons créer deux entités : `Salle` et `Machine`. Une salle peut contenir plusieurs machines, ce qui représente une relation "un à plusieurs" (One-to-Many).

**Consigne :**
Créez un nouveau package `org.example.entities` dans `src/main/java`. Ensuite, créez les classes `Salle.java` et `Machine.java` avec le contenu suivant.

**Annotations JPA utilisées :**
- `@Entity` : Spécifie que la classe est une entité JPA.
- `@Id` : Désigne la clé primaire de l'entité.
- `@GeneratedValue(strategy = GenerationType.IDENTITY)` : Configure la manière dont la clé primaire est générée (ici, auto-incrémentée par la base de données).
- `@Column` : Permet de spécifier le nom de la colonne (optionnel si le nom de l'attribut correspond).
- `@ManyToOne` : Définit une relation "plusieurs à un" avec une autre entité.
- `@OneToMany` : Définit une relation "un à plusieurs". `mappedBy` indique que l'autre côté de la relation est propriétaire.
- `@Temporal(TemporalType.DATE)` : Spécifie que l'attribut `Date` doit être mappé à une colonne de type DATE en SQL.

**Code Java (`Salle.java`) :**
```java
package org.example.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class Salle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String code;
    private String type;

    @OneToMany(mappedBy = "salle", fetch = FetchType.EAGER)
    private List<Machine> machines;

    public Salle() {
    }

    public Salle(String code, String type) {
        this.code = code;
        this.type = type;
    }

    // Getters and Setters
}
```

**Code Java (`Machine.java`) :**
```java
package org.example.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Machine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String ref;
    private String marque;
    private double prix;
    @Temporal(TemporalType.DATE)
    private Date dateAchat;

    @ManyToOne
    @JoinColumn(name = "salle_id")
    private Salle salle;

    public Machine() {
    }

    public Machine(String ref, String marque, double prix, Date dateAchat, Salle salle) {
        this.ref = ref;
        this.marque = marque;
        this.prix = prix;
        this.dateAchat = dateAchat;
        this.salle = salle;
    }

    // Getters and Setters
}
```

## Étape 4 : Configuration de Hibernate

Le fichier `hibernate.cfg.xml` est le cœur de la configuration d'Hibernate. Il contient les informations de connexion à la base de données, le dialecte SQL à utiliser, et la liste des classes d'entités que Hibernate doit gérer.

**Consigne :**
Créez un fichier nommé `hibernate.cfg.xml` à la racine du répertoire `src/main/resources` et ajoutez-y le contenu suivant. Assurez-vous de remplacer `root` et le mot de passe par vos propres identifiants de base de données MySQL si nécessaire.

**Propriétés de configuration :**
- `hibernate.connection.driver_class` : Le driver JDBC pour MySQL.
- `hibernate.connection.url` : L'URL de connexion à la base de données. `createDatabaseIfNotExist=true` crée la base de données si elle n'existe pas.
- `hibernate.connection.username` / `password` : Vos identifiants de connexion.
- `hibernate.dialect` : Le dialecte SQL spécifique à votre version de MySQL.
- `hibernate.show_sql` : Affiche les requêtes SQL générées par Hibernate dans la console.
- `hibernate.hbm2ddl.auto` : `update` met à jour le schéma de la base de données (crée les tables si elles n'existent pas, les modifie si nécessaire).
- `<mapping class="..." />` : Déclare les classes d'entités à Hibernate.

**Code XML (`hibernate.cfg.xml`) :**
```xml
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <!-- Database connection settings -->
        <property name="hibernate.connection.driver_class">com.mysql.cj.jdbc.Driver</property>
        <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/db_hibernate?createDatabaseIfNotExist=true</property>
        <property name="hibernate.connection.username">root</property>
        <property name="hibernate.connection.password"></property>

        <!-- SQL dialect -->
        <property name="hibernate.dialect">org.hibernate.dialect.MySQL8Dialect</property>

        <!-- Echo all executed SQL to stdout -->
        <property name="hibernate.show_sql">true</property>

        <!-- Drop and re-create the database schema on startup -->
        <property name="hibernate.hbm2ddl.auto">update</property>

        <!-- Mapped entity classes -->
        <mapping class="org.example.entities.Salle"/>
        <mapping class="org.example.entities.Machine"/>
    </session-factory>
</hibernate-configuration>
```

## Étape 5 : Création d'une classe utilitaire HibernateUtil

Pour interagir avec Hibernate, nous avons besoin d'une `SessionFactory`, qui est un objet coûteux à créer et thread-safe. Il est donc recommandé de n'en créer qu'une seule instance pour toute l'application. Nous allons créer une classe utilitaire `HibernateUtil` pour gérer l'initialisation de la `SessionFactory` en utilisant le pattern Singleton.

**Consigne :**
Créez un nouveau package `org.example.util` et, à l'intérieur, une classe `HibernateUtil.java`. Cette classe lira le fichier `hibernate.cfg.xml` et construira la `SessionFactory`.

**Code Java (`HibernateUtil.java`) :**
```java
package org.example.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory;

    static {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
```

## Étape 6 : Création de la couche Service

La couche de service agit comme un intermédiaire entre la couche de présentation (ou les tests, dans notre cas) et la couche d'accès aux données (DAO). Elle implémente la logique métier et utilise les services DAO pour interagir avec la base de données. Chaque service implémentera l'interface `IDao` que nous avons définie.

**Consigne :**
Créez un nouveau package `org.example.service`. À l'intérieur, créez les classes `SalleService.java` et `MachineService.java` qui implémentent `IDao<Salle>` et `IDao<Machine>` respectivement. Ces classes utiliseront la `Session` d'Hibernate pour effectuer les opérations CRUD.

**Schéma d'organisation :**
- `SalleService` implémente `IDao<Salle>`
- `MachineService` implémente `IDao<Machine>`

Chaque méthode (create, update, delete, findById, findAll) suit un modèle similaire :
1.  Ouvrir une `Session` Hibernate via `HibernateUtil`.
2.  Commencer une `Transaction`.
3.  Effectuer l'opération de base de données (save, update, delete, get, createQuery).
4.  Valider la `Transaction`.
5.  En cas d'erreur, effectuer un `rollback`.
6.  Fermer la `Session`.

**Code Java (`SalleService.java`) :**
```java
package org.example.service;

import org.example.dao.IDao;
import org.example.entities.Salle;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class SalleService implements IDao<Salle> {
    @Override
    public boolean create(Salle o) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.save(o);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
        } finally {
            if (session != null)
                session.close();
        }
        return false;
    }

    @Override
    public boolean update(Salle o) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.update(o);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
        } finally {
            if (session != null)
                session.close();
        }
        return false;
    }

    @Override
    public boolean delete(Salle o) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.delete(o);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
        } finally {
            if (session != null)
                session.close();
        }
        return false;
    }

    @Override
    public Salle findById(int id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.get(Salle.class, id);
        } finally {
            if (session != null)
                session.close();
        }
    }

    @Override
    public List<Salle> findAll() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.createQuery("from Salle", Salle.class).list();
        } finally {
            if (session != null)
                session.close();
        }
    }
}
```

**Code Java (`MachineService.java`) :**
```java
package org.example.service;

import org.example.dao.IDao;
import org.example.entities.Machine;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class MachineService implements IDao<Machine> {
    @Override
    public boolean create(Machine o) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.save(o);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
        } finally {
            if (session != null)
                session.close();
        }
        return false;
    }

    @Override
    public boolean update(Machine o) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.update(o);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
        } finally {
            if (session != null)
                session.close();
        }
        return false;
    }

    @Override
    public boolean delete(Machine o) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.delete(o);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
        } finally {
            if (session != null)
                session.close();
        }
        return false;
    }

    @Override
    public Machine findById(int id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.get(Machine.class, id);
        } finally {
            if (session != null)
                session.close();
        }
    }

    @Override
    public List<Machine> findAll() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.createQuery("from Machine", Machine.class).list();
        } finally {
            if (session != null)
                session.close();
        }
    }
}
```

## Étape 7 : Classe de test pour générer la base de données

Pour vérifier que notre configuration fonctionne et pour générer le schéma de la base de données, nous pouvons créer une classe principale qui instancie nos services et sauvegarde quelques objets. En raison de la configuration `hibernate.hbm2ddl.auto` à `update`, Hibernate créera automatiquement les tables `salle` et `machine` lors de la première exécution.

**Consigne :**
Utilisez la classe `Main.java` existante (ou créez-la si elle n'existe pas) dans `src/main/java/org/example` pour ajouter le code qui peuple la base de données.

**Code Java (`Main.java`) :**
```java
package org.example;

import org.example.entities.Machine;
import org.example.entities.Salle;
import org.example.service.MachineService;
import org.example.service.SalleService;

import java.util.Date;

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
```
Après avoir exécuté cette classe, vous pouvez vérifier votre base de données MySQL. Vous devriez y trouver une base `db_hibernate` avec les tables `salle` et `machine` remplies avec les données que nous venons de créer.

## Étape 8 : Mise en place des tests unitaires JUnit

Les tests unitaires sont essentiels pour garantir la qualité et la non-régression du code. Nous allons utiliser JUnit 5 pour tester les opérations CRUD de nos services.

**Consigne :**
Créez les classes de test `SalleServiceTest.java` et `MachineServiceTest.java` dans le répertoire `src/test/java/org/example`.

**Structure d'une classe de test :**
-   `@BeforeEach` : Une méthode annotée avec `@BeforeEach` est exécutée avant chaque méthode de test. Nous l'utiliserons pour initialiser les services et créer des objets de test.
-   `@AfterEach` : Exécutée après chaque test, cette méthode est idéale pour nettoyer la base de données en supprimant les objets créés pendant le test, afin que les tests soient indépendants les uns des autres.
-   `@Test` : Marque une méthode comme étant une méthode de test.
-   `Assertions` : La classe `Assertions` de JUnit (par exemple, `assertTrue`, `assertNull`, `assertEquals`) est utilisée pour vérifier les résultats des opérations.

**Code Java (`SalleServiceTest.java`) :**
```java
package org.example;

import org.example.entities.Salle;
import org.example.service.SalleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SalleServiceTest {
    private SalleService salleService;
    private Salle salle;

    @BeforeEach
    void setUp() {
        salleService = new SalleService();
        salle = new Salle("Test", "Test");
    }

    @AfterEach
    void tearDown() {
        if (salle != null && salleService.findById(salle.getId()) != null) {
            salleService.delete(salle);
        }
        salleService = null;
        salle = null;
    }

    @Test
    void testCreate() {
        assertTrue(salleService.create(salle));
        assertNotNull(salleService.findById(salle.getId()));
    }

    @Test
    void testUpdate() {
        salleService.create(salle);
        salle.setCode("Updated");
        assertTrue(salleService.update(salle));
        assertEquals("Updated", salleService.findById(salle.getId()).getCode());
    }

    @Test
    void testDelete() {
        salleService.create(salle);
        assertTrue(salleService.delete(salle));
        assertNull(salleService.findById(salle.getId()));
    }

    @Test
    void testFindById() {
        salleService.create(salle);
        assertNotNull(salleService.findById(salle.getId()));
    }

    @Test
    void testFindAll() {
        salleService.create(salle);
        assertFalse(salleService.findAll().isEmpty());
    }
}
```

**Code Java (`MachineServiceTest.java`) :**
```java
package org.example;

import org.example.entities.Machine;
import org.example.entities.Salle;
import org.example.service.MachineService;
import org.example.service.SalleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class MachineServiceTest {
    private MachineService machineService;
    private SalleService salleService;
    private Machine machine;
    private Salle salle;

    @BeforeEach
    void setUp() {
        machineService = new MachineService();
        salleService = new SalleService();
        salle = new Salle("Test", "Test");
        salleService.create(salle);
        machine = new Machine("Test", "Test", 1000, new Date(), salle);
    }

    @AfterEach
    void tearDown() {
        if (machine != null && machineService.findById(machine.getId()) != null) {
            machineService.delete(machine);
        }
        if (salle != null && salleService.findById(salle.getId()) != null) {
            salleService.delete(salle);
        }
        machineService = null;
        salleService = null;
        machine = null;
        salle = null;
    }

    // Tests pour create, update, delete, findById, findAll...
}
```

Félicitations ! Vous avez terminé ce TP sur Hibernate. Vous avez maintenant les bases pour construire des applications Java plus complexes avec une persistance des données robuste.
