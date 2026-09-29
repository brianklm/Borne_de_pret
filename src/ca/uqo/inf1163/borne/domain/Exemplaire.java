package ca.uqo.inf1163.borne.domain;

/**
 * Représente un exemplaire physique d'un livre dans la bibliothèque.
 *
 * Plusieurs exemplaires peuvent faire référence au même Livre,
 * mais chacun possède un RFID unique.
 */
public class Exemplaire {
    public String rfid;    // Code RFID (6 chiffres) qui identifie l'exemplaire
    public Livre livre;    // Référence au livre associé
    public boolean disponible = true; // true si l'exemplaire est empruntable
}
