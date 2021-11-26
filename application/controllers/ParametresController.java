package application.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import utils.Convert;
import utils.ResourceLoader;
import utils.database.Database;
import utils.database.Table;

public class ParametresController extends Controller {

	private AnchorPane appuieTouche;
	private AppuieToucheController toucheController;

	@FXML
	private String parent;

	@FXML
	private AnchorPane background;

	@FXML
	private VBox param1;

	@FXML
	private ImageView cancelButton;

	@FXML
	private Slider musiqueSlider;

	@FXML
	private Label musiqueLB;

	@FXML
	private Slider effetSlider;

	@FXML
	private Label effetLB;

	@FXML
	private ToggleButton clavierButton;

	@FXML
	private ToggleButton sourisButton;

	@FXML
	private TextField gauche;

	@FXML
	private TextField droite;

	@FXML
	private TextField haut;

	@FXML
	private TextField bas;

	@FXML
	private TextField rot;

	@FXML
	private TextField antirot;

	@FXML
	private TextField valider;

	@FXML
	private TextField annuler;

	@FXML
	private Label info1;

	@FXML
	private Label info2;

	@FXML
	private Label info3;

	@FXML
	private Label info4;

	@FXML
	private GridPane palette;

	@FXML
	private RadioButton parDefautRB;

	@FXML
	private RadioButton persoRB;

	@FXML
	private VBox bddPersoVB;

	@FXML
	private TextField ipTF;

	@FXML
	private TextField bddTF;

	@FXML
	private TextField userTF;

	@FXML
	private TextField mdpTF;

	private TextField currentTF;
	private boolean enAttenteDeTouche = false;
	private String[] settings;
	private int currentKey;

	private Image[] couleurs;
	private int colonneIndex;
	private int ligneIndex;
	private int numeroCouleur;

	public void initialize() {

		// Fenêtre appuie touche
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/views/AppuieTouche.fxml"));
			appuieTouche = (AnchorPane) loader.load();
			appuieTouche.getStylesheets().add(ResourceLoader.loadCss("cadrePause.css"));
			appuieTouche.getStylesheets().add(ResourceLoader.loadCss("default.css"));

			AppuieToucheController controller = loader.getController();
			controller.setParent(this);

			background.getChildren().add(appuieTouche);
			appuieTouche.setVisible(false);

			toucheController = loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// CSS
		cancelButton.getStyleClass().add("clickableButton");

		Database db = new Database();
		Table table = db.executeQueryLocal(
				"SELECT settings FROM JOUEUR WHERE Id_joueur = " + getMain().getProfilSelect().getId() + ";");

		settings = table.getLigne(0).getValeur(0).split(":");

		// ***MUSIQUE***//
		musiqueSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			int valeur = (int) ((double) newValue);
			musiqueLB.setText(valeur + "%");
			settings[1] = Integer.toString(valeur);
		});

		// ***EFFETS SONORES***//
		effetSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			int valeur = (int) ((double) newValue);
			effetLB.setText(valeur + "%");
			settings[2] = Integer.toString(valeur);
		});

		// ***CLAVIER OU SOURIS***//
		sourisButton.selectedProperty().addListener((observable, oldValue, newValue) -> {

			// Si il n'est plus sélectionné
			if (!newValue && !clavierButton.isSelected()) {

				sourisButton.setSelected(true);
			} else if (newValue && !oldValue) {
				settings[0] = "SOURIS";
				if (param1.getChildren().size() == 13) {
					for (Node node : param1.getChildren()) {
						if (param1.getChildren().indexOf(node) > 6) {
							node.setVisible(false);
						}
					}
					Label tournerPiece = new Label("Autres");
					tournerPiece.setFont(ResourceLoader.loadFont("Hogfish.ttf", 24));
					tournerPiece.setPadding(new Insets(20, 0, 0, 0));

					param1.getChildren().add(5, tournerPiece);
					((Label) param1.getChildren().get(2)).setText("Tourner la pièce");

					info1.setText("Vers la droite");
					info2.setText("Vers la gauche");
					info3.setText("Valider");
					info4.setText("Annuler");

					gauche.setText(Convert.buttonToString(settings[11]));
					droite.setText(Convert.buttonToString(settings[12]));
					haut.setText(Convert.buttonToString(settings[13]));
					bas.setText(Convert.buttonToString(settings[14]));
				}

			}
		});

		clavierButton.selectedProperty().addListener((observable, oldValue, newValue) -> {

			if (!newValue && !sourisButton.isSelected()) {
				clavierButton.setSelected(true);
			} else if (newValue && !oldValue) {
				settings[0] = "CLAVIER";
				if (param1.getChildren().size() == 14) {
					param1.getChildren().remove(5);
					for (Node node : param1.getChildren()) {
						node.setVisible(true);
					}
					((Label) param1.getChildren().get(2)).setText("Déplacer la pièce");

					info1.setText("À gauche");
					info2.setText("À droite");
					info3.setText("En haut");
					info4.setText("En bas");

					gauche.setText(Convert.codeToString(settings[3]));
					droite.setText(Convert.codeToString(settings[4]));
					haut.setText(Convert.codeToString(settings[5]));
					bas.setText(Convert.codeToString(settings[6]));
				}

			}
		});

		gauche.setOnMouseClicked(event -> {
			if (settings[0].equals("CLAVIER")) {
				afficherAppuieTouche(gauche, "Déplacer la pièce vers la gauche");
				currentKey = 3;
			} else {
				afficherAppuieTouche(gauche, "Tourner la pièce vers la droite");
				currentKey = 11;
			}

		});

		droite.setOnMouseClicked(event -> {
			if (settings[0].equals("CLAVIER")) {
				afficherAppuieTouche(droite, "Déplacer la pièce vers la droite");

				currentKey = 4;
			} else {
				afficherAppuieTouche(droite, "Tourner la pièce vers la gauche");
				currentKey = 12;
			}
		});

		haut.setOnMouseClicked(event -> {
			if (settings[0].equals("CLAVIER")) {
				afficherAppuieTouche(haut, "Déplacer la pièce vers le haut");

				currentKey = 5;
			} else {
				afficherAppuieTouche(haut, "Prendre/lâcher la pièce");
				currentKey = 13;
			}
		});

		bas.setOnMouseClicked(event -> {
			if (settings[0].equals("CLAVIER")) {
				afficherAppuieTouche(bas, "Déplacer la pièce vers le bas");

				currentKey = 6;
			} else {
				afficherAppuieTouche(bas, "Annuler la sélection de la pièce");
				currentKey = 14;
			}
		});

		rot.setOnMouseClicked(event -> {
			afficherAppuieTouche(rot, "Tourner la pièce vers la droite");

			currentKey = 7;
		});

		antirot.setOnMouseClicked(event -> {
			afficherAppuieTouche(antirot, "Tourner la pièce vers la gauche");

			currentKey = 8;
		});

		valider.setOnMouseClicked(event -> {
			afficherAppuieTouche(valider, "Prendre/lâcher la pièce");

			currentKey = 9;
		});

		annuler.setOnMouseClicked(event -> {
			afficherAppuieTouche(annuler, "Annuler la sélection de la pièce");

			currentKey = 10;
		});

		if (settings[0].equals("SOURIS")) {
			sourisButton.setSelected(true);
		} else {
			clavierButton.setSelected(true);
		}

		musiqueSlider.setValue(Double.parseDouble(settings[1]));
		effetSlider.setValue(Double.parseDouble(settings[2]));

		// Affichage des contrôles actuels
		if (settings[0].equals("CLAVIER")) {
			gauche.setText(Convert.codeToString(settings[3]));
			droite.setText(Convert.codeToString(settings[4]));
			haut.setText(Convert.codeToString(settings[5]));
			bas.setText(Convert.codeToString(settings[6]));

		} else {
			gauche.setText(Convert.buttonToString(settings[11]));
			droite.setText(Convert.buttonToString(settings[12]));
			haut.setText(Convert.buttonToString(settings[13]));
			bas.setText(Convert.buttonToString(settings[14]));
		}

		rot.setText(Convert.codeToString(settings[7]));
		antirot.setText(Convert.codeToString(settings[8]));
		valider.setText(Convert.codeToString(settings[9]));
		annuler.setText(Convert.codeToString(settings[10]));

		// PALETTE COULEUR
		couleurs = new Image[16];
		for (int i = 0; i < 8; i++) {
			couleurs[i * 2] = ResourceLoader.loadImg("couleur" + (i + 1) + ".png");
			couleurs[i * 2 + 1] = ResourceLoader.loadImg("couleur" + (i + 1) + "s.png");
		}
		resetGrille();

		numeroCouleur = getMain().getProfilSelect().getCouleur() - 1;

		((ImageView) palette.getChildren().get(numeroCouleur)).setImage(couleurs[numeroCouleur * 2 + 1]);

	}

	public void resetGrille() {
		colonneIndex = 0;
		ligneIndex = 0;

		palette.getChildren().clear();

		// PALETTE COULEUR
		for (int i = 0; i < 8; i++) {
			ImageView iv = new ImageView(couleurs[i * 2]);
			iv.setFitHeight(75);
			iv.setFitWidth(75);
			iv.getStyleClass().add("clickableButton");
			iv.setId(Integer.toString(i));
			iv.setCursor(Cursor.HAND);
			iv.setOnMouseClicked(event -> {
				numeroCouleur = Integer.parseInt(iv.getId());
				resetGrille();
				((ImageView) palette.getChildren().get(numeroCouleur)).setImage(couleurs[numeroCouleur * 2 + 1]);
			});

			palette.add(iv, colonneIndex, ligneIndex);

			if (colonneIndex == 3) {
				colonneIndex = 0;
				ligneIndex = 1;
			} else {
				colonneIndex++;
			}
		}
	}

	public void afficherAppuieTouche(TextField tf, String message) {

		if (!enAttenteDeTouche) {
			enAttenteDeTouche = true;
			currentTF = tf;

			AnchorPane.setTopAnchor(appuieTouche, background.getHeight() / 2 - appuieTouche.getHeight() / 2);
			AnchorPane.setLeftAnchor(appuieTouche, background.getWidth() / 2 - appuieTouche.getWidth() / 2);
			appuieTouche.setVisible(true);
			toucheController.setMessage(message);

			BoxBlur effet = new BoxBlur();
			effet.setWidth(20);
			effet.setHeight(20);
			effet.setIterations(2);

			for (Node node : background.getChildren()) {
				if (node != appuieTouche) {
					node.setEffect(effet);
				}
			}
			gauche.setCursor(Cursor.DEFAULT);
			droite.setCursor(Cursor.DEFAULT);
			haut.setCursor(Cursor.DEFAULT);
			bas.setCursor(Cursor.DEFAULT);
			rot.setCursor(Cursor.DEFAULT);
			antirot.setCursor(Cursor.DEFAULT);
			valider.setCursor(Cursor.DEFAULT);
			annuler.setCursor(Cursor.DEFAULT);
			cancelButton.setCursor(Cursor.DEFAULT);
			sourisButton.setCursor(Cursor.DEFAULT);
			clavierButton.setCursor(Cursor.DEFAULT);
			sourisButton.setDisable(true);
			clavierButton.setDisable(true);
			musiqueSlider.setDisable(true);
			effetSlider.setDisable(true);
			cancelButton.getStyleClass().remove("clickableButton");
		}

	}

	public void pressKey(KeyEvent event) {
		if (enAttenteDeTouche && settings[0].equals("CLAVIER")) {
			if (toucheController.isValideKey(event)) {
				settings[currentKey] = event.getCode().toString();
			}
		}
	}

	public void pressMouse(MouseEvent event) {
		// Si l'user clique sur la croix
		if (event.getTarget() instanceof ImageView) {
			if (((ImageView) event.getTarget()).getId().equals("closeButton")) {
				return;
			}
		}

		if (enAttenteDeTouche && settings[0].equals("SOURIS")) {
			if (toucheController.isValideMouse(event)) {
				settings[currentKey] = event.getButton().toString();
			}
		}
	}

	public void scrollMouse(ScrollEvent event) {
		if (enAttenteDeTouche && settings[0].equals("SOURIS")) {
			if (event.getDeltaY() > 0) {
				currentTF.setText("Molette vers le haut");
				settings[currentKey] = "SCROLL_UP";
			} else {
				currentTF.setText("Molette vers le bas");
				settings[currentKey] = "SCROLL_DOWN";
			}
			fermerAppuieTouche();
		}
	}

	public void validerAppuieTouche(String nomTouche) {
		currentTF.setText(nomTouche);

		fermerAppuieTouche();

	}

	public void fermerAppuieTouche() {

		enAttenteDeTouche = false;
		currentTF = null;

		appuieTouche.setVisible(false);

		for (Node node : background.getChildren()) {
			if (node != appuieTouche) {
				node.setEffect(null);
			}
		}

		gauche.setCursor(Cursor.HAND);
		droite.setCursor(Cursor.HAND);
		haut.setCursor(Cursor.HAND);
		bas.setCursor(Cursor.HAND);
		rot.setCursor(Cursor.HAND);
		antirot.setCursor(Cursor.HAND);
		valider.setCursor(Cursor.HAND);
		annuler.setCursor(Cursor.HAND);
		cancelButton.setCursor(Cursor.HAND);
		sourisButton.setCursor(Cursor.HAND);
		clavierButton.setCursor(Cursor.HAND);
		sourisButton.setDisable(false);
		clavierButton.setDisable(false);
		musiqueSlider.setDisable(false);
		effetSlider.setDisable(false);
		cancelButton.getStyleClass().add("clickableButton");
	}

	@FXML
	public void clickRetour() {
		Database db = new Database();
		db.executeUpdateLocal("UPDATE JOUEUR SET settings = '" + settings[0] + ":" + settings[1] + ":" + settings[2]
				+ ":" + settings[3] + ":" + settings[4] + ":" + settings[5] + ":" + settings[6] + ":" + settings[7]
				+ ":" + settings[8] + ":" + settings[9] + ":" + settings[10] + ":" + settings[11] + ":" + settings[12]
				+ ":" + settings[13] + ":" + settings[14] + "' WHERE id_joueur = " + getMain().getProfilSelect().getId()
				+ ";");
		db.executeUpdateLocal("UPDATE JOUEUR SET id_couleur = " + (numeroCouleur + 1) + " WHERE id_joueur = "
				+ getMain().getProfilSelect().getId());

		getMain().getProfilSelect().setSettings(settings);

		if (parent.equals("accueil")) {
			getMain().afficherMenuAcceuil();
		} else {
			getMain().afficherChoixModeJeu();
		}

	}

	@FXML
	public void clickClassementRadioButton() {
		if (persoRB.isSelected()) {
			bddPersoVB.setDisable(false);
		} else {
			bddPersoVB.setDisable(true);
		}
	}

	@FXML
	public void clickTesterButton() {
		String ip = ipTF.getText();
		String bdd = bddTF.getText();
		String user = userTF.getText();
		String mdp = mdpTF.getText();

		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://+" + ip + "/" + bdd, user, mdp);
			System.out.println("Connexion réussie.");
		} catch (SQLException e) {
			System.out.println("Erreur !");
		}

	}

	public void setParent(String parent) {
		this.parent = parent;
	}

}
