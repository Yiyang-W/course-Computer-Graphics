package client;

import windowing.drawable.Drawable;
import windowing.drawable.ZBufferDrawable;
import windowing.graphics.Color;

public class DepthCueingDrawable extends ZBufferDrawable {
	private static final int DEPTH = 200;
	private Color frontColor;
	double mr, mg, mb;
	public DepthCueingDrawable(Drawable delegate, Color frontColor) {
		super(delegate);
		this.frontColor = frontColor;
		mr = frontColor.getR() / DEPTH;
		mg = frontColor.getG() / DEPTH;
		mb = frontColor.getB() / DEPTH;
	}

	@Override
	public void setPixel(int x, int y, double z, int argbColor) {
		super.setPixel(x, y, z, new Color((DEPTH+z)*mr, (DEPTH+z)*mg, (DEPTH+z)*mb).asARGB());
	}
}
