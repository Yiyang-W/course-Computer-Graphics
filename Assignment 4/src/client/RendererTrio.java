package client;

import line.DDALineRenderer;
import line.LineRenderer;
import polygon.FilledPolygonRenderer;
import polygon.PolygonRenderer;
import polygon.WireframePolygonRenderer;

public class RendererTrio {
	LineRenderer lineRenderer;
	PolygonRenderer filledPolygonRenderer;
	PolygonRenderer wireframePolygonRenderer;
	RendererTrio(){
		lineRenderer = DDALineRenderer.make();
		filledPolygonRenderer = FilledPolygonRenderer.make();
		wireframePolygonRenderer = WireframePolygonRenderer.make();
	}

	public LineRenderer getLineRenderer() {
		return lineRenderer;
	}

	public PolygonRenderer getFilledRenderer() {
		return filledPolygonRenderer;
	}

	public PolygonRenderer getWireframeRenderer() {
		return wireframePolygonRenderer;
	}
}
