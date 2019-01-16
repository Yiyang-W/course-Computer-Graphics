package geometry;

import org.omg.CORBA.MARSHAL;
import windowing.graphics.Color;

import java.util.Arrays;

public class Matrix {
	protected double[][] matrix;
	private int row, col;
	public Matrix(int row, int col){
		this.row = row;
		this.col = col;
		matrix = new double[row][];
		for (int i = 0; i < row; ++i) {
			matrix[i] = new double[col];
			for (int j = 0; j < col; ++j) {
				matrix[i][j] = 0;
			}
		}
	}
	public Matrix(Vertex3D p){
		row = 4;
		col = 1;
		matrix = new double[4][1];
		matrix[0][0] = p.getX();
		matrix[1][0] = p.getY();
		matrix[2][0] = p.getZ();
		matrix[3][0] = p.point.getW();
	}
	public void makeIdentity(){
		if (row == col) {
			for (int i = 0; i < row; ++i) {
				for (int j = 0; j < row; ++j) {
					matrix[i][j] = i == j ? 1:0;
				}
			}
		}
	}
	public void assign(int row, int col, double value){
		matrix[row][col] = value;
	}
	public double getValue(int row, int col){
		return matrix[row][col];
	}

	public static Matrix matrixMultiple(Matrix m1, Matrix m2){
		Matrix m3 = new Matrix(m1.row, m2.col);
		for (int i = 0; i < m3.row; ++i) {
			for (int j = 0; j < m3.col; ++j) {
				for (int k = 0; k < m1.col; ++k) {
					m3.matrix[i][j] += m1.matrix[i][k] * m2.matrix[k][j];
				}
			}
		}
		return m3;
	}
	public static Vertex3D convertToPoint(Matrix matrix, Color color){
		if (matrix.col == 1  &&  matrix.row == 4) {
			return new Vertex3D(matrix.matrix[0][0]/matrix.matrix[3][0], matrix.matrix[1][0]/matrix.matrix[3][0], matrix.matrix[2][0]/matrix.matrix[3][0], color);
		}
		return new Vertex3D(0, 0, 0, Color.WHITE);
	}

	public static Point3DH convertToPoint(Matrix matrix){
		if (matrix.col == 1  &&  matrix.row == 4) {
			return new Point3DH(matrix.matrix[0][0]/matrix.matrix[3][0], matrix.matrix[1][0]/matrix.matrix[3][0], matrix.matrix[2][0]/matrix.matrix[3][0]);
		}
		return new Point3DH(0, 0, 0);
	}
/*
	public static Vertex3D convertToPointInCameraWorld(Matrix matrix, Color color, double viewingPlaneZ){
		if (matrix.col == 1  &&  matrix.row == 4) {
			return new Vertex3D(matrix.matrix[0][0]/matrix.matrix[3][0]*viewingPlaneZ/matrix.matrix[2][0], matrix.matrix[1][0]/matrix.matrix[3][0]*viewingPlaneZ/matrix.matrix[2][0], matrix.matrix[2][0], color);
		}
		return new Vertex3D(0, 0, 0, Color.WHITE);
	}
*/
	public double getDeterminant(){
		if (col != row) {
			return 0;
		}
		if (row == 1) {
			return matrix[0][0];
		}
		if (row == 2) {
			return matrix[0][0]*matrix[1][1]-matrix[0][1]*matrix[1][0];
		}
		double result = 0;
		Matrix tmp = new Matrix(row-1, row-1);
		for (int i = 0; i < row; ++i) {
			for (int j = 0; j < row-1; ++j) {
				for (int k = 0; k < row-1; ++k) {
					tmp.matrix[j][k] = matrix[j+1][k>=i?k+1:k];
				}
			}
			double t = tmp.getDeterminant();
			if (i % 2 == 0) {
				result += matrix[0][i] * t;
			}
			else {
				result -= matrix[0][i] * t;
			}
		}
		return result;
	}

	public Matrix getCofactor(){
		Matrix cofactor = new Matrix(row, col);
		if (row == 1) {
			cofactor.matrix[0][0] = 1;
		}
		Matrix tmp = new Matrix(row-1, row-1);
		for (int i = 0; i < row; ++i) {
			for (int j = 0; j < row; ++j) {
				for (int k = 0; k < row-1; ++k) {
					for (int l = 0; l < row-1; ++l) {
						tmp.matrix[k][l] = matrix[k>=i?k+1:k][l>=j?l+1:l];
					}
				}
				cofactor.matrix[j][i] = tmp.getDeterminant();
				if ((i+j) % 2 == 1) {
					cofactor.matrix[j][i] = -cofactor.matrix[j][i];
				}
			}
		}
		return cofactor;
	}

	public static Matrix inverse(Matrix m){
		double flag = m.getDeterminant();
		if (m.row != m.col  ||  flag == 0) {
			System.out.println("Error in inverse.");
			return new Matrix(0,0);
		}

		Matrix newMatrix = new Matrix(m.row, m.col);
		Matrix cofactor = m.getCofactor();
		for (int i = 0; i < m.row; ++i) {
			for (int j = 0; j < m.row; ++j) {
				newMatrix.matrix[i][j] = cofactor.matrix[i][j] / flag;
			}
		}
		return newMatrix;
	}

//	public static Matrix nomalizeVector(Matrix m){
//
//	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < row; ++i) {
			s.append("| ");
			for (int j = 0; j < col; ++j) {
				s.append(matrix[i][j]);
				s.append(' ');
			}
			s.append("|\n");
		}
		return s.toString();
	}
}
