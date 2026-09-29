package ca.uqo.inf1163.borne.ui;

import javax.swing.*;
import java.awt.*;

import ca.uqo.inf1163.borne.domain.Usager;
import ca.uqo.inf1163.borne.domain.Language;
import ca.uqo.inf1163.borne.service.EmpruntService;

/**
 * Panneau d'emprunt.
 *
 * Une fois connecté, l'usager arrive sur cet écran.
 * Il peut :
 *  - saisir des codes RFID (6 chiffres) correspondant aux exemplaires
 *  - voir la liste des documents ajoutés à sa session
 *  - choisir s'il souhaite un reçu
 *  - finaliser l'emprunt ou annuler.
 */
public class EmpruntPanel extends JPanel {
    private BorneFrame frame;
    private JLabel lblUsager;
    private JLabel lblRfid;
    private JTextField txtRfid;
    private JTextArea txtListe;
    private JCheckBox chkRecu;
    private JButton btnAjouter;
    private JButton btnTerminer;
    private JButton btnAnnuler;

    public EmpruntPanel(BorneFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(10,10));

        // Bandeau du haut : affiche l'usager connecté
        JPanel top = new JPanel(new BorderLayout());
        lblUsager = new JLabel("Usager : ");
        lblUsager.setFont(lblUsager.getFont().deriveFont(Font.BOLD, 16f));
        top.add(lblUsager, BorderLayout.WEST);
        add(top, BorderLayout.NORTH);

        // Partie centrale : saisie du RFID + liste des documents saisis
        JPanel center = new JPanel(new BorderLayout());
        JPanel rfidPanel = new JPanel();
        lblRfid = new JLabel();
        rfidPanel.add(lblRfid);
        txtRfid = new JTextField(10);
        rfidPanel.add(txtRfid);
        btnAjouter = new JButton();
        rfidPanel.add(btnAjouter);
        center.add(rfidPanel, BorderLayout.NORTH);

        txtListe = new JTextArea(15, 40);
        txtListe.setEditable(false);
        center.add(new JScrollPane(txtListe), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // Bas de l'écran : options et actions
        JPanel bottom = new JPanel();
        chkRecu = new JCheckBox();
        btnTerminer = new JButton();
        btnAnnuler = new JButton();
        bottom.add(chkRecu);
        bottom.add(btnTerminer);
        bottom.add(btnAnnuler);
        add(bottom, BorderLayout.SOUTH);

        // Gestion des actions utilisateur
        btnAjouter.addActionListener(e -> ajouterRFID());
        txtRfid.addActionListener(e -> ajouterRFID()); // Entrée dans le champ = ajouter aussi
        btnTerminer.addActionListener(e -> terminer());
        btnAnnuler.addActionListener(e -> annuler());

        // Applique la langue courante au chargement
        applyLanguage(frame.getLanguage());
    }

    /**
     * Applique les textes dans la langue courante.
     */
    public void applyLanguage(Language lang) {
        if (lang == Language.FR) {
            lblRfid.setText("RFID (6 chiffres) :");
            chkRecu.setText("Imprimer un reçu");
            btnAjouter.setText("Ajouter");
            btnTerminer.setText("Terminer l'emprunt");
            btnAnnuler.setText("Annuler / Déconnexion");
        } else {
            lblRfid.setText("RFID (6 digits):");
            chkRecu.setText("Print a receipt");
            btnAjouter.setText("Add");
            btnTerminer.setText("Finish loan");
            btnAnnuler.setText("Cancel / Logout");
        }
    }

    /**
     * Mise à jour du label avec le nom de l'usager connecté.
     */
    public void setUsager(Usager u) {
        if (frame.getLanguage() == Language.FR) {
            lblUsager.setText("Usager : " + u.nom() + " (" + u.compte() + ")");
        } else {
            lblUsager.setText("User: " + u.nom() + " (" + u.compte() + ")");
        }
    }

    /**
     * Récupère le RFID saisi et demande à la fenêtre principale de l'ajouter à la session.
     */
    private void ajouterRFID() {
        String rfid = txtRfid.getText().trim();
        if (rfid.isEmpty()) return;
        frame.ajouterRFID(rfid);
        txtRfid.setText("");
        txtRfid.requestFocusInWindow();
    }

    /**
     * Appelé lorsque l'usager clique sur "Terminer l'emprunt".
     * Transmet à la fenêtre principale la demande de finalisation.
     */
    private void terminer() {
        frame.terminerEmprunt(chkRecu.isSelected());
    }

    /**
     * Permet à l'usager d'annuler toute la session en cours
     * et de revenir à l'écran de connexion.
     */
    private void annuler() {
        Language lang = frame.getLanguage();
        String msg = (lang == Language.FR) ? "Annuler la session en cours ?" : "Cancel current session ?";
        String title = (lang == Language.FR) ? "Confirmation" : "Confirmation";

        int res = JOptionPane.showConfirmDialog(this, msg, title,
                                                JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            frame.terminerEmprunt(false);
        }
    }

    /**
     * Met à jour la zone de texte avec le récapitulatif des documents saisis.
     */
    public void refreshRecap() {
        EmpruntService service = frame.getEmpruntService();
        txtListe.setText(service.produireRecapitulatif());
    }

    /**
     * Réinitialise ce panneau pour le prochain usager.
     */
    public void resetSession() {
        txtListe.setText("");
        txtRfid.setText("");
        chkRecu.setSelected(false);
    }
}
