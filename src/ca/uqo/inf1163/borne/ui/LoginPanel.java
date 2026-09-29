package ca.uqo.inf1163.borne.ui;

import javax.swing.*;
import java.awt.*;

import ca.uqo.inf1163.borne.domain.Language;

/**
 * Panneau de connexion.
 *
 * Ce panneau permet à l'usager de saisir :
 * - son compte (par exemple : jean123)
 * - son NIP (4 chiffres)
 *
 * Il propose aussi un choix de langue (Français / English).
 */
public class LoginPanel extends JPanel {
    private JTextField txtCompte;
    private JPasswordField txtNip;
    private JLabel lblError;
    private JLabel lblTitle;
    private JLabel lblCompte;
    private JLabel lblNip;
    private JButton btnLogin;
    private JComboBox<String> cmbLang;

    private BorneFrame frame;

    public LoginPanel(BorneFrame frame) {
        this.frame = frame;

        // On utilise un GridBagLayout pour avoir un formulaire propre et centré
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Titre
        lblTitle = new JLabel();
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 20f));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitle, gbc);

        // Ligne Choix de langue
        gbc.gridy++;
        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        langPanel.add(new JLabel("Langue / Language :"));
        cmbLang = new JComboBox<>(new String[]{"Français", "English"});
        langPanel.add(cmbLang);
        add(langPanel, gbc);

        // Champ "Compte"
        gbc.gridwidth = 1;
        gbc.gridy++;
        lblCompte = new JLabel();
        add(lblCompte, gbc);
        txtCompte = new JTextField(15);
        gbc.gridx = 1;
        add(txtCompte, gbc);

        // Champ "NIP"
        gbc.gridx = 0; gbc.gridy++;
        lblNip = new JLabel();
        add(lblNip, gbc);
        txtNip = new JPasswordField(15);
        gbc.gridx = 1;
        add(txtNip, gbc);

        // Label d'erreur (en rouge)
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        lblError = new JLabel(" ");
        lblError.setForeground(Color.RED);
        add(lblError, gbc);

        // Bouton de connexion
        btnLogin = new JButton();
        gbc.gridy++;
        add(btnLogin, gbc);

        // Actions
        btnLogin.addActionListener(e -> doLogin());
        txtNip.addActionListener(e -> doLogin());

        cmbLang.addActionListener(e -> {
            if (cmbLang.getSelectedIndex() == 0) {
                frame.setLanguage(Language.FR);
            } else {
                frame.setLanguage(Language.EN);
            }
        });
    }

    /**
     * Applique la langue sur tous les textes du panneau.
     */
    public void applyLanguage(Language lang) {
        if (lang == Language.FR) {
            lblTitle.setText("Borne de prêt – Connexion");
            lblCompte.setText("Compte :");
            lblNip.setText("NIP (4 chiffres) :");
            btnLogin.setText("Connexion");
            cmbLang.setSelectedIndex(0);
        } else {
            lblTitle.setText("Self-checkout – Login");
            lblCompte.setText("Account :");
            lblNip.setText("PIN (4 digits) :");
            btnLogin.setText("Login");
            cmbLang.setSelectedIndex(1);
        }
    }

    /**
     * Récupère les champs et demande à la fenêtre principale de valider la connexion.
     */
    private void doLogin() {
        lblError.setText(" ");
        String compte = txtCompte.getText();
        String nip = new String(txtNip.getPassword());

        // Validation simple côté interface
        if (compte.isBlank() || nip.isBlank()) {
            if (frame.getLanguage() == Language.FR) {
                showError("Veuillez remplir tous les champs.");
            } else {
                showError("Please fill in all fields.");
            }
            return;
        }
        // La logique métier (vérification du compte) est déléguée à BorneFrame
        frame.tryLogin(compte, nip);
    }

    /**
     * Affiche un message d'erreur sous les champs.
     */
    public void showError(String msg) {
        lblError.setText(msg);
    }

    /**
     * Réinitialise le formulaire pour le prochain usager.
     */
    public void clearFields() {
        txtCompte.setText("");
        txtNip.setText("");
        lblError.setText(" ");
    }
}
