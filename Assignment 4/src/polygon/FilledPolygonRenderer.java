package polygon;

import client.interpreter.SimpInterpreter;
import geometry.Point3DH;
import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class FilledPolygonRenderer implements PolygonRenderer{
//	private int argb;
	private double mL;
	private double mR;
	private double mLC;
	private double mRC;
	private double mrL, mgL, mbL, mrR, mgR, mbR;
	private double kLC, kRC;
	private double zL, zR;
	private double zLC, zRC;
	private Drawable drawable;
	private Shader shader;
	private Polygon polygon;


	private Color color;


	@Override
	public void drawPolygon(Polygon polygon, Drawable drawable, Shader vertexShader){
		this.polygon = polygon;
		this.drawable = drawable;
		shader = vertexShader;
		int leftCount = 0, rightCount = 0;

		this.color = polygon.get(0).getColor();

		Chain leftChain = polygon.leftChain();
		Chain rightChain = polygon.rightChain();

//		System.out.println(leftChain.length());
//		System.out.println(rightChain.length());
		if (leftChain.length() == 1 || rightChain.length() == 1) {
			return;
		}

		Vertex3D l1 = leftChain.get(leftCount++);
		Vertex3D l2 = leftChain.get(leftCount++);
		Vertex3D r1 = rightChain.get(rightCount++);
		Vertex3D r2 = rightChain.get(rightCount++);
		Vertex3D q;
		mL = (l1.getX()-l2.getX()) / (l1.getY()-l2.getY());
		mR = (r1.getX()-r2.getX()) / (r1.getY()-r2.getY());

		if (SimpInterpreter.isInCameraWorld) {
			mLC = (l1.getCameraSpace().getX()-l2.getCameraSpace().getX()) / (l1.getY()-l2.getY());
			mRC = (r1.getCameraSpace().getX()-r2.getCameraSpace().getX()) / (r1.getY()-r2.getY());
			zLC = (l1.getCameraSpace().getZ()-l2.getCameraSpace().getZ()) / (l1.getY()-l2.getY());
			zRC = (r1.getCameraSpace().getZ()-r2.getCameraSpace().getZ()) / (r1.getY()-r2.getY());
			kLC = (l1.getCameraSpace().getY()-l2.getCameraSpace().getY()) / (l1.getY()-l2.getY());
			kRC = (r1.getCameraSpace().getY()-r2.getCameraSpace().getY()) / (r1.getY()-r2.getY());
		}

		zL = (l1.getZ()-l2.getZ()) / calcDistance2D(l1.getPoint3D(), l2.getPoint3D());
		zR = (r1.getZ()-r2.getZ()) / calcDistance2D(r1.getPoint3D(), r2.getPoint3D());

		mrL = (l1.getColor().getR()-l2.getColor().getR()) / (l1.getY()-l2.getY());
		mgL = (l1.getColor().getG()-l2.getColor().getG()) / (l1.getY()-l2.getY());
		mbL = (l1.getColor().getB()-l2.getColor().getB()) / (l1.getY()-l2.getY());

		mrR = (r1.getColor().getR()-r2.getColor().getR()) / (r1.getY()-r2.getY());
		mgR = (r1.getColor().getG()-r2.getColor().getG()) / (r1.getY()-r2.getY());
		mbR = (r1.getColor().getB()-r2.getColor().getB()) / (r1.getY()-r2.getY());

//		shader.shade(polygon, polygon.get(0));

		while (leftCount < leftChain.length() && rightCount < rightChain.length()){
			if (l2.getY() > r2.getY()) {
				double tmp = l2.getIntY()-r1.getIntY();
				q = new Vertex3D(r1.getX()+tmp*mR, l2.getIntY(), 0,
						new Color(r1.getColor().getR()+tmp*mrR, r1.getColor().getG()+tmp*mgR, r1.getColor().getB()+tmp*mbR));

				Point3DH tmpCamera = new Point3DH(r1.getCameraSpace().getX()+tmp*mRC, r1.getCameraSpace().getY()+tmp*kRC,r1.getCameraSpace().getZ()+tmp*zRC);
				q.setCameraSpace(tmpCamera);
				tmpCamera.setZ(r1.getCameraSpace().getZ() - zRC*calcDistance2D(q.getCameraSpace(), r1.getCameraSpace()));

				q.setZ(r1.getZ() - zR*calcDistance2D(q.getPoint3D(), r1.getPoint3D()));
				q.setCameraSpace(tmpCamera);

				drawPart(l1, l2, r1, q);
				r1 = q;
				l1 = l2;
				l2 = leftChain.get(leftCount++);
				mL = (l1.getX()-l2.getX()) / (l1.getY()-l2.getY());
				zL = (l1.getZ()-l2.getZ()) / calcDistance2D(l1.getPoint3D(), l2.getPoint3D());

				mLC = (l1.getCameraSpace().getX()-l2.getCameraSpace().getX()) / (l1.getCameraSpace().getY()-l2.getCameraSpace().getY());
				zLC = (l1.getCameraSpace().getZ()-l2.getCameraSpace().getZ()) / calcDistance2D(l1.getCameraSpace(), l2.getCameraSpace());
				
				mrL = (l1.getColor().getR()-l2.getColor().getR()) / (l1.getY()-l2.getY());
				mgL = (l1.getColor().getG()-l2.getColor().getG()) / (l1.getY()-l2.getY());
				mbL = (l1.getColor().getB()-l2.getColor().getB()) / (l1.getY()-l2.getY());
			}
			else {
				double tmp = r2.getIntY()-l1.getIntY();
				q = new Vertex3D(l1.getX()+tmp*mL, r2.getIntY(), 0.0,
						new Color(l1.getColor().getR()+tmp*mrL, l1.getColor().getG()+tmp*mgL, l1.getColor().getB()+tmp*mbL));


//			tmp = r2.getCameraSpace().getIntY()-l1.getCameraSpace().getIntY();
				Point3DH tmpCamera = new Point3DH(l1.getCameraSpace().getX()+tmp*mLC, l1.getCameraSpace().getY()+tmp*kLC,l1.getCameraSpace().getZ()+tmp*zLC);
				q.setCameraSpace(tmpCamera);
//			tmpCamera.setZ(l1.getCameraSpace().getZ() - zLC*calcDistance2D(q.getCameraSpace(), l1.getCameraSpace()));

				q.setZ(l1.getZ() - zL*calcDistance2D(q.getPoint3D(), l1.getPoint3D()));
//			q.setCameraSpace(tmpCamera);


				drawPart(l1, q, r1, r2);
				l1 = q;
				r1 = r2;
				r2 = rightChain.get(rightCount++);
				mR = (r1.getX()-r2.getX()) / (r1.getY()-r2.getY());
				zR = (r1.getZ()-r2.getZ()) / calcDistance2D(r1.getPoint3D(), r2.getPoint3D());

				mRC = (r1.getCameraSpace().getX()-r2.getCameraSpace().getX()) / (r1.getCameraSpace().getY()-r2.getCameraSpace().getY());
				zRC = (r1.getCameraSpace().getZ()-r2.getCameraSpace().getZ()) / calcDistance2D(r1.getCameraSpace(), r2.getCameraSpace());

				mrR = (r1.getColor().getR()-r2.getColor().getR()) / (r1.getY()-r2.getY());
				mgR = (r1.getColor().getG()-r2.getColor().getG()) / (r1.getY()-r2.getY());
				mbR = (r1.getColor().getB()-r2.getColor().getB()) / (r1.getY()-r2.getY());
			}
		}
		while (rightCount < rightChain.length()){
			double tmp = r2.getIntY()-l1.getIntY();
			q = new Vertex3D(l1.getX()+tmp*mL, r2.getIntY(), 0.0,
					new Color(l1.getColor().getR()+tmp*mrL, l1.getColor().getG()+tmp*mgL, l1.getColor().getB()+tmp*mbL));

//			tmp = r2.getCameraSpace().getIntY()-l1.getCameraSpace().getIntY();
			Point3DH tmpCamera = new Point3DH(l1.getCameraSpace().getX()+tmp*mLC, l1.getCameraSpace().getY()+tmp*kLC,l1.getCameraSpace().getZ()+tmp*zLC);
			q.setCameraSpace(tmpCamera);
//			tmpCamera.setZ(l1.getCameraSpace().getZ() - zLC*calcDistance2D(q.getCameraSpace(), l1.getCameraSpace()));

			q.setZ(l1.getZ() - zL*calcDistance2D(q.getPoint3D(), l1.getPoint3D()));
//			q.setCameraSpace(tmpCamera);

			drawPart(l1, q, r1, r2);
			l1 = q;
			r1 = r2;
			r2 = rightChain.get(rightCount++);
			mR = (r1.getX()-r2.getX()) / (r1.getY()-r2.getY());
			zR = (r1.getZ()-r2.getZ()) / calcDistance2D(r1.getPoint3D(), r2.getPoint3D());

			mRC = (r1.getCameraSpace().getX()-r2.getCameraSpace().getX()) / (r1.getY()-r2.getY());
			kRC = (r1.getCameraSpace().getY()-r2.getCameraSpace().getY()) / (r1.getY()-r2.getY());
			zRC = (r1.getCameraSpace().getZ()-r2.getCameraSpace().getZ()) / (r1.getY()-r2.getY());

			mrR = (r1.getColor().getR()-r2.getColor().getR()) / (r1.getY()-r2.getY());
			mgR = (r1.getColor().getG()-r2.getColor().getG()) / (r1.getY()-r2.getY());
			mbR = (r1.getColor().getB()-r2.getColor().getB()) / (r1.getY()-r2.getY());
		}
		while (leftCount < leftChain.length()) {
			double tmp = l2.getIntY()-r1.getIntY();
			q = new Vertex3D(r1.getX()+tmp*mR, l2.getIntY(), 0,
					new Color(r1.getColor().getR()+tmp*mrR, r1.getColor().getG()+tmp*mgR, r1.getColor().getB()+tmp*mbR));

//			tmp = l2.getCameraSpace().getIntY()-r1.getCameraSpace().getIntY();
			Point3DH tmpCamera = new Point3DH(r1.getCameraSpace().getX()+tmp*mRC, r1.getCameraSpace().getY()+tmp*kRC,r1.getCameraSpace().getZ()+tmp*zRC);
			q.setCameraSpace(tmpCamera);
			tmpCamera.setZ(r1.getCameraSpace().getZ() - zRC*calcDistance2D(q.getCameraSpace(), r1.getCameraSpace()));

			q.setZ(r1.getZ() - zR*calcDistance2D(q.getPoint3D(), r1.getPoint3D()));
			q.setCameraSpace(tmpCamera);

			drawPart(l1, l2, r1, q);
			r1 = q;
			l1 = l2;
			l2 = leftChain.get(leftCount++);
			mL = (l1.getX()-l2.getX()) / (l1.getY()-l2.getY());
			zL = (l1.getZ()-l2.getZ()) / calcDistance2D(l1.getPoint3D(), l2.getPoint3D());

			mLC = (l1.getCameraSpace().getX()-l2.getCameraSpace().getX()) / (l1.getY()-l2.getY());
			kLC = (l1.getCameraSpace().getY()-l2.getCameraSpace().getY()) / (l1.getY()-l2.getY());
			zLC = (l1.getCameraSpace().getZ()-l2.getCameraSpace().getZ()) / (l1.getY()-l2.getY());

			mrL = (l1.getColor().getR()-l2.getColor().getR()) / (l1.getY()-l2.getY());
			mgL = (l1.getColor().getG()-l2.getColor().getG()) / (l1.getY()-l2.getY());
			mbL = (l1.getColor().getB()-l2.getColor().getB()) / (l1.getY()-l2.getY());
		}
		drawPart(l1, l2, r1, r2);
	}

	private void drawPart(Vertex3D l1, Vertex3D l2, Vertex3D r1, Vertex3D r2){

		double xL = l1.getX();
		double xR = r1.getX();

		double xLC = l1.getCameraSpace().getX();
		double xRC = r1.getCameraSpace().getX();

		double zLL = l1.getZ();
		double zRR = r1.getZ();

		double zLLC = l1.getCameraSpace().getZ();
		double zRRC = r1.getCameraSpace().getZ();

		double rL = l1.getColor().getR();
		double gL = l1.getColor().getG();
		double bL = l1.getColor().getB();
		double rR = r1.getColor().getR();
		double gR = r1.getColor().getG();
		double bR = r1.getColor().getB();

		double yC = l1.getCameraSpace().getY();
		double scaleY = (l2.getCameraSpace().getY() - l1.getCameraSpace().getY()) / (l2.getIntY() - l1.getIntY());

		for (int y = l1.getIntY(); y > l2.getIntY(); --y) {
			double deltaX = Math.round(xL) - Math.round(xR);

//			double deltaXC = Math.round(xLC) - Math.round(xRC);

			double deltaR = (rL-rR)/deltaX;
			double deltaG = (gL-gR)/deltaX;
			double deltaB = (bL-bR)/deltaX;
			double r = rL;
			double g = gL;
			double b = bL;
			double mZ = (zLL - zRR)/(xL - xR);

			double mZC = (zLLC - zRRC)/(xL - xR);

			double xC = xLC;
			double scaleX = (xRC - xLC) / (Math.round(xR) - Math.round(xL));
			for (int x = (int)Math.round(xL); x < (int)Math.round(xR); ++x) {
				if (SimpInterpreter.isInCameraWorld) {
					double realZC = (zLLC+mZC*(x - (int)Math.round(xL)));
					Point3DH tmpCamera = new Point3DH(xC, yC, realZC);

					double realZ = 1/(zLL+mZ*(x - (int)Math.round(xL)));
					Color tmpColor = new Color(color);
					Vertex3D currentPoint = new Vertex3D(x, y, realZ, tmpColor);
					currentPoint.setCameraSpace(tmpCamera);
//					System.out.println(tmpCamera);
//					System.out.println("x:"+xC+"y:"+yC+"z:"+realZC);
					tmpColor = shader.shade(polygon, currentPoint);
					if (realZ > SimpInterpreter.depthNear) {
						;
					}
					else if (realZ < SimpInterpreter.depthFar) {
						tmpColor = SimpInterpreter.depthColor;
					}
					else {
						double r11 = tmpColor.getR();
						double g11 = tmpColor.getG();
						double b11 = tmpColor.getB();
						double r21 = SimpInterpreter.depthColor.getR();
						double g21 = SimpInterpreter.depthColor.getG();
						double b21 = SimpInterpreter.depthColor.getB();
						double deltaZ = SimpInterpreter.depthNear - SimpInterpreter.depthFar;
						double deltaZ2 = SimpInterpreter.depthNear - realZ;
						double mr = (r11-r21) / deltaZ;
						double mg = (g11-g21) / deltaZ;
						double mb = (b11-b21) / deltaZ;
						tmpColor = new Color(r11 - deltaZ2*mr, g11 - deltaZ2*mg, b11 - deltaZ2*mb);
					}
					drawable.setPixel(x, y, realZ, tmpColor.asARGB());
				}
				else
					drawable.setPixel(x, y, zLL+mZ*(x - (int)Math.round(xL)), new Color(r, g, b).asARGB());
				r += deltaR;
				b += deltaB;
				g += deltaG;

				xC += scaleX;
			}
			xL -= mL;
			xR -= mR;

			xLC -= mLC;
			xRC -= mRC;

			zLL = l1.getZ() - zL*calcDistance2D(l1.getX(), l1.getY(), xL, y);
			zRR = r1.getZ() - zR*calcDistance2D(r1.getX(), r1.getY(), xR, y);

//			zLLC = l1.getCameraSpace().getZ() - zLC*calcDistance2D(l1.getCameraSpace().getX(), l1.getCameraSpace().getY(), xLC, yC);
//			zRRC = r1.getCameraSpace().getZ() - zRC*calcDistance2D(r1.getCameraSpace().getX(), r1.getCameraSpace().getY(), xRC, yC);
			zLLC -= zLC;
			zRRC -= zRC;

			rL -= mrL;
			gL -= mgL;
			bL -= mbL;
			rR -= mrR;
			gR -= mgR;
			bR -= mbR;

			yC -= scaleY;
		}
	}

	private double calcDistance2D(Point3DH p1, Point3DH p2){
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
