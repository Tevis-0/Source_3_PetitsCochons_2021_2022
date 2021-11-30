package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CadrePauseController extends Controller {

	private JeuController parent;

	@FXML
	private Label reprendreLabel;

	@FXML
	private Label optionsLabel;

	@FXML
	private Label quitterLabel;

	@FXML
	public void initialize() {

		reprendreLabel.getParent().getStyleClass().add("clickableButton");
		optionsLabel.getParent().getStyleClass().add("clickableButton");
		quitterLabel.getParent().getStyleClass().add("clickableButton");
	}

	@FXML
	public void clickReprendreButton() {
		parent.clickReprendreButton();
	}

	@FXML
	public void clickOptionsButton() {

	}

	@FXML
	public void clickQuitterButton() {
		parent.clickQuitterButton();
	}

	public void setParent(JeuController parent) {
		this.parent = parent;
	}

}
