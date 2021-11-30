package application.controllers;

import application.models.Contexte;
import application.models.Niveau;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class MenuAcceuilController extends Controller {

	@FXML
	private BarreJoueurController barreJoueurController;

	@FXML
	private AnchorPane jouerBouton;

	@FXML
	private AnchorPane magasinBouton;

	@FXML
	private AnchorPane classementBouton;

	@FXML
	private AnchorPane parametreBouton;

	@FXML
	private AnchorPane deconnexionBouton;

	@FXML
	private AnchorPane background;

	@FXML
	private ImageView decoButton;

	@FXML
	private ImageView trophee;

	@FXML
	private ImageView shop;

	ScaleTransition anim;

	@FXML
	public void initialize() {

		jouerBouton.getStyleClass().add("clickableButton");
		magasinBouton.getStyleClass().add("clickableButton");
		classementBouton.getStyleClass().add("clickableButton");
		parametreBouton.getStyleClass().add("clickableButton");
		decoButton.getStyleClass().add("clickableButton");

		shop.setTranslateX(60);

		anim = new ScaleTransition(Duration.millis(300));
		anim.setToX(1.3);
		anim.setToY(1.3);
		anim.setAutoReverse(true);
		anim.setCycleCount(Timeline.INDEFINITE);

	}

	@FXML
	public void enterClassement() {
		anim.setNode(trophee);
		anim.play();
	}

	@FXML
	public void exitClassement() {
		anim.stop();
		trophee.setScaleX(1);
		trophee.setScaleY(1);
	}

	@FXML
	public void enterShop() {
		anim.setNode(shop);
		anim.play();
	}

	@FXML
	public void exitShop() {
		anim.stop();
		shop.setScaleX(1);
		shop.setScaleY(1);
	}

	@FXML
	public void clickJouer() {
		if (!isEnChargement()) {
			setEnChargement(true);
			getMain().afficherChoixModeJeu();
		}
	}
	
	@FXML
	public void clickMagasin(){
		if (!isEnChargement()) {
			setEnChargement(true);
			getMain().afficherMagasin();
		}
	}

	@FXML
	public void clickClassement() {
		if (!isEnChargement()) {
			setEnChargement(true);
			getMain().afficherClassement(false, Contexte.DIURNE, Niveau.STARTER);
		}

	}

	@FXML
	public void clickParametres() {
		if (!isEnChargement()) {
			setEnChargement(true);
			getMain().afficherParametres("accueil");
		}
	}

	@FXML
	public void clickDeco() {
		if (!isEnChargement()) {
			setEnChargement(true);
			getMain().afficherMenuChoixProfil();
		}
	}

	public BarreJoueurController getBarreJoueurController() {
		return barreJoueurController;
	}
}
