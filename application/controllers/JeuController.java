package application.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import application.main.LoaderFactory;
import application.models.Case;
import application.models.Contexte;
import application.models.Defi;
import application.models.EtatCase;
import application.models.Mode;
import application.models.Niveau;
import application.models.Piece;
import application.models.Plateau;
import application.models.Profil;
import application.models.Timer;
import application.models.TypeMaison;
import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.util.Duration;
import utils.Random;
import utils.ResourceLoader;
import utils.database.Database;
import utils.database.Table;

public class JeuController extends Controller {

	private CadrePauseController pauseController;
	private CadreMessageOuiNonController popupController;
	private Controller parent;

	@FXML
	private VBox background;

	@FXML
	private AnchorPane zoneGameplay;

	@FXML
	private AnchorPane zoneDragMaison;

	@FXML
	private ImageView maisonPailleIV;

	@FXML
	private ImageView maisonBoisIV;

	@FXML
	private ImageView maisonBriqueIV;

	@FXML
	private GridPane plateauGP;

	@FXML
	private ImageView annonceWin;

	@FXML
	private Label chrono;

	@FXML
	private Label contexte;

	@FXML
	private Label niveau;
	
	@FXML
	private AnchorPane indicationDefi;
	
	@FXML
	private Label nbDefiRestant;

	@FXML
	private ImageView pauseButton;

	@FXML
	private ImageView reprendreButton;

	@FXML
	private Label extraButton;

	@FXML
	private ImageView loading;

	private AnchorPane popup;
	private AnchorPane pauseCadre;
	private AnchorPane confirmerQuitter;

	// Modèles
	private Mode mode;
	private Defi defi;
	private Piece maisonPaille;
	private Piece maisonBois;
	private Piece maisonBrique;
	private Plateau plateau;
	private Timer timer;

	private List<Defi> listeDefis;

	private Piece[] pieces;
	private ImageView[] ivs;

	// Images
	private static Image pailleError;
	private static Image boisError;
	private static Image briqueError;
	private static Image pailleImg;
	private static Image boisImg;
	private static Image briqueImg;

	private static Image defiReussi;
	private static Image defiSuivant;
	private static Image defiFini;
	private static Image etoile;

	private static Image pauseImg;

	// Coordonnées
	private double xMouse;
	private double yMouse;

	private double oldxMouse = -1;
	private double oldyMouse = -1;

	private double xGameplay;
	private double yGameplay;

	private double xMaison;
	private double yMaison;

	private double minXplateau;
	private double minYplateau;
	private double maxXplateau;
	private double maxYplateau;

	private int numLigneMaison;
	private int numColonneMaison;

	// Contrôles
	private String left;
	private String right;
	private String up;
	private String down;
	private String alt;
	private String control;
	private String enter;
	private String back;
	private String scrollup;
	private String scrolldown;
	private String primary;
	private String secondary;

	// Autre
	private boolean withMouse;
	private boolean aimant = false;
	private boolean enPause = false;

	@FXML
	private void initialize() {

		background.setFocusTraversable(true);
		zoneGameplay.setFocusTraversable(true);
		loading.setVisible(false);
		// ********* INIT DU JEU *********//
		maisonPaille = new Piece(TypeMaison.PAILLE, 90);
		maisonBois = new Piece(TypeMaison.BOIS, 0);
		maisonBrique = new Piece(TypeMaison.BRIQUE, 90);

		pieces = new Piece[3];
		pieces[0] = maisonPaille;
		pieces[1] = maisonBois;
		pieces[2] = maisonBrique;

		ivs = new ImageView[3];
		ivs[0] = maisonPailleIV;
		ivs[1] = maisonBoisIV;
		ivs[2] = maisonBriqueIV;

		// ********* RESSOURCES IMAGES *********//
		pailleError = ResourceLoader.loadImg("maison_paille_error.png");
		boisError = ResourceLoader.loadImg("maison_bois_error.png");
		briqueError = ResourceLoader.loadImg("maison_brique_error.png");

		pailleImg = ResourceLoader.loadImg("maison_paille.png");
		boisImg = ResourceLoader.loadImg("maison_bois.png");
		briqueImg = ResourceLoader.loadImg("maison_brique.png");

		defiReussi = ResourceLoader.loadImg("defi_reussi.png");
		defiSuivant = ResourceLoader.loadImg("defi_suivant.png");
		defiFini = ResourceLoader.loadImg("fini.png");
		etoile = ResourceLoader.loadImg("etoile.png");

		pauseImg = ResourceLoader.loadImg("pause.png");

		// ********* AFFICHAGE *********//

		extraButton.getParent().setVisible(false);

		// CSS
		background.getStylesheets().add(ResourceLoader.loadCss("jeu.css"));
		extraButton.getParent().getStyleClass().add("clickableButton");

		// Message de victoire
		annonceWin = new ImageView(defiReussi);
		annonceWin.setVisible(false);
		annonceWin.setFitWidth(170);
		annonceWin.setFitHeight(48);

		// Pause
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/views/CadrePause.fxml"));
			pauseCadre = (AnchorPane) loader.load();
			pauseCadre.getStylesheets().add(ResourceLoader.loadCss("cadrePause.css"));
			pauseCadre.getStylesheets().add(ResourceLoader.loadCss("default.css"));

			pauseController = loader.getController();
			pauseController.setParent(this);

			FXMLLoader loader2 = new FXMLLoader();
			loader2.setLocation(getClass().getResource("/application/views/CadreMessageOuiNon.fxml"));
			confirmerQuitter = (AnchorPane) loader2.load();
			confirmerQuitter.getStylesheets().add(ResourceLoader.loadCss("cadrePause.css"));
			confirmerQuitter.getStylesheets().add(ResourceLoader.loadCss("default.css"));

			popupController = loader2.getController();
			popupController.setParent(this);

		} catch (IOException e) {
			e.printStackTrace();
		}

		popup = new AnchorPane();
		popup.setPrefHeight(400);
		popup.setPrefWidth(400);
		popup.setVisible(false);
		Bounds bounds = zoneGameplay.getBoundsInParent();
		zoneGameplay.getChildren().addAll(annonceWin, popup);

		// Contrôles
		String[] controles = getMain().getProfilSelect().getSettings();
		if (controles[0].equals("SOURIS")) {
			withMouse = true;
		} else {
			withMouse = false;
		}
		left = controles[3];
		right = controles[4];
		up = controles[5];
		down = controles[6];
		alt = controles[7];
		control = controles[8];
		enter = controles[9];
		back = controles[10];
		scrollup = controles[11];
		scrolldown = controles[12];
		primary = controles[13];
		secondary = controles[14];

		// Polices
		Font hogFish;
		hogFish = ResourceLoader.loadFont("Hogfish.ttf", 50);
		contexte.setFont(hogFish);
		niveau.setFont(hogFish);
		extraButton.setFont(ResourceLoader.loadFont("Grobold.ttf", 30));

		// Timer
		timer = new Timer(this);
		Thread thread = new Thread(timer);
		thread.start();

	}

	@FXML
	public void clickPlateau(MouseEvent event) {

		if (!withMouse) {
			return;
		}

		if (event.getButton().toString().equals("PRIMARY")) {

			double tailleCase = (maxXplateau - minXplateau) / 4;

			// Si le curseur est sur le plateau

			int numColonne = (int) ((xGameplay - minXplateau) / tailleCase);
			int numLigne = (int) ((yGameplay - minYplateau) / tailleCase);

			Piece piece = null;
			ImageView iv = null;

			if (maisonPaille.isSelectionnee()) {
				piece = maisonPaille;
				iv = maisonPailleIV;
			} else if (maisonBois.isSelectionnee()) {
				piece = maisonBois;
				iv = maisonBoisIV;
			} else if (maisonBrique.isSelectionnee()) {
				piece = maisonBrique;
				iv = maisonBriqueIV;
			}

			if (piece != null) {
				// Pièce pas placée
				if (!piece.isPlacee()) {
					plateau.placerPiece(piece, plateau.getCase(numLigne, numColonne));
					if (piece.isPlacee()) {
						piece.setFocus(false);
						piece.setSelectionnee(false);
						aimant = false;

						double aimantX = xGameplay - (minXplateau + tailleCase * numColonne + (tailleCase / 2));
						double aimantY = yGameplay - (minYplateau + tailleCase * numLigne + (tailleCase / 2));

						iv.setTranslateX(iv.getTranslateX() - aimantX);
						iv.setTranslateY(iv.getTranslateY() - aimantY);

						iv.setCursor(Cursor.OPEN_HAND);
						iv.setOpacity(1);
						iv.setEffect(null);
						iv.toBack();

						// Vérification win
						if (plateau.isGagner()) {
							afficherGagner();
						}
					}
				}

				// Si on enlève la pièce du plateau
				else {
					piece.setFocus(true);
					piece.setSelectionnee(true);
					piece.setPlacee(false);

				}
			}
		}
	}

	@FXML
	public void cursorMoveGameplay(MouseEvent event) {

		if (!withMouse) {
			return;
		}

		double x = event.getX();
		double y = event.getY();

		xGameplay = x;
		yGameplay = y;

		Bounds b = plateauGP.getBoundsInParent();
		minXplateau = b.getMinX() + 20;
		minYplateau = b.getMinY() + 20;
		maxXplateau = b.getMaxX() - 20;
		maxYplateau = b.getMaxY() - 20;

		double tailleCase = (maxXplateau - minXplateau) / 4;

		// Si le curseur est sur le plateau

		int numColonne = (int) ((x - minXplateau) / tailleCase);
		int numLigne = (int) ((y - minYplateau) / tailleCase);

		// Vérification du placement de la pièce
		verificationPlacementPiece(numColonne, numLigne);
	}

	@FXML
	public void focusMaison(MouseEvent event) {

		if (!withMouse) {
			return;
		}

		ImageView target = (ImageView) event.getTarget();
		if (!enPause) {
			if (target == maisonBriqueIV) {
				double x = event.getX();
				double y = event.getY();

				if (!maisonBrique.isSelectionnee()) {
					if (!(y < 134 && x < 234)) {
						applyEffectToFocusMaison(true, maisonBrique, maisonBriqueIV);
					} else {
						applyEffectToFocusMaison(false, maisonBrique, maisonBriqueIV);
					}
				}

			} else if (target == maisonBoisIV) {

				if (!maisonBois.isSelectionnee()) {
					applyEffectToFocusMaison(true, maisonBois, maisonBoisIV);
				}
			} else if (target == maisonPailleIV) {

				double x = event.getX();
				double y = event.getY();
				if (!maisonPaille.isSelectionnee()) {
					if (!(y < 115 && x > 115)) {
						applyEffectToFocusMaison(true, maisonPaille, maisonPailleIV);
					} else {
						applyEffectToFocusMaison(false, maisonPaille, maisonPailleIV);
					}
				}
			}

		} else {
			maisonPailleIV.setCursor(Cursor.DEFAULT);
			maisonBoisIV.setCursor(Cursor.DEFAULT);
			maisonBriqueIV.setCursor(Cursor.DEFAULT);
		}
	}

	@FXML
	public void exitFocusMaion(MouseEvent event) {

		if (!withMouse) {
			return;
		}

		ImageView iv = (ImageView) event.getTarget();
		Piece piece = null;

		if (iv == maisonPailleIV) {
			piece = maisonPaille;
		} else if (iv == maisonBoisIV) {
			piece = maisonBois;
		} else if (iv == maisonBriqueIV) {
			piece = maisonBrique;
		}

		if (!piece.isSelectionnee()) {
			applyEffectToFocusMaison(false, piece, iv);
		}

	}

	public void applyEffectToFocusMaison(boolean focus, Piece piece, ImageView iv) {
		if (focus) {
			piece.setFocus(true);
			if (!withMouse) {
				iv.setCursor(Cursor.OPEN_HAND);
			}
			iv.setOpacity(0.65);
			DropShadow shadow = new DropShadow();
			shadow.setColor(Color.web("#ff7700"));
			iv.setEffect(shadow);
		} else {
			piece.setFocus(false);

			iv.toBack();
			iv.setCursor(null);
			iv.setOpacity(1);
			iv.setEffect(null);
		}
	}

	@FXML
	public void aimantMaison(MouseEvent event) {

		if (!withMouse) {
			return;
		}

		ImageView target = (ImageView) event.getTarget();

		if (event.getButton().toString().equals("PRIMARY") && !enPause) {

			xMaison = event.getX();
			yMaison = event.getY();

			Bounds b = plateauGP.getBoundsInParent();
			minXplateau = b.getMinX() + 20;
			minYplateau = b.getMinY() + 20;
			maxXplateau = b.getMaxX() - 20;
			maxYplateau = b.getMaxY() - 20;

			double tailleCase = (maxXplateau - minXplateau) / 4;

			// Aimant Paille
			if (target == maisonPailleIV) {

				// Si la pièce est a son emplacement origine
				if (maisonPaille.isFocus() && !maisonPaille.isSelectionnee() && !maisonPaille.isPlacee()) {
					if (aimant == false) {
						maisonPailleIV.setTranslateY(maisonPailleIV.getTranslateY() + xMaison - 59.5);
						maisonPailleIV.setTranslateX(maisonPailleIV.getTranslateX() + 178 - yMaison);
					}
					aimant = true;
					selectionnerMaisonPaille();
				}
				// Si la pièce est sur le plateau
				else if (maisonPaille.isFocus() && maisonPaille.isPlacee()) {
					// Case 0,0
					Case caseRef = maisonPaille.getCaseReference();
					int numLigne = caseRef.getLigne();
					int numColonne = caseRef.getColonne();

					// Aimantation
					double ecartX = xGameplay - (tailleCase * numColonne + minXplateau);
					double ecartY = yGameplay - (tailleCase * numLigne + minYplateau);

					maisonPailleIV.setTranslateX(maisonPailleIV.getTranslateX() + ecartX - (tailleCase / 2));
					maisonPailleIV.setTranslateY(maisonPailleIV.getTranslateY() + ecartY - (tailleCase / 2));

					retirerPiecePlateau(maisonPaille, (int) ((yGameplay - minYplateau) / tailleCase),
							(int) ((xGameplay - minXplateau) / tailleCase));

					selectionnerMaisonPaille();
					verificationPlacementPiece(numColonne, numLigne);
				}

			}
			// Aimant Bois
			else if (target == maisonBoisIV) {

				if (maisonBois.isFocus() && !maisonBois.isSelectionnee() && !maisonBois.isPlacee()) {

					if (aimant == false) {
						maisonBoisIV.setTranslateY(maisonBoisIV.getTranslateY() + yMaison - 59.5);
						maisonBoisIV.setTranslateX(maisonBoisIV.getTranslateX() + xMaison - 59.5);
					}
					aimant = true;
					selectionnerMaisonBois();
				} else if (maisonBois.isFocus() && maisonBois.isPlacee()) {
					// Case 0,0
					Case caseRef = maisonBois.getCaseReference();
					int numLigne = caseRef.getLigne();
					int numColonne = caseRef.getColonne();

					// Aimantation
					double ecartX = xGameplay - (tailleCase * numColonne + minXplateau);
					double ecartY = yGameplay - (tailleCase * numLigne + minYplateau);

					maisonBoisIV.setTranslateX(maisonBoisIV.getTranslateX() + ecartX - (tailleCase / 2));
					maisonBoisIV.setTranslateY(maisonBoisIV.getTranslateY() + ecartY - (tailleCase / 2));

					retirerPiecePlateau(maisonBois, numLigne, numColonne);

					selectionnerMaisonBois();
					verificationPlacementPiece(numColonne, numLigne);

				}
			}
			// Aimant Brique
			else if (target == maisonBriqueIV) {

				if (maisonBrique.isFocus() && !maisonBrique.isSelectionnee() && !maisonBrique.isPlacee()) {

					if (aimant == false) {
						maisonBriqueIV.setTranslateX(maisonBriqueIV.getTranslateX() + 180 - yMaison);
						maisonBriqueIV.setTranslateY(maisonBriqueIV.getTranslateY() + xMaison - 54);

					}
					aimant = true;
					selectionnerMaisonBrique();

				} else if (maisonBrique.isFocus() && maisonBrique.isPlacee()) {
					// Case 0,0
					Case caseRef = maisonBrique.getCaseReference();
					int numLigne = caseRef.getLigne();
					int numColonne = caseRef.getColonne();

					// Aimantation
					double ecartX = xGameplay - (tailleCase * numColonne + minXplateau);
					double ecartY = yGameplay - (tailleCase * numLigne + minYplateau);

					maisonBriqueIV.setTranslateX(maisonBriqueIV.getTranslateX() + ecartX - (tailleCase / 2));
					maisonBriqueIV.setTranslateY(maisonBriqueIV.getTranslateY() + ecartY - (tailleCase / 2));

					retirerPiecePlateau(maisonBrique, (int) ((yGameplay - minYplateau) / tailleCase),
							(int) ((xGameplay - minXplateau) / tailleCase));

					selectionnerMaisonBrique();
					verificationPlacementPiece(numColonne, numLigne);
				}

			}
		}
	}

	/**
	 * Permet de faire en sorte que la pièce sélectionnée suive le curseur sur
	 * l'écran. Si le curseur sort de la fenêtre, la pièce ne bouge plus tant que le
	 * curseur n'est pas revenu.
	 */
	@FXML
	public void bougerSouris(MouseEvent event) {

		if (!withMouse) {
			return;
		}

		if (oldxMouse == -1) {
			oldxMouse = event.getSceneX();
			oldyMouse = event.getSceneY();
			xMouse = event.getSceneX();
			yMouse = event.getSceneY();

		} else {

			oldxMouse = xMouse;
			oldyMouse = yMouse;
			xMouse = event.getSceneX();
			yMouse = event.getSceneY();

		}

		double moveX = xMouse - oldxMouse;
		double moveY = yMouse - oldyMouse;

		Piece piece = null;
		ImageView iv = null;

		if (maisonPaille.isSelectionnee()) {
			piece = maisonPaille;
			iv = maisonPailleIV;
		} else if (maisonBois.isSelectionnee()) {
			piece = maisonBois;
			iv = maisonBoisIV;
		} else if (maisonBrique.isSelectionnee()) {
			piece = maisonBrique;
			iv = maisonBriqueIV;
		}

		if (piece != null) {
			iv.setTranslateX(iv.getTranslateX() + moveX);
			iv.setTranslateY(iv.getTranslateY() + moveY);

		}
	}

	/**
	 * Permet d'afficher ou désafficher l'écran de pause lorsque l'utilisateur
	 * clique sur le bouton Pause.
	 *
	 * @param event L'évènement envoyé par la souris.
	 */
	public void clickPauseButton(MouseEvent event) {
		if (!enPause) {

			popup.setScaleX(1.3);
			popup.setScaleY(1.3);
			popup.setTranslateX((background.getWidth() - popup.getWidth()) / 2);
			popup.setTranslateY((zoneGameplay.getHeight() - background.getHeight() + background.getHeight() / 2
					- popup.getHeight() / 2));

			popup.getChildren().clear();
			popup.getChildren().add(pauseCadre);
			pauseButton.setVisible(false);
			popup.setVisible(true);

			BoxBlur effet = new BoxBlur();
			effet.setWidth(50);
			effet.setHeight(50);
			effet.setIterations(2);

			zoneDragMaison.setEffect(effet);
			plateauGP.setEffect(effet);

			if (mode != Mode.COMPETITION) {
				timer.pause();
			}

			enPause = true;
		}
	}

	public void clickReprendreButton() {
		popup.setVisible(false);
		pauseButton.setVisible(true);

		BoxBlur effet = new BoxBlur();
		effet.setWidth(0);
		effet.setHeight(0);
		effet.setIterations(2);
		maisonPailleIV.setEffect(effet);
		maisonBoisIV.setEffect(effet);
		maisonBriqueIV.setEffect(effet);
		zoneDragMaison.setEffect(effet);
		plateauGP.setEffect(effet);

		if (getNumMaisonSelectionnee() != -1) {
			int num = getNumMaisonSelectionnee();

			DropShadow shadow = new DropShadow();
			shadow.setOffsetX(5);
			shadow.setOffsetY(5);

			ivs[num].setEffect(shadow);
			ivs[num].toFront();
		} else if (getNumMaisonFocus() != -1) {
			int num = getNumMaisonFocus();

			DropShadow shadow = new DropShadow();
			shadow.setColor(Color.web("#ff7700"));

			ivs[num].setEffect(shadow);
			ivs[num].toFront();
		}

		timer.reprendre();

		enPause = false;
	}

	public void clickQuitterButton() {

		popup.getChildren().clear();
		popup.getChildren().add(confirmerQuitter);
		if (mode == Mode.COMPETITION) {
			popupController.changerMessage("Êtes-vous sûr de vouloir quitter ? Aucun score ne sera enregistré.");
		} else {
			popupController.changerMessage("Êtes-vous sûr de vouloir quitter ? La solution sera affichée.");
		}

	}

	public void clickNonQuitterButton() {
		popup.getChildren().clear();
		popup.getChildren().add(pauseCadre);
	}

	public void clickOuiQuitterButton() {

		if (mode == Mode.COMPETITION) {
			timer.stopper();

			RotateTransition anim = getAnimation();
			anim.setNode(loading);
			loading.setVisible(true);
			anim.play();

			getMain().afficherMenuContexteNiveau(Mode.COMPETITION);

		} else {
			popup.setVisible(false);

			maisonPailleIV.setEffect(null);
			maisonBoisIV.setEffect(null);
			maisonBriqueIV.setEffect(null);
			zoneDragMaison.setEffect(null);
			plateauGP.setEffect(null);

			withMouse = false;

			for (int i = 0; i < 3; i++) {
				pieces[i].setSelectionnee(false);
				ivs[i].setOpacity(1);
				ivs[i].setEffect(null);
			}

			maisonPailleIV.setImage(pailleImg);
			maisonBoisIV.setImage(boisImg);
			maisonBriqueIV.setImage(briqueImg);

			timer.stopper();
			afficherSolution();
		}

	}

	@FXML
	public void clickExtraButton() {
		if (mode == Mode.COMPETITION) {
			Defi d = listeDefis.get(0);
			listeDefis.remove(listeDefis.get(0));
			listeDefis.add(d);
			defi = listeDefis.get(0);
			extraButton.getParent().setDisable(true);
			extraButton.getParent().setOpacity(0.5);
			initialiserPlateau(Mode.COMPETITION, defi.getContexte(), defi.getNiveau(), defi.getId());
			// On clean toutes les pièces
			for (int i = 0; i < 3; i++) {
				annulerPiece(pieces[i], ivs[i]);
				ivs[i].setEffect(null);
				pieces[i].setPlacee(false);
				pieces[i].setSelectionnee(false);
				pieces[i].setFocus(false);

			}
		} else {
			RotateTransition animation = getAnimation();
			animation.setNode(loading);
			animation.play();
			loading.setVisible(true);
			if (parent instanceof SelectNiveauController) {
				getMain().afficherMenuContexteNiveau(Mode.ENTRAINEMENT);
			} else if (parent instanceof TableauProgressionController) {
				getMain().afficherTableauProgression(defi.getContexte());
			}
		}
	}

	/**
	 * Permet de retirer une pièce du plateau.
	 *
	 * @param piece      La pièce à retirer du plateau.
	 * @param numLigne   Le numéro de la ligne de la case sur laquelle la maison a
	 *                   été cliquée.
	 * @param numColonne Le numéro de la colonne de la case sur laquelle la maison a
	 *                   été cliquée.
	 */
	public void retirerPiecePlateau(Piece piece, int numLigne, int numColonne) {

		plateau.retirerPiece(plateau.getCase(numLigne, numColonne));
		verificationPlacementPiece(numColonne, numLigne);
	}

	/**
	 * Peremt de changer la couleur de l'image faisant référence à la pièce
	 * sélectionnée en fonction de si celle-ci peut être placée sur le plateau ou
	 * non.
	 *
	 * @param numColonne Le numéro de colonne correspondant.
	 * @param numLigne   Le numéro de ligne correspondant.
	 */
	public void verificationPlacementPiece(int numColonne, int numLigne) {
		if (maisonPaille.isSelectionnee()) {

			if (plateau.isPlacable(maisonPaille, plateau.getCase(numLigne, numColonne))) {

				maisonPailleIV.setImage(pailleImg);
			} else {
				maisonPailleIV.setImage(pailleError);
			}

		} else if (maisonBois.isSelectionnee()) {
			if (plateau.isPlacable(maisonBois, plateau.getCase(numLigne, numColonne))) {
				maisonBoisIV.setImage(boisImg);
			} else {
				maisonBoisIV.setImage(boisError);
			}
		} else if (maisonBrique.isSelectionnee()) {
			if (plateau.isPlacable(maisonBrique, plateau.getCase(numLigne, numColonne))) {
				maisonBriqueIV.setImage(briqueImg);
			} else {
				maisonBriqueIV.setImage(briqueError);
			}
		}
	}

	/**
	 * Permet d'annuler la sélection d'une pièce en appelant la méthode
	 * annulerPiece. Seule la pièce actuellement sélectionnée sera repositionnée à
	 * son emplacement d'origine.
	 */
	@FXML
	public void cancelClick(MouseEvent event) {

		if (!withMouse) {
			return;
		}

		if (event.getButton().toString().equals("SECONDARY")) {
			if (maisonPaille.isSelectionnee()) {
				annulerPiece(maisonPaille, maisonPailleIV);
			} else if (maisonBois.isSelectionnee()) {
				annulerPiece(maisonBois, maisonBoisIV);
			} else if (maisonBrique.isSelectionnee()) {
				annulerPiece(maisonBrique, maisonBriqueIV);
			}
		}

	}

	/**
	 * Permet de tourner une pièce selon une rotation.
	 *
	 * @param newRotate La rotation a effectuée, en degré. 360° correspond à une
	 *                  rotation complète.
	 */
	@FXML
	public void scroll(ScrollEvent event) {

		if (!withMouse) {
			return;
		}

		double newRotate = event.getDeltaY();

		Piece piece = null;
		ImageView iv = null;

		if (maisonPaille.isSelectionnee()) {
			piece = maisonPaille;
			iv = maisonPailleIV;
		} else if (maisonBois.isSelectionnee()) {
			piece = maisonBois;
			iv = maisonBoisIV;
		} else if (maisonBrique.isSelectionnee()) {
			piece = maisonBrique;
			iv = maisonBriqueIV;
		}

		if (piece != null) {
			double rotate = iv.getRotate();
			if (newRotate < 0) {
				piece.tournerHoraire();
				iv.setRotate((rotate + 90) % 360);

			} else if (newRotate > 0) {
				piece.tournerAntiHoraire();
				iv.setRotate((rotate + 270) % 360);
			}
			aimantScroll(iv);

			double tailleCase = (maxXplateau - minXplateau) / 4;
			int numColonne = (int) ((xGameplay - minXplateau) / tailleCase);
			int numLigne = (int) ((yGameplay - minYplateau) / tailleCase);
			verificationPlacementPiece(numColonne, numLigne);
		}

	}

	/**
	 * Permet de réadapter l'image d'une pièce au curseur si celle-ci a été tournée.
	 *
	 * @param iv L'image correspondant à la pièce à aimanter.
	 */
	public void aimantScroll(ImageView iv) {

		if (iv == maisonPailleIV && maisonPaille.isSelectionnee()) {
			if (maisonPaille.isVertical()) {
				iv.setTranslateY(iv.getTranslateY() - 0);
				iv.setTranslateX(iv.getTranslateX() + 0);
			} else {
				iv.setTranslateY(iv.getTranslateY() + 0);
				iv.setTranslateX(iv.getTranslateX() - 0);
			}
			maisonPaille.setVertical(!maisonPaille.isVertical());
		} else if (iv == maisonBoisIV && maisonBois.isSelectionnee()) {
			if (maisonBois.isVertical()) {
				iv.setTranslateY(iv.getTranslateY() - 121);
				iv.setTranslateX(iv.getTranslateX() + 121);
			} else {
				iv.setTranslateY(iv.getTranslateY() + 121);
				iv.setTranslateX(iv.getTranslateX() - 121);

			}
			maisonBois.setVertical(!maisonBois.isVertical());
		} else if (iv == maisonBriqueIV && maisonBrique.isSelectionnee()) {
			if (maisonBrique.isVertical()) {
				iv.setTranslateY(iv.getTranslateY() - 64);
				iv.setTranslateX(iv.getTranslateX() + 64);
			} else {
				iv.setTranslateY(iv.getTranslateY() + 64);
				iv.setTranslateX(iv.getTranslateX() - 64);
			}
			maisonBrique.setVertical(!maisonBrique.isVertical());
		}

	}

	/**
	 * Permet d'annuler la sélection d'une pièce et de la replacer à son emplacement
	 * d'origine.
	 *
	 * @param piece La pièce en question.
	 * @param iv    L'image faisant référence à la pièce.
	 */

	public void annulerPiece(Piece piece, ImageView iv) {

		aimant = false;

		piece.setVertical(true);

		// Remise de la pièce dans sa position d'origine
		resetRotation(piece, iv);
		// Animations
		TranslateTransition transTransition = new TranslateTransition(Duration.millis(200), iv);
		transTransition.setToX(0);
		transTransition.setToY(0);

		RotateTransition rotateTransition = new RotateTransition(Duration.millis(200), iv);
		rotateTransition.setToAngle(piece.getDefautRotation() % 360);

		transTransition.play();
		rotateTransition.play();

		transTransition.setOnFinished(event -> {
			piece.setFocus(false);
			piece.setSelectionnee(false);
			piece.setPlacee(false);

			if (piece == maisonPaille) {
				iv.setImage(pailleImg);
			} else if (piece == maisonBois) {
				iv.setImage(boisImg);
			} else if (piece == maisonBrique) {
				iv.setImage(briqueImg);
			}

			if (withMouse) {
				iv.setCursor(Cursor.OPEN_HAND);
			}

			iv.setOpacity(1);
			iv.setEffect(null);
			iv.toBack();
		});
	}

	private void resetRotation(Piece piece, ImageView iv) {
		double rotation = iv.getRotate();
		double defaultRotation = piece.getDefautRotation();

		while (rotation != defaultRotation) {
			if (defaultRotation - rotation < 0) {
				piece.tournerAntiHoraire();
				rotation -= 90;
			} else if (defaultRotation - rotation > 0) {
				piece.tournerHoraire();
				rotation += 90;
			}
		}
	}

	public void selectionnerMaisonPaille() {
		if (!maisonBois.isSelectionnee() && !maisonBrique.isSelectionnee()) {
			selectionnerPiece(maisonPaille, maisonPailleIV);
		}

	}

	public void selectionnerMaisonBois() {
		if (!maisonPaille.isSelectionnee() && !maisonBrique.isSelectionnee()) {
			selectionnerPiece(maisonBois, maisonBoisIV);
		}
	}

	public void selectionnerMaisonBrique() {
		if (!maisonBois.isSelectionnee() && !maisonPaille.isSelectionnee()) {
			selectionnerPiece(maisonBrique, maisonBriqueIV);
		}

	}

	/**
	 * Permet de sélectionner une pièce ou de désélectionner une pièce. Si la pièce
	 * est déjà sélectionnée, celle-ci sera désélectionner et retournera à sa
	 * position d'origine. Si elle n'est pas sélectionnée, elle le sera alors.
	 *
	 * @param piece Pièce à sélectionner.
	 * @param iv    ImageView correspondant à la pièce à faire déplacer.
	 */
	public void selectionnerPiece(Piece piece, ImageView iv) {
		// Si la pièce est sélectionnée, on la dessélectionne
		if (!piece.isSelectionnee()) {
			// Sinon on la sélectionne
			piece.setSelectionnee(true);

			if (piece == maisonPaille) {
				iv.setImage(pailleError);
			} else if (piece == maisonBois) {
				iv.setImage(boisError);
			} else if (piece == maisonBrique) {
				iv.setImage(briqueError);
			}
			iv.setCursor(Cursor.CLOSED_HAND);
			iv.setOpacity(0.65);
			DropShadow shadow = new DropShadow();
			shadow.setOffsetX(5);
			shadow.setOffsetY(5);
			iv.setEffect(shadow);
			iv.toFront();
		}
	}

	/**
	 * Permet de mettre à jour le chrono.
	 *
	 * @param time La nouvelle durée à afficher, en seconde:centième de seconde.
	 */
	public void changerTime(Double time) {
		double timeArrondi = (double) Math.round(time * 10) / 10;

		Platform.runLater(() -> chrono.setText(Double.toString(timeArrondi)));
	}

	/**
	 * Permet d'afficher un message de confirmation que le défi a bien été réussi.
	 */
	public void afficherGagner() {

		enPause = true;

		// Defi reussi
		annonceWin.setTranslateX((background.getWidth() - annonceWin.getFitWidth()) / 2);
		annonceWin.setTranslateY((zoneGameplay.getHeight() / 2 - annonceWin.getFitHeight() / 2));

		// Image défi suivant/réussi/fini
		if (mode == Mode.COMPETITION) {
			if (listeDefis.size() > 1) {
				annonceWin.setImage(defiSuivant);
			} else {
				annonceWin.setImage(defiFini);
			}
		} else {
			annonceWin.setImage(defiReussi);
		}

		annonceWin.setVisible(true);
		pauseButton.setVisible(false);
		extraButton.getParent().setVisible(false);

		ScaleTransition transition = new ScaleTransition(Duration.millis(500), annonceWin);
		transition.setToX(5);
		transition.setToY(5);
		transition.play();

		// Etoiles
		afficherEtoile(Random.generer(200, 700), Random.generer(-300, -800));
		afficherEtoile(Random.generer(-600, -1000), Random.generer(-400, -800));
		afficherEtoile(Random.generer(-200, -800), Random.generer(400, 600));

		BoxBlur effet = new BoxBlur();
		effet.setWidth(10);
		effet.setHeight(10);
		effet.setIterations(2);
		maisonPailleIV.setEffect(effet);
		maisonBoisIV.setEffect(effet);
		maisonBriqueIV.setEffect(effet);
		zoneDragMaison.setEffect(effet);
		plateauGP.setEffect(effet);

		// Si c'est un mode compétition, on continue jusqu'au dernier défi
		if (mode == Mode.COMPETITION) {
			listeDefis.remove(0);
			int nbDefi = Integer.parseInt(nbDefiRestant.getText());
			nbDefi--;
			nbDefiRestant.setText(Integer.toString(nbDefi));
			
			if (!listeDefis.isEmpty()) {
				timer.pause();

				// On attend 3 secondes
				PauseTransition pause = new PauseTransition(Duration.millis(2000));
				pause.play();
				pause.setOnFinished(event -> {
					defi = listeDefis.get(0);
					initialiserPlateau(mode, defi.getContexte(), defi.getNiveau(), defi.getId());

					// On clean toutes les pièces
					for (int i = 0; i < 3; i++) {
						annulerPiece(pieces[i], ivs[i]);
						ivs[i].setEffect(null);
						pieces[i].setPlacee(false);
						pieces[i].setSelectionnee(false);
						pieces[i].setFocus(false);

					}
					zoneDragMaison.setEffect(null);
					plateauGP.setEffect(null);

					TranslateTransition anim = new TranslateTransition(Duration.millis(500), annonceWin);
					anim.setToX(-1000);
					anim.play();
					anim.setOnFinished(event2 -> {
						annonceWin.setVisible(false);
						annonceWin.setScaleX(1);
						annonceWin.setScaleY(1);

						pauseButton.setVisible(true);
						extraButton.getParent().setVisible(true);

						enPause = false;
						timer.reprendre();
					});

				});
				return;
			}
		}

		double time = (double) Math.round(timer.getTime() * 10) / 10;
		timer.stopper();

		// Modification du profil
		Profil profil = getMain().getProfilSelect();
		int facteurDiff = 0;

		Niveau niveauPlateau = defi.getNiveau();

		if (niveauPlateau == Niveau.STARTER) {
			facteurDiff = 60;
		} else if (niveauPlateau == Niveau.STARTER) {
			facteurDiff = 60;
		} else if (niveauPlateau == Niveau.JUNIOR) {
			facteurDiff = 70;
		} else if (niveauPlateau == Niveau.EXPERT) {
			facteurDiff = 80;
		} else if (niveauPlateau == Niveau.MASTER) {
			facteurDiff = 90;
		}

		// Exp gagnée
		int expGagnee = (int) ((facteurDiff - time) * mode.getFacteurMode());

		if (expGagnee < 15) {
			expGagnee = 15;
		}

		profil.ajouterExperience(expGagnee);

		LoaderFactory loaderFacto = new LoaderFactory(getMain()) {
			@Override
			public void run() {
				try {
					Database db = new Database();
					// Update du niveau et exp
					db.executeUpdateLocal("UPDATE JOUEUR SET niveau_joueur = " + profil.getNiveau() + " , exp_joueur = "
							+ profil.getExperience() + " WHERE pseudo_joueur = '" + profil.getPseudo() + "';");

					boolean dbCanConnectOnline = db.canConnectOnline();

					if (dbCanConnectOnline) {
						db.executeUpdateOnline("UPDATE JOUEUR SET Niv_jou = " + profil.getNiveau() + " WHERE Id_jou = "
								+ profil.getId_online() + ";");
					}

					// ********PROGRESSION********//
					if (mode == Mode.PROGRESSION) {
						// Vérification si le défi à déjà été résolue
						Table table = db.executeQueryLocal("SELECT record FROM PROGRESSION WHERE id_joueur = "
								+ profil.getId() + " AND contexte = '" + defi.getContexte() + "' AND difficulte = '"
								+ defi.getNiveau() + "' AND numerodefi = " + defi.getId() + ";");

						// Si le joueur ne l'a pas fait
						if (table == null || table.isVide()) {
							db.executeUpdateLocal(
									"INSERT INTO PROGRESSION VALUES (" + profil.getId() + ", '" + defi.getContexte()
											+ "', '" + defi.getNiveau() + "', " + defi.getId() + ", " + time + ");");
						}
						// S'il l'a déjà fait, on regarde si son temps est mieux
						else {
							double oldTime = Double.parseDouble(table.getLigne(0).getValeur(0));
							if (time < oldTime) {
								db.executeUpdateLocal("UPDATE PROGRESSION SET record = " + time + " WHERE id_joueur = "
										+ profil.getId() + " AND contexte = '" + defi.getContexte()
										+ "' AND difficulte = '" + defi.getNiveau() + "' AND numerodefi ="
										+ defi.getId() + ";");
							}

						}
					}
					// ********COMPETITION********//
					else if (mode == Mode.COMPETITION) {
						int id_defi = 1;
						if (defi.getContexte() == Contexte.NOCTURNE) {
							id_defi = 5;
						}
						if (defi.getNiveau() == Niveau.JUNIOR) {
							id_defi += 1;
						} else if (defi.getNiveau() == Niveau.EXPERT) {
							id_defi += 2;
						} else if (defi.getNiveau() == Niveau.MASTER) {
							id_defi += 3;
						}

						// Vérification si le challenge a déja été résolu
						Table table = db.executeQueryLocal("SELECT score FROM COMPETITION WHERE id_joueur = "
								+ profil.getId() + " AND id_defi = " + id_defi + ";");

						// Si le joueur ne l'a pas fait
						if (table == null || table.isVide()) {
							db.executeUpdateLocal("INSERT INTO COMPETITION VALUES (" + profil.getId() + ", '" + id_defi
									+ "', " + time + ");");
						}
						// S'il l'a déjà fait, on regarde si son temps est mieux
						else {
							double oldTime = Double.parseDouble(table.getLigne(0).getValeur(0));
							if (time < oldTime) {
								db.executeUpdateLocal("UPDATE COMPETITION SET score = " + time + " WHERE id_joueur = "
										+ profil.getId() + " AND id_defi =" + id_defi + ";");
							}
						}

						// On met le record en ligne
						if (dbCanConnectOnline) {
							db.executeUpdateOnline("CALL Nouveau_record (" + profil.getId_online() + ", " + id_defi
									+ ", " + time + ");");
						}
					}

					Thread.sleep(1000);
					RotateTransition animation = getAnimation();
					animation.setNode(loading);
					animation.play();
					loading.setVisible(true);

					if (parent instanceof SelectNiveauController) {
						if (((SelectNiveauController) parent).getMode() == Mode.COMPETITION) {
							getMain().afficherClassement(true, defi.getContexte(), defi.getNiveau());
						} else {
							getMain().afficherMenuContexteNiveau(Mode.ENTRAINEMENT);
						}

					} else if (parent instanceof TableauProgressionController) {
						getMain().afficherTableauProgression(defi.getContexte());

					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		};

		Thread t = new Thread(loaderFacto);
		t.start();

	}

	/**
	 * Permet d'afficher une étoile animée.
	 *
	 * @param x Décalage x par rapport au centre de l'écran.
	 * @param y Décalage y par rapport au centre de l'écran.
	 */
	public void afficherEtoile(double x, double y) {
		ImageView etoile1 = new ImageView(etoile);
		Bounds bounds = zoneGameplay.getBoundsInParent();
		zoneGameplay.getChildren().add(etoile1);
		zoneGameplay.setTopAnchor(etoile1, bounds.getMaxY() / 2.2);
		zoneGameplay.setLeftAnchor(etoile1, bounds.getMaxX() / 2);

		etoile1.setFitHeight(100);
		etoile1.setFitWidth(100);
		etoile1.setOpacity(0);

		Path path = new Path();
		path.getElements().add(new MoveTo(0, 0));
		path.getElements().add(new LineTo(x, y));

		PathTransition pathT = new PathTransition();
		pathT.setDuration(Duration.millis(Random.generer(600, 850)));
		pathT.setPath(path);
		pathT.setNode(etoile1);
		pathT.setDelay(Duration.millis(150));

		RotateTransition rT = new RotateTransition(Duration.millis(1000), etoile1);
		rT.setFromAngle(0);
		if (x >= 0) {
			rT.setToAngle(360);
		} else {
			rT.setToAngle(-360);
		}

		FadeTransition fadeT = new FadeTransition(Duration.millis(250), etoile1);
		fadeT.setFromValue(0);
		fadeT.setToValue(1);
		fadeT.setDelay(Duration.millis(150));

		FadeTransition reverseFadeT = new FadeTransition(Duration.millis(250), etoile1);
		reverseFadeT.setFromValue(1);
		reverseFadeT.setToValue(0);
		reverseFadeT.setDelay(Duration.millis(Random.generer(450, 750)));

		pathT.play();
		rT.play();
		fadeT.play();
		reverseFadeT.play();

		reverseFadeT.setOnFinished(event -> {
			zoneGameplay.getChildren().remove(etoile1);
		});
	}

	public void afficherSolution() {
		Scanner scanner = new Scanner(getClass().getResourceAsStream("/ressources/donnees/solution.txt"));

		int actuLigne = 1;

		int numDefi = defi.getNumeroDefi();

		// On va à la ligne demandée dans le fichier
		while (scanner.hasNext() && actuLigne != numDefi) {
			String ligne = scanner.nextLine();
			// Si c'est un commentaire, on saute
			if (!ligne.contains("//")) {
				actuLigne++;
			}
		}

		// ******* LECTURE SOLUTION *******//

		// Délimiteur
		scanner.useDelimiter(", ");

		ImageView iv = null;
		Piece piece = null;

		for (int i = 0; i < 3; i++) {
			iv = ivs[i];
			piece = pieces[i];

			// Informations
			int rotation = Integer.parseInt(scanner.next());
			String coordonnees = scanner.next();

			int numLigne = Integer.parseInt(Character.toString(coordonnees.charAt(0)));
			int numColonne = Integer.parseInt(Character.toString(coordonnees.charAt(2)));

			Bounds bIV = iv.getBoundsInParent();
			Bounds bPlateau = plateauGP.getBoundsInParent();
			minXplateau = bPlateau.getMinX() + 20;
			minYplateau = bPlateau.getMinY() + 20;
			maxXplateau = bPlateau.getMaxX() - 20;
			maxYplateau = bPlateau.getMaxY() - 20;

			double tailleCase = (maxXplateau - minXplateau) / 4;

			// Variables propres aux maisons
			int varMaisonX = 0;
			int varMaisonY = 0;

			if (iv == maisonPailleIV) {
				varMaisonX = 5;
				varMaisonY = 2;
			} else if (iv == maisonBriqueIV) {
				varMaisonX = 10;
				varMaisonY = 8;
			}

			// Calcul d'écart entre la pièce et sa position

			double ecartX = bIV.getMinX() + (iv.getParent().getLayoutX() - minXplateau) - varMaisonX
					- tailleCase * numColonne;
			double ecartY = bIV.getMinY() - (minYplateau - iv.getParent().getLayoutY()) - varMaisonY
					- tailleCase * numLigne;

			piece.setSelectionnee(true);

			// On adapte la pièce au nombre de rotation qu'elle va effectuer
			for (int k = 0; k < Math.abs(iv.getRotate() - piece.getDefautRotation() - rotation) / 90; k++) {
				aimantScroll(iv);
			}

			// Animation : on place la pièce
			TranslateTransition transition = new TranslateTransition(Duration.millis(2000), iv);
			transition.setToX(iv.getTranslateX() - ecartX);
			transition.setToY(iv.getTranslateY() - ecartY);
			transition.play();

			RotateTransition transition2 = new RotateTransition(Duration.millis(2000), iv);
			transition2.setToAngle(piece.getDefautRotation() + rotation);
			transition2.play();

			// Afficher le bouton Menu au bout de 2 secondes
			PauseTransition p = new PauseTransition(Duration.millis(2000));
			p.play();
			p.setOnFinished(event -> {
				extraButton.getParent().setVisible(true);

			});

		}

		scanner.close();
	}

	/**
	 * Permet d'ajouter un cochon au plateau.
	 *
	 * @param numLigne   Le numéro de la ligne où le cochon doit se situé. La
	 *                   première ligne est la numéro 0.
	 * @param numColonne Le numéro de la colonne où le cochon doit se situé. La
	 *                   première colonne est la numéro 0.
	 */
	public Case ajouterCochonAuPlateau(String coordonnees) {

		Case cochon = null;
		int numLigne = 0;
		int numColonne = 0;

		if (!coordonnees.equals("null")) {
			numLigne = Integer.parseInt(Character.toString(coordonnees.charAt(0)));
			numColonne = Integer.parseInt(Character.toString(coordonnees.charAt(2)));

			cochon = new Case(numLigne, numColonne, EtatCase.COCHON, null);
		}

		Image img = ResourceLoader.loadImg("cochon1.png");
		ImageView cochonIV = new ImageView(img);
		cochonIV.setScaleX(0.7);
		cochonIV.setScaleY(0.7);
		plateauGP.add(cochonIV, numColonne, numLigne, 1, 1);
		plateauGP.setHalignment(cochonIV, HPos.CENTER);

		return cochon;
	}

	public Case ajouterLoupAuPlateau(String coordonnees) {

		Case loup = null;
		int numLigne = 0;
		int numColonne = 0;

		if (!coordonnees.equals("null")) {
			numLigne = Integer.parseInt(Character.toString(coordonnees.charAt(0)));
			numColonne = Integer.parseInt(Character.toString(coordonnees.charAt(2)));

			loup = new Case(numLigne, numColonne, EtatCase.LOUP, null);
		}

		Image img = ResourceLoader.loadImg("loup1.png");
		ImageView loupIV = new ImageView(img);
		loupIV.setScaleX(0.7);
		loupIV.setScaleY(0.7);
		plateauGP.add(loupIV, numColonne, numLigne, 1, 1);
		plateauGP.setHalignment(loupIV, HPos.CENTER);

		return loup;

	}

	public void initialiserPlateau(Mode mode, Contexte contextePlateau, Niveau niveauPlateau, int numeroDefi) {

		this.mode = mode;

		Scanner scanner = new Scanner(getClass().getResourceAsStream("/ressources/donnees/defi.txt"));

		int actuLigne = 1;

		Defi defi = null;

		// Randomize un défi si entraînement
		if (mode == Mode.ENTRAINEMENT) {
			defi = new Defi(contextePlateau, niveauPlateau);
		}
		// Défi sélectionné par le joueur
		else if (mode == Mode.PROGRESSION) {
			defi = new Defi(contextePlateau, niveauPlateau, numeroDefi);
		}

		// Les 6 défis du mode compétition
		else {

			// Si c'est le premier défi
			if (numeroDefi == -1) {
				listeDefis = new ArrayList<Defi>();
				for (int i = 0; i < 6; i++) {
					listeDefis.add(new Defi(contextePlateau, niveauPlateau, i + 1));
				}
				Collections.shuffle(listeDefis);
				defi = listeDefis.get(0);

				extraButton.setText("PASSER");
				extraButton.getParent().setVisible(true);
			}
			// Si c'est un défi suivant
			else {
				defi = this.defi;
			}

		}

		this.defi = defi;

		int numDefi = defi.getNumeroDefi();
		Niveau niveauDefi = defi.getNiveau();

		// On va à la ligne demandée dans le fichier
		while (scanner.hasNext() && actuLigne != numDefi) {
			String ligne = scanner.nextLine();
			// Si c'est un commentaire, on saute
			if (!ligne.contains("//")) {
				actuLigne++;
			}

		}
		// ******* LECTURE DÉFI *******//

		// Délimiteur
		scanner.useDelimiter(", ");

		// Contexte
		String idContexte = scanner.next();
		Contexte contexte = null;
		if (idContexte.contains("D")) {
			contexte = Contexte.DIURNE;
			this.contexte.setText("DIURNE");

			background.getStylesheets().add(ResourceLoader.loadCss("diurne.css"));
		} else if (idContexte.contains("N")) {
			contexte = Contexte.NOCTURNE;
			this.contexte.setText("NOCTURNE");

			background.getStylesheets().add(ResourceLoader.loadCss("nocturne.css"));
		}

		// On clean le plateau
		plateauGP.getChildren().clear();

		// Loup
		String loup = scanner.next();
		Case loupCase = null;
		if (!loup.equals("null")) {
			loupCase = ajouterLoupAuPlateau(loup);
		}

		// Cochon 1
		String cochon1 = scanner.next();
		Case cochonCase1 = null;
		if (!cochon1.equals("null")) {
			cochonCase1 = ajouterCochonAuPlateau(cochon1);
		}

		// Cochon 2
		String cochon2 = scanner.next();
		Case cochonCase2 = null;
		if (!cochon2.equals("null")) {
			cochonCase2 = ajouterCochonAuPlateau(cochon2);
		}

		// Cochon 3
		String cochon3 = scanner.next();
		Case cochonCase3 = null;
		if (!cochon3.contains("null")) {
			cochonCase3 = ajouterCochonAuPlateau(cochon3);
		}

		// Création Plateau
		plateau = new Plateau(contexte, loupCase, cochonCase1, cochonCase2, cochonCase3);

		if (niveauDefi == Niveau.STARTER) {
			niveau.setText("STARTER - " + (defi.getId()));
		} else if (niveauDefi == Niveau.JUNIOR) {
			niveau.setText("JUNIOR - " + (defi.getId()));
		} else if (niveauDefi == Niveau.EXPERT) {
			niveau.setText("EXPERT - " + (defi.getId()));
		} else if (niveauDefi == Niveau.MASTER) {
			niveau.setText("MASTER - " + (defi.getId()));
		}

		scanner.close();
		
		if (mode != Mode.COMPETITION){
			((HBox)background.getChildren().get(0)).getChildren().remove(indicationDefi);
		}
		else{
			pauseButton.setImage(ResourceLoader.loadImg("menu_btn.png"));
		}
	}

	@FXML
	public void selectMaisonClavier(KeyEvent event) {

		if (withMouse || String.valueOf(event.isAltDown()).contains("true")) {
			return;
		}

		if (!enPause) {

			ImageView iv = null;

			String code = event.getCode().toString();

			// RIGHT
			if (code.equals(right)) {
				// Si une pièce est déjà sélectionnée
				if (getNumMaisonSelectionnee() != -1) {
					iv = ivs[getNumMaisonSelectionnee()];

					Bounds bPlateau = plateauGP.getBoundsInParent();
					minXplateau = bPlateau.getMinX() + 20;
					minYplateau = bPlateau.getMinY() + 20;
					maxXplateau = bPlateau.getMaxX() - 20;
					maxYplateau = bPlateau.getMaxY() - 20;

					double tailleCase = (maxXplateau - minXplateau) / 4;

					double ecartX = 0;
					double ecartY = 0;

					// Si la pièce n'est pas à droite du plateau
					if (numColonneMaison != 3) {
						ecartX = tailleCase;
						numColonneMaison++;
					}

					// On déplace la pièce
					iv.setTranslateX(iv.getTranslateX() + ecartX);
					iv.setTranslateY(iv.getTranslateY() + ecartY);

					verificationPlacementPiece(numColonneMaison, numLigneMaison);

				}
				// Si une pièce n'est pas focus
				else if (getNumMaisonFocus() == -1) {

					// On prend la première qui n'est pas focus
					for (int i = 0; i < 3; i++) {
						if (!pieces[i].isFocus() && !pieces[i].isPlacee()) {
							iv = ivs[i];
							pieces[i].setFocus(true);

							iv.setOpacity(0.65);
							DropShadow shadow = new DropShadow();
							shadow.setColor(Color.web("#ff7700"));
							iv.setEffect(shadow);

							iv.toFront();
							break;
						}
					}

				}

			}
			// DOWN + UP
			else if (code.equals(down) || code.equals(up)) {

				// Si une pièce est sélectionnée + DOWN
				if (getNumMaisonSelectionnee() != -1 && code.equals(down)) {
					iv = ivs[getNumMaisonSelectionnee()];

					Bounds bPlateau = plateauGP.getBoundsInParent();
					minXplateau = bPlateau.getMinX() + 20;
					minYplateau = bPlateau.getMinY() + 20;
					maxXplateau = bPlateau.getMaxX() - 20;
					maxYplateau = bPlateau.getMaxY() - 20;

					double tailleCase = (maxXplateau - minXplateau) / 4;

					double ecartX = 0;
					double ecartY = 0;

					// Si la pièce n'est pas à droite du plateau
					if (numLigneMaison != 3) {
						ecartY = tailleCase;
						numLigneMaison++;
					}

					// On déplace la pièce
					iv.setTranslateX(iv.getTranslateX() + ecartX);
					iv.setTranslateY(iv.getTranslateY() + ecartY);

					verificationPlacementPiece(numColonneMaison, numLigneMaison);
				}
				// Si une pièce est sélectionnée + UP
				else if (getNumMaisonSelectionnee() != -1 && code.equals(up)) {
					iv = ivs[getNumMaisonSelectionnee()];

					Bounds bPlateau = plateauGP.getBoundsInParent();
					minXplateau = bPlateau.getMinX() + 20;
					minYplateau = bPlateau.getMinY() + 20;
					maxXplateau = bPlateau.getMaxX() - 20;
					maxYplateau = bPlateau.getMaxY() - 20;

					double tailleCase = (maxXplateau - minXplateau) / 4;

					double ecartX = 0;
					double ecartY = 0;

					// Si la pièce n'est pas à droite du plateau
					if (numLigneMaison != 0) {
						ecartY = tailleCase;
						numLigneMaison--;
					}

					// On déplace la pièce
					iv.setTranslateX(iv.getTranslateX() - ecartX);
					iv.setTranslateY(iv.getTranslateY() - ecartY);

					verificationPlacementPiece(numColonneMaison, numLigneMaison);
				}

				// On récupère la pièce actuellement focus
				else if (getNumMaisonFocus() != -1) {
					int num = getNumMaisonFocus();

					iv = ivs[num];
					pieces[num].setFocus(false);

					iv.setOpacity(1);
					iv.setEffect(null);

					iv.toBack();

					boolean pieceSurPlateau = pieces[num].isPlacee();

					// On cherche la prochaine pièce à focus
					if (code.equals(down)) {
						for (int i = 0; i < 3; i++) {

							// Emplacement suivant
							num = (num + 1) % 3;

							// Si la pièce suivante n'est pas placée, on la
							// focus

							if ((!pieces[num].isPlacee() && !pieceSurPlateau)
									|| (pieces[num].isPlacee() && pieceSurPlateau)) {
								iv = ivs[num];
								pieces[num].setFocus(true);
								break;
							}
						}
					}

					else if (code.equals(up)) {
						for (int i = 0; i < 3; i++) {
							// Emplacement suivant
							num -= 1;
							if (num == -1) {
								num = 2;
							}

							// Si la pièce précédente n'est pas placée, on la
							// focus
							if ((!pieces[num].isPlacee() && !pieceSurPlateau)
									|| (pieces[num].isPlacee() && pieceSurPlateau)) {
								iv = ivs[num];
								pieces[num].setFocus(true);
								break;
							}
						}
					}
					// Si une pièce est bien focus
					if (iv != null) {
						// On assigne les effets à la nouvelle pièce
						iv.setOpacity(0.65);
						DropShadow shadow = new DropShadow();
						shadow.setColor(Color.web("#ff7700"));
						iv.setEffect(shadow);

						iv.toFront();
					}

				}

			}
			// LEFT
			else if (code.equals(left)) {
				for (int i = 0; i < 3; i++) {
					// Si la pièce est sélectionnée
					if (pieces[i].isSelectionnee()) {
						iv = ivs[i];

						Bounds bPlateau = plateauGP.getBoundsInParent();
						minXplateau = bPlateau.getMinX() + 20;
						minYplateau = bPlateau.getMinY() + 20;
						maxXplateau = bPlateau.getMaxX() - 20;
						maxYplateau = bPlateau.getMaxY() - 20;

						double tailleCase = (maxXplateau - minXplateau) / 4;

						double ecartX = 0;
						double ecartY = 0;

						// Si la maison est déjà à gauche du plateau
						if (numColonneMaison != 0) {
							ecartX = tailleCase;
							numColonneMaison--;
						}

						// On déplace la pièce
						iv.setTranslateX(iv.getTranslateX() - ecartX);
						iv.setTranslateY(iv.getTranslateY() - ecartY);

						verificationPlacementPiece(numColonneMaison, numLigneMaison);
						return;
					}
					// Si une pièce n'est pas focus
					else if (getNumMaisonFocus() == -1) {

						// On prend la première qui n'est pas focus
						for (int k = 0; k < 3; k++) {
							if (!pieces[k].isFocus() && pieces[k].isPlacee()) {
								iv = ivs[k];
								pieces[k].setFocus(true);

								iv.setOpacity(0.65);
								DropShadow shadow = new DropShadow();
								shadow.setColor(Color.web("#ff7700"));
								iv.setEffect(shadow);

								iv.toFront();
								break;
							}
						}
						return;
					}
				}
			}

			else if (code.equals(enter)) {

				// Si une maison est sélectionnée, on la pose si on peut
				if (getNumMaisonSelectionnee() != -1) {
					int num = getNumMaisonSelectionnee();
					iv = ivs[num];

					// On tente de placer la pièce
					plateau.placerPiece(pieces[num], plateau.getCase(numLigneMaison, numColonneMaison));
					if (pieces[num].isPlacee()) {
						iv.setOpacity(1);
						iv.setEffect(null);
						iv.toBack();

						pieces[num].setSelectionnee(false);
						pieces[num].setFocus(false);

						// Vérification win
						if (plateau.isGagner()) {
							afficherGagner();
						}
					}

				}
				// Si une maison est focus, on la sélectionne
				else if (getNumMaisonFocus() != -1) {
					int num = getNumMaisonFocus();

					iv = ivs[num];
					pieces[num].setSelectionnee(true);

					iv.setOpacity(0.65);
					DropShadow shadow = new DropShadow();
					shadow.setOffsetX(5);
					shadow.setOffsetY(5);
					iv.setEffect(shadow);
					iv.toFront();

					Bounds bIV = iv.getBoundsInParent();
					Bounds bPlateau = plateauGP.getBoundsInParent();
					minXplateau = bPlateau.getMinX() + 20;
					minYplateau = bPlateau.getMinY() + 20;
					maxXplateau = bPlateau.getMaxX() - 20;
					maxYplateau = bPlateau.getMaxY() - 20;

					double tailleCase = (maxXplateau - minXplateau) / 4;

					int varMaisonX = 0;

					if (iv == maisonPailleIV) {
						varMaisonX = 5;
					} else if (iv == maisonBriqueIV) {
						varMaisonX = 10;
					}

					// Calcul d'écart entre la pièce et le plateau si la pièce
					// n'était pas placée
					if (!pieces[num].isPlacee()) {
						double ecartX = bIV.getMinX() + (iv.getParent().getLayoutX() - maxXplateau) + tailleCase
								+ varMaisonX;
						double ecartY = bIV.getMinY() - (minYplateau - iv.getParent().getLayoutY());
						numLigneMaison = 0;
						numColonneMaison = 3;

						TranslateTransition transition = new TranslateTransition(Duration.millis(150), iv);
						transition.setToX(iv.getTranslateX() - ecartX);
						transition.setToY(iv.getTranslateY() - ecartY);
						transition.play();

						transition.setOnFinished(event2 -> {
							verificationPlacementPiece(numColonneMaison, numLigneMaison);
						});
					} else {

						numColonneMaison = pieces[num].getNumCaseColonne();
						numLigneMaison = pieces[num].getNumCaseLigne();
						plateau.retirerPiece(plateau.getCase(numLigneMaison, numColonneMaison));
					}

				}

			} else if (code.equals(back)) {
				// Si la pièce est sélectionnée
				if (getNumMaisonSelectionnee() != -1) {
					int num = getNumMaisonSelectionnee();

					iv = ivs[num];

					// Si elle est placée on l'enlève du plateau
					if (pieces[num].isPlacee()) {
						numColonneMaison = pieces[num].getNumCaseColonne();
						numLigneMaison = pieces[num].getNumCaseLigne();
						plateau.retirerPiece(plateau.getCase(numLigneMaison, numColonneMaison));
					}

					annulerPiece(pieces[num], iv);
				} else if (getNumMaisonFocus() != -1) {
					int num = getNumMaisonFocus();

					iv = ivs[num];

					if (pieces[num].isPlacee()) {
						iv.setOpacity(1);
						iv.setEffect(null);
						pieces[num].setFocus(false);
					} else {
						annulerPiece(pieces[num], iv);
					}

				}

			} else if (code.equals(alt)) {

				// Si une pièce est sélectionnée, on la tourne
				for (int i = 0; i < 3; i++) {
					if (pieces[i].isSelectionnee()) {
						iv = ivs[i];

						double rotate = iv.getRotate();
						pieces[i].tournerHoraire();

						iv.setRotate((rotate + 90) % 360);
						aimantScroll(iv);

						verificationPlacementPiece(numColonneMaison, numLigneMaison);
						break;
					}
				}

			} else if (code.equals(control)) {
				// Si une pièce est sélectionnée, on la tourne
				for (int i = 0; i < 3; i++) {
					if (pieces[i].isSelectionnee()) {

						iv = ivs[i];

						double rotate = iv.getRotate();
						pieces[i].tournerAntiHoraire();

						iv.setRotate((rotate - 90) % 360);
						aimantScroll(iv);

						verificationPlacementPiece(numColonneMaison, numLigneMaison);
						break;
					}
				}
			}
		}
	}

	public int getNumMaisonFocus() {
		for (int i = 0; i < 3; i++) {
			if (pieces[i].isFocus()) {

				return i;
			}
		}
		return -1;
	}

	public int getNumMaisonSelectionnee() {
		for (int i = 0; i < 3; i++) {
			if (pieces[i].isSelectionnee()) {

				return i;
			}
		}
		return -1;
	}

	public void setParent(Controller controller) {
		this.parent = controller;
	}

}
