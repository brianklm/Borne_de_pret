package ca.uqo.inf1163.borne.domain;

/**
 * Représente les informations bibliographiques d'un livre.
 *
 * Attention : cette classe ne contient pas les informations sur l'exemplaire
 * (RFID, disponibilité). Celles-ci se trouvent dans la classe Exemplaire.
 */
public class Livre {
    public String titre;
    public String auteur;
    public String editeur;
    public int annee;
    public int nbPages;
}
