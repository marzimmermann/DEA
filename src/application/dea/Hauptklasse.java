package application.dea;

import java.awt.EventQueue;
import java.io.File;

import javax.swing.JOptionPane;

public class Hauptklasse {

	
	public static void main(String[] args) {
		Konfiguration konfiguration = new Konfiguration();
		if(!konfiguration.lade()) {
			String verzeichnis = new Startseite().getVerzeichnis();
			konfiguration.setArbeitsverzeichnis(verzeichnis.toString());
			DEA d = new DEA("");
			konfiguration.setLetzterDea(konfiguration.getArbeitsverzeichnis()+"/"+d.getName());
		}
		Editor e = new Editor(konfiguration);
		
		
		
	}

}
/*
EventQueue.invokeLater(new Runnable() {
	public void run() {
		try {
			Editor frame = new Editor(konfiguration);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
});*/