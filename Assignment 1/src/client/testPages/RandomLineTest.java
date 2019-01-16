package client.testPages;

import geometry.Vertex3D;
import line.LineRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

import java.util.Random;

public class RandomLineTest {
	private static final int LINE_NUMBER = 30;
	private static final long SEED = 301353761L;
	private final LineRenderer renderer;
	private final Drawable panel;
	private Random random = new Random(SEED);

	public RandomLineTest(Drawable panel, LineRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;

		render();
	}

	private void render() {
		for (int i = 0; i < LINE_NUMBER; i++) {
			Vertex3D p1 = generatePoint();
			Vertex3D p2 = generatePoint();
			renderer.drawLine(p1, p2, panel);
		}
	}

	private Vertex3D generatePoint() {
//		return new Vertex3D(random.nextDouble()*299, random.nextDouble()*299, 0.0, new Color(random.nextDouble(), random.nextDouble(), random.nextDouble()));
		random.nextDouble();
		return new Vertex3D(random.nextDouble()*299, random.nextDouble()*299, 0.0, Color.random(random));
	}

}
