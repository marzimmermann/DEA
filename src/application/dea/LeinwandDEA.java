package application.dea;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
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
	private int durchmesser = 70, transitionDurchmesser = 20;
	private double Pfeilspitzenlaenge = 20;
	public LeinwandDEA(DEA d){
		super();
		dea = d;
		setBackground(Color.WHITE);
		addMouseListener(new MouseAdapter() {
			private Zustand zustand;
			private ZustandUmhueller zuUm;
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.isPopupTrigger()) {
					System.out.println("Rechtsklick");
				}
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
						
						for (Entry<Character, ZustandUmhueller> entry : z.getTransitionen().entrySet()) {
							ZustandUmhueller zUm = entry.getValue();
							double TranDistanz = getAbstand(new Point(zUm.getX(), zUm.getY()), new Point(x,y));
							if(TranDistanz < transitionDurchmesser/2) {
								zuUm = zUm;
								break;
							}
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
				else if(zuUm != null) {
					zuUm.setX(Math.max(0, Math.min(e.getX(), getWidth()-transitionDurchmesser/2)));
					zuUm.setY(Math.max(0, Math.min(e.getY(), getHeight()-transitionDurchmesser/2)));
					repaint();
				}
				zustand = null;
				zuUm= null;
			}

		});
	}
	@Override
	public void paintComponent(Graphics g){
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);
		for( Zustand z : dea.getZustaende()){
			if(!dea.getAktuellerZustand().equals("")){
				if(z.getName().equals(dea.getAktuellerZustand())){
					g.setColor(Color.MAGENTA);
				}
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
				zeichnePfeile(g, z, zUm.getZustand(), zeichen);

			}
		}
	}

	private void zeichnePfeile(Graphics g, Zustand a, Zustand b, char Transition){
		Point p1 = new Point(a.getX()+durchmesser/2, a.getY()+durchmesser/2);
		Point p2 = new Point(b.getX()+durchmesser/2, b.getY()+durchmesser/2);
		Point mid = new Point(a.getZustandhuelleTransition(Transition).getX(),
				a.getZustandhuelleTransition(Transition).getY());
		//genaue die Mitte der beiden Punkte
		if(mid.getX() == -1 ){
			mid.setLocation((a.getX()+b.getX())/2, (a.getY()+b.getY()) / 2);
		}
		a.getZustandhuelleTransition(Transition).setX((int)mid.getX());
		a.getZustandhuelleTransition(Transition).setY((int)mid.getY());
		g.drawOval((int)mid.getX()-transitionDurchmesser/2, (int)mid.getY()-transitionDurchmesser/2,
				transitionDurchmesser, transitionDurchmesser);
		
		g.drawString(String.valueOf(Transition), (int)mid.getX()-3, (int)mid.getY()+4);
		double angleRad = angle(p1, p2);
		//Berechne und Zeichne zwei Teile des Pfeiles
		Polygon arrowPart1 = CosinusLinie(p1, p1, mid, angleRad, durchmesser/2, transitionDurchmesser/2);
		if(arrowPart1.npoints == 0)
			arrowPart1 = CosinusLinie(p1, mid, p1, angleRad, transitionDurchmesser/2, durchmesser/2);
		g.drawPolyline(arrowPart1.xpoints, arrowPart1.ypoints, arrowPart1.npoints);
		Polygon arrowPart2 = CosinusLinie(p1, mid, p2, angleRad, transitionDurchmesser/2, durchmesser/2);
		if(arrowPart2.npoints == 0)
			arrowPart2 = CosinusLinie(p1, p2, mid, angleRad, durchmesser/2, transitionDurchmesser/2);
		g.drawPolyline(arrowPart2.xpoints, arrowPart2.ypoints, arrowPart2.npoints);

		//PFEILSPITZE
		double Pfeilwinkel = 30;
		double WinkelRad = Pfeilwinkel*Math.PI/180;

		//Hole letzten Punkt
		int index = arrowPart2.npoints-1;
		if(index >= 0)
		{
			Point letzter = new Point(arrowPart2.xpoints[index], arrowPart2.ypoints[index]);
			Point erster = new Point(arrowPart2.xpoints[0], arrowPart2.ypoints[0]);
			Point pfeilspitze;
			if(getAbstand(erster, p2) < getAbstand(letzter, p2))
				pfeilspitze = erster;
			else
				pfeilspitze = letzter;

			//Berechne Pfeilwinkel
			double arrowAngle = angle(pfeilspitze, p2);

			//Berechne spitzen
			
			Point pfeilkante = new Point((int)(pfeilspitze.getX()-Pfeilspitzenlaenge), (int) pfeilspitze.getY());
			pfeilkante = rotatePoint(pfeilkante, pfeilspitze, arrowAngle);

			Point Pfeilkante1 = rotatePoint(pfeilkante, pfeilspitze, WinkelRad);
			Point Pfeilkante2 = rotatePoint(pfeilkante, pfeilspitze, -WinkelRad);

			g.drawLine((int)Pfeilkante1.getX(), (int)Pfeilkante1.getY(), (int)pfeilspitze.getX(), (int)pfeilspitze.getY());
			g.drawLine((int)Pfeilkante2.getX(), (int)Pfeilkante2.getY(), (int)pfeilspitze.getX(), (int)pfeilspitze.getY());
		}
	}

	public Point rotatePoint(Point pt, Point center, double angleRad)
	{
		//double angleRad = (angleDeg*Math.PI/180);
		double cosAngle = Math.cos(angleRad );
		double sinAngle = Math.sin(angleRad );
		double dx = (pt.x-center.x);
		double dy = (pt.y-center.y);

		Point p2 = new Point();//don't change pt, it will create bugs
		p2.x = center.x + (int) (dx*cosAngle-dy*sinAngle);
		p2.y = center.y + (int) (dx*sinAngle+dy*cosAngle);
		return p2;
	}

	public Polygon CosinusLinie(Point rotationPoint, Point from, Point to, double angle, int distfrom, int distto)
	{
		Polygon ret = new Polygon();
		
		//Rotiere um den rotationspunkt
		Point start = rotatePoint(from, rotationPoint, -angle);//rotiere startpunkt um rotationspunkt
		Point end = rotatePoint(to, rotationPoint, -angle);

		//Berechne die Schwingung des Cosinus
		double altitude = (start.getY()-end.getY())/2;
		for(int i = 0; i < end.getX()-start.getX(); ++i)
		{
			//Berechne y wert, der Wert im Cosinus ist immer [0, 1]
			int y = (int) (altitude*Math.cos(i*Math.PI/(end.getX()-start.getX())) + start.getY()-altitude);

			//Berechne neuen Punkt
			Point cospart = new Point((int) (i+start.getX()), y);

			//Rotiere zurueck
			Point rotcospart = rotatePoint(cospart, rotationPoint, angle);

			if(getAbstand(from, rotcospart) >= distfrom && getAbstand(to, rotcospart) >= distto)//Fuege Punkt nur hinzu falls nicht in anderen Punkten
				ret.addPoint((int)rotcospart.getX(), (int)rotcospart.getY());
		}
		return ret;
	}

	public double getAbstand(Point p1, Point p2)
	{
		return Math.sqrt((p1.getX()-p2.getX())*(p1.getX()-p2.getX()) + (p1.getY() - p2.getY())*(p1.getY() - p2.getY()));
	}

	public double angle(Point p1, Point p2)
	{
		return Math.atan2(p2.getY()-p1.getY(), p2.getX()-p1.getX());
	}



}
