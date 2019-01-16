package geometry;

import geometry.Matrix;

public class Transformation{
	private Matrix matrix;
	public Transformation() {
		matrix = new Matrix(4, 4);
		matrix.makeIdentity();
	}

	public Transformation(Transformation old){
		matrix = new Matrix(4, 4);

		for (int i = 0; i < 4; ++i) {
			for (int j = 0; j < 4; ++j) {
				matrix.matrix[i][j] = old.matrix.matrix[i][j];
			}
		}
	}

	public Matrix getMatrix() {
		return matrix;
	}

	public void rotate(String axis, double angle){
		Matrix m = new Matrix(4, 4);
		m.makeIdentity();
		double angleInRadians = angle * Math.PI / 180;
		if (axis.equals("X")) {
			m.assign(1, 1, Math.cos(angleInRadians));
			m.assign(1, 2, -Math.sin(angleInRadians));
			m.assign(2, 1, Math.sin(angleInRadians));
			m.assign(2, 2, Math.cos(angleInRadians));
		}
		else if (axis.equals("Y")) {
			m.assign(0, 0, Math.cos(angleInRadians));
			m.assign(2, 0, -Math.sin(angleInRadians));
			m.assign(0, 2, Math.sin(angleInRadians));
			m.assign(2, 2, Math.cos(angleInRadians));
		}
		else if (axis.equals("Z")) {
			m.assign(0, 0, Math.cos(angleInRadians));
			m.assign(0, 1, -Math.sin(angleInRadians));
			m.assign(1, 0, Math.sin(angleInRadians));
			m.assign(1, 1, Math.cos(angleInRadians));
		}
		matrix = Matrix.matrixMultiple(matrix, m);
	}

	public void translate(double x, double y, double z){
		Matrix m = new Matrix(4, 4);
		m.makeIdentity();
		m.assign(0, 3, x);
		m.assign(1, 3, y);
		m.assign(2, 3, z);
		matrix = Matrix.matrixMultiple(matrix, m);
	}

	public void scale(double k1, double k2, double k3){
		Matrix m = new Matrix(4, 4);
		m.makeIdentity();
		m.assign(0, 0, k1);
		m.assign(1, 1, k2);
		m.assign(2, 2, k3);
		matrix = Matrix.matrixMultiple(matrix, m);
	}

}
