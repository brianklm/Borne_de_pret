package ca.uqo.inf1163.borne.service;

import ca.uqo.inf1163.borne.domain.*;
import java.time.LocalDate;

import javax.swing.JOptionPane;

/**
 * Service qui gère la logique d'emprunt.
 *
 * Il utilise :
 *  - le Catalogue pour retrouver les exemplaires à partir des RFIDs
 *  - le ReceiptService pour générer le reçu
 *  - SessionEmprunt pour mémoriser les exemplaires de la session.
 */
public class EmpruntService {
    private final Catalogue cat;
    private final ReceiptService receipt;
    private SessionEmprunt session;

    /** Langue courante pour les messages et les textes. */
    private Language language = Language.FR;

    public EmpruntService(Catalogue c, ReceiptService r) {
        this.cat = c;
        this.receipt = r;
    }

    /** Permet à la fenêtre principale de mettre à jour la langue. */
    public void setLanguage(Language lang) {
        if (lang != null) {
            this.language = lang;
        }
    }

    public Language getLanguage() {
        return language;
    }

    /**
     * Démarre une nouvelle session d'emprunt pour un usager donné.
     */
    public void nouvelleSession(Usager u, LocalDate retour) {
        this.session = new SessionEmprunt(u, retour);
    }

    /**
     * Tente d'ajouter un exemplaire à partir de son RFID.
     *
     * Cette méthode vérifie :
     *  - que le RFID a bien 6 chiffres
     *  - que l'exemplaire existe dans le catalogue
     *  - qu'il est disponible.
     *
     * En cas de problème, un message d'erreur est affiché à l'écran.
     */
    public void ajouterExemplaire(String rfid) {
        if (rfid == null || !rfid.matches("\\d{6}")) {
            JOptionPane.showMessageDialog(
                null,
                (language == Language.FR
                    ? "RFID invalide (6 chiffres requis)."
                    : "Invalid RFID (6 digits required)."),
                (language == Language.FR ? "Erreur RFID" : "RFID error"),
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        Exemplaire ex = cat.rechercherExemplaire(rfid);
        if (ex == null) {
            JOptionPane.showMessageDialog(
                null,
                (language == Language.FR
                    ? "Exemplaire introuvable."
                    : "Item not found."),
                (language == Language.FR ? "Erreur RFID" : "RFID error"),
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        if (!ex.disponible) {
            JOptionPane.showMessageDialog(
                null,
                (language == Language.FR
                    ? "Exemplaire non disponible."
                    : "Item not available."),
                (language == Language.FR ? "Erreur disponibilité" : "Availability error"),
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        // Tout est correct : on ajoute l'exemplaire à la session
        session.ajouter(ex);
    }

    /**
     * Produit un petit résumé textuel des documents saisis pendant la session.
     */
    public String produireRecapitulatif() {
        if (session == null || session.lignes.isEmpty()) {
            return (language == Language.FR
                    ? "Aucun exemplaire saisi."
                    : "No item scanned.");
        }
        StringBuilder b = new StringBuilder();
        if (language == Language.FR) {
            b.append("Récapitulatif :\n");
        } else {
            b.append("Summary:\n");
        }
        for (Exemplaire e : session.lignes) {
            if (language == Language.FR) {
                b.append(" - ").append(e.livre.titre)
                 .append(" — retour le ").append(session.dateRetour).append("\n");
            } else {
                b.append(" - ").append(e.livre.titre)
                 .append(" — due on ").append(session.dateRetour).append("\n");
            }
        }
        return b.toString();
    }

    /**
     * Demande au service de reçus de générer un texte formaté style bibliothèque.
     */
    public String genererRecu() {
        return receipt.generer(session, language);
    }

    /**
     * Finalise l'emprunt : les exemplaires de la session deviennent "non disponibles".
     */
    public void finaliserEmprunt() {
        if (session == null) return;
        for (Exemplaire e : session.lignes) {
            e.disponible = false;
        }
    }
}
