package polygon;

import geometry.Vertex3D;
import line.LineRenderer;
import windowing.drawable.Drawable;
import line.DDALineRenderer;

public class WireframePolygonRenderer implements PolygonRenderer {
	@Override
	public void drawPolygon(Polygon polygon, Drawable drawable, Shader vertexShader) {
		Chain leftChain = polygon.leftChain();
		Chain rightChain = polygon.rightChain();
		drawOneSide(leftChain, drawable);
		drawOneSide(rightChain, drawable);
	}
	private void drawOneSide(Chain chain, Drawable drawable){
		int count = 0;
		Vertex3D p1 = chain.get(count++);
		Vertex3D p2 = chain.get(count++);
		LineRenderer lineRenderer = DDALineRenderer.make();
		while (count < chain.length()) {
			lineRenderer.drawLine(p1, p2, drawable);
			p1 = p2;
			p2 = chain.get(count++);
		}
		lineRenderer.drawLine(p1, p2, drawable);
	}

	public static PolygonRenderer make() {
		return new WireframePolygonRenderer();
	}
}
