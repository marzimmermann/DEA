/**
 * Klasse fuer einen nicht
 * akzeptierenden Zustand
 */

package application.dea;

public class NAZustand extends Zustand {
    /** Konstruktor, der den Namen automatisch zuweist */
    public NAZustand(String name) {
        this.setName(name);
    }
    
    /** gibt zurueck, ob der Zustand akzeptierend ist */
    @Override
    public boolean istAkzeptierend() {
        return false;
    }
}
