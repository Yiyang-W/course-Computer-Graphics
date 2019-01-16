package geometry;

import windowing.graphics.Color;

public class Vertex3D implements Vertex {
	protected Point3DH point;
	protected Point3DH cameraSpace;
//	protected boolean isInCamera = false;
	protected Point3DH normal = new Point3DH(0,0,0);
	protected Color color;
	
	public Vertex3D(Point3DH point, Color color) {
		super();
		this.point = point;
		this.color = color;
	}
	public Vertex3D(double x, double y, double z, Color color) {
		this(new Point3DH(x, y, z), color);
	}

	public Vertex3D() {
	}
	public double getX() {
		return point.getX();
	}
	public double getY() {
		return point.getY();
	}
	public double getZ() {
		return point.getZ();
	}
//	public double getCameraSpaceZ() {
//		return getZ();
//	}
	public Point getPoint() {
		return point;
	}
	public Point3DH getPoint3D() {
		return point;
	}
	
	public int getIntX() {
		return (int) Math.round(getX());
	}
	public int getIntY() {
		return (int) Math.round(getY());
	}
	public int getIntZ() {
		return (int) Math.round(getZ());
	}
	
	public Color getColor() {
		return color;
	}

	public void setZ(double z){
		point.setZ(z);
	}
	
	public Vertex3D rounded() {
		return new Vertex3D(point.round(), color);
	}
	public Vertex3D add(Vertex other) {
		Vertex3D other3D = (Vertex3D)other;
		return new Vertex3D(point.add(other3D.getPoint()),
				            color.add(other3D.getColor()));
	}
	public Vertex3D subtract(Vertex other) {
		Vertex3D other3D = (Vertex3D)other;
		return new Vertex3D(point.subtract(other3D.getPoint()),
				            color.subtract(other3D.getColor()));
	}
	public Vertex3D scale(double scalar) {
		return new Vertex3D(point.scale(scalar),
				            color.scale(scalar));
	}
	public Vertex3D replacePoint(Point3DH newPoint) {
		return new Vertex3D(newPoint, color);
	}
	public Vertex3D replaceColor(Color newColor) {
		return new Vertex3D(point, newColor);
	}
	public Vertex3D euclidean() {
		Point3DH euclidean = getPoint3D().euclidean();
		return replacePoint(euclidean);
	}

//	public boolean isInCamera() {
//		return isInCamera;
//	}

	public void setCameraSpace(Point3DH cameraSpace){
		this.cameraSpace = cameraSpace;
//		isInCamera = true;
	}

	public void setCameraSpace(double x, double y, double z) {
		setCameraSpace(new Point3DH(x, y, z));
	}

	public Point3DH getCameraSpace() {
		return cameraSpace;
	}

	public void setNormal(Point3DH normal) {
		this.normal = normal;
	}

	public void setNormal(double x, double y, double z) {
		setNormal(new Point3DH(x, y, z));
	}

	public Point3DH getNormal() {
		return normal;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String toString() {
		return "(" + getX() + ", " + getY() + ", " + getZ() + ", " + getColor().toIntString() + ")";
	}
	public String toIntString() {
		return "(" + getIntX() + ", " + getIntY() + getIntZ() + ", " + ", " + getColor().toIntString() + ")";
	}

}
