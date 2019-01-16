package polygon;

import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class FilledPolygonRenderer implements PolygonRenderer{
	private int argb;
	private double mL;
	private double mR;
	private double mrL, mgL, mbL, mrR, mgR, mbR;

	private double zL, zR;
//	private int leftCount = 0;
//	private int rightCount = 0;
	private Drawable drawable;
//	private Polygon polygon;


//	private static int count = 100;
/*
	@Override
	public void drawPolygon(Polygon polygon, Drawable drawable, Shader vertexShader) {

		//assume triangle
		Chain leftChain = polygon.leftChain();
		Chain rightChain = polygon.rightChain();
		Vertex3D p1 = leftChain.get(0);
		Vertex3D p2 = leftChain.get(1);
		Vertex3D p3 = rightChain.get(1);
		Vertex3D q;
		double r = p1.getColor().getR()/3 + p2.getColor().getR()/3 + p3.getColor().getR();
		double b = p1.getColor().getB()/3 + p2.getColor().getB()/3 + p3.getColor().getB();
		double g = p1.getColor().getG()/3 + p2.getColor().getG()/3 + p3.getColor().getG();
		int argb = new Color(r, g, b).asARGB();
		if (p1.getIntY() == p2.getIntY()) {
			drawPart2(p3, p1, p2, drawable, argb);
		}
		else if (p1.getIntY() == p3.getIntY()) {
			drawPart2(p2, p1, p3, drawable, argb);
		}
		else if (p2.getIntY() == p3.getIntY()) {
			drawPart1(p1, p2, p3, drawable, argb);
		}
		else {
			double mL = (p1.getX()-p2.getX()) / (p1.getY()-p2.getY());
			double mR = (p1.getX()-p3.getX()) / (p1.getY()-p3.getY());
			if (p2.getIntY() > p3.getIntY()) {
				q = new Vertex3D(p1.getX()+(p2.getY()-p1.getY())*mR, p2.getY(), 0.0, p1.getColor());
				drawPart1(p1, p2, q, drawable, argb);
				drawPart2(p3, p2, q, drawable, argb);
			}
			else {
				q = new Vertex3D(p1.getX()+(p3.getY()-p1.getY())*mL, p3.getY(), 0.0, p1.getColor());
				drawPart1(p1, p3, q, drawable, argb);
				drawPart2(p2, p3, q, drawable, argb);
			}
		}
	}
*/
	@Override
	public void drawPolygon(Polygon polygon, Drawable drawable, Shader vertexShader){
//		this.polygon = polygon;
		this.drawable = drawable;
		int leftCount = 0, rightCount = 0;

		Chain leftChain = polygon.leftChain();
		Chain rightChain = polygon.rightChain();

//		System.out.println(leftChain.length());
//		System.out.println(rightChain.length());
		if (leftChain.length() == 1 || rightChain.length() == 1) {
//			System.out.println("\t!!");
			return;
		}

		Vertex3D l1 = leftChain.get(leftCount++);
		Vertex3D l2 = leftChain.get(leftCount++);
		Vertex3D r1 = rightChain.get(rightCount++);
		Vertex3D r2 = rightChain.get(rightCount++);
		Vertex3D q;
//		double r = 0, g = 0, b = 0;
//		for (Vertex3D p : leftChain.vertices) {
//			r += p.getColor().getR();
//			g += p.getColor().getG();
//			b += p.getColor().getB();
//		}
//		for (Vertex3D p : rightChain.vertices) {
//			r += p.getColor().getR();
//			g += p.getColor().getG();
//			b += p.getColor().getB();
//		}
//		r /= leftChain.length()+rightChain.length();
//		g /= leftChain.length()+rightChain.length();
//		b /= leftChain.length()+rightChain.length();
//		r = (r-leftChain.get(0).getColor().getR()-leftChain.get(leftChain.length()-1).getColor().getR()) / (leftChain.length()+rightChain.length()-2);
//		g = (r-leftChain.get(0).getColor().getG()-leftChain.get(leftChain.length()-1).getColor().getG()) / (leftChain.length()+rightChain.length()-2);
//		b = (r-leftChain.get(0).getColor().getB()-leftChain.get(leftChain.length()-1).getColor().getB()) / (leftChain.length()+rightChain.length()-2);
//		argb = new Color(r, g, b).asARGB();
		mL = (l1.getX()-l2.getX()) / (l1.getY()-l2.getY());
		mR = (r1.getX()-r2.getX()) / (r1.getY()-r2.getY());

		zL = (l1.getZ()-l2.getZ()) / calcDistance2D(l1, l2);
		zR = (r1.getZ()-r2.getZ()) / calcDistance2D(r1, r2);

		mrL = (l1.getColor().getR()-l2.getColor().getR()) / (l1.getY()-l2.getY());
		mgL = (l1.getColor().getG()-l2.getColor().getG()) / (l1.getY()-l2.getY());
		mbL = (l1.getColor().getB()-l2.getColor().getB()) / (l1.getY()-l2.getY());

		mrR = (r1.getColor().getR()-r2.getColor().getR()) / (r1.getY()-r2.getY());
		mgR = (r1.getColor().getG()-r2.getColor().getG()) / (r1.getY()-r2.getY());
		mbR = (r1.getColor().getB()-r2.getColor().getB()) / (r1.getY()-r2.getY());

		while (leftCount < leftChain.length() && rightCount < rightChain.length()){
			if (l2.getY() > r2.getY()) {
				double tmp = l2.getIntY()-r1.getIntY();
				q = new Vertex3D(r1.getX()+tmp*mR, l2.getIntY(), 0,
						new Color(r1.getColor().getR()+tmp*mrR, r1.getColor().getG()+tmp*mgR, r1.getColor().getB()+tmp*mbR));
				q.setZ(r1.getZ() - zR*calcDistance2D(q, r1));
				drawPart(l1, l2, r1, q);
				r1 = q;
				l1 = l2;
				l2 = leftChain.get(leftCount++);
				mL = (l1.getX()-l2.getX()) / (l1.getY()-l2.getY());
				zL = (l1.getZ()-l2.getZ()) / calcDistance2D(l1, l2);
				
				mrL = (l1.getColor().getR()-l2.getColor().getR()) / (l1.getY()-l2.getY());
				mgL = (l1.getColor().getG()-l2.getColor().getG()) / (l1.getY()-l2.getY());
				mbL = (l1.getColor().getB()-l2.getColor().getB()) / (l1.getY()-l2.getY());
			}
			else {
				double tmp = r2.getIntY()-l1.getIntY();
				q = new Vertex3D(l1.getX()+tmp*mL, r2.getIntY(), 0.0,
						new Color(l1.getColor().getR()+tmp*mrL, l1.getColor().getG()+tmp*mgL, l1.getColor().getB()+tmp*mbL));
				q.setZ(l1.getZ() - zL*calcDistance2D(q, l1));
				drawPart(l1, q, r1, r2);
				l1 = q;
				r1 = r2;
				r2 = rightChain.get(rightCount++);
				mR = (r1.getX()-r2.getX()) / (r1.getY()-r2.getY());
				zR = (r1.getZ()-r2.getZ()) / calcDistance2D(r1, r2);

				mrR = (r1.getColor().getR()-r2.getColor().getR()) / (r1.getY()-r2.getY());
				mgR = (r1.getColor().getG()-r2.getColor().getG()) / (r1.getY()-r2.getY());
				mbR = (r1.getColor().getB()-r2.getColor().getB()) / (r1.getY()-r2.getY());
			}
		}
		while (rightCount < rightChain.length()){
			double tmp = r2.getIntY()-l1.getIntY();
			q = new Vertex3D(l1.getX()+tmp*mL, r2.getIntY(), 0.0,
					new Color(l1.getColor().getR()+tmp*mrL, l1.getColor().getG()+tmp*mgL, l1.getColor().getB()+tmp*mbL));
			q.setZ(l1.getZ() - zL*calcDistance2D(q, l1));
			drawPart(l1, q, r1, r2);
			l1 = q;
			r1 = r2;
			r2 = rightChain.get(rightCount++);
			mR = (r1.getX()-r2.getX()) / (r1.getY()-r2.getY());
			zR = (r1.getZ()-r2.getZ()) / calcDistance2D(r1, r2);

			mrR = (r1.getColor().getR()-r2.getColor().getR()) / (r1.getY()-r2.getY());
			mgR = (r1.getColor().getG()-r2.getColor().getG()) / (r1.getY()-r2.getY());
			mbR = (r1.getColor().getB()-r2.getColor().getB()) / (r1.getY()-r2.getY());
		}
		while (leftCount < leftChain.length()) {
			double tmp = l2.getIntY()-r1.getIntY();
			q = new Vertex3D(r1.getX()+tmp*mR, l2.getIntY(), 0,
					new Color(r1.getColor().getR()+tmp*mrR, r1.getColor().getG()+tmp*mgR, r1.getColor().getB()+tmp*mbR));
			q.setZ(r1.getZ() - zR*calcDistance2D(q, r1));
			drawPart(l1, l2, r1, q);
			r1 = q;
			l1 = l2;
			l2 = leftChain.get(leftCount++);
			mL = (l1.getX()-l2.getX()) / (l1.getY()-l2.getY());
			zL = (l1.getZ()-l2.getZ()) / calcDistance2D(l1, l2);

			mrL = (l1.getColor().getR()-l2.getColor().getR()) / (l1.getY()-l2.getY());
			mgL = (l1.getColor().getG()-l2.getColor().getG()) / (l1.getY()-l2.getY());
			mbL = (l1.getColor().getB()-l2.getColor().getB()) / (l1.getY()-l2.getY());
		}
		drawPart(l1, l2, r1, r2);
	}

	private void drawPart(Vertex3D l1, Vertex3D l2, Vertex3D r1, Vertex3D r2){
		double xL = l1.getX();
		double xR = r1.getX();

		double zLL = l1.getZ();
		double zRR = r1.getZ();

		double rL = l1.getColor().getR();
		double gL = l1.getColor().getG();
		double bL = l1.getColor().getB();
		double rR = r1.getColor().getR();
		double gR = r1.getColor().getG();
		double bR = r1.getColor().getB();

		for (int y = l1.getIntY(); y > l2.getIntY(); --y) {
			double deltaX = Math.round(xL) - Math.round(xR);
			double deltaR = (rL-rR)/deltaX;
			double deltaG = (gL-gR)/deltaX;
			double deltaB = (bL-bR)/deltaX;
			double r = rL;
			double g = gL;
			double b = bL;
			double mZ = (zLL - zRR)/(xL - xR);

			for (int x = (int)Math.round(xL); x < (int)Math.round(xR); ++x) {
				drawable.setPixel(x, y, zLL+mZ*(x - (int)Math.round(xL)), new Color(r, g, b).asARGB());
				r += deltaR;
				b += deltaB;
				g += deltaG;
			}
			xL -= mL;
			xR -= mR;

			zLL = l1.getZ() - zL*calcDistance2D(l1.getX(), l1.getY(), xL, y);
			zRR = r1.getZ() - zR*calcDistance2D(r1.getX(), r1.getY(), xR, y);

			rL -= mrL;
			gL -= mgL;
			bL -= mbL;
			rR -= mrR;
			gR -= mgR;
			bR -= mbR;
		}
	}

	private double calcDistance2D(Vertex3D p1, Vertex3D p2){
		return Math.sqrt(Math.pow(p1.getX()-p2.getX(), 2) + Math.pow(p1.getY()-p2.getY(), 2));
	}

	private double calcDistance2D(double x1, double y1, double x2, double y2){
		return Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2));
	}
/*
	private void drawPart1(Vertex3D top, Vertex3D p1, Vertex3D p2, Drawable drawable, int argb){
		Vertex3D left, right;
		if (p1.getIntX() < p2.getIntX()) {
			left = p1;
			right = p2;
		}
		else {
			left = p2;
			right = p1;
		}
		double mL = (top.getX()-left.getX()) / (top.getY()-left.getY());
		double mR = (top.getX()-right.getX()) / (top.getY()-right.getY());
		double xL = top.getX();
		double xR = top.getX();

//		drawable.setPixel(top.getIntX(), top.getIntY(), top.getIntZ(), argb);
		for (int y = top.getIntY()-1; y > left.getIntY(); --y) {
			xL -= mL;
			xR -= mR;
			for (int x = (int)Math.round(xL); x < (int)Math.round(xR); ++x) {
				drawable.setPixel(x, y, 0, argb);
			}
		}
	}
*/

/*
	private void drawPart2(Vertex3D bottom, Vertex3D p1, Vertex3D p2, Drawable drawable, int argb){
		Vertex3D left, right;
		if (p1.getIntX() < p2.getIntX()) {
			left = p1;
			right = p2;
		}
		else {
			left = p2;
			right = p1;
		}
		double mL = (bottom.getX()-left.getX()) / (bottom.getY()-left.getY());
		double mR = (bottom.getX()-right.getX()) / (bottom.getY()-right.getY());
		double xL = bottom.getX();
		double xR = bottom.getX();

//		drawable.setPixel(bottom.getIntX(), bottom.getIntY(), bottom.getIntZ(), argb);
		for (int y = bottom.getIntY()+1; y <= left.getIntY(); ++y) {
			xL += mL;
			xR += mR;
			for (int x = (int)Math.round(xL); x < (int)Math.round(xR); ++x) {
				drawable.setPixel(x, y, 0, argb);
			}
		}
	}
*/

	public static PolygonRenderer make() {
		return new FilledPolygonRenderer();
	}
}
