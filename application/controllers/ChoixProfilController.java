package application.controllers;

import java.io.IOException;

import application.models.Profil;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import utils.ResourceLoader;
import utils.database.Database;
import utils.database.Ligne;
import utils.database.Table;

public class ChoixProfilController extends Controller {

	private AnchorPane creerNouveauProfilAP;
	private CreerNouveauProfilController controller;

	@FXML
	private GridPane profilsGP;

	@FXML
	private AnchorPane zoneProfil;

	@FXML
	private ImageView nouveauProfilIV;

	@FXML
	private ImageView loading;

	private int ligneIndex;
	private int colonneIndex;

	private boolean creationProfilEnCours;

	@FXML
	public void initialize() {

		ligneIndex = 0;
		colonneIndex = 1;
		creationProfilEnCours = false;

		nouveauProfilIV.getStyleClass().add("clickableButton");

		// Pause
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/views/CreerNouveauProfil.fxml"));
			creerNouveauProfilAP = (AnchorPane) loader.load();
			creerNouveauProfilAP.getStylesheets().add(ResourceLoader.loadCss("cadreCreerProfil.css"));

			controller = loader.getController();
			controller.setParent(this);

			zoneProfil.getChildren().addAll(creerNouveauProfilAP);
			creerNouveauProfilAP.setVisible(false);

		} catch (IOException e) {
			e.printStackTrace();
		}

		loading.setVisible(false);
	}

	public void initProfils() {
		Database db = new Database();

		// Création des tables si elles n'existe pas
		Table table = db.executeQueryLocal("SELECT * FROM JOUEUR;");

		if (table == null) {
			db.executeUpdateLocal("CREATE TABLE JOUEUR(id_joueur IDENTITY PRIMARY KEY NOT NULL, id_online INT,"
					+ "pseudo_joueur VARCHAR(30), niveau_joueur INT, exp_joueur INT, settings VARCHAR(200), id_couleur INT);");
			db.executeUpdateLocal(
					"CREATE TABLE PROGRESSION(id_joueur INT, contexte VARCHAR(10), difficulte VARCHAR(10), numeroDefi INT, record FLOAT);");
			db.executeUpdateLocal("CREATE TABLE DEFI(id_defi INT, nom_niveau VARCHAR(10), nom_contexte VARCHAR(10));");
			db.executeUpdateLocal(
					"INSERT INTO DEFI VALUES (1, 'STARTER', 'DIURNE'), (2, 'JUNIOR', 'DIURNE'), (3, 'EXPERT', 'DIURNE'),"
							+ " (4, 'MASTER', 'DIURNE'), (5, 'STARTER', 'NOCTURNE'), (6, 'JUNIOR', 'NOCTURNE'), (7, 'EXPERT', 'NOCTURNE'),"
							+ " (8, 'MASTER', 'NOCTURNE')");
			db.executeUpdateLocal("CREATE TABLE COMPETITION(id_joueur INT, id_defi INT, score FLOAT);");
		}

		Table pseudos = db.executeQueryLocal(
				"SELECT id_joueur, id_online, pseudo_joueur, niveau_joueur, exp_joueur, settings, id_couleur FROM JOUEUR ORDER BY id_joueur DESC;");
		if (pseudos != null){
			for (Ligne ligne : pseudos.getLignes()) {
				ajouterProfil(Integer.parseInt(ligne.getValeur(0)), Integer.parseInt(ligne.getValeur(1)),
						ligne.getValeur(2), ligne.getValeur(3), ligne.getValeur(4), ligne.getValeur(5).split(":"),
						Integer.parseInt(ligne.getValeur(6)));
			}
		}
	}

	@FXML
	public void clickNouveauProfil() {
		creationProfilEnCours = true;

		controller.init();
		creerNouveauProfilAP.setTranslateX(zoneProfil.getWidth() / 2 - creerNouveauProfilAP.getWidth() / 2);
		creerNouveauProfilAP.setTranslateY(zoneProfil.getHeight() / 2 - creerNouveauProfilAP.getHeight() / 2);
		creerNouveauProfilAP.setVisible(true);

		BoxBlur effet = new BoxBlur();
		effet.setWidth(50);
		effet.setHeight(50);
		effet.setIterations(2);

		for (Node node : zoneProfil.getChildren()) {
			if (node != creerNouveauProfilAP) {
				node.setEffect(effet);
			}
		}

		// Les boutons ne sont plus cliquables
		for (Node node : profilsGP.getChildren()) {
			node.getStyleClass().remove("clickableButton");
			node.setCursor(Cursor.DEFAULT);
		}
	}

	public void annulerCreationProfil() {
		creationProfilEnCours = false;

		creerNouveauProfilAP.setVisible(false);

		for (Node node : zoneProfil.getChildren()) {
			if (node != creerNouveauProfilAP) {
				node.setEffect(null);
			}
		}

		// Les boutons sont de nouveaux cliquables for (Node node :
		for (Node node : profilsGP.getChildren()) {
			node.getStyleClass().add("clickableButton");
			node.setCursor(Cursor.HAND);
		}
	}

	/**
	 * Ajoute un profil au menu.
	 * 
	 * @param pseudo Pseudo du joueur.
	 * @param niveau Niveau du joeur.
	 * @param exp    Exp�rience du joueur.
	 */
	public void ajouterProfil(int id, int id_online, String pseudo, String niveau, String exp, String[] settings,
			int couleur) {
		VBox vb = new VBox();
		vb.setPrefHeight(250);
		vb.setStyle("-fx-background-image : url('ressources/images/profil" + couleur + ".png');"
				+ "-fx-background-size : cover;");
		vb.getStyleClass().add("clickableButton");
		vb.setCursor(Cursor.HAND);

		// TODO doubles apostrophes
		String tempPseudo = pseudo.replace("\"", "''");

		Label pseudoLB = new Label(tempPseudo);
		pseudoLB.setPadding(new Insets(20, 0, 0, 30));
		pseudoLB.setFont(ResourceLoader.loadFont("Grobold.ttf", 30));
		pseudoLB.setTextFill(Color.WHITE);

		Line line = new Line(0, 0, 175, 0);
		line.setStroke(Color.WHITE);
		line.setStrokeWidth(2);
		line.setStrokeType(StrokeType.CENTERED);
		line.setStrokeLineCap(StrokeLineCap.ROUND);
		line.setStrokeLineJoin(StrokeLineJoin.ROUND);
		line.setTranslateX(20);
		line.setTranslateY(5);

		int expMax = Integer.parseInt(niveau) * 50;

		Label niveauLB = new Label("Niv. " + niveau + " : " + exp + "/" + expMax + "EXP");
		niveauLB.setPadding(new Insets(20, 0, 0, 30));
		niveauLB.setFont(ResourceLoader.loadFont("Hogfish.ttf", 25));
		niveauLB.setTextFill(Color.WHITE);

		Label defiReussiLB = new Label("JOUER");
		defiReussiLB.setPadding(new Insets(20, 0, 0, 30));
		defiReussiLB.setFont(ResourceLoader.loadFont("Hogfish.ttf", 30));
		defiReussiLB.setTextFill(Color.WHITE);

		vb.getChildren().addAll(pseudoLB, line, niveauLB, defiReussiLB);

		vb.setOnMouseClicked(event -> {
			if (!creationProfilEnCours && !isEnChargement()) {

				// Mises à jours des informations
				Database db = new Database();
				if (db.canConnectOnline()) {
					Table table = db
							.executeQueryOnline("SELECT id_jou FROM JOUEUR WHERE Pseudo_jou = '" + pseudo + "';");
					// Si le joueur n'a pas encore push en ligne
					if (!table.isVide() && id_online == -1) {
						db.executeUpdateOnline("INSERT INTO JOUEUR VALUES (NULL, '" + pseudo + "', " + niveau + ");");
						return;
					} else if (id_online == 1){
						db.executeUpdateOnline("INSERT INTO JOUEUR VALUES (NULL, '" + pseudo + "', " + niveau + ");");
					}
				}

				setEnChargement(true);
				RotateTransition animation = getAnimation();
				animation.setNode(loading);
				animation.play();
				loading.setVisible(true);

				getMain().setProfilSelect(new Profil(id, pseudo, Integer.parseInt(niveau), Integer.parseInt(exp),
						settings, id_online, couleur));
				getMain().afficherMenuAcceuil();
			}
		});

		if (colonneIndex == 0) {
			profilsGP.setPrefHeight(profilsGP.getPrefHeight() + 275);
		}
		profilsGP.add(vb, colonneIndex, ligneIndex);

		if (colonneIndex == 3) {
			colonneIndex = 0;
			ligneIndex++;
		} else {
			colonneIndex++;
		}
	}

}
