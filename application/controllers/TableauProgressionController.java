package application.controllers;

import application.models.Contexte;
import application.models.Mode;
import application.models.Niveau;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import utils.ResourceLoader;
import utils.database.Database;
import utils.database.Ligne;
import utils.database.Table;

public class TableauProgressionController extends Controller {

	@FXML
	private BarreJoueurController barreJoueurController;

	@FXML
	private AnchorPane background;

	@FXML
	private GridPane tableau;

	@FXML
	private VBox infosVB;

	@FXML
	private ImageView diurneButton;

	@FXML
	private ImageView nocturneButton;

	@FXML
	private Label recordLB;

	@FXML
	private ImageView loading;

	@FXML
	private ImageView retourButton;

	private Image cadenas;
	private Image diurneNoSelect;
	private Image diurneSelect;
	private Image nocturneNoSelect;
	private Image nocturneSelect;
	private Image check;

	private int[] defiReussis;
	private double[] records;
	private Contexte contexte;

	public void initialize() {
		defiReussis = new int[8];
		records = new double[48];
		contexte = Contexte.DIURNE;

		loading.setVisible(false);

		// IMAGES
		cadenas = ResourceLoader.loadImg("cadenas.png");
		diurneNoSelect = ResourceLoader.loadImg("diurne_nonselect.png");
		diurneSelect = ResourceLoader.loadImg("diurne_select.png");
		nocturneNoSelect = ResourceLoader.loadImg("nocturne_nonselect.png");
		nocturneSelect = ResourceLoader.loadImg("nocturne_select.png");
		check = ResourceLoader.loadImg("check.png");

		// CSS
		diurneButton.getStyleClass().add("clickableButton");
		nocturneButton.getStyleClass().add("clickableButton");
		retourButton.getStyleClass().add("clickableButton");

		// On récupère toutes les informations
		int id = getMain().getProfilSelect().getId();
		Database db = new Database();

		Table table = db
				.executeQueryLocal("SELECT contexte, difficulte, numerodefi, record FROM PROGRESSION WHERE id_joueur = "
						+ getMain().getProfilSelect().getId() + ";");

		// Init des records
		for (int i = 0; i < 48; i++) {
			records[i] = -1;
		}

		for (Ligne ligne : table.getLignes()) {
			int numeroDefi = 0;

			// Contexte
			String valeur = ligne.getValeur(0);
			if (valeur.equals("NOCTURNE")) {
				numeroDefi += 24;
			}

			// Difficulte
			valeur = ligne.getValeur(1);
			if (valeur.equals("JUNIOR")) {
				numeroDefi += 6;
			} else if (valeur.equals("EXPERT")) {
				numeroDefi += 12;
			} else if (valeur.equals("MASTER")) {
				numeroDefi += 18;
			}

			// Niveau
			valeur = ligne.getValeur(2);
			numeroDefi += Integer.parseInt(valeur);

			records[numeroDefi - 1] = Double.parseDouble(ligne.getValeur(3));

		}

		// Init du nombre de défis réussis
		for (int i = 0; i < 8; i++) {
			int nbDefiReussi = 0;
			for (int k = 0; k < 6; k++) {
				if (records[i * 6 + k] != -1) {
					nbDefiReussi++;
				}
			}
			defiReussis[i] = nbDefiReussi;
		}
	}

	public void initialiserBoutons() {
		clearTableau();

		// Pour chaque difficulté
		for (int i = 0; i < 4; i++) {
			// Pour chaque niveau
			for (int k = 1; k <= 6; k++) {
				AnchorPane button = new AnchorPane();
				button.setStyle("-fx-background-image : url('ressources/images/progress_niveau.png');"
						+ "-fx-background-size : cover;");

				button.getStyleClass().add("clickableButton");
				button.setCursor(Cursor.HAND);

				Label numeroNiveau = new Label(Integer.toString(k));
				numeroNiveau.setFont(ResourceLoader.loadFont("Grobold.ttf", 55));
				numeroNiveau.setLayoutX(36);
				numeroNiveau.setLayoutY(16);
				button.getChildren().add(numeroNiveau);

				// Création d'un cadenas, si le niveau n'est pas accessible

				int numLigne;
				if (contexte == Contexte.DIURNE) {
					numLigne = 0;
				} else {
					numLigne = 4;
				}

				if (i != 0 && defiReussis[numLigne + i - 1] < 3) {
					ImageView cadenasIV = new ImageView(cadenas);
					cadenasIV.setFitHeight(51);
					cadenasIV.setFitWidth(37);
					cadenasIV.setRotate(25);
					button.getChildren().add(cadenasIV);
					cadenasIV.setTranslateX(75);
					cadenasIV.setTranslateY(-5);

					button.setOpacity(0.8);

					button.setOnMouseClicked(event -> {
						TranslateTransition animation = new TranslateTransition(Duration.millis(40), button);
						animation.setToX(10);
						animation.setAutoReverse(true);
						animation.setCycleCount(4);
						animation.play();

						animation.setOnFinished(event2 -> {
							button.setTranslateX(0);
						});
					});
				} else {

					// On regarde si le niveau a déjà été résolu
					int numContexte;
					if (contexte == Contexte.DIURNE) {
						numContexte = 0;
					} else {
						numContexte = 24;
					}

					// Record du défi (existe s'il a déjà été résolu)
					double record = records[numContexte + i * 6 + k - 1];

					if (record != -1) {
						ImageView checkIV = new ImageView(check);
						checkIV.setFitHeight(60);
						checkIV.setFitWidth(54);
						button.getChildren().add(checkIV);
						checkIV.setTranslateX(50);
						checkIV.setTranslateY(50);

					}

					// Event click button
					button.setOnMouseClicked(event -> {
						if (!isEnChargement()) {
							setEnChargement(true);
							RotateTransition animation = getAnimation();
							animation.setNode(loading);
							animation.play();
							loading.setVisible(true);

							int l = GridPane.getRowIndex(button);
							int c = GridPane.getColumnIndex(button);

							Niveau niveau = null;
							if (l == 0) {
								niveau = Niveau.STARTER;
							} else if (l == 1) {
								niveau = Niveau.JUNIOR;
							} else if (l == 2) {
								niveau = Niveau.EXPERT;
							} else {
								niveau = Niveau.MASTER;
							}

							DropShadow effet = new DropShadow();
							effet.setColor(Color.web("#ff7700"));
							effet.setWidth(100);
							effet.setHeight(100);
							button.setEffect(effet);

							getMain().afficherJeu(Mode.PROGRESSION, contexte, niveau, c, this);
						}
					});
				}

				// Event record
				button.setOnMouseEntered(event -> {

					int l = GridPane.getRowIndex(button);
					int c = GridPane.getColumnIndex(button);

					int numContexte;
					if (contexte == Contexte.DIURNE) {
						numContexte = 0;
					} else {
						numContexte = 24;
					}

					// Record du défi
					double record = records[numContexte + l * 6 + c - 1];

					// Si l'utilisateur n'a pas de record sur ce défi
					if (record == -1) {
						recordLB.setFont(ResourceLoader.loadFont("Grobold.ttf", 25));
						recordLB.setText("Pas de record");
					} else {
						recordLB.setFont(ResourceLoader.loadFont("Grobold.ttf", 60));
						recordLB.setText(Double.toString(record) + "s.");
					}

				});

				button.setOnMouseExited(event -> {
					recordLB.setFont(ResourceLoader.loadFont("Grobold.ttf", 25));
					recordLB.setText("-");
				});

				tableau.add(button, k, i);
			}
		}

	}

	@FXML
	public void clickDiurne() {
		setContexte(Contexte.DIURNE);
		initialiserBoutons();
	}

	@FXML
	public void clickNocturne() {
		setContexte(Contexte.NOCTURNE);
		initialiserBoutons();
	}

	public void setContexte(Contexte contexte) {
		this.contexte = contexte;
		if (contexte == Contexte.DIURNE) {
			background.getStylesheets().clear();
			background.getStylesheets().add(ResourceLoader.loadCss("progression.css"));
			background.getStylesheets().add(ResourceLoader.loadCss("diurne.css"));
			diurneButton.setImage(diurneSelect);
			nocturneButton.setImage(nocturneNoSelect);

			for (Node node : infosVB.getChildren()) {
				if (node instanceof Label) {
					((Label) node).setTextFill(Color.BLACK);
				}
			}
		} else {
			background.getStylesheets().clear();
			background.getStylesheets().add(ResourceLoader.loadCss("progression.css"));
			background.getStylesheets().add(ResourceLoader.loadCss("nocturne.css"));
			diurneButton.setImage(diurneNoSelect);
			nocturneButton.setImage(nocturneSelect);

			for (Node node : infosVB.getChildren()) {
				if (node instanceof Label) {
					((Label) node).setTextFill(Color.WHITE);
				}
			}
		}
	}

	public void clearTableau() {

		ObservableList<Node> temp = FXCollections.observableArrayList();

		// On récupère tous les éléments à supprimer
		for (Node node : tableau.getChildren()) {
			if (node instanceof AnchorPane) {
				temp.add(node);
			}
		}

		tableau.getChildren().removeAll(temp);
	}

	@FXML
	public void clickRetour() {
		if (!isEnChargement()) {
			setEnChargement(true);
			getMain().afficherChoixModeJeu();
		}
	}

	public BarreJoueurController getBarreJoueurController() {
		return barreJoueurController;
	}
}
