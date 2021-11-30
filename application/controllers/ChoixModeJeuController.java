package application.controllers;

import application.models.Contexte;
import application.models.Mode;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class ChoixModeJeuController extends Controller {

	@FXML
	private BarreJoueurController barreJoueurController;

	@FXML
	private AnchorPane entrainementButton;

	@FXML
	private AnchorPane progressionButton;

	@FXML
	private AnchorPane competitionButton;

	@FXML
	private AnchorPane parametreBouton;

	@FXML
	private ImageView retourButton;

	@FXML
	private ImageView decoButton;

	@FXML
	public void initialize() {
		entrainementButton.getStyleClass().add("clickableButton");
		progressionButton.getStyleClass().add("clickableButton");
		competitionButton.getStyleClass().add("clickableButton");
		parametreBouton.getStyleClass().add("clickableButton");
		retourButton.getStyleClass().add("clickableButton");
		decoButton.getStyleClass().add("clickableButton");
	}

	@FXML
	private void clickRetour() {
		if (!isEnChargement()) {
			setEnChargement(true);
			getMain().afficherMenuAcceuil();
		}
	}

	@FXML
	public void clickEntrainement() {
		if (!isEnChargement()) {
			setEnChargement(true);
			getMain().afficherMenuContexteNiveau(Mode.ENTRAINEMENT);
		}
	}

	@FXML
	public void clickProgression() {
		if (!isEnChargement()) {
			setEnChargement(true);
			getMain().afficherTableauProgression(Contexte.DIURNE);
		}
	}

	@FXML
	public void clickCompetition() {
		if (!isEnChargement()) {
			setEnChargement(true);
			getMain().afficherMenuContexteNiveau(Mode.COMPETITION);
		}
	}

	@FXML
	public void clickSettings() {
		if (!isEnChargement()) {
			setEnChargement(true);
			getMain().afficherParametres("choixMode");
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
		// TODO Auto-generated method stub
		return barreJoueurController;
	}
}
