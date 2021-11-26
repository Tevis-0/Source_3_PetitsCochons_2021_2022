package application.controllers;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import utils.Convert;

public class AppuieToucheController extends Controller {

	private ParametresController parent;

	@FXML
	private Label error;

	@FXML
	private Label messageLB;

	@FXML
	private ImageView closeButton;

	public void initialize() {
		closeButton.getStyleClass().add("clickableButton");
		error.setVisible(false);

	}

	public boolean isValideKey(KeyEvent event) {

		if (String.valueOf(event.isAltDown()).contains("true")) {
			afficherErreur();
			return false;
		} else if (event.getCode().toString().equals("ESCAPE")) {
			clickCloseButton();
			return false;
		} else {
			String txt = Convert.codeToString(event.getCode().toString());
			if (txt == null) {
				afficherErreur();
				return false;
			} else {
				validerTouche(txt);
				return true;
			}
		}
	}

	public boolean isValideMouse(MouseEvent event) {
		String txt = Convert.buttonToString(event.getButton().toString());
		if (txt != null) {
			validerTouche(txt);
			return true;
		} else {
			afficherErreur();
			return false;
		}
	}

	@FXML
	public void clickCloseButton() {
		parent.fermerAppuieTouche();
		error.setVisible(false);
	}

	public void validerTouche(String nomTouche) {
		parent.validerAppuieTouche(nomTouche);
		error.setVisible(false);
	}

	public void afficherErreur() {

		ScaleTransition anim = new ScaleTransition(Duration.millis(100), error);
		anim.setToX(1.2);
		anim.setToY(1.2);
		anim.setAutoReverse(true);
		anim.setCycleCount(2);
		error.setVisible(true);
		anim.play();
		anim.setOnFinished(event -> {
			error.setScaleX(1);
			error.setScaleY(1);
		});
	}

	public void setMessage(String message) {
		messageLB.setText(message);
	}

	public void setParent(ParametresController parent) {
		this.parent = parent;
	}

}
