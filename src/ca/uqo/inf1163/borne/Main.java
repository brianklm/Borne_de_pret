package ca.uqo.inf1163.borne;

import javax.swing.SwingUtilities;
import ca.uqo.inf1163.borne.ui.BorneFrame;

/**
 * Classe principale du mini-projet.
 * C'est ici que l'application démarre.
 *
 * On utilise SwingUtilities.invokeLater pour démarrer l'interface graphique
 * sur le thread dédié à Swing (bonne pratique dans les applications Java GUI).
 */
public class Main {

    public static void main(String[] args){
        // On demande à Swing de créer la fenêtre principale de façon sécuritaire
        SwingUtilities.invokeLater(() -> {
            new BorneFrame().setVisible(true);
        });
    }
}
