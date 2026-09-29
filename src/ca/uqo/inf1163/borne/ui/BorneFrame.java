package ca.uqo.inf1163.borne.ui;

import javax.swing.*;
import java.awt.CardLayout;
import java.nio.file.*;
import java.time.LocalDate;
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import ca.uqo.inf1163.borne.domain.*;
import ca.uqo.inf1163.borne.service.*;

/**
 * Fenêtre principale de l'application.
 *
 * Elle joue le rôle de "borne de prêt" :
 * - gère le catalogue et les services métiers (authentification, emprunt, reçu)
 * - affiche les deux écrans : connexion et écran d'emprunt
 * - gère aussi la langue (français / anglais).
 */
public class BorneFrame extends JFrame {
    // Couche "domaine" et "service"
    private final Catalogue catalogue = new Catalogue();
    private final AuthService auth = new AuthService();
    private final ReceiptService receipt = new ReceiptService();
    private final EmpruntService emprunt = new EmpruntService(catalogue, receipt);

    // Langue courante de l'interface (par défaut : FR)
    private Language language = Language.FR;

    // Gestion de la navigation entre les panneaux (comme un petit "router")
    private CardLayout cardLayout = new CardLayout();
    private JPanel cards = new JPanel(cardLayout);

    // Les deux panneaux principaux de l'interface
    private LoginPanel loginPanel;
    private EmpruntPanel empruntPanel;

    /**
     * Constructeur : configuration de la fenêtre et initialisation du système.
     */
    public BorneFrame() {
        super();
        updateTitle();  // initialise le titre selon la langue
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 520);
        setLocationRelativeTo(null); // centre la fenêtre à l'écran

        // Charge les données (catalogue + usagers de test)
        initSystem();

        // Crée les écrans et les enregistre dans le "CardLayout"
        loginPanel = new LoginPanel(this);
        empruntPanel = new EmpruntPanel(this);

        // Applique la langue courante aux panneaux et services
        loginPanel.applyLanguage(language);
        empruntPanel.applyLanguage(language);
        emprunt.setLanguage(language);

        cards.add(loginPanel, "LOGIN");
        cards.add(empruntPanel, "EMPRUNT");
        setContentPane(cards);
    }

    /**
     * Met à jour le titre de la fenêtre en fonction de la langue.
     */
    private void updateTitle() {
        if (language == Language.FR) {
            setTitle("PROJET DE PRÊT DE MINI-BIBLIOTHÈQUE");
        } else {
            setTitle("MINI LIBRARY LOAN PROJECT");
        }
    }

    /**
     * Change la langue de l'application (appelé par le panneau de login).
     */
    public void setLanguage(Language language) {
        if (language == null) return;
        this.language = language;
        updateTitle();
        if (loginPanel != null) {
            loginPanel.applyLanguage(language);
        }
        if (empruntPanel != null) {
            empruntPanel.applyLanguage(language);
        }
        emprunt.setLanguage(language);
    }

    /**
     * Retourne la langue actuelle.
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Initialise la logique métier :
     * - charge le catalogue à partir du fichier CSV
     * - crée quelques usagers de test.
     */
    private void initSystem() {
        try {
            // On suppose que le fichier catalogue.csv se trouve dans le dossier "resources"
            Path p = Paths.get("resources", "catalogue.csv");
            catalogue.chargerDepuisCSV(p);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement du catalogue: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        // Usagers "simulés" pour tester l'authentification
        auth.ajouterUtilisateur(new Usager("jean123", "1234", "Jean"));
        auth.ajouterUtilisateur(new Usager("jeanne456", "4567", "Jeanne"));
    }

    /**
     * Appelée par le panneau de login lorsqu'un utilisateur tente de se connecter.
     * Si l'authentification réussit, on passe à l'écran d'emprunt.
     */
    public void tryLogin(String compte, String nip) {
        Usager u = auth.valider(compte.trim(), nip.trim());
        if (u == null) {
            // Affiche un message d'erreur dans le panneau de connexion
            loginPanel.showError(
                language == Language.FR ? "Identifiants invalides." : "Invalid credentials."
            );
        } else {
            // Démarre une nouvelle session d'emprunt pour cet usager
            LocalDate dateRetour = LocalDate.now().plusDays(21); // par exemple : 21 jours
            emprunt.nouvelleSession(u, dateRetour);
            empruntPanel.setUsager(u);
            empruntPanel.resetSession();
            // Affiche le panneau d'emprunt
            cardLayout.show(cards, "EMPRUNT");
        }
    }

    /**
     * Appelée par le panneau d'emprunt lorsque l'utilisateur saisit un RFID.
     */
    public void ajouterRFID(String rfid) {
        emprunt.ajouterExemplaire(rfid);
        empruntPanel.refreshRecap();
    }

    /**
     * Appelée lorsque l'utilisateur clique sur "Terminer l'emprunt".
     * Gère l'affichage du récapitulatif, l'impression du reçu, la sauvegarde éventuelle
     * et la finalisation.
     */
    public void terminerEmprunt(boolean imprimerRecu) {
        String recapTitle = (language == Language.FR) ? "Récapitulatif" : "Summary";

        // Affiche d'abord un récapitulatif simple
        String recap = emprunt.produireRecapitulatif();
        JOptionPane.showMessageDialog(this, recap, recapTitle, JOptionPane.INFORMATION_MESSAGE);

        String recu = null;

        // Si l'utilisateur a coché "Imprimer un reçu"
        if (imprimerRecu) {
            recu = emprunt.genererRecu();
            JTextArea area = new JTextArea(recu);
            area.setEditable(false);
            // Police monospacée pour bien aligner les colonnes du reçu
            area.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));
            JOptionPane.showMessageDialog(
                this,
                new JScrollPane(area),
                (language == Language.FR ? "Bibliothèque – Reçu d'emprunt" : "Library – Loan receipt"),
                JOptionPane.INFORMATION_MESSAGE
            );

            // Proposer d'enregistrer le reçu dans un fichier texte
            int saveChoice = JOptionPane.showConfirmDialog(
                this,
                (language == Language.FR
                    ? "Voulez-vous enregistrer le reçu dans un fichier texte ?"
                    : "Do you want to save the receipt as a text file?"),
                (language == Language.FR ? "Enregistrer le reçu" : "Save receipt"),
                JOptionPane.YES_NO_OPTION
            );

            if (saveChoice == JOptionPane.YES_OPTION) {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle(language == Language.FR
                    ? "Choisir l'emplacement du fichier"
                    : "Choose file location");
                chooser.setSelectedFile(new File("recu_emprunt.txt"));
                int userRes = chooser.showSaveDialog(this);
                if (userRes == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    try (PrintWriter out = new PrintWriter(file, StandardCharsets.UTF_8)) {
                        out.print(recu);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                            this,
                            (language == Language.FR
                                ? "Erreur lors de l'enregistrement du reçu : "
                                : "Error while saving receipt: ") + ex.getMessage(),
                            (language == Language.FR ? "Erreur" : "Error"),
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        }

        // Marque les exemplaires comme empruntés et termine la session
        emprunt.finaliserEmprunt();
        JOptionPane.showMessageDialog(this,
            (language == Language.FR ? "Emprunt complété. Merci !" : "Loan completed. Thank you!"),
            (language == Language.FR ? "Succès" : "Success"),
            JOptionPane.INFORMATION_MESSAGE);

        // Retour à l'écran de connexion pour le prochain usager
        cardLayout.show(cards, "LOGIN");
        loginPanel.clearFields();
    }

    /**
     * Donne accès au service d'emprunt pour les autres panneaux.
     */
    public EmpruntService getEmpruntService() {
        return emprunt;
    }
}
