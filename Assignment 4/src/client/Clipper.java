package client;

import client.interpreter.SimpInterpreter;
import geometry.Point3DH;
import geometry.Vertex3D;
import windowing.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class Clipper {
	private double xLow, xHigh, yLow, yHigh, zLow, zHigh;

	public Clipper(double x1, double x2, double y1, double y2, double z1, double z2){
		xLow = x1;
		xHigh = x2;
		yLow = y1;
		yHigh = y2;
		zLow = z1;
		zHigh = z2;
	}

	public Vertex3D[] clipLineZ(Vertex3D[] p){
		if (p[0].getZ() < zLow  &&  p[1].getZ() < zLow
			|| p[0].getZ() >= zHigh  &&  p[1].getZ() >= zHigh) {
			return null;
		}

		p[0] = clipOnePointInLineZ(p[0], p[1]);
		p[1] = clipOnePointInLineZ(p[1], p[0]);
		return p;
	}
	public Vertex3D[] clipLine(Vertex3D[] p){
		if ((p[0].getX() < xLow  &&  p[1].getX() < xLow
				|| p[0].getX() >= xHigh  &&  p[1].getX() >= xHigh)
				||
				(p[0].getY() < yLow  &&  p[1].getY() < yLow
						|| p[0].getY() >= yHigh  &&  p[1].getY() >= yHigh)) {
			return null;
		}
		p[0] = clipOnePointInLineX(p[0], p[1]);
		p[1] = clipOnePointInLineX(p[1], p[0]);
		p[0] = clipOnePointInLineY(p[0], p[1]);
		p[1] = clipOnePointInLineY(p[1], p[0]);

		return p;
	}
	private Vertex3D clipOnePointInLineZ(Vertex3D p, Vertex3D q){
		Point3DH tmpCamera = p.getCameraSpace();
		Point3DH tmpNormal = p.getNormal();
		if (p.getZ() < zLow) {
			double s = (zLow - p.getZ()) / (q.getZ() - p.getZ());
			double x = p.getX() + (q.getX()-p.getX())*s;
			double y = p.getY() + (q.getY()-p.getY())*s;
			double r = p.getColor().getR() + (q.getColor().getR()-p.getColor().getR())*s;
			double g = p.getColor().getG() + (q.getColor().getG()-p.getColor().getG())*s;
			double b = p.getColor().getB() + (q.getColor().getB()-p.getColor().getB())*s;
			p = new Vertex3D(x, y, zLow, new Color(r, g, b));

			if (SimpInterpreter.isInCameraWorld) {
				double xC = tmpCamera.getX() + (q.getCameraSpace().getX()-tmpCamera.getX())*s;
				double yC = tmpCamera.getY() + (q.getCameraSpace().getY()-tmpCamera.getY())*s;
				double zC = tmpCamera.getZ() + (q.getCameraSpace().getZ()-tmpCamera.getZ())*s;
				double xN = tmpNormal.getX() + (q.getNormal().getX()-tmpNormal.getX())*s;
				double yN = tmpNormal.getY() + (q.getNormal().getY()-tmpNormal.getY())*s;
				double zN = tmpNormal.getZ() + (q.getNormal().getZ()-tmpNormal.getZ())*s;
				p.setNormal(xN, yN, zN);
				p.setCameraSpace(xC, yC, zC);
			}
		}
		else if (p.getZ() >= zHigh) {
			double s = (zHigh - p.getZ()) / (q.getZ() - p.getZ());
			double x = p.getX() + (q.getX()-p.getX())*s;
			double y = p.getY() + (q.getY()-p.getY())*s;
			double r = p.getColor().getR() + (q.getColor().getR()-p.getColor().getR())*s;
			double g = p.getColor().getG() + (q.getColor().getG()-p.getColor().getG())*s;
			double b = p.getColor().getB() + (q.getColor().getB()-p.getColor().getB())*s;
			p = new Vertex3D(x, y, zHigh, new Color(r, g, b));

			if (SimpInterpreter.isInCameraWorld) {
				double xC = tmpCamera.getX() + (q.getCameraSpace().getX()-tmpCamera.getX())*s;
				double yC = tmpCamera.getY() + (q.getCameraSpace().getY()-tmpCamera.getY())*s;
				double zC = tmpCamera.getZ() + (q.getCameraSpace().getZ()-tmpCamera.getZ())*s;
				double xN = tmpNormal.getX() + (q.getNormal().getX()-tmpNormal.getX())*s;
				double yN = tmpNormal.getY() + (q.getNormal().getY()-tmpNormal.getY())*s;
				double zN = tmpNormal.getZ() + (q.getNormal().getZ()-tmpNormal.getZ())*s;
				p.setNormal(xN, yN, zN);
				p.setCameraSpace(new Point3DH(xC, yC, zC));
			}
		}
		return p;
	}
	private Vertex3D clipOnePointInLineX(Vertex3D p, Vertex3D q){
		Point3DH tmpCamera = p.getCameraSpace();
		Point3DH tmpNormal = p.getNormal();

		if (p.getX() < xLow) {
			double s = (xLow - p.getX()) / (q.getX() - p.getX());
			double z = p.getZ() + (q.getZ()-p.getZ())*s;
			double y = p.getY() + (q.getY()-p.getY())*s;
			double r = p.getColor().getR() + (q.getColor().getR()-p.getColor().getR())*s;
			double g = p.getColor().getG() + (q.getColor().getG()-p.getColor().getG())*s;
			double b = p.getColor().getB() + (q.getColor().getB()-p.getColor().getB())*s;
			p = new Vertex3D(xLow, y, z, new Color(r, g, b));
			if (SimpInterpreter.isInCameraWorld) {
				double xC = tmpCamera.getX() + (q.getCameraSpace().getX()-tmpCamera.getX())*s;
				double yC = tmpCamera.getY() + (q.getCameraSpace().getY()-tmpCamera.getY())*s;
				double zC = tmpCamera.getZ() + (q.getCameraSpace().getZ()-tmpCamera.getZ())*s;
				double xN = tmpNormal.getX() + (q.getNormal().getX()-tmpNormal.getX())*s;
				double yN = tmpNormal.getY() + (q.getNormal().getY()-tmpNormal.getY())*s;
				double zN = tmpNormal.getZ() + (q.getNormal().getZ()-tmpNormal.getZ())*s;
				p.setNormal(xN, yN, zN);
				p.setCameraSpace(new Point3DH(xC, yC, zC));
			}
		}
		else if (p.getX() >= xHigh) {
			double s = (xHigh - p.getX()) / (q.getX() - p.getX());
			double z = p.getZ() + (q.getZ()-p.getZ())*s;
			double y = p.getY() + (q.getY()-p.getY())*s;
			double r = p.getColor().getR() + (q.getColor().getR()-p.getColor().getR())*s;
			double g = p.getColor().getG() + (q.getColor().getG()-p.getColor().getG())*s;
			double b = p.getColor().getB() + (q.getColor().getB()-p.getColor().getB())*s;
			p = new Vertex3D(xHigh-1, y, z, new Color(r, g, b));
			if (SimpInterpreter.isInCameraWorld) {
				double xC = tmpCamera.getX() + (q.getCameraSpace().getX()-tmpCamera.getX())*s;
				double yC = tmpCamera.getY() + (q.getCameraSpace().getY()-tmpCamera.getY())*s;
				double zC = tmpCamera.getZ() + (q.getCameraSpace().getZ()-tmpCamera.getZ())*s;
				double xN = tmpNormal.getX() + (q.getNormal().getX()-tmpNormal.getX())*s;
				double yN = tmpNormal.getY() + (q.getNormal().getY()-tmpNormal.getY())*s;
				double zN = tmpNormal.getZ() + (q.getNormal().getZ()-tmpNormal.getZ())*s;
				p.setNormal(xN, yN, zN);
				p.setCameraSpace(new Point3DH(xC, yC, zC));
			}
		}
		return p;
	}
	private Vertex3D clipOnePointInLineY(Vertex3D p, Vertex3D q){
		Point3DH tmpCamera = p.getCameraSpace();
		Point3DH tmpNormal = p.getNormal();

		if (p.getY() < yLow) {
			double s = (yLow - p.getY()) / (q.getY() - p.getY());
			double x = p.getX() + (q.getX()-p.getX())*s;
			double z = p.getZ() + (q.getZ()-p.getZ())*s;
			double r = p.getColor().getR() + (q.getColor().getR()-p.getColor().getR())*s;
			double g = p.getColor().getG() + (q.getColor().getG()-p.getColor().getG())*s;
			double b = p.getColor().getB() + (q.getColor().getB()-p.getColor().getB())*s;
			p = new Vertex3D(x, yLow, z, new Color(r, g, b));
			if (SimpInterpreter.isInCameraWorld) {
				double xC = tmpCamera.getX() + (q.getCameraSpace().getX()-tmpCamera.getX())*s;
				double yC = tmpCamera.getY() + (q.getCameraSpace().getY()-tmpCamera.getY())*s;
				double zC = tmpCamera.getZ() + (q.getCameraSpace().getZ()-tmpCamera.getZ())*s;
				double xN = tmpNormal.getX() + (q.getNormal().getX()-tmpNormal.getX())*s;
				double yN = tmpNormal.getY() + (q.getNormal().getY()-tmpNormal.getY())*s;
				double zN = tmpNormal.getZ() + (q.getNormal().getZ()-tmpNormal.getZ())*s;
				p.setNormal(xN, yN, zN);
				p.setCameraSpace(new Point3DH(xC, yC, zC));
			}
		}
		else if (p.getY() >= yHigh) {
			double s = (yHigh - p.getY()) / (q.getY() - p.getY());
			double x = p.getX() + (q.getX()-p.getX())*s;
			double z = p.getZ() + (q.getZ()-p.getZ())*s;
			double r = p.getColor().getR() + (q.getColor().getR()-p.getColor().getR())*s;
			double g = p.getColor().getG() + (q.getColor().getG()-p.getColor().getG())*s;
			double b = p.getColor().getB() + (q.getColor().getB()-p.getColor().getB())*s;
			p = new Vertex3D(x, yHigh-1, z, new Color(r, g, b));
			if (SimpInterpreter.isInCameraWorld) {
				double xC = tmpCamera.getX() + (q.getCameraSpace().getX()-tmpCamera.getX())*s;
				double yC = tmpCamera.getY() + (q.getCameraSpace().getY()-tmpCamera.getY())*s;
				double zC = tmpCamera.getZ() + (q.getCameraSpace().getZ()-tmpCamera.getZ())*s;
				double xN = tmpNormal.getX() + (q.getNormal().getX()-tmpNormal.getX())*s;
				double yN = tmpNormal.getY() + (q.getNormal().getY()-tmpNormal.getY())*s;
				double zN = tmpNormal.getZ() + (q.getNormal().getZ()-tmpNormal.getZ())*s;
				p.setNormal(xN, yN, zN);
				p.setCameraSpace(new Point3DH(xC, yC, zC));
			}
		}
		return p;
	}


	public Vertex3D[] clipPolygonZ(Vertex3D[] p){
		List<Vertex3D> ls = new ArrayList<>();
//		int index = -1;
//		for (int i = 0; i < p.length; ++i) {
//			if(p[i].getZ() < zHigh  &&  p[i].getZ() >= zLow){
//				index = i;
//				break;
//			}
//		}
//		if (index == -1) {
//			return null;
//		}

//		System.out.println("Clipping");

		for (int i = 0; i < p.length; ++i) {
			if (p[i%p.length].getZ() < zLow) {
//				System.out.println("<zLow: "+i);
				if (p[(i+p.length-1)%p.length].getZ() > zLow) {
					Vertex3D newPoint = clipOnePointInLineZ(p[i%p.length], p[(i+p.length-1)%p.length]);
					ls.add(newPoint);
				}
				if (p[(i+1)%p.length].getZ() > zLow) {
					Vertex3D newPoint = clipOnePointInLineZ(p[i%p.length], p[(i+1)%p.length]);
					ls.add(newPoint);
				}
			}
			else{
				ls.add(p[i%p.length]);
			}
		}
		p = (Vertex3D[]) ls.toArray(new Vertex3D[ls.size()]);
		ls.clear();
		for (int i = 0; i < p.length; ++i) {
			if (p[i%p.length].getZ() >= zHigh) {
//				System.out.println(">=zHigh: "+i);
				if (p[(i+p.length-1)%p.length].getZ() < zHigh-1) {
					Vertex3D newPoint = clipOnePointInLineZ(p[i%p.length], p[(i+p.length-1)%p.length]);
					ls.add(newPoint);
//					System.out.println(newPoint);
				}
				if (p[(i+1)%p.length].getZ() < zHigh-1) {
					Vertex3D newPoint = clipOnePointInLineZ(p[i%p.length], p[(i+1)%p.length]);
					ls.add(newPoint);
//					System.out.println(newPoint);
				}
			}
			else{
				ls.add(p[i%p.length]);
			}
		}
//		System.out.println(ls.size());
//
//		System.out.println(ls.get(0));
//		System.out.println(ls.get(1));
//		System.out.println(ls.get(2));
		return (Vertex3D[]) ls.toArray(new Vertex3D[ls.size()]);
	}


	public Vertex3D[] clipPolygon(Vertex3D[] p){
		List<Vertex3D> ls = new ArrayList<>();
//		int index = -1;
//		for (int i = 0; i < p.length; ++i) {
//			if(p[i].getX() < xHigh  &&  p[i].getX() >= xLow){
//				index = i;
//				break;
//			}
//		}
//		if (index == -1) {
//			return null;
//		}
//		index = -1;
//		for (int i = 0; i < p.length; ++i) {
//			if(p[i].getY() < yHigh  &&  p[i].getY() >= yLow){
//				index = i;
//				break;
//			}
//		}
//		if (index == -1) {
//			return null;
//		}

		//x
		for (int i = 0; i < p.length; ++i) {
			if (p[i%p.length].getX() < xLow) {
				if (p[(i+p.length-1)%p.length].getX() > xLow) {
					Vertex3D newPoint = clipOnePointInLineX(p[i%p.length], p[(i+p.length-1)%p.length]);
					ls.add(newPoint);
				}
				if (p[(i+1)%p.length].getX() > xLow) {
					Vertex3D newPoint = clipOnePointInLineX(p[i%p.length], p[(i+1)%p.length]);
					ls.add(newPoint);
				}
			}
			else{
				ls.add(p[i%p.length]);
			}
		}
		p = (Vertex3D[]) ls.toArray(new Vertex3D[ls.size()]);
		ls.clear();
		for (int i = 0; i < p.length; ++i) {
			if (p[i%p.length].getX() >= xHigh) {
				if (p[(i+p.length-1)%p.length].getX() < xHigh-1) {
					Vertex3D newPoint = clipOnePointInLineX(p[i%p.length], p[(i+p.length-1)%p.length]);
					ls.add(newPoint);
				}
				if (p[(i+1)%p.length].getX() < xHigh-1) {
					Vertex3D newPoint = clipOnePointInLineX(p[i%p.length], p[(i+1)%p.length]);
					ls.add(newPoint);
				}
			}
			else{
				ls.add(p[i%p.length]);
			}
		}
		//y
		p = (Vertex3D[]) ls.toArray(new Vertex3D[ls.size()]);
		ls.clear();
		for (int i = 0; i < p.length; ++i) {
			if (p[i%p.length].getY() < yLow) {
				if (p[(i+p.length-1)%p.length].getY() > yLow) {
					Vertex3D newPoint = clipOnePointInLineY(p[i%p.length], p[(i+p.length-1)%p.length]);
					ls.add(newPoint);
				}
				if (p[(i+1)%p.length].getY() > yLow) {
					Vertex3D newPoint = clipOnePointInLineY(p[i%p.length], p[(i+1)%p.length]);
					ls.add(newPoint);
				}
			}
			else{
				ls.add(p[i%p.length]);
			}
		}
		p = (Vertex3D[]) ls.toArray(new Vertex3D[ls.size()]);
		ls.clear();
		for (int i = 0; i < p.length; ++i) {
			if (p[i%p.length].getY() >= yHigh) {
				if (p[(i+p.length-1)%p.length].getY() < yHigh-1) {
					Vertex3D newPoint = clipOnePointInLineY(p[i%p.length], p[(i+p.length-1)%p.length]);
					ls.add(newPoint);
				}
				if (p[(i+1)%p.length].getY() < yHigh-1) {
					Vertex3D newPoint = clipOnePointInLineY(p[i%p.length], p[(i+1)%p.length]);
					ls.add(newPoint);
				}
			}
			else{
				ls.add(p[i%p.length]);
			}
		}
		return (Vertex3D[]) ls.toArray(new Vertex3D[ls.size()]);
	}

}