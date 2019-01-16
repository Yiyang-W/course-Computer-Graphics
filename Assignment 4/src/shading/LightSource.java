package shading;

import geometry.Point3DH;
import geometry.Transformation;
import windowing.graphics.Color;

public class LightSource {
	private Point3DH lightPosition;
	private Color color;
	private double A, B;

	public LightSource(Point3DH lightPosition, Color color, double a, double b) {
		this.lightPosition = lightPosition;
		this.color = color;
		A = a;
		B = b;
	}

	public Point3DH getLightPosition() {
		return lightPosition;
	}

	public Color getColor() {
		return color;
	}

	public double getA() {
		return A;
	}

	public double getB() {
		return B;
	}
}
