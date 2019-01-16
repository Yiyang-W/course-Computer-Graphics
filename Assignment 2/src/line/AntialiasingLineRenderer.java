package line;

import geometry.Vertex;
import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class AntialiasingLineRenderer implements LineRenderer {

	private static final double RADIUS = 0.5;
	private static final double HALF_WIDTH = 0.5;

	@Override
	public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) {
		double deltaX = p2.getX() - p1.getX();
		double deltaY = p2.getY() - p1.getY();
		double slope = deltaY / deltaX;
		double intercept = p2.getY() - slope * p2.getX();
		int lineArgb = p1.getColor().asARGB();
		double y = p1.getY();

		for (int x = p1.getIntX(); x <= p2.getIntX(); x++) {
			int roundedY = (int)Math.round(y);
			for (int center = roundedY-1; center <= roundedY+1; ++center) {
				double distance = Math.abs(center - slope*x - intercept) / Math.sqrt(1 + slope*slope);
				double percentage;
				double theta;
				if (distance > RADIUS + HALF_WIDTH) {
					continue;
				}
				if (distance < RADIUS) {
					distance = HALF_WIDTH - distance;
					theta = Math.acos(distance/RADIUS);
					percentage = distance*Math.sqrt(RADIUS*RADIUS - distance*distance)/Math.PI*RADIUS*RADIUS +1- theta/Math.PI;

					if (percentage > 0.88) {
						percentage = 0.88;
					}

					drawable.setPixelWithCoverage(x, center, 0.0, lineArgb, percentage);
				}
				else if (distance - HALF_WIDTH < RADIUS) {
					distance = distance - HALF_WIDTH;
					theta = Math.acos(distance/RADIUS);
					percentage = -distance*Math.sqrt(RADIUS*RADIUS - distance*distance)/Math.PI*RADIUS*RADIUS + theta/Math.PI;

					drawable.setPixelWithCoverage(x, center, 0.0, lineArgb, percentage);
				}
			}
			y += slope;
		}
	}

	public static LineRenderer make() {
		return new AnyOctantLineRenderer(new AntialiasingLineRenderer());
	}

}
