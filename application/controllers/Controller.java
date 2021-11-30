package application.controllers;

import application.main.Main;
import javafx.animation.RotateTransition;
import javafx.util.Duration;

public class Controller {

	private static Main main;
	private boolean enChargement = false;

	public RotateTransition getAnimation() {
		RotateTransition animation = new RotateTransition();
		animation.setDuration(Duration.millis(1250));
		animation.setFromAngle(0);
		animation.setToAngle(-360);
		animation.setCycleCount(8);

		return animation;
	}

	/**
	 * Permet de définir la classe principale de l'application.
	 *
	 * @param main Classe Main de l'application.
	 */
	public void setMain(Main main) {
		Controller.main = main;
	}

	public Main getMain() {
		return main;
	}

	public boolean isEnChargement() {
		return enChargement;
	}

	public void setEnChargement(boolean enChargement) {
		this.enChargement = enChargement;
	}

}
