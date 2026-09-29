package ca.uqo.inf1163.borne.domain;

import java.nio.file.*;
import java.util.*;
import java.io.*;

/**
 * Catalogue des exemplaires de la bibliothèque.
 *
 * Cette classe charge les données à partir d'un fichier CSV et fournit
 * des méthodes pour rechercher un exemplaire par son RFID.
 */
public class Catalogue {
    // On indexe les exemplaires par RFID pour les retrouver rapidement
    private final Map<String, Exemplaire> exemplaires = new HashMap<>();

    /**
     * Retourne l'exemplaire correspondant au RFID donné,
     * ou null s'il n'existe pas dans le catalogue.
     */
    public Exemplaire rechercherExemplaire(String rfid) {
        return exemplaires.get(rfid);
    }

    /**
     * Charge le catalogue à partir d'un fichier CSV.
     *
     * Format attendu :
     *   RFID,Titre,Auteur,Edition,DateParution,NombrePages
     */
    public void chargerDepuisCSV(Path p) throws IOException {
        if (p == null) return;
        File f = p.toFile();
        if (!f.exists()) return;

        try (var br = new BufferedReader(new FileReader(f))) {
            String line;
            boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) {
                    // On ignore la première ligne (en-tête des colonnes)
                    header = false;
                    continue;
                }
                String[] t = line.split(",");
                if (t.length < 6) continue; // ligne mal formée, on l'ignore

                // Construction de l'objet Livre
                Livre l = new Livre();
                l.titre = t[1];
                l.auteur = t[2];
                l.editeur = t[3];
                try { l.annee = Integer.parseInt(t[4]); } catch (Exception e) { l.annee = 0; }
                try { l.nbPages = Integer.parseInt(t[5]); } catch (Exception e) { l.nbPages = 0; }

                // Construction de l'exemplaire associé
                Exemplaire ex = new Exemplaire();
                ex.rfid = t[0].trim();
                ex.livre = l;
                ex.disponible = true;

                exemplaires.put(ex.rfid, ex);
            }
        }
    }
}
