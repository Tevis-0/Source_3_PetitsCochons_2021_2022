package application.main;

import java.io.IOException;

import application.controllers.ChoixModeJeuController;
import application.controllers.ChoixProfilController;
import application.controllers.ClassementController;
import application.controllers.Controller;
import application.controllers.JeuController;
import application.controllers.MagasinController;
import application.controllers.MenuAcceuilController;
import application.controllers.ParametresController;
import application.controllers.SelectNiveauController;
import application.controllers.TableauProgressionController;
import application.models.Contexte;
import application.models.Mode;
import application.models.Niveau;
import application.models.Profil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.ResourceLoader;
import utils.database.Database;

public class Main extends Application {

	private BorderPane root;
	private Stage primaryStage;
	private FXMLLoader loader;

	private Profil profilSelect = null;

	@Override
	public void start(Stage primaryStage) {

		Database db = new Database();

		// POUR LES TESTS
		//db.executeUpdateLocal("DROP TABLE JOUEUR; DROP TABLE PROGRESSION; DROP TABLE COMPETITION; DROP TABLE DEFI;");

		ResourceLoader r = new ResourceLoader();
		r.setMain(this);

		this.primaryStage = primaryStage;
		root = new BorderPane();
		Scene scene = new Scene(root, 1408, 792);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Les 3 petits cochons");
		primaryStage.setResizable(false);

		afficherMenuChoixProfil();

		primaryStage.show();

		primaryStage.setOnCloseRequest(event -> {
			Platform.exit();
			System.exit(0);
		});

	}

	// Afficher le menu d'accueil
	public void afficherMenuAcceuil() {
		try {
			loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/views/MenuAcceuil.fxml"));
			AnchorPane menu = (AnchorPane) loader.load();

			MenuAcceuilController controller = loader.getController();
			controller.getBarreJoueurController().chargerProfil(getProfilSelect());

			Scene scene = new Scene(menu, 1408, 792);

			Platform.runLater(() -> primaryStage.setScene(scene));
			scene.getStylesheets().add(ResourceLoader.loadCss("menu.css").toString());
			scene.getStylesheets().add(ResourceLoader.loadCss("default.css").toString());
			// scene.getStylesheets().add(ResourceLoader.loadCss("menuAccueil.css").toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Afficher les différents modes de jeu
	public void afficherChoixModeJeu() {
		try {
			loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/views/ChoixModeJeu.fxml"));
			AnchorPane menu = (AnchorPane) loader.load();

			ChoixModeJeuController controller = loader.getController();
			controller.getBarreJoueurController().chargerProfil(getProfilSelect());

			Scene scene = new Scene(menu, 1408, 792);

			Platform.runLater(() -> primaryStage.setScene(scene));
			scene.getStylesheets().add(ResourceLoader.loadCss("menu.css").toString());
			scene.getStylesheets().add(ResourceLoader.loadCss("default.css").toString());
			// scene.getStylesheets().add(ResourceLoader.loadCss("menuAccueil.css").toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Afficher le magasin
		public void afficherMagasin() {
			try {
				loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("/application/views/Magasin.fxml"));
				AnchorPane menu = (AnchorPane) loader.load();

				MagasinController controller = loader.getController();
				controller.getBarreJoueurController().chargerProfil(getProfilSelect());

				Scene scene = new Scene(menu, 1408, 792);

				Platform.runLater(() -> primaryStage.setScene(scene));
				scene.getStylesheets().add(ResourceLoader.loadCss("menu.css").toString());
				scene.getStylesheets().add(ResourceLoader.loadCss("default.css").toString());
				// scene.getStylesheets().add(ResourceLoader.loadCss("menuAccueil.css").toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	// Afficher la sélection du contexte + niveau
	public void afficherMenuChoixProfil() {
		try {
			loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/views/ChoixProfil.fxml"));
			BorderPane menu = (BorderPane) loader.load();

			ChoixProfilController controller = loader.getController();
			controller.setMain(this);
			controller.initProfils();

			Scene scene = new Scene(menu, 1408, 792);
			scene.getStylesheets().add(ResourceLoader.loadCss("menu.css").toString());
			scene.getStylesheets().add(ResourceLoader.loadCss("default.css").toString());
			Platform.runLater(() -> primaryStage.setScene(scene));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Afficher le tableau progression
	public void afficherTableauProgression(Contexte contexte) {

		LoaderFactory loaderFacto = new LoaderFactory(this) {
			@Override
			public void run() {
				try {
					loader = new FXMLLoader();
					loader.setLocation(getClass().getResource("/application/views/TableauProgression.fxml"));
					AnchorPane menu = (AnchorPane) loader.load();

					TableauProgressionController controller = loader.getController();
					controller.setContexte(contexte);
					controller.initialiserBoutons();
					controller.getBarreJoueurController().setPanneau("JOUER");
					controller.getBarreJoueurController().chargerProfil(getProfilSelect());

					Scene scene = new Scene(menu, 1408, 792);
					scene.getStylesheets().add(ResourceLoader.loadCss("default.css").toString());
					scene.getStylesheets().add(ResourceLoader.loadCss("progression.css").toString());
					Platform.runLater(() -> primaryStage.setScene(scene));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Thread t = new Thread(loaderFacto);
				t.start();
			}

		});
	}

	// Afficher la sélection du contexte + niveau
	public void afficherMenuContexteNiveau(Mode mode) {

		LoaderFactory loaderFacto = new LoaderFactory(this) {
			@Override
			public void run() {
				try {
					loader = new FXMLLoader();
					loader.setLocation(getClass().getResource("/application/views/SelectNiveau.fxml"));
					AnchorPane menu = (AnchorPane) loader.load();

					SelectNiveauController controller = loader.getController();
					controller.setMode(mode);
					controller.setProfil(getProfilSelect());
					controller.getBarreJoueurController().chargerProfil(getProfilSelect());
					controller.getBarreJoueurController().setPanneau("JOUER");

					Scene scene = new Scene(menu, 1408, 792);
					scene.getStylesheets().add(ResourceLoader.loadCss("default.css").toString());
					Platform.runLater(() -> primaryStage.setScene(scene));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Thread t = new Thread(loaderFacto);
				t.start();
			}

		});
	}

	// Afficher le jeu
	public void afficherJeu(Mode mode, Contexte contextePlateau, Niveau niveauPlateau, int numDefi, Controller parent) {

		LoaderFactory loaderFacto = new LoaderFactory(this) {
			@Override
			public void run() {
				try {
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(getClass().getResource("/application/views/Jeu.fxml"));
					VBox jeu = (VBox) loader.load();

					JeuController controller = loader.getController();
					controller.initialiserPlateau(mode, contextePlateau, niveauPlateau, numDefi);
					controller.setParent(parent);

					Scene scene = new Scene(jeu, 1408, 792);
					scene.getStylesheets().add(ResourceLoader.loadCss("default.css").toString());
					Platform.runLater(() -> primaryStage.setScene(scene));

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Thread t = new Thread(loaderFacto);
				t.start();
			}

		});

	}

	// Afficher le classement
	public void afficherClassement(boolean afterGame, Contexte contexte, Niveau niveau) {

		LoaderFactory loaderFacto = new LoaderFactory(this) {
			@Override
			public void run() {
				try {
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(getClass().getResource("/application/views/Classement.fxml"));
					AnchorPane jeu = (AnchorPane) loader.load();

					ClassementController controller = loader.getController();
					controller.initClassement(contexte, niveau);
					controller.initRoue(contexte, niveau);
					controller.getBarreJoueurController().setPanneau("CLASSEMENT");
					controller.getBarreJoueurController().chargerProfil(getProfilSelect());

					if (afterGame) {
						controller.enleverRoue();
					}

					Scene scene = new Scene(jeu, 1408, 792);
					scene.getStylesheets().add(ResourceLoader.loadCss("default.css").toString());
					scene.getStylesheets().add(ResourceLoader.loadCss("menu.css").toString());
					scene.getStylesheets().add(ResourceLoader.loadCss("classement.css").toString());
					Platform.runLater(() -> primaryStage.setScene(scene));

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Thread t = new Thread(loaderFacto);
				t.start();
			}

		});

	}

	// Afficher les paramètres
	public void afficherParametres(String parent) {

		LoaderFactory loaderFacto = new LoaderFactory(this) {
			@Override
			public void run() {
				try {
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(getClass().getResource("/application/views/Parametres.fxml"));
					AnchorPane jeu = (AnchorPane) loader.load();

					ParametresController controller = loader.getController();
					controller.setParent(parent);

					Scene scene = new Scene(jeu, 1408, 792);
					scene.getStylesheets().add(ResourceLoader.loadCss("default.css").toString());
					scene.getStylesheets().add(ResourceLoader.loadCss("menu.css").toString());
					scene.getStylesheets().add(ResourceLoader.loadCss("parametres.css").toString());
					Platform.runLater(() -> primaryStage.setScene(scene));

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Thread t = new Thread(loaderFacto);
				t.start();
			}

		});

	}

	public Profil getProfilSelect() {
		return this.profilSelect;
	}

	public void setProfilSelect(Profil profilSelect) {
		this.profilSelect = profilSelect;
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void stop() {

	}

}
