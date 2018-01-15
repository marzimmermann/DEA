package application.dea;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;

import application.dea.Zustand.ZustandUmhueller;

public class LeinwandDEA extends JPanel {
	private DEA dea;
	private int durchmesser = 70;
	public LeinwandDEA(DEA d){
		super();
		dea = d;
		setBackground(Color.WHITE);
		addMouseListener(new MouseAdapter() {
			private Zustand zustand;
			 @Override
             public void mousePressed(MouseEvent e) {
				 int x = e.getX();
                 int y = e.getY();
				 if(zustand == null){
					 
	                 for( Zustand z : dea.getZustaende()){
	                	 int XMittelpunkt = z.getX()+(durchmesser/2);
	                	 int YMittelpunkt = z.getY()+(durchmesser/2);
	                	 double Distanz = Math.sqrt((XMittelpunkt-x)*(XMittelpunkt-x)+(YMittelpunkt-y)*(YMittelpunkt-y));
	                	 if(Distanz < durchmesser/2)
	                	 {
	                		 zustand = z;
	                		 break;
	                	 }
	                 }
	                 
				 }
				 
                
                 
             }

             @Override
             public void mouseReleased(MouseEvent e) {
            	 if(zustand != null){
            		 int x = e.getX()-durchmesser/2;
            		 int y = e.getY()-durchmesser/2;
            		 /* Verhindert, dass Zustand aus dem Panel gezogen wird */
					 zustand.setX(Math.max(0, Math.min(x, getWidth()-durchmesser)));
					 zustand.setY(Math.max(0, Math.min(y, getHeight()-durchmesser)));
					 repaint();
				 }
            	 zustand = null;
             }
     
		});
	}
	@Override
	public void paintComponent(Graphics g){
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);
		for( Zustand z : dea.getZustaende()){
			if( z.getName().equals(dea.getAktuellerZustand())){
				g.setColor(Color.MAGENTA);
			}
			g.drawOval(z.getX(), z.getY(), durchmesser, durchmesser);
			if(z.istAkzeptierend())
				g.drawOval(z.getX()+3, z.getY()+3, durchmesser-6, durchmesser-6);
			
			//zeiche den Namen des Zustandes
			g.setColor(Color.BLACK);
			int textBreite = g.getFontMetrics().stringWidth(z.getName());
			g.drawString(z.getName(), z.getX()+(durchmesser/2)-textBreite/2, z.getY()+(durchmesser/2)+3);
			
			//zeichne Pfeile
		    int zx = z.getX();
		    int zy = z.getY();
			for (Entry<Character, ZustandUmhueller> entry : z.getTransitionen().entrySet()) {
			    char zeichen = entry.getKey();
			    ZustandUmhueller zUm = entry.getValue();
			    

			}
		}
		
	}
}
