package shading;

import geometry.Point3DH;
import geometry.Vertex3D;
import polygon.Polygon;
import polygon.Shader;
import windowing.graphics.Color;

import java.util.List;

public class FlatShading implements Shader{
	private List<LightSource> lightSources;
	private Color color;
	private boolean done = false;

	public FlatShading(List<LightSource> lightSources) {
		this.lightSources = lightSources;
	}

	public Color shade(Polygon polygon, Vertex3D point) {
//		if (lightSources.size() == 0) {
//			return point.getColor();
//		}

		if (done) {
			return color;
		}
//		System.out.println("F");
		Point3DH avgNormal = getFaceNormal(polygon);
//		System.out.println(avgNormal);
		double x = (polygon.get(0).getCameraSpace().getX() + polygon.get(1).getCameraSpace().getX() + polygon.get(2).getCameraSpace().getX())/3;
		double y = (polygon.get(0).getCameraSpace().getY() + polygon.get(1).getCameraSpace().getY() + polygon.get(2).getCameraSpace().getY())/3;
		double z = (polygon.get(0).getCameraSpace().getZ() + polygon.get(1).getCameraSpace().getZ() + polygon.get(2).getCameraSpace().getZ())/3;
		Vertex3D middle = new Vertex3D(x, y, z, point.getColor());
		middle.setNormal(avgNormal);
		middle.setCameraSpace(x, y, z);
		color = lightCalculation(lightSources, middle);
		done = true;
		return color;
	}
}
