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
