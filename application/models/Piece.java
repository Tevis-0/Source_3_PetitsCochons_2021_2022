package application.models;

public class Piece {

	private Case caseReference;
	private Case[][] piece;

	private int hauteur;
	private int longueur;

	private int numCaseLigne;
	private int numCaseColonne;

	private boolean estPlacee;
	private boolean estFocus;
	private boolean estSelectionnee;
	private boolean estVertical;

	private double defautRotation;

	/**
	 * @param type
	 *            Le type de maison a créée.
	 */
	public Piece(TypeMaison type, double defautRotation) {
		switch (type) {

		// Maison de paille
		case PAILLE:
			creerPiece(2, 2);
			getCase(0, 0).setEtat(EtatCase.MAISON);
			getCase(1, 1).setEtat(EtatCase.NULLE);
			break;
		case BOIS:
			creerPiece(3, 1);
			getCase(1, 0).setEtat(EtatCase.MAISON);
			break;
		case BRIQUE:
			creerPiece(3, 2);
			getCase(2, 0).setEtat(EtatCase.MAISON);
			getCase(0, 1).setEtat(EtatCase.NULLE);
			getCase(1, 1).setEtat(EtatCase.NULLE);
			break;
		}
		this.caseReference = getCase(0, 0);
		this.defautRotation = defautRotation;
		this.estVertical = true;
		this.numCaseLigne = -1;
		this.numCaseColonne = -1;
	}

	/**
	 * Créé une pièce remplie d'herbe.
	 *
	 * @param hauteur
	 *            La hauteur de la pièce, en nombre de cases.
	 * @param longueur
	 *            La longueur de la pièce, en nombre de cases.
	 */
	public void creerPiece(int hauteur, int longueur) {
		piece = new Case[hauteur][longueur];
		this.hauteur = hauteur;
		this.longueur = longueur;

		for (int i = 0; i < hauteur; i++) {
			for (int j = 0; j < longueur; j++) {
				piece[i][j] = new Case(i, j, EtatCase.HERBE, this);
			}
		}
	}

	public void afficherPiece() {

		for (int i = 0; i < hauteur; i++) {
			for (int j = 0; j < longueur; j++) {
				switch (getCase(i, j).getEtat()) {
				case NULLE:
					System.out.print("-");
					break;
				case HERBE:
					System.out.print("H");
					break;
				case MAISON:
					System.out.print("M");
					break;
				default:
					break;
				}
				System.out.print(" ");
			}
			System.out.println("");
		}
	}

	/**
	 * Tourne la pièce dans le sens inverse des aiguilles d'une montre.
	 */
	public void tournerAntiHoraire() {

		Case[][] temp = new Case[longueur][hauteur];

		// On transpose toutes les anciennes valeurs dans le nouveau tableau
		for (int i = 0; i < hauteur; i++) {
			for (int j = 0; j < longueur; j++) {
				temp[longueur - 1 - j][i] = getCase(i, j);
			}
		}

		// Changements des largeurs et hauteurs
		int temp2 = hauteur;
		hauteur = longueur;
		longueur = temp2;

		// Mise à jour de la pièce
		piece = temp;
	}

	/**
	 * Tourne la pièce dans le sens des aiguilles d'une montre.
	 */
	public void tournerHoraire() {
		tournerAntiHoraire();
		tournerAntiHoraire();
		tournerAntiHoraire();
	}

	/**
	 * Permet de récupérer une case spécifique de la pièce.
	 *
	 * @param numLigne
	 *            La coordonnée y de la case par rapport à la pièce. (0 est le
	 *            numéro de la première ligne).
	 * @param numColonne
	 *            La coordonnée x de la case par rapport à la pièce. (0 est le
	 *            numéro de la première colonne).
	 *
	 * @return Retourne l'instance de la case ou <b>null</b> si les coordonnées
	 *         ne correspondent à aucune case.
	 */
	public Case getCase(int numLigne, int numColonne) {
		if (numLigne >= this.hauteur || numColonne >= this.longueur || numLigne < 0 || numColonne < 0) {
			return null;
		}
		return piece[numLigne][numColonne];
	}

	public int getNumCaseLigne() {
		return numCaseLigne;
	}

	public int getNumCaseColonne() {
		return numCaseColonne;
	}

	public void setNumCaseLigne(int numCaseLigne) {
		this.numCaseLigne = numCaseLigne;
	}

	public void setNumCaseColonne(int numCaseColonne) {
		this.numCaseColonne = numCaseColonne;
	}

	/**
	 * Permet de récupérer la longueur de la pièce actuelle.
	 *
	 * @return La longueur de la pièce, en nombre de cases.
	 */
	public int getLongueur() {
		return longueur;
	}

	/**
	 * Permet de récupérer la hauteur de la pièce actuelle.
	 *
	 * @return La hauteur de la pièce, en nombre de cases.
	 */
	public int getHauteur() {
		return hauteur;
	}

	/**
	 * Permet de savoir si la pièce est placée sur un plateau.
	 *
	 * @return Retourne <b>true</b> si la pièce est placée sur un plateau ou
	 *         <b>false</b> si elle ne l'est pas.
	 */
	public boolean isPlacee() {
		return estPlacee;
	}

	/**
	 * Permet de modifier la valeur <b>estPlacee</b> de la pièce.
	 *
	 * @param estPlacee
	 *            Le boolean qui indique si la pièce est placée sur un plateau.
	 */
	public void setPlacee(boolean estPlacee) {
		this.estPlacee = estPlacee;
	}

	/**
	 * Permet de savoir si la pièce est sélectionnée.
	 *
	 * @return Retourne <b>true</b> si la pièce est sélectionnée par le joueur
	 *         ou <b>false</b> si elle ne l'est pas.
	 */
	public boolean isSelectionnee() {
		return estSelectionnee;
	}

	/**
	 * Permet de modifier la sélection de la pièce.
	 *
	 * @param estSelectionnee
	 *            Le boolean qui indique si la pièce est sélectionnée par le
	 *            joueur.
	 */
	public void setSelectionnee(boolean estSelectionnee) {
		this.estSelectionnee = estSelectionnee;
	}

	public boolean isFocus() {
		return estFocus;
	}

	public void setFocus(boolean estFocus) {
		this.estFocus = estFocus;
	}

	public double getDefautRotation() {
		return defautRotation;
	}

	public void setCaseReference(int numLigne, int numColonne) {
		caseReference.setColonne(numColonne);
		caseReference.setLigne(numLigne);
	}

	public Case getCaseReference() {
		return caseReference;
	}

	public boolean isVertical() {
		return estVertical;
	}

	public void setVertical(boolean estVertical) {
		this.estVertical = estVertical;
	}
}
