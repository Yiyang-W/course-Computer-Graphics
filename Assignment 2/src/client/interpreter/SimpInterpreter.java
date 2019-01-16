package client.interpreter;

import java.util.Stack;

import client.interpreter.LineBasedReader;
import geometry.*;
import line.LineRenderer;
import client.Clipper;
import client.DepthCueingDrawable;
import client.RendererTrio;
import polygon.Polygon;
import polygon.PolygonRenderer;
import polygon.Shader;
import windowing.drawable.Drawable;
import windowing.graphics.Color;
import windowing.graphics.Dimensions;

public class SimpInterpreter {
	private static final int NUM_TOKENS_FOR_POINT = 3;
	private static final int NUM_TOKENS_FOR_COMMAND = 1;
	private static final int NUM_TOKENS_FOR_COLORED_VERTEX = 6;
	private static final int NUM_TOKENS_FOR_UNCOLORED_VERTEX = 3;
	private static final char COMMENT_CHAR = '#';
	private RenderStyle renderStyle;

	private Transformation CTM;
	private Transformation worldToScreen;

	private int WORLD_LOW_X = -100;
	private int WORLD_HIGH_X = 100;
	private int WORLD_LOW_Y = -100;
	private int WORLD_HIGH_Y = 100;

	private LineBasedReader reader;
	private Stack<LineBasedReader> readerStack;

	private Stack<Transformation> CTMs;

	private Color defaultColor = Color.WHITE;
	private Color ambientLight = Color.BLACK;

	private Drawable drawable;
	private Drawable depthCueingDrawable;

	private LineRenderer lineRenderer;
	private PolygonRenderer filledRenderer;
	private PolygonRenderer wireframeRenderer;
	private PolygonRenderer polygonRenderer;
	private Transformation cameraToScreen;
	private Clipper clipper;

	public enum RenderStyle {
		FILLED,
		WIREFRAME;
	}
	public SimpInterpreter(String filename,
			Drawable drawable,
			RendererTrio renderers) {
		this.drawable = drawable;
		this.depthCueingDrawable = drawable;
		this.lineRenderer = renderers.getLineRenderer();
		this.filledRenderer = renderers.getFilledRenderer();
		this.wireframeRenderer = renderers.getWireframeRenderer();
		this.defaultColor = Color.WHITE;

		polygonRenderer = filledRenderer;



		CTMs = new Stack<>();
		CTM = new Transformation();

//		System.out.print(CTM.getMatrix());

		makeWorldToScreenTransform(drawable.getDimensions());



		reader = new LineBasedReader(filename);
		readerStack = new Stack<>();
		renderStyle = RenderStyle.FILLED;
//		CTM = Transformation.identity();

		interpret();
	}

	private void makeWorldToScreenTransform(Dimensions dimensions) {
		// TODO: fill this in
		double scaleX = dimensions.getWidth()/(WORLD_HIGH_X - WORLD_LOW_X);
		double scaleY = dimensions.getHeight()/(WORLD_HIGH_Y - WORLD_LOW_Y);
		CTM.translate(dimensions.getWidth()/2, dimensions.getHeight()/2, 0);
		CTM.scale(scaleX, scaleY, 1);
//		WORLD_LOW_X *= scaleX;
//		WORLD_HIGH_X *= scaleX;
//		WORLD_LOW_Y *= scaleY;
//		WORLD_HIGH_Y *= scaleY;
		clipper = new Clipper(0, dimensions.getWidth(), 0, dimensions.getHeight(), -200, 0);
	}

	public void interpret() {
		while(reader.hasNext() ) {
			String line = reader.next().trim();
			interpretLine(line);
			while(!reader.hasNext()) {
				if(readerStack.isEmpty()) {
					return;
				}
				else {
					reader = readerStack.pop();
				}
			}
		}
	}
	public void interpretLine(String line) {
		if(!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
			String[] tokens = line.split("[ \t,()]+");
			if(tokens.length != 0) {
				interpretCommand(tokens);
			}
		}
	}
	private void interpretCommand(String[] tokens) {
		switch(tokens[0]) {
		case "{" :      push();   break;
		case "}" :      pop();    break;
		case "wire" :   wire();   break;
		case "filled" : filled(); break;

		case "file" :		interpretFile(tokens);		break;
		case "scale" :		interpretScale(tokens);		break;
		case "translate" :	interpretTranslate(tokens);	break;
		case "rotate" :		interpretRotate(tokens);	break;
		case "line" :		interpretLine(tokens);		break;
		case "polygon" :	interpretPolygon(tokens);	break;
//		case "camera" :		interpretCamera(tokens);	break;
//		case "surface" :	interpretSurface(tokens);	break;
//		case "ambient" :	interpretAmbient(tokens);	break;
//		case "depth" :		interpretDepth(tokens);		break;
//		case "obj" :		interpretObj(tokens);		break;

		default :
			System.err.println("bad input line: " + tokens);
			break;
		}
	}

	private void push() {
		// TODO: finish this method
		CTMs.push(new Transformation(CTM));
//		System.out.println(CTMs.size());
	}
	private void pop() {
		// TODO: finish this method
		CTM = CTMs.pop();
//		System.out.println(CTMs.size());
	}
	private void wire() {
		// TODO: finish this method
		polygonRenderer = wireframeRenderer;
	}
	private void filled() {
		// TODO: finish this method
		polygonRenderer = filledRenderer;
	}

	// this one is complete.
	private void interpretFile(String[] tokens) {
		String quotedFilename = tokens[1];
		int length = quotedFilename.length();
		assert quotedFilename.charAt(0) == '"' && quotedFilename.charAt(length-1) == '"';
		String filename = quotedFilename.substring(1, length-1);
		file(filename + ".simp");
	}
	private void file(String filename) {
		readerStack.push(reader);
		reader = new LineBasedReader(filename);
	}

	private void interpretScale(String[] tokens) {
		double sx = cleanNumber(tokens[1]);
		double sy = cleanNumber(tokens[2]);
		double sz = cleanNumber(tokens[3]);
		// TODO: finish this method
		CTM.scale(sx, sy, sz);
	}
	private void interpretTranslate(String[] tokens) {
		double tx = cleanNumber(tokens[1]);
		double ty = cleanNumber(tokens[2]);
		double tz = cleanNumber(tokens[3]);
		// TODO: finish this method
		CTM.translate(tx, ty, tz);
	}
	private void interpretRotate(String[] tokens) {
		String axisString = tokens[1];
		double angleInDegrees = cleanNumber(tokens[2]);

		// TODO: finish this method
		CTM.rotate(axisString, angleInDegrees);
	}
	private double cleanNumber(String string) {
		return Double.parseDouble(string);
	}

	private enum VertexColors {
		COLORED(NUM_TOKENS_FOR_COLORED_VERTEX),
		UNCOLORED(NUM_TOKENS_FOR_UNCOLORED_VERTEX);

		private int numTokensPerVertex;

		private VertexColors(int numTokensPerVertex) {
			this.numTokensPerVertex = numTokensPerVertex;
		}
		public int numTokensPerVertex() {
			return numTokensPerVertex;
		}
	}



	private void interpretLine(String[] tokens) {
		Vertex3D[] vertices = interpretVertices(tokens, 2, 1);

		// TODO: finish this method
		depthCueingDrawable = new DepthCueingDrawable(drawable, vertices[0].getColor());

//		System.out.println(vertices[0]);
//		System.out.println(vertices[1]);

		vertices = clipper.clipLineZ(vertices);
		if (vertices != null) {

//			System.out.println(vertices[0]);
//			System.out.println(vertices[1]);
			vertices = clipper.clipLine(vertices);
			if (vertices != null) {

//				System.out.println(vertices[0]);
//				System.out.println(vertices[1]);

				lineRenderer.drawLine(vertices[0], vertices[1], depthCueingDrawable);
			}

		}
	}

	private void interpretPolygon(String[] tokens) {
		Vertex3D[] vertices = interpretVertices(tokens, 3, 1);

		// TODO: finish this method
//		System.out.println(vertices.length);
		depthCueingDrawable = new DepthCueingDrawable(drawable, vertices[0].getColor());
//		if (isOneLine(vertices)) {
//			int index = 0;
//			if (vertices[1].getZ() < vertices[index].getZ()) {
//				index = 1;
//			}
//			if (vertices[2].getZ() < vertices[index].getZ()) {
//				index = 2;
//			}
//			Vertex3D p1, p2;
//			if (index == 0) {
//				p1 = vertices[1];
//				p2 = vertices[2];
//			}
//			else if (index == 1) {
//				p1 = vertices[0];
//				p2 = vertices[2];
//			}
//			else {
//				p1 = vertices[1];
//				p2 = vertices[0];
//			}
//			lineRenderer.drawLine(p1, p2, depthCueingDrawable);
//			return;
//		}
//		Polygon polygon;
		if (Polygon.isClockwise(vertices[0], vertices[1], vertices[2])) {
//			polygon = Polygon.make(vertices[0], vertices[2], vertices[1]);
			return;
		}
//		else{
//			polygon = Polygon.make(vertices[0], vertices[1], vertices[2]);
//		}
//		System.out.println(vertices[0]);
//		System.out.println(vertices[1]);
//		System.out.println(vertices[2]);
//		System.out.println(vertices.length);
//		System.out.println();
//
		vertices = clipper.clipPolygonZ(vertices);
//
//		System.out.println(vertices.length);
//		System.out.println(vertices[0]);
//		System.out.println(vertices[1]);
//		System.out.println(vertices[2]);

//		System.out.println();
		if (vertices != null) {
			vertices = clipper.clipPolygon(vertices);
			if (vertices != null && vertices.length > 2) {
//				System.out.println(vertices[0]);
//				System.out.println(vertices[1]);
//				System.out.println(vertices[2]);
//				System.out.println(vertices.length);
//				System.out.println();
				polygonRenderer.drawPolygon(Polygon.make(vertices), depthCueingDrawable);
			}
		}

	}

	private boolean isOneLine(Vertex3D[] vertices){
		return  (vertices[0].getIntX() == vertices[1].getIntX()
				&& vertices[0].getIntX() == vertices[2].getIntX()
				&& vertices[1].getIntX() == vertices[2].getIntX())
				||
				(vertices[0].getIntY() == vertices[1].getIntY()
				&& vertices[0].getIntY() == vertices[2].getIntY()
				&& vertices[1].getIntY() == vertices[2].getIntY());
	}

	public Vertex3D[] interpretVertices(String[] tokens, int numVertices, int startingIndex) {
		VertexColors vertexColors = verticesAreColored(tokens, numVertices);
		Vertex3D vertices[] = new Vertex3D[numVertices];

		for(int index = 0; index < numVertices; index++) {
			vertices[index] = interpretVertex(tokens, startingIndex + index * vertexColors.numTokensPerVertex(), vertexColors);
		}
		return vertices;
	}
	public VertexColors verticesAreColored(String[] tokens, int numVertices) {
		return hasColoredVertices(tokens, numVertices) ? VertexColors.COLORED :
														 VertexColors.UNCOLORED;
	}
	public boolean hasColoredVertices(String[] tokens, int numVertices) {
		return tokens.length == numTokensForCommandWithNVertices(numVertices);
	}
	public int numTokensForCommandWithNVertices(int numVertices) {
		return NUM_TOKENS_FOR_COMMAND + numVertices*(NUM_TOKENS_FOR_COLORED_VERTEX);
	}


	private Vertex3D interpretVertex(String[] tokens, int startingIndex, VertexColors colored) {
		Point3DH point = interpretPoint(tokens, startingIndex);

		Color color = defaultColor;
		if(colored == VertexColors.COLORED) {
			color = interpretColor(tokens, startingIndex + NUM_TOKENS_FOR_POINT);
		}

		// TODO: finish this method

		Vertex3D p = new Vertex3D(point, color);

		Matrix m = new Matrix(p);

//		System.out.println(m);
//		System.out.println(CTM.getMatrix());
		m = Matrix.matrixMultiple(CTM.getMatrix(), m);
//		System.out.println(m);
//		System.out.println(m.matrix[0][0]);
//		System.out.println(m.matrix[1][0]);
//		System.out.println(m.matrix[3][0]);

		p = Matrix.convertToPoint(m, color);
//		p.replaceColor(color);

//		System.out.println(p.getX());
//		System.out.println(p.getY());
//		System.out.println(p.getZ());

		return p;
	}
	public Point3DH interpretPoint(String[] tokens, int startingIndex) {
		double x = cleanNumber(tokens[startingIndex]);
		double y = cleanNumber(tokens[startingIndex + 1]);
		double z = cleanNumber(tokens[startingIndex + 2]);

//		System.out.println(x);
//		System.out.println(y);
//		System.out.println(z);
		// TODO: finish this method
		return new Point3DH(x, y, z);
	}
	public Color interpretColor(String[] tokens, int startingIndex) {
		double r = cleanNumber(tokens[startingIndex]);
		double g = cleanNumber(tokens[startingIndex + 1]);
		double b = cleanNumber(tokens[startingIndex + 2]);

		// TODO: finish this method
		return new Color(r, g, b);
	}

	private void line(Vertex3D p1, Vertex3D p2) {
		Vertex3D screenP1 = transformToCamera(p1);
		Vertex3D screenP2 = transformToCamera(p2);
		// TODO: finish this method
	}
	private void polygon(Vertex3D p1, Vertex3D p2, Vertex3D p3) {
		Vertex3D screenP1 = transformToCamera(p1);
		Vertex3D screenP2 = transformToCamera(p2);
		Vertex3D screenP3 = transformToCamera(p3);
		// TODO: finish this method
	}

	private Vertex3D transformToCamera(Vertex3D vertex) {
		// TODO: finish this method
		return vertex;
	}

}
