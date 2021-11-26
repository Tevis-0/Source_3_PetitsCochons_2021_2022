package application.controllers;

import java.io.IOException;

import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utils.ResourceLoader;
import utils.database.Database;
import utils.database.Table;

public class MagasinController extends Controller {

	@FXML
	private BarreJoueurController barreJoueurController;

	private CadreMessageOuiNonController popupController;

	public final static int NB_SKINS = 2;

	@FXML
	private GridPane shop;

	@FXML
	private AnchorPane background;
	
	@FXML
	private ImageView retourButton;

	private AnchorPane popup;
	
	private boolean enAchat = false;
	private String skinAchat;

	public void initialize() {
		
		retourButton.getStyleClass().add("clickableButton");
		// Pause
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/views/CadreMessageOuiNon.fxml"));
			popup = (AnchorPane) loader.load();
			popup.getStylesheets().add(ResourceLoader.loadCss("cadrePause.css"));
			popup.getStylesheets().add(ResourceLoader.loadCss("default.css"));

			popupController = loader.getController();
			popupController.setParent(this);

		} catch (IOException e) {
			e.printStackTrace();
		}

		background.getChildren().add(popup);
		popupController.setParent(this);
		popup.setPrefHeight(400);
		popup.setPrefWidth(400);
		popup.setVisible(false);

	}

	@FXML
	public void clickSkin(MouseEvent event) {
		
		if (!enAchat){
			enAchat = true;
			EventTarget target = (ImageView) event.getTarget();
			String id = ((ImageView) target).getId();

			ImageView cadenas = ((ImageView) ((HBox) ((VBox) ((ImageView) target).getParent()).getChildren().get(1))
					.getChildren().get(1));

			System.out.println(background.getHeight());
			AnchorPane.setTopAnchor(popup, background.getHeight() / 2 - popup.getHeight() / 2);
			AnchorPane.setLeftAnchor(popup, background.getWidth() / 2 - popup.getWidth() / 2);
			popupController.changerMessage("Voulez-vous acheter ce cosmétique ?");
			
			popup.setVisible(true);

			
			if (id.equals("defaut")) {

			} else if (id.equals("boueux")) {
				skinAchat = "boueux";
				

			} else if (id.equals("hiver")) {
				skinAchat = "hiver";

			} else if (id.equals("halloween")) {
				skinAchat = "halloween";

			}
		}

		
	}

	public void cancelAchat(){
		popup.setVisible(false);
		enAchat = false;
	}
	
	public void confirmAchat(){
		popup.setVisible(false);
		enAchat = false;
	}
	
	@FXML
	public void clickRetour() {
		if (!isEnChargement()) {
			setEnChargement(true);
			getMain().afficherMenuAcceuil();
		}
	}

	public BarreJoueurController getBarreJoueurController() {
		return barreJoueurController;
	}

}
