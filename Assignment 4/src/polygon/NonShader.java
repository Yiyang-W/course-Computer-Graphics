package polygon;

import geometry.Point3DH;
import geometry.Vertex3D;
import windowing.graphics.Color;

public class NonShader implements Shader {
	@Override
	public Color shade(Polygon polygon, Vertex3D point) {
//		return point.getColor();
		return ambientLight(point.getColor());
	}
}
