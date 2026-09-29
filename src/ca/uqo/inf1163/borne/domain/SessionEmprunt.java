package ca.uqo.inf1163.borne.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente une session d'emprunt en cours sur la borne.
 *
 * Elle regroupe :
 *  - l'usager connecté
 *  - la date de retour prévue (commune à tous les documents)
 *  - la liste des exemplaires ajoutés pendant cette session.
 */
public class SessionEmprunt {
    public Usager usager;
    public LocalDate dateRetour;
    public List<Exemplaire> lignes = new ArrayList<>();

    public SessionEmprunt(Usager u, LocalDate d) {
        this.usager = u;
        this.dateRetour = d;
    }

    /**
     * Ajoute un exemplaire à la liste des documents empruntés.
     */
    public void ajouter(Exemplaire e) {
        lignes.add(e);
    }
}
