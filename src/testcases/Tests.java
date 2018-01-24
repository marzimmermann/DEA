/**
 * Klasse zum Ausführen der Tests
 */
package application.dea;


public class Tests {
	
	/**
	* Methode erzeugt formartierte Ausgabe der Testfälle
	* @param name
	* @param bedeutung 
	* @param eingabe
	* @param erwartet
	* @param ausgabe
	*/
	public static void print(String name, String bedeutung, String eingabe, String erwartet, String ausgabe){
		System.out.println("## " +name);
		System.out.println("# " +bedeutung);
		System.out.println("# Eingabedaten: \n" +eingabe);
		System.out.println("## EOF");
		System.out.println("# Erwartet Ausgabe:\n" +erwartet);
		System.out.println("# Tatsaechliche Ausgabe:\n" +ausgabe);
		if(erwartet.equals(ausgabe)){
			System.out.println("## Test erfolgreich\n");
		}
		else {
			System.out.println("## Test fehlerhaft\n");
		}
	}
	
	public static void test1(){
		Konfiguration konfig = new Konfiguration();
		konfig.setArbeitsverzeichnis("test123");
		konfig.setDauer(30L);
		konfig.speichere(10, 20, "testdea");
		Konfiguration konf = new Konfiguration();
		konf.lade();
		String ausgabe = konf.getArbeitsverzeichnis() +"," +konf.getDauer() +"," +konf.getX() +"," +konf.getY() +","+konf.getLetzterDea();
		print("Test1: Konfiguration testen", "Test, ob man beim Laden die eben gespeicherte Konfiguration erhaelt", "test123,30L,10,20,testdea", "test123,30,10,20,testdea", ausgabe);
	}
	
	public static void test2(){
		DEA dea = new DEA("meinDea");
		dea.fuegeZustandHinzu(false);
		dea.fuegeZustandHinzu(true);
		dea.fuegeZeichenHinzu("xy");
		dea.fuegeTransitionHinzu("0",'x',"1");
		dea.fuegeTransitionHinzu("0",'y',"1");
		dea.fuegeTransitionHinzu("1",'x',"1");
		dea.fuegeTransitionHinzu("1",'y',"0");
		dea.validiere();
		String ausgabe = "" + dea.istValidiert() +",";
		dea.setStart("0");
		dea.validiere();
		ausgabe += dea.istValidiert() +",";
		dea.loescheTransition("1",'x');
		dea.validiere();
		ausgabe += dea.istValidiert() +",";
		dea.fuegeTransitionHinzu("1",'x',"1");
		dea.validiere();
		ausgabe += dea.istValidiert() +",";
		dea.fuegeZeichenHinzu("z");
		dea.validiere();
		ausgabe += dea.istValidiert();
		print("Test2: DEA validieren", "Methode validieren mit korrekten und falschen DEAs testen", "(DEA)", "false,true,false,true,false", ausgabe);
	}
	
	public static void test3(){
		DEA dea = new DEA("großDEA");
		dea.fuegeZustandHinzu(false);
		dea.fuegeZustandHinzu(false);
		dea.fuegeZustandHinzu(false);
		dea.fuegeZustandHinzu(false);
		dea.fuegeZustandHinzu(false);
		dea.fuegeZustandHinzu(false);
		dea.fuegeZustandHinzu(true);
		dea.fuegeZustandHinzu(true);
		dea.fuegeZustandHinzu(false);
		dea.fuegeZeichenHinzu("01");
		dea.fuegeTransitionHinzu("0",'0',"1");
		dea.fuegeTransitionHinzu("0",'1',"2");
		dea.fuegeTransitionHinzu("1",'0',"3");
		dea.fuegeTransitionHinzu("1",'1',"3");
		dea.fuegeTransitionHinzu("2",'0',"4");
		dea.fuegeTransitionHinzu("2",'1',"4");
		dea.fuegeTransitionHinzu("3",'0',"5");
		dea.fuegeTransitionHinzu("3",'1',"6");
		dea.fuegeTransitionHinzu("4",'0',"7");
		dea.fuegeTransitionHinzu("4",'1',"8");
		dea.fuegeTransitionHinzu("5",'0',"6");
		dea.fuegeTransitionHinzu("5",'1',"6");
		dea.fuegeTransitionHinzu("6",'0',"6");
		dea.fuegeTransitionHinzu("6",'1',"6");
		dea.fuegeTransitionHinzu("7",'0',"7");
		dea.fuegeTransitionHinzu("7",'1',"7");
		dea.fuegeTransitionHinzu("8",'0',"7");
		dea.fuegeTransitionHinzu("8",'1',"7");
		dea.setStart("0");
		dea = dea.minimiere();
		String ausgabe = "" + (dea.toString()).split("DEA großDEA:\n")[1]; 
		
		DEA vergl = new DEA("kleinDEA");
		vergl.fuegeZustandHinzu("2",false);
		vergl.fuegeZustandHinzu("3",false);
		vergl.fuegeZustandHinzu("4",false);
		vergl.fuegeZustandHinzu("5",false);
		vergl.fuegeZustandHinzu("6",false);
		vergl.fuegeZustandHinzu("1",false);
		vergl.fuegeZustandHinzu("0",true);
		vergl.fuegeZeichenHinzu("01");
		vergl.fuegeTransitionHinzu("2",'0',"3");
		vergl.fuegeTransitionHinzu("2",'1',"4");
		vergl.fuegeTransitionHinzu("3",'0',"5");
		vergl.fuegeTransitionHinzu("3",'1',"5");
		vergl.fuegeTransitionHinzu("4",'0',"6");
		vergl.fuegeTransitionHinzu("4",'1',"6");
		vergl.fuegeTransitionHinzu("5",'0',"1");
		vergl.fuegeTransitionHinzu("5",'1',"0");
		vergl.fuegeTransitionHinzu("6",'0',"0");
		vergl.fuegeTransitionHinzu("6",'1',"1");
		vergl.fuegeTransitionHinzu("1",'0',"0");
		vergl.fuegeTransitionHinzu("1",'1',"0");
		vergl.fuegeTransitionHinzu("0",'0',"0");
		vergl.fuegeTransitionHinzu("0",'1',"0");
		vergl.setStart("2");
		String erwartet = "" + (vergl.toString()).split("DEA kleinDEA:\n")[1];
		print("Test3: DEA minimieren", "Methode minimieren mit validiertem DEA testen", "(DEA)", erwartet, ausgabe);
	}
	
	public static void test4(){
        DEA dea = new DEA("meinDea");
		dea.fuegeZustandHinzu(false);
		dea.fuegeZustandHinzu(true);
		dea.fuegeZeichenHinzu("xy");
		dea.fuegeTransitionHinzu("0",'x',"1");
		dea.fuegeTransitionHinzu("0",'y',"1");
		dea.fuegeTransitionHinzu("1",'x',"1");
		dea.fuegeTransitionHinzu("1",'y',"0");
		dea.validiere();
		String ausgabe = "" +dea.minimiere();
		print("Test4: DEA minimieren2", "Methode minimieren mit unvollständigem DEA testen, darf nicht minimiert werden", "(DEA)", "null", ausgabe);
	}
	
	public static void test5(){
        DEA dea = new DEA("meinDea");
		dea.fuegeZustandHinzu("z1",false);
		String ausgabe = "" + dea.fuegeZustandHinzu("z1",false);
		print("Test5: DEA Zustaende hinzufuegen", "Es duerfen keine Zustaende mit gleichem Namen existieren", "(DEA)", "false", ausgabe);
	}
	
	public static void test6(){
        DEA dea = new DEA("meinDea");
		dea.fuegeZustandHinzu(false);
		dea.fuegeZustandHinzu(true);
		dea.fuegeZeichenHinzu("xy");
		dea.fuegeTransitionHinzu("0",'x',"1");
		dea.fuegeTransitionHinzu("0",'y',"1");
		dea.fuegeTransitionHinzu("1",'x',"1");
		dea.fuegeTransitionHinzu("1",'y',"0");
		dea.loescheZeichen('x');
		String deaInhalt = dea.toString();
        String ausgabe = "" +deaInhalt.contains("x") ;
        print("Test6: DEA Alphabet veraendern", "Test,ob beim Loeschen von Symbolen des Alphabets auch zugehoerige Transitionen geloescht werden", "(DEA)", "false", ausgabe);
	}
	
	public static void test7(){
        DEA dea = new DEA("aDea");
		dea.fuegeZustandHinzu(false);
		dea.fuegeZustandHinzu(false);
		dea.fuegeZustandHinzu(false);
		dea.fuegeZustandHinzu(false);
		dea.fuegeZustandHinzu(false);
		dea.fuegeZustandHinzu(false);
		dea.fuegeZustandHinzu(true);
		dea.fuegeZustandHinzu(true);
		dea.fuegeZustandHinzu(false);
		dea.fuegeZeichenHinzu("01");
		dea.fuegeTransitionHinzu("0",'0',"1");
		dea.fuegeTransitionHinzu("0",'1',"2");
		dea.fuegeTransitionHinzu("1",'0',"3");
		dea.fuegeTransitionHinzu("1",'1',"3");
		dea.fuegeTransitionHinzu("2",'0',"4");
		dea.fuegeTransitionHinzu("2",'1',"4");
		dea.fuegeTransitionHinzu("3",'0',"5");
		dea.fuegeTransitionHinzu("3",'1',"6");
		dea.fuegeTransitionHinzu("4",'0',"7");
		dea.fuegeTransitionHinzu("4",'1',"8");
		dea.fuegeTransitionHinzu("5",'0',"6");
		dea.fuegeTransitionHinzu("5",'1',"6");
		dea.fuegeTransitionHinzu("6",'0',"6");
		dea.fuegeTransitionHinzu("6",'1',"6");
		dea.fuegeTransitionHinzu("7",'0',"7");
		dea.fuegeTransitionHinzu("7",'1',"7");
		dea.fuegeTransitionHinzu("8",'0',"7");
		dea.fuegeTransitionHinzu("8",'1',"7");
		dea.setStart("0");
		dea.starte("010");
		String ausgabe = "" + dea.getAktuellerZustand();
		dea.geheWeiter();
		 ausgabe += dea.getAktuellerZustand();
		dea.geheWeiter();
		ausgabe += dea.getAktuellerZustand();
		dea.geheWeiter();
		ausgabe += dea.getAktuellerZustand();
		dea.geheWeiter();
		ausgabe += dea.getAktuellerZustand();
		print("Test7: DEA ausfuehren", "Test der Methoden starte(String eingabe), geheWeiter() und stoppe()", "(DEA)", "01350", ausgabe);
	}
	
	public static void test8(){
        DEA dea = new DEA("fDea");
		dea.fuegeZustandHinzu(false);
		dea.fuegeZustandHinzu(true);
		dea.fuegeZeichenHinzu("xy");
		dea.fuegeTransitionHinzu("0",'x',"1");
		dea.fuegeTransitionHinzu("0",'y',"1");
		dea.fuegeTransitionHinzu("1",'x',"1");
		dea.fuegeTransitionHinzu("1",'y',"0");
		dea.starte("xy");
		String ausgabe = "" + dea.getAktuellerZustand();
		dea.geheWeiter();
		ausgabe += dea.getAktuellerZustand();
		dea.geheWeiter();
		ausgabe += dea.getAktuellerZustand();
		print("Test8: unvollstaendigen DEA ausfuehren", "Test, ob die Methoden starte(String eingabe), geheWeiter() und stoppe() nicht ausgefuehrt werden", "(DEA)", "", ausgabe);
	}
	
	public static void test9(){
        DEA dea = new DEA("aDea");
		dea.fuegeZustandHinzu(false);
		dea.fuegeZustandHinzu(true);
		dea.fuegeZeichenHinzu("xy");
		dea.fuegeTransitionHinzu("0",'x',"1");
		dea.fuegeTransitionHinzu("0",'y',"1");
		dea.fuegeTransitionHinzu("1",'x',"1");
		dea.fuegeTransitionHinzu("1",'y',"0");
		dea.setStart("0");
		dea.speichere("./dist");
		String erwartet = dea.toString() +"\nDEA cDea:";
		erwartet += (dea.toString()).split("DEA aDea:")[1];
		
		
		DEA d = new DEA("");
		d = d.lade("./dist", "aDea");   
		String ausgabe = d.toString() +"\nDEA cDea:";
		
		DEA e = new DEA("cDea");
		e.importiere(dea);   //korrekt
		ausgabe += (e.toString()).split("DEA cDea:")[1];
		
		print("Test9: DEA speicehrn,laden und importieren", "Test, der Methoden speichern,laden und importieren", "(DEA)", erwartet, ausgabe);
	}
	
	public static void test10(){
        String ausgabe = "", erwartet = "";
        String[] a = new String[17];
        DEA dea = new DEA("aDea");
        dea.fuegeZustandHinzu(false);
        a[0] = dea.toString();
        Speicher.merke(dea);
		dea.fuegeZustandHinzu(false);
        a[1] = dea.toString();
		Speicher.merke(dea);
		dea.fuegeZustandHinzu(false);
        a[2] = dea.toString();
		Speicher.merke(dea);
		dea.fuegeZustandHinzu(false);
        a[3] = dea.toString();
		Speicher.merke(dea);
		dea.fuegeZustandHinzu(false);
        a[4] = dea.toString();
		Speicher.merke(dea);
		dea.fuegeZustandHinzu(true);
        a[5] = dea.toString();
		Speicher.merke(dea);
		dea.fuegeZeichenHinzu("01");
		dea.fuegeTransitionHinzu("0",'0',"1");
        a[6] = dea.toString();
		Speicher.merke(dea);
		dea.fuegeTransitionHinzu("0",'1',"2");
        a[7] = dea.toString();
		Speicher.merke(dea);
		dea.fuegeTransitionHinzu("1",'0',"3");
        a[8] = dea.toString();
		Speicher.merke(dea);
		dea.fuegeTransitionHinzu("1",'1',"3");
        a[9] = dea.toString();
		Speicher.merke(dea);
		dea.fuegeTransitionHinzu("2",'0',"4");
        a[10] = dea.toString();
		Speicher.merke(dea);
		dea.fuegeTransitionHinzu("2",'1',"4");
        a[11] = dea.toString();
		Speicher.merke(dea);
		dea.fuegeTransitionHinzu("3",'0',"5");
        a[12] = dea.toString();
		Speicher.merke(dea);
		dea.fuegeTransitionHinzu("3",'1',"4");
        a[13] = dea.toString();
		Speicher.merke(dea);
		dea.fuegeTransitionHinzu("4",'0',"1");
        a[14] = dea.toString();
		Speicher.merke(dea);
		dea.fuegeTransitionHinzu("4",'1',"2");
        a[15] = dea.toString();
		Speicher.merke(dea);
		dea.fuegeTransitionHinzu("5",'0',"5");
		Speicher.merke(dea);
		a[16] = dea.toString();
		dea.fuegeTransitionHinzu("5",'1',"5");
		Speicher.merke(dea);
        
        for(int i=16; i>0; i--){
            dea = Speicher.nimmAenderungZurueck();
            if((dea.toString()).equals(a[i])){
                //ausgabe += i +": " +dea.toString() +"\n";
                //erwartet += i +": " +a[i] +"\n";
                ausgabe += "true,";
            }
            else{
                //ausgabe += i +"falsch: " +dea.toString() +"\n";
                //erwartet += i +"falsch: " +a[i] +"\n";
                ausgabe += "false,";
            }
        }
        
        erwartet += "true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,false,";
		print("Test10: DEA rückgängig machen", "Test, ob die Methode die letzte Änderung korrekt und maximal 15 Änderungen rückgängig macht", "(DEA)", erwartet, ausgabe);
	}
	
	public static void test11(){
        String erwartet = "", ausgabe = "";
        DEA dea = new DEA("Dea");
		dea.fuegeZustandHinzu(false);
		dea.fuegeZustandHinzu(true);
		dea.fuegeZeichenHinzu("xy");
		dea.fuegeTransitionHinzu("0",'x',"1");
		dea.fuegeTransitionHinzu("0",'y',"1");
		dea.fuegeTransitionHinzu("1",'x',"1");
		dea.fuegeTransitionHinzu("1",'y',"0");
		
		erwartet += dea.toString() +"\n";
		dea.benneneZustandUm("0", "1"); //Erwarte: ändert sich nicht
		ausgabe += dea.toString() +"\n";
		
		dea.benneneZustandUm("0", "2"); //Erwarte: ändert sich
		ausgabe += dea.toString() +"\n";
		
		DEA d = new DEA("Dea");
		d.fuegeZustandHinzu("1", true);
		d.fuegeZustandHinzu("2",false);
		d.fuegeZeichenHinzu("xy");
		d.fuegeTransitionHinzu("1",'x',"1");
		d.fuegeTransitionHinzu("1",'y',"2");
		d.fuegeTransitionHinzu("2",'x',"1");
		d.fuegeTransitionHinzu("2",'y',"1");
		
		erwartet += d.toString();
        print("Test11: Zustand umbennen", "Test, ob sich der Name des Zustands ändert (kein schon vorhandener Name) und der restliche DEA erhalten bleibt", "(DEA)", erwartet, ausgabe);
	}
	
	public static void main (String arv[]){
		test1();
		test2();
		test3();
		test4();
		test5();
		test6();
		test7();
		test8();
		test9();  
		test10();
		test11();
	}
}
