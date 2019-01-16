package line;

import geometry.Vertex;
import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class BresenhamLineRenderer implements LineRenderer{

	@Override
	public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) {
		int m_num = 2*(p2.getIntY() - p1.getIntY());
		int den = 2*(p2.getIntX() - p1.getIntX());  //denominator of y and m are the same
		int y_int = p1.getIntY();
		int k = m_num - den;
		int y_num = p2.getIntX() - p1.getIntX() + k;
		int argbColor = p1.getColor().asARGB();

		for(int x = p1.getIntX(); x <= p2.getIntX(); x++) {
//			drawable.setPixel(x, y_int, 0.0, argbColor);
			drawable.setPixelWithCoverage(x, y_int, 0.0, argbColor, 1);
			if (y_num < 0) {
				y_num += m_num;
			}
			else {
				y_num += k;
				y_int++;
			}
		}
	}

	public static LineRenderer make() {
		return new AnyOctantLineRenderer(new BresenhamLineRenderer());
	}


}
