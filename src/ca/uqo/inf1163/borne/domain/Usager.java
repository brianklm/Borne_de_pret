package ca.uqo.inf1163.borne.domain;

/**
 * Représente un usager de la bibliothèque.
 *
 * On utilise un "record" Java pour avoir automatiquement :
 * - des champs immuables
 * - un constructeur
 * - equals / hashCode / toString
 */
public record Usager(String compte, String nip, String nom) {}
