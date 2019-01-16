package client.interpreter;

import java.util.ArrayList;
import java.util.List;

import client.Clipper;
import geometry.Matrix;
import geometry.Point3DH;
import geometry.Transformation;
import geometry.Vertex3D;
import polygon.Polygon;
import polygon.PolygonRenderer;
import windowing.graphics.Color;

class ObjReader {
	private static final char COMMENT_CHAR = '#';
	private static final int NOT_SPECIFIED = -1;

	private class ObjVertex {
		// TODO: fill this class in.  Store indices for a vertex, a texture, and a normal.  Have getters for them.
		int vertexIndex;
		int textureIndex;
		int normalIndex;
		public ObjVertex(int vertexIndex, int textureIndex, int normalIndex){
			this.vertexIndex = vertexIndex;
			this.textureIndex = textureIndex;
			this.normalIndex = normalIndex;
		}
	}
	private class ObjFace extends ArrayList<ObjVertex> {
		private static final long serialVersionUID = -4130668677651098160L;
	}

	private SimpInterpreter simpInterpreter;

//	private Clipper clipper;
//	private PolygonRenderer polygonRenderer;
//	private Transformation CTM, cameraToScreen, worldToScreen;
//	boolean isInCameraWorld;

	private LineBasedReader reader;
	
	private List<Vertex3D> objVertices;
	private List<Vertex3D> transformedVertices;
	private List<Point3DH> objNormals;
	private List<ObjFace> objFaces;

	private Color defaultColor;
	ObjReader(String filename, Color defaultColor, SimpInterpreter simpInterpreter){
		this.defaultColor = defaultColor;
		this.simpInterpreter = simpInterpreter;
		objVertices = new ArrayList<>();
		transformedVertices = new ArrayList<>();
		objNormals = new ArrayList<>();
		objFaces = new ArrayList<>();


		reader = new LineBasedReader(filename+".obj");
		read();

		transformVertices();

		render();
	}

	private void transformVertices(){
		for (Vertex3D vertex :
				objVertices) {
			Vertex3D transformedVertex = SimpInterpreter.objTransformVertex(simpInterpreter, vertex);
			transformedVertices.add(transformedVertex);
		}
	}

//	ObjReader(String filename, Color defaultColor, Clipper clipper, PolygonRenderer polygonRenderer,
//	          Transformation CTM, Transformation cameraToScreen, Transformation worldToScreen, boolean isInCameraWorld) {
//		// TODO: Initialize an instance of this class.
//		this.defaultColor = defaultColor;
//		objVertices = new ArrayList<>();
//		transformedVertices = new ArrayList<>();
//		objNormals = new ArrayList<>();
//		objFaces = new ArrayList<>();
//
//		this.clipper = clipper;
//		this.polygonRenderer = polygonRenderer;
//		this.CTM = CTM;
//		this.cameraToScreen = cameraToScreen;
//		this.worldToScreen = worldToScreen;
//		this.isInCameraWorld = isInCameraWorld;
//
//		reader = new LineBasedReader(filename+".obj");
//		read();
//	}

	public void render() {
		// TODO: Implement.  All of the vertices, normals, and faces have been defined.
		// First, transform all of the vertices.		
		// Then, go through each face, break into triangles if necessary, and send each triangle to the renderer.
		// You may need to add arguments to this function, and/or change the visibility of functions in SimpInterpreter.
		for (ObjFace face :objFaces) {
//			List<Vertex3D> ls = new ArrayList<>();
			Vertex3D[] vertices = new Vertex3D[3];
			vertices[0] = transformedVertices.get(face.get(0).vertexIndex-1);
			for (int i = 1; i < face.size()-1; ++i) {
				vertices[1] = transformedVertices.get(face.get(i).vertexIndex-1);
				vertices[2] = transformedVertices.get(face.get(i+1).vertexIndex-1);
				SimpInterpreter.objRenderPolygon(simpInterpreter, vertices);
			}
		}
	}
	
//	private Polygon polygonForFace(ObjFace face) {
//		// TODO: This function might be used in render() above.  Implement it if you find it handy.
//	}

//	private Vertex3D transformVertex(Vertex3D p){
//		Matrix m = new Matrix(p);
//		Vertex3D newPoint;
//		m = Matrix.matrixMultiple(CTM.getMatrix(), m);
//		if (isInCameraWorld) {
//			m = Matrix.matrixMultiple(cameraToScreen.getMatrix(), m);
//			newPoint = SimpInterpreter.transformToCamera(Matrix.convertToPoint(m, p.getColor()));
//			m = new Matrix(newPoint);
//		}
//		m = Matrix.matrixMultiple(worldToScreen.getMatrix(), m);
//		newPoint = Matrix.convertToPoint(m, p.getColor());
//		return newPoint;
//	}

	public void read() {
		while(reader.hasNext() ) {
			String line = reader.next().trim();
			interpretObjLine(line);
		}
	}
	private void interpretObjLine(String line) {
		if(!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
			String[] tokens = line.split("[ \t,()]+");
			if(tokens.length != 0) {
				interpretObjCommand(tokens);
			}
		}
	}

	private void interpretObjCommand(String[] tokens) {
		switch(tokens[0]) {
		case "v" :
		case "V" :
			interpretObjVertex(tokens);
			break;
		case "vn":
		case "VN":
			interpretObjNormal(tokens);
			break;
		case "f":
		case "F":
			interpretObjFace(tokens);
			break;
		default:	// do nothing
			break;
		}
	}
	private void interpretObjFace(String[] tokens) {
		ObjFace face = new ObjFace();
		
		for(int i = 1; i<tokens.length; i++) {
			String token = tokens[i];
			String[] subtokens = token.split("/");
			
			int vertexIndex  = objIndex(subtokens, 0, objVertices.size());
			int textureIndex = objIndex(subtokens, 1, 0);
			int normalIndex  = objIndex(subtokens, 2, objNormals.size());
			face.add(new ObjVertex(vertexIndex, textureIndex, normalIndex));
			// TODO: fill in action to take here.
		}
		// TODO: fill in action to take here.
		objFaces.add(face);
	}

	private int objIndex(String[] subtokens, int tokenIndex, int baseForNegativeIndices) {
		// TODO: write this.  subtokens[tokenIndex], if it exists, holds a string for an index.
		// use Integer.parseInt() to get the integer value of the index.
		// Be sure to handle both positive and negative indices.
		if (subtokens.length <= tokenIndex) {
			return 0;
		}
		if (subtokens[tokenIndex].equals("")) {
			return 0;
		}
		int index = Integer.parseInt(subtokens[tokenIndex]);
		if (index < 0) {
			index += baseForNegativeIndices + 1;
		}
		return index;
	}

	private Point3DH normalizeNormal(Point3DH old){
		double scale = 1/Math.sqrt(Math.pow(old.getX(),2) + Math.pow(old.getY(),2) + Math.pow(old.getZ(),2));
		return new Point3DH(scale*old.getX(), scale*old.getY(), scale*old.getZ());
	}

	private void interpretObjNormal(String[] tokens) {
		int numArgs = tokens.length - 1;
		if(numArgs != 3) {
			throw new BadObjFileException("vertex normal with wrong number of arguments : " + numArgs + ": " + tokens);
		}
		Point3DH normal = SimpInterpreter.interpretPoint(tokens, 1);
		// TODO: fill in action to take here.
		normal = normalizeNormal(normal);
		objNormals.add(normal);
	}
	private void interpretObjVertex(String[] tokens) {
		int numArgs = tokens.length - 1;
		Point3DH point = objVertexPoint(tokens, numArgs);
		Color color = objVertexColor(tokens, numArgs);

		// TODO: fill in action to take here.
		Vertex3D vertex = new Vertex3D(point, color);
		objVertices.add(vertex);

	}

	private Color objVertexColor(String[] tokens, int numArgs) {
		if(numArgs == 6) {
			return SimpInterpreter.interpretColor(tokens, 4);
		}
		if(numArgs == 7) {
			return SimpInterpreter.interpretColor(tokens, 5);
		}
		return defaultColor;
	}

	private Point3DH objVertexPoint(String[] tokens, int numArgs) {
		if(numArgs == 3 || numArgs == 6) {
			return SimpInterpreter.interpretPoint(tokens, 1);
		}
		else if(numArgs == 4 || numArgs == 7) {
			return SimpInterpreter.interpretPointWithW(tokens, 1);
		}
		throw new BadObjFileException("vertex with wrong number of arguments : " + numArgs + ": " + tokens);
	}
}