package application.models;

public class Case {
	private EtatCase etat;
	private int numLigne;
	private int numColonne;

	private Piece proprietaire;

	/**
	 *
	 * @param numLigne     Numéro de la ligne par rapport au propriétaire.
	 * @param numColonne   Numéro de colonne par rapport au propriétaire.
	 * @param etat         L'état de la case.
	 * @param proprietaire Le propriétaire, de type Pièce.
	 */
	public Case(int numLigne, int numColonne, EtatCase etat, Piece proprietaire) {
		this.etat = etat;
		this.numLigne = numLigne;
		this.numColonne = numColonne;
		this.proprietaire = proprietaire;
	}

	/**
	 * Permet de modifier l'état de la case actuelle.
	 *
	 * @param etat L'état de la case.
	 */
	public void setEtat(EtatCase etat) {
		this.etat = etat;
	}

	/**
	 * Permet de récupérer l'état de la case actuelle.
	 *
	 * @return Retourne l'état de la case actuelle.
	 */
	public EtatCase getEtat() {
		return etat;
	}

	/**
	 * Permet de récupérer le numéro de la ligne dans laquelle la pièce se situe.
	 *
	 * @return Retourne le numéro de la ligne dans laquelle la case est placée. (0
	 *         est la première ligne)
	 */
	public int getLigne() {
		return numLigne;
	}

	/**
	 * Permet de récupérer le numéro de la colonne dans laquelle la pièce se situe.
	 *
	 * @return Retourne le numéro de la colonne dans laquelle la case est placée. (0
	 *         est la première colonne)
	 */
	public int getColonne() {
		return numColonne;
	}

	/**
	 *
	 * @return Retourne le propriétaire de la case. Retourne une pièce ou
	 *         <b>null</b> si le propriétaire n'est pas une pièce.
	 */
	public Piece getProprietaire() {
		return proprietaire;
	}

	public void setProprietaire(Piece proprietaire) {
		this.proprietaire = proprietaire;
	}

	public void setCase(Case c) {
		this.setEtat(c.getEtat());
		this.setProprietaire(c.getProprietaire());
	}

	public void setColonne(int numColonne) {
		this.numColonne = numColonne;
	}

	public void setLigne(int numLigne) {
		this.numLigne = numLigne;
	}

}
