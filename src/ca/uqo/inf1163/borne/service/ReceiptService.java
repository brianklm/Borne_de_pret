package ca.uqo.inf1163.borne.service;

import ca.uqo.inf1163.borne.domain.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Service responsable de la génération du reçu d'emprunt.
 *
 * Le reçu produit est un texte formaté "style ticket de bibliothèque",
 * avec en-tête, informations de l'usager, tableau des documents et message final.
 */
public class ReceiptService {

    // Informations affichées dans l'en-tête du ticket
    private static final String BIB_NOM_FR    = "BIBLIOTHÈQUE UQO";
    private static final String BIB_NOM_EN    = "UQO LIBRARY";
    private static final String BIB_ADRESSE_FR = "283, boul. Alexandre-Taché, Gatineau";
    private static final String BIB_ADRESSE_EN = "283, Alexandre-Taché Blvd, Gatineau";
    private static final String BIB_TEL_FR     = "Tél. : 819-595-3900";
    private static final String BIB_TEL_EN     = "Tel: 819-595-3900";

    /**
     * Génère le reçu pour la session d'emprunt en cours.
     *
     * @param s la session d'emprunt (usager + liste d'exemplaires)
     * @param language langue de l'interface (FR ou EN)
     * @return une chaîne de caractères prête à être affichée dans une JTextArea.
     */
    public String generer(SessionEmprunt s, Language language) {
        if (s == null || s.lignes.isEmpty()) {
            return language == Language.FR ? "(aucun emprunt)" : "(no loan)";
        }

        StringBuilder b = new StringBuilder();

        // En-tête de la bibliothèque
        b.append("================================================\n");
        b.append(center(language == Language.FR ? BIB_NOM_FR : BIB_NOM_EN, 48)).append("\n");
        b.append(center(language == Language.FR ? "Reçu d'emprunt" : "Loan receipt", 48)).append("\n");
        b.append(center(language == Language.FR ? BIB_ADRESSE_FR : BIB_ADRESSE_EN, 48)).append("\n");
        b.append(center(language == Language.FR ? BIB_TEL_FR : BIB_TEL_EN, 48)).append("\n");
        b.append("================================================\n");

        // Informations sur l'usager et la date
        LocalDate auj = LocalDate.now();
        LocalTime heure = LocalTime.now();
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");

        if (language == Language.FR) {
            b.append("Usager : ")
             .append(s.usager.nom())
             .append(" (").append(s.usager.compte()).append(")\n");
            b.append("Date   : ")
             .append(auj).append("  ").append(heure.format(tf)).append("\n");
            b.append("Nb doc : ").append(s.lignes.size()).append("\n");
        } else {
            b.append("User   : ")
             .append(s.usager.nom())
             .append(" (").append(s.usager.compte()).append(")\n");
            b.append("Date   : ")
             .append(auj).append("  ").append(heure.format(tf)).append("\n");
            b.append("Items  : ").append(s.lignes.size()).append("\n");
        }
        b.append("------------------------------------------------\n");

        // En-tête du tableau des documents
        if (language == Language.FR) {
            b.append(String.format("%-32s %-12s\n", "Titre", "Retour le"));
        } else {
            b.append(String.format("%-32s %-12s\n", "Title", "Due on"));
        }
        b.append("------------------------------------------------\n");

        // Liste des exemplaires
        for (Exemplaire e : s.lignes) {
            String titre = e.livre.titre;
            if (titre.length() > 30) {
                // On tronque les titres trop longs pour garder un alignement propre
                titre = titre.substring(0, 27) + "...";
            }
            b.append(String.format("%-32s %-12s\n", titre, s.dateRetour));
        }

        b.append("------------------------------------------------\n");
        if (language == Language.FR) {
            b.append("Merci d'avoir utilisé la borne de prêt.\n");
            b.append("Veuillez respecter les dates de retour.\n");
        } else {
            b.append("Thank you for using the self-checkout.\n");
            b.append("Please respect due dates.\n");
        }
        b.append("================================================\n");

        return b.toString();
    }

    /**
     * Centre un texte dans une largeur donnée (utilisé pour l'en-tête).
     */
    private String center(String text, int width) {
        if (text.length() >= width) return text;
        int left = (width - text.length()) / 2;
        String padding = " ".repeat(left);
        return padding + text;
    }
}
