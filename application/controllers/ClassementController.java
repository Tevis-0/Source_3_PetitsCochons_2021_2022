package application.controllers;

import java.util.ArrayList;
import java.util.List;

import application.models.Contexte;
import application.models.Niveau;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import utils.ResourceLoader;
import utils.database.Database;
import utils.database.Table;

public class ClassementController extends Controller {

	@FXML
	private BarreJoueurController barreJoueurController;

	@FXML
	private AnchorPane background;

	@FXML
	private HBox hb;

	@FXML
	private VBox classement;

	@FXML
	private Label titre;

	@FXML
	private VBox optionsSelect;

	@FXML
	private ImageView diurneButton;

	@FXML
	private ImageView nocturneButton;

	@FXML
	private ImageView roue;

	@FXML
	private ImageView aiguille;

	@FXML
	private ImageView retourButton;

	private Image diurneNoSelect;
	private Image diurneSelect;
	private Image nocturneNoSelect;
	private Image nocturneSelect;

	private Table[] tables;
	private boolean roueFocus = false;
	private boolean isClassementOnline;
	private double angle;

	private Contexte contexte;
	private Niveau niveau;

	public void initialize() {

		// ********* RESSOURCES IMAGES *********//

		diurneNoSelect = ResourceLoader.loadImg("diurne_nonselect.png");
		diurneSelect = ResourceLoader.loadImg("diurne_select.png");
		nocturneNoSelect = ResourceLoader.loadImg("nocturne_nonselect.png");
		nocturneSelect = ResourceLoader.loadImg("nocturne_select.png");

		tables = new Table[8];

		Database db = new Database();
		boolean dbCanConnectOnline = db.canConnectOnline();

		if (dbCanConnectOnline) {
			isClassementOnline = true;
			System.out.println("online");
			for (int i = 0; i < 8; i++) {
				tables[i] = db.executeQueryOnline(
						"SELECT JOUEUR.Id_jou, Pseudo_jou, Niv_jou, Temps FROM RECORD INNER JOIN JOUEUR ON RECORD.Id_jou = JOUEUR.Id_jou "
								+ "WHERE Id_defi = " + (i + 1) + " ORDER BY Temps, Pseudo_jou");
			}
		} else {
			isClassementOnline = false;
			System.out.println("local");
			for (int i = 0; i < 8; i++) {
				tables[i] = db.executeQueryLocal(
						"SELECT JOUEUR.Id_joueur, Pseudo_joueur, Niveau_joueur, score FROM COMPETITION INNER JOIN JOUEUR ON COMPETITION.Id_joueur = JOUEUR.Id_joueur "
								+ "WHERE Id_defi = " + (i + 1) + " ORDER BY score, Pseudo_joueur");
			}
		}

		diurneButton.getStyleClass().add("clickableButton");
		nocturneButton.getStyleClass().add("clickableButton");
		retourButton.getStyleClass().add("clickableButton");

		Rotate rotate = new Rotate();
		rotate.setPivotX(24);
		rotate.setPivotY(132);
		rotate.axisProperty().setValue(Rotate.Z_AXIS);
		rotate.setAngle(-45);
		aiguille.getTransforms().add(rotate);
		angle = -45;

		contexte = Contexte.DIURNE;
		niveau = Niveau.STARTER;
	}

	public void initClassement(Contexte contexte, Niveau niveau) {

		titre.setText("CLASSEMENT " + contexte + "-" + niveau);

		List<Node> hboxASuppr = new ArrayList<Node>();

		// On clean tout
		for (Node node : classement.getChildren()) {
			if (node instanceof HBox && node.getId() == null) {
				hboxASuppr.add(node);
			}
		}

		classement.getChildren().removeAll(hboxASuppr);

		// On cherche l'id du défi
		int id_defi = 0;

		if (contexte == Contexte.NOCTURNE) {
			id_defi = 4;
		}

		if (niveau == Niveau.JUNIOR) {
			id_defi += 1;
		} else if (niveau == Niveau.EXPERT) {
			id_defi += 2;
		} else if (niveau == Niveau.MASTER) {
			id_defi += 3;
		}
		Table table = tables[id_defi];

		if (table == null || table.isVide()) {
			HBox ligne = new HBox();
			ligne.setAlignment(Pos.CENTER);
			ligne.setMinHeight(35);
			ligne.getStyleClass().add("uniqueLigne");

			Label message = new Label("Aucun temps pour ce couple contexte-niveau.");
			message.setFont(ResourceLoader.loadFont("Grobold.ttf", 20));
			message.setAlignment(Pos.CENTER);

			ligne.getChildren().add(message);

			classement.getChildren().add(ligne);

		} else {
			int rangTemp = 1;

			for (int i = 0; i < table.getNombreLigne() && i < 20; i++) {

				HBox ligne = new HBox();
				ligne.setMinHeight(27);

				if (i == 0) {
					if (table.getNombreLigne() == 1) {
						ligne.getStyleClass().add("uniqueLigne");
					} else {
						ligne.getStyleClass().add("premiereLigne");
					}
				} else if (i == table.getNombreLigne() - 1 || i == 19) {
					ligne.getStyleClass().add("derniereLigne");
				} else {
					ligne.getStyleClass().add("ligne");
				}

				Font grobold = ResourceLoader.loadFont("Grobold.ttf", 17);

				// Rang du joueur (on traite les égalités)
				double score = Double.parseDouble(table.getLigne(i).getValeur(3));
				int actuelRang = rangTemp;

				if (i != 0 && score != Double.parseDouble(table.getLigne(i - 1).getValeur(3))) {
					actuelRang = i + 1;
				}

				Label rang = new Label(Integer.toString(actuelRang));
				rang.setFont(grobold);
				rang.setAlignment(Pos.CENTER);
				rang.setPrefHeight(27);
				rang.setMinWidth(60);
				rang.setPadding(new Insets(0, 0, 0, 10));

				Label pseudo = new Label(table.getLigne(i).getValeur(1));
				pseudo.setFont(grobold);
				pseudo.setAlignment(Pos.CENTER_LEFT);
				pseudo.setPrefHeight(27);
				pseudo.setMinWidth(315);
				pseudo.setPadding(new Insets(0, 0, 0, 20));

				Label niv = new Label("Niv. " + table.getLigne(i).getValeur(2));
				niv.setFont(grobold);
				niv.setAlignment(Pos.CENTER_LEFT);
				niv.setPrefHeight(27);
				niv.setMinWidth(90);
				niv.setPadding(new Insets(0, 0, 0, 20));

				Label temps = new Label(Double.toString(score));
				temps.setFont(grobold);
				temps.setAlignment(Pos.CENTER_LEFT);
				temps.setPrefHeight(27);
				temps.setMinWidth(150);
				temps.setPadding(new Insets(0, 0, 0, 40));

				// On identifie le joueur

				int myId = getMain().getProfilSelect().getId();
				if (isClassementOnline) {
					myId = getMain().getProfilSelect().getId_online();
				}

				if (myId == Integer.parseInt(table.getLigne(i).getValeur(0))) {
					rang.setTextFill(Color.DODGERBLUE);
					pseudo.setTextFill(Color.DODGERBLUE);
					niv.setTextFill(Color.DODGERBLUE);
					temps.setTextFill(Color.DODGERBLUE);
				}

				ligne.getChildren().addAll(rang, pseudo, niv, temps);

				ligne.setOnMouseEntered(event -> {
					ligne.getStyleClass().add("focusLigne");
				});

				ligne.setOnMouseExited(event -> {
					ligne.getStyleClass().remove("focusLigne");
				});

				classement.getChildren().add(ligne);

				rangTemp = actuelRang;
			}
		}

	}

	public void initRoue(Contexte contexte, Niveau niveau) {
		double newAngle = 0;

		if (niveau == Niveau.STARTER) {
			// Starter
			newAngle = -45 - angle;
			angle = -45;
		} else if (niveau == Niveau.MASTER) {
			// Master
			newAngle = 225 - angle;
			angle = 225;
		} else if (niveau == Niveau.JUNIOR) {
			// Junior
			newAngle = 45 - angle;
			angle = 45;
		} else {
			// Expert
			newAngle = 135 - angle;
			angle = 135;
		}

		if (newAngle == 270) {
			newAngle = -90;
		} else if (newAngle == -270) {
			newAngle = 90;
		}

		Rotate rotate = new Rotate();
		rotate.setPivotX(24);
		rotate.setPivotY(132);
		rotate.axisProperty().setValue(Rotate.Z_AXIS);

		Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(rotate.angleProperty(), 0)),
				new KeyFrame(Duration.millis(1), new KeyValue(rotate.angleProperty(), newAngle)));

		aiguille.getTransforms().add(rotate);
		timeline.play();
		
		if(contexte == Contexte.NOCTURNE){
			diurneButton.setImage(diurneNoSelect);
			nocturneButton.setImage(nocturneSelect);
		}
		}

	@FXML
	public void clickButton(MouseEvent event) {
		ImageView button = (ImageView) event.getTarget();

		if (button == diurneButton) {
			button.setImage(diurneSelect);
			nocturneButton.setImage(nocturneNoSelect);

			contexte = Contexte.DIURNE;

		} else if (button == nocturneButton) {
			button.setImage(nocturneSelect);
			diurneButton.setImage(diurneNoSelect);

			contexte = Contexte.NOCTURNE;
		}

		initClassement(contexte, niveau);

	}

	@FXML
	public void hoverRoue(MouseEvent event) {
		double centreX = roue.getBoundsInLocal().getMaxX() / 2;
		double centreY = roue.getBoundsInLocal().getMaxY() / 2;
		double x = event.getX();
		double y = event.getY();

		// Si le curseur est sur la roue
		if (Math.pow((x - centreX), 2) + Math.pow((centreY - y), 2) < Math.pow(centreY, 2)) {
			roue.setCursor(Cursor.HAND);
			roueFocus = true;
		} else {
			roue.setCursor(Cursor.DEFAULT);
			roueFocus = false;
		}
	}

	@FXML
	public void clickRoue(MouseEvent event) {
		if (roueFocus) {
			double x = event.getX();
			double y = event.getY();

			double newAngle = 0;

			if (x <= 222) {
				if (y <= 175) {
					// Starter
					newAngle = -45 - angle;
					angle = -45;
					niveau = Niveau.STARTER;
				} else {
					// Master
					newAngle = 225 - angle;
					angle = 225;
					niveau = Niveau.MASTER;
				}
			} else {
				if (y <= 175) {
					// Junior
					newAngle = 45 - angle;
					angle = 45;
					niveau = Niveau.JUNIOR;
				} else {
					// Expert
					newAngle = 135 - angle;
					angle = 135;
					niveau = Niveau.EXPERT;
				}
			}

			if (newAngle == 270) {
				newAngle = -90;
			} else if (newAngle == -270) {
				newAngle = 90;
			}

			Rotate rotate = new Rotate();
			rotate.setPivotX(24);
			rotate.setPivotY(132);
			rotate.axisProperty().setValue(Rotate.Z_AXIS);

			Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(rotate.angleProperty(), 0)),
					new KeyFrame(Duration.millis(250 * Math.abs(newAngle) / 180),
							new KeyValue(rotate.angleProperty(), newAngle)));

			aiguille.getTransforms().add(rotate);
			timeline.play();

			initClassement(contexte, niveau);
		}
	}

	@FXML
	public void clickRetour() {
		if (!isEnChargement()) {
			setEnChargement(true);
			getMain().afficherMenuAcceuil();
		}
	}

	public void enleverRoue() {
		hb.getChildren().remove(optionsSelect);
	}

	public BarreJoueurController getBarreJoueurController() {
		return barreJoueurController;
	}

}
