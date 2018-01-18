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
			konfiguration.setLetzterDea(konfiguration.getArbeitsverzeichnis()+
					System.getProperty("file.separator")+d.getName());
			konfiguration.setDauer(1000);
			
		}
		Editor e = new Editor(konfiguration);
		
		
		
	}

}
