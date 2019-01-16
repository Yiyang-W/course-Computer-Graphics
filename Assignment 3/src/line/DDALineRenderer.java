package line;

import geometry.Vertex;
import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class DDALineRenderer implements LineRenderer{

	@Override
	public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) {
		double deltaX = p2.getX() - p1.getX();
		double deltaY = p2.getY() - p1.getY();
		double slope = deltaY / deltaX;
//		int argbColor = p1.getColor().asARGB();
		double deltaR = p2.getColor().getR() - p1.getColor().getR();
		double deltaG = p2.getColor().getG() - p1.getColor().getG();
		double deltaB = p2.getColor().getB() - p1.getColor().getB();
		double slopeR = deltaR / deltaX;
		double slopeG = deltaG / deltaX;
		double slopeB = deltaB / deltaX;

		double mZ = (p1.getZ() - p2.getZ()) / Math.sqrt(Math.pow(p1.getX()-p2.getX(), 2) + Math.pow(p1.getY()-p2.getY(), 2));

		double y = p1.getY();
//		double z = p1.getZ();
		double r = p1.getColor().getR();
		double g = p1.getColor().getG();
		double b = p1.getColor().getB();
		for(int x = p1.getIntX(); x <= p2.getIntX(); x++) {
//			drawable.setPixel(x, (int)Math.round(y), 0.0, argbColor);   //draw better using round()
			int argbColor = new Color(r, g, b).asARGB();
			double z = p1.getZ() - mZ*Math.sqrt(Math.pow(p1.getX()-x, 2) + Math.pow(p1.getY()-y, 2));
			drawable.setPixelWithCoverage(x, (int)Math.round(y), z, argbColor, 1);
			r += slopeR;
			g += slopeG;
			b += slopeB;
			y += slope;
		}
	}

	public static LineRenderer make() {
        return new AnyOctantLineRenderer(new DDALineRenderer());
    }

}
