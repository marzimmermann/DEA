/**
 * Klasse fuer einen DEA
 */

package application.dea;

import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.io.Serializable;
import java.lang.Iterable;

public class DEA implements Serializable {
	private String name, eingabe = "";
	private boolean validiert = false; // ob der Automat validiert ist und ausgefuehrt werden darf
	private boolean gesperrt = false; // ob der Automat fuer Aenderungen gesperrt ist (waehrend eines Durchlaufs)
	private boolean gespeichert = false; // ob der Automat in seiner aktuellen Form gespeichert wurde
	private int zustandId = 0; // zur Durchnummerierung der Zustaende, falls kein Name angegeben wird
	private int zeichenIndex; // Position des zu lesenden Zeichens waehrend einer Ausfuehrung
	private HashSet<Character> alphabet = new HashSet<>();
	private HashMap<String, Zustand> zustaende = new HashMap<>();
	private Zustand start, aktuellerZustand ;
	private String fileSeperator = System.getProperty("file.separator");
	private int xStart = -1, yStart;

	/*
	 * Konstruktoren
	 */

	/** erzeugt einen neuen Zustand mit dem angegebenen Namen */
	public DEA(String name) {
		setName(name);
	}

	/** Copy-Konstruktor */
	public DEA(DEA dea) {
		this.kopiere(dea);
	}

	/*
	 * Methoden zum Ausfuehren des Automaten
	 */

	/** startet den DEA mit einer neuen Eingabe */
	public boolean starte(String eingabe) {
		this.validiere();
		if (!validiert || gesperrt || !setEingabe(eingabe)) {
			return false;
		}
		zeichenIndex = 0;
		aktuellerZustand = start;
		gesperrt = true;
		return true;
	}

	/** geht in den naechsten Zustand ueber */
	public void geheWeiter() {
		if (!gesperrt) {
			return;
		}
		aktuellerZustand = aktuellerZustand.getTransition(liesZeichen());
		if (istFertig()) {
			stoppe();
		}
	}

	/** stoppt die aktuelle Ausfuehrung des Automaten */
	public void stoppe() {
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
		if (name == null || name.isEmpty()) {
			return fuegeZustandHinzu(akzeptierend);
		}
		if (zustaende.containsKey(name)) {
			return false;
		}
		gespeichert = false;
		Zustand zustand = akzeptierend? new AZustand(name) : new NAZustand(name);
		zustaende.put(name, zustand);
		return true;
	}

	/** fuegt einen neuen Zustand per Ordnungszahl hinzu */
	public boolean fuegeZustandHinzu(boolean akzeptierend) {
		while(!fuegeZustandHinzu(""+(zustandId++), akzeptierend)) {
			// hochzaehlen, falls Namen schon existieren
		}
		gespeichert = false;
		return true;
	}

	/** bennent einen Zustand um */
	public boolean benneneZustandUm(String von, String nach) {
		if (von == null || nach == null || von.isEmpty() || nach.isEmpty()) {
			return false;
		} else if (von.equals(nach)) {
			return true;
		} else if (!zustaende.containsKey(von) || zustaende.containsKey(nach)) {
			return false;
		}
		Zustand tmp = zustaende.get(von);
		gespeichert = false;
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
		gespeichert = false;
		Zustand zustand = zustaende.get(name);
		zustaende.remove(name);
		for (Zustand z : zustaende.values()) {
			z.loescheTransitionen(zustand);
		}
		return true;
	}

	/** fuegt eine neue Transition hinzu */
	public boolean fuegeTransitionHinzu(String von, char ueber, String nach) {
		if (!istImAlphabet(ueber) || !zustaende.containsKey(von) || !zustaende.containsKey(nach)) {
			return false;
		}
		gespeichert = false;
		zustaende.get(von).fuegeTransitionHinzu(ueber, zustaende.get(nach));
		return true;
	}

	/** loescht eine bestehende Transition */
	public void loescheTransition(String von, char ueber) {
		if (!zustaende.containsKey(von)) {
			return;
		}
		gespeichert = false;
		zustaende.get(von).loescheTransition(ueber);
	}

	/** fuegt dem Alphabet alle gueltigen Zeichen im uebergebenen String hinzu */
	public void fuegeZeichenHinzu(String zeichen) {
		for (char c : zeichen.toCharArray()) {
			fuegeZeichenHinzu(c);
		}
	}

	/** erweitert das Alphabet um ein Zeichen */
	public void fuegeZeichenHinzu(char c) {
		if (c != ' ') { // Leerzeichen werden ignoriert
			gespeichert = false;
			alphabet.add(c);
		}
	}

	/** loescht alle im uebergebenen String enthaltenen Zeichen aus dem Alphabet */
	public void loescheZeichen(String zeichen) {
		for (char c : zeichen.toCharArray()) {
			loescheZeichen(c);
		}
	}

	/** loescht ein Zeichen aus dem Alphabet (und alle damit verbundenen Transitionen) */
	public void loescheZeichen(char c) {
		if(!gesperrt){
			gespeichert = false;
			alphabet.remove(c);
			for (Zustand z : zustaende.values()) {
				z.loescheTransition(c);
			}
		}
	}

	/*
	 * Zusaetzliche Funktionen des DEAs
	 */

	/** prueft, ob es sich um einen gueltigen DEA handelt */
	public String validiere() {
		gespeichert = false;
		if (start == null || !zustaende.containsValue(start)) {
			validiert = false;
			return "Startzustand fehlt";
		}
		int anzTransitionen = 0;
		for (Zustand z : zustaende.values()) {
			int merke = z.zaehleTransitionen();
			anzTransitionen += merke;
			if (merke != this.alphabet.size()) {
				validiert = false;
				if (this.alphabet.size()-merke == 1) {
					return "Es fehlt eine Transition vom Zustand " + z.getName();
				} else {
					return "Es fehlen " + (this.alphabet.size()-merke) + " Transitionen vom Zustand " + z.getName();
				}
			}
		}
		validiert = (anzTransitionen == zustaende.size()*alphabet.size());
		if (!validiert) {
			return "Es fehlen Transitionen";
		}
		return "";
	}

	/** gibt sich selbst als minimalen DEA zurueck */
	public DEA minimiere() {
		this.validiere();
		if (istGesperrt() || !istValidiert()) {
			return null;
		}
		gespeichert = false;

		// loesche nicht erreichbare Zustaende
		HashSet<Zustand> erreichbar = new HashSet<>();
		erreichbar.add(this.start);
		int anzErreichbar = 0;
		while (anzErreichbar != erreichbar.size()) {
			anzErreichbar = erreichbar.size();
			for (Zustand z : new HashSet<Zustand>(erreichbar)) {
				for (char c : this.alphabet) {
					erreichbar.add(z.getTransition(c));
				}
			}
		}
		if (erreichbar.size() != this.zustaende.size()) {
			for (Zustand z : new HashSet<Zustand>(this.zustaende.values())) {
				if (!erreichbar.contains(z)) {
					this.loescheZustand(z.getName());
				}
			}
		}

		// berechne Markierungstabelle
		Zustand zust[] = new Zustand[zustaende.size()];
		zustaende.values().toArray(zust);
		boolean feld[][] = new boolean[zust.length][zust.length];
		HashMap<Zustand, Integer> getIndex = new HashMap<>();

		// markiere jedes Zustandspaar mit genau einem akzeptierenden Zustand
		for (int i = 0; i < zust.length; i++) {
			getIndex.put(zust[i], i);
			for (int j = 0; j < i; j++) {
				if (zust[i].istAkzeptierend() ^ zust[j].istAkzeptierend()) {
					feld[j][i] = true;
				}
			}
		}

		// markiere rekursiv andere Zustaende
		boolean veraendert = false;
		do {
			veraendert = false;
			for (int i = 0; i < zust.length; i++) {
				for (int j = 0; j < i; j++) {
					if (feld[j][i]) {
						continue; // nur unmarkierte Paare betrachten
					}
					for (char c : alphabet) {
						int z1 = getIndex.get(zust[i].getTransition(c));
						int z2 = getIndex.get(zust[j].getTransition(c));
						if (feld[z1][z2] || feld[z2][z1]) {
							veraendert = true;
							feld[j][i] = true;
						}
					}
				}
			}
		} while (veraendert);

		// initialisiere neuen DEA
		DEA tmp = new DEA(name);
		tmp.alphabet.addAll(this.alphabet);

		// fasse nicht markierte Zustandspaare zusammen
		int zustId = 0;
		int neuerZst[] = new int[zust.length];
		for (int i = 0; i < zust.length; i++) {
			for (int j = 0; j < i; j++) {
				if (!feld[j][i]) {
                    int zId_tmp = 0;
                    for (int k = 0; k < zust.length; k++) {
                        if (((!feld[i][k] && !feld[k][i]) || (!feld[j][k] && !feld[k][j])) && neuerZst[k] != 0) {
                            zId_tmp = neuerZst[k];
                            break;
                        }
                    }
                    if (zId_tmp == 0) {
                        zustId++;
                        zId_tmp = zustId;
                        tmp.fuegeZustandHinzu(""+(zustId-1), zust[i].istAkzeptierend());
					}
					neuerZst[i] = zId_tmp;
					neuerZst[j] = zId_tmp;
				}
			}
		}

		// nimm restliche Zustaende
		for (int i = 0; i < neuerZst.length; i++) {
			if (neuerZst[i] == 0) {
				neuerZst[i] = ++zustId;
				tmp.fuegeZustandHinzu(""+(zustId-1), zust[i].istAkzeptierend());
			}
		}

		// setze Startzustand
		tmp.setStart(""+(neuerZst[getIndex.get(this.start)]-1));

		// ordne Zustaende im Kreis an
		double r = 200, m = r+100;
		int k = 1;
		for (Zustand z : tmp.zustaende.values()) {
			if (z == tmp.start) {
				z.setX((int) (m-r));
				z.setY((int) m);
			} else {
				z.setX((int) (m-r*Math.cos(2*k*Math.PI/zustId)));
				z.setY((int) (m+r*Math.sin(2*k*Math.PI/zustId)));
				k++;
			}
		}

		// fuege Transitionen hinzu
		boolean benutzt[] = new boolean[zustId];
		for (int i = 0; i < zust.length; i++) {
			if (!benutzt[neuerZst[i]-1]) {
				benutzt[neuerZst[i]-1] = true;
				for (char c : alphabet) {
					tmp.fuegeTransitionHinzu(""+(neuerZst[i]-1), c, ""+(neuerZst[getIndex.get(zust[i].getTransition(c))]-1));
				}
			}
		}

		// pruefe, ob Minimierung geklappt hat
		tmp.validiere();
		if (!tmp.istValidiert()) {
			throw new RuntimeException("Minimierung hat nicht funktioniert");
		}

		return tmp;
	}

	/** kopiert alle Eigenschaften des uebergebenen DEA */
	public void kopiere(DEA dea) {
		this.gesperrt = false; // damit importieren moeglich ist

		/* komplexere Attribute */
		this.alphabet = new HashSet<>();
		this.zustaende = new HashMap<>();
		this.importiere(dea); // importiert Alphabet, Zustaende und Transitionen

		/* primitive Attribute */
		this.name = dea.name;
		this.eingabe = dea.eingabe;
		this.validiert = dea.validiert;
		this.gesperrt = dea.gesperrt;
		this.gespeichert = dea.gespeichert;
		this.zustandId = dea.zustandId;
		this.zeichenIndex = dea.zeichenIndex;
		if (dea.start != null) {
			this.start = this.zustaende.get(dea.start.getName());
		}
		if (dea.aktuellerZustand != null) {
			this.aktuellerZustand = this.zustaende.get(dea.aktuellerZustand.getName());
		}
	}

	/** importiert den uebergebenen DEA in den aktuellen hinein */
	public void importiere(DEA dea) {
		if (gesperrt) {
			return;
		}
		this.alphabet.addAll(dea.alphabet); // Alphabete werden vereinigt
		this.validiert = false;
		this.gespeichert = false;

		/* Aufbau der Zustandsstruktur */
		// Zustaende erstellen
		HashMap<String, String> umbenennungen = new HashMap<>();
		for (String s : dea.zustaende.keySet()) {
			String neuerName = s;
			if (this.zustaende.containsKey(s)) {
				int zahl = 1;
				while (this.zustaende.containsKey(s+zahl)) {
					zahl++;
				}
				neuerName = s + zahl;
			}
			umbenennungen.put(s, neuerName);
			this.fuegeZustandHinzu(neuerName, dea.zustaende.get(s).istAkzeptierend());
			this.zustaende.get(neuerName).setX(dea.zustaende.get(s).getX());
			this.zustaende.get(neuerName).setY(dea.zustaende.get(s).getY());
		}
		// Transitionen uebernehmen
		for (String s : dea.zustaende.keySet()) {
			for (char ueber : this.alphabet) {
				Zustand nach = dea.zustaende.get(s).getTransition(ueber);
				if (nach != null) {
					this.fuegeTransitionHinzu(umbenennungen.get(s), ueber, umbenennungen.get(nach.getName()));
					Zustand.ZustandUmhueller zu_alt = dea.zustaende.get(s).getTransitionen().get(ueber);
					Zustand.ZustandUmhueller zu_neu = this.zustaende.get(s).getTransitionen().get(ueber);
					zu_neu.setX(zu_alt.getX());
					zu_neu.setY(zu_alt.getY());
				}
			}
		}
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
		boolean tmp = Speicher.speichere(this, verzeichnis+fileSeperator+name+".dea");
		if (tmp) {
			gespeichert = true;
		}
		return tmp;
	}

	/** speichert den DEA im angegebenen Verzeichnis */
	public DEA lade(String verzeichnis, String name) {
		DEA tmp = (DEA) Speicher.lade(verzeichnis+fileSeperator+name+".dea");
		if (tmp != null) {
            tmp.gesperrt = false;
		}
		return tmp;
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

	/** gibt zurueck, ob der DEA schon gespeichert wurde */
	public boolean istGespeichert() {
		return gespeichert;
	}

	/** setzt den Startzustand */
	public boolean setStart(String zustand) {
		if (!zustaende.containsKey(zustand)) {
			return false;
		}
		gespeichert = false;
		start = zustaende.get(zustand);
		aktuellerZustand = start;
		return true;
	}

	/** gibt den Startzustand zurueck */
	public String getStart() {
		if (start != null) {
			return start.getName();
		}
		return "";
	}

	/** setzt die Eingabe des Automaten um und gibt zurueck, ob die Eingabe gueltig ist */
	public boolean setEingabe(String eingabe) {
		if (!istGueltigeEingabe(eingabe)) {
			return false;
		}
		gespeichert = false;
		this.eingabe = eingabe;
		return true;
	}

	/** gibt den aktuellen Zustand zurueck */
	public String getAktuellerZustand() {
		if(aktuellerZustand != null){
			return aktuellerZustand.getName();
		}
		return "";
	}

	/** gibt das Alphabet als String zurueck */
	public String getAlphabet() {
		StringBuilder tmp = new StringBuilder(alphabet.size()*2);
		for (Character c : alphabet) {
			tmp.append(c.charValue()+" ");
		}
		return tmp.toString();
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
		gespeichert = false;
		this.name = name;
	}

	/** gibt eine iterierbare Darstellung aller Zustandsobjekte zurueck */
	public Iterable<Zustand> getZustaende() {
		return zustaende.values();
	}

	/** gibt eine iterierbare Darstellung aller Zustandsnamen zurueck */
	public HashMap<String, Zustand> getZustaendeNamen() {
		return zustaende;
	}

	/** gibt die X-Koordinate des Startkreises zurueck */
	public int getXStart() {
		return xStart;
	}

	/** gibt die X-Koordinate des Startkreises zurueck */
	public int getYStart() {
		return yStart;
	}
	
	/** setzt X- und Y-Koordinate des Startkreises */
	public void setXYStart(int xStart, int yStart) {
		this.xStart = xStart;
		this.yStart = yStart;
	}

	/** zeigt alle im DEA enthaltenen Zustaende und Transitionen an */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DEA " + this.getName() + ":\n");
		for (Zustand z : this.getZustaende()) {
			sb.append("  " + z.getName()+" (" + z.istAkzeptierend() + "):\n");
			for (char c : this.alphabet) {
				if (z.getTransition(c) != null) {
					sb.append("     " + c + " -> " + z.getTransition(c).getName() + "\n");
				}
			}
		}
		return sb.toString();
	}

	/** setzt den Speicher-Status auf false */
	public void setUngespeichert() {
		gespeichert = false;
	}

	/** Main-Methode zum Testen */
	public static void main(String args[]) {
		/*
		 * beide folgenden Beispiele (zur Minimierung) scheinen zu stimmen
		 */

		System.out.println("Erster Test:\n");

		DEA d = new DEA("test");
		d.fuegeZeichenHinzu("01");
		d.fuegeZustandHinzu("A", false);
		d.fuegeZustandHinzu("B", false);
		d.fuegeZustandHinzu("C", true);
		Speicher.merke(d);
		d.fuegeZustandHinzu("D", false);
		d.fuegeZustandHinzu("E", false);
		d.fuegeZustandHinzu("F", false);
		d.fuegeZustandHinzu("G", false);
		d.fuegeZustandHinzu("H", false);

		Speicher.merke(d);
		d.fuegeTransitionHinzu("A", '0', "B");
		d.fuegeTransitionHinzu("A", '1', "F");
		d.fuegeTransitionHinzu("B", '0', "G");
		Speicher.merke(d);
		d.fuegeTransitionHinzu("B", '1', "C");
		d.fuegeTransitionHinzu("C", '0', "A");
		d.fuegeTransitionHinzu("C", '1', "C");
		d.fuegeTransitionHinzu("D", '0', "C");
		d.fuegeTransitionHinzu("D", '1', "G");
		d.fuegeTransitionHinzu("E", '0', "H");
		d.fuegeTransitionHinzu("E", '1', "F");
		d.fuegeTransitionHinzu("F", '0', "C");
		d.fuegeTransitionHinzu("F", '1', "G");
		d.fuegeTransitionHinzu("G", '0', "G");
		d.fuegeTransitionHinzu("G", '1', "E");
		d.fuegeTransitionHinzu("H", '0', "G");
		d.fuegeTransitionHinzu("H", '1', "C");

		d.setStart("A");
		d.validiere();
		d = d.minimiere();

		System.out.println(d);

		DEA f = d;

		d.speichere("/home/d.todt/");

		System.out.println("\n\nZweiter Test:\n");

		d = new DEA("test");
		d.fuegeZeichenHinzu("01");
		d.fuegeZustandHinzu("0", false);
		d.fuegeZustandHinzu("1", false);
		d.fuegeZustandHinzu("2", false);
		d.fuegeZustandHinzu("3", false);
		d.fuegeZustandHinzu("4", false);
		d.fuegeZustandHinzu("5", true);
		d.fuegeZustandHinzu("6", true);

		d.fuegeTransitionHinzu("0", '0', "1");
		d.fuegeTransitionHinzu("0", '1', "1");
		d.fuegeTransitionHinzu("1", '0', "2");
		d.fuegeTransitionHinzu("1", '1', "3");
		d.fuegeTransitionHinzu("2", '0', "5");
		d.fuegeTransitionHinzu("2", '1', "4");
		d.fuegeTransitionHinzu("3", '0', "6");
		d.fuegeTransitionHinzu("3", '1', "4");
		d.fuegeTransitionHinzu("4", '0', "5");
		d.fuegeTransitionHinzu("4", '1', "6");
		System.out.println(d.validiere());
		d.setStart("0");
		System.out.println(d.validiere());
		d.fuegeTransitionHinzu("5", '0', "5");
		d.fuegeTransitionHinzu("5", '1', "5");
		d.fuegeTransitionHinzu("6", '0', "6");
		d.fuegeTransitionHinzu("6", '1', "6");


		System.out.println(d.validiere());
		d = d.minimiere();
		d = new DEA(d);

		System.out.println(d);

		System.out.println("\n\nDritter Test\n");

		d.importiere(f);
		System.out.println(d);

		Speicher.merke(d);

		System.out.println("\n\nVierter Test\n");
		d = Speicher.nimmAenderungZurueck();
		System.out.println(d);

	}
}
