/**
 * Klasse fuer einen
 * akzeptierenden Zustand
 */

package application.dea;

public class AZustand extends Zustand {
    /** Konstruktor, der den Namen automatisch zuweist */
    public AZustand(String name) {
        this.setName(name);
    }
    
    /** gibt zurueck, ob der Zustand akzeptieren ist */
    @Override
    public boolean istAkzeptierend() {
        return true;
    }
}
