package application.models;

public class Plateau {

	private final static int longueurPlateau = 4;
	private final static int hauteurPlateau = 4;

	private Case cochon1 = null;
	private Case cochon2 = null;
	private Case cochon3 = null;
	private Case[][] plateau;

	private Contexte contexte;
	private int nbPiecePlacee = 0;
	private boolean estGagne;

	public Plateau(Contexte contexte, Case loup, Case cochon1, Case cochon2, Case cochon3) {
		plateau = new Case[hauteurPlateau][longueurPlateau];
		for (int i = 0; i < hauteurPlateau; i++) {
			for (int j = 0; j < longueurPlateau; j++) {
				plateau[i][j] = new Case(i, j, EtatCase.VIDE, null);
			}
		}
		getCase(0, 0).setEtat(EtatCase.NULLE);
		getCase(3, 0).setEtat(EtatCase.NULLE);
		getCase(0, 3).setEtat(EtatCase.NULLE);
		this.contexte = contexte;
		if (loup != null) {
			getCase(loup.getLigne(), loup.getColonne()).setEtat(loup.getEtat());
		}
		getCase(cochon1.getLigne(), cochon1.getColonne()).setEtat(cochon1.getEtat());
		this.cochon1 = getCase(cochon1.getLigne(), cochon1.getColonne());
		if (cochon2 != null) {
			getCase(cochon2.getLigne(), cochon2.getColonne()).setEtat(cochon2.getEtat());
			this.cochon2 = getCase(cochon2.getLigne(), cochon2.getColonne());
		}
		if (cochon3 != null) {
			getCase(cochon3.getLigne(), cochon3.getColonne()).setEtat(cochon3.getEtat());
			this.cochon3 = getCase(cochon3.getLigne(), cochon3.getColonne());
		}

		estGagne = false;

	}

	/*
	 * Place la pièce en vérifiant si elle est plaçable grâce à la méthode
	 * isPlacable. Change l'état des cases concernés sur le plateau.
	 */
	public void placerPiece(Piece piece, Case positionPlacable) {

		if (isPlacable(piece, positionPlacable)) {

			int numLigne = positionPlacable.getLigne();
			int numColonne = positionPlacable.getColonne();

			piece.setCaseReference(numLigne, numColonne);

			for (int i = 0; i < piece.getHauteur(); i++) {
				for (int j = 0; j < piece.getLongueur(); j++) {

					if (piece.getCase(i, j).getEtat() != EtatCase.NULLE) {

						getCase(numLigne + i, numColonne + j).setCase(piece.getCase(i, j));

						// Si la case a bien été placée
						if (getCase(numLigne + i, numColonne + j).getEtat() != EtatCase.VIDE) {

							getCase(numLigne + i, numColonne + j).setProprietaire(piece);
						}
					}
				}
			}

			piece.setNumCaseColonne(numColonne);
			piece.setNumCaseLigne(numLigne);
			piece.setPlacee(true);
			nbPiecePlacee++;
		}
	}

	public void retirerPiece(Case caseClique) {
		Piece piece = caseClique.getProprietaire();
		if (piece == null) {
			return;
		}

		Case caseReference = piece.getCaseReference();
		int numLigne = caseReference.getLigne();
		int numColonne = caseReference.getColonne();

		for (int i = 0; i < piece.getHauteur(); i++) {
			for (int j = 0; j < piece.getLongueur(); j++) {
				if (getCase(numLigne + i, numColonne + j).getProprietaire() == piece) {
					if (getCase(numLigne + i, numColonne + j).getEtat() != EtatCase.NULLE) {
						getCase(numLigne + i, numColonne + j).setEtat(EtatCase.VIDE);
						if (memePosition(cochon1, getCase(numLigne + i, numColonne + j))) {
							getCase(numLigne + i, numColonne + j).setEtat(EtatCase.COCHON);
						}
						if (cochon2 != null && memePosition(cochon2, getCase(numLigne + i, numColonne + j))) {
							getCase(numLigne + i, numColonne + j).setEtat(EtatCase.COCHON);
						}
						if (cochon3 != null && memePosition(cochon3, getCase(numLigne + i, numColonne + j))) {
							getCase(numLigne + i, numColonne + j).setEtat(EtatCase.COCHON);
						}
					}
				}
			}
		}

		piece.setNumCaseColonne(-1);
		piece.setNumCaseLigne(-1);

		piece.setPlacee(false);
		nbPiecePlacee--;
	}

	public void afficherPlateau() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {

				switch (getCase(i, j).getEtat()) {
				case NULLE:
					System.out.print("-");
					break;
				case VIDE:
					System.out.print("0");
					break;
				case HERBE:
					System.out.print("H");
					break;
				case MAISON:
					System.out.print("M");
					break;
				case COCHON:
					System.out.print("C");
					break;
				case LOUP:
					System.out.print("L");
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
	 *
	 * @param piece
	 *            La pièce à placer sur le plateau.
	 * @param La
	 *            case de référence (0,0) où doit être placée la pièce.
	 * @param contexte
	 *            Le contexte du défi. Peut être "Diurne" ou "Nocturne".
	 * @return Retourne <b>true</b> si la pièce est plaçable sur le plateau et
	 *         <b>flase</b> si elle ne l'est pas.
	 */

	public boolean isPlacable(Piece piece, Case positionPlateau) {

		if (positionPlateau == null) {
			return false;
		}

		/* Test avec les longeurs de la piece */
		int numColonne = positionPlateau.getColonne();
		int numLigne = positionPlateau.getLigne();

		int longueurPiece = piece.getLongueur();
		int hauteurPiece = piece.getHauteur();

		// Si la pièce sort du plateau
		if (numColonne + longueurPiece > longueurPlateau || numLigne + hauteurPiece > hauteurPlateau
				|| numColonne + longueurPiece < 0 || numLigne + hauteurPiece < 0) {
			return false;
		}

		// Pour chaque case du plateau, on la compare avec la case de la pièce
		for (int i = 0; i < hauteurPiece; i++) {
			for (int j = 0; j < longueurPiece; j++) {

				EtatCase etatPlateau = getCase(numLigne + i, numColonne + j).getEtat();
				EtatCase etatPiece = piece.getCase(i, j).getEtat();

				// Si la case du plateau ne peut contenir de maison, on retoure
				// false
				if (etatPlateau == EtatCase.NULLE || etatPlateau == EtatCase.MAISON || etatPlateau == EtatCase.HERBE
						|| etatPlateau == EtatCase.LOUP) {

					// On retourne false si ce n'est pas une case nulle
					if (etatPiece != EtatCase.NULLE) {
						return false;
					}

				}

				// Si la case est un cochon
				if (etatPlateau == EtatCase.COCHON) {

					// Si le contexte est diurne, on ne peut rien mettre sur les
					// cochons
					if (contexte == Contexte.DIURNE) {
						if (etatPiece != EtatCase.NULLE) {
							return false;
						}
					} else {
						// Sinon, si ce n'est pas une maison, alors on peut pas
						// placer
						if (etatPiece != EtatCase.MAISON && etatPiece != EtatCase.NULLE) {
							return false;
						}
					}
				}
			}
		}

		// Sinon la maison est plaçable
		return true;
	}

	/**
	 * Vérifie si la partie est gagnée.
	 *
	 * En mode diurne, si les trois pièces sont placées c'est gagné. En mode
	 * nocturne, il faut que les cases <b>cochon1</b>, <b>cochon2</b> et
	 * <b>cochon3</b> originellement à l'état de cochon soient à l'état de
	 * MAISON.
	 *
	 * @return Retourne <b>true</b> si la partie est gagnée et <b>false</b> si
	 *         elle ne l'est pas.
	 */
	public boolean isGagner() {

		if (nbPiecePlacee != 3) {
			return false;
		}

		if (getContexte() == Contexte.NOCTURNE) {

			if (cochon2 == null && cochon3 == null) {
				if (cochon1.getEtat() != EtatCase.MAISON) {
					return false;
				}
			} else if (cochon3 == null) {
				if (cochon1.getEtat() != EtatCase.MAISON || cochon2.getEtat() != EtatCase.MAISON) {
					return false;
				}
			} else if (cochon1.getEtat() != EtatCase.MAISON || cochon2.getEtat() != EtatCase.MAISON
					|| cochon3.getEtat() != EtatCase.MAISON) {
				return false;
			}
		}

		estGagne = true;
		return true;

	}

	public Case getCase(int numLigne, int numColonne) {
		if (numLigne >= hauteurPlateau || numColonne >= longueurPlateau || numLigne < 0 || numColonne < 0) {
			return null;
		}
		return plateau[numLigne][numColonne];
	}

	public boolean memePosition(Case a, Case b) {
		if (a.getLigne() != b.getLigne()) {
			return false;
		}
		if (a.getColonne() != b.getColonne()) {
			return false;
		}
		return true;
	}

	public void setContexte(Contexte contexte) {
		this.contexte = contexte;
	}

	public Contexte getContexte() {
		return contexte;
	}

}
