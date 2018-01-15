/**
 * Abstrakte Klasse fuer
 * jede Art von Zustaenden
 */

package application.dea;

import java.util.HashMap;
import java.io.Serializable;

public abstract class Zustand implements Serializable {
    protected String name;
    protected int x  = 0, y = 0;
    
    /*Wrapper*/
    public class ZustandUmhueller implements Serializable {
    	Zustand z;
    	int x;
    	int y;
    	public ZustandUmhueller(Zustand s) {
    		z = s;
    	}
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
    	public Zustand getZustand(){
    		return z;
    	}
    	
    }
    //protected HashMap<Character, Zustand> transitionen = new HashMap<>();
    protected HashMap<Character, ZustandUmhueller> transitionen = new HashMap<>();
    
    /** gibt den Namen des Zustands zurueck */
    public String getName() {
        return name;
    }
    
    /** setzt den Namen des Zustands um */
    public void setName(String name) {
        this.name = name;
    }
    
    /** fuegt eine Transition hinzu */
    public void fuegeTransitionHinzu(char zeichen, Zustand zustand) {
        transitionen.put(zeichen, new ZustandUmhueller(zustand));
    }
    
    /** loescht die Transition mit dem entsprechenden Zeichen */
    public void loescheTransition(char zeichen) {
        transitionen.remove(zeichen);
    }
    
    /** loescht alle Transitionen, die zum uebergebenen Zustand fuehren */
    public void loescheTransitionen(Zustand zustand) {
        for (char c : transitionen.keySet()) {
            if (transitionen.get(c).getZustand() == zustand) {
                loescheTransition(c);
            }
        }
    }
    
    /** gibt den ueber eine Transition erreichten Zustand zurueck */
    public Zustand getTransition(char c) {
        return transitionen.get(c).getZustand();
    }
    
    /** gibt den ueber eine Transition erreichten Zustand zurueck */
    public ZustandUmhueller getZustandhuelleTransition(char c) {
        return transitionen.get(c);
    }
    
    /** gibt die Anzahl der gespeicherten Transitionen zurueck */
    public int zaehleTransitionen() {
        return transitionen.size();
    }
    
    /** gibt die x-Koordinate zurueck */
    public int getX() {
        return x;
    }
    
    /** gibt die y-Koordinate zurueck */
    public int getY() {
        return y;
    }
    
    /** setzt die x-Koordinate um */
    public void setX(int x) {
        this.x = x;
    }
    
    /** setzt die y-Koordinate um */
    public void setY(int y) {
        this.y = y;
    }
    
    /** gibt die gesamten Transitionen zrueck */
    public HashMap<Character, ZustandUmhueller> getTransitionen() {
		return transitionen;
	}

	/** soll zurueckgeben, ob der Zustand akzeptierend ist */
    public abstract boolean istAkzeptierend();
}


