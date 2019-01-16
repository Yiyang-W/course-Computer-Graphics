package polygon;

import client.interpreter.SimpInterpreter;
import geometry.Point3DH;
import geometry.Vertex3D;
import shading.LightSource;
import windowing.graphics.Color;

import java.util.List;

@FunctionalInterface
public interface Shader {
	public Color shade(Polygon polygon, Vertex3D point);
	default boolean isNoNormal(Point3DH normal) {
		return (normal.getX() == 0  &&  normal.getY() == 0  &&  normal.getZ() == 0);
	}

	default Color lightCalculation(List<LightSource> lightSources, Vertex3D point) {
		Color newColor = ambientLight(point.getColor());
		double r = newColor.getR();
		double g = newColor.getG();
		double b = newColor.getB();

		for (LightSource light : lightSources) {
			double distance = calculateDistance(light.getLightPosition(), point.getCameraSpace());
			double fatt = 1/(light.getA() + distance*light.getB());
			Point3DH L = calculateVector(light.getLightPosition(), point.getCameraSpace());
			if (dotProduct(L, point.getNormal()) < 0) {
				continue;
			}
			Point3DH R = calculateReflect(L, point.getNormal());
			Point3DH V = normalizeVector(point.getCameraSpace());
			double tmp = SimpInterpreter.ks * Math.pow(dotProduct(V, R), SimpInterpreter.p);
			r += fatt*light.getColor().getR()*(tmp + point.getColor().getR()*dotProduct(L, point.getNormal()));
			g += fatt*light.getColor().getG()*(tmp + point.getColor().getG()*dotProduct(L, point.getNormal()));
			b += fatt*light.getColor().getB()*(tmp + point.getColor().getB()*dotProduct(L, point.getNormal()));
		}
		newColor = new Color(r, g, b);
//		System.out.println(newColor);
		return newColor;
	}
	default Color ambientLight(Color color) {
		double r = color.getR() * SimpInterpreter.ambientLight.getR();
		double g = color.getG() * SimpInterpreter.ambientLight.getG();
		double b = color.getB() * SimpInterpreter.ambientLight.getB();
		return new Color(r, g, b);
	}
	default Point3DH getFaceNormal(Polygon polygon) {
		if (isNoNormal(polygon.get(0).getNormal()) || isNoNormal(polygon.get(1).getNormal()) || isNoNormal(polygon.get(2).getNormal())) {
			return calculateFaceNormal(polygon);
		}
		double x = polygon.get(0).getNormal().getX() + polygon.get(1).getNormal().getX() + polygon.get(2).getNormal().getX();
		double y = polygon.get(0).getNormal().getY() + polygon.get(1).getNormal().getY() + polygon.get(2).getNormal().getY();
		double z = polygon.get(0).getNormal().getZ() + polygon.get(1).getNormal().getZ() + polygon.get(2).getNormal().getZ();

		return normalizeVector(new Point3DH(x, y, z));
	}
	default Point3DH calculateFaceNormal(Polygon polygon) {
//		List<Vertex3D> vertices = polygon.vertices;
		Point3DH cameraSpace1 = polygon.get(0).getCameraSpace();
		Point3DH cameraSpace2 = polygon.get(1).getCameraSpace();
		Point3DH cameraSpace3 = polygon.get(2).getCameraSpace();

		double x1 = cameraSpace2.getX() - cameraSpace1.getX();
		double y1 = cameraSpace2.getY() - cameraSpace1.getY();
		double z1 = cameraSpace2.getZ() - cameraSpace1.getZ();
		double x2 = cameraSpace3.getX() - cameraSpace1.getX();
		double y2 = cameraSpace3.getY() - cameraSpace1.getY();
		double z2 = cameraSpace3.getZ() - cameraSpace1.getZ();

		double x3 = y1*z2 - y2*z1;
		double y3 = z1*x2 - z2*x1;
		double z3 = x1*y2 - x2*y1;
		return normalizeVector(new Point3DH(x3, y3, z3));
	}

	default Point3DH calculateVector(Point3DH p2, Point3DH p1) {
		double x1 = p2.getX() - p1.getX();
		double y1 = p2.getY() - p1.getY();
		double z1 = p2.getZ() - p1.getZ();

		return normalizeVector(new Point3DH(x1, y1, z1));
	}
	default Point3DH calculateReflect(Point3DH l, Point3DH normal) {
		double scale = dotProduct(l, normal);
		double x = 2*scale*normal.getX() - l.getX();
		double y = 2*scale*normal.getY() - l.getY();
		double z = 2*scale*normal.getZ() - l.getZ();
		return new Point3DH(x, y, z);
	}

	default double dotProduct(Point3DH p1, Point3DH p2) {
		return p1.getX()*p2.getX() + p1.getY()*p2.getY() + p1.getZ()*p2.getZ();
	}

	default Point3DH normalizeVector(Point3DH oldNormal) {
		if (isNoNormal(oldNormal)) {
//			System.out.println("It has no normal!");
			return oldNormal;
		}
		double scale = 1/Math.sqrt(Math.pow(oldNormal.getX(),2) + Math.pow(oldNormal.getY(),2) + Math.pow(oldNormal.getZ(),2));
		return new Point3DH(scale*oldNormal.getX(), scale*oldNormal.getY(), scale*oldNormal.getZ());
	}
	default double calculateDistance(Point3DH p1, Point3DH p2) {
		return Math.sqrt(Math.pow(p1.getX()-p2.getX(), 2) + Math.pow(p1.getY()-p2.getY(), 2) + Math.pow(p1.getZ()-p2.getZ(), 2));
	}
	default double crossProductValue(Point3DH p, Point3DH q) {
		Point3DH cross = crossProduct(p, q);
		return calculateDistance(cross, new Point3DH(0,0,0));
	}

	default Point3DH crossProduct(Point3DH p, Point3DH q) {
		double x = p.getY()*q.getZ() - q.getY()*p.getZ();
		double y = p.getZ()*q.getX() - q.getZ()*p.getX();
		double z = p.getX()*q.getY() - q.getX()*p.getY();
		return new Point3DH(x, y, z);
	}
}
