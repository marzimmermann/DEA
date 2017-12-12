/**
 * Hilfsklasse zum Speichern
 * und Laden beliebiger Objekte
 */

package application.dea;

import java.io.File;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.Serializable;
import java.io.IOException;
import java.io.FileNotFoundException;

public class Speicher {
    /** speichert ein uebergebenes Serializable unter dem angegebenen Dateinamen */
    public static boolean speichere(Serializable obj, String pfad) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(pfad))) {
            oos.writeObject(obj);
            return true;
        } catch (IOException e) {
            System.err.printf("Beim Speichern der Datei %s ist ein Ein-/Ausgabefehler aufgetreten.\n", pfad);
        } catch (Exception e) {
            System.err.printf("Beim Speichern der Datei %s ist ein unbekannter Fehler aufgetreten.\n", pfad);
        }
        return false;
    }
    
    /** laedt ein Serializable aus der Datei mit dem angegebenen Dateinamen */
    public static Serializable lade(String pfad) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(pfad))) {
            return (Serializable) ois.readObject();
        } catch (FileNotFoundException e) {
            System.err.printf("Die zu ladende Datei %s konnte nicht gefunden werden.\n", pfad);
        } catch (IOException e) {
            System.err.printf("Beim Laden der Datei %s ist ein Ein-/Ausgabefehler aufgetreten.\n", pfad);
        } catch (Exception e) {
            System.err.printf("Beim Laden der Datei %s ist ein unbekannter Fehler aufgetreten.\n", pfad);
        }
        return null;
    }
    
    /** prueft, ob der angegebene Pfad ein Verzeichnis ist */
    public static boolean existiertVerzeichnis(String pfad) {
        return new File(pfad).isDirectory();
    }
    
    /** prueft, ob der angegebene Pfad eine Datei ist */
    public static boolean existiertDatei(String pfad) {
        return new File(pfad).isFile();
    }
}
