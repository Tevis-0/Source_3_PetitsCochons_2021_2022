package application.models;

public class Case {
	private EtatCase etat;
	private int numLigne;
	private int numColonne;

	private Piece proprietaire;

	/**
	 *
	 * @param numLigne     Num�ro de la ligne par rapport au propri�taire.
	 * @param numColonne   Num�ro de colonne par rapport au propri�taire.
	 * @param etat         L'�tat de la case.
	 * @param proprietaire Le propri�taire, de type Pi�ce.
	 */
	public Case(int numLigne, int numColonne, EtatCase etat, Piece proprietaire) {
		this.etat = etat;
		this.numLigne = numLigne;
		this.numColonne = numColonne;
		this.proprietaire = proprietaire;
	}

	/**
	 * Permet de modifier l'�tat de la case actuelle.
	 *
	 * @param etat L'�tat de la case.
	 */
	public void setEtat(EtatCase etat) {
		this.etat = etat;
	}

	/**
	 * Permet de r�cup�rer l'�tat de la case actuelle.
	 *
	 * @return Retourne l'�tat de la case actuelle.
	 */
	public EtatCase getEtat() {
		return etat;
	}

	/**
	 * Permet de r�cup�rer le num�ro de la ligne dans laquelle la pi�ce se situe.
	 *
	 * @return Retourne le num�ro de la ligne dans laquelle la case est plac�e. (0
	 *         est la premi�re ligne)
	 */
	public int getLigne() {
		return numLigne;
	}

	/**
	 * Permet de r�cup�rer le num�ro de la colonne dans laquelle la pi�ce se situe.
	 *
	 * @return Retourne le num�ro de la colonne dans laquelle la case est plac�e. (0
	 *         est la premi�re colonne)
	 */
	public int getColonne() {
		return numColonne;
	}

	/**
	 *
	 * @return Retourne le propri�taire de la case. Retourne une pi�ce ou
	 *         <b>null</b> si le propri�taire n'est pas une pi�ce.
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
