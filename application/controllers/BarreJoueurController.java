package application.controllers;

import application.models.Profil;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import utils.ResourceLoader;

public class BarreJoueurController extends Controller {

	@FXML
	private Label nomJoueur;
	@FXML
	private Label levelJoueur;
	@FXML
	private Rectangle barreJoueur;
	@FXML
	private ImageView panneau;
	@FXML
	private Label titre;
	@FXML
	private Label nombreEXP;

	private Font grobold;
	private Profil profil;

	@FXML
	private void initialize() {

		grobold = ResourceLoader.loadFont("Grobold.ttf", 40);
		nomJoueur.setFont(grobold);
		levelJoueur.setFont(grobold);

		titre.setVisible(false);
	}

	public void chargerProfil(Profil profil) {
		this.profil = profil;
		nomJoueur.setText(profil.getPseudo());
		levelJoueur.setText("Niv. " + profil.getNiveau());
		nombreEXP.setText(profil.getExperience() + "/" + profil.getExperienceMax() + " EXP.");
		double calc = (double) profil.getExperience() / (double) profil.getExperienceMax();
		barreJoueur.setWidth(calc * 565);
	}

	// Animation pour le gain d'expérience
	public void gainExperience(int experienceGagnee) {
		profil.ajouterExperience(experienceGagnee);
		double calc = (double) profil.getExperience() / (double) profil.getExperienceMax();

		Timeline passerNiveau = new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(barreJoueur.widthProperty(), barreJoueur.getWidth())),
				new KeyFrame(Duration.millis(500), new KeyValue(barreJoueur.widthProperty(), 565)));

		Timeline timeline = new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(barreJoueur.widthProperty(), barreJoueur.getWidth())),
				new KeyFrame(Duration.millis(500), new KeyValue(barreJoueur.widthProperty(), calc * 565)));

		if (profil.getNouveauNiveau() - profil.getAncienNiveau() != 0) {
			int expRestant = profil.getExperience();
			for (int i = 0; i < (profil.getNouveauNiveau() - profil.getAncienNiveau()); i++) {
				passerNiveau.play();
				levelJoueur.setText("Niv. " + (profil.getNiveau()));
			}

			experienceGagnee = expRestant;

			if (experienceGagnee > 0) {
				double calculReste = (double) experienceGagnee / (double) profil.getExperienceMax();
				Timeline reste = new Timeline(
						new KeyFrame(Duration.ZERO, new KeyValue(barreJoueur.widthProperty(), barreJoueur.getWidth())),
						new KeyFrame(Duration.millis(500),
								new KeyValue(barreJoueur.widthProperty(), calculReste * 565)));

				passerNiveau.setOnFinished(e -> {
					reste.play();
				});

			}

		} else {
			timeline.play();
		}

		nombreEXP.setText(profil.getExperience() + "/" + profil.getExperienceMax() + " EXP.");

	}

	public void setLogo() {
		panneau.setImage(ResourceLoader.loadImg("logo.png"));
		this.titre.setVisible(false);
	}

	public void setPanneau(String titre) {
		panneau.setImage(ResourceLoader.loadImg("panneau.png"));

		this.titre.setText(titre);
		this.titre.setVisible(true);
	}

}
