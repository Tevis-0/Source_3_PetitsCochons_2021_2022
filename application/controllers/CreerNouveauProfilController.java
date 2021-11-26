package application.controllers;

import application.models.Profil;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import utils.ResourceLoader;
import utils.database.Database;
import utils.database.Table;

public class CreerNouveauProfilController extends Controller {

	private ChoixProfilController parent;

	@FXML
	private ImageView closeButton;

	@FXML
	private Label creerProfilButton;

	@FXML
	private Label tonPseudoLB;

	@FXML
	private TextField pseudoTF;

	@FXML
	private GridPane palette;

	@FXML
	private Label errorLB;

	@FXML
	private ImageView loading;

	private Image[] couleurs;
	private int colonneIndex;
	private int ligneIndex;
	private int numeroCouleur;

	@FXML
	public void initialize() {

		creerProfilButton.getParent().getStyleClass().add("clickableButton");
		closeButton.getStyleClass().add("clickableButton");
		errorLB.setVisible(false);
		loading.setVisible(false);

		// POLICES
		errorLB.setFont(ResourceLoader.loadFont("Hogfish.ttf", 20));
		creerProfilButton.setFont(ResourceLoader.loadFont("Grobold.ttf", 37));
		pseudoTF.setFont(ResourceLoader.loadFont("Grobold.ttf", 16));
		tonPseudoLB.setFont(ResourceLoader.loadFont("Grobold.ttf", 16));

		// LISTENER
		pseudoTF.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.isEmpty() || newValue.trim().length() == 0) {
				creerProfilButton.getParent().setOpacity(0.5);
			} else {
				creerProfilButton.getParent().setOpacity(1);
				errorLB.setVisible(false);
			}

			if (newValue.length() > 15) {
				pseudoTF.setText(oldValue);
			}
		});

		// PALETTE COULEUR
		couleurs = new Image[16];
		for (int i = 0; i < 8; i++) {
			couleurs[i * 2] = ResourceLoader.loadImg("couleur" + (i + 1) + ".png");
			couleurs[i * 2 + 1] = ResourceLoader.loadImg("couleur" + (i + 1) + "s.png");
		}
		resetGrille();
		((ImageView) palette.getChildren().get(0)).setImage(couleurs[1]);
		numeroCouleur = 0;

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

	public void init() {
		pseudoTF.clear();
		creerProfilButton.getParent().setOpacity(0.5);
	}

	@FXML
	public void clickCreerProfil() {

		if (!isEnChargement()) {
			String pseudo = pseudoTF.getText();

			// Si le pseudo est vide
			if (pseudo.isEmpty()) {
				errorLB.setText("Le pseudo ne peut pas être vide.");
				errorLB.setVisible(true);

			} else {
				String newPseudo = pseudo.replace("'", "''");
				// On vérifie que le pseudo n'est pas déjà utilisé (en ligne et
				// local)
				Database db = new Database();
				Table table = db
						.executeQueryLocal("SELECT id_joueur, pseudo_joueur FROM JOUEUR WHERE lower(pseudo_joueur) = '"
								+ newPseudo.toLowerCase() + "';");

				Database dbOnline = new Database();
				Table tableO = null;

				boolean dbCanConnectOnline = dbOnline.canConnectOnline();

				if (dbCanConnectOnline) {
					tableO = dbOnline
							.executeQueryOnline("SELECT id_jou, pseudo_jou FROM JOUEUR WHERE lower(pseudo_jou) = '"
									+ newPseudo.toLowerCase() + "';");
				}

				// Si le pseudo existe déjà (en local ou en ligne)
				if (!table.isVide() || (tableO != null && !tableO.isVide())) {
					errorLB.setText("Le pseudo est déjà pris. Prends-en un autre.");
					errorLB.setVisible(true);
				} else {
					setEnChargement(true);
					RotateTransition anim = getAnimation();
					anim.setNode(loading);
					anim.play();
					loading.setVisible(true);

					int id_online = -1;
					System.out.println("Online:" + dbCanConnectOnline);
					// Création du profil en ligne
					if (dbCanConnectOnline) {
						db.executeUpdateOnline("INSERT INTO JOUEUR VALUES (NULL, '" + newPseudo + "', 1);");
						id_online = Integer
								.parseInt(db
										.executeQueryOnline(
												"SELECT id_jou FROM JOUEUR WHERE Pseudo_jou = '" + newPseudo + "';")
										.getLigne(0).getValeur(0));
					}

					// Création du profil en local
					db.executeUpdateLocal("INSERT INTO JOUEUR VALUES (NULL, " + id_online + ", '" + newPseudo
							+ "', 1, 0, 'SOURIS:100:100:LEFT:RIGHT:UP:DOWN:ALT:CONTROL:"
							+ "ENTER:BACK_SPACE:SCROLL_UP:SCROLL_DOWN:PRIMARY:SECONDARY', " + (numeroCouleur + 1)
							+ ");");

					// Création du profil dans le main
					table = db.executeQueryLocal(
							"SELECT id_joueur, settings FROM JOUEUR WHERE pseudo_joueur = '" + newPseudo + "';");
					int id = Integer.parseInt(table.getLigne(0).getValeur(0));
					getMain().setProfilSelect(new Profil(id, pseudo, 1, 0, table.getLigne(0).getValeur(1).split(":"),
							id_online, numeroCouleur + 1));

					getMain().afficherMenuAcceuil();
				}

			}
		}

	}

	@FXML
	public void clickCloseButton() {
		parent.annulerCreationProfil();
	}

	public void setParent(ChoixProfilController parent) {
		this.parent = parent;
	}

}
