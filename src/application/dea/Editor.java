package application.dea;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout;

public class Editor extends JFrame {

	private JPanel inhalt;
	private JMenuBar menue;
	private DEA dea;
	private Konfiguration konfig;
	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */
	public Editor(Konfiguration k) {
		konfig = k;
		dea =  (DEA) Speicher.lade(konfig.getLetzterDea());
		if(konfig.lade()) { // will be in Hauptklasse
			setSize(konfig.getX(), konfig.getY());
		}
		else {
			this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		}
		createFrame();
		
	}

	private void erstelleBeenden() {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); 
		WindowListener exitListener = new WindowAdapter() {
			Object[] options = {"Speichern und Beenden","Beenden", "Abbrechen"}; // noetig, da Programm deustch( default : Yes/No)
		    @Override
		    public void windowClosing(WindowEvent e) {
		    	//TODO: abfrage, ob bereits gespeichert wurde
		        int confirm = JOptionPane.showOptionDialog(
		             null, "Moechten Sie den DEA-Editor beenden?", 
		             "DEA Beenden", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
		             null, options, options[0]);
		        if (confirm == 0) {

		        	konfig.speichere(e.getWindow().getWidth(),e.getWindow().getHeight(), dea.getName());
		        	dea.speichere(konfig.getArbeitsverzeichnis());
		        	dispose();
		        	e.getWindow().dispose();
		        	System.exit(0);
		        }
		        else if(confirm == 1) {
		        	konfig.speichere(e.getWindow().getWidth(),e.getWindow().getHeight(), dea.getName());
		        	dispose();
		        	e.getWindow().dispose();
			        System.exit(0);
		        }
		    }
		};
		addWindowListener(exitListener);
	}
	
	private void erstelleInhalt() {
		inhalt = new JPanel();
		inhalt.setSize(this.getSize());
		setContentPane(inhalt);
	}
	
	private void erstelleMenue() {
		menue = new JMenuBar();
		JMenu reiter  = new JMenu("Datei");
		JMenuItem item = new JMenuItem("Neu");
		item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("NEU");
			}
		});
		reiter.add(item);
		item = new JMenuItem("Laden");
		item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("LADEN");
			}
		});
		reiter.add(item);
		item = new JMenuItem("Importieren");
		item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("IMPORTIEREN");
			}
		});
		reiter.add(item);
		item = new JMenuItem("Speichern", KeyEvent.VK_S);
		item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("SPEICHERN");
			}
		});
		reiter.add(item);
		menue.add(reiter);
		
		reiter = new JMenu("Einstellungen");
		menue.add(reiter);
		setJMenuBar(menue);
	}
	
	private void createFrame() {
		setTitle("DEA - Editor");
		setLocationRelativeTo(null);
		setMinimumSize(new Dimension(400, 300));
		erstelleBeenden(); 
		erstelleInhalt();
		erstelleMenue();
		setVisible(true);
	}
}
