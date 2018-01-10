package application.dea;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Fehlermeldung extends JDialog{
	public Fehlermeldung(String fehler) {
		setTitle("Fehler");
		setSize(500 ,  180);
		setLocationRelativeTo(null); 
		getContentPane().setLayout(null);
		setResizable(false);
		JLabel fehlerme = new JLabel(fehler);
		fehlerme.setBounds(12, 12, 470, 80);
		getContentPane().add(fehlerme);
		
		JButton bestaetigen = new JButton("Verstanden");
		bestaetigen.setBounds(198, 113, 96, 27);
		bestaetigen.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		getContentPane().add(bestaetigen);
		this.setVisible(true);
		isAlwaysOnTop();
	}

}

