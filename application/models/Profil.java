package application.models;

public class Profil {

	private int id;
	private String pseudo;
	private int niveau;
	private int experience;
	private int experienceMax;
	private String[] settings;
	private int id_online;
	private int couleur;
	private int ancienNiveau;
	private int nouveauNiveau;

	public Profil(int id, String pseudo, int niveau, int experience, String[] settings, int id_online, int couleur) {
		this.id = id;
		this.pseudo = pseudo;
		this.niveau = niveau;
		this.experience = experience;
		this.experienceMax = 50 * niveau;
		this.settings = settings;
		this.id_online = id_online;
		this.couleur = couleur;
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public int getNiveau() {
		return niveau;
	}

	public int getExperience() {
		return experience;
	}

	public void ajouterExperience(int experience) {
		setAncienNiveau(this.niveau);
		this.experience += experience;
		while (this.experience > experienceMax) {
			this.experience -= experienceMax;
			niveau++;
			experienceMax += 50;
		}
		setNouveauNiveau(this.niveau);
	}

	public int getExperienceMax() {
		return experienceMax;
	}

	public String[] getSettings() {
		return settings;
	}

	public void setSettings(String[] settings) {
		this.settings = settings;
	}

	public int getId_online() {
		return id_online;
	}

	public int getCouleur() {
		return couleur;
	}

	public void setCouleur(int couleur) {
		this.couleur = couleur;
	}
	
	public int getAncienNiveau(){
		return ancienNiveau;
	}
	
	public void setAncienNiveau(int an){
		this.ancienNiveau = an;
	}

	public int getNouveauNiveau() {
		return nouveauNiveau;
	}

	public void setNouveauNiveau(int nouveauNiveau) {
		this.nouveauNiveau = nouveauNiveau;
	}
	
	

}
