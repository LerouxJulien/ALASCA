package fr.upmc.alasca.computer.enums;

/**
 * Statut de la VM
 * NEW  : VM venant d'etre deployee (Aucune requete n'est traitee.)
 * FREE : Libre (Au moins un fil d'execution est libre.)
 * BUSY : Occupe (Tous les fils d'execution sont occupes.)
 * IDLE : En attente d'eutanasie (S'il ne fout rien pendant X temps.)
 */
public enum Status {

	NEW, FREE, BUSY, IDLE
	
}
