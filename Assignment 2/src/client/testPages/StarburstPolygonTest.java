package client.testPages;

import geometry.Vertex3D;
import polygon.Polygon;
import polygon.PolygonRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

import java.util.Random;

public class StarburstPolygonTest {
	private static final int NUM_RAYS = 90;
	private static final double FRACTION_OF_PANEL_FOR_DRAWING = 0.9;
	private static final long SEED = 301353761L;
	private Random random = new Random(SEED);

	private final Drawable panel;
	private final PolygonRenderer renderer;
	Polygon polygon;
	private Vertex3D center;

	public StarburstPolygonTest(Drawable panel, PolygonRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;

		makeCenter();
		render();
	}

	private void render() {
		double radius = computeRadius();
		double angleDifference = (2.0 * Math.PI) / NUM_RAYS;
		double angle = 0.0;

//		Vertex3D startPoint = radialPoint(radius, 0.0);
		Vertex3D p1 = radialPoint(radius, 0.0);
		angle += angleDifference;
		for(int ray = 0; ray < NUM_RAYS; ray++) {
			Vertex3D p2 = radialPoint(radius, angle);
			polygon = Polygon.make(center, p1, p2);
			renderer.drawPolygon(polygon, panel);

			p1 = p2;
			angle = angle + angleDifference;
		}
//		polygon = Polygon.make(center, p1, start);
	}

	private void makeCenter() {
		int centerX = panel.getWidth() / 2;
		int centerY = panel.getHeight() / 2;
		center = new Vertex3D(centerX, centerY, 0, Color.random(random));
	}

	private Vertex3D radialPoint(double radius, double angle) {
		double x = center.getX() + radius * Math.cos(angle);
		double y = center.getY() + radius * Math.sin(angle);
		return new Vertex3D(x, y, 0, Color.random(random));
	}

	private double computeRadius() {
		int width = panel.getWidth();
		int height = panel.getHeight();

		int minDimension = width < height ? width : height;

		return (minDimension / 2.0) * FRACTION_OF_PANEL_FOR_DRAWING;
	}
}
