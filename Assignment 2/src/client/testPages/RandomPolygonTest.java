package client.testPages;

import geometry.Vertex3D;
import polygon.Polygon;
import polygon.PolygonRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

import java.util.Random;

public class RandomPolygonTest {
	private static final int TRIANGLE_NUMBER = 20;
	private static final long SEED = 301353761L;
	private Random random = new Random(SEED);
	private final Drawable panel;
	private final PolygonRenderer renderer;
//	Polygon polygon;

	public RandomPolygonTest(Drawable panel, PolygonRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;

		render();
	}

	private void render(){
		Vertex3D a, b, c;
		for (int i = 0; i < TRIANGLE_NUMBER; ++i) {
			a = generatePoint();
			b = generatePoint();
			c = generatePoint();
			if (Polygon.isClockwise(a, b, c)) {
				renderer.drawPolygon(Polygon.make(a, c, b), panel);
			}
			else{
				renderer.drawPolygon(Polygon.make(a, b, c), panel);
			}
		}
	}

	private Vertex3D generatePoint() {
//		return new Vertex3D(random.nextDouble()*299, random.nextDouble()*299, 0.0, new Color(random.nextDouble(), random.nextDouble(), random.nextDouble()));
		random.nextDouble();
		return new Vertex3D(random.nextDouble()*299, random.nextDouble()*299, 0.0, Color.random(random));
	}
}
