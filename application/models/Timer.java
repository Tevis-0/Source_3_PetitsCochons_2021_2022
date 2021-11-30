package application.models;

import application.controllers.JeuController;

public class Timer implements Runnable {

	private double time;
	private JeuController controller;

	private boolean enCours;
	private boolean stopper;

	public Timer(JeuController controller) {
		this.controller = controller;
		time = 0;
		enCours = false;
		stopper = false;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(1000);
			enCours = true;
			while (enCours && !stopper) {
				controller.changerTime(time);
				Thread.sleep(100);
				time += 0.1d;
				synchronized (this) {
					if (!enCours) {
						wait();
					}
				}
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void pause() {
		enCours = false;
	}

	public synchronized void reprendre() {
		enCours = true;
		notify();
	}

	public void stopper() {
		stopper = true;
		reprendre();
		time = 0.0d;
	}

	public double getTime() {
		return time;
	}

}
