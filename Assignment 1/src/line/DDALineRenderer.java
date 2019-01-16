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
		int argbColor = p1.getColor().asARGB();

		double y = p1.getY();
		for(int x = p1.getIntX(); x <= p2.getIntX(); x++) {
//			drawable.setPixel(x, (int)Math.round(y), 0.0, argbColor);   //draw better using round()
			drawable.setPixelWithCoverage(x, (int)Math.round(y), 0.0, argbColor, 1);
			y += slope;
		}
	}

	public static LineRenderer make() {
        return new AnyOctantLineRenderer(new DDALineRenderer());
    }

}
