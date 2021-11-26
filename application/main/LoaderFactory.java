package application.main;

public class LoaderFactory implements Runnable {

	private Main main;

	public LoaderFactory(Main main) {

		this.main = main;

	}

	@Override
	public void run() {
	}

	public Main getMain() {
		return main;
	}

}
