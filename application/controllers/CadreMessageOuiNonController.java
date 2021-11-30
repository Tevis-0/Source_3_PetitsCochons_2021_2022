package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import utils.ResourceLoader;

public class CadreMessageOuiNonController extends Controller {

	private Controller parent;
	
	@FXML
	private AnchorPane cadre;
	
	@FXML
	private Label ouiButton;
	
	@FXML
	private Label nonButton;
	
	@FXML
	private Label message;
	
	
	public void initialize(){
		ouiButton.getParent().getStyleClass().add("clickableButton");
		nonButton.getParent().getStyleClass().add("clickableButton");
		message.setFont(ResourceLoader.loadFont("Hogfish.ttf", 30));
	}
	
	public void changerMessage(String newMessage){
		message.setText(newMessage);
	}
	
	public void buttonNonClick(){
		if (parent instanceof JeuController){
			((JeuController)parent).clickNonQuitterButton();
		}
		else if (parent instanceof MagasinController){
			((MagasinController)parent).cancelAchat();
		}
	}
	
	public void buttonOuiClick(){
		if (parent instanceof JeuController){
			((JeuController)parent).clickOuiQuitterButton();
		}
		else if (parent instanceof MagasinController){
			((MagasinController)parent).confirmAchat();
		}
	}
	
	public void setParent(Controller parent) {
		this.parent = parent;
	}
	
}
