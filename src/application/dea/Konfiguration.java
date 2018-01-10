/**
 * Klasse zum Verwalten der
 * Konfigurationen des Editors
 */

package application.dea;

import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class Konfiguration implements Serializable {
    private String arbeitsverzeichnis, letzterDea;
    private int x, y;
    public static final String dateiname = ".dea_editor.konfiguration";
    
    /** speichert die Konfiguration mit der Fenstergroesse x, y */
    public boolean speichere(int x, int y, String letzter) {
        this.x = x;
        this.y = y;
        this.letzterDea = letzter;
        return Speicher.speichere(this, System.getProperty("user.home")+"/"+dateiname);
    }
    
    /** laedt eine bestehende Konfiguration in die aktuelle hinein */
    public boolean lade() {
        Konfiguration tmp = (Konfiguration) Speicher.lade(System.getProperty("user.home")+"/"+dateiname);
        if (tmp == null) {
            return false; // Datei nicht vorhanden oder sonstiger Fehler
        }
        this.arbeitsverzeichnis = tmp.arbeitsverzeichnis;
        this.letzterDea = tmp.letzterDea;
        this.x = tmp.x;
        this.y = tmp.y;
        return true;
    }
    
    /** gibt das Arbeitsverzeichnis zurueck */
    public String getArbeitsverzeichnis() {
        return arbeitsverzeichnis;
    }
    
    /** gibt den Pfad des zuletzt bearbeiteten DEAs zurueck */
    public String getLetzterDea() {
        return letzterDea;
    }
    
    /** setzt den Pfad des zuletzt bearbeiteten DEAs um */
    public void setLetzterDea(String dea) {
        letzterDea = dea;
    }
    
    /** gibt die Fensterbreite zurueck */
    public int getX() {
        return x;
    }
    
    /** gibt die Fensterhoehe zurueck */
    public int getY() {
        return y;
    }
     /** setzt das Arbeitsverzeichnis um */
    public void setArbeitsverzeichnis(String arbeitsverzeichnis) {
        this.arbeitsverzeichnis = arbeitsverzeichnis;
    }
}
