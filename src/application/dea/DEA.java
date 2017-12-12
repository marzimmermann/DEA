/**
 * Klasse fuer einen DEA
 */

package application.dea;

import java.util.HashSet;
import java.util.HashMap;
import java.io.Serializable;
import java.lang.Iterable;

public class DEA implements Serializable {
    private String name, eingabe = "";
    private boolean validiert = false; // ob der Automat validiert ist und ausgefuehrt werden darf
    private boolean gesperrt = false; // ob der Automat fuer Aenderungen gesperrt ist (waehrend eines Durchlaufs)
    private int zustandId = 0; // zur Durchnummerierung der Zustaende, falls kein Name angegeben wird
    private int zeichenIndex; // Position des zu lesenden Zeichens waehrend einer Ausfuehrung
    private HashSet<Character> alphabet = new HashSet<>();
    private HashMap<String, Zustand> zustaende = new HashMap<>();
    private Zustand start, aktuellerZustand;
    
    /*
     * Konstruktoren
     */
    
    /** erzeugt einen neuen Zustand mit dem angegebenen Namen */
    public DEA(String name) {
        setName(name);
    }
    
    /*
     * Methoden zum Ausfuehren des Automaten
     */
    
    /** startet den DEA mit einer neuen Eingabe */
    public void starte(String eingabe) {
        if (!validiert || gesperrt || !setEingabe(eingabe)) {
            return;
        }
        zeichenIndex = 0;
        aktuellerZustand = start;
        gesperrt = true;
    }
    
    /** geht in den naechsten Zustand ueber */
    public void geheWeiter() { // entspricht bisher wohl stepByStep()
        if (!gesperrt) {
            return;
        }
        aktuellerZustand = aktuellerZustand.getTransition(liesZeichen());
        if (istFertig()) {
            gesperrt = false;
        }
    }
    
    /** stoppt die aktuelle Ausfuehrung des Automaten */
    public void stoppe() {
        aktuellerZustand = null;
        gesperrt = false;
    }
    
    /** liest ein Zeichen aus der Eingabe */
    public char liesZeichen() {
        return eingabe.charAt(zeichenIndex++);
    }
    
    /** gibt zurueck, ob der Automat am Ende der Eingabe angekommen ist */
    private boolean istFertig() { // private, um nach aussen Verwechslung mit istGesperrt vorzubeugen
        return zeichenIndex == eingabe.length();
    }
    
    /*
     * Methoden zum Aendern des Automaten
     */
    
    /** fuegt einen neuen Zustand per Namen hinzu */
    public boolean fuegeZustandHinzu(String name, boolean akzeptierend) {
        if (zustaende.containsKey(name)) {
            return false;
        }
        Zustand zustand = akzeptierend? new AZustand(name) : new NAZustand(name);
        zustaende.put(name, zustand);
        return true;
    }
    
    /** fuegt einen neuen Zustand per Ordnungszahl hinzu */
    public boolean fuegeZustandHinzu(boolean akzeptierend) {
        while(!fuegeZustandHinzu(""+(zustandId++), akzeptierend)) {
            // hochzaehlen, falls Namen schon existieren
        }
        return true;
    }
    
    /** bennent einen Zustand um */
    public boolean benneneZustandUm(String von, String nach) {
        Zustand tmp = zustaende.get(von);
        if (tmp == null) {
            return false;
        }
        tmp.setName(nach);
        zustaende.put(nach, tmp);
        zustaende.remove(von);
        return true;
    }
    
    /** loescht einen existierenden Zustand */
    public boolean loescheZustand(String name) {
        if (!zustaende.containsKey(name)) {
            return false;
        }
        Zustand zustand = zustaende.get(name);
        zustaende.remove(name);
        for (Zustand z : zustaende.values()) {
            z.loescheTransitionen(zustand);
        }
        return true;
    }
    
    /** fuegt eine neue Transition hinzu */
    public void fuegeTransitionHinzu(String von, char ueber, String nach) {
        if (!istImAlphabet(ueber)) {
            return;
        }
        zustaende.get(von).fuegeTransitionHinzu(ueber, zustaende.get(nach));
    }
    
    /** loescht eine bestehende Transition */
    public void loescheTransition(String von, char ueber) {
        zustaende.get(von).loescheTransition(ueber);
    }
    
    /** fuegt dem Alphabet alle gueltigen Zeichen im uebergebenen String hinzu */
    public void fuegeZeichenHinzu(String zeichen) {
        for (char c : zeichen.toCharArray()) {
            if (c == ' ') {
                continue; // Leerzeichen werden ignoriert
            }
            fuegeZeichenHinzu(c);
        }
    }
    
    /** erweitert das Alphabet um ein Zeichen */
    public void fuegeZeichenHinzu(char c) {
        alphabet.add(c);
    }
    
    /** loescht ein Zeichen aus dem Alphabet (und alle damit verbundenen Transitionen) */
    public void loescheZeichen(char c) {
        alphabet.remove(c);
        for (Zustand z : zustaende.values()) {
            z.loescheTransition(c);
        }
    }
    
    /*
     * Zusaetzliche Funktionen des DEAs
     */
    
    /** prueft, ob es sich um einen gueltigen DEA handelt */
    public boolean validiere() {
        if (start == null || !zustaende.containsValue(start)) {
            return validiert = false;
        }
        int anzTransitionen = 0;
        for (Zustand z : zustaende.values()) {
            anzTransitionen += z.zaehleTransitionen();
        }
        return validiert = (anzTransitionen == zustaende.size()*alphabet.size());
    }
    
    /** prueft, ob es sich beim uebergebenen String um eine gueltige Eingabe handelt */
    public boolean istGueltigeEingabe(String eingabe) {
        for (char c : eingabe.toCharArray()) {
            if (!istImAlphabet(c)) {
                return false;
            }
        }
        return true;
    }
    
    /*
     * Methoden zur Verwaltung
     */
    
    /** speichert den DEA im angegebenen Verzeichnis */
    public boolean speichere(String verzeichnis) {
        return Speicher.speichere(this, verzeichnis+"/"+name);
    }
    
    /*
     * Getter, Setter etc.
     */
    
    /** gibt zurueck, ob der DEA gerade ausgefuehrt wird */
    public boolean istGesperrt() {
        return gesperrt;
    }
    
    /** gibt zurueck, ob der DEA validiert ist */
    public boolean istValidiert() {
        return validiert;
    }
    
    /** setzt den Startzustand */
    public void setStart(String zustand) {
        start = zustaende.get(zustand);
    }
    
    /** setzt die Eingabe des Automaten um und gibt zurueck, ob die Eingabe gueltig ist */
    public boolean setEingabe(String eingabe) {
        if (!istGueltigeEingabe(eingabe)) {
            return false;
        }
        this.eingabe = eingabe;
        return true;
    }
    
    /** gibt den aktuellen Zustand zurueck */
    public String getAktuellerZustand() {
        return aktuellerZustand.getName();
    }
    
    /** prueft, ob ein Zeichen Teil des Alphabets ist */
    public boolean istImAlphabet(char c) {
        return alphabet.contains(c);
    }
    
    /** gibt die aktuelle Eingabe zurueck */
    public String getEingabe() {
        return eingabe;
    }
    
    /** gibt den Dateinamen zurueck */
    public String getName() {
        return name;
    }
    
    /** setzt den Dateinamen um */
    public void setName(String name) {
        this.name = name;
    }
    
    /** gibt eine iterierbare Darstellung aller Zustandsobjekte zurueck */
    public Iterable<Zustand> getZustaende() {
        return zustaende.values();
    }
}
