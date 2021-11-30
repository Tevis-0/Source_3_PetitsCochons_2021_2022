package application.models;

import utils.Random;

public class Defi {

	private Contexte contexte;
	private Niveau niveau;
	private int id;

	public Defi(Contexte contexte, Niveau niveau, int id) {
		this.contexte = contexte;
		this.niveau = niveau;
		this.id = id;
	}

	public Defi(Contexte contexte, Niveau niveau) {
		this(contexte, niveau, Random.generer(1, 6));
	}

	public int getNumeroDefi() {
		int numeroDefi = 0;

		// Contexte
		if (contexte == Contexte.NOCTURNE) {
			numeroDefi += 24;
		}

		// Niveau
		if (niveau == Niveau.JUNIOR) {
			numeroDefi += 6;
		} else if (niveau == Niveau.EXPERT) {
			numeroDefi += 12;
		} else if (niveau == Niveau.MASTER) {
			numeroDefi += 18;
		}

		numeroDefi += id;

		return numeroDefi;

	}

	public Contexte getContexte() {
		return contexte;
	}

	public void setContexte(Contexte contexte) {
		this.contexte = contexte;
	}

	public Niveau getNiveau() {
		return niveau;
	}

	public void setNiveau(Niveau niveau) {
		this.niveau = niveau;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
