package ca.uqo.inf1163.borne.service;

import java.util.*;
import ca.uqo.inf1163.borne.domain.*;

/**
 * Service d'authentification.
 *
 * Pour ce mini-projet, les usagers sont conservés en mémoire (HashMap)
 * et non dans une base de données. Cette classe encapsule la logique de
 * validation du compte et du NIP.
 */
public class AuthService {
    private final Map<String, Usager> users = new HashMap<>();

    /**
     * Ajoute un usager au "répertoire" interne.
     */
    public void ajouterUtilisateur(Usager u) {
        users.put(u.compte(), u);
    }

    /**
     * Vérifie si le compte et le NIP correspondent à un usager connu.
     *
     * @return l'usager correspondant ou null si les informations sont invalides.
     */
    public Usager valider(String compte, String nip) {
        Usager u = users.get(compte);
        if (u != null && u.nip().equals(nip)) {
            return u;
        }
        return null;
    }
}
