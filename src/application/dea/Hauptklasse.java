package application.dea;

import java.awt.EventQueue;
import java.io.File;

public class Hauptklasse {

	
	public static void main(String[] args) {
		boolean gesetztesArbeitsverzeichnis = false;
		Konfiguration konfiguration = new Konfiguration();
		if(!konfiguration.lade()) {
			File verzeichnis;
			do {
				Startseite dia = new Startseite();
				verzeichnis = new File(dia.getVerzeichnis());
				if( verzeichnis.isDirectory()) {
					gesetztesArbeitsverzeichnis = true;
				}
				else if(!verzeichnis.exists() ) {
					verzeichnis.mkdirs();
					gesetztesArbeitsverzeichnis = true;
				}
				else {
					new Fehlermeldung("Ungueltige Eingabe");
				}
			} 
			while( !gesetztesArbeitsverzeichnis );
			konfiguration.setArbeitsverzeichnis(verzeichnis.toString());
			DEA d = new DEA("tmp.dea");
			konfiguration.setLetzterDea(konfiguration.getArbeitsverzeichnis()+"/"+d.getName());
		}
		
		
		
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