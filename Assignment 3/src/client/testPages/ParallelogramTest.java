package client.testPages;

import geometry.Vertex3D;
import line.LineRenderer;
import windowing.drawable.Drawable;
import windowing.drawable.InvertedYDrawable;
import windowing.graphics.Color;

public class ParallelogramTest {
	private static final int UPBOUND = 50;
	private final LineRenderer renderer;
	private final InvertedYDrawable panel;

	public ParallelogramTest(Drawable panel, LineRenderer renderer) {
		this.panel = new InvertedYDrawable(panel);
		this.renderer = renderer;

		render();
	}

	private void render() {
		for(int p = 0; p < UPBOUND; p++) {
			renderer.drawLine(new Vertex3D(20, 80+p, 0.0, Color.WHITE), new Vertex3D(150, 150+p, 0.0, Color.WHITE), panel);
			renderer.drawLine(new Vertex3D(160+p, 270, 0.0, Color.WHITE), new Vertex3D(240+p, 40, 0.0, Color.WHITE), panel);
		}
	}

}
