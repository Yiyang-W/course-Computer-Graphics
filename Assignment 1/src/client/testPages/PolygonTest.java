package client.testPages;

import geometry.Vertex3D;
import polygon.Polygon;
import polygon.PolygonRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class PolygonTest {
	private final Drawable panel;
	private final PolygonRenderer renderer;
	private Polygon polygon = Polygon.makeEmpty();
	private int height;
	private int width;

	public PolygonTest(Drawable panel, PolygonRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;
		height = panel.getHeight();
		width = panel.getWidth();
		setPoints();
		render();
	}

	private void render(){
		renderer.drawPolygon(polygon, panel);
//		for (int i = 0; i < 7; i++) {
//			panel.setPixel(polygon.get(i).getIntX(), polygon.get(i).getIntY(), 0, Color.WHITE.asARGB());
//		}
	}

	private void setPoints(){
		polygon.add(new Vertex3D(width*3.0/7, height*8.0/9, 0, Color.random()));
		polygon.add(new Vertex3D(width*1.0/5, height*8.0/11, 0, Color.random()));
		polygon.add(new Vertex3D(width*1.8/8, height*5.8/9, 0, Color.random()));
		polygon.add(new Vertex3D(width*1.0/3, height*4.0/7, 0, Color.random()));
		polygon.add(new Vertex3D(width*1.0/4.2, height*2.5/7, 0, Color.random()));
		polygon.add(new Vertex3D(width*5.5/7, height*1.1/10, 0, Color.random()));
		polygon.add(new Vertex3D(width*1.2/2, height*3.14/11, 0, Color.random()));
		polygon.add(new Vertex3D(width*8.0/9, height*7.7/10, 0, Color.random()));
	}
}
