public class Tests {
	
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
		print("Konfiguration testen", "Test, ob man beim Laden die eben gespeicherte Konfiguration erhaelt", "test123,30L,10,20,testdea", "test123,30L,10,20,testdea", ausgabe);
	}
	
	public static test2(){
		DEA dea = new DEA("meinDea");
		dea.fuegeZustandHinzu(false);
		dea.fuegeZustandHinzu(true);
		dea.fuegeZeichenHinzu("xy");
		dea.fuegeTransitionHinzu("0",'x',"1");
		dea.fuegeTransitionHinzu("0",'y',"1");
		dea.fuegeTransitionHinzu("1",'x',"1");
		dea.fuegeTransitionHinzu("1",'y',"0");
		dea.validiere();
		String ausgabe = "" + dea.istValidiert();
		dea.setStart("0");
		dea.validiere();
		ausgabe += dea.istValidiert();
		dea.loescheTransition("1",'x');
		dea.validiere();
		ausgabe += dea.istValidiert();
		print("DEA validieren", "Methode validieren mit korrekten und falschen DEAs testen", "(DEA)", "false,true,false", ausgabe);
	}
	
	public static test3(){
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
		dea.fuegeTransitionHinzu("8 ",'0',"7");
		dea.fuegeTransitionHinzu("8",'1',"7");
		dea.minimiere();
		
		DEA vergl = new DEA("kleinDEA");
		vergl.fuegeZustandHinzu(false);
		vergl.fuegeZustandHinzu(false);
		vergl.fuegeZustandHinzu(false);
		vergl.fuegeZustandHinzu(false);
		vergl.fuegeZustandHinzu(false);
		vergl.fuegeZustandHinzu(false);
		vergl.fuegeZustandHinzu(true);
		vergl.fuegeZeichenHinzu("01");
		vergl.fuegeTransitionHinzu("0",'0',"1");
		vergl.fuegeTransitionHinzu("0",'1',"2");
		vergl.fuegeTransitionHinzu("1",'0',"3");
		vergl.fuegeTransitionHinzu("1",'1',"3");
		vergl.fuegeTransitionHinzu("2",'0',"4");
		vergl.fuegeTransitionHinzu("2",'1',"4");
		vergl.fuegeTransitionHinzu("3",'0',"5");
		vergl.fuegeTransitionHinzu("3",'1',"6");
		vergl.fuegeTransitionHinzu("4",'0',"6");
		vergl.fuegeTransitionHinzu("4",'1',"5");
		vergl.fuegeTransitionHinzu("5",'0',"6");
		vergl.fuegeTransitionHinzu("5",'1',"6");
		vergl.fuegeTransitionHinzu("6",'0',"6");
		vergl.fuegeTransitionHinzu("6",'1',"6");
		
		String ausgabe = "";
		print("DEA minimieren", "Methode minimieren mit validiertem DEA testen", "(DEA)", "", ausgabe);
	}
	
	
	
	
	
	
	public static void main (String arv[]){
		test1();
		test2();
		test3();
	}
}