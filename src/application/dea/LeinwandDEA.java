package application.dea;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
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
	private int Pfeilpunktwahl = 10;
	private double Pfeilwinkel = 30;
	private double WinkelRad = Pfeilwinkel*Math.PI/180;
	private double selbstOvaldicke = 40;//Durchmesser des Ovals an der orthogonalen achse zwischen Zustand und Mitte
	private double selbstAbstand = 40;//Mindestabstand der Mitte zum Zustand
	private int fontSize = 20;

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
	public void paintComponent(Graphics gr){
		super.paintComponent(gr);

		Graphics2D g = (Graphics2D) gr;

		//Bestimmte Schrifftart und textgroesse
		g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize)); 

		//Mache Kanten weich
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);
		for( Zustand z : dea.getZustaende()){
			if(!dea.getAktuellerZustand().equals("")){
				if(z.getName().equals(dea.getAktuellerZustand())){
					g.setColor(Color.MAGENTA);
				}
			}
			Point p = new Point(z.getX()+durchmesser/2, z.getY()+durchmesser/2);
			zeichneZustand(g, p, z.getName(), durchmesser, z.istAkzeptierend());

			//zeichne Pfeile
			for (Entry<Character, ZustandUmhueller> entry : z.getTransitionen().entrySet()) {
				char zeichen = entry.getKey();
				ZustandUmhueller zUm = entry.getValue();
				zeichnePfeile(g, z, zUm.getZustand(), zeichen);
			}
		}
	}

	/**
	 * Zeichnet Pfeil von Zustand a auf Zustand b ueber Transition 
	 * @param g Grafik
	 * @param a Zustand
	 * @param b Zustand
	 * @param Transition Transition
	 */
	private void zeichnePfeile(Graphics2D g, Zustand a, Zustand b, char Transition){

		//Pruefe ob zustand auf sich selbst zeigt
		if(a.getName().equals(b.getName()))
		{
			zeichnePfeilSelbst(g, a, Transition);
			return;
		}
		Point p1 = new Point(a.getX()+durchmesser/2, a.getY()+durchmesser/2);
		Point p2 = new Point(b.getX()+durchmesser/2, b.getY()+durchmesser/2);
		Point mid = new Point(a.getZustandhuelleTransition(Transition).getX(),
				a.getZustandhuelleTransition(Transition).getY());

		//berechne und setzte die Mitte der beiden Punkte
		if(mid.getX() == -1 ){
			mid.setLocation((p1.getX()+p2.getX())/2, (p1.getY()+p2.getY()) / 2);
			a.getZustandhuelleTransition(Transition).setX((int)mid.getX());
			a.getZustandhuelleTransition(Transition).setY((int)mid.getY());
		}

		//Zeichne tranisionskreis
		zeichneTransition(g, mid, Transition);

		//Berechne Winkel zwischen zustaenden
		double angleRad = angle(p1, p2);

		//Berechne und Zeichne zwei Teile des Pfeiles:
		//Von zustand 1 bis zur Mitte
		Polygon linie1 = CosinusLinie(p1, p1, mid, angleRad, durchmesser/2, transitionDurchmesser/2);
		if(linie1.npoints == 0)
			linie1 = CosinusLinie(p1, mid, p1, angleRad, transitionDurchmesser/2, durchmesser/2);
		g.drawPolyline(linie1.xpoints, linie1.ypoints, linie1.npoints);

		//Von der Mitte bis Zustand 2
		Polygon linie2 = CosinusLinie(p1, mid, p2, angleRad, transitionDurchmesser/2, durchmesser/2);
		if(linie2.npoints == 0)
			linie2 = CosinusLinie(p1, p2, mid, angleRad, durchmesser/2, transitionDurchmesser/2);
		g.drawPolyline(linie2.xpoints, linie2.ypoints, linie2.npoints);

		//Berechne Pfeilspitzenkoordinaten
		int index = linie2.npoints-1;//Anzahl der Punkte von linie2
		if(index >= 1)
		{
			int korrektur = Math.min(index, Pfeilpunktwahl);//Gehe 'Pfeilpunktwahl' schritte zurueck um einen Punkt fuer die Pfeilrichtung zu suchen
			Point letzter = new Point(linie2.xpoints[index], linie2.ypoints[index]);
			Point vorletzter = new Point(linie2.xpoints[index-korrektur], linie2.ypoints[index-korrektur]);
			Point erster = new Point(linie2.xpoints[0], linie2.ypoints[0]);
			Point zweiter = new Point(linie2.xpoints[korrektur], linie2.ypoints[korrektur]);
			Point pfeilspitze, pfeilende;

			//Sonderfall: Der Mittelpunkt liegt nicht mehr zwischen den Zustaenden
			if(getAbstand(erster, p2) < getAbstand(letzter, p2))
			{	
				pfeilspitze = erster;
				pfeilende = zweiter;
			}
			else
			{
				pfeilspitze = letzter;
				pfeilende = vorletzter;
			}
			zeichnePfeilspitze(g, pfeilspitze, pfeilende);
		}
	}

	void zeichnePfeilSelbst(Graphics2D g, Zustand a, char Transition)
	{
		Point p1 = new Point(a.getX()+durchmesser/2, a.getY()+durchmesser/2);
		Point mid = new Point(a.getZustandhuelleTransition(Transition).getX(),
				a.getZustandhuelleTransition(Transition).getY());

		//berechne und setzte die Mitte irgendwo
		if(mid.getX() == -1 ){
			mid.setLocation(p1.getX(), p1.getY()-durchmesser/2-transitionDurchmesser/2-selbstAbstand);
			a.getZustandhuelleTransition(Transition).setX((int)mid.getX());
			a.getZustandhuelleTransition(Transition).setY((int)mid.getY());
		}

		//Zeichne Transition
		zeichneTransition(g, mid, Transition);

		//Berechne und Zeichne zwei Teile des Pfeiles:
		//Von zustand 1 bis zur Mitte
		Polygon linie1 = Halbkreis(p1, mid, durchmesser/2, transitionDurchmesser/2);
		g.drawPolyline(linie1.xpoints, linie1.ypoints, linie1.npoints);

		//Von der Mitte bis Zustand 1
		Polygon linie2 = Halbkreis(mid, p1, transitionDurchmesser/2, durchmesser/2);
		g.drawPolyline(linie2.xpoints, linie2.ypoints, linie2.npoints);

		//Berechne Pfeilspitzenkoordinaten
		int index = linie2.npoints-1;//Anzahl der Punkte von linie2
		if(index >= 1)
		{
			int korrektur = Math.min(index, Pfeilpunktwahl);//Gehe 'Pfeilpunktwahl' schritte zurueck um einen Punkt fuer die Pfeilrichtung zu suchen
			Point pfeilspitze = new Point(linie2.xpoints[index], linie2.ypoints[index]);
			Point pfeilende = new Point(linie2.xpoints[index-korrektur], linie2.ypoints[index-korrektur]);
			zeichnePfeilspitze(g, pfeilspitze, pfeilende);
		}
	}

	public void zeichneTransition(Graphics2D g, Point p, char Transition)
	{	
		zeichneZustand(g, p, String.valueOf(Transition), transitionDurchmesser);
	}
	public void zeichneZustand(Graphics2D g, Point p, String text, int durchmesser)
	{
		zeichneZustand(g, p, text, durchmesser, false);
	}

	public void zeichneZustand(Graphics2D g, Point p, String text, int durchmesser, boolean akzeptierend)
	{
		g.drawOval((int) (p.getX()-durchmesser/2), (int)p.getY()-durchmesser/2, durchmesser, durchmesser);
		if(akzeptierend)
			g.drawOval((int)p.getX()-durchmesser/2+3, (int)p.getY()-durchmesser/2+3, durchmesser-6, durchmesser-6);

		//zeiche den Namen des Zustandes
		g.setColor(Color.BLACK);
		int textBreite = g.getFontMetrics().stringWidth(text);
		//g.drawString(text, (int)p.getX()-textBreite/2, (int)p.getY()+3);
		zeichneString(g, text, (int)p.getX(), (int)p.getY(), durchmesser);
	}

	public void zeichnePfeilspitze(Graphics2D g, Point pfeilspitze, Point b)
	{
		//Pfeilwinkel
		double angle = angle(b, pfeilspitze);

		//Berechne spitzen

		Point pfeilkante = new Point((int)(pfeilspitze.getX()-Pfeilspitzenlaenge), (int) pfeilspitze.getY());
		pfeilkante = rotatePoint(pfeilkante, pfeilspitze, angle);

		Point Pfeilkante1 = rotatePoint(pfeilkante, pfeilspitze, WinkelRad);
		Point Pfeilkante2 = rotatePoint(pfeilkante, pfeilspitze, -WinkelRad);

		//Schoenes gefuelltes Dreieck als spitze
		Polygon p = new Polygon();
		p.addPoint((int)pfeilspitze.getX(), (int)pfeilspitze.getY());
		p.addPoint((int)Pfeilkante1.getX(), (int)Pfeilkante1.getY());
		p.addPoint((int)Pfeilkante2.getX(), (int)Pfeilkante2.getY());

		g.fillPolygon(p);
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
		for(int i = 0; i <= end.getX()-start.getX(); i+=2)
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

	private double kreis(double x)
	{
		return Math.sqrt(1-x*x);
	}

	private Polygon Halbkreis(Point p1, Point p2, int dist1, int dist2) {
		Polygon ret = new Polygon();

		//Berechne Rotationswinkel
		double angle = angle(p1, p2);

		//Rotiere um den rotationspunkt
		Point start = p1;//rotiere startpunkt um rotationspunkt
		Point end = rotatePoint(p2, p1, -angle);

		//Berechne dicke des ovals
		double altitude = selbstOvaldicke/2;

		double range = end.getX()-start.getX();
		for(int i = 0; i <= range; i+=2)
		{
			//Berechne y wert, der Wert von x darf nur zwischen [-1, 1] liegen
			double x = 2*i/range-1; 
			int y = (int) (altitude*kreis(x) + start.getY());

			//Berechne neuen Punkt
			Point kreispunkt = new Point((int) (i+start.getX()), y);

			//Rotiere zurueck
			Point rotkreispunkt = rotatePoint(kreispunkt, p1, angle);

			if(getAbstand(p1, rotkreispunkt) >= dist1 && getAbstand(p2, rotkreispunkt) >= dist2)//Fuege Punkt nur hinzu falls nicht in anderen Punkten
				ret.addPoint((int)rotkreispunkt.getX(), (int)rotkreispunkt.getY());
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

	/**
	 * Berechne groesse und breite des Strings str
	 * @param g2
	 * @param str
	 * @param x
	 * @param y
	 * @return
	 */
	private Rectangle getStringBounds(Graphics2D g2, String str,
			float x, float y)
	{
		FontRenderContext frc = g2.getFontRenderContext();
		GlyphVector gv = g2.getFont().createGlyphVector(frc, str);
		return gv.getPixelBounds(null, x, y);
	}
	/**
	 * @param g
	 * @param str
	 * @param x
	 * @param y
	 * @param maxsize
	 */
	private void zeichneString(Graphics2D g, String str, int x, int y, int maxsize)
	{
		int size = fontSize;
		Rectangle rec = getStringBounds(g, str, x, y);
		while(rec.getWidth() > maxsize)
		{
			size--;
			g.setFont(new Font("TimesRoman", Font.PLAIN, size));
			rec = getStringBounds(g, str, x, y);
		}
		g.drawString(str, (int)(x-rec.getWidth()/2), (int)(y+rec.getHeight()/2));
		g.setFont(getDefaultFont());//Reset font
	}
	/**
	 * 
	 * @return
	 */
	private Font getDefaultFont()
	{
		return new Font("TimesRoman", Font.PLAIN, fontSize); 
	}
}
