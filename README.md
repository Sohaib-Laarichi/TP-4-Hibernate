# TP d'Ingénierie sur Hibernate, MySQL et Java

Ce projet est une application Java simple démontrant l'utilisation d'Hibernate pour le mapping objet-relationnel (ORM) avec une base de données MySQL. Il met en œuvre des opérations CRUD de base pour gérer des entités `Salle` et `Machine`.

## Technologies Utilisées

- **Java 8**
- **Maven** : Gestion des dépendances et du cycle de vie du projet.
- **Hibernate ORM 5.6** : Framework de persistance des données.
- **MySQL Connector/J 8.0** : Driver JDBC pour la base de données MySQL.
- **JUnit 5** : Framework de test pour les tests unitaires.

## Structure du Projet

Le projet suit une structure Maven standard :

```
.
├── pom.xml                 # Fichier de configuration Maven
├── README.md               # Ce fichier
├── TP_Hibernate.md         # Énoncé complet du TP
└── src
    ├── main
    │   ├── java
    │   │   └── org
    │   │       └── example
    │   │           ├── dao         # Interface DAO générique
    │   │           ├── entities    # Classes d'entités (Machine, Salle)
    │   │           ├── service     # Classes de service (logique métier)
    │   │           ├── util        # Classe utilitaire HibernateUtil
    │   │           └── Main.java   # Point d'entrée pour tester la création de données
    │   └── resources
    │       └── hibernate.cfg.xml   # Fichier de configuration d'Hibernate
    └── test
        └── java
            └── org
                └── example
                    ├── MachineServiceTest.java # Tests unitaires pour MachineService
                    └── SalleServiceTest.java   # Tests unitaires pour SalleService
```

## Comment l'utiliser

### Prérequis

1.  **JDK 8** ou supérieur installé.
2.  **Maven** installé.
3.  Un serveur **MySQL** en cours d'exécution.

### Configuration

1.  **Base de données** : Le projet est configuré pour se connecter à une base de données MySQL nommée `db_hibernate`. Le fichier `src/main/resources/hibernate.cfg.xml` contient la configuration.
    - URL : `jdbc:mysql://localhost:3306/db_hibernate?createDatabaseIfNotExist=true`
    - Utilisateur : `root`
    - Mot de passe : (vide par défaut)

    Modifiez ces valeurs si votre configuration MySQL est différente.

2.  **Dépendances** : Exécutez la commande suivante à la racine du projet pour télécharger les dépendances Maven :
    ```sh
    mvn clean install
    ```

### Exécuter l'application

Pour peupler la base de données avec des données de test, vous pouvez exécuter la méthode `main` de la classe `org.example.Main`. Cela créera quelques salles et machines.

### Exécuter les tests

Pour lancer la suite de tests unitaires JUnit, exécutez la commande Maven suivante :

```sh
mvn test
```

Les tests valideront les opérations CRUD pour les services `SalleService` et `MachineService`.

