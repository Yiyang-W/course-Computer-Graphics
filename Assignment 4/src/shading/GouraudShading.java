package shading;
import geometry.Point3DH;
import geometry.Vertex3D;
import polygon.Polygon;
import polygon.Shader;
import windowing.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class GouraudShading implements Shader{
	private List<LightSource> lightSources;
	private Point3DH faceNormal;
//	private ArrayList<Point3DH> vertexNormals = new ArrayList<>();
	private ArrayList<Color> colors = new ArrayList<>();
	private boolean done = false;
	private double whole;

	public GouraudShading(List<LightSource> lightSources) {
		this.lightSources = lightSources;
	}

	public Color shade(Polygon polygon, Vertex3D point) {
		if (!done) {
//			System.out.println("G");
			faceNormal = getFaceNormal(polygon);
			if (isNoNormal(polygon.get(0).getNormal()) || isNoNormal(polygon.get(1).getNormal()) || isNoNormal(polygon.get(2).getNormal())) {
				for (Vertex3D v : polygon.getVertices()) {
					Vertex3D vertex = new Vertex3D(v.getCameraSpace(), v.getColor());
					vertex.setNormal(faceNormal);
					vertex.setCameraSpace(v.getCameraSpace());
					Color color = lightCalculation(lightSources, vertex);
					//	v.setColor(color);
					colors.add(color);
				}
			}
			else {
				for (Vertex3D v : polygon.getVertices()) {
					Color color = lightCalculation(lightSources, v);
					colors.add(color);
				}
			}
			Point3DH v1 = new Point3DH(polygon.get(2).getCameraSpace().getX() - polygon.get(0).getCameraSpace().getX(), polygon.get(2).getCameraSpace().getY() - polygon.get(0).getCameraSpace().getY(), polygon.get(2).getCameraSpace().getZ() - polygon.get(0).getCameraSpace().getZ());
			Point3DH v2 = new Point3DH(polygon.get(1).getCameraSpace().getX() - polygon.get(0).getCameraSpace().getX(), polygon.get(1).getCameraSpace().getY() - polygon.get(0).getCameraSpace().getY(), polygon.get(1).getCameraSpace().getZ() - polygon.get(0).getCameraSpace().getZ());
			whole = Math.abs(crossProductValue(v1, v2));

			done = true;
		}
//		return point.getColor();
//		System.out.println(point.getCameraSpace());
		double x = point.getCameraSpace().getX();
		double y = point.getCameraSpace().getY();
		double z = point.getCameraSpace().getZ();
//		System.out.println(whole);
		Point3DH v1 = new Point3DH(polygon.get(2).getCameraSpace().getX() - x, polygon.get(2).getCameraSpace().getY() - y, polygon.get(2).getCameraSpace().getZ() - z);
		Point3DH v2 = new Point3DH(polygon.get(1).getCameraSpace().getX() - x, polygon.get(1).getCameraSpace().getY() - y, polygon.get(1).getCameraSpace().getZ() - z);
		Point3DH v3 = new Point3DH(polygon.get(0).getCameraSpace().getX() - x, polygon.get(0).getCameraSpace().getY() - y, polygon.get(0).getCameraSpace().getZ() - z);
		double a = Math.abs(crossProductValue(v1, v2))/whole;
		double b = Math.abs(crossProductValue(v1, v3))/whole;
		double c = Math.abs(crossProductValue(v3, v2))/whole;
//		System.out.println(v1);
//		System.out.println(v2);
//		System.out.println(v3);
////		System.out.println("x:"+x+"y:"+y+"z:"+z);
//		System.out.println("a:"+a+" b:"+b+" c:"+c);
		double tmp = a+b+c;
		a /= tmp;
		b /= tmp;
		c /= tmp;
//		System.out.println("a:"+a+" b:"+b+" c:"+c);
		double rr = a*colors.get(0).getR() + b*colors.get(1).getR() + c*colors.get(2).getR();
		double gg = a*colors.get(0).getG() + b*colors.get(1).getG() + c*colors.get(2).getG();
		double bb = a*colors.get(0).getB() + b*colors.get(1).getB() + c*colors.get(2).getB();
		return new Color(rr, gg, bb);
	}
}
