import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ActionEvent;
import javax.swing.UIManager;

/*
 * Startseite.java
 *
 *  Created on: Nov 21, 2017
 *      Author: l.hofer
 *
 */ 

public class Startseite extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel inhaltPanel = new JPanel();
	private JTextField eingabe;
	private JButton okKnopf;
	private static JLabel ueberschrift;

	/**
	 * Create the dialog.
	 */
	public Startseite() {
		setBounds(100, 100, 450, 300);
		setTitle("DEA_Editor - Verzeichniswahl");
		setLocationRelativeTo(null); 			// Positioniert Dialog zentral 
		getContentPane().setLayout(new BorderLayout());
		inhaltPanel.setForeground(Color.WHITE);
		inhaltPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(inhaltPanel, BorderLayout.CENTER);
		inhaltPanel.setLayout(null);
		erstelleUeberschrift();
		erstelleEingabe();
		erstelleOKKnopf();
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // siehe erstelleBeendenKnopf  
		erstelleBeendenKnopf();
		setVisible(true);
	}

	private void erstelleUeberschrift() {
		ueberschrift = new JLabel("DEA-Editor");
		ueberschrift.setBounds(137, 25, 188, 61);
        ueberschrift.setForeground(UIManager.getColor("OptionPane.questionDialog.titlePane.foreground"));
		ueberschrift.setFont(new Font("DialogInput", Font.BOLD | Font.ITALIC, 25)); // Farbe + Groesse
		inhaltPanel.add(ueberschrift);
		JLabel anweisung = new JLabel("Verzeichnis angeben:");
		anweisung.setBounds(12, 128, 125, 17);
		inhaltPanel.add(anweisung);
	}
	
	private void erstelleEingabe() {
		eingabe = new JTextField(System.getProperty("user.dir")); // default aktuelle Verzeichnis
		eingabe.setBounds(155, 119, 277, 36);
		inhaltPanel.add(eingabe);
		eingabe.setColumns(15);
		eingabe.selectAll();
	}
	
	private void erstelleOKKnopf() {
		okKnopf = new JButton("OK");
		okKnopf.setBounds(209, 196, 50, 27);
		okKnopf.setEnabled(true);
		okKnopf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okKnopf.setEnabled(false); // deaktiviert ok Knopf
				dispose(); // Dialog zerstört sich bei OK
			}

		});
		inhaltPanel.add(okKnopf);
		okKnopf.setActionCommand("OK");
		getRootPane().setDefaultButton(okKnopf);
	}

	
	private void erstelleBeendenKnopf() {
		WindowListener exitListener = new WindowAdapter() {
			Object[] options = {"Ja","Nein"}; // noetig, da Programm deustch( default : Yes/No)
		    @Override
		    public void windowClosing(WindowEvent e) {
		        int confirm = JOptionPane.showOptionDialog(
		             null, "Moechten Sie den DEA-Editor beenden?", 
		             "DEA Beenden", JOptionPane.YES_NO_OPTION, 
		             JOptionPane.QUESTION_MESSAGE, null, options, null);
		        if (confirm == 0) {
		           System.exit(0);
		        }
		    }
		};
		addWindowListener(exitListener);
	}

	public String getVerzeichnis() {
		while(okKnopf.isEnabled()) {
			System.out.print("");
		}
		return eingabe.getText();

	}
}
