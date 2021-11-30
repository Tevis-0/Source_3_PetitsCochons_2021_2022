package application.models;

public enum Mode {
	ENTRAINEMENT(1), PROGRESSION(1.3), COMPETITION(2);

	private double facteurMode;

	Mode(double facteurMode) {
		this.facteurMode = facteurMode;
	}

	public double getFacteurMode() {
		return facteurMode;
	}
}
