package client.interpreter;

import java.util.Stack;

import client.interpreter.LineBasedReader;
import geometry.*;
import javafx.geometry.Point3D;
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

	private double WORLD_LOW_X = -100;
	private double WORLD_HIGH_X = 100;
	private double WORLD_LOW_Y = -100;
	private double WORLD_HIGH_Y = 100;
	private double WORLD_LOW_Z = -200;
	private double WORLD_HIGH_Z = 0;

	private static final int CAMERA_VIEWING_PLAIN = -1;


	private LineBasedReader reader;
	private Stack<LineBasedReader> readerStack;

	private Stack<Transformation> CTMs;

	private Color defaultColor = Color.WHITE;
	private Color ambientLight = Color.BLACK;

	private Drawable drawable;
//	private Drawable depthCueingDrawable;

	private LineRenderer lineRenderer;
	private PolygonRenderer filledRenderer;
	private PolygonRenderer wireframeRenderer;
	PolygonRenderer polygonRenderer;
	private Transformation cameraToScreen;
	Clipper clipper;
//	private ObjReader objReader;

	private double depthNear = -Double.MAX_VALUE, depthFar = -Double.MAX_VALUE;
	private Color depthColor = Color.BLACK;

	private boolean isInCameraWorld = false;
	private double cameraLowX, cameraLowY, cameraNearZ, cameraHighX, cameraHighY, cameraFarZ;

	public enum RenderStyle {
		FILLED,
		WIREFRAME;
	}
	public SimpInterpreter(String filename,
			Drawable drawable,
			RendererTrio renderers) {
		this.drawable = drawable;
//		this.depthCueingDrawable = drawable;
		this.lineRenderer = renderers.getLineRenderer();
		this.filledRenderer = renderers.getFilledRenderer();
		this.wireframeRenderer = renderers.getWireframeRenderer();
//		this.defaultColor = Color.WHITE;

		polygonRenderer = filledRenderer;



		CTMs = new Stack<>();
		CTM = new Transformation();
		worldToScreen = new Transformation();
		cameraToScreen = new Transformation();

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
		double scale = scaleX > scaleY? scaleY: scaleX;
		double centerX = (WORLD_HIGH_X + WORLD_LOW_X)/2;
		double centerY = (WORLD_HIGH_Y + WORLD_LOW_Y)/2;
//		CTM = new Transformation(Matrix.matrixMultiple(Matrix.inverse(worldToScreen.getMatrix()), CTM.getMatrix()));
//		System.out.println(CTM.getMatrix());

		worldToScreen = new Transformation();

		double moveX = dimensions.getWidth()/2 - centerX*scale;
		double moveY = dimensions.getHeight()/2 - centerY*scale;
//		System.out.println(moveX);
//		System.out.println(moveY);
		worldToScreen.translate(moveX, moveY, 0);
		worldToScreen.scale(scale, scale, 1);
//		CTM = new Transformation(Matrix.matrixMultiple(worldToScreen.getMatrix(), CTM.getMatrix()));

//		System.out.println(CTM.getMatrix());
//		System.out.println(worldToScreen.getMatrix());
//		System.out.println(Matrix.inverse(worldToScreen.getMatrix()));
//		System.out.println(Matrix.matrixMultiple(CTM.getMatrix(), Matrix.inverse(worldToScreen.getMatrix())));
//		System.out.println(scale*(centerX-WORLD_LOW_X));
//		System.out.println(scale*(centerY-WORLD_LOW_Y));
		clipper = new Clipper(dimensions.getWidth()/2-scale*(centerX-WORLD_LOW_X), dimensions.getWidth()/2+scale*(centerX-WORLD_LOW_X), dimensions.getHeight()/2-scale*(centerY-WORLD_LOW_Y), dimensions.getHeight()/2+scale*(centerY-WORLD_LOW_Y), WORLD_LOW_Z, WORLD_HIGH_Z);
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
		case "camera" :		interpretCamera(tokens);	break;
		case "surface" :	interpretSurface(tokens);	break;
		case "ambient" :	interpretAmbient(tokens);	break;
		case "depth" :		interpretDepth(tokens);		break;
		case "obj" :		interpretObj(tokens);		break;

		default :
			System.err.println("bad input line: " + tokens);
			break;
		}
	}


	private void interpretSurface(String[] tokens) {
		defaultColor = interpretColor(tokens, 1);
	}

	private void interpretAmbient(String[] tokens) {
		ambientLight = interpretColor(tokens, 1);
	}

	private void interpretDepth(String[] tokens) {
		depthNear = cleanNumber(tokens[1]);
		depthFar = cleanNumber(tokens[2]);
		depthColor = interpretColor(tokens, 3);
	}

	private void interpretCamera(String[] tokens) {
		WORLD_LOW_X = cleanNumber(tokens[1]);
		WORLD_LOW_Y = cleanNumber(tokens[2]);
		WORLD_HIGH_X = cleanNumber(tokens[3]);
		WORLD_HIGH_Y = cleanNumber(tokens[4]);
		WORLD_HIGH_Z = cleanNumber(tokens[5]);
		WORLD_LOW_Z = cleanNumber(tokens[6]);
//		clipper = new Clipper(cameraLowX, cameraHighX, cameraLowY, cameraHighY, cameraFarZ, cameraNearZ);
		isInCameraWorld = true;
		makeWorldToScreenTransform(drawable.getDimensions());
//		Matrix tmp = Matrix.matrixMultiple(Matrix.inverse(worldToScreen.getMatrix()), CTM.getMatrix());
//		Matrix tmp = Matrix.matrixMultiple(worldToScreen.getMatrix(), Matrix.inverse(CTM.getMatrix()));
//		cameraToScreen = new Transformation(tmp);
//		System.out.println();
//		System.out.println(worldToScreen.getMatrix());
//		System.out.println(tmp);
//		System.out.println(Matrix.matrixMultiple(cameraToScreen.getMatrix(), CTM.getMatrix()));
////		System.out.println(Matrix.inverse(tmp));

//		cameraToScreen = new Transformation(Matrix.matrixMultiple(worldToScreen.getMatrix(), Matrix.inverse(CTM.getMatrix())));
//		clipper = new Clipper(WORLD_LOW_X, WORLD_HIGH_X, WORLD_LOW_Y, WORLD_HIGH_Y, WORLD_LOW_Z, WORLD_HIGH_Z);
		cameraToScreen = new Transformation(Matrix.inverse(CTM.getMatrix()));
//		System.out.println();
//		System.out.println(worldToScreen.getMatrix());
//		System.out.println(CTM.getMatrix());
//		System.out.println(cameraToScreen.getMatrix());
	}

	private void interpretObj(String[] tokens) {
		String quotedFilename = tokens[1];
		int length = quotedFilename.length();
		assert quotedFilename.charAt(0) == '"' && quotedFilename.charAt(length-1) == '"';
		String filename = quotedFilename.substring(1, length-1);
//		file(filename + ".simp");
		objFile(filename);
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
	private static double cleanNumber(String string) {
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
//		depthCueingDrawable = new DepthCueingDrawable(drawable, vertices[0].getColor());

		System.out.println(vertices[0]);
		System.out.println(vertices[1]);

		vertices = clipper.clipLineZ(vertices);
		if (vertices != null) {
//			if (isInCameraWorld) {
//				for (Vertex3D v: vertices) {
//					v = transformToCamera(v);
//				}
//			}
//			System.out.println(vertices[0]);
//			System.out.println(vertices[1]);
			if (isInCameraWorld) {
				for (int i = 0; i < vertices.length; ++i) {
					vertices[i] = transformToCamera(vertices[i]);
					Matrix m = new Matrix(vertices[i]);
					m = Matrix.matrixMultiple(worldToScreen.getMatrix(), m);
					vertices[i] = Matrix.convertToPoint(m, vertices[i].getColor());
				}
			}
			vertices = clipper.clipLine(vertices);
			if (vertices != null) {

//				System.out.println(vertices[0]);
//				System.out.println(vertices[1]);

				lineRenderer.drawLine(vertices[0], vertices[1], drawable);
			}

		}
	}

	public static Vertex3D objTransformVertex(SimpInterpreter simp, Vertex3D vertex){
		Vertex3D transformedVertex;
		Color color = vertex.getColor();
		color = new Color(color.getR()*simp.ambientLight.getR(), color.getG()*simp.ambientLight.getG(), color.getB()*simp.ambientLight.getB());

		Matrix m = new Matrix(vertex);
		m = Matrix.matrixMultiple(simp.CTM.getMatrix(), m);
		if (simp.isInCameraWorld) {
			m = Matrix.matrixMultiple(simp.cameraToScreen.getMatrix(), m);
		}
		else {
			m = Matrix.matrixMultiple(simp.worldToScreen.getMatrix(), m);
		}

		transformedVertex = Matrix.convertToPoint(m, color);
		return transformedVertex;
	}

	public static void objRenderPolygon(SimpInterpreter simp, Vertex3D[] vertices){
		vertices = simp.clipper.clipPolygonZ(vertices);

		if (simp.isInCameraWorld) {
			for (int i = 0; i < vertices.length; ++i) {
				vertices[i] = transformToCamera(vertices[i]);
				Color color = vertices[i].getColor();
				if (vertices[i].getZ() > simp.depthNear) {
					;
				}
				else if (vertices[i].getZ() < simp.depthFar) {
					color = simp.depthColor;
				}
				else {
					double r1 = color.getR();
					double g1 = color.getG();
					double b1 = color.getB();
					double r2 = simp.depthColor.getR();
					double g2 = simp.depthColor.getG();
					double b2 = simp.depthColor.getB();
					double deltaZ = simp.depthNear - simp.depthFar;
					double deltaZ2 = simp.depthNear - vertices[i].getZ();
					double mr = (r1-r2) / deltaZ;
					double mg = (g1-g2) / deltaZ;
					double mb = (b1-b2) / deltaZ;
					color = new Color(r1 - deltaZ2*mr, g1 - deltaZ2*mg, b1 - deltaZ2*mb);
				}
				Matrix m = new Matrix(vertices[i]);
				m = Matrix.matrixMultiple(simp.worldToScreen.getMatrix(), m);
				vertices[i] = Matrix.convertToPoint(m, color);
			}
		}

		if (vertices != null) {
			vertices = simp.clipper.clipPolygon(vertices);

			if (vertices != null && vertices.length > 2) {
				simp.polygonRenderer.drawPolygon(Polygon.make(vertices), simp.drawable);
			}
		}
	}

	private void interpretPolygon(String[] tokens) {
		Vertex3D[] vertices = interpretVertices(tokens, 3, 1);

		// TODO: finish this method
//		System.out.println(vertices.length);
//		depthCueingDrawable = new DepthCueingDrawable(drawable, vertices[0].getColor());
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
//		if (Polygon.isClockwise(vertices[0], vertices[1], vertices[2])) {
////			polygon = Polygon.make(vertices[0], vertices[2], vertices[1]);
//			return;
//		}
//		else{
//			polygon = Polygon.make(vertices[0], vertices[1], vertices[2]);
//		}
//		System.out.println();
//		System.out.println(vertices.length);
//		System.out.println(vertices[0]);
//		System.out.println(vertices[1]);
//		System.out.println(vertices[2]);

		vertices = clipper.clipPolygonZ(vertices);

//		System.out.println(vertices.length);
//		for (Vertex3D vertex: vertices) {
//			System.out.println(vertex);
//		}
//		System.out.println();
		if (isInCameraWorld) {
			for (int i = 0; i < vertices.length; ++i) {
				vertices[i] = transformToCamera(vertices[i]);

				Color color = vertices[i].getColor();
				if (vertices[i].getZ() > depthNear) {
					;
				}
				else if (vertices[i].getZ() < depthFar) {
					color = depthColor;
				}
				else {
					double r1 = color.getR();
					double g1 = color.getG();
					double b1 = color.getB();
					double r2 = depthColor.getR();
					double g2 = depthColor.getG();
					double b2 = depthColor.getB();
					double deltaZ = depthNear - depthFar;
					double deltaZ2 = depthNear - vertices[i].getZ();
					double mr = (r1-r2) / deltaZ;
					double mg = (g1-g2) / deltaZ;
					double mb = (b1-b2) / deltaZ;
					color = new Color(r1 - deltaZ2*mr, g1 - deltaZ2*mg, b1 - deltaZ2*mb);
				}
				Matrix m = new Matrix(vertices[i]);
				m = Matrix.matrixMultiple(worldToScreen.getMatrix(), m);
				vertices[i] = Matrix.convertToPoint(m, color);
			}
		}
//		System.out.println(worldToScreen.getMatrix());
//		System.out.println(vertices.length);
//		for (Vertex3D vertex: vertices) {
//			System.out.println(vertex);
//		}

		if (vertices != null) {
			vertices = clipper.clipPolygon(vertices);
//			for (int i = 0; i < vertices.length; ++i) {
//				Color color = vertices[i].getColor();
//				if (vertices[i].getZ() > depthNear) {
//					;
//				}
//				else if (vertices[i].getZ() < depthFar) {
//					color = depthColor;
//				}
//				else {
//					double r1 = color.getR();
//					double g1 = color.getG();
//					double b1 = color.getB();
//					double r2 = depthColor.getR();
//					double g2 = depthColor.getG();
//					double b2 = depthColor.getB();
//					double deltaZ = depthNear - depthFar;
//					double deltaZ2 = depthNear - vertices[i].getZ();
//					double mr = (r1-r2) / deltaZ;
//					double mg = (g1-g2) / deltaZ;
//					double mb = (b1-b2) / deltaZ;
//					color = new Color(r1 - deltaZ2*mr, g1 - deltaZ2*mg, b1 - deltaZ2*mb);
//				}
//				Matrix m = new Matrix(vertices[i]);
//				vertices[i] = Matrix.convertToPoint(m, color);
//			}
			if (vertices != null && vertices.length > 2) {
//				System.out.println(vertices.length);
//				for (Vertex3D vertex: vertices) {
//					System.out.println(vertex);
//				}
//				System.out.println();
				polygonRenderer.drawPolygon(Polygon.make(vertices), drawable);
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
		color = new Color(color.getR()*ambientLight.getR(), color.getG()*ambientLight.getG(), color.getB()*ambientLight.getB());
		// TODO: finish this method

		Vertex3D p = new Vertex3D(point, color);
		Matrix m = new Matrix(p);
		m = Matrix.matrixMultiple(CTM.getMatrix(), m);
//		p = Matrix.convertToPoint(m, color);

//		System.out.println(m);
//		System.out.println(CTM.getMatrix());
//		Matrix tmp;
		if (isInCameraWorld) {
			m = Matrix.matrixMultiple(cameraToScreen.getMatrix(), m);
//			p = Matrix.convertToPoint(m, color);
//			p = transformToCamera(Matrix.convertToPoint(m, color));
//			m = new Matrix(p);
		}
		else {
			m = Matrix.matrixMultiple(worldToScreen.getMatrix(), m);
		}

//		m = Matrix.matrixMultiple(worldToScreen.getMatrix(), m);
//		System.out.println(m);
//		System.out.println(m.matrix[0][0]);
//		System.out.println(m.matrix[1][0]);
//		System.out.println(m.matrix[3][0]);

		p = Matrix.convertToPoint(m, color);

//		if (isInCameraWorld) {
//			point = new Point3DH(point.getX())
//			p = new Vertex3D()
//		}
//		p.replaceColor(color);

//		System.out.println(p.getX());
//		System.out.println(p.getY());
//		System.out.println(p.getZ());

		return p;
	}
	public static Point3DH interpretPoint(String[] tokens, int startingIndex) {
		double x = cleanNumber(tokens[startingIndex]);
		double y = cleanNumber(tokens[startingIndex + 1]);
		double z = cleanNumber(tokens[startingIndex + 2]);
//		System.out.println(x);
//		System.out.println(y);
//		System.out.println(z);
		// TODO: finish this method
		return new Point3DH(x, y, z);
	}
	public static Color interpretColor(String[] tokens, int startingIndex) {
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

	public static Vertex3D transformToCamera(Vertex3D vertex) {
		// TODO: finish this method
//		System.out.println();
//		System.out.println(vertex);
		if (Math.abs(vertex.getZ()) < 1e-6 ) {
			return new Vertex3D(0, 0, Double.MAX_VALUE, vertex.getColor());
		}
		vertex = new Vertex3D(vertex.getX()/vertex.getZ()*CAMERA_VIEWING_PLAIN, vertex.getY()/vertex.getZ()*CAMERA_VIEWING_PLAIN, vertex.getZ(), vertex.getColor());

//		System.out.println(vertex);
		return vertex;
	}


	public static Point3DH interpretPointWithW(String[] tokens, int startingIndex) {
		double x = cleanNumber(tokens[startingIndex]);
		double y = cleanNumber(tokens[startingIndex + 1]);
		double z = cleanNumber(tokens[startingIndex + 2]);
		double w = cleanNumber(tokens[startingIndex + 3]);
		return new Point3DH(x, y, z, w);
	}

	private void objFile(String filename) {
		if (isInCameraWorld) {

		}
		ObjReader objReader = new ObjReader(filename, defaultColor, this);
		objReader.read();
		objReader.render();
	}

}

