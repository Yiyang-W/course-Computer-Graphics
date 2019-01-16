package shading;
import geometry.Point3DH;
import geometry.Vertex3D;
import polygon.Polygon;
import polygon.Shader;
import windowing.graphics.Color;

import java.util.ArrayList;
import java.util.List;
public class PhongShading implements Shader{
	private List<LightSource> lightSources;
	private Point3DH faceNormal;
	private ArrayList<Point3DH> vertexNormals = new ArrayList<>();
//	private ArrayList<Color> polygon = new ArrayList<>();
	private boolean done = false;
	private boolean useFace = false;
	private double whole;

	public PhongShading(List<LightSource> lightSources) {
		this.lightSources = lightSources;
	}

	public Color shade(Polygon polygon, Vertex3D point) {
		if (!done) {
//			System.out.println("P");
			faceNormal = getFaceNormal(polygon);
			if (isNoNormal(polygon.get(0).getNormal()) || isNoNormal(polygon.get(1).getNormal()) || isNoNormal(polygon.get(2).getNormal())) {
				useFace = true;
			}
			Point3DH v1 = new Point3DH(polygon.get(2).getCameraSpace().getX() - polygon.get(0).getCameraSpace().getX(), polygon.get(2).getCameraSpace().getY() - polygon.get(0).getCameraSpace().getY(), polygon.get(2).getCameraSpace().getZ() - polygon.get(0).getCameraSpace().getZ());
			Point3DH v2 = new Point3DH(polygon.get(1).getCameraSpace().getX() - polygon.get(0).getCameraSpace().getX(), polygon.get(1).getCameraSpace().getY() - polygon.get(0).getCameraSpace().getY(), polygon.get(1).getCameraSpace().getZ() - polygon.get(0).getCameraSpace().getZ());
			whole = crossProductValue(v1, v2);
			done = true;
		}
		if (useFace) {
			Vertex3D v = new Vertex3D(point.getCameraSpace(), point.getColor());
			v.setCameraSpace(point.getCameraSpace());
			v.setNormal(faceNormal);
			return lightCalculation(lightSources, v);
		}
		else {
			double x = point.getCameraSpace().getX();
			double y = point.getCameraSpace().getY();
			double z = point.getCameraSpace().getZ();
			Point3DH v1 = new Point3DH(polygon.get(2).getCameraSpace().getX() - x, polygon.get(2).getCameraSpace().getY() - y, polygon.get(2).getCameraSpace().getZ() - z);
			Point3DH v2 = new Point3DH(polygon.get(1).getCameraSpace().getX() - x, polygon.get(1).getCameraSpace().getY() - y, polygon.get(1).getCameraSpace().getZ() - z);
			Point3DH v3 = new Point3DH(polygon.get(0).getCameraSpace().getX() - x, polygon.get(0).getCameraSpace().getY() - y, polygon.get(0).getCameraSpace().getZ() - z);
			double a = Math.abs(crossProductValue(v1, v2))/whole;
			double b = Math.abs(crossProductValue(v1, v3))/whole;
			double c = Math.abs(crossProductValue(v3, v2))/whole;
			double tmp = a+b+c;
			a /= tmp;
			b /= tmp;
			c /= tmp;
			double xx = a*polygon.get(0).getNormal().getX() + b*polygon.get(1).getNormal().getX() + c*polygon.get(2).getNormal().getX();
			double yy = a*polygon.get(0).getNormal().getY() + b*polygon.get(1).getNormal().getY() + c*polygon.get(2).getNormal().getY();
			double zz = a*polygon.get(0).getNormal().getZ() + b*polygon.get(1).getNormal().getZ() + c*polygon.get(2).getNormal().getZ();
			Point3DH normal = normalizeVector(new Point3DH(xx, yy, zz));
			Vertex3D v = new Vertex3D(point.getCameraSpace(), point.getColor());
			v.setNormal(normal);
			v.setCameraSpace(point.getCameraSpace());
			return lightCalculation(lightSources, v);
		}
	}
}
