package client.testPages;

import geometry.Vertex3D;
import polygon.Polygon;
import polygon.PolygonRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

import java.util.Random;

public class A2Page3 {
	private final Drawable panel;
	private final PolygonRenderer renderer;
	private int radius = 275;
	private static long SEED = 7783193996L;
	private Random random = new Random(SEED);
	private Polygon[] polygons;
	private int width, height;

	public A2Page3(Drawable panel, PolygonRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;
		polygons = new Polygon[6];
		width = panel.getWidth();
		height = panel.getHeight();

		double c = 1;
		for (int i = 0; i < 6; ++i) {
			polygons[i] = createPolygon(new Color(c, c, c));
			c -= 0.15;
		}
		render();
	}
	private void render(){
		for (int i = 0; i < 6; ++i) {
			renderer.drawPolygon(polygons[i], panel);
		}
	}
	private Polygon createPolygon(Color color){
		double angle = random.nextDouble() * 120;
//		double angle = 0;
		double z = random.nextDouble() * -200;
		Vertex3D p1 = new Vertex3D(width/2+Math.cos(calcRadian(angle+90))*radius, height/2+Math.sin(calcRadian(angle+90))*radius, z, color);
		Vertex3D p2 = new Vertex3D(width/2+Math.cos(calcRadian(angle+210))*radius, height/2+Math.sin(calcRadian(angle+210))*radius, z,  color);
		Vertex3D p3 = new Vertex3D(width/2+Math.cos(calcRadian(angle-30))*radius, height/2+Math.sin(calcRadian(angle-30))*radius, z,  color);
		return Polygon.make(p1, p2, p3);
	}
	private double calcRadian(double a){
		return a / 180 * Math.PI;
	}
}
