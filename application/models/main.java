package application.models;

public class main {

	public static void main(String[] args) {
		Case cochon1 = new Case(0, 2, EtatCase.COCHON, null);
		Case cochon2 = new Case(2, 3, EtatCase.COCHON, null);
		Case cochon3 = new Case(3, 1, EtatCase.COCHON, null);
		Case loup = new Case(1, 0, EtatCase.LOUP, null);
		Plateau plateau = new Plateau(Contexte.NOCTURNE, loup, cochon1, cochon2, null);
		Piece brique = new Piece(TypeMaison.BRIQUE, 90);
		Piece paille = new Piece(TypeMaison.PAILLE, 90);
		Piece bois = new Piece(TypeMaison.BOIS, 90);
		System.out.println("Pièce :");
		brique.tournerAntiHoraire();
		brique.afficherPiece();
		System.out.println("");
		bois.tournerHoraire();
		bois.afficherPiece();
		System.out.println("");
		paille.tournerHoraire();
		paille.afficherPiece();

		System.out.println("====================");

		plateau.afficherPlateau();

		System.out.println("====================");
		System.out.println("placer");
		plateau.placerPiece(paille, plateau.getCase(0, 1));
		plateau.placerPiece(brique, plateau.getCase(1, 1));
		plateau.afficherPlateau();

		System.out.println("====================");
		System.out.println("retirer");
		plateau.retirerPiece(plateau.getCase(2, 2));
		// plateau.placerPiece(brique, plateau.getCase(1, 1));

		if (plateau.isGagner())
			System.out.println("Tu es le meilleur tu as gagner cette partie!");
		;
		plateau.afficherPlateau();

	}

}
