package application.dea;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Desktop;
import java.awt.Dimension;
import javax.print.attribute.standard.JobImpressionsCompleted;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Image;

public class Editor extends JFrame {
	private JPanel inhalt;
	private JMenuBar menue;
	private DEA dea;
	private String letzterGespeicherterDEA;
	private Konfiguration konfig;
	private JToolBar symbolleiste, eingabeLeiste;
	private LeinwandDEA leinwand;
	private JTextField eingabe;
	private String fileSeperator = System.getProperty("file.separator");
	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */
	public Editor(Konfiguration k) {
		konfig = k;
		dea = new DEA("");
		dea = dea.lade(konfig.getArbeitsverzeichnis(), konfig.getLetzterDea());
		if(dea == null) { 
			this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
			dea = new DEA("");
		}
		else {
			if(dea.getName() !=""){
				letzterGespeicherterDEA = dea.getName();
			}
			setSize(konfig.getX(), konfig.getY());
		}
		createFrame();

	}

	private void erstelleBeenden() {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); 
		WindowListener exitListener = new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				if(!dea.istGespeichert()){
					Object[] options = {"Speichern und Beenden","Beenden", "Abbrechen"}; // noetig, da Programm deustch( default : Yes/No)
					int confirm = JOptionPane.showOptionDialog(
							null, "Moechten Sie den DEA-Editor ungespeichert beenden?", 
							"DEA Beenden", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
							null, options, options[0]);
					if (confirm == 0) {
						if(speichereDEA()){
							dispose();
							e.getWindow().dispose();
							System.exit(0);
						}
					}
					else if(confirm == 1) {
						if(letzterGespeicherterDEA == null){
							konfig.speichere(e.getWindow().getWidth(),e.getWindow().getHeight(), "");
						}
						else{
							konfig.speichere(e.getWindow().getWidth(),e.getWindow().getHeight(), letzterGespeicherterDEA);
						}
						dispose();
						e.getWindow().dispose();
						System.exit(0);
					}
				}

				else{
					Object[] options = {"Beenden", "Abbrechen"};
					int confirm = JOptionPane.showOptionDialog(
							null, "Moechten Sie den DEA-Editor beenden?", 
							"DEA Beenden", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
							null, options, options[0]);
					if (confirm == 0) {
						dispose();
						e.getWindow().dispose();
						System.exit(0);
					}
				}
			}
		};
		addWindowListener(exitListener);
	}

	private void erstelleInhalt() {
		inhalt = new JPanel(new BorderLayout());
		inhalt.setSize(this.getSize());
		setContentPane(inhalt);
	}

	private void erstelleMenue() {
		menue = new JMenuBar();
		JMenu reiter  = new JMenu("Datei");
		JMenuItem item = new JMenuItem("Neu", KeyEvent.VK_N);
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(!dea.istGespeichert()){
					Object[] options = {"Speichern/Neu","Verwerfen/Neu", "Abbrechen"};
					int confirm = JOptionPane.showOptionDialog(
							null, "Moechten Sie Aenderungen verwerfen oder speichern?", 
							"Neuer DEA", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
							null, options, options[0]);	
					if(confirm == 0){
						if(!speichereDEA()){
							return;
						}
					}
					if(confirm == 0 || confirm == 1){
						DEA tmp = new DEA("");
						if(speichereDEA("Neuen DEA erstellen", tmp)){
							dea = tmp;
						}
					}
				}

			}
		});
		reiter.add(item);
		item = new JMenuItem("Laden", KeyEvent.VK_L);
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(!dea.istGespeichert()){
					Object[] options = {"Speichern/Laden","Verwerfen/Laden", "Abbrechen"};
					int confirm = JOptionPane.showOptionDialog(
							null, "Moechten Sie Aenderungen verwerfen oder speichern?", 
							"DEA laden", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
							null, options, options[0]);	
					if(confirm == 0){
						if(!speichereDEA()){
							return;
						}
					}
					if(confirm == 0 || confirm == 1){
						JFileChooser auswahl = new JFileChooser();
						FileNameExtensionFilter filter = new FileNameExtensionFilter("Nur DEA Dateien", ".dea");
						auswahl.setFileFilter(filter);
						auswahl.setCurrentDirectory(new File(konfig.getArbeitsverzeichnis()));
						try{
							auswahl.showOpenDialog( null );
						}
						catch(IndexOutOfBoundsException exce){
							JOptionPane.showMessageDialog(getRootPane(), "Fehlerhafter Ordner", "Fehler", JOptionPane.ERROR_MESSAGE);
						}
						if(auswahl.getSelectedFile() != null){
							DEA tmp = (DEA) Speicher.lade(auswahl.getSelectedFile().toString());
							if(tmp != null){
								if(speichereDEA("Geladener DEA hat keinen Namen", tmp)){
									dea = tmp;
									konfig.setArbeitsverzeichnis(auswahl.getCurrentDirectory().toString());
								}
								else{
									JOptionPane.showMessageDialog(getRootPane(), "Fehlerhafter Datei. Laden nicht moeglich", "Fehler", JOptionPane.ERROR_MESSAGE);
								}
							}
							else{
								JOptionPane.showMessageDialog(getRootPane(), "Fehlerhafter Datei. Laden nicht moeglich", "Fehler", JOptionPane.ERROR_MESSAGE);
							}
						}

					}
				}

			}
		});
		reiter.add(item);
		item = new JMenuItem("Importieren", KeyEvent.VK_I);
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser auswahl = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Nur DEA Dateien", ".dea");
				auswahl.setFileFilter(filter);
				auswahl.setCurrentDirectory(new File(konfig.getArbeitsverzeichnis()));
				try{
					auswahl.showOpenDialog( null );
				}
				catch(IndexOutOfBoundsException exce){
					JOptionPane.showMessageDialog(getRootPane(), "Fehlerhafter Ordner", "Fehler", JOptionPane.ERROR_MESSAGE);
				}
				if(auswahl.getSelectedFile() != null){
					DEA tmp = (DEA) Speicher.lade(auswahl.getSelectedFile().toString());
					if(tmp != null){
						dea.importiere(tmp);
					}
					else{
						JOptionPane.showMessageDialog(getRootPane(), "Fehlerhafter Datei. Importieren nicht moeglich", "Fehler", JOptionPane.ERROR_MESSAGE);
					}
				}
				JOptionPane.showMessageDialog(getRootPane(), "Fehlerhafter Datei. Importieren nicht moeglich", "Fehler", JOptionPane.ERROR_MESSAGE);
			}
		});
		reiter.add(item);
		item = new JMenuItem("Speichern", KeyEvent.VK_S);
		item.setAccelerator(
				KeyStroke.getKeyStroke( 'S', InputEvent.CTRL_DOWN_MASK )
		);
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				speichereDEA();
			}
		});
		reiter.add(item);
		item = new JMenuItem("Speichern und Beenden", KeyEvent.VK_U);
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(dea.istGespeichert()){
					dispose();
					System.exit(0);
				}
				else if(speichereDEA()){
					dispose();
					System.exit(0);
				}
			}
		});
		reiter.add(item);
		item = new JMenuItem("Beenden", KeyEvent.VK_B);
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(dea.istGespeichert()){
					dispose();
					System.exit(0);
				}
				else {
					Object[] options = {"Speichern und Beenden","Beenden", "Abbrechen"}; // noetig, da Programm deustch( default : Yes/No)
					int confirm = JOptionPane.showOptionDialog(
							null, "Moechten Sie den DEA-Editor ungespeichert beenden?", 
							"DEA Beenden", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
							null, options, options[0]);
					if (confirm == 0) {
						if(speichereDEA()){
							dispose();
							System.exit(0);
						}
					}
					else if(confirm == 1) {
						if(letzterGespeicherterDEA == null){
							konfig.speichere(getWidth(),getHeight(), "");
						}
						else{
							konfig.speichere(getWidth(),getHeight(), letzterGespeicherterDEA);
						}
						dispose();
						System.exit(0);
					}
				}
			}
		});
		reiter.add(item);
		menue.add(reiter);
		reiter = new JMenu("Einstellungen");
		item = new JMenuItem("Konfigurationen anzeigen", KeyEvent.VK_K);
		item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		reiter.add(item);
		item = new JMenuItem("Zustandsuebergangsdauer aendern", KeyEvent.VK_Z);
		item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		reiter.add(item);
		item = new JMenuItem("Arbeitsverzeichnis aendern", KeyEvent.VK_A);
		item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		reiter.add(item);
		menue.add(reiter);
		
		reiter = new JMenu("Hilfe");
		item = new JMenuItem("Ueber DEAs", KeyEvent.VK_U);
		item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Desktop desktop = Desktop.getDesktop();
	            if (desktop != null && desktop.isSupported(Desktop.Action.OPEN)) {
	                try {
						desktop.open(new File("src"+fileSeperator+"data"+fileSeperator+"Was ist ein DEA.pdf"));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	            }
			}
		});
		reiter.add(item);
		item = new JMenuItem("Programmhinweise", KeyEvent.VK_H);
		item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		reiter.add(item);
		item = new JMenuItem("Impressum", KeyEvent.VK_I);
		item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String impressum = "TODO: Impressum";
				JOptionPane.showMessageDialog(null, impressum, "Impressum", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		reiter.add(item);
		menue.add(reiter);
		setJMenuBar(menue);
	}

	private void erstelleSymbolleiste(){
		symbolleiste = new JToolBar("Symbolleiste");
		String fileSeperator = System.getProperty("file.separator");
		String pfadIcons = "src"+fileSeperator+"data"+
				fileSeperator+"icons"+fileSeperator;
		symbolleiste.addSeparator();
		ImageIcon img  = new ImageIcon(pfadIcons+"Backbutton.png");
		Image i = img.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
		JButton button = new JButton(new ImageIcon(i));
		button.setToolTipText("Rueckgaenging");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO

			}
		});
		symbolleiste.add(button);
		symbolleiste.addSeparator();
		img  = new ImageIcon(pfadIcons+"stopbutton.png");
		i = img.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
		button = new JButton(new ImageIcon(i));
		button.setToolTipText("Stop");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dea.stoppe();
			}
		});
		symbolleiste.add(button);
		symbolleiste.addSeparator();
		img  = new ImageIcon(pfadIcons+"playbutton.png");
		i = img.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
		button = new JButton(new ImageIcon(i));
		button.setToolTipText("Starte");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dea.starte(eingabe.getText());
			}
		});
		symbolleiste.add(button);
		symbolleiste.addSeparator();
		img  = new ImageIcon(pfadIcons+"stepplaybutton.png");
		i = img.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
		button = new JButton(new ImageIcon(i));
		button.setToolTipText("Step");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dea.geheWeiter();
			}
		});
		symbolleiste.add(button);
		symbolleiste.addSeparator();
		img  = new ImageIcon(pfadIcons+"validatebutton2.png");
		i = img.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
		button = new JButton(new ImageIcon(i));
		button.setToolTipText("Validiere");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(!dea.validiere()){
					JOptionPane.showMessageDialog(null, "Der DEA ist nicht vollstaendig und ist somit  nicht validiert.", 
							"DEA - Validieren", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		symbolleiste.add(button);
		symbolleiste.addSeparator();
		img  = new ImageIcon(pfadIcons+"minibutton.png");
		i = img.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
		button = new JButton(new ImageIcon(i));
		button.setToolTipText("Minimiere");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DEA tmp = dea.minimiere();
				if(tmp != null){
					dea = tmp;
					leinwand.repaint();
				}
				else{
					if(dea.istValidiert()){
                        JOptionPane.showInternalMessageDialog(null, "DEA muss zum minimieren validiert werden",
                        "DEA minimieren fehlgeschlagen", JOptionPane.ERROR_MESSAGE);
                    }
				}
				
			}
		});
		symbolleiste.add(button);
		symbolleiste.addSeparator();
		img  = new ImageIcon(pfadIcons+"Circle.png");
		i = img.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
		button = new JButton(new ImageIcon(i));
		button.setToolTipText("Zustand hinzufuegen");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String zName = JOptionPane.showInputDialog(getContentPane(), "Geben Sie einen Namen fuer den Zustand ein:",
						"Neuer Zustand");
				if(zName != null){
					if(dea.fuegeZustandHinzu(zName, false)){
						leinwand.repaint();
					}
					else{
						JOptionPane.showMessageDialog(getContentPane(), "Hinzufuegen des Zustandes war nicht erfolgreich",
								"Fehler beim Hinzufuegen", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		symbolleiste.add(button);
		symbolleiste.addSeparator();
		img  = new ImageIcon(pfadIcons+"DoubleCircle.png");
		i = img.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
		button = new JButton(new ImageIcon(i));
		button.setToolTipText("Zustand hinzufuegen");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String zName = JOptionPane.showInputDialog(getContentPane(), "Geben Sie einen Namen fuer den Zustand ein:",
						"Neuer Zustand");
				if(zName != null){
					if(dea.fuegeZustandHinzu(zName, true)){
						leinwand.repaint();
					}
					else{
						JOptionPane.showMessageDialog(getContentPane(), "Hinzufuegen des Zustandes war nicht erfolgreich",
								"Fehler beim Hinzufuegen", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		symbolleiste.add(button);
		symbolleiste.addSeparator();
		img  = new ImageIcon(pfadIcons+"Arrow.png");
		i = img.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
		button = new JButton(new ImageIcon(i));
		button.setToolTipText("Transition hinzufuegen");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField von = new JTextField();
				JTextField ueber = new JTextField(1);
				JTextField zu = new JTextField();
				Object[] message = {
				    " Von : ", von,
				    " Ueber : ", ueber,
				    " Zu : ", zu,
				};
				int option = JOptionPane.showConfirmDialog(null, message, "Transition hinzufuegen", JOptionPane.OK_CANCEL_OPTION);
				if (option == JOptionPane.OK_OPTION) {
				    if (!dea.fuegeTransitionHinzu(von.getText(),
				    		ueber.getText().charAt(0), zu.getText())) {
				    	JOptionPane.showMessageDialog(null, 
				    			"Hinzufuegen der Transition war nicht erfolgreich",
				    			"Fehler", JOptionPane.WARNING_MESSAGE);
				    }
				}
			}
		});
		symbolleiste.add(button);
		symbolleiste.addSeparator();
		img  = new ImageIcon(pfadIcons+"StartArrow.png");
		i = img.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
		button = new JButton(new ImageIcon(i));
		button.setToolTipText("Transition hinzufuegen");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO
			}
		});
		symbolleiste.add(button);
		symbolleiste.addSeparator();
		
		
		
		
		inhalt.add(symbolleiste, BorderLayout.PAGE_START);
	}

	private void erstelleEingabeLeiste(){
		eingabe = new JTextField();
		eingabeLeiste  = new JToolBar();
		eingabeLeiste.add(new JLabel("\tEingabe :\t"));
		eingabe.setEditable(true);
		eingabe.setToolTipText("Eingabe");
		eingabeLeiste.add(eingabe);
		inhalt.add(eingabeLeiste, BorderLayout.PAGE_END);
	}
	
	public boolean speichereDEA(){
		return speichereDEA("DEA speichern als", dea);
	}

	public boolean speichereDEA(String titel, DEA d){
		while(d.getName() == ""){
			String deaName = (JOptionPane.showInputDialog(this.getContentPane(),
					"Benennen Sie den DEA :",titel, JOptionPane.PLAIN_MESSAGE));
			if(deaName == null){
				return false;
			}
			else if(deaName == ""){
				JOptionPane.showInputDialog(this, "Ungueltiger Name", "Ungueltiger Name", JOptionPane.ERROR_MESSAGE);
				continue;
			}
			d.setName(deaName );
		}
		d.speichere(konfig.getArbeitsverzeichnis());
		konfig.speichere(getWidth(), getHeight(), d.getName());
		letzterGespeicherterDEA = d.getName();
		return true;
	}
	
	
	
	private void createFrame() {
		setTitle("DEA - Editor");
		setLocationRelativeTo(null);
		setMinimumSize(new Dimension(400, 300));
		erstelleBeenden(); 
		erstelleInhalt();
		erstelleMenue();
		erstelleSymbolleiste();

		inhalt.add(leinwand = new LeinwandDEA(dea));
		erstelleEingabeLeiste();
		setVisible(true);
	}
}
