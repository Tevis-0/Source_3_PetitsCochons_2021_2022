package application.controllers;

import java.net.MalformedURLException;

import application.models.Contexte;
import application.models.Mode;
import application.models.Niveau;
import application.models.Profil;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import utils.ResourceLoader;

public class SelectNiveauController extends Controller {

	@FXML
	private BarreJoueurController barreJoueurController;

	@FXML
	private AnchorPane background;

	@FXML
	private ImageView diurneButton;

	@FXML
	private ImageView nocturneButton;

	@FXML
	private Label jouerButton;

	@FXML
	private ImageView roue;

	@FXML
	private ImageView aiguille;

	@FXML
	private ImageView loadImg;

	@FXML
	private ImageView retourButton;

	private static Image diurneNoSelect;
	private static Image diurneSelect;
	private static Image nocturneNoSelect;
	private static Image nocturneSelect;

	private boolean roueFocus;
	private double angle;
	private Contexte contexte;
	private Niveau niveau;
	private Mode mode;

	@FXML
	public void initialize() throws MalformedURLException {

		// ********* RESSOURCES IMAGES *********//

		diurneNoSelect = ResourceLoader.loadImg("diurne_nonselect.png");
		diurneSelect = ResourceLoader.loadImg("diurne_select.png");
		nocturneNoSelect = ResourceLoader.loadImg("nocturne_nonselect.png");
		nocturneSelect = ResourceLoader.loadImg("nocturne_select.png");

		diurneButton.setImage(diurneSelect);

		// ********* AFFICHAGE *********//

		// CSS
		background.getStylesheets().add(ResourceLoader.loadCss("diurne.css"));
		jouerButton.getParent().getStyleClass().add("clickableButton");
		diurneButton.getStyleClass().add("clickableButton");
		nocturneButton.getStyleClass().add("clickableButton");
		retourButton.getStyleClass().add("clickableButton");

		Rotate rotate = new Rotate();
		rotate.setPivotX(28);
		rotate.setPivotY(161);
		rotate.axisProperty().setValue(Rotate.Z_AXIS);
		rotate.setAngle(-45);
		aiguille.getTransforms().add(rotate);
		angle = -45;

		loadImg.setVisible(false);

		// Polices
		Font grobold;

		grobold = ResourceLoader.loadFont("Grobold.ttf", 40);
		jouerButton.setFont(grobold);

		contexte = Contexte.DIURNE;
		niveau = Niveau.STARTER;
	}

	@FXML
	public void clickButton(MouseEvent event) {
		ImageView button = (ImageView) event.getTarget();

		if (button == diurneButton) {
			button.setImage(diurneSelect);
			nocturneButton.setImage(nocturneNoSelect);
			background.getStylesheets().clear();
			background.getStylesheets().add(ResourceLoader.loadCss("diurne.css"));
			contexte = Contexte.DIURNE;
		} else if (button == nocturneButton) {
			button.setImage(nocturneSelect);
			diurneButton.setImage(diurneNoSelect);
			background.getStylesheets().clear();
			background.getStylesheets().add(ResourceLoader.loadCss("nocturne.css"));
			contexte = Contexte.NOCTURNE;
		}
	}

	@FXML
	public void clickJouer() {

		if (!isEnChargement()) {
			setEnChargement(true);
			RotateTransition anim = getAnimation();
			anim.setNode(loadImg);
			anim.play();

			getMain().afficherJeu(mode, contexte, niveau, -1, this);
			loadImg.setVisible(true);
		}
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
			rotate.setPivotX(28);
			rotate.setPivotY(161);
			rotate.axisProperty().setValue(Rotate.Z_AXIS);

			Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(rotate.angleProperty(), 0)),
					new KeyFrame(Duration.millis(250 * Math.abs(newAngle) / 180),
							new KeyValue(rotate.angleProperty(), newAngle)));

			aiguille.getTransforms().add(rotate);
			timeline.play();
		}
	}

	public BarreJoueurController getBarreJoueurController() {
		return barreJoueurController;
	}

	@FXML
	public void clickRetour() {
		if (!isEnChargement()) {
			setEnChargement(true);
			getMain().afficherChoixModeJeu();
		}
	}

	public void setProfil(Profil profil) {
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public Mode getMode() {
		return mode;
	}

}
