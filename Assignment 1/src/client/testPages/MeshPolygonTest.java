package client.testPages;

import geometry.Vertex3D;
import polygon.Polygon;
import polygon.PolygonRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

import java.util.ArrayList;
import java.util.Random;

public class MeshPolygonTest {
	private static final double FRACTION_OF_PANEL_FOR_DRAWING = 0.9;
	private static final int TRIANGLE_NUMBER = 9;
	private static final int PERTURBATION_DEVIATION = 12;
	private static final long SEED = 301353761L;
	private Random random = new Random(SEED);
	public static final int NO_PERTURBATION = 0;
	public static final int USE_PERTURBATION = 1;

	private int marginX, marginY, sideLength, perturbation;

	private final Drawable panel;
	private final PolygonRenderer renderer;


	public MeshPolygonTest(Drawable panel, PolygonRenderer renderer, int perturbation) {
		this.panel = panel;
		this.renderer = renderer;
		this.perturbation = perturbation;
		computeLength();
//		makeCenter();
		render();
	}

	private void render() {
		ArrayList<Vertex3D> p1 = makeOneLine(marginY);
//		ArrayList<Vertex3D>
		for (int y = marginY + sideLength; y <= marginY + sideLength * TRIANGLE_NUMBER; y += sideLength) {
			ArrayList<Vertex3D> p2 = makeOneLine(y);
			for (int i = 0; i < TRIANGLE_NUMBER; ++i) {
				renderer.drawPolygon(Polygon.make(p1.get(i), p2.get(i+1), p2.get(i)), panel);
				renderer.drawPolygon(Polygon.make(p1.get(i), p1.get(i+1), p2.get(i+1)), panel);
			}
			p1 = p2;
		}
	}

	private ArrayList<Vertex3D> makeOneLine(int y){
		ArrayList<Vertex3D> p = new ArrayList<>();
		for (int x = marginX; x <= marginX + sideLength*TRIANGLE_NUMBER; x += sideLength) {
			int x1 = x;
			int y1 = y;
			if (perturbation == USE_PERTURBATION) {
				x1 += (random.nextDouble()-random.nextDouble()) * PERTURBATION_DEVIATION;
				y1 += (random.nextDouble()-random.nextDouble()) * PERTURBATION_DEVIATION;
			}
			p.add(new Vertex3D(x1, y1, 0.0, Color.random(random)));
		}
		return p;
	}

//	private void makeCenter() {
//		int centerX = panel.getWidth() / 2;
//		int centerY = panel.getHeight() / 2;
//		center = new Vertex3D(centerX, centerY, 0, Color.random(random));
//	}

//	private Vertex3D radialPoint(double radius, double angle) {
//		double x = center.getX() + radius * Math.cos(angle);
//		double y = center.getY() + radius * Math.sin(angle);
//		return new Vertex3D(x, y, 0, Color.random(random));
//	}

	private void computeLength() {
		int width = panel.getWidth();
		int height = panel.getHeight();
		int minDimension = width < height ? width : height;

		sideLength = (int)(minDimension * FRACTION_OF_PANEL_FOR_DRAWING / TRIANGLE_NUMBER);
		marginX = (width - sideLength*TRIANGLE_NUMBER) / 2;
		marginY = (height - sideLength*TRIANGLE_NUMBER) / 2;
	}
}
